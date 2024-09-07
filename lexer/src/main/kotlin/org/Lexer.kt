package org

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.LinkedList
import java.util.Queue

class Lexer(private val lexicon: Lexicon, private val input: InputStream): Iterator<Token> {
    private var currentIndex: Int = 0 // indice en el input string.
    private var currentTokens: Queue<Token> = LinkedList() // tokens q tokenize al llamar a next()
    private var position: Position = Position(1, 1)

    private fun tokenizeStatement(statement: String, position: Position) {
        val components = splitIgnoringLiterals(statement)
        for (component in components) {
            if (component.contains("\n")) {
                handleNewLine(position)
            }
            tokenizeComponent(component, position)
        }
    }

    private fun tokenizeComponent(component: String, position: Position) {
        val subComponents = splitComponent(component)
        for (subComponent in subComponents) {
            try {
                val token = lexicon.getToken(subComponent, Location(position.line, position.column))
                currentTokens.add(token)
            } catch (e: Exception) {
                throw Exception(e.message ?: "Unknown error")
            }
            position.column += subComponent.length
        }
        position.column++
    }

    private fun handleNewLine(position: Position) {
        position.line++
        position.column = 0
    }

    private fun splitStatements(input: String): List<String> {
        var result: List<String> = ArrayList()
        var statement = ""
        for (character in input) {
            statement += character
            if (character == ';') {
                result = ArrayList(result + statement)
                statement = ""
            }
        }
        result = ArrayList(result + statement)
        return result
    }

    private fun splitIgnoringLiterals(input: String): List<String> {
        val regex = Regex("\"[^\"]*\"|'[^']*'|[^\\s\"']+|\\n")
        return regex.findAll(input).map { it.value }.toList()
    }

    private fun splitComponent(component: String): List<String> {
        val regex = Regex("[a-zA-Z_][a-zA-Z0-9_]*|:|;|=|[0-9]+(?:\\.[0-9]+)?|\".*?\"|'.*?'|\\S")
        return regex.findAll(component).map { it.value }.toList()
    }

    override fun hasNext(): Boolean {
        // if tokens left in currrent statement
        if (!currentTokens.isEmpty()) {
            return true
        }

        // there are characters in input left or 0.
        return input.available() == 0
    }

    override fun next(): Token {
        if (!hasNext()) {
            throw NoSuchElementException()
        }

        // se acabaron los tokens de la statement actual. lexeo el próximo.
        if (currentTokens.isEmpty()) {
            lexNextStatement()
        }

        // return next token from current statement
        return currentTokens.remove()
    }

    private fun lexNextStatement() {
        val statement = StringBuilder()

        // leo hassta encontrar ";" con un reader
        val reader = BufferedReader(InputStreamReader(input))

        var currentCharInt: Int
        var currentChar: Char

        // Read characters one by one
        while (reader.read().also { currentCharInt = it } != -1) {
            currentChar = currentCharInt.toChar()

            statement.append(currentChar)
            currentIndex++

            // End of statement
            if (currentChar == ';') {
                break
            }

            // Update position
            position = if (currentChar == '\n') {
                position.copy(line = position.line + 1, column = 0)
            } else {
                position.copy(column = position.column + 1)
            }
        }

        // Tokens of the tokenized statement -> currentTokens actualizado
        tokenizeStatement(statement.toString(), position)
    }
}
