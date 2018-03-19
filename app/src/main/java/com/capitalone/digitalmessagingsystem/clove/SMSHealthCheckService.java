package com.capitalone.digitalmessagingsystem.clove;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class SMSHealthCheckService extends Service {
    private static final String TAG = "SMSHealthCheckService";
    Handler handler = new Handler();
    Handler cursorhandler = new Handler();
    private String shortCode;
    private int frequency;
    private int sla;
    public SMSHealthCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(toastRunnable);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand: ");
        shortCode = intent.getStringExtra("sc");
        sla = Integer.parseInt(intent.getStringExtra("sla"));
        frequency = Integer.parseInt(intent.getStringExtra("frequency"));

        Log.d(TAG, "onStartCommand: "+ intent.getStringExtra("sc"));
        Log.d(TAG, "onStartCommand: "+ intent.getStringExtra("sla"));
        Log.d(TAG, "onStartCommand: "+ intent.getStringExtra("frequency"));
        handler.postDelayed(toastRunnable, frequency * 60 * 1000);
//        handler.post(toastRunnable);

        return START_STICKY;
    }

    final Runnable toastRunnable = new Runnable(){
        public void run(){
            sendSMS();
            handler.postDelayed( toastRunnable, frequency * 60 * 1000);

        }
    };

    private void sendSMS(){

        try {
            Log.w(TAG, "sendSMS: came inside try" );
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(shortCode, null, "HELP", null, null);
            Log.w(TAG, "sendSMS: sent message successfully");

            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    Cursor cursor = null;
                    boolean isAssertionSuccessful = true;
                    String threadId = null;
                    SmsManager smsManager = SmsManager.getDefault();
                    try {
//                        long systemTime = System.currentTimeMillis();
                        Thread.sleep(5000);
                        cursor = getContentResolver().query(Uri.parse("content://sms/inbox"),new String[] { "thread_id","address","body", "date" }, "address=?", new String[]{"51617"}, null);
                        Log.d(TAG, "sendSMS: count of messages is"+ cursor.getCount());

                        if(cursor.getCount() == 0 || cursor.moveToFirst()){

                            threadId = cursor.getString(0);
                            Log.w(TAG, "run: ThreadId is" + threadId );
                            Date date1 = new Date(Long.parseLong(cursor.getString(3)));
                            Log.w(TAG, "run: Date is" + date1 );

                            long timestamp = Long.parseLong(cursor.getString(3));
                            long tenSecAgo = System.currentTimeMillis() - sla * 1000;
                            if(timestamp < tenSecAgo){
                                Log.w(TAG, "run: timestamp is older than 10 secs");
                                isAssertionSuccessful = false;
                            }
                            if(isAssertionSuccessful){
                                for(int idx=0;idx<cursor.getColumnCount();idx++)
                                {
                                    Log.w(TAG, "run: Data is"+ " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx));

                                    if("body".equalsIgnoreCase(cursor.getColumnName(idx)) && cursor.getString(idx).contains("Capital One Alert: Need help")){
                                        isAssertionSuccessful = true;
                                        break;
                                    } else {
                                        isAssertionSuccessful = false;
                                    }
                                }
                            }
//                            do{
//                                Log.w(TAG, "sendSMS: Count of messages received ----"+ cursor.getCount());
//                                threadId = cursor.getString(0);
//                                Log.w(TAG, "run: ThreadId is" + threadId );
//                                Date date1 = new Date(Long.parseLong(cursor.getString(3)));
//                                Log.w(TAG, "run: Date is" + date1 );
//                                for(int idx=0;idx<cursor.getColumnCount();idx++)
//                                {
//                                    Log.w(TAG, "run: Data is"+ " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx));
//                                    if("body".equalsIgnoreCase(cursor.getColumnName(idx)) && cursor.getString(idx).contains("Capital One Alert: Need help")){
//                                        isAssertionSuccessful = true;
//                                    } else {
//                                      isAssertionSuccessful = false;
//                                    }
//                                }
//
//
//                            } while (cursor.moveToNext());

                            //Remove all messages
//                            getContentResolver().delete(Uri.parse("content://sms/"), "thread_id= ?", new String[] {threadId});

                            Log.w(TAG, "run: assertions is"+isAssertionSuccessful );

                            if(!isAssertionSuccessful){

                                smsManager.sendTextMessage("2108598688", null, "Clove detected that SMS Delivery has issues.", null, null);
                            } else {
                                smsManager.sendTextMessage("2108598688", null, "Clove detected that Helath of SMS Delivery is good.", null, null);
                            }
                        } else {
                            Log.w(TAG, "sendSMS: No Message received" );
                            smsManager.sendTextMessage("2108598688", null, "Clove detected that SMS Delivery has issues.", null, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        cursor.close();
                    }
                }
            };
            cursorhandler.post(runnable);

//            Thread.sleep(10000);
//            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"),new String[] { "_id", "thread_id", "address", "person", "date","body", "type" }, "address=?", new String[]{"51617"}, null);
//            Log.d(TAG, "sendSMS: count of messages is"+ cursor.getCount());
//            if(cursor.moveToFirst()){
//                do{
//                    Log.w(TAG, "sendSMS: Count of messages received ----"+ cursor.getCount());
//
//                } while (cursor.moveToNext());
//            } else {
//                Log.w(TAG, "sendSMS: No Message received" );
//            }
        } catch (Exception e){
            Log.e(TAG, "sendSMS: received exception ---------", e);
        }



    }

}
