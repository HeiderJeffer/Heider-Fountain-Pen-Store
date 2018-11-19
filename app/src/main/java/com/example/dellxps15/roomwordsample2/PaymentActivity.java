package com.example.dellxps15.roomwordsample2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;


import com.stripe.android.view.CardInputWidget;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;

//import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity{

    Card card;
    public static final String PUBLISHABLE_KEY = "pk_test_ODGk52WMPpS9kBGzKeHClyMr";
    private ProgressDialog progress;
    CardInputWidget mCardInputWidget;
    String amount;
    private SessionHandler session;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private static final String PREF_NAME = "UserSession";

    private ProgressDialog pDialog;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_CARD_NUMBER = "number";
    private static final String KEY_EXPM = "expm";
    private static final String KEY_EXPY = "expy";
    private static final String KEY_CVC = "cvc";
    private static final String KEY_EMPTY = "";
    private String register_url = "http://anjandash.com/member/savecard.php"; // ******
    private String register_url2 = "http://anjandash.com/member/getcard.php"; // ******

    String cardNum;
    int expM;
    int expY;
    String cvc;

    String cardNumGet;
    String expMGet;
    String expYGet;
    String cvcGet;

    String username="", itemsInCart="", userEmail="";
    CheckBox repeatChkBx;
    TextView txv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        session = new SessionHandler(PaymentActivity.this);

        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        String totpr = getIntent().getExtras().getString("TOTALPR");
        userEmail = getIntent().getExtras().getString("USEREMAIL");;
        itemsInCart = getIntent().getExtras().getString("ITEMSINCART");;
        amount = String.valueOf((Integer.parseInt(totpr))*1);

        Toast.makeText(PaymentActivity.this, "USER:"+userEmail+" ITEMS: "+itemsInCart, Toast.LENGTH_LONG).show();


        if(session.isLoggedIn()){
            User u = session.getUserDetails();
            username = u.username;
            //Log.i("------------->", cardNumGet);

            getCard();

        }

        repeatChkBx = ( CheckBox ) findViewById( R.id.checkBox );
        repeatChkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    Card c = mCardInputWidget.getCard();

                    cardNum = c.getNumber();
                    expM = c.getExpMonth();
                    expY = c.getExpYear();
                    cvc = c.getCVC();

                    saveCard();
                    //Toast.makeText(PaymentActivity.this, "AFTER CXCXCX:", Toast.LENGTH_SHORT).show();

                }

            }
        });

        txv = (TextView) findViewById(R.id.textView7);

    }

    /**
     * Display Progress bar while registering
     */
    private void displayLoader() {
        pDialog = new ProgressDialog(PaymentActivity.this);
        pDialog.setMessage("Signing Up.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    public void useNewCard(View view){

        mCardInputWidget.clear();
        repeatChkBx.setVisibility(View.VISIBLE);
        txv.setVisibility(View.GONE);
    }

    private void saveCard() {
        //displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_CARD_NUMBER, cardNum);
            request.put(KEY_EXPM, expM);
            request.put(KEY_EXPY, expY);
            request.put(KEY_CVC, cvc);
            request.put(KEY_USERNAME, username);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, register_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //pDialog.dismiss();

                        try {

                            //Check if user got registered successfully
                            if (response.getInt(KEY_STATUS) == 0) {
                                Toast.makeText(PaymentActivity.this, "Card saved:"+cardNum, Toast.LENGTH_SHORT).show();

                            }else if(response.getInt(KEY_STATUS) == 1){
                                Toast.makeText(PaymentActivity.this, "Save Failed:"+cardNum, Toast.LENGTH_SHORT).show();


                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage()+"ssss", Toast.LENGTH_LONG).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    private void getCard() {
        //displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, username);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, register_url2, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //pDialog.dismiss();

                        try {

                            //Check if user got registered successfully
                            if (response.getInt(KEY_STATUS) == 0) {
                                //Toast.makeText(PaymentActivity.this, response.getString(KEY_CARD_NUMBER), Toast.LENGTH_SHORT).show();


                                // get card details

                                cardNumGet =  response.getString(KEY_CARD_NUMBER);
                                expMGet = response.getString(KEY_EXPM);
                                expYGet = response.getString(KEY_EXPY);
                                cvcGet = response.getString(KEY_CVC);

                                if(cardNumGet != null){
                                    //Toast.makeText(PaymentActivity.this, cardNumGet, Toast.LENGTH_SHORT).show();
                                    mCardInputWidget.setCardNumber(cardNumGet);
                                    mCardInputWidget.setExpiryDate(Integer.parseInt(expMGet), Integer.parseInt(expYGet));
                                    mCardInputWidget.setCvcCode(cvcGet);
                                }

                                Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
                                repeatChkBx.setVisibility(View.GONE);
                                txv.setVisibility(View.VISIBLE);

                            }else if(response.getInt(KEY_STATUS) == 1){
                                Toast.makeText(PaymentActivity.this, "Save Failed:"+cardNum, Toast.LENGTH_SHORT).show();


                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage()+"ssss", Toast.LENGTH_LONG).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }


    public void handleSub(View view){
        //mCardInputWidget = (CardInputWidget) view.findViewById(R.id.card_input_widget);

        if(mCardInputWidget == null){
            Toast.makeText(PaymentActivity.this, "INVALID CARD!", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(PaymentActivity.this, "GOOD NEWS!", Toast.LENGTH_SHORT).show();
        }


        if(mCardInputWidget.getCard()!=null){
            card = mCardInputWidget.getCard();
            String cvv= mCardInputWidget.getCard().getCVC();
            int exp= mCardInputWidget.getCard().getExpMonth();
            int exp_year= mCardInputWidget.getCard().getExpYear();
            String card_num= mCardInputWidget.getCard().getNumber();

            card = new Card(card_num, exp, exp_year, cvv);
            //Toast.makeText(PaymentActivity.this, "STAGE 1 SUCCESS!", Toast.LENGTH_SHORT).show();

            Stripe stripe = null;
            stripe = new Stripe(PaymentActivity.this, PUBLISHABLE_KEY);
            stripe.createToken(card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // Send token to your server
                            Log.i("token OOOOOOOOOOOO => ","token"+token);
                            Toast.makeText(PaymentActivity.this, "PROCESSING PAYMENT...", Toast.LENGTH_SHORT).show();

                            new StripeCharge(token.getId(), "loggedin user", amount).execute();
                            //httpPostRequest(PaymentActivity.this, "abc", "anjan8@gmail.com");
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            Log.d("token","excep"+error.getMessage());
                            Toast.makeText(PaymentActivity.this, "ERROR IN CREATING TOKEN", Toast.LENGTH_SHORT).show();

                        }
                    }
            );
        }


        if (card == null) {
            Stripe stripe = null;
            try {
                stripe = new Stripe(PaymentActivity.this, PUBLISHABLE_KEY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class StripeCharge extends AsyncTask<String, Void, String> {
        String token, name, amount;

        public StripeCharge(String token, String name, String amount) {
            this.token = token;
            this.name = name;
            this.amount = amount;
        }

        @Override
        protected String doInBackground(String... params) {
            new Thread() {
                @Override
                public void run() {
                    boolean result = postData(name,token,amount);
                    if(result){
                        boolean chk = postData2(userEmail, itemsInCart, amount);

                        if(chk){
                            Intent myIntent = new Intent(PaymentActivity.this, SuccessActivity.class);
                            myIntent.putExtra("ITEMSORDERED", itemsInCart);
                            myIntent.putExtra("TOTALPRCHARGED", amount);
                            PaymentActivity.this.startActivity(myIntent);
                        } else {
                            Toast.makeText(PaymentActivity.this, "Insert failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }.start();
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Result",s);
        }
    }

    public boolean postData(String description, String token, String amount) {
        // Create a new HttpClient and Post Header

        boolean check = false;

        try {
            URL url = new URL("http://pixellato.com/androidtest/charge.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            String data = "";
            data += "&" + URLEncoder.encode("stripeToken", "UTF-8") + "="
                    + URLEncoder.encode(token, "UTF-8");
            data += "&" + URLEncoder.encode("amount", "UTF-8") + "="
                    + URLEncoder.encode(amount, "UTF-8");

            OutputStream os = null;

            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            os.close();

            Log.i("response code XXXXXX =>", conn.getResponseCode() + " ");

            if(conn.getResponseCode() == 200){

                check = true;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return check;
    }

    public boolean postData2(String userEmail, String itemsInCart, String amount) {
        // Create a new HttpClient and Post Header
        try {
            URL url = new URL("http://anjandash.com/member/savetrans.php?");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            String data = "";
            data += "&" + URLEncoder.encode("useremail", "UTF-8") + "="
                    + URLEncoder.encode(userEmail, "UTF-8");
            data += "&" + URLEncoder.encode("itemsincart", "UTF-8") + "="
                    + URLEncoder.encode(itemsInCart, "UTF-8");
            data += "&" + URLEncoder.encode("amount", "UTF-8") + "="
                    + URLEncoder.encode(amount, "UTF-8");

            OutputStream os = null;

            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            os.close();

            Log.i("response code XXXXXX =>", conn.getResponseCode() + " ");

            if(conn.getResponseCode() == 200){
                //Toast.makeText(PaymentActivity.this, "Transaction success", Toast.LENGTH_LONG).show();
                return true;
            } else {
                //Toast.makeText(PaymentActivity.this, "Transaction Failed", Toast.LENGTH_LONG).show();
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(PaymentActivity.this, DashboardActivity.class);
            PaymentActivity.this.startActivity(myIntent);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings2) {
            Intent myIntent = new Intent(PaymentActivity.this, MainActivity.class);
            PaymentActivity.this.startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }

}

