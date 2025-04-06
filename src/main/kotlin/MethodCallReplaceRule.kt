package org.example

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.KtNodeType
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class MethodCallReplaceRule(
    private val from: String,
    private val to: String
) : Rule("method-call-replace-${from.lowercase()}-${to.lowercase()}") {

    override fun visit(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        if (node.elementType is KtNodeType) {
            val callExpression = node.psi as? KtCallExpression ?: return


            if (callExpression.calleeExpression?.text == from) {
                emit(node.startOffset, "Replace `.$from()` with `.$to()`", true)

                if (autoCorrect) {
                    val psiFactory = KtPsiFactory(node.psi.project)
                    val newExpression = psiFactory.createExpression(to)
                    callExpression.calleeExpression?.replace(newExpression)
                }
            }
        }
    }
}