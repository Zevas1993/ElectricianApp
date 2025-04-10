package com.example.electricianapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.electricianapp.data.local.entity.MaterialEntity
import com.example.electricianapp.data.local.entity.MaterialInventoryEntity
import com.example.electricianapp.data.local.entity.MaterialListEntity
import com.example.electricianapp.data.local.entity.MaterialListItemEntity
import com.example.electricianapp.data.local.entity.MaterialPackageEntity
import com.example.electricianapp.data.local.entity.MaterialPackageItemEntity
import com.example.electricianapp.data.local.entity.MaterialQuoteEntity
import com.example.electricianapp.data.local.entity.MaterialTransactionEntity
import com.example.electricianapp.data.local.entity.SupplierEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for material-related entities
 */
@Dao
interface MaterialDao {
    
    // Material operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)
    
    @Update
    suspend fun updateMaterial(material: MaterialEntity)
    
    @Delete
    suspend fun deleteMaterial(material: MaterialEntity)
    
    @Query("DELETE FROM materials WHERE id = :materialId")
    suspend fun deleteMaterialById(materialId: String)
    
    @Query("SELECT * FROM materials WHERE id = :materialId")
    suspend fun getMaterialById(materialId: String): MaterialEntity?
    
    @Query("SELECT * FROM materials ORDER BY name ASC")
    fun getAllMaterials(): Flow<List<MaterialEntity>>
    
    @Query("SELECT * FROM materials WHERE category = :category ORDER BY name ASC")
    fun getMaterialsByCategory(category: String): Flow<List<MaterialEntity>>
    
