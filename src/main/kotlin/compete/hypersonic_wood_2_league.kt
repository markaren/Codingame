import java.util.*

fun main(args: Array<String>) {
    hypersonic_wood_2_league.main(args)
}

object hypersonic_wood_2_league {

    const val WIDTH = 13
    const val HEIGHT = 11

    val closed = mutableSetOf<Tile>()
    var bombPlaced = false
    var myBombCountdown: Int = -1
    var movingTo: Tile? = null
    var explosionRange: Int = 1

    lateinit var world: World
    lateinit var gameInfo: List<EntityInfo>

    fun moveTo(from: Tile, target: Tile) {
        if (from == target) {
            System.err.println("Not moving")
        } else {
            System.err.println("Moving from $from to $target")
        }
        println("MOVE ${target.x} ${target.y}")
        movingTo = target
    }

    fun bomb(tile: Tile) {
        println("BOMB ${tile.x} ${tile.y}")
        bombPlaced = true
        myBombCountdown = 8
    }

    fun isSetToExplode(tile: Tile): Boolean {
        var bombTiles: List<Tile> = gameInfo.mapNotNull { if (it.entityType == 1) it.tile else null }
        return bombTiles.firstOrNull{
            return false//((Math.abs(tile.x - it.x) < explosionRange && tile.y == it.y) || (Math.abs(tile.y - it.y) < explosionRange && tile.x == it.x))
        }.let { it != null }
    }

    /**
     * Auto-generated code below aims at helping you parse
     * the standard input according to the problem statement.
     **/
    fun main(args: Array<String>) {
        val input = Scanner(System.`in`)
        val width = input.nextInt()
        val height = input.nextInt()
        val myId = input.nextInt()

        GameInputProvider(input).also { provider ->

            // game loop
            while (true) {

                var bombJustWentOff = false
                world = World(List(height, { input.next() }))

                if (bombPlaced) {
                    myBombCountdown--
                    if (myBombCountdown == 0) {
                        System.err.println("ka-boom")
                        bombPlaced = false
                        bombJustWentOff = true
                    } else {
                        System.err.println("Bomb will explode in $myBombCountdown rounds")
                    }
                }

                fun isUser(it: EntityInfo) = (it.entityType == 0 && it.owner == myId)
                provider.newInstance().firstOrNull{isUser(it)}?.apply {

                    world.findNextBox(tile)?.also { box ->

                        System.err.println("Closest box to $tile is $box")
                        if (movingTo == null || bombPlaced || bombJustWentOff) {
                            moveTo(tile, box)
                        } else if (movingTo!!.manhattanDistance(tile) > 1) {
                            moveTo(tile, box)
                        } else {
                            bomb(tile)//.also { boxesNotToConsider.add(box) }
                        }

                    } ?: moveTo(tile, tile)
                }

            }

        }

    }

    data class EntityInfo(
            val entityType: Int,
            val owner: Int,
            val tile: Tile,
            val param1: Int,
            val param2: Int
    )

    class GameInputProvider(
            val input: Scanner
    ) {

        fun newInstance() : List<EntityInfo> {
            return List(input.nextInt(), { EntityInfo(
                        entityType = input.nextInt(),
                        owner = input.nextInt(),
                        tile = Tile(input.nextInt(), input.nextInt()),
                        param1 = input.nextInt(),
                        param2 = input.nextInt())
            })
        }

    }

    enum class TileType {
        FLOOR, BOX, EXTRA_RANGE_ITEM, EXTRA_BOMB_ITEM
    }

    data class Tile(
            val x: Int,
            val y: Int
    ) {

        fun getTileType(world: World): TileType {
            return when(world[y][x]) {
                '.' -> TileType.FLOOR
                '0' -> TileType.BOX
                '1' -> TileType.EXTRA_RANGE_ITEM
                '2' -> TileType.EXTRA_BOMB_ITEM
                else -> throw IllegalStateException()
            }
        }

        fun manhattanDistance(other: Tile): Int {
            return Math.abs(x - other.x) + Math.abs(y - other.y)
        }

        fun getNeighbors(straightLine: Boolean = false): List<Tile> {

            fun isValid(x: Int, y: Int): Tile? {
                return if ((x > 0) && (y > 0) && (x < WIDTH) && (y < HEIGHT)) Tile(x, y) else null
            }

            return mutableListOf<Tile>().apply {
                isValid(x - 1, y)?.also { add(it) }
                isValid(x + 1, y)?.also { add(it) }
                isValid(x, y - 1)?.also { add(it) }
                isValid(x, y + 1)?.also { add(it) }
                if (!straightLine) {
                    isValid(x - 1, y + 1)?.also { add(it) }
                    isValid(x + 1, y - 1)?.also { add(it) }
                    isValid(x - 1, y - 1)?.also { add(it) }
                    isValid(x + 1, y + 1)?.also { add(it) }
                }
            }

        }

    }

    class World(
            private val rows: List<String>
    ) : List<String> by rows {

        fun print(out: Appendable) {
            rows.forEach { out.appendln(it) }
        }

        fun bft(root: Tile, traverser: (Tile) -> Unit) {

            val visited = mutableSetOf<Tile>().apply { add(root) }
            val queue = LinkedList<Tile>().apply { add(root) }

            while(!queue.isEmpty()) {
                val u = queue.poll()
                traverser.invoke(u)
                for (v in u.getNeighbors()) {
                    if (v !in visited) {
                        visited.add(v)
                        queue.add(v)
                    }
                }
            }

        }

        fun buildGraph(root: Tile): Map<Tile, List<Tile>> {
            return mutableMapOf<Tile, List<Tile>>().apply {
                bft(root, {put(it, it.getNeighbors())})
            }
        }

        fun findNextBox(start: Tile): Tile? {

//            val graph = buildGraph(start)
//
//            val q = mutableSetOf<Tile>()
//            val dist = mutableMapOf<Tile, Double>()
//            val prev = mutableMapOf<Tile, Tile?>()
//
//            for (v in graph.keys) {
//                dist[v] = Double.POSITIVE_INFINITY
//                prev[v] = null
//                q.add(v)
//            }
//
//            dist[start] = 0.0
//
//            while (!q.isEmpty()) {
//                val u = q.sortedBy {dist[it]}[0]
//                q.remove(u)
//
//                for (v in graph[u]!!) {
//                    val alt = dist[u]!! + u.manhattanDistance(v)
//                    if (alt < dist[v]!!) {
//                        dist[v] = alt
//                        prev[v] = u
//                    }
//                }
//
//            }

            val visited = mutableSetOf<Tile>().also { it.add(start) }
            val queue = LinkedList<Tile>().also { it.add(start) }

            while (!queue.isEmpty()) {
                val current: Tile = queue.poll()

                if (current !in closed && current.getTileType(this) == TileType.BOX) {
                    return current
                }

                for (neighbor in current.getNeighbors()) {
                    if (neighbor !in visited) { //ignore previously visited nodes
                        visited.add(neighbor)
                        queue.add(neighbor)
                    }
                }

            }

            System.err.println("no goal found")

            return null //no goal found
        }

    }
}


