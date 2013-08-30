package de.prob.model.eventb.translate

import spock.lang.Specification
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.theory.AxiomaticDefinitionsBlock
import de.prob.model.eventb.theory.AxiomaticOperatorDefinition
import de.prob.model.eventb.theory.DataType
import de.prob.model.eventb.theory.DirectDefinition
import de.prob.model.eventb.theory.Operator
import de.prob.model.eventb.theory.RecursiveOperatorDefinition
import de.prob.model.eventb.theory.Theorem

class TheoryTranslatorTest extends Specification {

	TheoryTranslator translator

	def setup() {
		translator = new TheoryTranslator();
	}

	def "It is possible to correctly parse a typeParameter"() {
		when:
		String typeP = '<typeParameter identifier="set1"/>'
		def xml = new XmlParser().parseText(typeP)
		def param = translator.addTypeParameter(xml.@identifier)

		then:
		param != null
	}

	def "Creating extensions for datatypes works"() {
		when:
		String code = '''<datatypeDefinition identifier="Tree">
							<typeArgument givenType="T"/>
								<datatypeConstructor identifier="empty"/>
								<datatypeConstructor identifier="tree">
									<constructorArgument  identifier="left" type="Tree(T)"/>
									<constructorArgument identifier="val" type="T"/>
									<constructorArgument identifier="right" type="Tree(T)"/>
								</datatypeConstructor>
						</datatypeDefinition>'''
		def xml = new XmlParser().parseText(code)
		DataType data = translator.addDataType(xml)

		then:
		new EventB("tree(l,v,r)",translator.getExtensions())
		data != null
		data.getTypeIdentifier() == new EventB("Tree")
		data.getDataTypeConstructors().size() == 2
	}

	def "It is possible to define a new operator of type predicate and have it parse something"() {
		when:
		String code = '''<newOperatorDefinition label="seqIsEmpty" associative="false" commutative="false" formulaType="false" notationType="PREFIX">
							<operatorArgument expression="seq(A)" identifier="s"/>
							<directOperatorDefinition formula="seqSize(s)=0"/>
						</newOperatorDefinition>'''
		def xml = new XmlParser().parseText(code)
		Operator op = translator.addOperator("blah", xml)

		then:
		new EventB("¬ seqIsEmpty(s)", translator.getExtensions())
		op != null
		op.getSyntax() == new EventB("seqIsEmpty")
		!op.isAssociative()
		!op.isCommutative()
		op.getFormulaType().toString() == "PREDICATE"
		op.getNotation().toString() == "PREFIX"
		op.getArguments().size() == 1
		op.getDefinition() instanceof DirectDefinition
	}

	def "It is possible to define a new operator of type expression and have it parse something"() {
		when:
		String code = '''<newOperatorDefinition label="seq" associative="false" commutative="false" formulaType="true" notationType="PREFIX">
							<operatorArgument expression="ℙ(A)" identifier="a"/>
							<directOperatorDefinition formula="{f,n · n ∈ ℕ ∧ f ∈ 1‥n→a∣f}"/>
						</newOperatorDefinition>'''
		def xml = new XmlParser().parseText(code)
		Operator op = translator.addOperator("blah", xml)

		then:
		new EventB("s ∈ seq(a)", translator.getExtensions())
		op != null
		op.getSyntax() == new EventB("seq")
		!op.isAssociative()
		!op.isCommutative()
		op.getFormulaType().toString() == "EXPRESSION"
		op.getNotation().toString() == "PREFIX"
		op.getArguments().size() == 1
		op.getDefinition() instanceof DirectDefinition
	}

