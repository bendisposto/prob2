package de.prob.model.eventb.translate

import org.eclipse.jetty.util.ConcurrentHashSet
import org.eventb.core.ast.FormulaFactory
import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.theory.AxiomaticOperatorDefinition
import de.prob.model.eventb.theory.DataType
import de.prob.model.eventb.theory.DataTypeConstructor
import de.prob.model.eventb.theory.DataTypeDestructor
import de.prob.model.eventb.theory.DirectDefinition
import de.prob.model.eventb.theory.InferenceRule
import de.prob.model.eventb.theory.MetaVariable
import de.prob.model.eventb.theory.Operator
import de.prob.model.eventb.theory.OperatorArgument
import de.prob.model.eventb.theory.ProofRulesBlock
import de.prob.model.eventb.theory.RecursiveDefinitionCase
import de.prob.model.eventb.theory.RecursiveOperatorDefinition
import de.prob.model.eventb.theory.RewriteRule
import de.prob.model.eventb.theory.RewriteRuleRHS
import de.prob.model.eventb.theory.Theory
import de.prob.model.eventb.theory.Type
import de.prob.model.representation.AbstractElement
import de.prob.model.representation.ModelElementList

class TheoryTranslator {

	def theories = new ModelElementList<AbstractElement>()
	def theoryMap = [:]
	def workspacePath
	def Set<IFormulaExtension> extensions = new ConcurrentHashSet<IFormulaExtension>()

	def getTheories(String directory) {
		def f = new File("${directory}/TheoryPath.tul")
		if(!f.exists()) {
			return theories
		}

		workspacePath = directory.lastIndexOf('/').with {
			it != -1 ? directory[0..<it] : directory
		}

		def xml = getXML(f)
		xml.availableTheoryProject.each {
			it.availableTheory.each{
				def path = it.@availableTheory;
				path = path.replaceAll(".dtf.*\$","")
				theories << extractTheory("${workspacePath}/${path}.tuf")
			}
		}
		return theories
	}

	def extractTheory(path) {
		def file = new File(path)
		def name = file.getName().lastIndexOf('.').with {
			it != -1 ? file.getName()[0..<it] : file.getName()
		}

		if(theoryMap.containsKey(name)) {
			return theoryMap[name];
		}

		def xml = getXML(file)
		def theory = new Theory(name)

		def imported = []
		xml.importTheoryProject.importTheory.each {
			def p = it.@importTheory
			p = p.replaceAll(".dtf.*\$","")
			imported << extractTheory("${workspacePath}/${p}.tuf")
		}
		theory.addImported(imported)

		def params = []
		xml.typeParameter.@identifier.each { params << addTypeParameter(it) }
		theory.addTypeParameters(params)

		def dataTypes = []
		xml.datatypeDefinition.each { dataTypes << addDataType(it) }
		theory.addDataTypes(dataTypes)

		def operators = []
		xml.newOperatorDefinition.each { operators << addOperator(name, it) }
		theory.addOperators(operators)

		def axDefs = []
		xml.axiomaticDefinitionsBlock.each { axDefs << addAxiomaticDefinition(name, it) }
		theory.addAxiomaticDefinitions(axDefs)

		def theorems = []
		xml.theorem.each { theorems << addTheorem(it) }
		theory.addTheorems(theorems)

		def proofBlocks = []
		xml.proofRulesBlock.each {  proofBlocks << addProofRuleBlock(it) }
		theory.addProofRules(proofBlocks)

		theoryMap[name] = theory;
		return theory
	}

	def getXML(file) {
		def text = file.text.replaceAll("org.eventb.core.","")
		text = text.replaceAll("org.eventb.theory.core.", "")
		return new XmlParser().parseText(text)
	}


	def addTypeParameter(type) {
		def p =  new Type(type, extensions)
		extensions << p.getFormulaExtension()
		return p
	}

	def addDataType(datum) {
		def data = new DataType(datum.@identifier)

		def types = []
		datum.typeArgument.each {
			types << new Type(it.@givenType, extensions)
		}
		data.addTypeArguments(types)

		def constructors = []
		datum.datatypeConstructor.each { cons ->
			def struct = new DataTypeConstructor(cons.@identifier)

			def destrs = []
			cons.constructorArgument.each {
				destrs << new DataTypeDestructor(it.@identifier, it.@type)
			}
			struct.addDestructors(destrs)
			constructors << struct
		}
		data.addConstructors(constructors)

		def exts = data.getFormulaExtensions(FormulaFactory.getInstance(extensions))
		extensions.addAll(exts)

		data.parseElements(extensions)
		return data
	}

