import java.util.*


fun main(args : Array<String>) {
    SkynetRevolutionEp1.main(args)
}

object SkynetRevolutionEp1 {

    fun main(args : Array<String>) {

        val input = Scanner(System.`in`)
        val numNodes = input.nextInt() // the total number of nodes in the level, including the gateways
        val numLinks = input.nextInt() // the number of links
        val numGateways = input.nextInt() // the number of exit gateways

        val graph = mutableMapOf<Int, MutableList<Int>>() //key: Node, Value: A list of the node's neighbours
        for (i in 0 until numNodes) {
            graph[i] = mutableListOf()
        }

        for (i in 0 until numLinks) {
            val src = input.nextInt()
            val dst = input.nextInt()

            //assign neighbours to nodes
            graph[src]!!.add(dst)
            graph[dst]!!.add(src)
        }

        val gateways = List(numGateways, {input.nextInt()})
        // game loop
        while (true) {
            val agentNode = input.nextInt() // The index of the node on which the Skynet agent is positioned this turn

            val (src, dst) = findEdgeToCut(graph, agentNode, gateways)!! //locate edge
            println("$src $dst") //print answer

            //severed links can be removed
            graph[src]!!.remove(dst)
            graph[dst]!!.remove(src)

        }
    }

    /**
     * Find the next edge to cut using a Breadth First Search
     * Returns an edge once it reaches a goal node.
     *
     * @start skynet agent
     * @goals exit gateways
     *
     */
    fun findEdgeToCut(graph: Map<Int, List<Int>>, start: Int, goals: List<Int>) : Pair<Int, Int>? {

        val visited = mutableSetOf<Int>().also { it.add(start) }
        val queue = LinkedList<Int>().also { it.add(start) }

        val prev = mutableMapOf<Int, Int>() //allows us to backstep once a solution has been found

        while (!queue.isEmpty()) {
            val current = queue.poll()

            if (current in goals) {
                return prev[current]!! to current //return the link leading to the goal
            }

            for (neighbour in graph[current]!!) {
                if (neighbour !in visited) { //ignore previously visited nodes
                    visited.add(neighbour)
                    queue.add(neighbour)
                    prev[neighbour] = current
                }
            }

        }
        return null //no goal found

    }


}
