import java.util.concurrent.ArrayBlockingQueue

class Link(val node1: Int, val node2: Int, private val node1Writer: ArrayBlockingQueue<Message> = ArrayBlockingQueue(1),
           private val node2Writer: ArrayBlockingQueue<Message> = ArrayBlockingQueue(1)) {


    fun sendMsg(fr: Int, msg: Message) {
        when(fr){
            node1 -> {
                node1Writer.put(msg)
                Log.write(fr.toString(), msg.toString())
            }
            node2 -> node2Writer.put(msg)
        }
    }

    fun readMsg(reader: Int) : Message? {
        return when(reader){
            node1 -> node2Writer.poll()
            else -> node1Writer.poll()
        }
    }

    fun peekMsg(reader: Int) : Message? {
        return when(reader){
            node1 -> node2Writer.peek()
            else -> node1Writer.peek()
        }
    }

    fun linkedTo(n: Int) : Int {
        return when(n){
            node1 -> node2
            else -> node1
        }
    }

    override fun toString(): String {
        return "( $node1 - $node2 )"
    }

}