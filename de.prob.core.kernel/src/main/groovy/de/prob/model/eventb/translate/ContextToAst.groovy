package de.prob.model.eventb.translate

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.be4.classicalb.core.parser.node.AAbstractConstantsContextClause
import de.be4.classicalb.core.parser.node.AAxiomsContextClause
import de.be4.classicalb.core.parser.node.AConstantsContextClause
import de.be4.classicalb.core.parser.node.ADeferredSetSet
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit
import de.be4.classicalb.core.parser.node.AExtendsContextClause
import de.be4.classicalb.core.parser.node.ASetsContextClause
import de.be4.classicalb.core.parser.node.ATheoremsContextClause
import de.be4.classicalb.core.parser.node.Node
import de.be4.classicalb.core.parser.node.PContextClause
import de.be4.classicalb.core.parser.node.TIdentifierLiteral
import de.prob.model.eventb.Context
import de.prob.prolog.output.IPrologTermOutput

class ContextToAst {

	Logger logger = LoggerFactory.getLogger(ContextToAst.class)
	Context context;

	def ContextToAst(context) {
		this.context = context;
	}

	def Node translateContext() {
		AEventBContextParseUnit ast = new AEventBContextParseUnit();
		ast.setName(new TIdentifierLiteral(context.getName()))

		List<PContextClause> clauses = new ArrayList<PContextClause>()
		clauses << processExtends()
		clauses.addAll(processConstants())
		clauses.addAll(processAxiomsAndTheorems())
		clauses << processSets()

		ast.setContextClauses(clauses)
		return ast
	}

	def processExtends() {
		def extended = []
		context.getExtends().each {
			extended << new TIdentifierLiteral(it.getName())
		}
		return new AExtendsContextClause(extended)
	}

	def processConstants() {
		def concreteConstants = []
		def abstractConstants = []

		context.getConstants().each {
			if(it.isAbstract()) {
				abstractConstants << it.getExpression().ast
			} else {
				concreteConstants << it.getExpression().ast
			}
		}

		return [
			new AConstantsContextClause(concreteConstants),
			new AAbstractConstantsContextClause(abstractConstants)
		]
	}

	def processAxiomsAndTheorems() {
		def axioms = []
		def theorems = []

		context.getAxioms().each {
			if(it.isTheorem()) {
				theorems << it.getPredicate().ast
			} else {
				axioms << it.getPredicate().ast
			}
		}
		return [
			new AAxiomsContextClause(axioms),
			new ATheoremsContextClause(theorems)
		]
	}

	def processSets() {
		def sets = []

		context.getSets().each {
			sets << new ADeferredSetSet([
				new TIdentifierLiteral(it.getName())
			])
		}

		return new ASetsContextClause(sets)
	}

	def printProofsToProlog(IPrologTermOutput pto) {
		context.getProofs().each {
			pto.openTerm("po")
			pto.printAtom(context.getName())
			pto.printAtom(it.getDescription())
			pto.openList()
			it.toProlog(pto)
			pto.closeList()
			pto.printAtom(String.valueOf(it.isDischarged()))
			pto.closeTerm()
		}
	}
}
