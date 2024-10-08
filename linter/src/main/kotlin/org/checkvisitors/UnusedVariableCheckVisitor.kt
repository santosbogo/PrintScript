package org.checkvisitors

import org.astnode.ASTNode
import org.astnode.astnodevisitor.VisitorResult
import org.astnode.expressionnode.BinaryExpressionNode
import org.astnode.expressionnode.IdentifierNode
import org.astnode.expressionnode.LiteralNode
import org.astnode.statementnode.AssignmentNode
import org.astnode.statementnode.PrintStatementNode
import org.astnode.statementnode.VariableDeclarationNode

class UnusedVariableCheckVisitor : CheckVisitors {
    private val declaredVariables = mutableSetOf<String>()
    private val usedVariables = mutableSetOf<String>()
    override val warnings = mutableListOf<String>()

    override fun visit(node: ASTNode): VisitorResult {
        return when (node) {
            is AssignmentNode -> visitAssignmentNode(node)
            is PrintStatementNode -> visitPrintStatementNode(node)
            is VariableDeclarationNode -> visitVariableDeclarationNode(node)
            is LiteralNode -> visitLiteralNode(node)
            is IdentifierNode -> visitIdentifierNode(node)
            is BinaryExpressionNode -> visitBinaryExpressionNode(node)
            else -> VisitorResult.Empty
        }
    }

    override fun checkWarnings(): VisitorResult {
        // Agarro las variables que fueron declaradas pero nunca usadas.
        val unusedVariables = declaredVariables - usedVariables

        // agrego un warning por cada variable de esas.
        unusedVariables.forEach {
            warnings.add("Variable '$it' is declared but never used.")
        }

        return if (warnings.isNotEmpty()) {
            VisitorResult.ListResult(warnings)
        } else {
            VisitorResult.Empty
        }
    }

    private fun visitAssignmentNode(node: AssignmentNode): VisitorResult {
        usedVariables.add(node.identifier.name)
        return VisitorResult.Empty
    }

    private fun visitPrintStatementNode(node: PrintStatementNode): VisitorResult {
        node.value.accept(this)
        return VisitorResult.Empty
    }

    private fun visitVariableDeclarationNode(node: VariableDeclarationNode): VisitorResult {
        declaredVariables.add(node.identifier.name)
        return VisitorResult.Empty
    }

    private fun visitLiteralNode(node: LiteralNode): VisitorResult {
        return VisitorResult.Empty
    }

    private fun visitBinaryExpressionNode(node: BinaryExpressionNode): VisitorResult {
        node.left.accept(this)
        node.right.accept(this)
        return VisitorResult.Empty
    }

    private fun visitIdentifierNode(node: IdentifierNode): VisitorResult {
        usedVariables.add(node.name)
        return VisitorResult.Empty
    }
}
