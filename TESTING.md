# Testing Guide for ElectricianApp

This document provides guidance on testing the ElectricianApp, including unit tests, integration tests, and UI tests.

## Unit Tests

### Voltage Drop Calculator Tests

The Voltage Drop Calculator should be tested with various input combinations to ensure accurate calculations:

```kotlin
// Example test for single-phase copper circuit
@Test
fun calculateVoltageDropForSinglePhaseCopperCircuit() {
    // Arrange
    val input = VoltageDropInput(
        systemType = SystemType.SINGLE_PHASE,
        conductorType = ConductorType.COPPER,
        conduitMaterial = ConduitMaterial.PVC,
        temperatureRating = TemperatureRating.RATING_75C,
        wireSize = "12",
        lengthInFeet = 100.0,
        loadInAmps = 20.0,
        voltageInVolts = 120.0,
        powerFactor = 1.0
    )

    // Act
    val result = calculateVoltageDropUseCase(input)

    // Assert
    // Expected voltage drop: 20A * 1.93 ohms/1000ft * (100ft/1000) * 2 = 7.72V
    assertEquals(7.72, result.voltageDropInVolts, 0.1)
    assertEquals(6.43, result.voltageDropPercentage, 0.1)
    assertEquals(1.93, result.conductorResistance, 0.01)
    assertEquals(0.054, result.conductorReactance, 0.001)
    assertEquals(112.28, result.endVoltage, 0.1)
    assertFalse(result.isWithinRecommendedLimits) // 6.43% > 3%
}
```

### Material Inventory Tests

The Material Inventory functionality should be tested to ensure proper inventory management:

```kotlin
// Example test for adding inventory
@Test
fun addInventoryItem() {
    // Arrange
    val material = Material(
        id = "material1",
        name = "Test Material",
        description = "Test Description",
        code = "TM001",
        unitOfMeasure = UnitOfMeasure.EACH
    )
    
    val inventory = MaterialInventory(
        id = "inventory1",
        materialId = "material1",
        material = material,
        quantity = 100.0,
        minimumQuantity = 20.0,
        location = "Test Location",
        notes = "Test Notes",
        lastUpdated = Date()
    )
    
    // Act
    saveInventoryItemUseCase(inventory)
    
    // Assert
    val savedInventory = getInventoryItemByIdUseCase("inventory1")
    assertEquals(inventory, savedInventory)
}

// Example test for inventory adjustment
@Test
fun adjustInventoryQuantity() {
    // Arrange
    val initialQuantity = 100.0
    val adjustmentQuantity = 50.0
    val inventoryId = "inventory1"
    
    // Act - Purchase (add)
    adjustInventory(inventoryId, adjustmentQuantity, "Purchase", TransactionType.PURCHASE)
    
    // Assert
    val updatedInventory = getInventoryItemByIdUseCase(inventoryId)
    assertEquals(initialQuantity + adjustmentQuantity, updatedInventory.quantity, 0.01)
}
```

## UI Tests

### Voltage Drop Fragment UI Test

```kotlin
@Test
fun testVoltageDropCalculation() {
    // Launch the fragment
    launchFragmentInContainer<VoltageDropFragment>()

    // Input test values
    onView(withId(R.id.editTextVoltage)).perform(clearText(), typeText("120"), closeSoftKeyboard())
    onView(withId(R.id.spinnerPhase)).perform(click())
    onView(withText("Single Phase")).perform(click())
    onView(withId(R.id.spinnerMaterial)).perform(click())
    onView(withText("Copper")).perform(click())
    onView(withId(R.id.spinnerWireSize)).perform(click())
    onView(withText("12 AWG")).perform(click())
    onView(withId(R.id.editTextLoadCurrent)).perform(clearText(), typeText("20"), closeSoftKeyboard())
    onView(withId(R.id.editTextDistance)).perform(clearText(), typeText("100"), closeSoftKeyboard())

    // Perform calculation
    onView(withId(R.id.buttonCalculate)).perform(click())

    // Verify results are displayed
    onView(withId(R.id.textViewVoltageDropValue)).check(matches(not(withText(""))))
    onView(withId(R.id.textViewVoltageDropPercentValue)).check(matches(not(withText(""))))
    onView(withId(R.id.textViewEndVoltageValue)).check(matches(not(withText(""))))
}
```

### Material Inventory Fragment UI Test

```kotlin
@Test
fun testInventoryListDisplayed() {
    // Launch the fragment
    launchFragmentInContainer<MaterialInventoryFragment>()

    // Verify the title is displayed
    onView(withId(R.id.textViewTitle)).check(matches(withText("Material Inventory")))
    
    // Verify the search view is displayed
    onView(withId(R.id.searchView)).check(matches(isDisplayed()))
    
    // Verify the filter button is displayed
    onView(withId(R.id.filterButton)).check(matches(isDisplayed()))
    
    // Verify the FAB is displayed
    onView(withId(R.id.fabAddInventory)).check(matches(isDisplayed()))
}
```

## Integration Tests

Integration tests should verify that different components of the app work together correctly:

```kotlin
@Test
fun testInventoryAdjustmentUpdatesTransactionHistory() {
    // Setup test data
    val material = createTestMaterial()
    val inventory = createTestInventory(material)
    
    // Perform inventory adjustment
    adjustInventory(inventory.id, 50.0, "Test adjustment", TransactionType.PURCHASE)
    
    // Verify transaction history is updated
    val transactions = getTransactionHistory(material.id)
    assertTrue(transactions.isNotEmpty())
    assertEquals(TransactionType.PURCHASE, transactions[0].transactionType)
    assertEquals(50.0, transactions[0].quantity, 0.01)
}
```

## Running Tests

To run unit tests:
```
./gradlew test
```

To run instrumented tests:
```
./gradlew connectedAndroidTest
```

To run a specific test class:
```
./gradlew testDebugUnitTest --tests "com.example.electricianapp.domain.usecase.voltagedrop.CalculateVoltageDropUseCaseTest"
```

## Test Coverage

To generate test coverage reports:
```
./gradlew jacocoTestReport
```

The coverage report will be available at:
```
app/build/reports/jacoco/jacocoTestReport/html/index.html
```
