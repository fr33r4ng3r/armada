package armada.utils

object FormatUtils {

    fun Int.format(digits: Int): String {
        return "%0${digits}d".format(this)
    }
}