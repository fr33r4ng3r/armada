package armada.ui

import armada.WiringHarness
import armada.engine.animation.effect.ExplosionEffect
import armada.engine.animation.effect.SplashEffect
import armada.ui.sprites.ExplosionSprite
import armada.ui.sprites.SplashSprite
import javafx.scene.canvas.GraphicsContext
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class EffectsPainter {

    fun paint(gc: GraphicsContext, gridSize: Double) {
        val grid = WiringHarness.battleGrid
        grid.effectsLayer.forEach { row ->
            row.forEach { tile ->
                when (tile.content) {
                    is SplashEffect -> {
                        val splashEffect = tile.content as SplashEffect
                        SplashSprite.draw(gc, tile.x, tile.y, splashEffect.frame, gridSize)
                    }
                    is ExplosionEffect -> {
                        val explosionEffect = tile.content as ExplosionEffect
                        ExplosionSprite.draw(gc, tile.x, tile.y, explosionEffect.frame, gridSize)
                    }
                }
            }
        }
    }
}