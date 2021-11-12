package armada

import armada.engine.Engine
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.DelicateCoroutinesApi
import java.lang.Thread.UncaughtExceptionHandler
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


class Main : Application() {
    override fun start(stage: Stage) {

        stage.initStyle(StageStyle.UNDECORATED)

        Thread.currentThread().uncaughtExceptionHandler =
            UncaughtExceptionHandler { _: Thread?, throwable: Throwable ->
                LOG.log(Level.SEVERE, throwable) { throwable.message }
            }

        Font.loadFont(Main::class.java.getResourceAsStream("/armada/webfonts/fa-regular-400.ttf"), 24.0)
        Font.loadFont(Main::class.java.getResourceAsStream("/armada/webfonts/fa-solid-900.ttf"), 24.0)
        Font.loadFont(Main::class.java.getResourceAsStream("/armada/webfonts/OpenSans-Condensed-Regular.ttf"), 24.0)
        Font.loadFont(Main::class.java.getResourceAsStream("/armada/webfonts/OpenSans-Condensed-Bold.ttf"), 24.0)

        val bundle: ResourceBundle = ResourceBundle.getBundle("armada.main")
        val loader = FXMLLoader(sceneUrl, bundle)

        val root = loader.load<Parent>()

        val scene = Scene(root)

        val props = Properties()
        props.load(javaClass.getResourceAsStream("/armada/version.properties"))
        stage.title =
            "Armada Verification Platform version ${props.getProperty("version")})"
        stage.scene = scene

        ResizeHelper.addResizeListener(stage)

        stage.show()
    }

    @DelicateCoroutinesApi
    companion object {

        private val sceneUrl = Main::class.java.getResource("/armada/scene.fxml")
        private val LOG = Logger.getLogger(Main::class.java.name)

        @JvmStatic
        fun main(args: Array<String>) {
            val context = Engine.startServer()
            launch(Main::class.java, *args)
            context.stop()
        }

    }
}