	def "It is possible to define a new infix operator of type expression with multiple arguments and have it parse something"() {
		when:
		String code = '''<newOperatorDefinition label="AND" associative="true" commutative="true" formulaType="true" notationType="INFIX" syntaxSymbol="AND">
							<operatorArgument expression="BOOL" identifier="a" type="BOOL"/>
							<operatorArgument expression="BOOL" identifier="b" type="BOOL"/>
							<directOperatorDefinition formula="bool(a=TRUE ∧ b=TRUE)"/>
						</newOperatorDefinition>'''
		def xml = new XmlParser().parseText(code)
		Operator op = translator.addOperator("blah", xml)

		then:
		new EventB("a AND b", translator.getExtensions())
		op != null
		op.getSyntax() == new EventB("AND")
		op.isAssociative()
		op.isCommutative()
		op.getFormulaType().toString() == "EXPRESSION"
		op.getNotation().toString() == "INFIX"
		op.getArguments().size() == 2
		op.getDefinition() instanceof DirectDefinition
	}

	def "It is possible to create an operator with a recursive operator definition"() {
		when:
		String dataCode = '''<datatypeDefinition identifier="Tree">
							<typeArgument givenType="T"/>
								<datatypeConstructor identifier="empty"/>
								<datatypeConstructor identifier="tree">
									<constructorArgument  identifier="left" type="Tree(T)"/>
									<constructorArgument identifier="val" type="T"/>
									<constructorArgument identifier="right" type="Tree(T)"/>
								</datatypeConstructor>
						</datatypeDefinition>'''
		String opCode = '''<newOperatorDefinition label="treeDepth" associative="false" commutative="false" formulaType="true" notationType="PREFIX">
								<operatorArgument expression="Tree(T)" identifier="t"/>
								<recursiveOperatorDefinition inductiveArgument="t">
									<recursiveDefinitionCase expression="empty" formula="0"/>
									<recursiveDefinitionCase expression="tree(l,v,r)" formula="1+max({treeDepth(l), treeDepth(r)})"/>
								</recursiveOperatorDefinition>
							</newOperatorDefinition>'''
		def xml = new XmlParser().parseText(dataCode)
		translator.addDataType(xml)
		xml = new XmlParser().parseText(opCode)
		Operator op = translator.addOperator("BinTree", xml)

		then:
		op != null
		op.getSyntax() == new EventB("treeDepth")
		!op.isAssociative()
		!op.isCommutative()
		op.getFormulaType().toString() == "EXPRESSION"
		op.getNotation().toString() == "PREFIX"
		op.getArguments().size() == 1
		op.getDefinition() instanceof RecursiveOperatorDefinition
		op.getDefinition().getCases().size() == 2
	}

	def "adding theorems works"() {
		when:
		String opCode = '''<newOperatorDefinition label="cls" associative="false" commutative="false" formulaType="true" notationType="PREFIX">
								<operatorArgument expression="ℙ(S×S)" identifier="r"/>
								<directOperatorDefinition formula="fix(λs·s∈ℙ(S×S) ∣ r ∪ (s;r))"/>
							</newOperatorDefinition>'''
		String thmCode = '''<theorem label="thm6" predicate="∀r·r∈ℙ(S×S) ⇒ cls(r);cls(r)⊆cls(r)"/>'''
		def xml = new XmlParser().parseText(opCode)
		translator.addOperator("thmTest",xml)
		xml = new XmlParser().parseText(thmCode)
		Theorem thm = translator.addTheorem(xml)

		then:
		thm != null
		thm.getName() == "thm6"
		thm.getPredicate() == new EventB("∀r·r∈ℙ(S×S) ⇒ cls(r);cls(r)⊆cls(r)", translator.getExtensions())
	}

