import java.util.*

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val nbFloors = input.nextInt() // number of floors
    val width = input.nextInt() // width of the area
    val nbRounds = input.nextInt() // maximum number of rounds
    val exitFloor = input.nextInt() // floor on which the exit is found
    val exitPos = input.nextInt() // position of the exit on its floor
    val nbTotalClones = input.nextInt() // number of generated clones
    val nbAdditionalElevators = input.nextInt() // ignore (always zero)
    val nbElevators = input.nextInt() // number of elevators

    val map = mutableMapOf(exitFloor to exitPos)
    for (i in 0 until nbElevators) {
        val ef = input.nextInt() // floor on which this elevator is found
        val ep = input.nextInt() // position of the elevator on its floor
        map[ef] = ep
    }

    // game loop
    while (true) {
        val cf = input.nextInt() // floor of the leading clone
        val cp = input.nextInt() // position of the leading clone on its floor
        val dir = input.next() // direction of the leading clone: LEFT or RIGHT

        if (cf == -1 && cp == -1 && dir == "NONE") {
            println("WAIT")
        } else {
            val goal = map[cf]!!
            if(cp < goal && dir == "LEFT" || cp > goal && dir == "RIGHT") {
                println("BLOCK")
            } else {
                println("WAIT")
            }
        }

    }
}