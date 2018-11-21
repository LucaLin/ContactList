package com.example.r30_a.contactlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnGotoContact;
    public static int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGotoContact = (Button)findViewById(R.id.btnGotoContact);
        btnGotoContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.alertTitle)
                        .setMessage(R.string.scope)
                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                type = 0;
                                startActivity(new Intent(MainActivity.this, ContactListActivity.class));
                            }
                        })
                        .setNegativeButton(R.string.allData, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                type = 1;
                                startActivity(new Intent(MainActivity.this, ContactListActivity.class));
                            }
                        }).create();

                builder.show();
            }
        });

    }
}
