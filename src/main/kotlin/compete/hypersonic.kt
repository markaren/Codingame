import java.util.*

fun main(args: Array<String>) {
    hypersonic.main(args)
}

object hypersonic {

    /**
     * Easier debug printing
     */
    private fun debug(out: Any?) {
        System.err.println(out)
    }

    fun main(args: Array<String>) {
        val input = Scanner(System.`in`)
        val width = input.nextInt()
        val height = input.nextInt()
        val myId = input.nextInt()

        /**
         * Tell agent to move
         * @from Current position (used for debugging purpose only)
         * @target the tile we want to move towards
         */
        fun moveTo(from: Tile, target: Tile) {
            if (from == target) {
                debug("Not moving")
            } else {
                debug("Moving from $from to $target")
            }
            println("MOVE ${target.x} ${target.y}")
        }

        /**
         * Tell the agent to plant a bomb at the current location, and then move to the provided tile
         * @tile tile to move to after bomb has been planted
         */
        fun bomb(tile: Tile) {
            println("BOMB ${tile.x} ${tile.y}")
        }

        fun isMyPlayer(state: EntityState): Boolean {
            return state.owner == myId && state.entityType == EntityType.PLAYER
        }

        val world = GameWorld(width, height, input)

        // game loop
        while (true) {

            for (state in world.newRound()) {

                //I only care about my own agent state
                if (isMyPlayer(state)) {
                    val myPos = state.tile
                    world.search(myPos, TraverseOption.BREADTH_FIRST, { floorTile ->
                        //does this floor tile have a neighbouring box that is not about to explode?
                        floorTile.getNeighbors(straightLineOnly = true, range = 1, predicate = {it.isBox() && it !in world.boxesNotToConsider }).isNotEmpty()
                    })?.also { tileIWantToBomb ->
                        val availableBombs = state.param1
                        val dist = myPos.manhattanDistance(tileIWantToBomb)
                        if (availableBombs > 0 && dist == 0) {
                            bomb(myPos) //reached bomb location. Fire away
                        } else {
                            moveTo(myPos, tileIWantToBomb) //move to tile we want to bomb
                        }
                    } ?: moveTo(myPos, myPos) //in case we don't find a box to bomb. Do nothing

                }

            }

        }

    }

    /**
     * Represents the state of an entity
     */
    data class EntityState(
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

    /**
     * Represents a tile in the game world
     */
    data class Tile(
            val x: Int,
            val y: Int,
            private val world: GameWorld
    ) {

        /**
         * Calculates the manhattan distance between this and the other tile
         */
        fun manhattanDistance(other: Tile): Int {
            return Math.abs(x - other.x) + Math.abs(y - other.y)
        }

        fun isFloor() = world.get(x, y) == '.'
        fun isWall() = world.get(x, y) == 'x' || world.get(x, y) == 'X'
        fun isBox(): Boolean = !isFloor() && !isWall()

        private fun isWithinBounds(x: Int, y: Int): Boolean {
            return ((x >= 0) && (y >= 0) && (x < world.width) && (y < world.height))
        }

        private fun createTileIfWithinBounds(x: Int, y: Int): Tile? {
            return if (isWithinBounds(x, y)) world.getTile(x,y) else null
        }

        fun getNeighbors(straightLineOnly: Boolean = false, range: Int = 1, predicate: ((Tile) -> Boolean)? = null): List<Tile> {
            return mutableListOf<Tile>().apply {
                for (i in 1 until range+1) {
                    createTileIfWithinBounds(x - i, y)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                    createTileIfWithinBounds(x + i, y)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                    createTileIfWithinBounds(x, y - i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                    createTileIfWithinBounds(x, y + i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                    if (!straightLineOnly) {
                        createTileIfWithinBounds(x - i, y + i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                        createTileIfWithinBounds(x + i, y - i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                        createTileIfWithinBounds(x - i, y - i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
                        createTileIfWithinBounds(x + i, y + i)?.also { predicate?.apply { if (invoke(it)) add(it) } ?: add(it) }
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

    /**
     * Represents the game world
     */
    class GameWorld(
            val width: Int,
            val height: Int,
            private val input: Scanner
    ) {

        val boxesNotToConsider = mutableSetOf<Tile>()
        private val map = mutableListOf<String>()

        fun print(appendable: Appendable) = map.forEach { appendable.appendln(it) }

        fun get(x: Int, y: Int): Char = map[y][x]

        fun getTile(x: Int, y: Int) = Tile(x,y,this)

        fun newRound(): List<EntityState> {

            map.clear()
            for (i in 0 until height) {
                map.add(input.next())
            }
            return newStates().also {
                it.filter { it.entityType == EntityType.BOMB }.forEach {
                    it.tile.getNeighbors(true,2, {it.isBox()} ).forEach {
                        boxesNotToConsider.add(it)
                    }
                }
            }
        }

        private fun newStates(): List<EntityState> {
            val numEntities = input.nextInt()
            return List(numEntities, {
                EntityState(
                    entityType = EntityType.valueOf(input.nextInt()),
                    owner = input.nextInt(),
                    tile = getTile(input.nextInt(), input.nextInt()),
                    param1 = input.nextInt(),
                    param2 = input.nextInt()
            )})
        }

        fun buildGraph(root: Tile): Map<Tile, List<Tile>> {
            return mutableMapOf<Tile, List<Tile>>().also { map ->
                traverse(root, TraverseOption.DEPTH_FIRST, {map[root] = root.getNeighbors()})
            }
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

//        fun search(root: Tile, traverseOption: TraverseOption, predicate: (Tile) -> Boolean): Tile? {
//
//            val visited = mutableSetOf<Tile>().apply { add(root) }
//            val queue = LinkedList<Tile>().apply { add(root) }
//
//            while(!queue.isEmpty()) {
//                val u = queue.poll()
//                if (predicate.invoke(u)) {
//                    return u
//                }
//                val neighbors = u.getNeighbors()
//                for (neighbor in neighbors) {
//                    if (neighbor !in visited) {
//                        visited.add(neighbor)
//                        when (traverseOption) {
//                            TraverseOption.BREADTH_FIRST -> queue.add(neighbor)
//                            TraverseOption.DEPTH_FIRST -> queue.addFirst(neighbor)
//                            else -> throw IllegalArgumentException()
//                        }
//                    }
//                }
//            }
//            return null
//        }

        fun search(root: Tile, traverseOption: TraverseOption, predicate: (Tile) -> Boolean): Tile? {

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
