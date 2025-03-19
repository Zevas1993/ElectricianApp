package com.example.electricalcalculator.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity representation of a box fill calculation in the database
 */
@Entity(tableName = "box_fill")
public class BoxFillEntity {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String boxType;
    private String boxDimensions;
    private double boxVolumeInCubicInches;
    private double totalRequiredVolumeInCubicInches;
    private double remainingVolumeInCubicInches;
    private double fillPercentage;
    private boolean isWithinLimits;
    private long timestamp;

    public BoxFillEntity(String boxType, String boxDimensions, double boxVolumeInCubicInches,
                         double totalRequiredVolumeInCubicInches, double remainingVolumeInCubicInches,
                         double fillPercentage, boolean isWithinLimits, long timestamp) {
        this.boxType = boxType;
        this.boxDimensions = boxDimensions;
        this.boxVolumeInCubicInches = boxVolumeInCubicInches;
        this.totalRequiredVolumeInCubicInches = totalRequiredVolumeInCubicInches;
        this.remainingVolumeInCubicInches = remainingVolumeInCubicInches;
        this.fillPercentage = fillPercentage;
        this.isWithinLimits = isWithinLimits;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBoxType() {
        return boxType;
    }

    public void setBoxType(String boxType) {
        this.boxType = boxType;
    }

    public String getBoxDimensions() {
        return boxDimensions;
    }

    public void setBoxDimensions(String boxDimensions) {
        this.boxDimensions = boxDimensions;
    }

    public double getBoxVolumeInCubicInches() {
        return boxVolumeInCubicInches;
    }

    public void setBoxVolumeInCubicInches(double boxVolumeInCubicInches) {
        this.boxVolumeInCubicInches = boxVolumeInCubicInches;
    }

    public double getTotalRequiredVolumeInCubicInches() {
        return totalRequiredVolumeInCubicInches;
    }

    public void setTotalRequiredVolumeInCubicInches(double totalRequiredVolumeInCubicInches) {
        this.totalRequiredVolumeInCubicInches = totalRequiredVolumeInCubicInches;
    }

    public double getRemainingVolumeInCubicInches() {
        return remainingVolumeInCubicInches;
    }

    public void setRemainingVolumeInCubicInches(double remainingVolumeInCubicInches) {
        this.remainingVolumeInCubicInches = remainingVolumeInCubicInches;
    }

    public double getFillPercentage() {
        return fillPercentage;
    }

    public void setFillPercentage(double fillPercentage) {
        this.fillPercentage = fillPercentage;
    }

    public boolean isWithinLimits() {
        return isWithinLimits;
    }

    public void setWithinLimits(boolean withinLimits) {
        isWithinLimits = withinLimits;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
