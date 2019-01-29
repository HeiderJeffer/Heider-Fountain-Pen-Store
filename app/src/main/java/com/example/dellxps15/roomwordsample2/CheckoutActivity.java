package com.example.dellxps15.roomwordsample2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static android.content.Context.MODE_PRIVATE;

public class CheckoutActivity extends AppCompatActivity {

    public static String SHARED_PREFS_FILE_NAME = "fontana_shared_prefs";
    Context context = CheckoutActivity.this;
    private ProductViewModel mWordViewModel;
    int totalPrice;
    private SessionHandler session;
    String xuv = "";
    String superItems;
    String zimmer;

    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;

    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private String products_url = "http://heiderjeffer.com/member/getproducts.php"; // ******

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        session = new SessionHandler(getApplicationContext());


        String name="xxx";

        // CHECK FOR NEW ITEMS
        // DELETE ITEM IN SHARED PREF
        if(isNetworkAvailable()){
            checkProd();
        }

        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);
        String restoredText = "scdsc";


            name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
            int count = prefs.getInt("count", 0);
            totalPrice = 0;


            for(int i =0; i < count; i++){
                int idName = prefs.getInt("idName"+(i+1), 0);

                System.out.println("RAIIIIIIIIIIIIIIITAHHHH: " + idName);

                if(idName < 0){
                    continue;
                }

                mWordViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
                Products p = mWordViewModel.findItem(idName);

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.cart_item, null);


                ////////////////////// - - - ////////////////////

