package com.example.electricalcalculator.domain.model.conduitfill

/**
 * Represents the type of conduit used in conduit fill calculations.
 */
enum class ConduitType {
    EMT, // Electrical Metallic Tubing
    IMC, // Intermediate Metal Conduit
    RMC, // Rigid Metal Conduit
    PVC, // Rigid PVC Conduit (Schedule 40 or 80 - assuming common values)
    ENT  // Electrical Nonmetallic Tubing
}
