package com.example.rishabhcha.vinnovate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IncomingSms extends BroadcastReceiver {

    private DatabaseReference mDustbinRef;

    public void onReceive(Context context, Intent intent) {
        Bundle myBundle = intent.getExtras();
        SmsMessage[] messages = null;
        String strMessage = "";

        mDustbinRef = FirebaseDatabase.getInstance().getReference().child("Dustbin");

        if (myBundle != null) {
            Object[] pdus = (Object[]) myBundle.get("pdus");

            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = myBundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                Log.d("--------number---",messages[i].getDisplayOriginatingAddress());

                if (messages[i].getDisplayOriginatingAddress().equals("+917584004505")){

                    strMessage += messages[i].getMessageBody();

                    String[] strArray = strMessage.split(" ");
                    String fillPercent = strArray[0];
                    String dustbinId = strArray[1];

                    Log.d("-----1----",fillPercent);
                    Log.d("------2---",dustbinId);

                    if (dustbinId.equals("dustbin1")) {
                        if (fillPercent.equals("75")) {
                            Log.d("-----dustbin1----", "called");
                            DatabaseReference newDus = mDustbinRef.child("dustbin1");
                            newDus.child("latitude").setValue("12.8734");
                            newDus.child("longitude").setValue("79.0885");
                            newDus.child("fillpercent").setValue("75");
                        } else{
                            DatabaseReference newDus = mDustbinRef.child("dustbin1");
                            newDus.child("latitude").setValue("12.8734");
                            newDus.child("longitude").setValue("79.0885");
                            newDus.child("fillpercent").setValue("90");
                        }
                    }else if (dustbinId.equals("dustbin2")) {
                        if (fillPercent.equals("75")) {
                            DatabaseReference newDus = mDustbinRef.child("dustbin2");
                            newDus.child("latitude").setValue("12.9248");
                            newDus.child("longitude").setValue("79.1354");
                            newDus.child("fillpercent").setValue("75");
                        } else{
                            DatabaseReference newDus = mDustbinRef.child("dustbin2");
                            newDus.child("latitude").setValue("12.9248");
                            newDus.child("longitude").setValue("79.1354");
                            newDus.child("fillpercent").setValue("90");
                        }
                    }else {
                        if (fillPercent.equals("75")) {
                            DatabaseReference newDus = mDustbinRef.child("dustbin3");
                            newDus.child("latitude").setValue("12.9693");
                            newDus.child("longitude").setValue("79.1559");
                            newDus.child("fillpercent").setValue("75");
                        } else{
                            DatabaseReference newDus = mDustbinRef.child("dustbin3");
                            newDus.child("latitude").setValue("12.9693");
                            newDus.child("longitude").setValue("79.1559");
                            newDus.child("fillpercent").setValue("90");
                        }
                    }
                    Toast.makeText(context, strMessage, Toast.LENGTH_SHORT).show();
                }
            }

            Log.v("SMS", strMessage);

        }
    }}