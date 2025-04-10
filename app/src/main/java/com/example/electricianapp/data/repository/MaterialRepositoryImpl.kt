package com.example.electricianapp.data.repository

import com.example.electricianapp.data.local.dao.MaterialDao
import com.example.electricianapp.data.local.entity.MaterialEntity
import com.example.electricianapp.data.local.entity.MaterialInventoryEntity
import com.example.electricianapp.data.local.entity.MaterialListEntity
import com.example.electricianapp.data.local.entity.MaterialListItemEntity
import com.example.electricianapp.data.local.entity.MaterialPackageEntity
import com.example.electricianapp.data.local.entity.MaterialPackageItemEntity
import com.example.electricianapp.data.local.entity.MaterialQuoteEntity
import com.example.electricianapp.data.local.entity.MaterialTransactionEntity
import com.example.electricianapp.data.local.entity.SupplierEntity
import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.model.materials.MaterialCategory
import com.example.electricianapp.domain.model.materials.MaterialInventory
import com.example.electricianapp.domain.model.materials.MaterialItemStatus
import com.example.electricianapp.domain.model.materials.MaterialList
import com.example.electricianapp.domain.model.materials.MaterialListItem
import com.example.electricianapp.domain.model.materials.MaterialListStatus
import com.example.electricianapp.domain.model.materials.MaterialPackage
import com.example.electricianapp.domain.model.materials.MaterialPackageItem
import com.example.electricianapp.domain.model.materials.MaterialQuote
import com.example.electricianapp.domain.model.materials.MaterialTransaction
import com.example.electricianapp.domain.model.materials.Supplier
import com.example.electricianapp.domain.model.materials.TransactionType
import com.example.electricianapp.domain.model.materials.UnitOfMeasure
import com.example.electricianapp.domain.repository.materials.MaterialRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaterialRepositoryImpl @Inject constructor(
    private val materialDao: MaterialDao,
    private val gson: Gson
) : MaterialRepository {
    
    // Mappers
    
    private fun mapMaterialEntityToDomain(entity: MaterialEntity): Material {
        val tagsType = object : TypeToken<List<String>>() {}.type
        val tags: List<String> = gson.fromJson(entity.tagsJson, tagsType) ?: emptyList()
        
        return Material(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            category = MaterialCategory.valueOf(entity.category),
            partNumber = entity.partNumber,
            manufacturer = entity.manufacturer,
            unitOfMeasure = UnitOfMeasure.valueOf(entity.unitOfMeasure),
            unitPrice = entity.unitPrice,
            imageUrl = entity.imageUrl,
            tags = tags
        )
    }
    
    private fun mapMaterialDomainToEntity(domain: Material): MaterialEntity {
        val tagsJson = gson.toJson(domain.tags)
        
        return MaterialEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            category = domain.category.name,
            partNumber = domain.partNumber,
            manufacturer = domain.manufacturer,
            unitOfMeasure = domain.unitOfMeasure.name,
            unitPrice = domain.unitPrice,
            imageUrl = domain.imageUrl,
            tagsJson = tagsJson
        )
    }
    
    private fun mapMaterialListEntityToDomain(
        entity: MaterialListEntity,
        items: List<MaterialListItemEntity>,
        materials: Map<String, Material>
    ): MaterialList {
        val tagsType = object : TypeToken<List<String>>() {}.type
        val tags: List<String> = gson.fromJson(entity.tagsJson, tagsType) ?: emptyList()
        
        val materialListItems = items.map { itemEntity ->
            MaterialListItem(
                id = itemEntity.id,
                materialId = itemEntity.materialId,
                material = materials[itemEntity.materialId],
                quantity = itemEntity.quantity,
                notes = itemEntity.notes,
                status = MaterialItemStatus.valueOf(itemEntity.status),
                allocatedQuantity = itemEntity.allocatedQuantity,
                usedQuantity = itemEntity.usedQuantity
            )
        }
        
        return MaterialList(
            id = entity.id,
            jobId = entity.jobId,
            name = entity.name,
            description = entity.description,
            createdDate = Date(entity.createdDate),
            modifiedDate = Date(entity.modifiedDate),
            items = materialListItems,
            status = MaterialListStatus.valueOf(entity.status),
            tags = tags
        )
    }
    
    private fun mapMaterialListDomainToEntity(domain: MaterialList): MaterialListEntity {
        val tagsJson = gson.toJson(domain.tags)
        
        return MaterialListEntity(
            id = domain.id,
            jobId = domain.jobId,
            name = domain.name,
            description = domain.description,
            createdDate = domain.createdDate.time,
            modifiedDate = domain.modifiedDate.time,
            status = domain.status.name,
            tagsJson = tagsJson
        )
    }
    
    private fun mapMaterialListItemDomainToEntity(domain: MaterialListItem, materialListId: String): MaterialListItemEntity {
        return MaterialListItemEntity(
            id = domain.id,
            materialListId = materialListId,
            materialId = domain.materialId,
            quantity = domain.quantity,
            notes = domain.notes,
            status = domain.status.name,
            allocatedQuantity = domain.allocatedQuantity,
            usedQuantity = domain.usedQuantity
        )
    }
    
    private fun mapSupplierEntityToDomain(entity: SupplierEntity): Supplier {
        val categoriesType = object : TypeToken<List<String>>() {}.type
        val categoryStrings: List<String> = gson.fromJson(entity.preferredCategoriesJson, categoriesType) ?: emptyList()
        val categories = categoryStrings.map { MaterialCategory.valueOf(it) }
        
        return Supplier(
            id = entity.id,
            name = entity.name,
            contactName = entity.contactName,
            phone = entity.phone,
            email = entity.email,
            website = entity.website,
            address = entity.address,
            notes = entity.notes,
            preferredForCategories = categories,
            isPreferred = entity.isPreferred
        )
    }
    
    private fun mapSupplierDomainToEntity(domain: Supplier): SupplierEntity {
        val categoryStrings = domain.preferredForCategories.map { it.name }
        val categoriesJson = gson.toJson(categoryStrings)
        
        return SupplierEntity(
            id = domain.id,
            name = domain.name,
            contactName = domain.contactName,
            phone = domain.phone,
            email = domain.email,
            website = domain.website,
            address = domain.address,
            notes = domain.notes,
            preferredCategoriesJson = categoriesJson,
            isPreferred = domain.isPreferred
        )
    }
    
    private fun mapMaterialQuoteEntityToDomain(entity: MaterialQuoteEntity): MaterialQuote {
        return MaterialQuote(
            id = entity.id,
            materialId = entity.materialId,
            supplierId = entity.supplierId,
            price = entity.price,
            minimumQuantity = entity.minimumQuantity,
            availableQuantity = entity.availableQuantity,
            leadTimeInDays = entity.leadTimeInDays,
            quoteDate = Date(entity.quoteDate),
            expirationDate = entity.expirationDate?.let { Date(it) },
            notes = entity.notes
        )
    }
    
    private fun mapMaterialQuoteDomainToEntity(domain: MaterialQuote): MaterialQuoteEntity {
        return MaterialQuoteEntity(
            id = domain.id,
            materialId = domain.materialId,
            supplierId = domain.supplierId,
            price = domain.price,
            minimumQuantity = domain.minimumQuantity,
            availableQuantity = domain.availableQuantity,
            leadTimeInDays = domain.leadTimeInDays,
            quoteDate = domain.quoteDate.time,
            expirationDate = domain.expirationDate?.time,
            notes = domain.notes
        )
    }
    
    private fun mapInventoryEntityToDomain(entity: MaterialInventoryEntity, material: Material? = null): MaterialInventory {
        return MaterialInventory(
            id = entity.id,
            materialId = entity.materialId,
            material = material,
            quantity = entity.quantity,
            location = entity.location,
            lastUpdated = Date(entity.lastUpdated),
            minimumQuantity = entity.minimumQuantity,
            notes = entity.notes
        )
    }
    
    private fun mapInventoryDomainToEntity(domain: MaterialInventory): MaterialInventoryEntity {
        return MaterialInventoryEntity(
            id = domain.id,
            materialId = domain.materialId,
            quantity = domain.quantity,
            location = domain.location,
            lastUpdated = domain.lastUpdated.time,
            minimumQuantity = domain.minimumQuantity,
            notes = domain.notes
        )
    }
    
    private fun mapTransactionEntityToDomain(entity: MaterialTransactionEntity): MaterialTransaction {
        return MaterialTransaction(
            id = entity.id,
            materialId = entity.materialId,
            jobId = entity.jobId,
            materialListId = entity.materialListId,
            quantity = entity.quantity,
            transactionType = TransactionType.valueOf(entity.transactionType),
            date = Date(entity.date),
            performedBy = entity.performedBy,
            notes = entity.notes
        )
    }
    
    private fun mapTransactionDomainToEntity(domain: MaterialTransaction): MaterialTransactionEntity {
        return MaterialTransactionEntity(
            id = domain.id,
            materialId = domain.materialId,
            jobId = domain.jobId,
            materialListId = domain.materialListId,
            quantity = domain.quantity,
            transactionType = domain.transactionType.name,
            date = domain.date.time,
            performedBy = domain.performedBy,
            notes = domain.notes
        )
    }
    
    private fun mapPackageEntityToDomain(
        entity: MaterialPackageEntity,
        items: List<MaterialPackageItemEntity>,
        materials: Map<String, Material>
    ): MaterialPackage {
        val tagsType = object : TypeToken<List<String>>() {}.type
        val tags: List<String> = gson.fromJson(entity.tagsJson, tagsType) ?: emptyList()
        
        val packageItems = items.map { itemEntity ->
            MaterialPackageItem(
                id = itemEntity.id,
                materialId = itemEntity.materialId,
                material = materials[itemEntity.materialId],
                quantity = itemEntity.quantity,
                notes = itemEntity.notes
            )
        }
        
        return MaterialPackage(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            category = entity.category,
            items = packageItems,
            tags = tags
        )
    }
    
    private fun mapPackageDomainToEntity(domain: MaterialPackage): MaterialPackageEntity {
        val tagsJson = gson.toJson(domain.tags)
        
        return MaterialPackageEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            category = domain.category,
            tagsJson = tagsJson
        )
    }
    
    private fun mapPackageItemDomainToEntity(domain: MaterialPackageItem, packageId: String): MaterialPackageItemEntity {
        return MaterialPackageItemEntity(
            id = domain.id,
            packageId = packageId,
            materialId = domain.materialId,
            quantity = domain.quantity,
            notes = domain.notes
        )
    }
    
    // Repository implementation
    
    override suspend fun saveMaterial(material: Material): String {
        val entity = mapMaterialDomainToEntity(material)
        materialDao.insertMaterial(entity)
        return material.id
    }
    
    override suspend fun updateMaterial(material: Material) {
        val entity = mapMaterialDomainToEntity(material)
        materialDao.updateMaterial(entity)
    }
    
    override suspend fun deleteMaterial(materialId: String) {
        materialDao.deleteMaterialById(materialId)
    }
    
    override suspend fun getMaterialById(materialId: String): Material? {
        val entity = materialDao.getMaterialById(materialId) ?: return null
        return mapMaterialEntityToDomain(entity)
    }
    
    override fun getAllMaterials(): Flow<List<Material>> {
        return materialDao.getAllMaterials().map { entities ->
            entities.map { mapMaterialEntityToDomain(it) }
        }
    }
    
    override fun getMaterialsByCategory(category: MaterialCategory): Flow<List<Material>> {
        return materialDao.getMaterialsByCategory(category.name).map { entities ->
            entities.map { mapMaterialEntityToDomain(it) }
        }
    }
    
    override fun searchMaterials(query: String): Flow<List<Material>> {
        return materialDao.searchMaterials(query).map { entities ->
            entities.map { mapMaterialEntityToDomain(it) }
        }
    }
    
    override suspend fun saveMaterialList(materialList: MaterialList): String {
        val listEntity = mapMaterialListDomainToEntity(materialList)
        val itemEntities = materialList.items.map { mapMaterialListItemDomainToEntity(it, materialList.id) }
        materialDao.insertMaterialListWithItems(listEntity, itemEntities)
        return materialList.id
    }
    
    override suspend fun updateMaterialList(materialList: MaterialList) {
        val listEntity = mapMaterialListDomainToEntity(materialList)
        val itemEntities = materialList.items.map { mapMaterialListItemDomainToEntity(it, materialList.id) }
        materialDao.updateMaterialListWithItems(listEntity, itemEntities)
    }
    
    override suspend fun deleteMaterialList(materialListId: String) {
        materialDao.deleteMaterialListWithItems(materialListId)
    }
    
    override suspend fun getMaterialListById(materialListId: String): MaterialList? {
        val listEntity = materialDao.getMaterialListById(materialListId) ?: return null
        val itemEntities = materialDao.getMaterialListItemsByListId(materialListId)
        
        // Get all materials needed for this list
        val materialIds = itemEntities.map { it.materialId }.distinct()
        val materials = materialIds.mapNotNull { materialId ->
            materialDao.getMaterialById(materialId)?.let { entity ->
                mapMaterialEntityToDomain(entity)
            }
        }.associateBy { it.id }
        
        return mapMaterialListEntityToDomain(listEntity, itemEntities, materials)
    }
    
    override fun getAllMaterialLists(): Flow<List<MaterialList>> {
        return materialDao.getAllMaterialLists().map { listEntities ->
            // This is not efficient for large datasets, but works for demo purposes
            // In a real app, we would use Room's @Relation or a more efficient query strategy
            listEntities.map { listEntity ->
                val itemEntities = materialDao.getMaterialListItemsByListId(listEntity.id)
                
                // Get all materials needed for this list
                val materialIds = itemEntities.map { it.materialId }.distinct()
                val materials = materialIds.mapNotNull { materialId ->
                    materialDao.getMaterialById(materialId)?.let { entity ->
                        mapMaterialEntityToDomain(entity)
                    }
                }.associateBy { it.id }
                
                mapMaterialListEntityToDomain(listEntity, itemEntities, materials)
            }
        }
    }
    
    override fun getMaterialListsByJobId(jobId: String): Flow<List<MaterialList>> {
        return materialDao.getMaterialListsByJobId(jobId).map { listEntities ->
            listEntities.map { listEntity ->
                val itemEntities = materialDao.getMaterialListItemsByListId(listEntity.id)
                
                // Get all materials needed for this list
                val materialIds = itemEntities.map { it.materialId }.distinct()
                val materials = materialIds.mapNotNull { materialId ->
                    materialDao.getMaterialById(materialId)?.let { entity ->
                        mapMaterialEntityToDomain(entity)
                    }
                }.associateBy { it.id }
                
                mapMaterialListEntityToDomain(listEntity, itemEntities, materials)
            }
        }
    }
    
    override fun searchMaterialLists(query: String): Flow<List<MaterialList>> {
        return materialDao.searchMaterialLists(query).map { listEntities ->
            listEntities.map { listEntity ->
                val itemEntities = materialDao.getMaterialListItemsByListId(listEntity.id)
                
                // Get all materials needed for this list
                val materialIds = itemEntities.map { it.materialId }.distinct()
                val materials = materialIds.mapNotNull { materialId ->
                    materialDao.getMaterialById(materialId)?.let { entity ->
                        mapMaterialEntityToDomain(entity)
                    }
                }.associateBy { it.id }
                
                mapMaterialListEntityToDomain(listEntity, itemEntities, materials)
            }
        }
    }
    
    override suspend fun saveSupplier(supplier: Supplier): String {
        val entity = mapSupplierDomainToEntity(supplier)
        materialDao.insertSupplier(entity)
        return supplier.id
    }
    
    override suspend fun updateSupplier(supplier: Supplier) {
        val entity = mapSupplierDomainToEntity(supplier)
        materialDao.updateSupplier(entity)
    }
    
    override suspend fun deleteSupplier(supplierId: String) {
        materialDao.deleteSupplierById(supplierId)
    }
    
    override suspend fun getSupplierById(supplierId: String): Supplier? {
        val entity = materialDao.getSupplierById(supplierId) ?: return null
        return mapSupplierEntityToDomain(entity)
    }
    
    override fun getAllSuppliers(): Flow<List<Supplier>> {
        return materialDao.getAllSuppliers().map { entities ->
            entities.map { mapSupplierEntityToDomain(it) }
        }
    }
    
    override fun getPreferredSuppliers(): Flow<List<Supplier>> {
        return materialDao.getPreferredSuppliers().map { entities ->
            entities.map { mapSupplierEntityToDomain(it) }
        }
    }
    
    override fun searchSuppliers(query: String): Flow<List<Supplier>> {
        return materialDao.searchSuppliers(query).map { entities ->
            entities.map { mapSupplierEntityToDomain(it) }
        }
    }
    
    override suspend fun saveMaterialQuote(materialQuote: MaterialQuote): String {
        val entity = mapMaterialQuoteDomainToEntity(materialQuote)
        materialDao.insertMaterialQuote(entity)
        return materialQuote.id
    }
    
    override suspend fun updateMaterialQuote(materialQuote: MaterialQuote) {
        val entity = mapMaterialQuoteDomainToEntity(materialQuote)
        materialDao.updateMaterialQuote(entity)
    }
    
    override suspend fun deleteMaterialQuote(quoteId: String) {
        materialDao.deleteMaterialQuoteById(quoteId)
    }
    
    override suspend fun getMaterialQuoteById(quoteId: String): MaterialQuote? {
        val entity = materialDao.getMaterialQuoteById(quoteId) ?: return null
        return mapMaterialQuoteEntityToDomain(entity)
    }
    
    override fun getMaterialQuotesByMaterialId(materialId: String): Flow<List<MaterialQuote>> {
        return materialDao.getMaterialQuotesByMaterialId(materialId).map { entities ->
            entities.map { mapMaterialQuoteEntityToDomain(it) }
        }
    }
    
    override fun getMaterialQuotesBySupplierId(supplierId: String): Flow<List<MaterialQuote>> {
        return materialDao.getMaterialQuotesBySupplierId(supplierId).map { entities ->
            entities.map { mapMaterialQuoteEntityToDomain(it) }
        }
    }
    
    override suspend fun saveInventoryItem(inventoryItem: MaterialInventory): String {
        val entity = mapInventoryDomainToEntity(inventoryItem)
        materialDao.insertInventoryItem(entity)
        return inventoryItem.id
    }
    
    override suspend fun updateInventoryItem(inventoryItem: MaterialInventory) {
        val entity = mapInventoryDomainToEntity(inventoryItem)
        materialDao.updateInventoryItem(entity)
    }
    
    override suspend fun deleteInventoryItem(inventoryId: String) {
        materialDao.deleteInventoryItemById(inventoryId)
    }
    
    override suspend fun getInventoryItemById(inventoryId: String): MaterialInventory? {
        val entity = materialDao.getInventoryItemById(inventoryId) ?: return null
        
        // Get the associated material if available
        val material = entity.materialId.let { materialId ->
            materialDao.getMaterialById(materialId)?.let { materialEntity ->
                mapMaterialEntityToDomain(materialEntity)
            }
        }
        
        return mapInventoryEntityToDomain(entity, material)
    }
    
    override suspend fun getInventoryItemByMaterialId(materialId: String): MaterialInventory? {
        val entity = materialDao.getInventoryItemByMaterialId(materialId) ?: return null
        
        // Get the associated material if available
        val material = materialDao.getMaterialById(materialId)?.let { materialEntity ->
            mapMaterialEntityToDomain(materialEntity)
        }
        
        return mapInventoryEntityToDomain(entity, material)
    }
    
    override fun getAllInventoryItems(): Flow<List<MaterialInventory>> {
        return materialDao.getAllInventoryItems().map { entities ->
            entities.map { entity ->
                // Get the associated material if available
                val material = entity.materialId.let { materialId ->
                    materialDao.getMaterialById(materialId)?.let { materialEntity ->
                        mapMaterialEntityToDomain(materialEntity)
                    }
                }
                
                mapInventoryEntityToDomain(entity, material)
            }
        }
    }
    
    override fun getLowStockInventoryItems(): Flow<List<MaterialInventory>> {
        return materialDao.getLowStockInventoryItems().map { entities ->
            entities.map { entity ->
                // Get the associated material if available
                val material = entity.materialId.let { materialId ->
                    materialDao.getMaterialById(materialId)?.let { materialEntity ->
                        mapMaterialEntityToDomain(materialEntity)
                    }
                }
                
                mapInventoryEntityToDomain(entity, material)
            }
        }
    }
    
    override suspend fun saveTransaction(transaction: MaterialTransaction): String {
        val entity = mapTransactionDomainToEntity(transaction)
        materialDao.insertTransaction(entity)
        return transaction.id
    }
    
    override suspend fun getTransactionById(transactionId: String): MaterialTransaction? {
        val entity = materialDao.getTransactionById(transactionId) ?: return null
        return mapTransactionEntityToDomain(entity)
    }
    
    override fun getTransactionsByMaterialId(materialId: String): Flow<List<MaterialTransaction>> {
        return materialDao.getTransactionsByMaterialId(materialId).map { entities ->
            entities.map { mapTransactionEntityToDomain(it) }
        }
    }
    
    override fun getTransactionsByJobId(jobId: String): Flow<List<MaterialTransaction>> {
        return materialDao.getTransactionsByJobId(jobId).map { entities ->
            entities.map { mapTransactionEntityToDomain(it) }
        }
    }
    
    override suspend fun saveMaterialPackage(materialPackage: MaterialPackage): String {
        val packageEntity = mapPackageDomainToEntity(materialPackage)
        val itemEntities = materialPackage.items.map { mapPackageItemDomainToEntity(it, materialPackage.id) }
        materialDao.insertMaterialPackageWithItems(packageEntity, itemEntities)
        return materialPackage.id
    }
    
    override suspend fun updateMaterialPackage(materialPackage: MaterialPackage) {
        val packageEntity = mapPackageDomainToEntity(materialPackage)
        val itemEntities = materialPackage.items.map { mapPackageItemDomainToEntity(it, materialPackage.id) }
        materialDao.updateMaterialPackageWithItems(packageEntity, itemEntities)
    }
    
    override suspend fun deleteMaterialPackage(packageId: String) {
        materialDao.deleteMaterialPackageWithItems(packageId)
    }
    
    override suspend fun getMaterialPackageById(packageId: String): MaterialPackage? {
        val packageEntity = materialDao.getMaterialPackageById(packageId) ?: return null
        val itemEntities = materialDao.getMaterialPackageItemsByPackageId(packageId)
        
        // Get all materials needed for this package
        val materialIds = itemEntities.map { it.materialId }.distinct()
        val materials = materialIds.mapNotNull { materialId ->
            materialDao.getMaterialById(materialId)?.let { entity ->
                mapMaterialEntityToDomain(entity)
            }
        }.associateBy { it.id }
        
        return mapPackageEntityToDomain(packageEntity, itemEntities, materials)
    }
    
    override fun getAllMaterialPackages(): Flow<List<MaterialPackage>> {
        return materialDao.getAllMaterialPackages().map { packageEntities ->
            packageEntities.map { packageEntity ->
                val itemEntities = materialDao.getMaterialPackageItemsByPackageId(packageEntity.id)
                
                // Get all materials needed for this package
                val materialIds = itemEntities.map { it.materialId }.distinct()
                val materials = materialIds.mapNotNull { materialId ->
                    materialDao.getMaterialById(materialId)?.let { entity ->
                        mapMaterialEntityToDomain(entity)
                    }
                }.associateBy { it.id }
                
                mapPackageEntityToDomain(packageEntity, itemEntities, materials)
            }
        }
    }
    
    override fun searchMaterialPackages(query: String): Flow<List<MaterialPackage>> {
        return materialDao.searchMaterialPackages(query).map { packageEntities ->
            packageEntities.map { packageEntity ->
                val itemEntities = materialDao.getMaterialPackageItemsByPackageId(packageEntity.id)
                
                // Get all materials needed for this package
                val materialIds = itemEntities.map { it.materialId }.distinct()
                val materials = materialIds.mapNotNull { materialId ->
                    materialDao.getMaterialById(materialId)?.let { entity ->
                        mapMaterialEntityToDomain(entity)
                    }
                }.associateBy { it.id }
                
                mapPackageEntityToDomain(packageEntity, itemEntities, materials)
            }
        }
    }
    
    override suspend fun generateMaterialListFromCalculation(calculationId: String, calculationType: String): MaterialList {
        // This would be implemented based on the specific calculation types
        // For now, return an empty material list
        return MaterialList(
            name = "Generated from $calculationType calculation",
            description = "Automatically generated from calculation ID: $calculationId",
            items = emptyList()
        )
    }
    
    override suspend fun generateMaterialListFromPackage(packageId: String, jobId: String?): MaterialList {
        val materialPackage = getMaterialPackageById(packageId) ?: throw IllegalArgumentException("Package not found")
        
        val materialListItems = materialPackage.items.map { packageItem ->
            MaterialListItem(
                materialId = packageItem.materialId,
                material = packageItem.material,
                quantity = packageItem.quantity,
                notes = packageItem.notes
            )
        }
        
        return MaterialList(
            name = "Generated from ${materialPackage.name}",
            description = "Automatically generated from package: ${materialPackage.name}",
            jobId = jobId,
            items = materialListItems
        )
    }
}
