package com.example.r30_a.contactlist;

import android.content.ContentResolver;
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

    private void init() {
        toast = Toast.makeText(this, "",Toast.LENGTH_SHORT);
        resolver = this.getContentResolver();

        edtName = (EditText)findViewById(R.id.edtContactName);
        edtPhomeNumber = (EditText)findViewById(R.id.edtPhoneNumber);
        btnAddContact = (Button)findViewById(R.id.btnUpdate);

    }

    private void insertContact(String name, String phoneNum) {

        ContentValues values = new ContentValues();

        Uri contactUri = resolver.insert(RawContacts.CONTENT_URI, values);

        long contactId = ContentUris.parseId(contactUri);
        //新增Name
        values.clear();
        values.put(Data.RAW_CONTACT_ID, contactId );
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        values.put(StructuredName.GIVEN_NAME, name);

        resolver.insert(Data.CONTENT_URI, values);

        //新增PhoneNum
        values.clear();
        values.put(Data.RAW_CONTACT_ID, contactId );
        values.put(Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.NUMBER, phoneNum);
        values.put(Phone.TYPE,Phone.TYPE_MOBILE);
        resolver.insert(Data.CONTENT_URI,values);

    }


}
