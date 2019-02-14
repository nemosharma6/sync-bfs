import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

object ParseInput {

    var processCount: Int = 0
    var processId: IntArray = IntArray(1)
    var rootProcess: Int = 0
    var adjacencyMatrix: Array<IntArray> = Array(0) { IntArray(0) }

    fun readFile(file: String) {
        val f = File(file)
        if (!f.exists())
            Log.write("ParseInput", "Input File not found")
        else {
            val br = BufferedReader(InputStreamReader(FileInputStream(f)))
            var count = 0
            var line = br.readLine()
            while (line != null) {
                when (count) {
                    0 -> processCount = line.toInt()
                    1 -> {
                        processId = IntArray(processCount)
                        val ids = line.split(" ")
                        for (i in 0..(processCount-1)) processId[i] = ids[i].toInt()
                    }
                    2 -> rootProcess = line.toInt()
                    else -> {
                        if (count == 3) adjacencyMatrix = Array(processCount) { IntArray(processCount) }
                        val ids = line.split(" ")
                        for (i in 0..(processCount-1)) adjacencyMatrix[count - 3][i] = ids[i].toInt()
                    }
                }
                count++
                line = br.readLine()
            }
        }
    }

}