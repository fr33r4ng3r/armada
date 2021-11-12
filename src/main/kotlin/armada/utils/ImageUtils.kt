package armada.utils

import armada.utils.ImageUtils.tile
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.AlphaComposite
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.*


object ImageUtils {

    fun BufferedImage.rotate(rotation: Double): BufferedImage {
        val image = this
        val rads = Math.toRadians(rotation)
        val sin = abs(sin(rads))
        val cos = abs(cos(rads))
        val w = floor(image.width * cos + image.height * sin).toInt()
        val h = floor(image.height * cos + image.width * sin).toInt()
        val rotatedImage = BufferedImage(w, h, image.type)
        val at = AffineTransform()
        at.translate((w / 2).toDouble(), (h / 2).toDouble())
        at.rotate(rads, 0.0, 0.0)
        at.translate((-image.width / 2).toDouble(), (-image.height / 2).toDouble())
        val rotateOp = AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
        rotateOp.filter(image, rotatedImage)
        return rotatedImage
    }

    fun BufferedImage.makeSquare(): BufferedImage {
        val image = this
        val n = max(image.width, image.height)
        val dW = n - image.width
        val dH = n - image.height
        val squareImage = BufferedImage(n, n, image.type)
        val at = AffineTransform()
        at.translate(if (dW > 0) dW / 2.0 else 0.0, if (dH > 0) dH / 2.0 else 0.0)
        val rotateOp = AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
        rotateOp.filter(image, squareImage)
        return squareImage
    }

    fun BufferedImage.rotateNormalized(rotation: Double): BufferedImage {
        val image = this
        val rads = Math.toRadians(rotation)
        val radsS = Math.toRadians(45.0)
        val sinS = abs(sin(radsS))
        val cosS = abs(cos(radsS))
        val w = image.width * cosS + image.height * sinS
        val h = image.height * cosS + image.width * sinS
        val sW = image.width / w
        val sH = image.height / h
        val cX = image.width / 2.0
        val cY = image.height / 2.0
        val dW = cX - (image.width * sW) / 2
        val dH = cY - (image.height * sH) / 2
        val rotatedImage = BufferedImage(image.width, image.height, image.type)
        val at = AffineTransform()
        at.scale(sW, sH)
        at.translate(dW, dH)
        at.rotate(rads, cX, cY)
        val rotateOp = AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
        rotateOp.filter(image, rotatedImage)
        return rotatedImage
    }

    fun BufferedImage.scaleToWidth(width: Double): BufferedImage {
        val image = this
        val scaleF = width / image.width
        val height = image.height * scaleF
        val scaledImage = BufferedImage(width.roundToInt(), height.roundToInt(), image.type)
        val at = AffineTransform()
        at.scale(scaleF, scaleF)
        val scaleOp = AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC)
        scaleOp.filter(image, scaledImage)
        return scaledImage
    }

    fun BufferedImage.tile(rows: Int, columns: Int): List<BufferedImage> {
        val image = this
        val w = image.width / columns
        val h = image.height / rows
        val tiles = (0 until rows).flatMap { y ->
            (0 until columns).map { x ->
                val tile = BufferedImage(w, h, image.type)
                val gc = tile.createGraphics()

                val srcLeft = w * x
                val srcTop = h * y

                gc.drawImage(
                    image,
                    0,
                    0,
                    w,
                    h,
                    srcLeft,
                    srcTop,
                    srcLeft + w,
                    srcTop + h,
                    null
                )
                gc.dispose()
                tile
            }
        }
        return tiles
    }

    fun BufferedImage.fade(alpha: Double): BufferedImage {
        val image = this
        val result = BufferedImage(this.width, this.height, image.type)
        val gc = result.createGraphics()
        gc.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha.toFloat())
        gc.drawImage(
            image,
            0,
            0,
            this.width,
            this.height,
            0,
            0,
            this.width,
            this.height,
            null
        )
        gc.dispose()
        return result
    }

    fun BufferedImage.toFXImage(): Image {
        return SwingFXUtils.toFXImage(this, null)
    }


}
