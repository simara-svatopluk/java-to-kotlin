import com.pinterest.ktlint.core.*
import org.assertj.core.api.Assertions.assertThat
import org.example.InterfaceNotNullableRule
import org.example.MethodCallReplaceRule
import kotlin.test.Test

class NoInternalImportRuleTest {

    @Test
    fun `formatted() to format()`() {
        val code = """
            fun formatted(name: String) {
                println("Hello, %".formatted(name))
            }
        """.trimIndent()
        val expected = """
            fun formatted(name: String) {
                println("Hello, %".format(name))
            }
        """.trimIndent()

        assertThat(code.refactor()).isEqualTo(expected)
    }

//    @Test
//    fun `formatted() to format() but only on String`() {
//        val code = """
//            fun formatted(name: String) {
//                val object = Object()
//                println(object.formatted(name))
//            }
//        """.trimIndent()
//        val expected = """
//            fun formatted(name: String) {
//                val object = Object()
//                println(object.formatted(name))
//            }
//        """.trimIndent()
//
//        assertThat(code.refactor()).isEqualTo(expected)
//    }

    @Test
    fun `getFirst() to first()`() {
        val code = """
            fun getFirst(name: String) {
                val getFirst = listOf().getFirst()
            }
        """.trimIndent()
        val expected = """
            fun getFirst(name: String) {
                val getFirst = listOf().first()
            }
        """.trimIndent()

        assertThat(code.refactor()).isEqualTo(expected)
    }

    @Test
    fun `doubleValue() to toDouble()`() {
        val code = """
            fun doubleValue(name: String) {
                BigDecimal.ONE.doubleValue()
            }
        """.trimIndent()
        val expected = """
            fun doubleValue(name: String) {
                BigDecimal.ONE.toDouble()
            }
        """.trimIndent()

        assertThat(code.refactor()).isEqualTo(expected)
    }

    @Test
    fun `interface remove nullalbes`() {
        val code = """
            interface VrbReportCreator {
                fun getPurchases(timeSpan: TimeSpan?): Stream<Purchase?>?

                fun generateVrbReport(creationDate: Instant?, purchases: Stream<Purchase?>?): VrbReport?
                
                fun notNulls(creationDate: Instant, purchases: Stream<Purchase>): VrbReport
            }
        """.trimIndent()
        val expected = """
            interface VrbReportCreator {
                fun getPurchases(timeSpan: TimeSpan): Stream<Purchase>

                fun generateVrbReport(creationDate: Instant, purchases: Stream<Purchase>): VrbReport
                
                fun notNulls(creationDate: Instant, purchases: Stream<Purchase>): VrbReport
            }
        """.trimIndent()

        assertThat(code.refactor()).isEqualTo(expected)
    }

    @Test
    fun `interface @FreeBuilder no changes`() {
        val code = """
            import org.inferred.freebuilder.FreeBuilder;

            @FreeBuilder
            interface Person {
                fun getId(): String?
            }
        """.trimIndent()

        assertThat(code.refactor()).isEqualTo(code)
    }

    private fun String.refactor() = KtLint.format(
        KtLint.ExperimentalParams(
            text = this,
            ruleProviders = setOf(
                RuleProvider { MethodCallReplaceRule("formatted", "format") },
                RuleProvider { MethodCallReplaceRule("getFirst", "first") },
                RuleProvider { MethodCallReplaceRule("doubleValue", "toDouble") },
                RuleProvider { InterfaceNotNullableRule() },
            ),
            cb = { e, corrected -> }
        )
    )
}