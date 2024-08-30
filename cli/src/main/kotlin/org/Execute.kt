package org

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import java.io.File

class Execute : CliktCommand() {
    private val filePath by argument(help = "Path to the script file to execute")

    override fun run() {
        val code = File(filePath).readText()

        echo("Lexing...\n", trailingNewline = true)
        val lexerResult = Lexer().tokenize(code)

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

        echo("Executing...\n", trailingNewline = true)
        Interpreter().interpret(parserResult.programNode!!)

        echo("Execution successful")
    }
}