	def addOperator(theoryName, rep) {
		def label = rep.@label
		def associative = rep.@associative == "true"
		def commutative = rep.@commutative == "true"
		def formulaType = rep.@formulaType == "true"
		def notationType = rep.@notationType
		def definition
		if(rep.@type != null) {
			definition = new AxiomaticOperatorDefinition(rep.@type,extensions)
		} else if(rep.directOperatorDefinition != []) {
			definition = new DirectDefinition(rep.directOperatorDefinition[0].@formula, extensions)
		} else if(rep.recursiveOperatorDefinition != []) {
			def recDef = rep.recursiveOperatorDefinition[0]
			definition = new RecursiveOperatorDefinition(recDef.@inductiveArgument, extensions);
		}
		Operator operator = new Operator(theoryName, label, associative, commutative, formulaType, notationType, definition, extensions)

		def args = []
		rep.operatorArgument.each {
			args << new OperatorArgument(it.@identifier, it.@expression, extensions)
		}
		operator.addArguments(args)

		def wds = []
		rep.operatorWDcondition.each {
			//wds << new OperatorWDCondition(it.@predicate, extensions)
		}
		operator.addWDConditions(wds)

		extensions << operator.getFormulaExtension()

		// In order for the recursive definition cases to be parsed, they have to be added after the Operator extension has already been defined
		if(definition instanceof RecursiveOperatorDefinition) {
			def recDef = rep.recursiveOperatorDefinition[0]
			def cases = []
			recDef.recursiveDefinitionCase.each {
				cases << new RecursiveDefinitionCase(it.@expression, it.@formula, extensions)
			}
			definition.addCases(cases)
		}

		return operator;
	}

	def addAxiomaticDefinition(theoryName, block) {
		/*def defBlock = new AxiomaticDefinitionsBlock(block.@label)
		 def typeDefs = []
		 block.axiomaticTypeDefinition.each {
		 Type type = new Type(it.@identifier, extensions)
		 typeDefs << type
		 extensions << type.getFormulaExtension()
		 }
		 defBlock.addTypeDefinitions(typeDefs)
		 def ops = []
		 block.axiomaticOperatorDefinition.each { ops << addOperator(theoryName, it)	 }
		 defBlock.addOperatorDefinitions(ops)
		 def axioms = []
		 block.axiomaticDefinitionAxiom.each {
		 axioms << new EventBAxiom(it.@label, it.@predicate, false, extensions)
		 }
		 defBlock.addDefinitionAxioms(axioms)
		 return defBlock*/
	}

	def addProofRuleBlock(pRB) {
		def block = new ProofRulesBlock(pRB.@label)

		def vars = []
		pRB.metaVariable.each {
			vars << new MetaVariable(it.@identifier, it.@type, extensions)
		}
		block.addMetaVariables(vars)

		def rewrites = []
		pRB.rewriteRule.each { rwR ->
			def label = rwR.@label
			def applicability = rwR.@applicability
			def complete = rwR.@complete == "true"
			def desc = rwR.@desc
			def formula = rwR.@formula
			def rule = new RewriteRule(label, applicability, complete, desc, formula, extensions)

			def rHSs = []
			rwR.rewriteRuleRHS.each {
				rHSs << new RewriteRuleRHS(it.@label, it.@predicate, it.@formula, extensions)
			}
			rule.addRightHandSide(rHSs)
			rewrites << rule
		}
		block.addRewriteRules(rewrites)

		def inferences = []
		pRB.inferenceRule.each { inf ->
			def given = []
			inf.given.each {
				given << new EventB(it.@predicate, extensions)
			}

			def infer = inf.infer.isEmpty() ? null : new EventB(inf.infer[0].@predicate, extensions)
			inferences << new InferenceRule(given, infer)
		}
		block.addInferenceRules(inferences)
		return block
	}

	def addTheorem(xml) {
		//return new Theorem(xml.@label, xml.@predicate, extensions)
	}
}
