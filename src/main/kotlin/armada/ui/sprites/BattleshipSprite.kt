package armada.ui.sprites

import armada.engine.Orientation
import armada.engine.ships.BattleShip
import armada.utils.ImageUtils.rotate
import armada.utils.ImageUtils.toFXImage
import javax.imageio.ImageIO

object BattleshipSprite : ShipSprite {

    private val base = ImageIO.read(javaClass.getResourceAsStream("/armada/sprites/Battleship/ShipBattleshipHull.png"))
    private val base0 = base.toFXImage()
    private val base90 = base.rotate(90.0).toFXImage()

    private val weapon =
        ImageIO.read(javaClass.getResourceAsStream("/armada/sprites/Battleship/WeaponBattleshipStandardGun.png"))

    override fun meta() = ShipSprite.Meta(
        image0 = base0,
        image90 = base90,
        defaultOrientation = Orientation.vertical,
        baseGridSize = 56.0
    )

    override val engineObject = BattleShip

}
