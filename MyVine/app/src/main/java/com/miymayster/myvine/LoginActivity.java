package com.miymayster.myvine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.entry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEditText = (EditText) findViewById(R.id.entry_email);
                EditText passwordEditText = (EditText) findViewById(R.id.entry_password);
                loginWithEmail(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
        findViewById(R.id.entrance_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithFacebook();
            }
        });
        findViewById(R.id.entrance_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithGoogle();
            }
        });
        findViewById(R.id.entrance_twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithTwitter();
            }
        });
    }

    private void loginWithEmail(String email, String password) {
        // TODO: implement login with email and password
        openVineList();
    }

    private void loginWithFacebook() {
        // TODO: implement login with facebook
        openVineList();
    }

    private void loginWithGoogle() {
        // TODO: implement login with google
        openVineList();
    }

    private void loginWithTwitter() {
        // TODO: implement login with twitter
        openVineList();
    }

    private void openVineList() {
        Intent openVineList = new Intent(LoginActivity.this, VineListActivity.class);
        startActivity(openVineList);
        finish();
    }
}
