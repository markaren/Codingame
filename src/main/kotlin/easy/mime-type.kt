import java.util.*

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val N = input.nextInt() // Number of elements which make up the association table.
    val Q = input.nextInt() // Number Q of file names to be analyzed.
    val map = hashMapOf<String, String>()
    for (i in 0 until N) {
        val EXT = input.next() // file extension
        val MT = input.next() // MIME type.
        map[EXT.toLowerCase()] = MT
    }
    System.err.println(map)
    input.nextLine()
    for (i in 0 until Q) {
        val fileName = input.nextLine() // One file name per line.
        System.err.println(fileName)

        val index = fileName.lastIndexOf('.')
        if (index != -1 && fileName.length > index) {
            val ext = fileName.substring(index+1, fileName.length)
            System.err.println(ext)
            println(map[ext.toLowerCase()] ?: "UNKNOWN")
        } else {
            println("UNKNOWN")
        }

    }

}