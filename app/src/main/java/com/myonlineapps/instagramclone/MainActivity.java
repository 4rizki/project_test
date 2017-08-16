package com.myonlineapps.instagramclone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText etUserName;
    EditText etPassword;
    Button btnMode;
    TextView tvMode;
    boolean loginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnMode = (Button) findViewById(R.id.btnMode);
        tvMode = (TextView) findViewById(R.id.tvMode);

        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    ButtonCLick(null);
                }
                return false;
            }
        });

        InitializeParse();



        // biar jika user sudah pernah login nggak usah login lagi --> parse itu nyimpan di local database, jd
        //ga perlu login lagi
        if (ParseUser.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
            //intent.putExtra("name", etUserName.getText().toString());
            startActivity(intent);
        }

        //CreateObject();
        //ReadObject();
    }

    public void ChangeMode(View view){
//        if (view.getId() == findViewById(R.id.btnMode).getId()){
//            if (view.get)
//        }
        if (loginMode){
            loginMode = false;
            btnMode.setText("SIGNUP");
            tvMode.setText("Or, Login");
        }
        else {
            loginMode = true;
            btnMode.setText("LOGIN");
            tvMode.setText("Or, SignUp");
        }
    }

    public void ButtonCLick(View view){
        if (loginMode){
            Toast.makeText(this, "Mode: Login", Toast.LENGTH_SHORT).show();
            ParseUser.logInInBackground(etUserName.getText().toString(), etPassword.getText().toString(),
                    new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(e==null){
                                Toast.makeText(MainActivity.this, "Login berhasil dengan email: "+user.getEmail(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                //intent.putExtra("name", etUserName.getText().toString());
                                startActivity(intent);

                            }
                            else {
                                Toast.makeText(MainActivity.this, "Login gagal: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "Mode: SignUp", Toast.LENGTH_SHORT).show();
            ParseUser user = new ParseUser();
            user.setUsername(etUserName.getText().toString());
            user.setPassword(etPassword.getText().toString());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null){
                        Toast.makeText(MainActivity.this, "Sign up berhasil", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                       // intent.putExtra("name", etUserName.getText().toString());
                        startActivity(intent);

                    }
                    else{
                        Toast.makeText(MainActivity.this, "Sign up gagal: "+ e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void ReadObject() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Score");
        query.whereGreaterThan("score", 70)
                .addDescendingOrder("score");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if (objects.size()>0){
                        int i=0;
                        for (ParseObject object : objects){
                            i++;
                            String username = object.getString("username");
                            Integer score = object.getInt("score");
                            Toast.makeText(MainActivity.this, i+". username: "+username+"\n"+"score: "+Integer.toString(score), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{

                }
            }
        });
    }

    private void CreateObject() {
        ParseObject obj = new ParseObject("Score");
        obj.put("username","ahmad");
        obj.put("score",90);
        obj.put("prevScore",55);
        obj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(MainActivity.this, "Save Berhasil", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Save gagal: "+e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void InitializeParse() {
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("YOUR_APP_ID")
                .server("YOUR_SERVER_URL")
                .build()
        );
    }

    public void HideInput(View v){
        //Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        InputMethodManager ipm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        ipm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }

}
