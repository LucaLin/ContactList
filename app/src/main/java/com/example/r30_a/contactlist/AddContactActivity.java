package com.example.r30_a.contactlist;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import static com.example.r30_a.contactlist.ContactListActivity.isCellPhoneNumber;

public class AddContactActivity extends AppCompatActivity {



    Toast toast;
    EditText edtName, edtPhomeNumber;//使用者編輯區
    Button btnAddContact;
    ContentResolver resolver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        init();

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String name = edtName.getText().toString();
            String phoneNum = edtPhomeNumber.getText().toString();
                if(TextUtils.isEmpty(name) || !isCellPhoneNumber(phoneNum)){
                    toast.setText(R.string.wrongInput);
                    toast.show();
                }else {
                    insertContact(name, phoneNum);
                    toast.setText(R.string.addSuccess);
                    toast.show();
                    finish();
                    }
                }
        });
    }

    public void init() {
        toast = Toast.makeText(this, "",Toast.LENGTH_SHORT);
        resolver = this.getContentResolver();

        edtName = (EditText)findViewById(R.id.edtContactName);
        edtPhomeNumber = (EditText)findViewById(R.id.edtPhoneNumber);
        btnAddContact = (Button)findViewById(R.id.btnUpdate);

    }


    public boolean insertContact(String name, String phoneNum) {

        try {
            ContentValues values = new ContentValues();

            //建立一個空白ID供新增資料用
            Uri contactUri = resolver.insert(RawContacts.CONTENT_URI, values);
            long contactId = ContentUris.parseId(contactUri);

            //新增Name
            insertNameData(Data.RAW_CONTACT_ID, contactId, Data.MIMETYPE,
                    StructuredName.CONTENT_ITEM_TYPE,
                    StructuredName.GIVEN_NAME, name, Data.CONTENT_URI, values);


            //新增PhoneNum
            insertData(Data.RAW_CONTACT_ID, contactId,
                    Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE,
                    Phone.TYPE, Phone.TYPE_MOBILE,
                    Phone.NUMBER, phoneNum, Data.CONTENT_URI, values);

        }catch (Exception e){
            e.getMessage();
            return false;
        }
        return true;
    }
    //新增資料到聯絡人表格中
    public void insertData(String rawContactIdColumn, long contactId,
                           String MIMETYPE_column, String Content_Item_Type,
                           String phoneType, int TypeMode,
                           String dataColumn, String data,
                           Uri uri, ContentValues values){

        values.clear();
        values.put(rawContactIdColumn, contactId );
        values.put(MIMETYPE_column,Content_Item_Type);
        values.put(phoneType,TypeMode);
        values.put(dataColumn, data);
        resolver.insert(uri,values);

    }

    public boolean insertNameData(String rawContactIdColumn, long contactId,
                           String MIMETYPE_column, String Content_Item_Type,
                           String dataColumn, String data,
                           Uri uri, ContentValues values){

        values.clear();
        values.put(rawContactIdColumn, contactId );
        values.put(MIMETYPE_column,Content_Item_Type);
        values.put(dataColumn, data);
        resolver.insert(uri,values);
        return true;
    }


}
