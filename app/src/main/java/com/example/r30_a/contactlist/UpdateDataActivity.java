package com.example.r30_a.contactlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateDataActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txvDataName, txvDataPhone;
    Button btnUpdate;
    EditText edtName, edtPhone;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);
        init();
    }

    private void init() {
        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        txvDataName = (TextView)findViewById(R.id.txvDataName);
        txvDataPhone = (TextView)findViewById(R.id.txvDataPhone);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        Intent intent = getIntent();
        txvDataName.setText(intent.getStringExtra("name"));
        txvDataPhone.setText(intent.getStringExtra("phone"));
        edtName = (EditText)findViewById(R.id.edtContactName);
        edtPhone = (EditText)findViewById(R.id.edtPhoneNumber);
    }

    @Override
    public void onClick(View v) {
        final String updateName = edtName.getText().toString();
        final String updatePhone = edtPhone.getText().toString();
        if(TextUtils.isEmpty(updateName) || TextUtils.isEmpty(updatePhone)) {
            toast.setText(R.string.wrongInput);toast.show();
        }else if(updateName.equals(txvDataName) && updatePhone.equals(txvDataPhone)){
            toast.setText(R.string.noUpdate);toast.show();
        }else {
            if(ContactListActivity.isCellPhoneNumber(updatePhone)){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.hint)
                    .setMessage(R.string.sureToUpdate)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent();
                            intent.putExtra("Name",updateName);
                            intent.putExtra("Phone",updatePhone);
                            intent.setClass(UpdateDataActivity.this,ContactListActivity.class);
                            setResult(RESULT_OK, intent);
                            toast.setText(R.string.updateDataOK);toast.show();
                            finish();

                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }).create();

            builder.show();
            }else {
                toast.setText(R.string.wrongInput);toast.show();
            }
        }
    }
}
