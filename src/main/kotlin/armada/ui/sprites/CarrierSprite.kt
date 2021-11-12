package armada.ui.sprites

import armada.engine.Dimensions
import armada.engine.Orientation
import armada.engine.ships.BattleShip
import armada.engine.ships.Carrier
import armada.utils.ImageUtils.rotate
import armada.utils.ImageUtils.toFXImage
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javax.imageio.ImageIO

object CarrierSprite : ShipSprite {

    private val base = ImageIO.read(javaClass.getResourceAsStream("/armada/sprites/Carrier/ShipCarrierHull.png"))
    private val base0 = base.toFXImage()
    private val base90 = base.rotate(90.0).toFXImage()

    override val dimensions: Dimensions
        get() = Carrier.dimensions

    override fun meta() = ShipSprite.Meta(
        image0 = base0,
        image90 = base90,
        defaultOrientation = Orientation.vertical,
        baseGridSize = 50.0
    )

}