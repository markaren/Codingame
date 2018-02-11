import java.util.*

fun main(args: Array<String>) {
    hypersonic_wood_3_league.main(args)
}

object hypersonic_wood_3_league {

    const val WIDTH = 13
    const val HEIGHT = 11

    val closed = mutableSetOf<Tile>()

    /**
     * Auto-generated code below aims at helping you parse
     * the standard input according to the problem statement.
     **/
    @JvmStatic
    fun main(args: Array<String>) {
        val input = Scanner(System.`in`)
        val width = input.nextInt()
        val height = input.nextInt()
        val myId = input.nextInt()

        var bombPlaced = false
        var myBombCountdown: Int = -1
        var movingTo: Tile? = null

        fun moveTo(tile: Tile) {
            println("MOVE ${tile.x} ${tile.y}")
            movingTo = tile
        }

        fun bomb(tile: Tile) {
            println("BOMB ${tile.x} ${tile.y}")
            bombPlaced = true
            myBombCountdown = 8
        }

        // game loop
        while (true) {
            val world = World(List(height, { input.next() }))
            //world.print(System.err)

            var bombJustWentOff = false

            if (bombPlaced) {
                myBombCountdown--
                if (myBombCountdown == 0) {
                    System.err.println("ka-boom")
                    bombPlaced = false
                    bombJustWentOff = true
                } else {
                    System.err.println("Bomb will explode in ${myBombCountdown} rounds")
                }
            }

            val entities = input.nextInt()
            for (i in 0 until entities) {
                val entityType = input.nextInt()
                val owner = input.nextInt()
                val tile = Tile(input.nextInt(), input.nextInt())
                val (param1, param2) = input.nextInt() to input.nextInt()

                if (entityType == 0 && owner == myId) {
                    world.findClosestBoxTo(tile)?.also { closest ->

                        System.err.println("Closest box to $tile is $closest")
                        movingTo?.also { System.err.println("Currently at $tile, moving to $movingTo") }

                        if (movingTo == null || bombPlaced || bombJustWentOff) {
                            moveTo(closest)
                        } else if (closest!!.dist(tile) > 1) {
                            moveTo(closest)
                        } else {
                            bomb(tile)
                            closed.add(closest)
                        }

                    } ?: moveTo(tile)
                }
            }

        }

    }


    data class Tile(
            val x: Int,
            val y: Int
    ) {

        fun dist(other: Tile): Int {
            return Math.abs(x - other.x) + Math.abs(y - other.y)
        }

        fun getNeighbors(): List<Tile> {

            fun isValid(x: Int, y: Int): Tile? {
                return if ((x < 0) || (y < 0) || (x >= hypersonic_wood_3_league.WIDTH) || (y >= hypersonic_wood_3_league.HEIGHT)) null else Tile(x, y)
            }

            return mutableListOf<Tile>().apply {
                isValid(x - 1, y)?.also { add(it) }
                isValid(x + 1, y)?.also { add(it) }
                isValid(x, y - 1)?.also { add(it) }
                isValid(x, y + 1)?.also { add(it) }
                isValid(x - 1, y + 1)?.also { add(it) }
                isValid(x + 1, y - 1)?.also { add(it) }
                isValid(x - 1, y - 1)?.also { add(it) }
                isValid(x + 1, y + 1)?.also { add(it) }
            }

        }

    }

    class World(
            private val rows: List<String>
    ) {

        fun isFloor(tile: Tile) = rows[tile.y][tile.x] == '.'
        fun isBox(tile: Tile) = rows[tile.y][tile.x] == '0'

        fun print(out: Appendable) {
            rows.forEach { out.appendln(it) }
        }

        fun findClosestBoxTo(start: Tile): Tile? {
            val visited = mutableSetOf<Tile>().also { it.add(start) }
            val queue = LinkedList<Tile>().also { it.add(start) }

            while (!queue.isEmpty()) {
                val current: Tile = queue.poll()

                if (current !in hypersonic_wood_3_league.closed && isBox(current)) {
                    return current
                }

                for (neighbor in current.getNeighbors()) {
                    if (neighbor !in visited) { //ignore previously visited nodes
                        visited.add(neighbor)
                        queue.add(neighbor)
                    }
                }

            }
            return null //no goal found
        }

    }

}





