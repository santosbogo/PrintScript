package org

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import java.io.File

class Validate : CliktCommand() {
    private val filePath by argument(help = "Path to the script file to execute")

    override fun run() {
        val code = File(filePath).readText()

        echo("Lexing...\n", trailingNewline = true)
        val lexer = LexerFactory.createLexerV11()
        val lexerResult = lexer.tokenize(code)

        if (lexerResult.hasErrors()) {
            lexerResult.errors.forEach { echo(it, err = true) }
            return
        }

        echo("Parsing...\n", trailingNewline = true)
        val parserResult = Parser().parse(lexerResult.tokens)

        if (parserResult.programNode == null) {
            parserResult.errors.forEach { echo(it, err = true) }
            return
        }

        echo("Validation successful")
    }
}
