import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object Log {

    private var logFile: String = ""
    private var fileWriter: FileWriter? = null
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS")

    private fun time() : String = dateTimeFormat.format(Date())

    fun initialise(file: String) {
        logFile = file
        fileWriter = FileWriter(file)
    }

    fun write(node: String, msg: String) {
        if (fileWriter != null) {
            fileWriter?.write(time() + " - " + node + " : " + msg + "\n")
            fileWriter?.flush()
        }
    }

}