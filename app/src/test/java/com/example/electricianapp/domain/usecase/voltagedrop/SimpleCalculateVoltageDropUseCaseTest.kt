package com.example.electricianapp.domain.usecase.voltagedrop

import com.example.electricianapp.domain.model.voltagedrop.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Simple unit tests for [CalculateVoltageDropUseCase] without Hilt dependencies
 */
class SimpleCalculateVoltageDropUseCaseTest {

    private val calculateVoltageDropUseCase = CalculateVoltageDropUseCase()

    @Test
    fun `calculate voltage drop for single-phase copper circuit`() {
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
        assertEquals(3.0, result.recommendedLimit, 0.01)
    }

    @Test
    fun `calculate voltage drop for three-phase aluminum circuit`() {
        // Arrange
        val input = VoltageDropInput(
            systemType = SystemType.THREE_PHASE,
            conductorType = ConductorType.ALUMINUM,
            conduitMaterial = ConduitMaterial.STEEL,
            temperatureRating = TemperatureRating.RATING_75C,
            wireSize = "2",
            lengthInFeet = 200.0,
            loadInAmps = 50.0,
            voltageInVolts = 480.0,
            powerFactor = 0.9
        )

        // Act
        val result = calculateVoltageDropUseCase(input)

        // Assert
        // Expected voltage drop will be calculated with impedance and power factor
        assertTrue(result.voltageDropInVolts > 0)
        assertTrue(result.voltageDropPercentage > 0)
        assertEquals(0.319, result.conductorResistance, 0.001)
        assertEquals(0.054, result.conductorReactance, 0.001) // 0.045 * 1.2 for steel conduit
        assertTrue(result.endVoltage < 480.0)
    }

    @Test
    fun `calculate voltage drop for DC circuit`() {
        // Arrange
        val input = VoltageDropInput(
            systemType = SystemType.DC,
            conductorType = ConductorType.COPPER,
            conduitMaterial = ConduitMaterial.PVC,
            temperatureRating = TemperatureRating.RATING_75C,
            wireSize = "10",
            lengthInFeet = 50.0,
            loadInAmps = 15.0,
            voltageInVolts = 24.0,
            powerFactor = 1.0 // Not used for DC
        )

        // Act
        val result = calculateVoltageDropUseCase(input)

        // Assert
        // Expected voltage drop: 15A * 1.21 ohms/1000ft * (50ft/1000) * 2 = 1.815V
        assertEquals(1.815, result.voltageDropInVolts, 0.1)
        assertEquals(7.56, result.voltageDropPercentage, 0.1)
        assertEquals(1.21, result.conductorResistance, 0.01)
        assertEquals(0.05, result.conductorReactance, 0.001) // Not used in calculation
        assertEquals(22.185, result.endVoltage, 0.1)
        assertFalse(result.isWithinRecommendedLimits) // 7.56% > 3%
    }
}
