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
import static com.ogil.dopamine.LoginActivity.PREF_NAME;
import static com.ogil.dopamine.LoginActivity.STR_USER;
import static com.ogil.dopamine.LoginActivity.STR_USER_COUNT;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email, password;
    private TextView helpText;
    private Button register;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);

        helpText = findViewById(R.id.help);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String e = email.getText().toString();
                if(user.length()>3 && pass.length() > 3 && e.length()> 3){
                    int c = pref.getInt(STR_USER_COUNT, 0);
                    pref.edit().putString(STR_USER+(++c), user+":"+pass+":"+email).apply();
                    pref.edit().putInt(STR_USER_COUNT, ++c).apply();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), "Check Fields again!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
