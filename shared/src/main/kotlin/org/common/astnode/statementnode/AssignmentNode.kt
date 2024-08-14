package org.common.astnode.statementnode

import org.common.Location
import org.common.astnode.astnodevisitor.ASTNodeVisitor
import org.common.astnode.astnodevisitor.types.VisitorResult
import org.common.astnode.expressionnode.ExpressionNode
import org.common.astnode.expressionnode.IdentifierNode

class AssignmentNode(
    override val type: String,
    override val location: Location,
    val value: ExpressionNode,
    val identifierNode: IdentifierNode
) : StatementNode {
    override fun accept(visitor: ASTNodeVisitor): VisitorResult {
        return visitor.visitAssignmentNode(this)
    }
}
