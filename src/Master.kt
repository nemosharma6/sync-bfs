import java.util.ArrayList

object Master {

    @JvmStatic
    fun main(args: Array<String>) {
        Log.initialise("output")
        ParseInput.readFile("/Users/nimish/Movies/sync-bfs/src/input.dat")
        val nodes = ArrayList<Node>()

        val linkList = ArrayList<Link>()
        for (i in 0 until ParseInput.processCount) {
            for (j in 0 until ParseInput.processCount) {
                if (ParseInput.adjacencyMatrix[i][j] == 1 && j > i)
                    linkList.add(Link(ParseInput.processId[i], ParseInput.processId[j]))
            }
        }

        for (i in ParseInput.processId.indices) {
            val links = ArrayList<Link>()
            for (e in linkList) {
                if (e.node1 == ParseInput.processId[i] || e.node2 == ParseInput.processId[i])
                    links.add(e)
            }
            nodes.add(Node(ParseInput.processId[i], links))
        }

        var terminate = false

        try {
            while (!terminate) {
                for (node in nodes)
                    node.start()

                for (node in nodes)
                    node.join()

                terminate = true
                for (node in nodes)
                    terminate = terminate && node.terminate
            }
        } catch (e: Exception) {
            Log.write(Master::class.java.name, e.message!!)
        }

        for (node in nodes) {
            Log.write(Master::class.java.name, "Node : " + node.id + ", Parent : " + node.parent() +
                    ", Children : " + node.getChildren())
            println(Master::class.java.name + " : " + node.id + " Parent: " + node.parent() +
                    ", Children: {" + node.getChildren() + "}")
        }
    }
}
