package com.example.electricianapp.presentation.viewmodel.materials

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.model.materials.MaterialInventory
import com.example.electricianapp.domain.model.materials.UnitOfMeasure
import com.example.electricianapp.domain.usecase.materials.GetAllMaterialsUseCase
import com.example.electricianapp.domain.usecase.materials.GetInventoryItemByIdUseCase
import com.example.electricianapp.domain.usecase.materials.SaveInventoryItemUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
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
    private val testDispatcher = TestCoroutineDispatcher()

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

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Setup default mock responses
        `when`(getAllMaterialsUseCase()).thenReturn(flowOf(listOf(testMaterial)))
        `when`(getInventoryItemByIdUseCase("inventory1")).thenReturn(testInventory)

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
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `loadMaterials should update materials LiveData`() = testDispatcher.runBlockingTest {
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
    fun `loadInventoryItem should update selectedInventory LiveData`() = testDispatcher.runBlockingTest {
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
    fun `saveInventory should call saveInventoryItemUseCase`() = testDispatcher.runBlockingTest {
        // Act
        viewModel.saveInventory(testInventory)

        // Assert
        verify(saveInventoryItemUseCase).invoke(testInventory)
        assertTrue(viewModel.saveSuccess.value!!)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `updateInventory should call saveInventoryItemUseCase`() = testDispatcher.runBlockingTest {
        // Act
        viewModel.updateInventory(testInventory)

        // Assert
        verify(saveInventoryItemUseCase).invoke(testInventory)
        assertTrue(viewModel.saveSuccess.value!!)
        assertFalse(viewModel.isLoading.value!!)
    }

    @Test
    fun `loadInventoryItem should handle error`() = testDispatcher.runBlockingTest {
        // Arrange
        val errorMessage = "Inventory not found"
        `when`(getInventoryItemByIdUseCase("invalid_id")).thenThrow(IllegalArgumentException(errorMessage))

        // Act
        viewModel.loadInventoryItem("invalid_id")

        // Assert
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isLoading.value!!)
    }
}
