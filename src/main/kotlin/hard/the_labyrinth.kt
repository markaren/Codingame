import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    the_labyrinth.main(args)
}

object the_labyrinth {

    fun moveTowards(start: Tile, end: Tile) {
        when  {
            start.x > end.x ->  println("LEFT")
            start.x < end.x ->  println("RIGHT")
            start.y > end.y ->  println("UP")
            start.y < end.y ->  println("DOWN")
            else -> println("wat")
        }
    }

    fun main(args : Array<String>) {
        val input = Scanner(System.`in`)
        val R = input.nextInt() // number of rows.
        val C = input.nextInt() // number of columns.
        val A = input.nextInt() // number of rounds between the time the alarm countdown is activated and the time the alarm goes off.

        val world = World(C, R)
        val expansion by lazy {
            world.BFSIterator(world.kirkPos.copy()).also { it.next() }
        }

        fun kirkPos() = world.kirkPos
        var start: Tile? = null
        var next: Tile? = null
        var control_room_found = false

        var path = LinkedList<Tile>()
        // game loop
        while (true) {

            world.update(input)

            if (start == null) {
                start = kirkPos().copy()
            }

            if (!control_room_found) {

                world.print(System.err)

                val controlRoom = world.getNeighbors(kirkPos()).find { world.getTileType(it) == TileType.CONTROL_ROOM }
                if (controlRoom != null) {
                    System.err.println("found it")
                    control_room_found = true
                    world.findPathDjikstra(kirkPos(), controlRoom, path).also { path.poll()  }
                } else if (next == null || kirkPos() == next) {
                    next = expansion.next()
                    world.findPathDjikstra(kirkPos(), next, path).also { path.poll()  }
                }

            } else if (path.isEmpty()) {
                System.err.println("Time to backtrack")
                world.findPathDjikstra(kirkPos(), start!!, path).also { path.poll()  }
                System.err.println("current=${kirkPos()}, path=$path)")

                val result = world.map.map { it.toCharArray() }
                path.forEach {
                    result[it.y][it.x] = 'x'
                }.also { result.forEach { System.err.println(it) } }

            }

            moveTowards(kirkPos(), path.poll())

        }
    }

    data class Tile(
            var x: Int,
            var y: Int
    ) {
        fun dist(other: Tile): Int {
            return Math.abs(x-other.x) + Math.abs(y - other.y)
        }
    }

    enum class TileType {
        WALL, HOLLOW, START_POS, CONTROL_ROOM, UNKNOWN
    }

    class World(
            private val width: Int,
            private val height: Int
    ) {

        val map = MutableList(height, { MutableList(width, {'?'})})
        val kirkPos = Tile(0,0)

        fun print(appendable: Appendable) {
            map.forEach { appendable.appendln(it.joinToString("")) }
        }

        fun controlRoomFound(): Tile? {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (map[y][x] == 'C') return Tile(x,y)
                }
            }
            return null
        }

        fun getTileType(x: Int, y: Int): TileType {
            return when (map[y][x]) {
                '?' -> TileType.UNKNOWN
                '.' -> TileType.HOLLOW
                '#' -> TileType.WALL
                'T' -> TileType.START_POS
                'C' -> TileType.CONTROL_ROOM
                else -> throw AssertionError()
            }
        }

        fun getTileType(tile: Tile) = getTileType(tile.x, tile.y)

        fun update(input: Scanner) {
            kirkPos.apply {
                y = input.nextInt()
                x = input.nextInt()
            }
            for (y in 0 until height) {
                for ((x,i) in input.next().withIndex()) {
                    map[y][x] = i
                }
            }

        }

        fun buildGraph(root: Tile): Map<Tile, List<Tile>> {
            return mutableMapOf<Tile, List<Tile>>().apply {
                val bfs = BFSIterator(root)
                while (bfs.hasNext()) {
                    val next = bfs.next()
                    this[next] = getNeighbors(next)
                }
            }
        }

        fun findPathDjikstra(start: Tile, goal: Tile, path: LinkedList<Tile>) {

            System.err.println("calculating path between $start and $goal")

            val graph = buildGraph(start)
            //System.err.println(graph)

            val q = mutableSetOf<Tile>()
            val dist = mutableMapOf<Tile, Double>()
            val prev = mutableMapOf<Tile, Tile?>()

            for (v in graph.keys) {
                dist[v] = Double.POSITIVE_INFINITY
                prev[v] = null
                q.add(v)
            }

            dist[start] = 0.0

            while (!q.isEmpty()) {
                val u = q.sortedBy {dist[it]}[0]
                q.remove(u)
                if (u == goal) {
                    break
                }

                for (v in graph[u]!!) {
                    val alt = dist[u]!! + u.dist(v)
                    if (alt < dist[v]!!) {
                        dist[v] = alt
                        prev[v] = u
                    }
                }

            }

            path.apply {
                var u: Tile? = goal
                while (u != null) {
                    addFirst(u)
                    u = prev[u]
                }
            }

        }

        fun getNeighbors(tile: Tile): List<Tile> {

            fun validTile(x: Int, y: Int): Boolean {
                val tileType = getTileType(x, y)
                return (tileType == TileType.HOLLOW || tileType == TileType.CONTROL_ROOM || tileType == TileType.START_POS)
            }
            fun isValid(x: Int, y: Int): Tile? {
                return (if (x > 0 && y > 0 && x < width && y < height && validTile(x,y)) Tile(x, y) else null)
            }

            return mutableListOf<Tile>().also {
                with(tile) {
                    isValid(x - 1, y)?.apply { it.add(this) }
                    isValid(x + 1, y)?.apply { it.add(this) }
                    isValid(x, y - 1)?.apply { it.add(this) }
                    isValid(x, y + 1)?.apply { it.add(this) }
                }
            }

        }

        inner class BFSIterator(
                private val start: Tile
        ) {

            private val visited = mutableSetOf<Tile>().also {it.add(start)}
            private val queue = LinkedList<Tile>().also { it.add(start) }

            private fun visitNeighbors(tile: Tile) {
                for (neighbor in getNeighbors(tile)) {
                    if (neighbor !in visited) {
                        queue.add(neighbor)
                        visited.add(neighbor)
                    }
                }
            }

            val size
            get() = queue.size

            fun hasNext() = !queue.isEmpty()

            fun next(): Tile {
                val next: Tile = queue.poll()
                visitNeighbors(next)
                return next
            }

        }

    }



}
