package org.parser.astnode

import org.Location

class VariableDeclaration(
    private val type: String,
    private val location: Location,
    private val kind: String,
    private val declarators: ArrayList<VariableDeclarator>
) : ASTNode {

}