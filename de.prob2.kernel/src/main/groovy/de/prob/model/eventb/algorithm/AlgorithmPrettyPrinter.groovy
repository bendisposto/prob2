package de.prob.model.eventb.algorithm

import static de.prob.unicode.UnicodeTranslator.toUnicode

class AlgorithmPrettyPrinter {

	Block algorithm

	def AlgorithmPrettyPrinter(Block algorithm) {
		this.algorithm = algorithm
	}

	def String prettyPrint() {
		StringBuilder sb = new StringBuilder()
		printBlock(algorithm, sb, null)
		sb.toString()
	}

	def printBlock(Block b, StringBuilder sb, String indent) {
		def newindent = indent == null ? "" : "  ${indent}"
		if (b.statements) {
			b.statements.each {
				printStatement(it, sb, newindent)
			}
		} else {
			writeLine(sb, newindent,"// do nothing")
		}
	}

	def printStatement(While statement, StringBuilder sb, String indent) {
		writeLine(sb,indent,statement.toString())
		if (statement.variant) {
			def newindent = indent == null ? "" : "  ${indent}"
			writeLine(sb,newindent,"variant: "+toUnicode(statement.variant))
		}
		printBlock(statement.block, sb, indent)
	}

	def printStatement(Assignments statement, StringBuilder sb, String indent) {
		writeLine(sb,indent,statement.toString())
	}

	def printStatement(If statement, StringBuilder sb, String indent) {
		writeLine(sb,indent, statement.toString())
		printBlock(statement.Then, sb, indent)
		if (statement.Else.statements) {
			writeLine(sb,indent,"else:")
			printBlock(statement.Else, sb, indent)
		}
	}

	def printStatement(Assertion statement, StringBuilder sb, String indent) {
		writeLine(sb,indent,statement.toString())
	}

	def writeLine(StringBuilder sb, String indent, String line) {
		if (indent) sb.append(indent)
		sb.append(line)
		sb.append("\n")
	}

}
