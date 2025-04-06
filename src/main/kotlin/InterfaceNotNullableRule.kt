package org.example

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.stubs.elements.KtClassElementType
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

class InterfaceNotNullableRule : Rule("remove-nullables") {

    override fun visit(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        if (node.elementType is KtClassElementType) {
            val interfaceDeclaration = node.psi as? KtClass ?: return
            if (!interfaceDeclaration.isInterface()) return

            interfaceDeclaration.annotationEntries.any {
                it.text == "@FreeBuilder"
            }.ifTrue { return }

            val factory = KtPsiFactory(interfaceDeclaration.project)

            interfaceDeclaration.declarations
                .filterIsInstance<KtNamedFunction>()
                .forEach { declaration ->
                    emit(
                        node.startOffset,
                        "Remove nullable from interface ${interfaceDeclaration.name} ${declaration.text}",
                        true
                    )

                    if (autoCorrect) {
                        val newFunction = declaration.text.replace("?", "")
                        val newDeclaration = factory.createFunction(newFunction)
                        declaration.replace(newDeclaration)
                    }
                }
        }
    }

}