	def "adding axiomatic definitions works"() {
		when:
		String code = '''<axiomaticDefinitionsBlock label="xdb1">
							<axiomaticOperatorDefinition label="SUM" associative="false" commutative="false" formulaType="true" notationType="PREFIX" type="ℤ">
								<operatorArgument expression="T↔ℤ" identifier="s"/>
								<operatorWDcondition predicate="s ∈ T⇸ℤ"/>
								<operatorWDcondition predicate="finite(s)"/>
							</axiomaticOperatorDefinition>
							<axiomaticDefinitionAxiom comment="SUM(∅ ⦂ T↔ℤ) = 0" label="axm1" predicate="SUM({p·p∈(T×ℤ)∧⊥∣p}) = 0"/>
						</axiomaticDefinitionsBlock>'''
		def xml = new XmlParser().parseText(code)
		AxiomaticDefinitionsBlock block = translator.addAxiomaticDefinition("blah" , xml)

		then:
		block != null
		block.getName() == "xdb1"
		block.getAxiomaticTypeExtensions().size() == 0
		block.getDefinitionAxioms().size() == 1
		block.getDefinitionAxioms()[0].getName() == "axm1"
		block.getDefinitionAxioms()[0].getPredicate() == new EventB("SUM({p·p∈(T×ℤ)∧⊥∣p}) = 0")
		block.getOperatorDefinitions().size() == 1
		block.getOperatorDefinitions()[0].getSyntax() == new EventB("SUM")
		block.getOperatorDefinitions()[0].getDefinition() instanceof AxiomaticOperatorDefinition
		block.getOperatorDefinitions()[0].getDefinition().getType() == new EventB("ℤ")
	}

	def "adding proof rules block works"() {
		when:
		String opDef = '''<newOperatorDefinition label="NOT" associative="false" commutative="false" formulaType="true" notationType="PREFIX" syntaxSymbol="NOT">
							<operatorArgument expression="BOOL" identifier="a" type="BOOL"/>
							<directOperatorDefinition formula="bool(¬ a=TRUE)"/>
						</newOperatorDefinition>'''
		String rewriteBlock = '''<proofRulesBlock label="blockNOT">
									<rewriteRule label="rewNT" applicability="automatic" auto="true" complete="true" desc="NOT TRUE" formula="NOT(TRUE)" interactive="true" toolTip="NOT T == F">
										<rewriteRuleRHS label="rhs1" predicate="⊤" formula="FALSE"/>
									</rewriteRule>
									<rewriteRule label="rewNF" applicability="automatic" auto="true" complete="true" desc="NOT FALSE" formula="NOT(FALSE)" interactive="true" toolTip="NOT F == T">
										<rewriteRuleRHS label="rhs1" predicate="⊤" formula="TRUE"/>
									</rewriteRule>
								</proofRulesBlock>'''
		def xml = new XmlParser().parseText(opDef)
		translator.addOperator("Not",xml)
		xml = new XmlParser().parseText(rewriteBlock)
		def block = translator.addProofRuleBlock(xml)

		then:
		block != null
	}

	def "operators that use datatypes can be parsed"() {
		when:
		String dataType = '''<datatypeDefinition identifier="List">
								<typeArgument givenType="T"/>
								<datatypeConstructor identifier="nil"/>
								<datatypeConstructor identifier="cons">
									<constructorArgument identifier="head" type="T"/>
									<constructorArgument identifier="tail" type="List(T)"/>
								</datatypeConstructor>
							</datatypeDefinition>'''
		String op = '''<newOperatorDefinition label="append" associative="false" commutative="false" formulaType="true" notationType="PREFIX">
							<operatorArgument expression="List(T)" identifier="l"/>
							<operatorArgument expression="T" identifier="x"/>
							<recursiveOperatorDefinition inductiveArgument="l">
								<recursiveDefinitionCase expression="nil" formula="cons(x,nil)"/>
								<recursiveDefinitionCase expression="cons(x0,l0)" formula="cons(x0,append(l0,x))"/>
							</recursiveOperatorDefinition>
						</newOperatorDefinition>'''
		def xml = new XmlParser().parseText(dataType)
		translator.addDataType(xml)
		xml = new XmlParser().parseText(op)
		def operator = translator.addOperator("blah",xml)

		then:
		operator != null
	}
}