    @Query("SELECT * FROM materials WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR partNumber LIKE '%' || :query || '%' OR manufacturer LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchMaterials(query: String): Flow<List<MaterialEntity>>
    
    // Material list operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialList(materialList: MaterialListEntity)
    
    @Update
    suspend fun updateMaterialList(materialList: MaterialListEntity)
    
    @Delete
    suspend fun deleteMaterialList(materialList: MaterialListEntity)
    
    @Query("DELETE FROM material_lists WHERE id = :materialListId")
    suspend fun deleteMaterialListById(materialListId: String)
    
    @Query("SELECT * FROM material_lists WHERE id = :materialListId")
    suspend fun getMaterialListById(materialListId: String): MaterialListEntity?
    
    @Query("SELECT * FROM material_lists ORDER BY modifiedDate DESC")
    fun getAllMaterialLists(): Flow<List<MaterialListEntity>>
    
    @Query("SELECT * FROM material_lists WHERE jobId = :jobId ORDER BY modifiedDate DESC")
    fun getMaterialListsByJobId(jobId: String): Flow<List<MaterialListEntity>>
    
    @Query("SELECT * FROM material_lists WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY modifiedDate DESC")
    fun searchMaterialLists(query: String): Flow<List<MaterialListEntity>>
    
    // Material list item operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialListItem(item: MaterialListItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialListItems(items: List<MaterialListItemEntity>)
    
    @Update
    suspend fun updateMaterialListItem(item: MaterialListItemEntity)
    
    @Delete
    suspend fun deleteMaterialListItem(item: MaterialListItemEntity)
    
    @Query("DELETE FROM material_list_items WHERE materialListId = :materialListId")
    suspend fun deleteMaterialListItemsByListId(materialListId: String)
    
    @Query("SELECT * FROM material_list_items WHERE materialListId = :materialListId")
    suspend fun getMaterialListItemsByListId(materialListId: String): List<MaterialListItemEntity>
    
    // Supplier operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: SupplierEntity)
    
    @Update
    suspend fun updateSupplier(supplier: SupplierEntity)
    
    @Delete
    suspend fun deleteSupplier(supplier: SupplierEntity)
    
    @Query("DELETE FROM suppliers WHERE id = :supplierId")
    suspend fun deleteSupplierById(supplierId: String)
    
    @Query("SELECT * FROM suppliers WHERE id = :supplierId")
    suspend fun getSupplierById(supplierId: String): SupplierEntity?
    
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAllSuppliers(): Flow<List<SupplierEntity>>
    
    @Query("SELECT * FROM suppliers WHERE isPreferred = 1 ORDER BY name ASC")
    fun getPreferredSuppliers(): Flow<List<SupplierEntity>>
    
    @Query("SELECT * FROM suppliers WHERE name LIKE '%' || :query || '%' OR contactName LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchSuppliers(query: String): Flow<List<SupplierEntity>>
    
    // Material quote operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialQuote(quote: MaterialQuoteEntity)
    
    @Update
    suspend fun updateMaterialQuote(quote: MaterialQuoteEntity)
    
    @Delete
    suspend fun deleteMaterialQuote(quote: MaterialQuoteEntity)
    
    @Query("DELETE FROM material_quotes WHERE id = :quoteId")
    suspend fun deleteMaterialQuoteById(quoteId: String)
    
    @Query("SELECT * FROM material_quotes WHERE id = :quoteId")
    suspend fun getMaterialQuoteById(quoteId: String): MaterialQuoteEntity?
    
    @Query("SELECT * FROM material_quotes WHERE materialId = :materialId ORDER BY price ASC")
    fun getMaterialQuotesByMaterialId(materialId: String): Flow<List<MaterialQuoteEntity>>
    
    @Query("SELECT * FROM material_quotes WHERE supplierId = :supplierId ORDER BY quoteDate DESC")
    fun getMaterialQuotesBySupplierId(supplierId: String): Flow<List<MaterialQuoteEntity>>
    
    // Inventory operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: MaterialInventoryEntity)
    
    @Update
    suspend fun updateInventoryItem(item: MaterialInventoryEntity)
    
    @Delete
    suspend fun deleteInventoryItem(item: MaterialInventoryEntity)
    
    @Query("DELETE FROM material_inventory WHERE id = :inventoryId")
    suspend fun deleteInventoryItemById(inventoryId: String)
    
    @Query("SELECT * FROM material_inventory WHERE id = :inventoryId")
    suspend fun getInventoryItemById(inventoryId: String): MaterialInventoryEntity?
    
    @Query("SELECT * FROM material_inventory WHERE materialId = :materialId")
    suspend fun getInventoryItemByMaterialId(materialId: String): MaterialInventoryEntity?
    
    @Query("SELECT * FROM material_inventory ORDER BY materialId ASC")
    fun getAllInventoryItems(): Flow<List<MaterialInventoryEntity>>
    
    @Query("SELECT * FROM material_inventory WHERE quantity <= minimumQuantity ORDER BY (minimumQuantity - quantity) DESC")
    fun getLowStockInventoryItems(): Flow<List<MaterialInventoryEntity>>
    
    // Transaction operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: MaterialTransactionEntity)
    
    @Query("SELECT * FROM material_transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): MaterialTransactionEntity?
    
    @Query("SELECT * FROM material_transactions WHERE materialId = :materialId ORDER BY date DESC")
    fun getTransactionsByMaterialId(materialId: String): Flow<List<MaterialTransactionEntity>>
    
    @Query("SELECT * FROM material_transactions WHERE jobId = :jobId ORDER BY date DESC")
    fun getTransactionsByJobId(jobId: String): Flow<List<MaterialTransactionEntity>>
    
    // Material package operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialPackage(materialPackage: MaterialPackageEntity)
    
    @Update
    suspend fun updateMaterialPackage(materialPackage: MaterialPackageEntity)
    
    @Delete
    suspend fun deleteMaterialPackage(materialPackage: MaterialPackageEntity)
    
    @Query("DELETE FROM material_packages WHERE id = :packageId")
    suspend fun deleteMaterialPackageById(packageId: String)
    
    @Query("SELECT * FROM material_packages WHERE id = :packageId")
    suspend fun getMaterialPackageById(packageId: String): MaterialPackageEntity?
    
    @Query("SELECT * FROM material_packages ORDER BY name ASC")
    fun getAllMaterialPackages(): Flow<List<MaterialPackageEntity>>
    
    @Query("SELECT * FROM material_packages WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchMaterialPackages(query: String): Flow<List<MaterialPackageEntity>>
    
    // Material package item operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialPackageItem(item: MaterialPackageItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialPackageItems(items: List<MaterialPackageItemEntity>)
    
    @Update
    suspend fun updateMaterialPackageItem(item: MaterialPackageItemEntity)
    
    @Delete
    suspend fun deleteMaterialPackageItem(item: MaterialPackageItemEntity)
    
    @Query("DELETE FROM material_package_items WHERE packageId = :packageId")
    suspend fun deleteMaterialPackageItemsByPackageId(packageId: String)
    
    @Query("SELECT * FROM material_package_items WHERE packageId = :packageId")
    suspend fun getMaterialPackageItemsByPackageId(packageId: String): List<MaterialPackageItemEntity>
    
    // Transaction methods
    
    @Transaction
    suspend fun insertMaterialListWithItems(materialList: MaterialListEntity, items: List<MaterialListItemEntity>) {
        insertMaterialList(materialList)
        insertMaterialListItems(items)
    }
    
    @Transaction
    suspend fun updateMaterialListWithItems(materialList: MaterialListEntity, items: List<MaterialListItemEntity>) {
        updateMaterialList(materialList)
        deleteMaterialListItemsByListId(materialList.id)
        insertMaterialListItems(items)
    }
    
    @Transaction
    suspend fun deleteMaterialListWithItems(materialListId: String) {
        deleteMaterialListItemsByListId(materialListId)
        deleteMaterialListById(materialListId)
    }
    
    @Transaction
    suspend fun insertMaterialPackageWithItems(materialPackage: MaterialPackageEntity, items: List<MaterialPackageItemEntity>) {
        insertMaterialPackage(materialPackage)
        insertMaterialPackageItems(items)
    }
    
    @Transaction
    suspend fun updateMaterialPackageWithItems(materialPackage: MaterialPackageEntity, items: List<MaterialPackageItemEntity>) {
        updateMaterialPackage(materialPackage)
        deleteMaterialPackageItemsByPackageId(materialPackage.id)
        insertMaterialPackageItems(items)
    }
    
    @Transaction
    suspend fun deleteMaterialPackageWithItems(packageId: String) {
        deleteMaterialPackageItemsByPackageId(packageId)
        deleteMaterialPackageById(packageId)
    }
}
