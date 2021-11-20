package armada.engine.animation

import armada.WiringHarness
import armada.engine.Engine
import armada.engine.Projectile
import armada.engine.Ship
import armada.engine.Theatre
import armada.utils.Trigonometry
import kotlinx.coroutines.DelicateCoroutinesApi
import org.slf4j.LoggerFactory
import java.lang.Math.toRadians
import kotlin.math.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@DelicateCoroutinesApi
class ProjectileAnimator : Animator {

    val effectsAnimator = EffectsAnimator()

    fun launch(fromX: Int, fromY: Int, toX: Int, toY: Int, velocity: Double, willHit: Boolean) {
        val theatre = WiringHarness.theatre
        val rotation = Trigonometry.deltaDegrees(fromX, fromY, fromX - 1, fromY, toX, toY)
        val xVector = (fromX - toX) * Theatre.GRID_SQUARE_WORLD_DISTANCE
        val yVector = (fromY - toY) * Theatre.GRID_SQUARE_WORLD_DISTANCE
        val distance = sqrt(xVector.pow(2) + yVector.pow(2))
        val ticks = floor((distance / velocity) * 100).toInt()
        val delta = distance / ticks
        val deltaX = cos(toRadians(rotation)) * -delta
        val deltaY = sin(toRadians(rotation)) * -delta

        LOG.debug("creating projectile from: $fromX, $fromY with vectors: $xVector,$yVector at a distance of: $distance with delta: $delta | $deltaX,$deltaY aimed at $toX,$toY with a rotation of $rotation")

        val projectile = Projectile()
        with(projectile) {
            xWorld = fromX * Theatre.GRID_SQUARE_WORLD_DISTANCE
            yWorld = fromY * Theatre.GRID_SQUARE_WORLD_DISTANCE
            orientation = rotation
        }
        theatre.projectiles.add(projectile)
        (0..ticks).forEach { tick ->
            Engine.scheduleImmediate(tick) {
                with(projectile) {
                    xWorld += deltaX
                    yWorld += deltaY
                }
                Engine.Action.SUCCESS
            }
        }
        Engine.scheduleImmediate(ticks) {
            theatre.projectiles.remove(projectile)
            if (willHit) {
                effectsAnimator.explode(toX, toY)
                // TODO consider alternative wiring for animation dependent game state changes
                (theatre.grid.damageLayer[toX][toY].content as? Ship.Companion.HullSegment)?.health?.addAndGet(-2)
            } else {
                effectsAnimator.splash(toX, toY)
            }
            Engine.Action.SUCCESS
        }
    }

    companion object {
        val LOG = LoggerFactory.getLogger(this::class.java)
    }
}