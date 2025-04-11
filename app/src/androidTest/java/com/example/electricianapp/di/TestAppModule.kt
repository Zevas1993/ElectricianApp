package com.example.electricianapp.di

import com.example.electricianapp.domain.model.materials.* // Use wildcard for materials
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
        // code = "TM001", // Removed non-existent parameter
        category = MaterialCategory.MISCELLANEOUS, // Added required parameter
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

    // Removed fake UseCase providers causing compilation errors.
    // Real UseCases will be injected by default in @AndroidTest.
    // Use @BindValue in specific test classes if fakes are needed.
}
