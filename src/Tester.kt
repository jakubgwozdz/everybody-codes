import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Tester")

internal inline fun <T> go(desc: String, expected: T? = null, op: () -> T) {
    val result = op().toString()
    logger.info("$desc: ${if ('\n' in result) "\n$result" else result}")
    if (expected != null) check(result == expected.toString()) { "$desc: expected $expected, got $result" }
}

internal fun yearAndQuestFromPackage(x: Any): Pair<Int, Int> = x.javaClass.packageName.run {
    check(contains("ec") && contains("quest")) { "package name `$this` for $x must contain 'ec' and 'quest'" }
    substringAfter("ec").substringBefore(".").toInt() to substringAfter("quest").substringBefore(".").toInt()
}
