package com.example

import com.example.data.model.CompatibilityLevel
import com.example.data.model.SeedArchetype
import com.example.domain.SeedAnalyzerEngine
import com.example.domain.SeedGeneratorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SeedEngineRobolectricTest {

    @Test
    fun `numeric seed input parses directly as 64-bit Long`() {
        val input = "-4270425838048259167"
        val (seed, isNumeric) = SeedGeneratorEngine.parseOrHashSeed(input)
        assertEquals(-4270425838048259167L, seed)
        assertTrue(isNumeric)
    }

    @Test
    fun `alphanumeric string computes Java String hashCode`() {
        val text = "Minecraft"
        val (seed, isNumeric) = SeedGeneratorEngine.parseOrHashSeed(text)
        assertEquals("Minecraft".hashCode().toLong(), seed)
        assertEquals(false, isNumeric)
    }

    @Test
    fun `archetype generation returns seed and POIs`() {
        val (seed, title, pois) = SeedGeneratorEngine.generateArchetypeSeed(SeedArchetype.SURVIVAL_ISLAND)
        assertTrue(title.isNotEmpty())
        assertTrue(pois.isNotEmpty())
    }

    @Test
    fun `version compatibility marks identical version as verified`() {
        val result = SeedAnalyzerEngine.analyzeSeed(
            seedValue = -4270425838048259167L,
            targetVersion = "1.21.x",
            loader = "Vanilla"
        )
        val v121Report = result.compatibilityReports.find { it.evaluatedVersion == "1.21.x" }
        assertNotNull(v121Report)
        assertEquals(CompatibilityLevel.VERIFIED_COMPATIBLE, v121Report?.level)
        assertEquals(100, v121Report?.biomeSimilarityPercent)
    }

    @Test
    fun `version compatibility detects 1_18 terrain overhaul divide`() {
        val result = SeedAnalyzerEngine.analyzeSeed(
            seedValue = -4270425838048259167L,
            targetVersion = "1.21.x",
            loader = "Vanilla"
        )
        val olderReport = result.compatibilityReports.find { it.evaluatedVersion == "Older" }
        assertNotNull(olderReport)
        assertEquals(CompatibilityLevel.TERRAIN_INCOMPATIBLE, olderReport?.level)
    }

    @Test
    fun `multi-noise climate analysis calculates biomes and spawn coordinates`() {
        val result = SeedAnalyzerEngine.analyzeSeed(
            seedValue = 8624896L,
            targetVersion = "1.21.x",
            loader = "Vanilla"
        )
        assertTrue(result.estimatedSpawnBiome.isNotEmpty())
        assertTrue(result.climateZone.isNotEmpty())
        assertTrue(result.keyPoiEstimates.isNotEmpty())
        val spawn = result.keyPoiEstimates.first()
        assertEquals("World Spawn Point", spawn.name)
    }
}
