package com.example.yaseen.androidchat;

import android.support.v7.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChatActivity extends ActionBarActivity implements LocationListener{
    private static final String TAG = "ChatActivity";

    LocationManager locationManager;
    TextView tv ;
    public ChatArrayAdapter chatArrayAdapter;
    private ListView listView;

    SharedPreferences prefs;
    List<NameValuePair> params;
    EditText chat_msg;
    Button buttonSend;
    Bundle bundle;
    String my_msg;
    public String myMessage,eMessage,cMessage,lMessage,rMessage;
    public String str1;
    public String mylocation;
    public Location location;
    public Double latitude,mylatitude;
    public Double longitude,mylongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //tab = (TableLayout)findViewById(R.id.tab);


        prefs = getSharedPreferences("Chat", 0);
        bundle = getIntent().getBundleExtra("INFO");
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("CURRENT_ACTIVE", bundle.getString("mobno"));
        edit.commit();
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

        buttonSend = (Button) findViewById(R.id.buttonSend);

        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_list_item);
        listView.setAdapter(chatArrayAdapter);

        chat_msg = (EditText) findViewById(R.id.chat_msg);
        my_msg = chat_msg.getText().toString();

        ActionBar ab = getSupportActionBar();
        ab.setTitle(getIntent().getExtras().getString("name"));
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        if (bundle.getString("name") != null) {
            chatArrayAdapter.add(new ChatMessage(0, bundle.getString("msg")));
            str1 = bundle.getString("name");
            ab.setTitle(str1);
            //call check method
            check(bundle.getString("msg"),0);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    tv = (TextView) v.findViewById(R.id.singleMessage);
                    String snoopy = tv.getText().toString();
                    doAction(snoopy);
                }

            });


        }

        else
            str1 = getIntent().getExtras().getString("name");


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMessage = chat_msg.getText().toString();
                chat_msg.setText("");
                chatArrayAdapter.add(new ChatMessage(1, myMessage));

                new Send().execute();

                //Call the check method..
                check(myMessage,1);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        tv = (TextView) v.findViewById(R.id.singleMessage);
                        String snoopy = tv.getText().toString();
                        doAction(snoopy);
                    }

                });


            }


        });


        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);


        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("msg");
            str1 = intent.getStringExtra("fromname");
            String str2 = intent.getStringExtra("fromu");
            if(str2.equals(bundle.getString("mobno"))){

                chatArrayAdapter.add(new ChatMessage(0, str));

                //call check method
                check(str,0);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        tv = (TextView) v.findViewById(R.id.singleMessage);
                        String snoopy = tv.getText().toString();
                        doAction(snoopy);
                    }

                });
            }



        }
    };
    private class Send extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("from", prefs.getString("REG_FROM","")));
            params.add(new BasicNameValuePair("fromn", prefs.getString("FROM_NAME","")));
            params.add(new BasicNameValuePair("to", bundle.getString("mobno")));
            params.add((new BasicNameValuePair("msg",myMessage)));

            JSONObject jObj = json.getJSONFromUrl("https://snoopon-fawwaz94.c9users.io:8080/send",params);
            return jObj;



        }
        @Override
        protected void onPostExecute(JSONObject json) {



            String res = null;
            try {
                res = json.getString("response");
                if(res.equals("Failure")){
                    Toast.makeText(getApplicationContext(),"The user has logged out. You cant send message anymore !",Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {str1 +"@gmail.com"};
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
            //finish();
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void makeCall() {
        Log.i("Make call", "");

        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        phoneIntent.setData(Uri.parse("tel:" + (bundle.getString("mobno"))));

        try {
            startActivity(phoneIntent);
            //finish();
            Log.i("Finished making a call.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ChatActivity.this,
                    "Call failed, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void sendLocation() {

        boolean isGPSEnabled=false;
        boolean isNetworkEnabled=false;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no GPS Provider and no network provider is enabled
            chatArrayAdapter.add(new ChatMessage(2, "Sorry I was unable to send your Location.... Please try after sometime... \nI think your GPS is not enabled that's why\n Or you are in a remote area...\nOr your are using an emulator :P"));
        }
        else
        {   // Either GPS provider or network provider is enabled

            // First get location from Network Provider
            if (isNetworkEnabled)
            {
                locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,0,
                        0, this);
                if (locationManager != null)
                {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null)
                    {
                        mylatitude = location.getLatitude();
                        mylongitude = location.getLongitude();
                        //new Send().execute();
                    }
                }
            }// End of IF network enabled

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled)
            {
                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0,
                        0, this);
                if (locationManager != null)
                {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null)
                    {
                        mylatitude = location.getLatitude();
                        mylongitude = location.getLongitude();
                    }
                }

            }// End of if GPS Enabled

            mylocation = "Your Location ----- Latitude:  " + mylatitude + " Longitude: " + mylongitude + " has been sent.\nDo you want to check your location in maps?";
            chatArrayAdapter.add(new ChatMessage(2, mylocation));
            myMessage = "I'm at this geographic points --- Latitude: " + mylatitude + " Longitude: " + mylongitude + " \npls locate me in your map";
            new Send().execute();


        }// End of Either GPS provider or network provider is enabled

    }
    @Override
    public void onLocationChanged(Location arg0)
    {   // TODO Auto-generated method stub
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
    }

    public void check(String snoop,int fromwhere){

        if(fromwhere == 0) {

            if (snoop.matches("(?i).* email me .*") || snoop.matches("(?i)email me .*") || snoop.matches("(?i).* email me") || snoop.equalsIgnoreCase("email me")) {
                //display message
                eMessage = "Hey... The message you received mentions 'email'.... Would you like to send an email to " + str1 + " ?";
                chatArrayAdapter.add(new ChatMessage(2, eMessage));
                chatArrayAdapter.add(new ChatMessage(2, "Or reply to him by tapping any of these"));
                chatArrayAdapter.add(new ChatMessage(2, "---> Can't email you right now... Will do it later"));
                chatArrayAdapter.add(new ChatMessage(2, "---> I will do it in a while"));
                chatArrayAdapter.add(new ChatMessage(2, "---> I'm a lil busy"));
            } else if (snoop.matches("(?i).* call me .*") || snoop.matches("(?i)call me .*") || snoop.matches("(?i).* call me") || snoop.equalsIgnoreCase("call me")) {

                cMessage = "Hey... The message you've received mentions 'call'... Would you like to call " + str1 + " ?";
                chatArrayAdapter.add(new ChatMessage(2, cMessage));
                chatArrayAdapter.add(new ChatMessage(2, "Or reply to him by tapping any of these"));
                chatArrayAdapter.add(new ChatMessage(2, "---> Can't talk right now...I can chat only"));
                chatArrayAdapter.add(new ChatMessage(2, "---> Can't talk to you right now... Will call you later"));
                chatArrayAdapter.add(new ChatMessage(2, "---> I will call you in a while"));
                chatArrayAdapter.add(new ChatMessage(2, "---> I'm a lil busy"));
            }
            else if (snoop.matches("(?i).* where are you .*") || snoop.matches("(?i)where are you .*") || snoop.matches("(?i).* where are you") || snoop.equalsIgnoreCase("where are you")
                    || snoop.equalsIgnoreCase("where are you?") || snoop.matches("(?i).* where are you? .*") || snoop.matches("(?i)where are you? .*") || snoop.matches("(?i).* where are you?")
                    || snoop.equalsIgnoreCase("whr r u?") || snoop.matches("(?i).* whr r u? .*") || snoop.matches("(?i)whr r u? .*") || snoop.matches("(?i).* whr r u?")
                    || snoop.equalsIgnoreCase("whr r u") || snoop.matches("(?i).* whr r u .*") || snoop.matches("(?i)whr r u .*") || snoop.matches("(?i).* whr r u")
                    || snoop.equalsIgnoreCase("whr u?") || snoop.matches("(?i).* whr u? .*") || snoop.matches("(?i)whr u? .*") || snoop.matches("(?i).* whr u?")
                    || snoop.equalsIgnoreCase("whr u") || snoop.matches("(?i).* whr u .*") || snoop.matches("(?i)whr u .*") || snoop.matches("(?i).* whr u")
                    || snoop.equalsIgnoreCase("whr are u?") || snoop.matches("(?i).* whr are u? .*") || snoop.matches("(?i)whr are u? .*") || snoop.matches("(?i).* whr are u?")
                    || snoop.equalsIgnoreCase("whr are u") || snoop.matches("(?i).* whr are u .*") || snoop.matches("(?i)whr are  u .*") || snoop.matches("(?i).* whr are u")
                    || snoop.equalsIgnoreCase("where are u?") || snoop.matches("(?i).* where are u? .*") || snoop.matches("(?i)where are u? .*") || snoop.matches("(?i).* where are u?")
                    || snoop.equalsIgnoreCase("where are u") || snoop.matches("(?i).* where are u .*") || snoop.matches("(?i)where are  u .*") || snoop.matches("(?i).* where are u"))
            {
                lMessage = "Hey..." + str1 + " is asking for your location... Should I send him your location ?";
                chatArrayAdapter.add(new ChatMessage(2, lMessage));
                chatArrayAdapter.add(new ChatMessage(2, "Or reply to him by tapping any of these"));
                chatArrayAdapter.add(new ChatMessage(2, "---> On my way"));
                chatArrayAdapter.add(new ChatMessage(2, "---> I'm in my house"));
                chatArrayAdapter.add(new ChatMessage(2, "---> I'm at my friend's place"));
                chatArrayAdapter.add(new ChatMessage(2, "---> I'm in college"));
            }
            else if (snoop.matches("(?i).* meet me at .*") || snoop.matches("(?i)meet me at .*") || snoop.matches("(?i).* can we meet at .*") || snoop.matches("(?i)can we meet at .*") || snoop.matches("(?i).* let's meet at .*") || snoop.matches("(?i)let's meet at .*") || snoop.matches("(?i).* lets meet at .*") || snoop.matches("(?i)lets meet at .*"))
            {   rMessage = "Hey... Do you want me to set a reminder?\nI see that you have to meet  " + str1;
                chatArrayAdapter.add(new ChatMessage(2, rMessage));
            }

            else if (snoop.matches("(?i).* how are you .*") || snoop.matches("(?i)how are you .*") || snoop.matches("(?i).* how are you") || snoop.equalsIgnoreCase("how are you")
                    || snoop.equalsIgnoreCase("how are you?") || snoop.matches("(?i).* how are you? .*") || snoop.matches("(?i)how are you? .*") || snoop.matches("(?i).* how are you?")
                    || snoop.equalsIgnoreCase("how r u?") || snoop.matches("(?i).* how r u? .*") || snoop.matches("(?i)how r u? .*") || snoop.matches("(?i).* how r u?")
                    || snoop.equalsIgnoreCase("how r u") || snoop.matches("(?i).* how r u .*") || snoop.matches("(?i)how r u .*") || snoop.matches("(?i).* how r u")
                    || snoop.equalsIgnoreCase("how u?") || snoop.matches("(?i).* how u? .*") || snoop.matches("(?i)how u? .*") || snoop.matches("(?i).* how u?")
                    || snoop.equalsIgnoreCase("how u") || snoop.matches("(?i).* how u .*") || snoop.matches("(?i)how u .*") || snoop.matches("(?i).* how u")
                    || snoop.equalsIgnoreCase("how are u?") || snoop.matches("(?i).* how are u? .*") || snoop.matches("(?i)how are u? .*") || snoop.matches("(?i).* how are u?")
                    || snoop.equalsIgnoreCase("how are u") || snoop.matches("(?i).* how are u .*") || snoop.matches("(?i)how are  u .*") || snoop.matches("(?i).* how are u")
                    || snoop.equalsIgnoreCase("how are u?") || snoop.matches("(?i).* how are u? .*") || snoop.matches("(?i)how are u? .*") || snoop.matches("(?i).* how are u?")
                    || snoop.equalsIgnoreCase("how are u") || snoop.matches("(?i).* how are u .*") || snoop.matches("(?i)how are  u .*") || snoop.matches("(?i).* how are u"))
            {
                chatArrayAdapter.add(new ChatMessage(2, "Tap on any of these to reply"));
                chatArrayAdapter.add(new ChatMessage(2, "---> I'm Good....What about you?"));
                chatArrayAdapter.add(new ChatMessage(2, "---> I'm fine....How are you?"));
                chatArrayAdapter.add(new ChatMessage(2, "---> I'm fine...."));

            }
        }

        else if(fromwhere == 1){

            if (snoop.matches("(?i).* meet me at .*") || snoop.matches("(?i)meet me at .*") || snoop.matches("(?i).* can we meet at .*") || snoop.matches("(?i)can we meet at .*") || snoop.matches("(?i).* let's meet at .*") || snoop.matches("(?i)let's meet at .*") || snoop.matches("(?i).* lets meet at .*") || snoop.matches("(?i)lets meet at .*"))
            {   rMessage = "Hey... Do you want me to set a reminder?\nI see that you have to meet  " + str1;
                chatArrayAdapter.add(new ChatMessage(2, rMessage));
            }

        }
        }

    public void doAction(String snoopy){
        if (snoopy.equals(eMessage)) {
            sendEmail();
        }

        else if(snoopy.equals("---> Can't email you right now... Will do it later")){
            chatArrayAdapter.add(new ChatMessage(1,"Can't email you right now... Will do it later"));
            myMessage = "Can't email you right now... Will do it later";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase("---> I will do it in a while")){
            chatArrayAdapter.add(new ChatMessage(1,"I will do it in a while"));
            myMessage = "I will do it in a while";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase("---> I'm a lil busy")){
            chatArrayAdapter.add(new ChatMessage(1,"I'm a lil busy"));
            myMessage = "I'm a lil busy";
            new Send().execute();
        }

        else if(snoopy.equals(cMessage)){
            makeCall();
        }

        else if(snoopy.equals("---> Can't talk right now...I can chat only")){
            chatArrayAdapter.add(new ChatMessage(1,"Can't talk right now...I can chat only"));
            myMessage = "Can't talk right now...I can chat only";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase("---> Can't talk to you right now... Will call you later")){
            chatArrayAdapter.add(new ChatMessage(1,"Can't talk to you right now... Will call you later"));
            myMessage = "Can't talk to you right now... Will call you later";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase("---> I will call you in a while")){
            chatArrayAdapter.add(new ChatMessage(1,"I will call you in a while"));
            myMessage = "I will call you in a while";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase("---> I'm a lil busy")){
            chatArrayAdapter.add(new ChatMessage(1,"I'm a lil busy"));
            myMessage = "I'm a lil busy";
            new Send().execute();
        }

        else if(snoopy.equals(lMessage)){
            sendLocation();
        }

        else if(snoopy.equals("---> On my way")){
            chatArrayAdapter.add(new ChatMessage(1,"On my way"));
            myMessage = "On my way";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase("---> I'm in my house")){
            chatArrayAdapter.add(new ChatMessage(1,"I'm in my house"));
            myMessage = "I'm in my house";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase("---> I'm at my friend's place")){
            chatArrayAdapter.add(new ChatMessage(1,"I'm at my friend's place"));
            myMessage = "I'm at my friend's place";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase("---> I'm in college")){
            chatArrayAdapter.add(new ChatMessage(1,"I'm in college"));
            myMessage = "I'm in college";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase(mylocation)){
            //openMaps();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?q=loc:" + String.format("%f,%f", mylatitude, mylongitude)));
            startActivity(intent);
        }

        else if(snoopy.endsWith("locate me in your map")){
            //Get Latitude and Longitude
            String result[] = snoopy.split(" ");

            //Get Latitude
            latitude = Double.parseDouble(result[7]);

            //Get Longitude
            longitude = Double.parseDouble(result[9]);

            //openMaps();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?q=loc:" + String.format("%f,%f", latitude, longitude)));
            startActivity(intent);
        }

        else if(snoopy.equalsIgnoreCase(rMessage)){
            Calendar cal = Calendar.getInstance();
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", cal.getTimeInMillis());
            intent.putExtra("allDay", false);
            intent.putExtra("description", "Set By Snoopy");
            intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
            intent.putExtra("title", "You've got a meeting with "+str1);
            startActivity(intent);
        }

        else if(snoopy.equalsIgnoreCase("---> I'm Good....What about you?")){
            chatArrayAdapter.add(new ChatMessage(1,"I'm Good....What about you?"));
            myMessage = "I'm Good....What about you?";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase("---> I'm fine....How are you?")){
            chatArrayAdapter.add(new ChatMessage(1,"I'm fine....How are you?"));
            myMessage = "I'm fine....How are you?";
            new Send().execute();
        }

        else if(snoopy.equalsIgnoreCase("---> I'm fine....")){
            chatArrayAdapter.add(new ChatMessage(1,"I'm fine...."));
            myMessage = "I'm fine....";
            new Send().execute();
        }

        else{
            Toast.makeText(getApplicationContext(), snoopy, Toast.LENGTH_SHORT).show();
        }
    }
}