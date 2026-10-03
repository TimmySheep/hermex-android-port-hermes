package com.uzairansar.hermex.ui.settings

import java.io.File
import javax.imageio.ImageIO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppIconForegroundAssetTest {
    @Test
    fun lightForegroundHasTransparencyVisibleArtworkAndFitsAdaptiveSafeZone() {
        val file = File("src/main/res/drawable-nodpi/hermex_app_icon_light_foreground.png")
        assertTrue("Missing Android-only light foreground: ${file.absolutePath}", file.isFile)

        val image = ImageIO.read(file)
        assertEquals(1024, image.width)
        assertEquals(1024, image.height)

        var hasTransparentPixel = false
        var minX = image.width
        var minY = image.height
        var maxX = -1
        var maxY = -1
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val alpha = image.getRGB(x, y) ushr 24
                hasTransparentPixel = hasTransparentPixel || alpha == 0
                if (alpha > 8) {
                    minX = minOf(minX, x)
                    minY = minOf(minY, y)
                    maxX = maxOf(maxX, x)
                    maxY = maxOf(maxY, y)
                }
            }
        }

        assertTrue("Foreground must include transparent pixels", hasTransparentPixel)
        assertEquals("Canvas corner must remain transparent", 0, image.getRGB(0, 0) ushr 24)
        assertTrue("Foreground must contain visible artwork", maxX >= minX && maxY >= minY)

        val safeZoneStart = (1024 * (1.0 - 66.0 / 108.0) / 2.0).toInt()
        val safeZoneEnd = 1024 - safeZoneStart
        assertTrue("Artwork left of adaptive safe zone: $minX", minX >= safeZoneStart)
        assertTrue("Artwork top of adaptive safe zone: $minY", minY >= safeZoneStart)
        assertTrue("Artwork right of adaptive safe zone: $maxX", maxX < safeZoneEnd)
        assertTrue("Artwork bottom of adaptive safe zone: $maxY", maxY < safeZoneEnd)
    }
}
