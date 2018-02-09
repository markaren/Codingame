import java.util.*

/**
 * The while loop represents the game.
 * Each iteration represents a turn of the game
 * where you are given inputs (the heights of the mountains)
 * and where you have to print an output (the index of the mountain to fire on)
 * The inputs you are given are automatically updated according to your last actions.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)

    // game loop
    while (true) {

        val heights = List(8, {input.nextInt()})

        var highestAt = 0
        for (i in heights.indices) {
            if (heights[i] > heights[highestAt] ) {
                highestAt = i
            }
        }

        println(highestAt)

    }
}