//                String imgName = p.getImage();
//                int resID = context.getResources().getIdentifier(imgName , "drawable", context.getPackageName());
//
//                ImageView imageItemView = (ImageView) view.findViewById(R.id.imageView4);
//                imageItemView.setImageResource(resID);


                String imgName = p.getImage();
                int lin = imgName.lastIndexOf("/");
                String justImgName = imgName.substring(lin+1);

                ContextWrapper wrapper = new ContextWrapper(context);
                File file = wrapper.getDir("Images",MODE_PRIVATE);
                file = new File(file, justImgName);

                try {

                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
                    //ma.showToast(context, justImgName);
                    ImageView imageItemView = (ImageView) view.findViewById(R.id.imageView4);
                    imageItemView.setImageBitmap(b);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                ///////////////////// - - - ///////////////////

                TextView textView4 = (TextView) view.findViewById(R.id.textView4);
                textView4.setText(p.getProduct());
                TextView textView5 = (TextView) view.findViewById(R.id.textView5);
                textView5.setText("EUR "+String.valueOf(p.getPrice()));

                TextView textView11 = (TextView) view.findViewById(R.id.textView11);
                textView11.setText("Item:");
                textView11.setTag(p.getId());

                ///////

                Button myButton = new Button(this);
                myButton.setId(p.getId());
                myButton.setWidth(100);
                myButton.setOnClickListener(getOnClickDoSomething(myButton));
                myButton.setTextColor(Color.parseColor("#ffffff"));
                myButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                myButton.setPadding(10, 10, 10, 10);
                myButton.setText("Remove item from cart");

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.height = 90;
                params.setMargins(0, 10, 0, 50);
                myButton.setLayoutParams(params);

                // ******************** //

                totalPrice = totalPrice + p.getPrice();

                ViewGroup main = (ViewGroup) findViewById(R.id.insert_point);
                main.addView(view, -1);
                main.addView(myButton, -1);
            }

        LayoutInflater inflater = getLayoutInflater();
        View viewlogin = inflater.inflate(R.layout.loginbutton_item, null);
        ViewGroup main2 = (ViewGroup) findViewById(R.id.insert_point);
        main2.addView(viewlogin, -1);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });

        final ProductListAdapter adapter = new ProductListAdapter(this);
        mWordViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        mWordViewModel.getAllProducts().observe(this, new Observer<List<Products>>() {
            @Override
            public void onChanged(@Nullable final List<Products> products) {
                // Update the cached copy of the words in the adapter.
                adapter.setProducts(products);
                checkProd();
            }
        });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onResume() {
        super.onResume();


        // CHECK FOR NEW ITEMS
        // DELETE ITEM IN SHARED PREF
        if(isNetworkAvailable()){
            context = CheckoutActivity.this;
            checkProd();
        }

        Toast.makeText(getApplicationContext(),
                "onresume!", Toast.LENGTH_SHORT).show();

        final ProductListAdapter adapter = new ProductListAdapter(this);
        mWordViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        mWordViewModel.getAllProducts().observe(this, new Observer<List<Products>>() {
            @Override
            public void onChanged(@Nullable final List<Products> products) {
                // Update the cached copy of the words in the adapter.
                adapter.setProducts(products);
                checkProd();
            }
        });

        Intent intent = getIntent();

        if(intent.hasExtra("GOTOEXTRA")){
            String extra = intent.getStringExtra("GOTOEXTRA");
            if(extra.equals("CART")){
//                Toast.makeText(getApplicationContext(),
//                        "WE SHOULD DO SOMETHING", Toast.LENGTH_SHORT).show();

                checkProd();

                Intent myIntent = new Intent(CheckoutActivity.this, CheckoutActivity.class);
                CheckoutActivity.this.startActivity(myIntent);
                finish();
            }

        }else{
            // Do something else
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("MESSAGE: ERROR: ", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    public void updateUI(GoogleSignInAccount account){
        if(account != null){
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            TextView lg6 = (TextView) findViewById(R.id.textView6);
            lg6.setVisibility(View.GONE);


            Toast.makeText(getApplicationContext(),
                    "YOU ARE LOGGED IN!", Toast.LENGTH_SHORT).show();

            LayoutInflater inflater = getLayoutInflater();
            View viewx = inflater.inflate(R.layout.pay_item, null);



            if(totalPrice > 1){
                TextView tx = (TextView) viewx.findViewById(R.id.textView3);
                tx.setText("TOTAL PRICE: EUR "+ String.valueOf(totalPrice));

                ViewGroup main = (ViewGroup) findViewById(R.id.insert_point);
                main.addView(viewx, -1);
            } else {
                TextView tx = (TextView) viewx.findViewById(R.id.textView3);
                tx.setText("You have no items in your cart!");


                TextView bv4 = (TextView) viewx.findViewById(R.id.buttonView4);
                bv4.setVisibility(View.GONE);

                ViewGroup main = (ViewGroup) findViewById(R.id.insert_point);
                main.addView(viewx, -1);
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "YOU ARE NOT LOGGED IN!", Toast.LENGTH_SHORT).show();

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    // *********************************************************************

    private void checkProd() {

        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, "username");
            request.put(KEY_PASSWORD, "password");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, products_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Check if user got logged in successfully

                            if (true) {

//                                Toast.makeText(getApplicationContext(),
//                                        "WE ARE INSIDSE!", Toast.LENGTH_SHORT).show();


                                JSONObject obj = new JSONObject(response.toString());
                                int len = obj.length();
                                int[] myIntArray = new int[len];

                                for(int i = 0; i < len; i++){
                                    JSONObject prod = obj.getJSONObject(Integer.toString(i));
                                    Products x = new Products(Integer.parseInt(prod.getString("id")), prod.getString("product"), prod.getString("description"), Integer.parseInt(prod.getString("price")), prod.getString("image"));

                                    // find id and check if it exists in sharedprefs
                                    // warning: except empty cart

                                    myIntArray[i] = Integer.parseInt(prod.getString("id"));

                                }

                                // pick each position / idName from sharedprefs and check through myIntArray
                                SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);
                                int count = prefs.getInt("count", 0);

                                // check if idName exists in myIntArray
                                // if true OK
                                // if not delete shared pref input


                                for(int i =0; i< count; i++){
                                    int idNamex = prefs.getInt("idName"+(i+1), -1);

                                    if(idNamex < 0){
                                        continue;
                                    }

                                    boolean contains = Arrays.asList(myIntArray).contains(idNamex);
                                    boolean cont = false;

                                    for(int y = 0; y < myIntArray.length; y++){
                                        if(myIntArray[y] == idNamex){
                                            cont = true;
                                        }
                                    }

                                    if(cont){
                                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE).edit();
                                        editor.putInt("count", (i+1));
                                        editor.putInt("idName"+(i+1), idNamex);
                                        editor.apply();
                                    } else {
                                        // remove item from pr
                                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE).edit();
                                        editor.putInt("count", (i+1));
                                        editor.putInt("idName"+(i+1), -2);
                                        editor.apply();
                                    }
                                }



                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        RequestQueue queue = Volley.newRequestQueue(this);
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    // *********************************************************************

    public void makeCharge(View view){
        Intent myIntent = new Intent(CheckoutActivity.this, PaymentActivity.class);

        // ------------------------------------------------------------
        // ---------- Here get all the positions of items in cart
        // --- And then consequently get names of the items in cart
        // - Store them in an array or somewhere

        SharedPreferences prefs = CheckoutActivity.this.getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);
        int count = prefs.getInt("count", 0); //0 is the default value.

        String itemsInCartPositions = "";

        for(int i =0; i< count; i++){
            int idNamePosition = prefs.getInt("idName"+(i+1), -1);


            if(idNamePosition >= 0){

                //Store The Positions and carry it forward to the next activity
                itemsInCartPositions = itemsInCartPositions + idNamePosition + ",";

            }
        }



        //--- Find the logged in user and carry it forward too

        superItems = itemsInCartPositions;
        //String itemsAsIds = getProd2();


        //------------------------------------------------------------

        String personEmail="";
        String personId="";


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(CheckoutActivity.this);
        if (acct != null) {
            personEmail = acct.getEmail();
            personId = acct.getId();
        }


        myIntent.putExtra("TOTALPR", String.valueOf(totalPrice));
        myIntent.putExtra("ITEMSINCART", itemsInCartPositions);
        myIntent.putExtra("USEREMAIL", personEmail);
        CheckoutActivity.this.startActivity(myIntent);
    }

    public void removeFromCart(View view){


    }

    View.OnClickListener getOnClickDoSomething(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {

            int item = v.getId();
            Toast.makeText(CheckoutActivity.this, String.valueOf(v.getId()), Toast.LENGTH_SHORT).show();

                SharedPreferences prefs = CheckoutActivity.this.getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);
                int count = prefs.getInt("count", 0); //0 is the default value.

                int newcount = count - 1;
                int breakposition;

                for(int i =0; i< count; i++){
                    int idName = prefs.getInt("idName"+(i+1), -1);


                    if(idName == item){
                        // remove item from pr
                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE).edit();
                        editor.putInt("count", (i+1));
                        editor.putInt("idName"+(i+1), -2);
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE).edit();
                        editor.putInt("count", (i+1));
                        editor.putInt("idName"+(i+1), idName);
                        editor.apply();

                    }
                }

                Intent myIntent = new Intent(CheckoutActivity.this, CheckoutActivity.class);
                CheckoutActivity.this.startActivity(myIntent);


            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(CheckoutActivity.this, MainActivity.class);
        CheckoutActivity.this.startActivity(myIntent);
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
            Intent myIntent = new Intent(CheckoutActivity.this, DashboardActivity.class);
            CheckoutActivity.this.startActivity(myIntent);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings2) {
            Intent myIntent = new Intent(CheckoutActivity.this, MainActivity.class);
            CheckoutActivity.this.startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private String getProd2() {

        JSONObject request = new JSONObject();

        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, "username");
            request.put(KEY_PASSWORD, "password");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, products_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Check if user got logged in successfully

                            if (true) {

                                JSONObject obj = new JSONObject(response.toString());
                                int len = obj.length();

                                for(int i = 0; i < len; i++){
                                    JSONObject prod = obj.getJSONObject(Integer.toString(i));

                                    String xamxerxx = superItems.substring(0,superItems.length()-1);

                                    String[] stringArray = xamxerxx.split("\\.");

                                    int xlen = stringArray.length;
                                    //Toast.makeText(getApplicationContext(),
                                            //Integer.toString(xlen), Toast.LENGTH_SHORT).show();

                                    for(int j = 0; j < xlen; j++){


                                        if(i == Integer.parseInt(stringArray[j])){
                                            xuv = xuv + prod.getString("product") + ".";
                                        }
                                    }

                                    Products x = new Products(Integer.parseInt(prod.getString("id")), prod.getString("product"), prod.getString("description"), Integer.parseInt(prod.getString("price")), prod.getString("image"));
                                    //newPop(x);

                                }

                                Toast.makeText(getApplicationContext(),
                                        xuv, Toast.LENGTH_SHORT).show();

                                zimmer = xuv;


                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        RequestQueue queue = Volley.newRequestQueue(this);
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);

        //xuv ="khali";
        return zimmer;
    }


}
