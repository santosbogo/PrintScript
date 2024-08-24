package org

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.File

class Analyze : CliktCommand() {
    private val filePath by argument(help = "Path to the script file to execute")
    private val rulePath by argument(help = "Path to the rule json to use")

    override fun run() {
        val code = File(filePath).readText()
        val rulesContent = File(rulePath).readText()
        val rules = Json.parseToJsonElement(rulesContent).jsonObject

        val lexerResult = Lexer().tokenize(code)

        if (lexerResult.hasErrors()) {
            lexerResult.errors.forEach { echo("Error: $it") }
            return
        }

        val parserResult = Parser().parse(lexerResult.tokens)

        if (parserResult.programNode == null) {
            parserResult.errors.forEach { echo("Error: $it") }
            return
        }

        val linterResult = Linter(rules).lint(parserResult.programNode!!)
        linterResult.getList().forEach { echo(it, true) }

        echo("Analyze successful")
    }
}
