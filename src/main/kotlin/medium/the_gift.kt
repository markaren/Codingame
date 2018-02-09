import java.util.*

const val IMPOSSIBLE = "IMPOSSIBLE"

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val N = input.nextInt() //num
    var C = input.nextInt() //price

    val B = IntArray(N, {input.nextInt()}).apply{
        sortDescending()
        reverse()
    }
    if (B.sum() < C) {
        println(IMPOSSIBLE)
    } else {

        for ((i,b) in B.withIndex()) {

            val p = C / (N-i)
            val m = Math.min(b, p)

            println(m)
            C -= m

        }

    }


}