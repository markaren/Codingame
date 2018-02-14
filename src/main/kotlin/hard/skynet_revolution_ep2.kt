import java.util.*


fun main(args : Array<String>) {
    SkynetRevolutionEp2.run(args)
}

object SkynetRevolutionEp2 {

    fun run(args : Array<String>) {
        val input = Scanner(System.`in`)
        val numNodes = input.nextInt() // the total number of nodes in the level, including the gateways
        val numLinks = input.nextInt() // the number of links
        val numGateways = input.nextInt() // the number of exit gateways

        val graph = mutableMapOf<Int, MutableList<Int>>()
        for (i in 0 until numNodes) {
            graph[i] = mutableListOf() //key: Tile, Value: The node's neighbours
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
     * Find the next edge to cut using a Djikstra.
     * Returns an edge once it reaches a goal node.
     *
     * Implementation based on wikipedia pseudocode
     *
     * @start skynet agent
     * @goals exit gateways
     *
     * @return link to sewer
     *
     */
    fun findEdgeToCut(graph: Map<Int, List<Int>>, start: Int, goals: List<Int>) : Pair<Int, Int>? {

        //prioritize link with more neighbours
        fun length(u: Int, v: Int): Double {
            return if (v !in goals)  1.0 else 1.0 / graph[u]!!.size
        }

        val vertexSet = mutableSetOf<Int>()
        val dist = mutableMapOf<Int, Double>()
        val prev = mutableMapOf<Int, Int?>()

        for (v in graph.keys) {
            dist[v] = Double.POSITIVE_INFINITY
            prev[v] = null
            vertexSet.add(v)
        }

        dist[start] = 0.0

        while (!vertexSet.isEmpty()) {
            val u = vertexSet.sortedBy { dist[it] }[0]
            vertexSet.remove(u)

            if (u in goals) {
                return prev[u]!! to u
            }

            for (v in graph[u]!!) {
                val alt = dist[u]!! + length(u,v)
                if (alt < dist[v]!!) {
                    dist[v] = alt
                    prev[v] = u
                }
            }

        }

        return null

    }

}
