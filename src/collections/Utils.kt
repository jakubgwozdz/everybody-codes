package collections

fun <E> List<List<E>>.transposed(defaultOp: (Int) -> E = { error("No default provided") }): List<List<E>> {
    return first().indices.map { j -> map { it.getOrElse(j, defaultOp) } }
}

