package com.example.yaseen.androidchat;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Yaseen on 4/12/2015.
 */
public class Snoopy extends ChatActivity {

    public String str;

    public void check(String snoop){
        if (snoop.equalsIgnoreCase("email me")) {
            //display message
            str = "Snoopy: Hey... The message you received mentions 'email'.... Would you like to send an email?";
            chatArrayAdapter.add(new ChatMessage(2, str));
        }

    }

    public void doAction(String snoopy){
        if (snoopy.equalsIgnoreCase(str)) {
            sendEmail();
        }
        else{
            Toast.makeText(getApplicationContext(), snoopy, Toast.LENGTH_SHORT).show();
        }
    }

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {bundle.getString("name")+"@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
