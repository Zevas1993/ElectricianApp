package com.example.electricianapp.presentation.viewmodel.materials

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.electricianapp.domain.model.materials.* // Use wildcard
import com.example.electricianapp.domain.usecase.materials.GetAllInventoryItemsUseCase
import com.example.electricianapp.domain.usecase.materials.GetInventoryItemByIdUseCase
import com.example.electricianapp.domain.usecase.materials.GetLowStockInventoryItemsUseCase
import com.example.electricianapp.domain.usecase.materials.GetTransactionHistoryUseCase
import com.example.electricianapp.domain.usecase.materials.SaveInventoryItemUseCase
import com.example.electricianapp.domain.usecase.materials.SaveTransactionUseCase
import com.example.electricianapp.domain.usecase.materials.SearchInventoryItemsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.* // Use wildcard
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.Date

@ExperimentalCoroutinesApi
class MaterialInventoryViewModelTest {

    // Rule for LiveData testing
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher() // Use StandardTestDispatcher

    // Mocks
    @Mock
    private lateinit var getAllInventoryItemsUseCase: GetAllInventoryItemsUseCase

    @Mock
    private lateinit var getLowStockInventoryItemsUseCase: GetLowStockInventoryItemsUseCase

    @Mock
    private lateinit var getInventoryItemByIdUseCase: GetInventoryItemByIdUseCase

    @Mock
    private lateinit var getTransactionHistoryUseCase: GetTransactionHistoryUseCase

    @Mock
    private lateinit var saveInventoryItemUseCase: SaveInventoryItemUseCase

    @Mock
    private lateinit var saveTransactionUseCase: SaveTransactionUseCase

    @Mock
    private lateinit var searchInventoryItemsUseCase: SearchInventoryItemsUseCase

    // Class under test
    private lateinit var viewModel: MaterialInventoryViewModel

    // Test data
    private val testMaterial = Material(
        id = "material1",
        name = "Test Material",
        description = "Test Description",
        // code = "TM001", // Removed
        category = MaterialCategory.MISCELLANEOUS, // Added
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
        transactionType = TransactionType.PURCHASE,
        date = Date(),
        notes = "Test Transaction"
    )

    // Use TestScope with the StandardTestDispatcher
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Setup default mock responses
        // Use runTest for setting up suspend function mocks
        testScope.runTest {
            `when`(getAllInventoryItemsUseCase()).thenReturn(flowOf(listOf(testInventory)))
            `when`(getLowStockInventoryItemsUseCase()).thenReturn(flowOf(emptyList()))
            // Mock the suspend function correctly
            `when`(getInventoryItemByIdUseCase.invoke("inventory1")).thenReturn(testInventory)
            `when`(getTransactionHistoryUseCase("material1")).thenReturn(flowOf(listOf(testTransaction)))
        }

        // Initialize the view model
        viewModel = MaterialInventoryViewModel(
            getAllInventoryItemsUseCase,
            getLowStockInventoryItemsUseCase,
            getInventoryItemByIdUseCase,
            getTransactionHistoryUseCase,
            saveInventoryItemUseCase,
            saveTransactionUseCase,
            searchInventoryItemsUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // No cleanup needed for StandardTestDispatcher
    }

    @Test
    fun `loadInventory should update inventory LiveData`() = testScope.runTest { // Use runTest
        // Arrange
        val expectedInventory = listOf(testInventory)
        `when`(getAllInventoryItemsUseCase()).thenReturn(flowOf(expectedInventory))

        // Act
        viewModel.loadInventory()

        // Assert
        assertEquals(expectedInventory, viewModel.inventory.value)
        assertFalse(viewModel.isLoading.value!!)
        assertEquals("", viewModel.error.value!!)
    }

    @Test
    fun `loadInventoryItem should update selectedInventory LiveData`() = testScope.runTest { // Use runTest
        // Arrange is now done in setUp

        // Act
        viewModel.loadInventoryItem("inventory1")

        // Assert
        assertEquals(testInventory, viewModel.selectedInventory.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `loadTransactionHistory should update transactionHistory LiveData`() = testScope.runTest { // Use runTest
        // Arrange
        val expectedTransactions = listOf(testTransaction)
        // `getInventoryItemByIdUseCase` mock is already set up
        `when`(getTransactionHistoryUseCase("material1")).thenReturn(flowOf(expectedTransactions))

        // Act
        viewModel.loadTransactionHistory("inventory1")

        // Assert
        assertEquals(expectedTransactions, viewModel.transactionHistory.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `showLowStockItems should update inventory with low stock items`() = testScope.runTest { // Use runTest
        // Arrange
        val lowStockInventory = testInventory.copy(quantity = 10.0) // Below minimum of 20
        val expectedLowStockItems = listOf(lowStockInventory)
        `when`(getLowStockInventoryItemsUseCase()).thenReturn(flowOf(expectedLowStockItems))

        // Act
        viewModel.showLowStockItems()

        // Assert
        assertEquals(expectedLowStockItems, viewModel.inventory.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `searchInventory should update inventory with search results`() = testScope.runTest { // Use runTest
        // Arrange
        val searchQuery = "Test"
        val expectedSearchResults = listOf(testInventory)
        `when`(searchInventoryItemsUseCase(searchQuery)).thenReturn(flowOf(expectedSearchResults))

        // Act
        viewModel.searchInventory(searchQuery)

        // Assert
        assertEquals(expectedSearchResults, viewModel.inventory.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `adjustInventory should update inventory and create transaction`() = testScope.runTest { // Use runTest
        // Arrange
        val inventoryId = "inventory1"
        val quantity = 50.0
        val notes = "Test adjustment"
        val transactionType = TransactionType.PURCHASE

        // Act
        viewModel.adjustInventory(inventoryId, quantity, notes, transactionType)

        // Assert
        verify(saveTransactionUseCase).invoke(org.mockito.kotlin.any())
        verify(saveInventoryItemUseCase).invoke(org.mockito.kotlin.any())
        assertTrue(viewModel.transactionSuccess.value!!)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `adjustInventory should handle different transaction types correctly`() = testScope.runTest { // Use runTest
        // Arrange - Purchase (add)
        val initialQuantity = 100.0
        val adjustmentQuantity = 50.0
        val inventoryId = "inventory1"
        val inventory = testInventory.copy(quantity = initialQuantity)
        // Mock the suspend function call for this specific test case
        `when`(getInventoryItemByIdUseCase.invoke(inventoryId)).thenReturn(inventory)

        // Act - Purchase
        viewModel.adjustInventory(inventoryId, adjustmentQuantity, "Purchase", TransactionType.PURCHASE)

        // Assert - Quantity should increase
        verify(saveInventoryItemUseCase).invoke(org.mockito.kotlin.check {
            assertEquals(initialQuantity + adjustmentQuantity, it.quantity, 0.01)
        })

        // Arrange - Use (subtract)
        // Mock the suspend function call again for this part of the test
        `when`(getInventoryItemByIdUseCase.invoke(inventoryId)).thenReturn(inventory)

        // Act - Use
        viewModel.adjustInventory(inventoryId, adjustmentQuantity, "Use", TransactionType.USE)

        // Assert - Quantity should decrease
        verify(saveInventoryItemUseCase).invoke(org.mockito.kotlin.check {
            assertEquals(initialQuantity - adjustmentQuantity, it.quantity, 0.01)
        })
    }
}
