import java.util.*

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val n =  input.nextInt()// the number of temperatures to analyse

    if (n == 0) {
        println(0)
    } else {

        val temperatures = List(n, {input.nextInt()})
        System.err.println(temperatures)


        val closestToZero = temperatures.reduce({x,y ->
            if (Math.abs(x) == Math.abs(y)) {
                if (x > y) x else y
            } else {
                if (Math.abs(y) > Math.abs(x)) x else y
            }
        })

        println(closestToZero)

    }

}