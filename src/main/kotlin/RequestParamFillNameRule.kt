package org.example

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.stubs.elements.KtClassElementType
import org.jetbrains.kotlin.psi.stubs.elements.KtFunctionElementType
import org.jetbrains.kotlin.psi.stubs.elements.KtParameterElementType
import org.jetbrains.kotlin.psi.stubs.elements.KtValueArgumentElementType
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance
import org.jetbrains.kotlin.utils.addToStdlib.ifFalse
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

class RequestParamFillNameRule : Rule("request-param-fill-name") {

    override fun visit(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        if (node.elementType is KtParameterElementType) {
            val declaration = node.psi as? KtParameter ?: return

            declaration.annotationEntries
                .filter { it.shortName?.asString() == "RequestParam"}
                .forEach { annotation ->
                    annotation.valueArguments
                        .map { it as KtValueArgument }
                        .any {it.isNamed() && it.getArgumentName()?.text == "name"}
                        .ifTrue { return@forEach }

                    annotation.valueArguments
                        .firstOrNull()
                        ?.let { it as KtValueArgument }
                        ?.let {
                            it.isNamed()
                                .ifFalse {
                                    return@forEach
                                }
                        }

                    val parameterName = declaration.nameIdentifier?.text ?: return@forEach

                    emit(
                        node.startOffset,
                        "Add paramenter name $parameterName to ${annotation.text}",
                        true
                    )

                    if (autoCorrect) {
                        val factory = KtPsiFactory(annotation.project)

                        val newDeclaration = factory.createArgument("\"$parameterName\"")

                        val firstArgument = annotation.valueArguments.firstOrNull() as? KtValueArgument

                        annotation.children
                            .firstIsInstance<KtValueArgumentList>()
                            .addArgumentBefore(newDeclaration, firstArgument)
                    }
            }
        }
    }

}