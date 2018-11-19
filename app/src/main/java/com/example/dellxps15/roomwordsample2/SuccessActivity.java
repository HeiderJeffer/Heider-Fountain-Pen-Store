package com.example.dellxps15.roomwordsample2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import static com.example.dellxps15.roomwordsample2.MainActivity.SHARED_PREFS_FILE_NAME;

public class SuccessActivity extends AppCompatActivity {

    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        String totamount = getIntent().getExtras().getString("TOTALPRCHARGED");

        TextView tx = (TextView) findViewById(R.id.buttonViewSx);
        tx.setText("SUCCESS: Charged amount EUR "+ totamount);

        final SharedPreferences prefx = getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefx.edit();
        editor.clear().commit();
    }

    public void backHome(View view){
        Intent myIntent = new Intent(SuccessActivity.this, MainActivity.class);
        SuccessActivity.this.startActivity(myIntent);
    }
}
