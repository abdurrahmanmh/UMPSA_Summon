package com.cb20034.umpsas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class StartPage extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        Button bLogin = findViewById(R.id.bLogin);
        Button bRegister = findViewById(R.id.bRegister);

        bLogin.setOnClickListener(n->{
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
        });
        bRegister.setOnClickListener(n->{
            Intent intent = new Intent(getApplicationContext(),Register.class);
            startActivity(intent);
        });
    }
}