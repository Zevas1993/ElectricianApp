package com.example.electricalcalculator.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity representation of a dwelling unit in the database
 */
@Entity(tableName = "dwelling_units")
public class DwellingUnitEntity {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private double squareFootage;
    private int voltageRating;
    private long timestamp;

    public DwellingUnitEntity(String name, double squareFootage, int voltageRating, long timestamp) {
        this.name = name;
        this.squareFootage = squareFootage;
        this.voltageRating = voltageRating;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getSquareFootage() { return squareFootage; }
    public void setSquareFootage(double squareFootage) { this.squareFootage = squareFootage; }
    
    public int getVoltageRating() { return voltageRating; }
    public void setVoltageRating(int voltageRating) { this.voltageRating = voltageRating; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
