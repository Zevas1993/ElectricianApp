package com.example.electricianapp.domain.repository.materials

import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.model.materials.MaterialCategory
import com.example.electricianapp.domain.model.materials.MaterialInventory
import com.example.electricianapp.domain.model.materials.MaterialList
import com.example.electricianapp.domain.model.materials.MaterialPackage
import com.example.electricianapp.domain.model.materials.MaterialQuote
import com.example.electricianapp.domain.model.materials.MaterialTransaction
import com.example.electricianapp.domain.model.materials.Supplier
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for material management operations
 */
interface MaterialRepository {
    
    // Material operations
    suspend fun saveMaterial(material: Material): String
    suspend fun updateMaterial(material: Material)
    suspend fun deleteMaterial(materialId: String)
    suspend fun getMaterialById(materialId: String): Material?
    fun getAllMaterials(): Flow<List<Material>>
    fun getMaterialsByCategory(category: MaterialCategory): Flow<List<Material>>
    fun searchMaterials(query: String): Flow<List<Material>>
    
    // Material list operations
    suspend fun saveMaterialList(materialList: MaterialList): String
    suspend fun updateMaterialList(materialList: MaterialList)
    suspend fun deleteMaterialList(materialListId: String)
    suspend fun getMaterialListById(materialListId: String): MaterialList?
    fun getAllMaterialLists(): Flow<List<MaterialList>>
    fun getMaterialListsByJobId(jobId: String): Flow<List<MaterialList>>
    fun searchMaterialLists(query: String): Flow<List<MaterialList>>
    
    // Supplier operations
    suspend fun saveSupplier(supplier: Supplier): String
    suspend fun updateSupplier(supplier: Supplier)
    suspend fun deleteSupplier(supplierId: String)
    suspend fun getSupplierById(supplierId: String): Supplier?
    fun getAllSuppliers(): Flow<List<Supplier>>
    fun getPreferredSuppliers(): Flow<List<Supplier>>
    fun searchSuppliers(query: String): Flow<List<Supplier>>
    
    // Material quote operations
    suspend fun saveMaterialQuote(materialQuote: MaterialQuote): String
    suspend fun updateMaterialQuote(materialQuote: MaterialQuote)
    suspend fun deleteMaterialQuote(quoteId: String)
    suspend fun getMaterialQuoteById(quoteId: String): MaterialQuote?
    fun getMaterialQuotesByMaterialId(materialId: String): Flow<List<MaterialQuote>>
    fun getMaterialQuotesBySupplierId(supplierId: String): Flow<List<MaterialQuote>>
    
    // Inventory operations
    suspend fun saveInventoryItem(inventoryItem: MaterialInventory): String
    suspend fun updateInventoryItem(inventoryItem: MaterialInventory)
    suspend fun deleteInventoryItem(inventoryId: String)
    suspend fun getInventoryItemById(inventoryId: String): MaterialInventory?
    suspend fun getInventoryItemByMaterialId(materialId: String): MaterialInventory?
    fun getAllInventoryItems(): Flow<List<MaterialInventory>>
    fun getLowStockInventoryItems(): Flow<List<MaterialInventory>>
    
    // Transaction operations
    suspend fun saveTransaction(transaction: MaterialTransaction): String
    suspend fun getTransactionById(transactionId: String): MaterialTransaction?
    fun getTransactionsByMaterialId(materialId: String): Flow<List<MaterialTransaction>>
    fun getTransactionsByJobId(jobId: String): Flow<List<MaterialTransaction>>
    
    // Material package operations
    suspend fun saveMaterialPackage(materialPackage: MaterialPackage): String
    suspend fun updateMaterialPackage(materialPackage: MaterialPackage)
    suspend fun deleteMaterialPackage(packageId: String)
    suspend fun getMaterialPackageById(packageId: String): MaterialPackage?
    fun getAllMaterialPackages(): Flow<List<MaterialPackage>>
    fun searchMaterialPackages(query: String): Flow<List<MaterialPackage>>
    
    // Material list generation
    suspend fun generateMaterialListFromCalculation(calculationId: String, calculationType: String): MaterialList
    suspend fun generateMaterialListFromPackage(packageId: String, jobId: String? = null): MaterialList
}
