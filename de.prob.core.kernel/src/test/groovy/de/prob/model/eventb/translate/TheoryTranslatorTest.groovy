package de.prob.model.eventb.translate

import spock.lang.Specification
import de.prob.animator.domainobjects.EventB

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
		def data = translator.addDataType(xml)

		then:
		new EventB("tree(l,v,r)",translator.getExtensions())
		data != null
	}

	def "It is possible to define a new operator of type predicate and have it parse something"() {
		when:
		String code = '''<newOperatorDefinition label="seqIsEmpty" associative="false" commutative="false" formulaType="false" notationType="PREFIX">
							<operatorArgument expression="seq(A)" identifier="s"/>
							<directOperatorDefinition formula="seqSize(s)=0"/>
						</newOperatorDefinition>'''
		def xml = new XmlParser().parseText(code)
		def op = translator.addOperator("blah", xml)

		then:
		new EventB("¬ seqIsEmpty(s)", translator.getExtensions())
		op != null
	}

	def "It is possible to define a new operator of type expression and have it parse something"() {
		when:
		String code = '''<newOperatorDefinition label="seq" associative="false" commutative="false" formulaType="true" notationType="PREFIX">
							<operatorArgument expression="ℙ(A)" identifier="a"/>
							<directOperatorDefinition formula="{f,n · n ∈ ℕ ∧ f ∈ 1‥n→a∣f}"/>
						</newOperatorDefinition>'''
		def xml = new XmlParser().parseText(code)
		def op = translator.addOperator("blah", xml)

		then:
		new EventB("s ∈ seq(a)", translator.getExtensions())
		op != null
	}

	def "It is possible to define a new infix operator of type expression with multiple arguments and have it parse something"() {
		when:
		String code = '''<newOperatorDefinition label="AND" associative="true" commutative="true" formulaType="true" notationType="INFIX" syntaxSymbol="AND">
							<operatorArgument expression="BOOL" identifier="a" type="BOOL"/>
							<operatorArgument expression="BOOL" identifier="b" type="BOOL"/>
							<directOperatorDefinition formula="bool(a=TRUE ∧ b=TRUE)"/>
						</newOperatorDefinition>'''
		def xml = new XmlParser().parseText(code)
		def op = translator.addOperator("blah", xml)

		then:
		new EventB("a AND b", translator.getExtensions())
		op != null
	}
}