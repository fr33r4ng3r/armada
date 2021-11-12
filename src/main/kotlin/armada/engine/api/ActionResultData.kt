package armada.engine.api

import armada.engine.Engine
import kotlinx.coroutines.DelicateCoroutinesApi

data class ActionResultData(val success: Boolean, val message: String?) {
    @DelicateCoroutinesApi
    companion object {
        fun from(result: Engine.Action.Result): ActionResultData {
            return ActionResultData(result.success, result.message)
        }
    }
}
