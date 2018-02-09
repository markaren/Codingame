import java.util.*

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val N = input.nextInt()
    val P = List(N, {input.nextInt()}).sorted()

    var closest: Int? = null
    for (i in 1 until N-1) {
        val diff = Math.abs(P[i-1] - P[i])
        if (closest == null || diff < closest) {
            closest = diff
        }
    }

    println(closest!!)
}