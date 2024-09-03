package org.interpretervisitors

import org.astnode.ASTNode
import org.astnode.ProgramNode
import org.astnode.astnodevisitor.VisitorHelper
import org.astnode.astnodevisitor.VisitorResult
import org.astnode.expressionnode.BinaryExpressionNode
import org.astnode.expressionnode.BooleanExpressionNode
import org.astnode.expressionnode.IdentifierNode
import org.astnode.expressionnode.LiteralNode
import org.astnode.expressionnode.LiteralValue
import org.astnode.statementnode.AssignmentNode
import org.astnode.statementnode.CompleteIfNode
import org.astnode.statementnode.IfNode
import org.astnode.statementnode.PrintStatementNode
import org.astnode.statementnode.VariableDeclarationNode

class InterpreterVisitorV11 : InterpreterVisitor {
    private val symbolTable: MutableMap<String, LiteralValue> = mutableMapOf()
    override val printsList: MutableList<String> = mutableListOf()

    override fun visit(node: ASTNode): VisitorResult {
        return when (node) {
            is ProgramNode -> visitProgramNode(node)
            is AssignmentNode -> visitAssignmentNode(node)
            is PrintStatementNode -> visitPrintStatementNode(node)
            is VariableDeclarationNode -> visitVariableDeclarationNode(node)
            is LiteralNode -> visitLiteralNode(node)
            is IdentifierNode -> visitIdentifierNode(node)
            is BinaryExpressionNode -> visitBinaryExpressionNode(node)
            is BooleanExpressionNode -> visitBooleanExpressionNode(node)
            is CompleteIfNode -> visitCompleteIfNode(node)
            is IfNode -> visitIfNode(node)
            else -> throw UnsupportedOperationException("Unsupported node: ${node::class}")
        }
    }

    private fun visitProgramNode(node: ProgramNode): VisitorResult {
        val statements = node.statements
        statements.forEach { it.accept(this) }
        return VisitorResult.MapResult(symbolTable)
    }

    private fun visitAssignmentNode(node: AssignmentNode): VisitorResult {
        val variableIdentifier = node.identifier
        val value = node.value.accept(this) as VisitorResult.LiteralValueResult
        symbolTable[variableIdentifier.name] = value.value
        return VisitorResult.MapResult(symbolTable)
    }

    private fun visitPrintStatementNode(node: PrintStatementNode): VisitorResult {
        val value = node.value.accept(this) as VisitorResult.LiteralValueResult
        when (value.value) {
            is LiteralValue.StringValue -> {
                val stringValue = (value.value as LiteralValue.StringValue).value
                val cleanedStringValue = stringValue
                    .replace("'", "")
                    .replace("\"", "")
                printsList.add(cleanedStringValue)
                println(cleanedStringValue)
            }
            is LiteralValue.NumberValue -> {
                printsList.add((value.value as LiteralValue.NumberValue).value.toString())
                println((value.value as LiteralValue.NumberValue).value)
            }
            is LiteralValue.BooleanValue -> {
                printsList.add((value.value as LiteralValue.BooleanValue).value.toString())
                println((value.value as LiteralValue.BooleanValue).value)
            }
        }
        return VisitorResult.Empty
    }

    private fun visitVariableDeclarationNode(node: VariableDeclarationNode): VisitorResult {
        val variableIdentifier = node.identifier
        val value = node.init.accept(this) as VisitorResult.LiteralValueResult
        symbolTable[variableIdentifier.name] = value.value
        return VisitorResult.MapResult(symbolTable)
    }

    private fun visitLiteralNode(node: LiteralNode): VisitorResult {
        return VisitorResult.LiteralValueResult(node.value)
    }

    private fun visitIdentifierNode(node: IdentifierNode): VisitorResult {
        val value = symbolTable[node.name]
        if (value != null) {
            return when (value) {
                is LiteralValue.StringValue -> VisitorResult.LiteralValueResult(value)
                is LiteralValue.NumberValue -> VisitorResult.LiteralValueResult(value)
                is LiteralValue.BooleanValue -> VisitorResult.LiteralValueResult(value)
            }
        } else {
            throw Exception("Variable ${node.name} not declared")
        }
    }

    private fun visitBinaryExpressionNode(node: BinaryExpressionNode): VisitorResult {
        val leftResult = node.left.accept(this) as VisitorResult.LiteralValueResult
        val rightResult = node.right.accept(this) as VisitorResult.LiteralValueResult

        val leftValue = leftResult.value
        val rightValue = rightResult.value

        val resultLiteralValue: LiteralValue = VisitorHelper().evaluateBinaryExpression(leftValue, rightValue, node.operator)

        return VisitorResult.LiteralValueResult(resultLiteralValue)
    }

    private fun visitBooleanExpressionNode(node: BooleanExpressionNode): VisitorResult {
        val bool = node.bool.accept(this) as VisitorResult.LiteralValueResult
        return VisitorResult.LiteralValueResult(bool.value)
    }

    private fun visitIfNode(node: IfNode): VisitorResult {
        val bool = node.boolean.accept(this) as VisitorResult.LiteralValueResult
        val statements = node.ifStatements
        if (bool.value is LiteralValue.BooleanValue) {
            statements.forEach { it.accept(this) }
        }
        return VisitorResult.Empty
    }

    private fun visitCompleteIfNode(node: CompleteIfNode): VisitorResult {
        val bool = node.ifNode.boolean.accept(this) as VisitorResult.LiteralValueResult
        val ifStatements = node.ifNode.ifStatements
        val elseStatements = node.elseNode?.elseStatements
        if ((bool.value as LiteralValue.BooleanValue).value) {
            ifStatements.forEach { it.accept(this) }
        } else {
            elseStatements?.forEach { it.accept(this) }
        }
        return VisitorResult.Empty
    }
}
