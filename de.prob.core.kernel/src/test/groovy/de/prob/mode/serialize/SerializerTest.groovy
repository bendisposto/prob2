package de.prob.mode.serialize

import spock.lang.Specification
import de.prob.model.eventb.EventBMachine
import de.prob.model.serialize.ModelObject
import de.prob.model.serialize.Serializer

class SerializerTest extends Specification {

	String coded = '''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'''
	ModelObject modelObj

	def setup() {
		modelObj = Serializer.deserialize(coded)
	}

	def "model file is correct"() {
		expect:
		modelObj.getModelFile().absolutePath == "/home/joy/apps/workspaces/runtime-EclipseApplication/Farmer/MFarmer.bum"
	}

	def "machines are correct"() {
		expect:
		modelObj.getMachines().size() == 1
		modelObj.getMachines()[0].getName() == "MFarmer"
	}

	def "contexts are correct"() {
		expect:
		modelObj.getContexts().size() == 1
		modelObj.getContexts().toArray()[0].getName() == "CFarmer"
	}

	def "main component has correct amount of children"() {
		expect:
		modelObj.getMainComponent().getChildren().keySet().size() == 6
	}

	def "main component is a machine"() {
		expect:
		modelObj.getMainComponent() instanceof EventBMachine
	}
}
