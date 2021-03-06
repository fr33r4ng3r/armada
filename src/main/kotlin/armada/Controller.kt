package armada

import armada.engine.*
import armada.ui.OceanView
import armada.utils.FormatUtils.format
import com.jfoenix.controls.JFXButton
import javafx.animation.AnimationTimer
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBoxBase
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.selects.select
import java.net.URL
import java.util.*
import java.util.prefs.Preferences
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class Controller : Initializable {

    @FXML
    lateinit var main: BorderPane

    @FXML
    lateinit var maximizeLabel: Label

    @FXML
    lateinit var content: Pane

    @FXML
    lateinit var initializeButton: JFXButton

    @FXML
    lateinit var startButton: JFXButton

    @FXML
    lateinit var resetButton: JFXButton

    @FXML
    lateinit var gridXField: TextField

    @FXML
    lateinit var gridYField: TextField

    @FXML
    lateinit var nameLabel: Label

    @FXML
    lateinit var munitionsLabel: Label

    @FXML
    lateinit var timeLabel: Label

    @FXML
    lateinit var scorePane: AnchorPane

    @FXML
    lateinit var finalMunitionsLabel: Label

    @FXML
    lateinit var finalTimeLabel: Label

    @FXML
    lateinit var finalScoreLabel: Label

    @FXML
    lateinit var closeButton: Button

    private val stage: Stage by lazy { main.scene.window as Stage }

    private val appEvents: Channel<AppEvent> by lazy { Channel(10_000) }
    private val logChannel: Channel<String> by lazy { Channel(10_000) }

    private var yOffset: Double = 0.0
    private var xOffset: Double = 0.0

    private val preferences: Preferences = Preferences.userNodeForPackage(Main::class.java)

    private fun Node.onClick(action: suspend (MouseEvent) -> Unit) {
        // launch one actor to handle all events on this node
        val eventActor = GlobalScope.actor<MouseEvent>(Dispatchers.JavaFx) {
            for (event in channel) action(event) // pass event to action
        }
        // install a listener to offer events to this actor
        onMouseClicked = EventHandler { event ->
            eventActor.trySend(event)
        }
    }

    private fun ComboBoxBase<*>.onAction(action: suspend (ActionEvent) -> Unit) {
        // launch one actor to handle all events on this node
        val eventActor = GlobalScope.actor<ActionEvent>(Dispatchers.JavaFx) {
            for (event in channel) action(event) // pass event to action
        }
        // install a listener to offer events to this actor
        onAction = EventHandler { event ->
            eventActor.trySend(event)
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        val canvas = OceanView()
        content.children.add(canvas)

        canvas.widthProperty().bind(content.widthProperty())
        canvas.heightProperty().bind(content.heightProperty())

        object : AnimationTimer() {
            override fun handle(now: Long) {
                appEvents.tryReceive().getOrNull()?.handle()
                logChannel.tryReceive().getOrNull()?.let { }
            }
        }.start()

        initializeButton.onClick {
            onSetup()
            canvas.draw()
        }
        initializeButton.isDisable = false

        GlobalScope.launch(Dispatchers.JavaFx) {
            while (isActive) {
                val name = select<String> {
                    Engine.registrationChannel.onReceive {
                        it.name
                    }
                }
                nameLabel.text = name
                startButton.isDisable = false
                initializeButton.isDisable = true
            }
        }

        GlobalScope.launch(Dispatchers.JavaFx) {
            while (isActive) {
                val score = select<Score> {
                    Engine.finishChannel.onReceive { it }
                }
                WiringHarness.satellite.stopScanner()
                initializeButton.isDisable = false
                resetButton.isDisable = true
                finalMunitionsLabel.text = score.munitionsRemaining.format(1)
                finalTimeLabel.text = "${score.time.toMinutesPart().format(2)}:${
                    score.time.toSecondsPart().format(2)
                }:${(score.time.toMillisPart() / 10).format(2)}"
                scorePane.isVisible = true
            }
        }

        GlobalScope.launch(Dispatchers.JavaFx) {
            while (isActive) {
                select<Int> {
                    Engine.drawChannel.onReceive {
                        it
                    }
                }
                canvas.draw()
            }
        }

        startButton.onClick {
            startButton.isDisable = true
            resetButton.isDisable = false
            Engine.start(WiringHarness.theatre)
            WiringHarness.scoreKeeper.start()
            WiringHarness.satellite.startScanner()
        }

        resetButton.onClick {
            initializeButton.isDisable = false
            resetButton.isDisable = true
            WiringHarness.satellite.stopScanner()
            WiringHarness.scoreKeeper.stop()
        }

        closeButton.onClick {
            scorePane.isVisible = false
        }
    }

    private suspend fun onSetup() = coroutineScope {
        val width = try {
            gridXField.text.toInt()
        } catch (e: Exception) {
            20
        }.let { if (it < 5) 5 else it }
        val height = try {
            gridYField.text.toInt()
        } catch (e: Exception) {
            20
        }.let { if (it < 5) 5 else it }
        val grid = BattleGrid(width, height)
        val battery = Battery(grid, (width * height))
        battery.turrets.addAll(
            listOf(
                battery.Turret(width, height / 2 + 1, 0.5),
                battery.Turret(width, height / 2 - 1, 0.5)
            )
        )
        (0..1).forEach { i ->
            battery.turrets[i].let {
                it.guns.addAll(
                    listOf(
                        it.Gun(20, 5),
                        it.Gun(20, 5)
                    )
                )
            }
        }
        val satellite = Satellite(grid, 1, 3)
        val theatre = Theatre(grid, battery, satellite)
        theatre.randomize()

        val scoreKeeper = ScoreKeeper({
            munitionsLabel.text = it
        }, {
            timeLabel.text = it
        })

        WiringHarness.battleGrid = grid
        WiringHarness.theatre = theatre
        WiringHarness.battery = battery
        WiringHarness.satellite = satellite
        WiringHarness.scoreKeeper = scoreKeeper
    }

    private fun disableActions(value: Boolean) {
        main.lookupAll(".button").forEach { it.isDisable = value }
    }

    fun interface AppEvent {
        fun handle()
    }

    fun onClose() {
        stage.close()
    }

    fun dragWindowStart(event: MouseEvent) {
        xOffset = stage.x - event.screenX;
        yOffset = stage.y - event.screenY;
    }

    fun dragWindowDrag(event: MouseEvent) {
        if (stage.isMaximized) {
            stage.isMaximized = false
            maximizeLabel.text = "\uF0C8"
        }
        stage.x = event.screenX + xOffset;
        stage.y = event.screenY + yOffset;
    }

    fun onMinimize() {
        stage.isIconified = true
    }

    fun onMaximize() {
        if (stage.isMaximized) {
            stage.isMaximized = false
            maximizeLabel.text = "\uF0C8"
        } else {
            stage.isMaximized = true
            maximizeLabel.text = "\uF24D"
        }
    }

    fun onWindowTitleDoubleClick(mouseEvent: MouseEvent) {
        if (mouseEvent.clickCount == 2) {
            onMaximize()
        }
    }

}