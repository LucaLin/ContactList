package com.example.r30_a.contactlist;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.r30_a.contactlist.ContactListActivity.isCellPhoneNumber;

public class AddContactActivity extends AppCompatActivity {

    Toast toast;
    EditText edtName, edtPhomeNumber;//使用者編輯區
    Button btnAddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        toast = Toast.makeText(this, "",Toast.LENGTH_SHORT);

        edtName = (EditText)findViewById(R.id.edtContactName);
        edtPhomeNumber = (EditText)findViewById(R.id.edtPhoneNumber);
        btnAddContact = (Button)findViewById(R.id.btnUpdate);
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

    private void insertContact(String name, String phoneNum) {

        ContentValues values = new ContentValues();

        Uri contactUri = this.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);

        long contactId = ContentUris.parseId(contactUri);
        //新增Name
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId );
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);

        this.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        //新增PhoneNum
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId );
        values.put(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE,ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        this.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,values);

    }


}
