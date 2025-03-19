package com.example.electricalcalculator.data.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Cross-reference entity to represent many-to-many relationship between box fills and wires
 */
@Entity(
    primaryKeys = {"boxFillId", "wireId"},
    foreignKeys = {
        @ForeignKey(
            entity = BoxFillEntity.class,
            parentColumns = "id",
            childColumns = "boxFillId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = WireEntity.class,
            parentColumns = "id",
            childColumns = "wireId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index("wireId")
    }
)
public class BoxFillWireCrossRef {
    private long boxFillId;
    private long wireId;
    private int quantity;
    
    public BoxFillWireCrossRef(long boxFillId, long wireId, int quantity) {
        this.boxFillId = boxFillId;
        this.wireId = wireId;
        this.quantity = quantity;
    }

    public long getBoxFillId() {
        return boxFillId;
    }

    public void setBoxFillId(long boxFillId) {
        this.boxFillId = boxFillId;
    }

    public long getWireId() {
        return wireId;
    }

    public void setWireId(long wireId) {
        this.wireId = wireId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
