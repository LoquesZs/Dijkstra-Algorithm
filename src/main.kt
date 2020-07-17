data class Node (var weight: Int? = null, val index: Int)

fun min(pair: Pair<Int?, Int?>): Int? {
    return when {
        pair.first != null && pair.second != null -> if (pair.first!! < pair.second!!) pair.first else pair.second
        pair.first == null && pair.second != null -> pair.second
        pair.first != null && pair.second == null -> pair.first
        else -> null
    }
}

fun sum(weight: Int?, length: Int?): Int? {
    return when {
        weight != null && length != null -> weight+length
        else -> null
    }
}

fun findMinimalWeight(constantNodes: MutableList<Node>, graph: List<Node>, length: Array<Array<Int?>>): MutableList<Node> {
    var minimal: Pair<Int?, Int> // Pair<weight?, index>
    val weights = mutableListOf<Pair<Int?, Int>>()
    for (inner in graph.indices) {
        if (constantNodes.contains(graph[inner]))  {
            continue
        } else {
            val currentNode = constantNodes.last()
            graph[inner].weight = min(Pair(graph[inner].weight, sum(currentNode.weight, length[currentNode.index][inner])))
            minimal = Pair(graph[inner].weight, inner)
            weights.add(minimal)
        }
    }
    weights.sortBy { it.first }
    weights.removeAll { it.first == null }
    constantNodes.add(graph[weights.first().second])
    return constantNodes
}

fun findMinimalWay(constantNodes: MutableList<Node>, length: Array<Array<Int?>>, way: MutableList<Node>, startIndex: Int) {
    var indexOfMin = 0
    val weights: MutableList<Int> = mutableListOf()
    var sum: Int?
    for(inner in constantNodes.indices){
        if(way.contains(constantNodes[inner])){
            continue
        } else {
            sum = sum(constantNodes[inner].weight, length[way.last().index][inner])
            if (sum != null){
                weights.add(sum)
                if(sum <= weights.sortedBy { it }[0]) {
                    indexOfMin = inner
                }
            } else continue
        }
        weights.sortBy { it }
    }
    way.add(constantNodes[indexOfMin])
    if(way.last().index == startIndex) return
    else findMinimalWay(constantNodes, length, way, startIndex)
}

fun setLength(size: Int): Array<Array<Int?>> {
    val length = Array(size){arrayOfNulls<Int?>(size)}
    length[0][4] = 4 ; length[0][6] = 1
    length[1][4] = 2 ; length[1][5] = 8
    length[2][6] = 3 ; length[2][7] = 9
    length[3][5] = 1 ; length[3][7] = 3
    length[4][0] = 4 ; length[4][1] = 2 ; length[4][5] = 7 ; length[4][6] = 2 ; length[4][7] = 2
    length[5][1] = 8 ; length[5][3] = 1 ; length[5][4] = 7 ; length[5][7] = 1
    length[6][0] = 1 ; length[6][2] = 3 ; length[6][4] = 2 ; length[6][7] = 8
    length[7][2] = 9 ; length[7][3] = 3 ; length[7][4] = 2 ; length[7][5] = 1 ; length[7][6] = 8
    return length
}

fun setStartNode(graph: List<Node>, index: Int): MutableList<Node> {
    graph[index].weight = 0
    return mutableListOf(graph[index])
}

fun setEndNode(graph: List<Node>, index: Int): MutableList<Node> {
    return mutableListOf(graph[index])

}

fun String.substringBetween(first: String, second: String): String {
    if (this.contains(first) && this.contains(second)) {
        val before = this.substringBefore(second)
        return before.substringAfter(first)
    } else throw IllegalArgumentException("String does not contain one or all of argument substrings")
}

fun printArray(array: Array<Array<Int?>>) {
    for (innerArray in array){
        for (item in innerArray){
            if (item == null) print("n \t") else print("$item \t")
        }
        println()
    }
}

fun stringToArray(inputString: String): Array<Array<Int?>> {
    val size = inputString[inputString.lastIndex - 3].toString().toInt() + 1
    val array = Array(size){ arrayOfNulls<Int?>(size)}
    for (index in 0..7) {
        when(index) {
            0 -> {
                for (jindex in 0..7) {
                    val list = inputString.substringBefore(" ${index}END").split(" ")
                    array[index][jindex] = list[jindex].toIntOrNull()
                }
            }
            else -> {
                for (jindex in 0..7) {
                    val list = inputString.substringBetween(" ${index-1}END", " ${index}END").split(" ")
                    array[index][jindex] = list[jindex].toIntOrNull()
                }
            }
        }
    }
    return array
}

fun arrayToString(array: Array<Array<Int?>>): String {
    var arrayString = ""
    for (index in 0..7) {
        for(jindex in 0..7) {
            arrayString += if (array[index][jindex] == null) "n "
            else "${array[index][jindex]} "
        }
        arrayString += "${index}END"
    }
    return arrayString
}

fun main() {
    val length = setLength(8)

    val graph = mutableListOf<Node>()

    for (index in length.indices){
        val node = Node(index = index)
        graph.add(node)
    }
    graph.toList()

     // Инициализируем стартовый узел
    val startIndex = 0
    val constantNodes = setStartNode(graph, startIndex)

    for(index in 0 until graph.size-1) {                        // запуск расчёта веса каждого узла
        findMinimalWeight(constantNodes, graph, length)
    }

    val way: MutableList<Node> = setEndNode(graph, 7)          //коллекция хранящая узлы, нашего конечного пути
    findMinimalWay(constantNodes.sortedBy { it.index }.toMutableList(), length, way, startIndex)

    way.reverse()                                               //вывод нашего пути в отформатированную строку
    var wayInString = ""
    for(index in way.indices){
        wayInString += (way[index].index+1).toString()
        if(index != way.size-1){
            wayInString += " -> "
        }
    }
    println("Кратчайший путь: $wayInString")                    //вывод нашего пути в отформатированную строку

    val arrayString = arrayToString(length)                     //конвертация массива в строку для сохранения

    println(arrayString)
    val array = stringToArray(arrayString)                      //конвертация строки в массив
    printArray(array)
}

