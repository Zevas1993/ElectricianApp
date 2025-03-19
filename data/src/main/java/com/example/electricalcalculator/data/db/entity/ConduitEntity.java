package com.example.electricalcalculator.data.db.entity; 
 
import androidx.room.Entity; 
import androidx.room.PrimaryKey; 
 
/**  
 * Entity representation of a conduit in the database 
 */ 
@Entity(tableName = "conduits") 
public class ConduitEntity { 
    @PrimaryKey(autoGenerate = true) 
    private long id; 
    private String conduitType; 
    private String conduitSize; 
    private double conduitArea; 
 
    public ConduitEntity(String conduitType, String conduitSize, double conduitArea) { 
        this.conduitType = conduitType; 
        this.conduitSize = conduitSize; 
        this.conduitArea = conduitArea; 
    } 
 
    // Getters and setters 
    public long getId() { return id; } 
    public void setId(long id) { this.id = id; } 
    public String getConduitType() { return conduitType; } 
    public void setConduitType(String conduitType) { this.conduitType = conduitType; } 
    public String getConduitSize() { return conduitSize; } 
    public void setConduitSize(String conduitSize) { this.conduitSize = conduitSize; } 
    public double getConduitArea() { return conduitArea; } 
    public void setConduitArea(double conduitArea) { this.conduitArea = conduitArea; } 
} 
