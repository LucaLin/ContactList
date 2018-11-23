package com.example.r30_a.contactlist;

import android.Manifest;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.r30_a.contactlist.adapter.ContactsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactListActivity extends AppCompatActivity {

    Button btnToAddContact;
    Toast toast;
    ArrayList<ContactData> readContactList;//聯絡人清單表
    ListView myContactList;
    ContactsAdapter adapter;
    private Cursor cursor;//搜尋資料的游標
    private HashMap contactMap = new HashMap();//用來儲存資料的物件
    private ContactData contactData;
    public static final Uri SIM_URI = Uri.parse("content://icc/adn");//讀取sim卡資料的uri string
    String[] phoneNumberProjection = new String[]{//欲搜尋的欄位區塊
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.DISPLAY_NAME};
    String tempId = "";//聯絡人id的暫存
    public static final int REQUEST_CODE = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        init();

        myContactList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                return true;
            }
        });
        myContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                final String phoneTel = readContactList.get(position).getPhoneNum();
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactListActivity.this);
                builder.setNeutralButton(R.string.dial, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneTel));
                        if (ActivityCompat.checkSelfPermission(ContactListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(intent);
                    }
                })
                        .setPositiveButton(R.string.deleteData, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteContact(readContactList.get(position).getId());
                            }
                        })
                        .setNegativeButton(R.string.updateData, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.putExtra("id",readContactList.get(position).getId());
                                intent.putExtra("name", readContactList.get(position).getName());
                                intent.putExtra("phone", phoneTel);
                                intent.setClass(ContactListActivity.this, UpdateDataActivity.class);
                                startActivityForResult(intent, ContactListActivity.REQUEST_CODE);
                            }
                        })
                        .setTitle(R.string.chooseFunction)
                        .create();
                builder.show();
            }
        });

        btnToAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactListActivity.this, AddContactActivity.class));
            }
        });
    }

    private void deleteContact(String id) {
        try {
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            //使用id來找原始資料
            Cursor c = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    phoneNumberProjection,
                    "contact_id =?",
                    new String[]{id},
                    null);
            if (c.moveToFirst()) {

                this.getContentResolver().delete(uri, "contact_id =?", new String[]{id});
                toast.setText(R.string.deleteOK);
                toast.show();
                setAdapter(MainActivity.type);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter(MainActivity.type);
    }

    //獲取聯絡人清單資料
    private void setAdapter(int type) {
        if (type == 0) {//只顯示sim卡資料
            adapter = new ContactsAdapter(this, getContactList(SIM_URI, phoneNumberProjection, 0, 1));
        } else {//全部顯示
            adapter = new ContactsAdapter(this, getContactList(Phone.CONTENT_URI, phoneNumberProjection, 2, 1));
        }
        myContactList.setAdapter(adapter);
    }

    private ArrayList getContactList(Uri uri, String[] projecction, int nameColumn, int numColunm) {
        readContactList = new ArrayList();
        String name;
        String phoneNumber;
        String formatPhoneNum = "";

        cursor = this.getContentResolver().query(uri, projecction, null, null, null);

        //直接取contacts中的號碼資料區，再從號碼欄去抓對應的name跟number
        if (cursor != null) {
            while (cursor != null && cursor.moveToNext()) {
                //抓取id用來判別是否有重覆資料抓取

                String id = cursor.getString(cursor.getColumnIndex(Phone.CONTACT_ID));
                name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
                phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                if (!TextUtils.isEmpty(phoneNumber) && !isCellPhoneNumber(phoneNumber)) {
                    continue;
                } else {
                    addContactToList(id, phoneNumber, formatPhoneNum, contactMap, name, readContactList);
                }
            }
            cursor.close();
            return readContactList;
        } else {
            toast.setText(R.string.noData);
            toast.show();
            return readContactList;
        }

    }

    private void addContactToList(String id, String phoneNumber, String formatPhoneNum, HashMap contactMap, String name, ArrayList readContactList) {
        formatPhoneNum = getFormatPhone(phoneNumber);

        if (!tempId.equals(id)) {
            contactData = new ContactData();
            contactData.setId(id);
            contactData.setName(name);
            contactData.setPhoneNum(formatPhoneNum);
            tempId = id;
            readContactList.add(contactData);
        }
    }

    /*簡單判斷字串是否為電話號碼格式*/
    public static boolean isCellPhoneNumber(String cellphone) {
        if (cellphone.length() < 10) {
            return false;
        } else {
            boolean isCellPhone;
            String sub = "";
            cellphone = cellphone.trim()
                    .replace("+", "")
                    .replace("-", "")
                    .replace("+886", "")
                    .replace("886", "0")
                    .replace(" ", "");
            if (cellphone.length() > 2) {
                sub = cellphone.substring(0, 2).trim();

                if (!sub.equals("09")) {
                    isCellPhone = false;
                } else {
                    Pattern pattern = Pattern.compile("[0-9]{4}[0-9]{3}[0-9]{3}");
                    Matcher matcher = pattern.matcher(cellphone);

                    isCellPhone = matcher.matches();
                }
            } else {
                isCellPhone = false;
            }
            return isCellPhone;
        }
    }

    private String getFormatPhone(String phoneNumber) {
        //1: 開頭是+886的
        //2： 格式為xxxx-xxx-xxx的
        //3: 手機號碼在市話欄或傳真欄的
        //4: 根本沒有手機號碼的
        //5: 一人有多支號碼的
        //6: 不是09或+886就不取
        return phoneNumber.trim()
                .replace("+", "")
                .replace("-", "")
                .replace("+886", "")
                .replace("886", "0")
                .replace(" ", "");
    }

    private void init() {
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        btnToAddContact = (Button) findViewById(R.id.btnUpdate);
        myContactList = (ListView) findViewById(R.id.ContactList);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE){

            if(resultCode == RESULT_OK){
                String updateName = data.getStringExtra("Name");
                String updatePhone = data.getStringExtra("Phone");
                String oldName = data.getStringExtra("oldName");

                Cursor c = this.getContentResolver().query(Data.CONTENT_URI,
                        new String[]{Data.RAW_CONTACT_ID},
                        ContactsContract.Contacts.DISPLAY_NAME + " =?",
                        new String[]{oldName},null);

                c.moveToFirst();
                String id = c.getString(c.getColumnIndex(Data.RAW_CONTACT_ID));
                c.close();

//                Uri nameUri = Uri.parse("content://com.android.contacts/raw_contacts");
//                Uri numberUri = Uri.parse("content://com.android.contacts/data");
//                //使用id來找原始資料
//                Cursor c = this.getContentResolver().query(numberUri,
//                        phoneNumberProjection,
//                        "contact_id =?",
//                        new String[]{dataid},
//                        null);
                try{

                    ContentValues values = new ContentValues();
                    values.put(Phone.NUMBER,updatePhone);
                    values.put(Phone.TYPE, Phone.TYPE_MOBILE);
                    this.getContentResolver().update(
                                    Data.CONTENT_URI,
                                    values,
                             Data.RAW_CONTACT_ID+" =?" +" AND "+ Data.MIMETYPE + " =?" ,
                                    new String[]{id, Phone.CONTENT_ITEM_TYPE});
//                    values = new ContentValues();
//                    values.put(ContactsContract.Contacts.DISPLAY_NAME,updateName);
//                    this.getContentResolver().update(nameUri,values,"contact_id =?",new String[]{dataid});
//
                }catch (Exception e){
                    e.getMessage();
                }

            }
        }
    }
}

