import java.util.*

data class Vector2i(
    var x: Int,
    var y: Int
) {
    override fun toString() = "$x $y"
}

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    var W0 = 0
    var W1 = input.nextInt() // width of the building.
    var H0 = 0
    var H1 = input.nextInt() // height of the building.
    val N = input.nextInt() // maximum number of turns before game over.
    
    val P0 = Vector2i(input.nextInt(), input.nextInt())
    var P = P0.copy()

    // game loop
    while (true) {
        val bombDir = input.next() // the direction of the bombs from batman's current location (U, UR, R, DR, D, DL, L or UL)


        if (bombDir.contains("U")) {
             H1 = P.y
             P.y = ((P.y + H0) / 2 )
        } else if (bombDir.contains("D")) {
             H0 = P.y
             P.y = ((P.y + H1) / 2 )
        }
        
        if (bombDir.contains("L")) {
             W1 = P.x
             P.x = ((P.x + W0) / 2 )
        } else if (bombDir.contains("R")) {
            W0 = P.x
            P.x = ((P.x + W1) / 2 )
        }

        // the location of the next window Batman should jump to.
        println(P)
    }
}