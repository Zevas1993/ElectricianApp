package com.example.electricianapp.di

import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.model.materials.MaterialInventory
import com.example.electricianapp.domain.model.materials.MaterialTransaction
import com.example.electricianapp.domain.model.materials.UnitOfMeasure
import com.example.electricianapp.domain.usecase.materials.GetAllInventoryItemsUseCase
import com.example.electricianapp.domain.usecase.materials.GetInventoryItemByIdUseCase
import com.example.electricianapp.domain.usecase.materials.GetLowStockInventoryItemsUseCase
import com.example.electricianapp.domain.usecase.materials.GetTransactionHistoryUseCase
import com.example.electricianapp.domain.usecase.materials.SaveInventoryItemUseCase
import com.example.electricianapp.domain.usecase.materials.SaveTransactionUseCase
import com.example.electricianapp.domain.usecase.materials.SearchInventoryItemsUseCase
import com.example.electricianapp.domain.usecase.voltagedrop.CalculateVoltageDropUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date
import javax.inject.Singleton

/**
 * Test module for providing mock dependencies for UI tests
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    // Sample test data
    private val testMaterial = Material(
        id = "material1",
        name = "Test Material",
        description = "Test Description",
        code = "TM001",
        unitOfMeasure = UnitOfMeasure.EACH
    )

    private val testInventory = MaterialInventory(
        id = "inventory1",
        materialId = "material1",
        material = testMaterial,
        quantity = 100.0,
        minimumQuantity = 20.0,
        location = "Test Location",
        notes = "Test Notes",
        lastUpdated = Date()
    )

    private val testTransaction = MaterialTransaction(
        id = "transaction1",
        materialId = "material1",
        quantity = 50.0,
        transactionType = com.example.electricianapp.domain.model.materials.TransactionType.PURCHASE,
        date = Date(),
        notes = "Test Transaction"
    )

    @Provides
    @Singleton
    fun provideCalculateVoltageDropUseCase(): CalculateVoltageDropUseCase {
        return CalculateVoltageDropUseCase()
    }

    @Provides
    @Singleton
    fun provideGetAllInventoryItemsUseCase(): GetAllInventoryItemsUseCase {
        return object : GetAllInventoryItemsUseCase {
            override fun invoke(): Flow<List<MaterialInventory>> {
                return flowOf(listOf(testInventory))
            }
        }
    }

    @Provides
    @Singleton
    fun provideGetLowStockInventoryItemsUseCase(): GetLowStockInventoryItemsUseCase {
        return object : GetLowStockInventoryItemsUseCase {
            override fun invoke(): Flow<List<MaterialInventory>> {
                return flowOf(listOf(testInventory.copy(quantity = 10.0))) // Below minimum
            }
        }
    }

    @Provides
    @Singleton
    fun provideGetInventoryItemByIdUseCase(): GetInventoryItemByIdUseCase {
        return object : GetInventoryItemByIdUseCase {
            override suspend fun invoke(id: String): MaterialInventory? {
                return if (id == "inventory1") testInventory else null
            }
        }
    }

    @Provides
    @Singleton
    fun provideGetTransactionHistoryUseCase(): GetTransactionHistoryUseCase {
        return object : GetTransactionHistoryUseCase {
            override fun invoke(materialId: String): Flow<List<MaterialTransaction>> {
                return flowOf(listOf(testTransaction))
            }
        }
    }

    @Provides
    @Singleton
    fun provideSaveInventoryItemUseCase(): SaveInventoryItemUseCase {
        return object : SaveInventoryItemUseCase {
            override suspend fun invoke(inventory: MaterialInventory) {
                // Do nothing in test
            }
        }
    }

    @Provides
    @Singleton
    fun provideSaveTransactionUseCase(): SaveTransactionUseCase {
        return object : SaveTransactionUseCase {
            override suspend fun invoke(transaction: MaterialTransaction) {
                // Do nothing in test
            }
        }
    }

    @Provides
    @Singleton
    fun provideSearchInventoryItemsUseCase(): SearchInventoryItemsUseCase {
        return object : SearchInventoryItemsUseCase {
            override fun invoke(query: String): Flow<List<MaterialInventory>> {
                return flowOf(
                    if (query.contains("test", ignoreCase = true)) {
                        listOf(testInventory)
                    } else {
                        emptyList()
                    }
                )
            }
        }
    }
}
