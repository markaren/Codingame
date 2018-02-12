import java.util.*

fun main(args: Array<String>) {
    hypersonic.main(args)
}

object hypersonic {

    fun debug(out: Any?) {
        System.err.println(out)
    }

    fun main(args: Array<String>) {
        val input = Scanner(System.`in`)
        val width = input.nextInt()
        val height = input.nextInt()
        val myId = input.nextInt()

        fun moveTo(from: Tile, target: Tile) {
            if (from == target) {
                debug("Not moving")
            } else {
                debug("Moving from $from to $target")
            }
            println("MOVE ${target.x} ${target.y}")
        }

        fun bomb(tile: Tile) {
            println("BOMB ${tile.x} ${tile.y}")
        }

        fun isMyPlayer(state: GameState): Boolean {
            return state.owner == myId && state.entityType == EntityType.PLAYER
        }

        val world = GameWorld(width, height, input)

        val closed = mutableSetOf<Tile>()

        // game loop
        while (true) {

            for (state in world.newRound()) {

                if (isMyPlayer(state)) {
                    val myPos = state.tile
                    world.search2(myPos, TraverseOption.BREADTH_FIRST, {
                        it.getNeighbors(true, 1, {it.isBox() && it !in closed}).isNotEmpty()
                    })?.apply {
                        val availableBombs = state.param1
                        val dist = myPos.manhattanDistance(this)
                        if (availableBombs > 0 && dist <= 2 && (myPos != world.getTile(0,0))) {
                            bomb(myPos)
                            myPos.getNeighbors(true,2, {it.isBox()} ).forEach {
                                closed.add(it)
                            }
                        } else {
                            moveTo(myPos, this)
                        }

                    } ?: moveTo(myPos, myPos)

                }

            }

        }
    }

    data class GameState(
            val entityType: EntityType,
            val owner: Int,
            val tile: Tile,
            val param1: Int,
            val param2: Int
    )

    enum class EntityType(
            private val code: Int
    ) {
        PLAYER(0), BOMB(1), ITEM(2);

        companion object {
            fun valueOf(i: Int): EntityType {
                return values().firstOrNull() { it.code == i } ?: throw IllegalArgumentException()
            }
        }

    }

    data class Tile(
            val x: Int,
            val y: Int,
            val world: GameWorld
    ) {

        fun manhattanDistance(other: Tile): Int {
            return Math.abs(x - other.x) + Math.abs(y - other.y)
        }

        fun isFloor() = world.get(x, y) == '.'
        fun isWall() = world.get(x, y) == 'x' || world.get(x, y) == 'X'

        fun isBox(): Boolean = !isFloor() && !isWall()

        fun getNeighbors(straightLineOnly: Boolean = false, range: Int = 1, predicate: ((Tile) -> Boolean)? = null): List<Tile> {

            fun isValid(x: Int, y: Int): Tile? {
                return if ((x >= 0) && (y >= 0) && (x < world.width) && (y < world.height)) world.getTile(x,y) else null
            }

            return mutableListOf<Tile>().apply {
                for (i in 1 until range+1) {
                    isValid(x - i, y)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                    isValid(x + i, y)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                    isValid(x, y - i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                    isValid(x, y + i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                    if (!straightLineOnly) {
                        isValid(x - i, y + i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                        isValid(x + i, y - i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                        isValid(x - i, y - i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                        isValid(x + i, y + i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                    }
                }
            }
        }

        override fun toString(): String {
            return "Tile(x=$x, y=$y)"
        }


    }

    enum class TraverseOption {
        DEPTH_FIRST, BREADTH_FIRST
    }

    class GameWorld(
            val width: Int,
            val height: Int,
            private val input: Scanner
    ) {

        private val bombs = mutableSetOf<Tile>()
        private val map = mutableListOf<String>()

        fun print(appendable: Appendable) {
            map.forEach { appendable.appendln(it) }
        }

        fun newRound(): List<GameState> {

            map.clear()
            for (i in 0 until height) {
                map.add(input.next())
            }
            //print(System.err)
            return newStates().also {
               // updateBombs(it)
            }
        }

//        fun updateBombs(states: List<GameState>) {
//            states.forEach { state ->
//                if (state.entityType == EntityType.BOMB && state.tile !in bombs) {
//                    bombs.add(state.tile)
//                }
//            }
//
//        }

        fun get(x: Int, y: Int): Char = map[y][x]
        fun getTile(x: Int, y: Int) = Tile(x,y,this)

        fun buildGraph(root: Tile): Map<Tile, List<Tile>> {
            return mutableMapOf<Tile, List<Tile>>().also { map ->
                traverse(root, TraverseOption.DEPTH_FIRST, {map[root] = root.getNeighbors()})
            }
        }

        fun newStates(): List<GameState> {
            val numEntities = input.nextInt()
            return List(numEntities, {GameState(
                    entityType = EntityType.valueOf(input.nextInt()),
                    owner = input.nextInt(),
                    tile = getTile(input.nextInt(), input.nextInt()),
                    param1 = input.nextInt(),
                    param2 = input.nextInt()
            )})
        }

        fun traverse(root: Tile, traverseOption: TraverseOption, traverser: (Tile) -> Unit) {
            val visited = mutableSetOf<Tile>().apply { add(root) }
            val queue = LinkedList<Tile>().apply { add(root) }

            while(!queue.isEmpty()) {
                val u = queue.poll()
                traverser.invoke(u)
                for (neighbor in u.getNeighbors()) {
                    if (neighbor !in visited) {
                        visited.add(neighbor)
                        when (traverseOption) {
                            TraverseOption.BREADTH_FIRST -> queue.add(neighbor)
                            TraverseOption.DEPTH_FIRST -> queue.addFirst(neighbor)
                            else -> throw IllegalArgumentException()
                        }
                    }
                }
            }
        }

        fun search(root: Tile, traverseOption: TraverseOption, predicate: (Tile) -> Boolean): Tile? {

            val visited = mutableSetOf<Tile>().apply { add(root) }
            val queue = LinkedList<Tile>().apply { add(root) }

            while(!queue.isEmpty()) {
                val u = queue.poll()
                if (predicate.invoke(u)) {
                    return u
                }
                val neighbors = u.getNeighbors()
                for (neighbor in neighbors) {
                    if (neighbor !in visited) {
                        visited.add(neighbor)
                        when (traverseOption) {
                            TraverseOption.BREADTH_FIRST -> queue.add(neighbor)
                            TraverseOption.DEPTH_FIRST -> queue.addFirst(neighbor)
                            else -> throw IllegalArgumentException()
                        }
                    }
                }
            }
            return null
        }

        fun search2(root: Tile, traverseOption: TraverseOption, predicate: (Tile) -> Boolean): Tile? {

            val visited = mutableSetOf<Tile>().apply { add(root) }
            val queue = LinkedList<Tile>().apply { add(root) }

            while(!queue.isEmpty()) {
                val u = queue.poll()
                if (predicate.invoke(u)) {
                    return u
                }
                val neighbors = u.getNeighbors(true, 1, {it.isFloor()})
                for (neighbor in neighbors) {
                    if (neighbor !in visited) {
                        visited.add(neighbor)
                        when (traverseOption) {
                            TraverseOption.BREADTH_FIRST -> queue.add(neighbor)
                            TraverseOption.DEPTH_FIRST -> queue.addFirst(neighbor)
                            else -> throw IllegalArgumentException()
                        }
                    }
                }
            }
            return null
        }

    }



}
