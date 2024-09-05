package org

import kotlinx.serialization.json.JsonObject
import java.util.Queue

class Runner(version: String) {
    private val lexer: Lexer
    private val parser: Parser
    private val linter: Linter
    private val formatter: Formatter

    init {
        when (version) {
            "1.0" -> {
                lexer = LexerFactory.createLexerV10()
                parser = ParserFactory.createParserV10()
                linter = Linter("1.0")
                formatter = FormatterFactory().createFormatterV10()
            }

            "1.1" -> {
                lexer = LexerFactory.createLexerV11()
                parser = ParserFactory.createParserV11()
                linter = Linter("1.1")
                formatter = FormatterFactory().createFormatterV11()
            }

            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }

    fun execute(str: String, version: String, input: Queue<String>): RunnerResult.Execute {
        val interpreter = InterpreterFactory.createRunnerInterpreter(version, input)
        val printList = mutableListOf<String>()
        val errorsList = mutableListOf<String>()

        val lexerResult = lexer.tokenize(str)

        if (lexerResult.hasErrors()) {
            lexerResult.errors.forEach { errorsList.add(it) }
            return RunnerResult.Execute(printList, errorsList)
        }

        val parserResult = parser.parse(lexerResult.tokens)

        if (parserResult.programNode == null) {
            parserResult.errors.forEach { errorsList.add(it) }
            return RunnerResult.Execute(printList, errorsList)
        }

        val interpreterResult = interpreter.interpret(parserResult.programNode!!)

        if (interpreterResult.printsList.isNotEmpty()) {
            interpreterResult.printsList.forEach { printList.add(it) }
        }
        return RunnerResult.Execute(printList, errorsList)
    }

    fun analyze(str: String, jsonFile: JsonObject): RunnerResult.Analyze {
        val warningsList = mutableListOf<String>()
        val errorsList = mutableListOf<String>()

        val lexerResult = lexer.tokenize(str)

        if (lexerResult.hasErrors()) {
            lexerResult.errors.forEach { errorsList.add(it) }
            return RunnerResult.Analyze(warningsList, errorsList)
        }

        val parserResult = parser.parse(lexerResult.tokens)

        if (parserResult.programNode == null) {
            parserResult.errors.forEach { errorsList.add(it) }
            return RunnerResult.Analyze(warningsList, errorsList)
        }

        val linterResult = linter.lint(parserResult.programNode!!, jsonFile)
        linterResult.getList().forEach { warningsList.add(it) }
        return RunnerResult.Analyze(warningsList, errorsList)
    }

    fun validate(str: String): RunnerResult.Validate {
        val errorsList = mutableListOf<String>()

        val lexerResult = lexer.tokenize(str)

        if (lexerResult.hasErrors()) {
            lexerResult.errors.forEach { errorsList.add(it) }
            return RunnerResult.Validate(errorsList)
        }

        val parserResult = parser.parse(lexerResult.tokens)

        if (parserResult.programNode == null) {
            parserResult.errors.forEach { errorsList.add(it) }
            return RunnerResult.Validate(errorsList)
        }
        return RunnerResult.Validate(errorsList)
    }

    fun format(str: String, json: JsonObject): RunnerResult.Format {
        val errorsList = mutableListOf<String>()

        val lexerResult = lexer.tokenize(str)

        if (lexerResult.hasErrors()) {
            lexerResult.errors.forEach { errorsList.add(it) }
            return RunnerResult.Format("", errorsList)
        }

        val parserResult = parser.parse(lexerResult.tokens)

        if (parserResult.programNode == null) {
            parserResult.errors.forEach { errorsList.add(it) }
            return RunnerResult.Format("", errorsList)
        }

        val formatterResult = formatter.format(parserResult.programNode!!, json)
        return RunnerResult.Format(formatterResult.code, errorsList)
    }
}
