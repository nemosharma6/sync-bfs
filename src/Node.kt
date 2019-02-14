import java.util.ArrayList

class Node(val id: Int, private val links: List<Link>) : Runnable {

    private var thread: Thread? = null
    var round: Int = 0
    var initiate: Boolean = true
    var terminate: Boolean = false
    var parentLink: Link? = null
    var bestMsg: Message? = null
    private var children: MutableList<Int> = mutableListOf()
    var linkMap: MutableMap<Int, Link> = mutableMapOf()
    var waitingForThem: MutableMap<Int, Message> = mutableMapOf()
    var respondToThem: MutableMap<Int, Message> = mutableMapOf()
    private val name = "node-$id"
    var minLevel = Int.MAX_VALUE

    init {
        val sb = StringBuilder()
        sb.append("[")
        links.forEach { l ->
            linkMap[l.linkedTo(id)] = l
            sb.append("$l ")
        }

        sb.deleteCharAt(sb.length - 1)
        sb.append("]")
        Log.write(name, sb.toString())
    }

    fun start(){
        Log.write(name, " round :" + round++)
        thread = Thread(this, id.toString())
        thread!!.start()
    }

    fun join() {
        thread!!.join()
        Log.write(name, " finish round :" + (round - 1))
    }

    fun parent(): String {
        return if (parentLink == null) "Just an Orphan"
        else parentLink!!.linkedTo(id).toString()
    }

    private fun clearAfterResponse(sender: Int) {
        if (waitingForThem.containsKey(sender)) {
            Log.write(name, " Delete processed message - " + waitingForThem[sender])
            waitingForThem.remove(sender)
        }
    }

    private fun anyMsg(): Boolean {
        for (i in linkMap.keys) {
            val msg = linkMap[i]!!.peekMsg(id)
            if (msg != null) return true
        }

        return false
    }

    private fun displayWaitingList() {
        for (e in waitingForThem)
            Log.write(name, "${e.key} - ${e.value}")
    }

    private fun displayToDoList() {
        for (e in respondToThem)
            Log.write(name, "${e.key} - ${e.value}")
    }

    override fun run() {
        if (terminate) return

        if (id == ParseInput.rootProcess) {
            if (initiate) {
                initiate = false
                for (recipient in linkMap.keys) {
                    val explore = Message(MessageType.Exp, id, recipient, 1)
                    waitingForThem[recipient] = explore
                    linkMap[recipient]!!.sendMsg(id, explore)
                }
            } else {
                if (!waitingForThem.isEmpty()) {
                    for (e in linkMap.values) {
                        val sender = e.linkedTo(id)
                        val m = e.readMsg(id)
                        if (m != null) {
                            if (m.type === MessageType.Ack && !children.contains(e.linkedTo(id))) {
                                children.add(e.linkedTo(id))
                                Log.write(name, sender.toString() + " sent me an Ack, he is my newest child.")
                            }
                            clearAfterResponse(sender)
                        }
                    }
                } else
                    terminate = true
            }

            displayToDoList()
            displayWaitingList()

        } else {
            if (initiate) {
                val temp = anyMsg()
                if (temp) {
                    for (e in linkMap.values) {
                        val sender = e.linkedTo(id)
                        val m = e.readMsg(id) ?: continue

                        if (m.type === MessageType.Exp) {
                            respondToThem[sender] = m
                            if (m.level < minLevel) {
                                minLevel = m.level
                                parentLink = e
                                bestMsg = m
                            }
                        }
                    }

                    if (bestMsg == null)
                        println("$name has best msg as null")

                    for (e in linkMap.values) {
                        val sender = e.linkedTo(id)
                        if (respondToThem.containsKey(sender)) continue
                        val childMsg = Message(MessageType.Exp, id, sender, bestMsg!!.level + 1)
                        e.sendMsg(id, childMsg)
                        waitingForThem[sender] = childMsg
                    }

                    initiate = false
                }
            } else {

                for (e in linkMap.values) {
                    val sender = e.linkedTo(id)
                    val m = e.readMsg(id) ?: continue

                    if (m.type === MessageType.Exp) respondToThem[sender] = m

                    if (m.type === MessageType.Ack && !children.contains(e.linkedTo(id))) {
                        children.add(e.linkedTo(id))
                        Log.write(name, sender.toString() + " sent me an Ack, he is my newest child.")
                    }

                    if (m.type === MessageType.Nack)
                        Log.write(name, sender.toString() + " sent me an Nack, he is not my child.")

                    clearAfterResponse(sender)

                }

                // send nacks next
                val toDelete = ArrayList<Int>()
                for (sender in respondToThem.keys) {
                    val m = respondToThem[sender]
                    if (bestMsg != m) {
                        toDelete.add(sender)
                        val nack = Message(MessageType.Nack, m!!.from, m.to, m.level)
                        linkMap[sender]!!.sendMsg(id, nack)
                    }
                }

                for (sender in toDelete) {
                    respondToThem.remove(sender)
                }

                if (waitingForThem.isEmpty()) {
                    if (parentLink != null) {
                        val ack = Message(MessageType.Ack, bestMsg!!.from, bestMsg!!.to, bestMsg!!.level)
                        parentLink!!.sendMsg(id, ack)
                        respondToThem.remove(parentLink!!.linkedTo(id))
                    }

                    terminate = true
                }
            }

            displayToDoList()
            displayWaitingList()
        }
    }

    fun getChildren() : String {
        val sb = StringBuilder()
        for (i in children) {
            sb.append(i)
            sb.append(",")
        }

        if (children.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
        return sb.toString()
    }
}