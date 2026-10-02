package com.example

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SeedArchetype
import com.example.domain.SeedCriteria
import com.example.domain.SeedGeneratorEngine
import com.example.ui.viewmodel.MapLayer
import com.example.ui.viewmodel.SeedMapState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Seed Generate", appName)
  }

  @Test
  fun `seedMapState manages zoom pan and layer persistence`() {
    val savedState = SavedStateHandle()
    val mapState = SeedMapState(savedState)

    // Initial default zoom and layers
    assertEquals(1.0f, mapState.zoom.value, 0.001f)
    assertTrue(mapState.layerBiomes.value)
    assertTrue(mapState.layerVillages.value)
    assertTrue(mapState.layerStructures.value)
    assertTrue(mapState.layerCoordinates.value)

    // Panning & centering
    mapState.pan(100f, -50f)
    assertEquals(100f, mapState.centerX.value, 0.001f)
    assertEquals(-50f, mapState.centerZ.value, 0.001f)

    mapState.centerOnSpawn(250, 400)
    assertEquals(250f, mapState.centerX.value, 0.001f)
    assertEquals(400f, mapState.centerZ.value, 0.001f)

    // Zooming
    mapState.setZoom(2.5f)
    assertEquals(2.5f, mapState.zoom.value, 0.001f)

    // Layer toggles
    mapState.toggleLayer(MapLayer.BIOMES)
    assertFalse(mapState.layerBiomes.value)

    // Test persistence across recreation with saved state handle
    val recreatedMapState = SeedMapState(savedState)
    assertEquals(250f, recreatedMapState.centerX.value, 0.001f)
    assertEquals(400f, recreatedMapState.centerZ.value, 0.001f)
    assertEquals(2.5f, recreatedMapState.zoom.value, 0.001f)
    assertFalse(recreatedMapState.layerBiomes.value)
  }

  @Test
  fun `seed generator parses numeric and hashes text correctly`() {
    // Numeric string parse
    val (numSeed, isNumeric) = SeedGeneratorEngine.parseOrHashSeed("-4270425838048259167")
    assertEquals(-4270425838048259167L, numSeed)
    assertTrue(isNumeric)

    // Text string hashing using standard Java hashCode
    val (textSeed, isTextNumeric) = SeedGeneratorEngine.parseOrHashSeed("Minecraft")
    assertEquals("Minecraft".hashCode().toLong(), textSeed)
    assertFalse(isTextNumeric)

    // Empty input produces a non-zero random seed
    val (emptySeed, _) = SeedGeneratorEngine.parseOrHashSeed("   ")
    assertNotNull(emptySeed)
  }

  @Test
  fun `seed generator generates archetype results with valid coordinates and parity`() {
    for (archetype in SeedArchetype.entries) {
      val result = SeedGeneratorEngine.generateArchetypeResult(archetype)
      assertNotNull(result.seed)
      assertTrue(result.title.isNotBlank())
      assertTrue(result.spawnBiome.isNotBlank())
      assertTrue(result.coordinates.isNotEmpty())
      assertNotNull(result.hexFormat)
      assertTrue(result.hexFormat.startsWith("0x"))

      val parity = SeedGeneratorEngine.getBedrockParityInfo(result.seed)
      assertEquals(result.seed.toInt().toLong(), parity.bedrockSeed)
    }
  }

  @Test
  fun `seed generator produces phrase seeds and bedrock parity`() {
    val result = SeedGeneratorEngine.generateMnemonicWordSeed()
    assertTrue(result.seedString.contains("-"))
    assertNotNull(result.seed)
    assertEquals("Phrase: ${result.seedString}", result.title)
    assertTrue(result.tags.contains("Mnemonic"))

    val parity = SeedGeneratorEngine.getBedrockParityInfo(result.seed)
    assertEquals(result.isBedrockDirectParity, result.seed in Int.MIN_VALUE..Int.MAX_VALUE)
  }

  @Test
  fun `seed generator finds seeds matching criteria`() {
    val criteria = SeedCriteria(
      requireVillage = true,
      requireTrialChamber = true,
      preferredBiome = "Cherry"
    )
    val result = SeedGeneratorEngine.generateWithCriteria(criteria, version = "26.2")
    assertNotNull(result.seed)
    assertTrue(result.coordinates.isNotEmpty())
  }

  @Test
  fun `seedMapState handles drag-to-pan and pinch-to-zoom gestures accurately`() {
    val savedState = SavedStateHandle()
    val mapState = SeedMapState(savedState)

    // Initial state
    assertEquals(0f, mapState.centerX.value, 0.001f)
    assertEquals(0f, mapState.centerZ.value, 0.001f)
    assertEquals(1.0f, mapState.zoom.value, 0.001f)

    // Simulate drag-to-pan: dragging right/down moves camera by delta
    val currentZoom = mapState.zoom.value
    val tileSize = (24f * currentZoom).coerceAtLeast(8f)
    val scaleRatio = tileSize / 48f
    val panX = 96f // 96 pixels drag
    val panY = -48f // 48 pixels drag upward
    mapState.pan(-panX / scaleRatio, -panY / scaleRatio)

    assertEquals(-192f, mapState.centerX.value, 0.01f)
    assertEquals(96f, mapState.centerZ.value, 0.01f)

    // Simulate pinch-to-zoom
    mapState.zoomBy(1.5f)
    assertEquals(1.5f, mapState.zoom.value, 0.001f)

    // Pinch-to-zoom limits clamping
    mapState.zoomBy(10.0f)
    assertEquals(SeedMapState.MAX_ZOOM, mapState.zoom.value, 0.001f)

    mapState.zoomBy(0.01f)
    assertEquals(SeedMapState.MIN_ZOOM, mapState.zoom.value, 0.001f)

    // Reset zoom
    mapState.resetZoom()
    assertEquals(1.0f, mapState.zoom.value, 0.001f)
  }
}
