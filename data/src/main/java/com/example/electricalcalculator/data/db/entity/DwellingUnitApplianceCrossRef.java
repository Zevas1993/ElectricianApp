package com.example.electricalcalculator.data.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Cross-reference entity to represent many-to-many relationship between dwelling units and appliances
 */
@Entity(
    primaryKeys = {"dwellingUnitId", "applianceId"},
    foreignKeys = {
        @ForeignKey(
            entity = DwellingUnitEntity.class,
            parentColumns = "id",
            childColumns = "dwellingUnitId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = ApplianceEntity.class,
            parentColumns = "id",
            childColumns = "applianceId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index("applianceId")
    }
)
public class DwellingUnitApplianceCrossRef {
    
    private long dwellingUnitId;
    private long applianceId;
    private int quantity;

    public DwellingUnitApplianceCrossRef(long dwellingUnitId, long applianceId, int quantity) {
        this.dwellingUnitId = dwellingUnitId;
        this.applianceId = applianceId;
        this.quantity = quantity;
    }

    // Getters and setters
    public long getDwellingUnitId() { return dwellingUnitId; }
    public void setDwellingUnitId(long dwellingUnitId) { this.dwellingUnitId = dwellingUnitId; }
    
    public long getApplianceId() { return applianceId; }
    public void setApplianceId(long applianceId) { this.applianceId = applianceId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
