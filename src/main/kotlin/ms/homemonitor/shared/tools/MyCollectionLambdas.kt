package ms.homemonitor.shared.tools

fun <T> Iterable<T>.splitByCondition(predicate: (T) -> Boolean): List<List<T>> {
    val result = ArrayList<List<T>>()
    var tmp = mutableListOf<T>()
    this.forEach {
        if (predicate(it)) {
            result.add(tmp)
            tmp = mutableListOf(it)
        } else {
            tmp.add(it)
        }
    }
    result.add(tmp)
    return result
}
