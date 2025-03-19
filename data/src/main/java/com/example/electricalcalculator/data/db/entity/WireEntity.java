package com.example.electricalcalculator.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity representation of a wire in the database
 */
@Entity(tableName = "wires")
public class WireEntity {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String wireSize;
    private String wireType;
    private String wireColor;
    private double volumeRequirement;

    // Default constructor required by Room
    public WireEntity() {
    }

    // Constructor with parameters
    public WireEntity(String wireSize, String wireType, String wireColor, double volumeRequirement) {
        this.wireSize = wireSize;
        this.wireType = wireType;
        this.wireColor = wireColor;
        this.volumeRequirement = volumeRequirement;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWireSize() {
        return wireSize;
    }

    public void setWireSize(String wireSize) {
        this.wireSize = wireSize;
    }

    public String getWireType() {
        return wireType;
    }

    public void setWireType(String wireType) {
        this.wireType = wireType;
    }

    public String getWireColor() {
        return wireColor;
    }

    public void setWireColor(String wireColor) {
        this.wireColor = wireColor;
    }

    public double getVolumeRequirement() {
        return volumeRequirement;
    }

    public void setVolumeRequirement(double volumeRequirement) {
        this.volumeRequirement = volumeRequirement;
    }
}
