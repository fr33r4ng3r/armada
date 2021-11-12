package armada.process

import java.io.File
import java.io.IOException
import java.util.*

object JavaProcess {
    @Throws(IOException::class, InterruptedException::class)
    fun exec(klass: Class<*>, args: List<String>?): Int {
        val javaHome = System.getProperty("java.home")
        val javaBin = "$javaHome${File.separator}bin${File.separator}java"
        val classpath = System.getProperty("java.class.path")
        val className = klass.name
        val command: MutableList<String> = LinkedList()
        command.add(javaBin)
        command.add("-cp")
        command.add(classpath)
        command.add(className)
        if (args != null) {
            command.addAll(args)
        }
        val builder = ProcessBuilder(command)
        val process = builder.inheritIO().start()
        process.waitFor()
        return process.exitValue()
    }
}