package org.astnode.expressionnode

import org.Location
import org.astnode.astnodevisitor.ASTNodeVisitor
import org.astnode.astnodevisitor.VisitorResult

class LiteralNode(
    override val type: String,
    override val location: Location,
    val value: LiteralValue
) : ExpressionNode {
    override fun accept(visitor: ASTNodeVisitor): VisitorResult {
        return visitor.visit(this)
    }

    override fun getType(symbolTable: MutableMap<String, Pair<String, LiteralValue>>): String {
        return value.getType()
    }

    override fun toString(): String {
        return value.toString()
    }
}

sealed class LiteralValue {
    abstract fun getType(): String

    data class StringValue(val value: String) : LiteralValue() {
        override fun getType(): String = "string"
        override fun toString(): String {
            return value
        }
    }

    data class NumberValue(val value: Number) : LiteralValue() {
        override fun getType(): String = "number"
        override fun toString(): String {
            return value.toString()
        }
    }

    data class BooleanValue(val value: Boolean) : LiteralValue() {
        override fun getType(): String = "boolean"
        override fun toString(): String {
            return value.toString()
        }
    }

    data object PromiseValue : LiteralValue() {
        override fun getType(): String = "promise"
        override fun toString(): String {
            return "Promise"
        }
    }

    data object NullValue : LiteralValue() {
        val value = null
        override fun getType(): String = "null"
        override fun toString(): String {
            return "null"
        }
    }
}
