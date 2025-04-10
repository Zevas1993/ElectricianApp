package com.example.electricianapp.data.repository.neccodes

import com.example.electricianapp.data.local.dao.NecCodeDao
import com.example.electricianapp.data.local.entity.CodeViolationCheckEntity
import com.example.electricianapp.data.local.entity.NecArticleEntity
import com.example.electricianapp.data.local.entity.NecCodeUpdateEntity
import com.example.electricianapp.domain.model.neccodes.ImpactLevel
import com.example.electricianapp.domain.model.neccodes.NecCategory
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provider for sample NEC code data.
 * This class is used to populate the database with sample data for testing and demonstration.
 */
@Singleton
class NecCodeDataProvider @Inject constructor(
    private val necCodeDao: NecCodeDao,
    private val gson: Gson
) {

    /**
     * Populate the database with sample NEC code articles
     */
    suspend fun populateSampleData() = withContext(Dispatchers.IO) {
        // Check if data already exists
        val existingArticles = necCodeDao.getArticleByNumber("210.8(A)(1)", 2020)
        if (existingArticles != null) {
            // Data already exists, no need to populate
            return@withContext
        }

        // Insert sample articles
        necCodeDao.insertArticles(getSampleArticles())

        // Insert sample updates
        necCodeDao.insertCodeUpdates(getSampleUpdates())

        // Insert sample violation checks
        necCodeDao.insertViolationChecks(getSampleViolationChecks())
    }

    /**
     * Get sample NEC articles
     */
    private fun getSampleArticles(): List<NecArticleEntity> {
        return listOf(
            // GFCI Protection
            NecArticleEntity(
                articleNumber = "210.8(A)(1)",
                title = "GFCI Protection for Personnel - Dwelling Units - Bathrooms",
                content = "All 125-volt, single-phase, 15- and 20-ampere receptacles installed in bathrooms shall have ground-fault circuit-interrupter protection for personnel.",
                summary = "GFCI protection required for all 125-volt, single-phase, 15- and 20-ampere receptacles in dwelling unit bathrooms.",
                category = NecCategory.WIRING_AND_PROTECTION,
                tagsJson = gson.toJson(listOf("GFCI", "Bathroom", "Dwelling Unit", "Receptacle")),
                relatedArticlesJson = gson.toJson(listOf("210.8(A)(2)", "210.8(A)(3)", "210.8(B)")),
                year = 2020
            ),
            
            NecArticleEntity(
                articleNumber = "210.8(A)(2)",
                title = "GFCI Protection for Personnel - Dwelling Units - Garages",
                content = "All 125-volt, single-phase, 15- and 20-ampere receptacles installed in garages, and grade-level portions of unfinished or finished accessory buildings used for storage or work areas shall have ground-fault circuit-interrupter protection for personnel.",
                summary = "GFCI protection required for all 125-volt, single-phase, 15- and 20-ampere receptacles in dwelling unit garages and accessory buildings.",
                category = NecCategory.WIRING_AND_PROTECTION,
                tagsJson = gson.toJson(listOf("GFCI", "Garage", "Dwelling Unit", "Receptacle", "Accessory Building")),
                relatedArticlesJson = gson.toJson(listOf("210.8(A)(1)", "210.8(A)(3)", "210.8(B)")),
                year = 2020
            ),
            
            // Box Fill
            NecArticleEntity(
                articleNumber = "314.16(A)",
                title = "Box Volume Calculations - Standard Boxes",
                content = "The volume of a wiring enclosure (box) shall be the total volume of the assembled sections and, where used, the space provided by plaster rings, domed covers, extension rings, and so forth, that are marked with their volume or are made from boxes the dimensions of which are listed in Table 314.16(A).",
                summary = "Defines how to calculate the volume of electrical boxes for determining maximum fill.",
                category = NecCategory.WIRING_METHODS_AND_MATERIALS,
                tagsJson = gson.toJson(listOf("Box Fill", "Volume", "Wiring Enclosure")),
                relatedArticlesJson = gson.toJson(listOf("314.16(B)", "314.16(C)")),
                year = 2020
            ),
            
            NecArticleEntity(
                articleNumber = "314.16(B)",
                title = "Box Fill Calculations",
                content = "The volumes in Table 314.16(A), or where applicable the actual volume of a box, shall be considered to contain only those conductors that are considered to be within the box. Conductors that originate and terminate within the same box are counted only once.",
                summary = "Specifies how to count conductors for box fill calculations.",
                category = NecCategory.WIRING_METHODS_AND_MATERIALS,
                tagsJson = gson.toJson(listOf("Box Fill", "Conductor Count", "Wiring Enclosure")),
                relatedArticlesJson = gson.toJson(listOf("314.16(A)", "314.16(C)")),
                year = 2020
            ),
            
            // Conduit Fill
            NecArticleEntity(
                articleNumber = "Chapter 9, Table 1",
                title = "Percent of Cross Section of Conduit and Tubing for Conductors",
                content = "The number of conductors permitted in a single conduit or tubing shall not exceed the percentages in Table 1.",
                summary = "Specifies the maximum fill percentage for conduits based on the number of conductors.",
                category = NecCategory.TABLES,
                tagsJson = gson.toJson(listOf("Conduit Fill", "Raceway", "Conductors")),
                relatedArticlesJson = gson.toJson(listOf("Chapter 9, Table 4", "Chapter 9, Table 5")),
                year = 2020
            ),
            
            // Dwelling Unit Calculations
            NecArticleEntity(
                articleNumber = "220.12",
                title = "Lighting Load for Specified Occupancies",
                content = "A unit load of not less than that specified in Table 220.12 for occupancies specified therein shall constitute the minimum lighting load.",
                summary = "Specifies the minimum lighting load to be included in load calculations for various occupancies.",
                category = NecCategory.WIRING_AND_PROTECTION,
                tagsJson = gson.toJson(listOf("Lighting Load", "Load Calculation", "Dwelling Unit")),
                relatedArticlesJson = gson.toJson(listOf("220.14", "220.42")),
                year = 2020
            ),
            
            NecArticleEntity(
                articleNumber = "220.42",
                title = "General Lighting",
                content = "The demand factors specified in Table 220.42 shall apply to that portion of the total branch-circuit load calculated for general illumination.",
                summary = "Provides demand factors for general lighting loads in various occupancies.",
                category = NecCategory.WIRING_AND_PROTECTION,
                tagsJson = gson.toJson(listOf("Lighting Load", "Demand Factor", "Load Calculation")),
                relatedArticlesJson = gson.toJson(listOf("220.12", "220.14")),
                year = 2020
            ),
            
            // Voltage Drop
            NecArticleEntity(
                articleNumber = "210.19(A)(1) Informational Note No. 4",
                title = "Voltage Drop - Branch Circuits",
                content = "Conductors for branch circuits as defined in Article 100, sized to prevent a voltage drop exceeding 3 percent at the farthest outlet of power, heating, and lighting loads, or combinations of such loads, and where the maximum total voltage drop on both feeders and branch circuits to the farthest outlet does not exceed 5 percent, provide reasonable efficiency of operation.",
                summary = "Recommends limiting voltage drop to 3% for branch circuits and 5% total.",
                category = NecCategory.WIRING_AND_PROTECTION,
                tagsJson = gson.toJson(listOf("Voltage Drop", "Branch Circuit", "Conductor Sizing")),
                relatedArticlesJson = gson.toJson(listOf("215.2(A)(1) Informational Note No. 2")),
                year = 2020
            ),
            
            NecArticleEntity(
                articleNumber = "215.2(A)(1) Informational Note No. 2",
                title = "Voltage Drop - Feeders",
                content = "Conductors for feeders as defined in Article 100, sized to prevent a voltage drop exceeding 3 percent at the farthest outlet of power, heating, and lighting loads, or combinations of such loads, and where the maximum total voltage drop on both feeders and branch circuits to the farthest outlet does not exceed 5 percent, provide reasonable efficiency of operation.",
                summary = "Recommends limiting voltage drop to 3% for feeders and 5% total.",
                category = NecCategory.WIRING_AND_PROTECTION,
                tagsJson = gson.toJson(listOf("Voltage Drop", "Feeder", "Conductor Sizing")),
                relatedArticlesJson = gson.toJson(listOf("210.19(A)(1) Informational Note No. 4")),
                year = 2020
            )
        )
    }

    /**
     * Get sample NEC code updates
     */
    private fun getSampleUpdates(): List<NecCodeUpdateEntity> {
        return listOf(
            NecCodeUpdateEntity(
                articleNumber = "210.8(A)",
                previousEdition = 2017,
                currentEdition = 2020,
                changeDescription = "Added GFCI protection requirements for all 125-volt, single-phase, 15- and 20-ampere receptacles in dwelling unit basements, whether finished or unfinished.",
                impactLevel = ImpactLevel.SIGNIFICANT
            ),
            
            NecCodeUpdateEntity(
                articleNumber = "210.8(F)",
                previousEdition = 2017,
                currentEdition = 2020,
                changeDescription = "Added new requirement for GFCI protection for all outdoor outlets supplied by single-phase branch circuits rated 150 volts to ground or less, 50 amperes or less.",
                impactLevel = ImpactLevel.SIGNIFICANT
            ),
            
            NecCodeUpdateEntity(
                articleNumber = "210.52(C)(2)",
                previousEdition = 2017,
                currentEdition = 2020,
                changeDescription = "Clarified that peninsular countertop spaces are measured from the connected perpendicular wall, not from the edge of the countertop.",
                impactLevel = ImpactLevel.MODERATE
            ),
            
            NecCodeUpdateEntity(
                articleNumber = "220.12",
                previousEdition = 2017,
                currentEdition = 2020,
                changeDescription = "Revised to allow an additional method for calculating lighting loads using the lighting power densities in Table 220.12.",
                impactLevel = ImpactLevel.MODERATE
            ),
            
            NecCodeUpdateEntity(
                articleNumber = "230.67",
                previousEdition = 2017,
                currentEdition = 2020,
                changeDescription = "Added new requirement for surge protection devices (SPDs) for all dwelling unit service equipment.",
                impactLevel = ImpactLevel.SIGNIFICANT
            )
        )
    }

    /**
     * Get sample code violation checks
     */
    private fun getSampleViolationChecks(): List<CodeViolationCheckEntity> {
        return listOf(
            CodeViolationCheckEntity(
                articleNumber = "210.8(A)(1)",
                checkDescription = "GFCI Protection in Bathrooms",
                parametersJson = gson.toJson(mapOf(
                    "location" to "string",
                    "hasGFCI" to "boolean"
                )),
                checkLogic = "location === 'bathroom' && hasGFCI === 'false'",
                explanationTemplate = "Receptacles in {{location}} require GFCI protection per NEC 210.8(A)(1)."
            ),
            
            CodeViolationCheckEntity(
                articleNumber = "210.52(C)",
                checkDescription = "Kitchen Countertop Receptacle Spacing",
                parametersJson = gson.toJson(mapOf(
                    "countertopLength" to "number",
                    "receptacleCount" to "number"
                )),
                checkLogic = "countertopLength > 24 && receptacleCount < Math.ceil(countertopLength / 24)",
                explanationTemplate = "Kitchen countertop ({{countertopLength}} inches) requires at least {{Math.ceil(countertopLength / 24)}} receptacles, but only {{receptacleCount}} are provided."
            ),
            
            CodeViolationCheckEntity(
                articleNumber = "210.52(D)",
                checkDescription = "Bathroom Receptacle Requirement",
                parametersJson = gson.toJson(mapOf(
                    "hasBathroomReceptacle" to "boolean",
                    "isWithin3FeetOfSink" to "boolean"
                )),
                checkLogic = "hasBathroomReceptacle === 'false' || isWithin3FeetOfSink === 'false'",
                explanationTemplate = "Bathroom requires at least one receptacle within 3 feet of the outside edge of each basin."
            ),
            
            CodeViolationCheckEntity(
                articleNumber = "210.70(A)(1)",
                checkDescription = "Lighting Outlet in Habitable Rooms",
                parametersJson = gson.toJson(mapOf(
                    "roomType" to "string",
                    "hasLightingOutlet" to "boolean",
                    "hasWallSwitch" to "boolean"
                )),
                checkLogic = "(roomType === 'bedroom' || roomType === 'living room' || roomType === 'kitchen' || roomType === 'dining room') && (hasLightingOutlet === 'false' || hasWallSwitch === 'false')",
                explanationTemplate = "{{roomType}} requires at least one wall-switch-controlled lighting outlet."
            )
        )
    }
}
