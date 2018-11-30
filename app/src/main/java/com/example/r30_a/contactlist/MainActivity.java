package com.example.r30_a.contactlist;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    Button btnGotoContact;
    public static int type;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGotoContact = (Button)findViewById(R.id.btnGotoContact);
        btnGotoContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(PermissionsUtil.hasPermission(MainActivity.this,Manifest.permission.READ_CONTACTS ) &&
                        PermissionsUtil.hasPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS)){

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

                }else {
                    PermissionsUtil.requestPermission(MainActivity.this, new PermissionListener() {
                        @Override
                        public void permissionGranted(@NonNull String[] permission) {}
                        @Override
                        public void permissionDenied(@NonNull String[] permission) {}
                    }, new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS});
                }

            }
        });

    }
}
