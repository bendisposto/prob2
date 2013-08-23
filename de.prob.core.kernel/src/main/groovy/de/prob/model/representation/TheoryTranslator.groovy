package de.prob.model.representation

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.EventBAxiom
import de.prob.model.eventb.theory.AxiomaticDefinitionsBlock
import de.prob.model.eventb.theory.AxiomaticOperatorDefinition
import de.prob.model.eventb.theory.DataType
import de.prob.model.eventb.theory.DataTypeConstructor
import de.prob.model.eventb.theory.DataTypeDestructor
import de.prob.model.eventb.theory.DirectDefinition
import de.prob.model.eventb.theory.InferenceRule
import de.prob.model.eventb.theory.MetaVariable
import de.prob.model.eventb.theory.Operator
import de.prob.model.eventb.theory.OperatorArgument
import de.prob.model.eventb.theory.OperatorWDCondition
import de.prob.model.eventb.theory.ProofRulesBlock
import de.prob.model.eventb.theory.RecursiveDefinitionCase
import de.prob.model.eventb.theory.RecursiveOperatorDefinition
import de.prob.model.eventb.theory.RewriteRule
import de.prob.model.eventb.theory.RewriteRuleRHS
import de.prob.model.eventb.theory.Theorem
import de.prob.model.eventb.theory.Theory
import de.prob.model.eventb.theory.Type

class TheoryTranslator {

	def theories = new ModelElementList<AbstractElement>()
	def theoryMap = [:]
	def workspacePath

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

		def theorems = []
		xml.theorem.each {
			theorems << new Theorem(it.@label, it.@predicate)
		}
		theory.addTheorems(theorems)

		def params = []
		xml.typeParameter.@identifier.each {
			params << new Type(it)
		}
		theory.addTypeParameters(params)

		def extractOperator = { container ->
			{ rep ->
				def label = rep.@label
				def associative = rep.@associative == "true"
				def commutative = rep.@commutative == "true"
				def formulaType = rep.@formulaType == "true"
				def notationType = rep.@notationType
				def definition
				if(rep.@type != null) {
					definition = new AxiomaticOperatorDefinition(rep.@type)
				} else if(rep.directOperatorDefinition != []) {
					definition = new DirectDefinition(rep.directOperatorDefinition[0].@formula)
				} else if(rep.recursiveOperatorDefinition != []) {
					def recDef = rep.recursiveOperatorDefinition[0]
					definition = new RecursiveOperatorDefinition(recDef.@inductiveArgument);
					def cases = []
					recDef.recursiveDefinitionCase.each {
						cases << new RecursiveDefinitionCase(it.@expression, it.@formula)
					}
					definition.addCases(cases)
				}
				def operator = new Operator(label, associative, commutative, formulaType, notationType, definition)

				def args = []
				rep.operatorArgument.each {
					args << new OperatorArgument(it.@identifier, it.@expression)
				}
				operator.addArguments(args)

				def wds = []
				rep.operatorWDcondition.each {
					wds << new OperatorWDCondition(it.@predicate)
				}
				operator.addWDConditions(wds)

				container << operator
			}
		}

		def operators = []
		xml.newOperatorDefinition.each extractOperator(operators)
		theory.addOperators(operators)

		def axDefs = []
		xml.axiomaticDefinitionsBlock.each { block ->
			def defBlock = new AxiomaticDefinitionsBlock(block.@label)

			def typeDefs = []
			block.axiomaticTypeDefinition.each {
				typeDefs << new Type(it.@identifier)
			}
			defBlock.addTypeDefinitions(typeDefs)

			def axioms = []
			block.axiomaticDefinitionAxiom.each {
				axioms << new EventBAxiom(it.@label, it.@predicate, false)
			}
			defBlock.addDefinitionAxioms(axioms)

			def ops = []
			block.axiomaticOperatorDefinition.each extractOperator(ops)
			defBlock.addOperatorDefinitions(ops)
			axDefs << defBlock
		}
		theory.addAxiomaticDefinitions(axDefs)

		def proofBlocks = []
		xml.proofRulesBlock.each { pRB ->
			def block = new ProofRulesBlock(pRB.@label)

			def vars = []
			pRB.metaVariable.each {
				vars << new MetaVariable(it.@identifier, it.@type)
			}
			block.addMetaVariables(vars)

			def rewrites = []
			pRB.rewriteRule.each { rwR ->
				def label = rwR.@label
				def applicability = rwR.@applicability
				def complete = rwR.@complete == "true"
				def desc = rwR.@desc
				def formula = rwR.@formula
				def rule = new RewriteRule(label, applicability, complete, desc, formula)

				def rHSs = []
				rwR.rewriteRuleRHS.each { rHSs << new RewriteRuleRHS(it.@label, it.@predicate, it.@formula) }
				rule.addRightHandSide(rHSs)
				rewrites << rule
			}
			block.addRewriteRules(rewrites)

			def inferences = []
			pRB.inferenceRule.each { inf ->
				def given = []
				inf.given.each {
					given << new EventB(it.@predicate)
				}

				def infer = inf.infer.isEmpty() ? null : new EventB(inf.infer[0].@predicate)
				inferences << new InferenceRule(given, infer)
			}
			block.addInferenceRules(inferences)

			proofBlocks << block
		}
		theory.addProofRules(proofBlocks)

		def dataTypes = []
		xml.dataTypeDefinition.each {datum ->
			def data = new DataType(datum.@identifier)

			def types = []
			datum.typeArgument.each {
				types << new Type(it.@givenType)
			}
			data.addTypeArguments(types)

			def constructors = []
			datum.dataTypeConstructor.each { cons ->
				def struct = new DataTypeConstructor(cons.@identifier)

				def destrs = []
				cons.constructorArgument.each {
					destrs << new DataTypeDestructor(it.@identifier, it.@type)
				}
				struct.addDestructors(destrs)
				constructors << struct
			}
			data.addConstructors(constructors)

			dataTypes << data
		}
		theory.addDataTypes(dataTypes)

		theoryMap[name] = theory;
		return theory
	}

	def getXML(file) {
		def text = file.text.replaceAll("org.eventb.core.","")
		text = text.replaceAll("org.eventb.theory.core.", "")
		return new XmlParser().parseText(text)
	}
}
