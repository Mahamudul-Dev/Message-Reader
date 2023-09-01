package com.uniquepaybd.uniquepay;
import static com.uniquepaybd.uniquepay.LoginManager.getSavedUid;
import static com.uniquepaybd.uniquepay.LoginManager.getUserEmail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        Bundle bundle = intent.getExtras();
//        if (bundle != null) {
//            Object[] pdus = (Object[]) bundle.get("pdus");
//            if (pdus != null) {
//                for (Object pdu : pdus) {
//                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
//                    String sender = smsMessage.getOriginatingAddress();
//                    String messageBody = smsMessage.getMessageBody();
//
//                    // Display a toast for the incoming SMS
//                    ToastHelper.showCustomToast(context, "SMS received from: " + sender + "\nMessage: " + messageBody,null);
//
//                    // Check if the app has notification permission and request if necessary
//                    if (hasNotificationPermission(context)) {
//                        // Create and display a notification with a unique ID
//                        Functions.createNotification(context, "New message from:"+sender, messageBody);
//                    }
//                }
//            }
//        }

        if (intent != null && "android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            // Implement your SMS reading logic here
            // Access SMS content from the intent extras
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            // Process the messages as needed
            StringBuilder fullMessage = new StringBuilder();
            String smsAddress = "";
            for (SmsMessage message : messages) {
                fullMessage.append(message.getDisplayMessageBody());
                smsAddress = message.getOriginatingAddress().toString().trim();
            }
            Log.d("fullSMS", fullMessage.toString().trim());
            saveMessageToFirebase(fullMessage.toString().trim(), smsAddress, context);

            if (hasNotificationPermission(context)) {
                // Create and display a notification with a unique ID
                Functions.createNotification(context, "New message from:"+smsAddress, fullMessage.toString().trim());

            }
        }
    }

    private boolean hasNotificationPermission(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
    }


    private void saveMessageToFirebase(String messageBody, String messageAddress, Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference messagesRef = database.getReference().child("messageBody");


        // Generate a unique key for the message using push()
        DatabaseReference newMessageRef = messagesRef.push();

        HashMap<String, String> data = new HashMap<>();
        data.put("email",getUserEmail(context));
        data.put("uid", getSavedUid(context));
        data.put("message", messageBody);
        data.put("address", messageAddress);

        // Write the data to the database
        newMessageRef.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Data successfully saved to the database
                Log.d("Firebase", "Message saved to Firebase");
                Log.i("firebaseSMS", data.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors that occurred during data writing
                Log.e("Firebase", "Error saving message to Firebase: " + exception.toString());
            }
        });
    }


}
