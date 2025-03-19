package com.example.electricalcalculator; 
 
import android.app.Application; 
import dagger.hilt.android.HiltAndroidApp; 
 
/**  
 * Main application class with Hilt dependency injection 
 */ 
@HiltAndroidApp 
public class ElectricianApp extends Application { 
 
    @Override 
    public void onCreate() { 
        super.onCreate(); 
    } 
} 
