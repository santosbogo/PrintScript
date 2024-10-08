package org

import org.astnode.ASTNode

class ParserResult {
    val errors = mutableListOf<String>()
    var node: ASTNode? = null

    fun addError(message: String) {
        errors.add(message)
    }

    fun hasErrors() = errors.isNotEmpty()
}
