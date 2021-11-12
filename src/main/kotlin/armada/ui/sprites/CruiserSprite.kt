package armada.ui.sprites

import armada.engine.Orientation
import armada.engine.ships.Cruiser
import armada.utils.ImageUtils.rotate
import armada.utils.ImageUtils.toFXImage
import javax.imageio.ImageIO

object CruiserSprite : ShipSprite {

    private val base = ImageIO.read(javaClass.getResourceAsStream("/armada/sprites/Cruiser/ShipCruiserHull.png"))
    private val base0 = base.toFXImage()
    private val base90 = base.rotate(90.0).toFXImage()

    private val weapon =
        ImageIO.read(javaClass.getResourceAsStream("/armada/sprites/Cruiser/WeaponCruiserStandardSTSM.png"))

    override fun meta() = ShipSprite.Meta(
        image0 = base0,
        image90 = base90,
        defaultOrientation = Orientation.vertical,
        baseGridSize = 45.0
    )

    override val engineObject = Cruiser

}
