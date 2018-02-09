import java.util.*

/**
 * Don't let the machines win. You are humanity's last hope...
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val width = input.nextInt() // the number of cells on the X axis
    val height = input.nextInt() // the number of cells on the Y axis
    if (input.hasNextLine()) {
        input.nextLine()
    }

    val grid = List(height, {input.nextLine()})
            .map {line -> line.map{ if (it == '0') 1 else 0  }}

    for (y in 0 until height) {
        for (x in 0 until width) {

            val node = grid[y][x]
            if (node == 1) {

                var str = "$x $y "
                var hasRight = false
                for (x1 in x+1 until width) {
                    hasRight = if (x1 == width) false else (grid[y][x1] == 1)
                    if (hasRight) {
                        str += "${x1} ${y} "
                        break
                    }

                }
                if (!hasRight) {
                    str += "-1 -1 "
                }

                var hasDown = false
                for (y1 in y+1 until height) {
                    hasDown = if (y1 == height) false else (grid[y1][x] == 1)
                    if (hasDown) {
                        str += "${x} ${y1} "
                        break
                    }

                }
                if (!hasDown) {
                    str += "-1 -1 "
                }

                println(str)

            }

        }

    }

}
