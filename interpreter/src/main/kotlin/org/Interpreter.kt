package org

import org.astnode.ASTNode
import org.interpretervisitors.InterpreterVisitor

class Interpreter(private val visitor: InterpreterVisitor, private val nodeIterator: Iterator<ASTNode>) {
    fun interpret(): InterpreterResult {
        while (nodeIterator.hasNext()) {
            try {
                val node = nodeIterator.next()
                visitor.visit(node)
            } catch (e: Exception) {
                return InterpreterResult(emptyList(), listOf(e.message ?: "Unknown error"))
            }
        }
        // El printer dentro del visitor tiene el output. cuando itero lo hago con el mismo visitor.
        return InterpreterResult(visitor.printer.getOutput(), emptyList())
    }
}
