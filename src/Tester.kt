import org.slf4j.LoggerFactory
import kotlin.time.measureTimedValue

private val logger = LoggerFactory.getLogger("Tester")

internal inline fun <T> go(desc: String, expected: T? = null, op: () -> T) {
    val (result, time) = measureTimedValue { op().toString() }
    logger.info("$desc took $time: ${if ('\n' in result) "\n$result" else result}")
    if (expected != null) check(result == expected.toString()) { "$desc: expected $expected, got $result" }
}

internal fun yearAndQuestFromPackage(x: Any): Pair<Int, Int> = x.javaClass.packageName.run {
    check(contains("ec") && contains("quest")) { "package name `$this` for $x must contain 'ec' and 'quest'" }
    substringAfter("ec").substringBefore(".").toInt() to substringAfter("quest").substringBefore(".").toInt()
}
