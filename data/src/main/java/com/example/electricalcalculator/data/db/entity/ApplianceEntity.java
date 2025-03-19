package com.example.electricalcalculator.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity representation of an appliance in the database
 */
@Entity(tableName = "appliances")
public class ApplianceEntity {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private double wattage;
    private double voltageRating;
    private boolean isMotorLoad;

    public ApplianceEntity(String name, double wattage, double voltageRating, boolean isMotorLoad) {
        this.name = name;
        this.wattage = wattage;
        this.voltageRating = voltageRating;
        this.isMotorLoad = isMotorLoad;
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getWattage() { return wattage; }
    public void setWattage(double wattage) { this.wattage = wattage; }
    
    public double getVoltageRating() { return voltageRating; }
    public void setVoltageRating(double voltageRating) { this.voltageRating = voltageRating; }
    
    public boolean isMotorLoad() { return isMotorLoad; }
    public void setMotorLoad(boolean motorLoad) { isMotorLoad = motorLoad; }
}
