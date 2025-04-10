package com.example.electricianapp.domain.usecase.materials

import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.model.materials.MaterialCategory
import com.example.electricianapp.domain.model.materials.MaterialInventory
import com.example.electricianapp.domain.model.materials.MaterialList
import com.example.electricianapp.domain.model.materials.MaterialPackage
import com.example.electricianapp.domain.model.materials.MaterialQuote
import com.example.electricianapp.domain.model.materials.MaterialTransaction
import com.example.electricianapp.domain.model.materials.Supplier
import com.example.electricianapp.domain.model.materials.TransactionType
import com.example.electricianapp.domain.repository.materials.MaterialRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

// Material management use cases

class GetAllMaterialsUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(): Flow<List<Material>> {
        return repository.getAllMaterials()
    }
}

class GetMaterialsByCategoryUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(category: MaterialCategory): Flow<List<Material>> {
        return repository.getMaterialsByCategory(category)
    }
}

class SearchMaterialsUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(query: String): Flow<List<Material>> {
        return repository.searchMaterials(query)
    }
}

class GetMaterialByIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialId: String): Material? {
        return repository.getMaterialById(materialId)
    }
}

class SaveMaterialUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(material: Material): String {
        return repository.saveMaterial(material)
    }
}

class UpdateMaterialUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(material: Material) {
        repository.updateMaterial(material)
    }
}

class DeleteMaterialUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialId: String) {
        repository.deleteMaterial(materialId)
    }
}

// Material list use cases

class GetAllMaterialListsUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(): Flow<List<MaterialList>> {
        return repository.getAllMaterialLists()
    }
}

class GetMaterialListsByJobIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(jobId: String): Flow<List<MaterialList>> {
        return repository.getMaterialListsByJobId(jobId)
    }
}

class SearchMaterialListsUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(query: String): Flow<List<MaterialList>> {
        return repository.searchMaterialLists(query)
    }
}

class GetMaterialListByIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialListId: String): MaterialList? {
        return repository.getMaterialListById(materialListId)
    }
}

class SaveMaterialListUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialList: MaterialList): String {
        val updatedList = materialList.copy(modifiedDate = Date())
        return repository.saveMaterialList(updatedList)
    }
}

class UpdateMaterialListUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialList: MaterialList) {
        val updatedList = materialList.copy(modifiedDate = Date())
        repository.updateMaterialList(updatedList)
    }
}

class DeleteMaterialListUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialListId: String) {
        repository.deleteMaterialList(materialListId)
    }
}

class GenerateMaterialListFromCalculationUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(calculationId: String, calculationType: String): MaterialList {
        return repository.generateMaterialListFromCalculation(calculationId, calculationType)
    }
}

class GenerateMaterialListFromPackageUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(packageId: String, jobId: String? = null): MaterialList {
        return repository.generateMaterialListFromPackage(packageId, jobId)
    }
}

// Supplier use cases

class GetAllSuppliersUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(): Flow<List<Supplier>> {
        return repository.getAllSuppliers()
    }
}

class GetPreferredSuppliersUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(): Flow<List<Supplier>> {
        return repository.getPreferredSuppliers()
    }
}

class SearchSuppliersUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(query: String): Flow<List<Supplier>> {
        return repository.searchSuppliers(query)
    }
}

class GetSupplierByIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(supplierId: String): Supplier? {
        return repository.getSupplierById(supplierId)
    }
}

class SaveSupplierUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(supplier: Supplier): String {
        return repository.saveSupplier(supplier)
    }
}

class UpdateSupplierUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(supplier: Supplier) {
        repository.updateSupplier(supplier)
    }
}

class DeleteSupplierUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(supplierId: String) {
        repository.deleteSupplier(supplierId)
    }
}

// Material quote use cases

class GetMaterialQuotesByMaterialIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(materialId: String): Flow<List<MaterialQuote>> {
        return repository.getMaterialQuotesByMaterialId(materialId)
    }
}

class GetMaterialQuotesBySupplierIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(supplierId: String): Flow<List<MaterialQuote>> {
        return repository.getMaterialQuotesBySupplierId(supplierId)
    }
}

class GetMaterialQuoteByIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(quoteId: String): MaterialQuote? {
        return repository.getMaterialQuoteById(quoteId)
    }
}

class SaveMaterialQuoteUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialQuote: MaterialQuote): String {
        return repository.saveMaterialQuote(materialQuote)
    }
}

class UpdateMaterialQuoteUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialQuote: MaterialQuote) {
        repository.updateMaterialQuote(materialQuote)
    }
}

class DeleteMaterialQuoteUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(quoteId: String) {
        repository.deleteMaterialQuote(quoteId)
    }
}

