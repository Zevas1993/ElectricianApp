package com.example.electricalcalculator; 
 
import android.app.Activity; 
import android.os.Bundle; 
import android.widget.TextView; 
 
public class MainActivity extends Activity { 
    @Override 
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_main); 
        TextView infoView = findViewById(R.id.infoView); 
        infoView.setText("The Electrician App includes tools for box fill calculations, conduit fill, " + 
                         "dwelling load calculations and more."); 
    } 
} 
