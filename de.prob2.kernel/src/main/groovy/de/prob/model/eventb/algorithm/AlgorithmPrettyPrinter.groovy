package de.prob.model.eventb.algorithm


import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.IAssignment
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.representation.ModelElementList

class AlgorithmPrettyPrinter {

	def algorithm
	ModelElementList<Procedure> procedures

	def AlgorithmPrettyPrinter() {
	}

	def AlgorithmPrettyPrinter(Block algorithm) {
		this.algorithm = algorithm
		this.procedures = new ModelElementList<Procedure>()
	}

	def AlgorithmPrettyPrinter(Block algorithm, ModelElementList<Procedure> procedures) {
		this.algorithm = algorithm
		this.procedures = procedures
	}

	def String prettyPrint() {
		StringBuilder sb = new StringBuilder()
		if (!procedures.isEmpty()) {
			procedures.each { Procedure p ->
				writeLine(sb, null, p.toString())
				writeLine(sb, "  ", "when: "+p.precondition.toUnicode())
				writeLine(sb, "  ", "then: "+p.postcondition.toUnicode())
				writeLine(sb, "  ", "");
			}
		}

		printBlock(algorithm, sb, null)
		sb.toString()
	}

	def String prettyPrint(Block b) {
		StringBuilder sb = new StringBuilder()
		printBlock(b, sb, null)
		sb.toString()
	}

	def String prettyPrint(Statement stmt) {
		StringBuilder sb = new StringBuilder()
		printStatement(stmt, sb, "")
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
		if (statement.variant || statement.invariant) {
			def newindent = indent == null ? "" : "  ${indent}"
			if (statement.invariant) {
				writeLine(sb, newindent,"invariant: "+statement.invariant.toUnicode())
			}
			if (statement.variant) {
				writeLine(sb, newindent,"variant: "+statement.variant.toUnicode())
			}
		}
		printBlock(statement.block, sb, indent)
	}

	def printStatement(IAssignment statement, StringBuilder sb, String indent) {
		writeLine(sb,indent,statement.toString())
	}


	def printStatement(Skip statement, StringBuilder sb, String indent) {
	}

	def printStatement(Assertion statement, StringBuilder sb, String indent) {
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

	def writeLine(StringBuilder sb, String indent, String line) {
		if (indent) sb.append(indent)
		sb.append(line)
		sb.append("\n")
	}
}
