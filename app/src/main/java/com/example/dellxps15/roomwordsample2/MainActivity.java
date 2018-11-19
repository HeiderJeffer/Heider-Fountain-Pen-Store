package com.example.dellxps15.roomwordsample2;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private ProductViewModel mWordViewModel;
    public static String SHARED_PREFS_FILE_NAME = "fontana_shared_prefs";
    Context context = MainActivity.this;
    private static final String TAG = "MainActivity";

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";

    private String products_url = "http://anjandash.com/member/getproducts.php"; // ******

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Heider Fountain Pen Store");


        // IF THERE IS INTERNET CALL getProd() else do nothing
        if(isNetworkAvailable()){
            getProd();
            checkProd();
            updateCartList();
        }


        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final ProductListAdapter adapter = new ProductListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        final SharedPreferences prefx = getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefx.edit();
        //editor.clear().commit();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        SharedPreferences prefs = MainActivity.this.getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);
                        int count = prefs.getInt("count", 0); //0 is the default value.


                        for(int i =0; i< count; i++){
                            int idName = prefs.getInt("idName"+(i+1), -1);

                            if(idName == position){
                                return;
                            }
                        }

                        Toast.makeText(MainActivity.this, "Item added to cart: "+position, Toast.LENGTH_SHORT).show();
                        TextView tvc = (TextView) findViewById(R.id.textViewCart);
                        tvc.setBackgroundResource(R.drawable.removebutton);
                        tvc.setText("ITEM ADDED");

                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE).edit();
                        editor.putInt("count", (count+1));
                        editor.putInt("idName"+(count+1), position);
                        editor.apply();
                    }
                })
        );


        mWordViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        mWordViewModel.getAllProducts().observe(this, new Observer<List<Products>>() {
            @Override
            public void onChanged(@Nullable final List<Products> products) {
                // Update the cached copy of the items in the adapter.
                adapter.setProducts(products);
                checkProd();
            }
        });


    }

    public void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(getApplicationContext(),
                                                "ITEM WITH PRODUCT ID:" + idNamex +" WAS REMOVED AUTOMATICALLY FROM YOUR CART BECAUSE IT JUST WENT OUT OF STOCK OR WAS REMOVED THE ONLINE STORE.", Toast.LENGTH_SHORT).show();

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


    @Override
    protected void onResume() {
        super.onResume();

        context = MainActivity.this;
        // CHECK FOR NEW ITEMS
        // DELETE ITEM IN SHARED PREF
        if(isNetworkAvailable()){
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
    }

    private void getProd() {

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

//                                for(int i = 0; i < len; i++){
//                                    JSONObject prod = obj.getJSONObject(Integer.toString(i));
//                                    Products x = new Products(Integer.parseInt(prod.getString("id")), prod.getString("product"), prod.getString("description"), Integer.parseInt(prod.getString("price")), prod.getString("image"));
//
//                                    //check if this product already exists in room database
//                                    //then delete that
//
//                                    int cid = Integer.parseInt(prod.getString("id"));
//                                    deleteOne(cid);
//                                }
//
//                                //whichever product is leftover is deleted
//                                checkPop();

                                // CALL DELETEALL() HERE
                                oldPop();

                                for(int i = 0; i < len; i++){
                                    JSONObject prod = obj.getJSONObject(Integer.toString(i));

                                    Products x = new Products(Integer.parseInt(prod.getString("id")), prod.getString("product"), prod.getString("description"), Integer.parseInt(prod.getString("price")), prod.getString("image"));
                                    newPop(x);

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

    public boolean newPop(Products p){

        ProductViewModel c = new ProductViewModel(this.getApplication());
        c.insert(p);
        return true;
    }

    public boolean oldPop(){

        ProductViewModel c = new ProductViewModel(this.getApplication());
        Products x = new Products(0,"CHECK1", "Gold-tip Mont Blanc (Black and Gold) Made in Switzerland", 678, "a1");

        c.delAll(x);
        return true;
    }

    public void deleteOne(int id){
        ProductViewModel c = new ProductViewModel(this.getApplication());
        Products x = new Products(0,"CHECK1", "Gold-tip Mont Blanc (Black and Gold) Made in Switzerland", 678, "a1");

        c.deleteOne(id);
    }

    public void checkPop(){
        ProductViewModel c = new ProductViewModel(this.getApplication());

         List<Products> products = c.getAllProducts().getValue();

         if(products != null){
             Toast.makeText(getApplicationContext(),
                     "SOMETHING REMOVED/DELETED", Toast.LENGTH_SHORT).show();
         } else {
             Toast.makeText(getApplicationContext(),
                     "NO ITEMS WERE DELETED", Toast.LENGTH_SHORT).show();
         }
    }

    public static double getRandomDoubleBetweenRange(double min, double max){
        double x = (Math.random()*((max-min)+1))+min;
        return x;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
            Intent myIntent = new Intent(MainActivity.this, DashboardActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings2) {
            Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkout(View view){
//        Intent intent = new Intent(MainActivity.this, MainActivity.class);
//        intent.putExtra("GOTOEXTRA", "CART");
//        startActivity(intent);

        Intent myIntent = new Intent(MainActivity.this, CheckoutActivity.class);
        myIntent.putExtra("GOTOEXTRA", "CART");
        MainActivity.this.startActivity(myIntent);

    }

    public void updateCartList(){

    }

}
