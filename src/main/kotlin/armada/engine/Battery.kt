package armada.engine

import armada.engine.animation.ProjectileAnimator
import armada.utils.Trigonometry.deltaDegrees
import kotlinx.coroutines.DelicateCoroutinesApi
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

@DelicateCoroutinesApi
class Battery(val grid: BattleGrid, val munitions: Int) {

    private val projectileAnimator = ProjectileAnimator()

    val turrets = LinkedList<Turret>()

    var munitionsRemaining = munitions
        private set

    inner class Turret(val x: Int, val y: Int, private val turnDegreesPerTick: Double) {
        val guns = LinkedList<Gun>()

        private var targetX = x - 1
        private var targetY = y
        private var ticksToTarget = 0
        private var targetAcquired = false
        var rotation = 0.0
            private set

        inner class Gun(private val ticksToLoad: Int, private val ticksToCool: Int) {
            private var ticksToLoaded = 0;
            private var loaded = false;
            private var ticksToCooled = 0;
            private var cooled = true;

            fun load(): Engine.Todo {
                if (ticksToLoaded > 0) {
                    return Engine.Todo(0) {
                        Engine.Action.Result(false, "Gun Loading already in progress")
                    }
                }
                ticksToLoaded = ticksToLoad + ticksToCooled
                return Engine.Todo(ticksToLoaded) {
                    ticksToLoaded = 0
                    ticksToCooled = 0
                    loaded = true
                    cooled = true
                    Engine.Action.SUCCESS
                }
            }

            fun fire(): Engine.Todo {
                if (!targetAcquired) {
                    return Engine.Todo(0) {
                        Engine.Action.Result(false, "Cannot Fire! - target has not yet been acquired")
                    }
                }
                if (!loaded) {
                    return Engine.Todo(0) {
                        Engine.Action.Result(false, "Cannot Fire! - gun has not been loaded")
                    }
                }
                if (munitionsRemaining <= 0) {
                    return Engine.Todo(0) {
                        Engine.Action.Result(false, "Cannot Fire! - out of ammunition")
                    }
                }
                munitionsRemaining -= 1
                val hullSegment = grid.damageLayer[this@Turret.targetX][this@Turret.targetY].content as? Ship.Companion.HullSegment
                val willHit = hullSegment != null
                Engine.scheduleImmediate(0) {
                    grid.actionsLayer[this@Turret.targetX][this@Turret.targetY].content = null
                    Engine.Action.SUCCESS
                }
                return Engine.Todo(0) {
                    ticksToCooled = ticksToCool
                    cooled = false
                    loaded = false
                    projectileAnimator.launch(this@Turret.x,this@Turret.y, this@Turret.targetX, this@Turret.targetY, 750.0, willHit)
                    Engine.Action.SUCCESS
                }
            }
        }

        fun target(x: Int, y: Int): Engine.Todo {

            if (x == targetX && y == targetY) {
                ticksToTarget = 0
                return Engine.Todo(ticksToTarget) {
                    targetAcquired = true
                    targetX = x;
                    targetY = y;
                    grid.actionsLayer[x][y].content = TargetLocked
                    Engine.Action.SUCCESS
                }
            } else if (ticksToTarget > 0) {
                return Engine.Todo(0) {
                    Engine.Action.Result(false, "Turret targeting already in progress")
                }
            }

            Engine.scheduleImmediate(0) {
                grid.actionsLayer[x][y].content = null
                Engine.Action.SUCCESS
            }

            targetAcquired = false

            val deltaD = deltaDegrees(this.x, this.y, targetX, targetY, x, y)

            ticksToTarget = (abs(deltaD) / turnDegreesPerTick).roundToInt()

            LOG.debug("targeting $x, $y at $deltaD in $ticksToTarget ticks")

            val zDelta = 10.0 / ticksToTarget
            val dDelta = deltaD / ticksToTarget

            grid.actionsLayer[x][y].content = TargetAction(10.0)

            (1 until ticksToTarget).forEach { i ->
                Engine.scheduleImmediate(i) {
                    if (ticksToTarget > 0) ticksToTarget -= 1
                    rotation += dDelta
                    val actionTile = grid.actionsLayer[x][y]
                    (actionTile.content as? TargetAction?).let {
                        val z = it?.zone ?: 10.0
                        actionTile.content = TargetAction(z - zDelta)
                    }
                    Engine.Action.SUCCESS
                }
            }
            return Engine.Todo(ticksToTarget) {
                ticksToTarget = 0
                targetAcquired = true
                targetX = x;
                targetY = y;
                rotation = deltaDegrees(this.x, this.y, this.x - 1, this.y, x, y)
                grid.actionsLayer[x][y].content = TargetLocked
                Engine.Action.SUCCESS
            }
        }
    }

    companion object {
        val LOG = LoggerFactory.getLogger(this::class.java)

        class TargetAction(val zone: Double) : Tile.Companion.Content
        object TargetLocked : Tile.Companion.Content
    }
}