package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Redirect to login screen immediately
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity so user can't go back to it
    }
}
