package com.example.electricianapp.domain.model.materials

import java.util.Date
import java.util.UUID

/**
 * Represents a material item that can be used in electrical work
 */
data class Material(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val category: MaterialCategory,
    val partNumber: String = "",
    val manufacturer: String = "",
    val unitOfMeasure: UnitOfMeasure,
    val unitPrice: Double = 0.0,
    val imageUrl: String? = null,
    val tags: List<String> = emptyList()
)

/**
 * Represents a category of electrical materials
 */
enum class MaterialCategory {
    WIRE_AND_CABLE,
    CONDUIT_AND_FITTINGS,
    BOXES_AND_ENCLOSURES,
    DEVICES_AND_RECEPTACLES,
    LIGHTING_FIXTURES,
    CIRCUIT_BREAKERS,
    PANELS_AND_LOADCENTERS,
    TRANSFORMERS,
    GROUNDING_EQUIPMENT,
    TOOLS,
    SAFETY_EQUIPMENT,
    MISCELLANEOUS
}

/**
 * Represents a unit of measure for materials
 */
enum class UnitOfMeasure {
    EACH,
    FOOT,
    METER,
    POUND,
    KILOGRAM,
    BOX,
    ROLL,
    PACKAGE
}

/**
 * Represents a material list for a job
 */
data class MaterialList(
    val id: String = UUID.randomUUID().toString(),
    val jobId: String? = null,
    val name: String,
    val description: String = "",
    val createdDate: Date = Date(),
    val modifiedDate: Date = Date(),
    val items: List<MaterialListItem> = emptyList(),
    val status: MaterialListStatus = MaterialListStatus.DRAFT,
    val tags: List<String> = emptyList()
)

/**
 * Represents an item in a material list
 */
data class MaterialListItem(
    val id: String = UUID.randomUUID().toString(),
    val materialId: String,
    val material: Material? = null, // Populated when needed
    val quantity: Double,
    val notes: String = "",
    val status: MaterialItemStatus = MaterialItemStatus.PENDING,
    val allocatedQuantity: Double = 0.0,
    val usedQuantity: Double = 0.0
)

/**
 * Represents the status of a material list
 */
enum class MaterialListStatus {
    DRAFT,
    FINALIZED,
    ORDERED,
    PARTIALLY_RECEIVED,
    RECEIVED,
    COMPLETED,
    CANCELLED
}

/**
 * Represents the status of a material item
 */
enum class MaterialItemStatus {
    PENDING,
    ORDERED,
    PARTIALLY_RECEIVED,
    RECEIVED,
    USED,
    RETURNED,
    CANCELLED
}

/**
 * Represents a supplier of electrical materials
 */
data class Supplier(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val contactName: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val address: String = "",
    val notes: String = "",
    val preferredForCategories: List<MaterialCategory> = emptyList(),
    val isPreferred: Boolean = false
)

/**
 * Represents a price quote from a supplier for a material
 */
data class MaterialQuote(
    val id: String = UUID.randomUUID().toString(),
    val materialId: String,
    val supplierId: String,
    val price: Double,
    val minimumQuantity: Double = 1.0,
    val availableQuantity: Double? = null,
    val leadTimeInDays: Int? = null,
    val quoteDate: Date = Date(),
    val expirationDate: Date? = null,
    val notes: String = ""
)

/**
 * Represents inventory of materials
 */
data class MaterialInventory(
    val id: String = UUID.randomUUID().toString(),
    val materialId: String,
    val material: Material? = null, // Populated when needed
    val quantity: Double,
    val location: String = "",
    val lastUpdated: Date = Date(),
    val minimumQuantity: Double = 0.0,
    val notes: String = ""
)

/**
 * Represents a transaction affecting material inventory
 */
data class MaterialTransaction(
    val id: String = UUID.randomUUID().toString(),
    val materialId: String,
    val jobId: String? = null,
    val materialListId: String? = null,
    val quantity: Double,
    val transactionType: TransactionType,
    val date: Date = Date(),
    val performedBy: String = "",
    val notes: String = ""
)

/**
 * Represents the type of material transaction
 */
enum class TransactionType {
    PURCHASE,
    USE,
    RETURN_TO_INVENTORY,
    RETURN_TO_SUPPLIER,
    ADJUSTMENT,
    TRANSFER
}

/**
 * Represents a material package template for common installations
 */
data class MaterialPackage(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val category: String = "",
    val items: List<MaterialPackageItem> = emptyList(),
    val tags: List<String> = emptyList()
)

/**
 * Represents an item in a material package template
 */
data class MaterialPackageItem(
    val id: String = UUID.randomUUID().toString(),
    val materialId: String,
    val material: Material? = null, // Populated when needed
    val quantity: Double,
    val notes: String = ""
)
