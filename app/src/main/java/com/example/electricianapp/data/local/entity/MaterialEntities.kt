package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.electricianapp.domain.model.materials.MaterialCategory
import com.example.electricianapp.domain.model.materials.MaterialItemStatus
import com.example.electricianapp.domain.model.materials.MaterialListStatus
import com.example.electricianapp.domain.model.materials.TransactionType
import com.example.electricianapp.domain.model.materials.UnitOfMeasure

/**
 * Entity representing a material in the database
 */
@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String, // Stored as string representation of MaterialCategory
    val partNumber: String,
    val manufacturer: String,
    val unitOfMeasure: String, // Stored as string representation of UnitOfMeasure
    val unitPrice: Double,
    val imageUrl: String?,
    val tagsJson: String // JSON array of tags
)

/**
 * Entity representing a material list in the database
 */
@Entity(
    tableName = "material_lists",
    indices = [Index("jobId")]
)
data class MaterialListEntity(
    @PrimaryKey
    val id: String,
    val jobId: String?,
    val name: String,
    val description: String,
    val createdDate: Long, // Stored as timestamp
    val modifiedDate: Long, // Stored as timestamp
    val status: String, // Stored as string representation of MaterialListStatus
    val tagsJson: String // JSON array of tags
)

/**
 * Entity representing an item in a material list in the database
 */
@Entity(
    tableName = "material_list_items",
    foreignKeys = [
        ForeignKey(
            entity = MaterialListEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialListId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("materialListId"),
        Index("materialId")
    ]
)
data class MaterialListItemEntity(
    @PrimaryKey
    val id: String,
    val materialListId: String,
    val materialId: String,
    val quantity: Double,
    val notes: String,
    val status: String, // Stored as string representation of MaterialItemStatus
    val allocatedQuantity: Double,
    val usedQuantity: Double
)

/**
 * Entity representing a supplier in the database
 */
@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val contactName: String,
    val phone: String,
    val email: String,
    val website: String,
    val address: String,
    val notes: String,
    val preferredCategoriesJson: String, // JSON array of MaterialCategory strings
    val isPreferred: Boolean
)

/**
 * Entity representing a price quote from a supplier in the database
 */
@Entity(
    tableName = "material_quotes",
    foreignKeys = [
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SupplierEntity::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("materialId"),
        Index("supplierId")
    ]
)
data class MaterialQuoteEntity(
    @PrimaryKey
    val id: String,
    val materialId: String,
    val supplierId: String,
    val price: Double,
    val minimumQuantity: Double,
    val availableQuantity: Double?,
    val leadTimeInDays: Int?,
    val quoteDate: Long, // Stored as timestamp
    val expirationDate: Long?, // Stored as timestamp
    val notes: String
)

/**
 * Entity representing inventory of materials in the database
 */
@Entity(
    tableName = "material_inventory",
    foreignKeys = [
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("materialId")]
)
data class MaterialInventoryEntity(
    @PrimaryKey
    val id: String,
    val materialId: String,
    val quantity: Double,
    val location: String,
    val lastUpdated: Long, // Stored as timestamp
    val minimumQuantity: Double,
    val notes: String
)

/**
 * Entity representing a transaction affecting material inventory in the database
 */
@Entity(
    tableName = "material_transactions",
    foreignKeys = [
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("materialId"),
        Index("jobId"),
        Index("materialListId")
    ]
)
data class MaterialTransactionEntity(
    @PrimaryKey
    val id: String,
    val materialId: String,
    val jobId: String?,
    val materialListId: String?,
    val quantity: Double,
    val transactionType: String, // Stored as string representation of TransactionType
    val date: Long, // Stored as timestamp
    val performedBy: String,
    val notes: String
)

/**
 * Entity representing a material package template in the database
 */
@Entity(tableName = "material_packages")
data class MaterialPackageEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val tagsJson: String // JSON array of tags
)

/**
 * Entity representing an item in a material package template in the database
 */
@Entity(
    tableName = "material_package_items",
    foreignKeys = [
        ForeignKey(
            entity = MaterialPackageEntity::class,
            parentColumns = ["id"],
            childColumns = ["packageId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("packageId"),
        Index("materialId")
    ]
)
data class MaterialPackageItemEntity(
    @PrimaryKey
    val id: String,
    val packageId: String,
    val materialId: String,
    val quantity: Double,
    val notes: String
)
