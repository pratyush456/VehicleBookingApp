package com.vehiclebooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "MainActivity started");
            
            // Set content view first to prevent layout issues
            setContentView(R.layout.activity_main);
            
            Log.d(TAG, "Content view set successfully");
            
            // Small delay to ensure proper initialization
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    redirectToLogin();
                }
            }, 100);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in MainActivity onCreate", e);
            Toast.makeText(this, "Error starting app: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Don't finish() if there's an error, let user see the error
        }
    }
    
    private void redirectToLogin() {
        try {
            Log.d(TAG, "Attempting to start LoginActivity");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity so user can't go back to it
            Log.d(TAG, "LoginActivity started successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error starting LoginActivity", e);
            Toast.makeText(this, "Error starting login: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
