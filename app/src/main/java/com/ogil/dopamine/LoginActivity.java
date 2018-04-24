package com.ogil.dopamine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private TextView registerText;
    private Button login;
    public static final String PREF_NAME = "ogil";
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        username = findViewById(R.id.username);
        password = findViewById(R.id.pass);
        login = findViewById(R.id.login);
        registerText = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                if(user.length() >3 && pass.length() > 3)
                if(checkLogin(user, pass)){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else{
                    Toast.makeText(getApplicationContext(), "Wrong Username password combination", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Put Something cool!", Toast.LENGTH_LONG).show();
            }
        });
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    public static final String STR_USER_COUNT = "count";
    public static final String STR_USER =  "user:";

    private boolean checkLogin(String user, String pass){
        int count = pref.getInt(STR_USER_COUNT, 0);
        for(int i=0; i<count; i++){
            if(pref.getString(STR_USER+i, "-1").contains(user)){
                if(pref.getString(STR_USER+i, "-1").contains(pass))
                    return true;
                else
                    return false;
            }
        }
        return false;
    }
}
