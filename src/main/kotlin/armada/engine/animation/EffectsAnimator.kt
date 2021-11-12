package armada.engine.animation

import armada.WiringHarness
import armada.engine.Engine
import armada.engine.animation.effect.ExplosionEffect
import armada.engine.animation.effect.SplashEffect
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class EffectsAnimator : Animator {

    fun splash(x: Int, y: Int) {
        val grid = WiringHarness.battleGrid
        (0..9).forEach {
            Engine.scheduleImmediate(it * 6) {
                grid.effectsLayer[x][y].content = SplashEffect(it)
                Engine.Action.SUCCESS
            }
        }
    }

    fun explode(x: Int, y: Int) {
        val grid = WiringHarness.battleGrid
        (0..19).forEach {
            Engine.scheduleImmediate(it * 6) {
                grid.effectsLayer[x][y].content = ExplosionEffect(it)
                Engine.Action.SUCCESS
            }
        }
    }
}