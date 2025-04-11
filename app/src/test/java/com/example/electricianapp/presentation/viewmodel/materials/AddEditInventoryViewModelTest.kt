package com.example.electricianapp.presentation.viewmodel.materials

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.model.materials.MaterialInventory
import com.example.electricianapp.domain.model.materials.UnitOfMeasure
import com.example.electricianapp.domain.usecase.materials.GetAllMaterialsUseCase
import com.example.electricianapp.domain.usecase.materials.GetInventoryItemByIdUseCase
import com.example.electricianapp.domain.usecase.materials.SaveInventoryItemUseCase
import com.example.electricianapp.domain.model.materials.MaterialCategory // Import Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.* // Use wildcard for coroutines test
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
class AddEditInventoryViewModelTest {

    // Rule for LiveData testing
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher() // Use StandardTestDispatcher

    // Mocks
    @Mock
    private lateinit var getAllMaterialsUseCase: GetAllMaterialsUseCase

    @Mock
    private lateinit var getInventoryItemByIdUseCase: GetInventoryItemByIdUseCase

    @Mock
    private lateinit var saveInventoryItemUseCase: SaveInventoryItemUseCase

    // Class under test
    private lateinit var viewModel: AddEditInventoryViewModel

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

    // Use TestScope with the StandardTestDispatcher
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Setup default mock responses
        // Use runTest for setting up suspend function mocks
        testScope.runTest {
            `when`(getAllMaterialsUseCase()).thenReturn(flowOf(listOf(testMaterial)))
            // Mock the suspend function correctly
            `when`(getInventoryItemByIdUseCase.invoke("inventory1")).thenReturn(testInventory)
            `when`(getInventoryItemByIdUseCase.invoke("invalid_id")).thenThrow(IllegalArgumentException("Inventory not found")) // Ensure error case is also mocked as suspend
        }

        // Initialize the view model
        viewModel = AddEditInventoryViewModel(
            getAllMaterialsUseCase,
            getInventoryItemByIdUseCase,
            saveInventoryItemUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // No cleanup needed for StandardTestDispatcher
    }

    @Test
    fun `loadMaterials should update materials LiveData`() = testScope.runTest { // Use runTest
        // Arrange
        val expectedMaterials = listOf(testMaterial)
        `when`(getAllMaterialsUseCase()).thenReturn(flowOf(expectedMaterials))

        // Act
        viewModel.loadMaterials()

        // Assert
        assertEquals(expectedMaterials, viewModel.materials.value)
        assertFalse(viewModel.isLoading.value!!)
        assertEquals("", viewModel.error.value!!)
    }

    @Test
    fun `loadInventoryItem should update selectedInventory LiveData`() = testScope.runTest { // Use runTest
        // Arrange
        `when`(getInventoryItemByIdUseCase("inventory1")).thenReturn(testInventory)

        // Act
        viewModel.loadInventoryItem("inventory1")

        // Assert
        assertEquals(testInventory, viewModel.selectedInventory.value)
        assertEquals(testMaterial, viewModel.selectedMaterial.value)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `selectMaterial should update selectedMaterial LiveData`() {
        // Act
        viewModel.selectMaterial(testMaterial)

        // Assert
        assertEquals(testMaterial, viewModel.selectedMaterial.value)
    }

    @Test
    fun `saveInventory should call saveInventoryItemUseCase`() = testScope.runTest { // Use runTest
        // Act
        viewModel.saveInventory(testInventory)

        // Assert
        verify(saveInventoryItemUseCase).invoke(testInventory)
        assertTrue(viewModel.saveSuccess.value!!)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `updateInventory should call saveInventoryItemUseCase`() = testScope.runTest { // Use runTest
        // Act
        viewModel.updateInventory(testInventory)

        // Assert
        verify(saveInventoryItemUseCase).invoke(testInventory)
        assertTrue(viewModel.saveSuccess.value!!)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `loadInventoryItem should handle error`() = testScope.runTest { // Use runTest
        // Arrange is now done in setUp

        // Act
        viewModel.loadInventoryItem("invalid_id")

        // Assert
        val errorMessage = "Inventory not found" // Define the expected error message
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isLoading.value!!)
    }
}
