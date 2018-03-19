package com.capitalone.digitalmessagingsystem.clove;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SMSHealthCheckActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "SMSHealthCheckActivity";
    private static final String[] permissions = {Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS};
    private String shortCode;
    private String frequency;
    private String sla;
    private boolean isButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smshealth_check);


        Button button = findViewById(R.id.button);
        button.setClickable(true);
        button.setOnClickListener(this);

        Button button1 = findViewById(R.id.button1);
        button1.setClickable(true);
        button1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        EditText shortCodeEd = findViewById(R.id.editText);
        EditText frequencyEd = findViewById(R.id.editText1);
        EditText slaEd = findViewById(R.id.editText2);

        shortCode = shortCodeEd.getText().toString();
        frequency = frequencyEd.getText().toString();
        sla = slaEd.getText().toString();
        Intent intent = new Intent(this, SMSHealthCheckService.class);
        intent.putExtra("sc", shortCode);
        intent.putExtra("frequency", frequency);
        intent.putExtra("sla", sla);

        switch (view.getId()) {

            case R.id.button:

                shortCodeEd.getText().clear();
                frequencyEd.getText().clear();
                slaEd.getText().clear();
                slaEd.setFocusable(false);

                if(isButtonClicked){
                    Toast.makeText(this, "Please stop the service before starting an another one!", Toast.LENGTH_LONG).show();
                    isButtonClicked = false;
                } else {
                    Toast.makeText(this, "Welcome To Clove! Monitoring started", Toast.LENGTH_LONG).show();
                    boolean hasAllPermissions = true;
                    isButtonClicked = true;

                    for(String permission : permissions){
                        if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                            hasAllPermissions = false;
                            break;
                        }
                    }

//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
//        {
//            Log.d(TAG, "onClick: inside check self permission");
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
//        }
                    if(!hasAllPermissions){
                        Log.d(TAG, "onClick: inside check self permission");
                        ActivityCompat.requestPermissions(this, permissions, 1);
                    }
                    else
                    {
                        Log.d(TAG, "onClick: outside check self permission");
//                        Intent intent = new Intent(this, SMSHealthCheckService.class);
                        startService(intent);
                    }
                    break;

                }

            case R.id.button1:
                stopService(intent);
                Toast.makeText(this, "Service stopped successfully!", Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 1:
                Log.d(TAG, "onRequestPermissionsResult: inside permission");
                Intent intent = new Intent(this, SMSHealthCheckService.class);
                startService(intent);
//                if(grantResults != null)
//                {
//                    for(int grantResult : grantResults){
//                        if(grantResult == PackageManager.PERMISSION_GRANTED){
//
//                        }
//                    }
//                }
//
//                if(grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    Log.d(TAG, "onRequestPermissionsResult: inside permission");
//                    Intent intent = new Intent(this, SMSHealthCheckService.class);
//                    startService(intent);
//                }
                break;
        }

    }
}
