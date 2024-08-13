package org.astnodebuilder

import org.astnodebuilder.expressionfactory.PatternFactory
import org.common.Token
import org.shared.astnode.ASTNode
import org.shared.astnode.expressionnode.ExpressionNode
import org.common.astnode.statementnode.PrintStatementNode

class PrintNodeBuilder: ASTNodeBuilder {
    override val formula: String = "PrintToken OpenParenthesisToken ExpressionNode CloseParenthesisToken SemicolonToken"

    override fun generate(tokens: List<Token>): ASTNode {
        return PrintStatementNode(
            type = "PrintStatementNode",
            location = tokens[0].location,
            value = ExpressionNodeBuilder().generate(tokens.subList(2, tokens.size - 2)) as ExpressionNode
        )
    }

    override fun checkFormula(tokensString: String): Boolean {
        val expressionPattern = PatternFactory.getExpressionPattern()
        val pattern = "^PrintToken\\s+OpenParenthesisToken\\s*$expressionPattern\\s*CloseParenthesisToken\\s+SemicolonToken$"
        return Regex(pattern).matches(tokensString)
    }

}