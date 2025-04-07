package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long, // Foreign key to link jobs to users (if applicable)
    val name: String,
    val address: String,
    val description: String?,
    val status: String, // e.g., "Pending", "In Progress", "Completed"
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
    // Add other relevant fields like contact info, estimated hours, etc.
)
