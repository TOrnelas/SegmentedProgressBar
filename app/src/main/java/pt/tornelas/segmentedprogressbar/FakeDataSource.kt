package pt.tornelas.segmentedprogressbar

/**
 * Returns a list with random size between 1 and 15
 * With Page {index} strings
 */
fun dataSource(): List<Int>{
    return List((1..15).random()){ index -> index }
}