// Inventory use cases

class GetAllInventoryItemsUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(): Flow<List<MaterialInventory>> {
        return repository.getAllInventoryItems()
    }
}

class GetLowStockInventoryItemsUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(): Flow<List<MaterialInventory>> {
        return repository.getLowStockInventoryItems()
    }
}

class GetInventoryItemByIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(inventoryId: String): MaterialInventory? {
        return repository.getInventoryItemById(inventoryId)
    }
}

class GetInventoryItemByMaterialIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialId: String): MaterialInventory? {
        return repository.getInventoryItemByMaterialId(materialId)
    }
}

class SaveInventoryItemUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(inventoryItem: MaterialInventory): String {
        val updatedItem = inventoryItem.copy(lastUpdated = Date())
        return repository.saveInventoryItem(updatedItem)
    }
}

class UpdateInventoryItemUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(inventoryItem: MaterialInventory) {
        val updatedItem = inventoryItem.copy(lastUpdated = Date())
        repository.updateInventoryItem(updatedItem)
    }
}

class DeleteInventoryItemUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(inventoryId: String) {
        repository.deleteInventoryItem(inventoryId)
    }
}

// Transaction use cases

class RecordMaterialTransactionUseCase @Inject constructor(
    private val repository: MaterialRepository,
    private val getInventoryItemByMaterialIdUseCase: GetInventoryItemByMaterialIdUseCase,
    private val saveInventoryItemUseCase: SaveInventoryItemUseCase,
    private val updateInventoryItemUseCase: UpdateInventoryItemUseCase
) {
    suspend operator fun invoke(transaction: MaterialTransaction): String {
        // Update inventory based on transaction type
        val inventoryItem = getInventoryItemByMaterialIdUseCase(transaction.materialId)
        
        if (inventoryItem != null) {
            // Update existing inventory
            val newQuantity = when (transaction.transactionType) {
                TransactionType.PURCHASE -> inventoryItem.quantity + transaction.quantity
                TransactionType.USE -> inventoryItem.quantity - transaction.quantity
                TransactionType.RETURN_TO_INVENTORY -> inventoryItem.quantity + transaction.quantity
                TransactionType.RETURN_TO_SUPPLIER -> inventoryItem.quantity - transaction.quantity
                TransactionType.ADJUSTMENT -> transaction.quantity // Direct set
                TransactionType.TRANSFER -> inventoryItem.quantity // No change for transfer
            }
            
            val updatedInventory = inventoryItem.copy(
                quantity = newQuantity,
                lastUpdated = Date(),
                notes = if (transaction.notes.isNotEmpty()) 
                    "${inventoryItem.notes}\n${transaction.transactionType}: ${transaction.notes}" 
                else 
                    inventoryItem.notes
            )
            
            updateInventoryItemUseCase(updatedInventory)
        } else if (transaction.transactionType == TransactionType.PURCHASE || 
                   transaction.transactionType == TransactionType.ADJUSTMENT) {
            // Create new inventory item
            val newInventory = MaterialInventory(
                materialId = transaction.materialId,
                quantity = transaction.quantity,
                lastUpdated = Date(),
                notes = "Created from ${transaction.transactionType}: ${transaction.notes}"
            )
            
            saveInventoryItemUseCase(newInventory)
        }
        
        // Save the transaction
        return repository.saveTransaction(transaction)
    }
}

class GetTransactionsByMaterialIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(materialId: String): Flow<List<MaterialTransaction>> {
        return repository.getTransactionsByMaterialId(materialId)
    }
}

class GetTransactionsByJobIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(jobId: String): Flow<List<MaterialTransaction>> {
        return repository.getTransactionsByJobId(jobId)
    }
}

class GetTransactionByIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(transactionId: String): MaterialTransaction? {
        return repository.getTransactionById(transactionId)
    }
}

// Material package use cases

class GetAllMaterialPackagesUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(): Flow<List<MaterialPackage>> {
        return repository.getAllMaterialPackages()
    }
}

class SearchMaterialPackagesUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(query: String): Flow<List<MaterialPackage>> {
        return repository.searchMaterialPackages(query)
    }
}

class GetMaterialPackageByIdUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(packageId: String): MaterialPackage? {
        return repository.getMaterialPackageById(packageId)
    }
}

class SaveMaterialPackageUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialPackage: MaterialPackage): String {
        return repository.saveMaterialPackage(materialPackage)
    }
}

class UpdateMaterialPackageUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(materialPackage: MaterialPackage) {
        repository.updateMaterialPackage(materialPackage)
    }
}

class DeleteMaterialPackageUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(packageId: String) {
        repository.deleteMaterialPackage(packageId)
    }
}
