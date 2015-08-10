package de.prob.model.eventb

import spock.lang.Specification
import de.prob.model.representation.DependencyGraph.ERefType

class ModelModifierTest extends Specification {

	def "adding a machine with no refinement should not overwrite existing refinement"() {
		when:
		def mm = new ModelModifier().make {
			machine(name: "m1") {}

			machine(name: "m2", refines: "m1") {}
		}
		def model1 = mm.getModel()
		mm = mm.make {
			machine(name: "m2") {}
		}
		def model2 = mm.getModel()

		then:
		model1.m2.getRefines() == [model1.m1]
		model2.m2.getRefines() == [model2.m1]
	}

	def "adding a machine with a refinement should replace existing refinement (and delete existing relationship)"() {
		when:
		def mm = new ModelModifier().make {
			machine(name: "m1") {}
			machine(name: "m2", refines: "m1") {}
		}
		def model1 = mm.getModel()
		mm = mm.make {
			machine(name: "m0") {}
			machine(name: "m2", refines: "m0") {}
		}
		def model2 = mm.getModel()

		then:
		model1.m2.getRefines() == [model1.m1]
		model2.m2.getRefines() == [model2.m0]
		model2.m1 != null
		model2.getRelationship(model2.m2.getName(), model2.m1.getName()) == null
		model2.getRelationship(model2.m2.getName(), model2.m0.getName()) == ERefType.REFINES
	}

	def "adding a machine with no sees should not overwrite existing sees"() {
		when:
		def mm = new ModelModifier().make {
			context(name: "c1") {}

			machine(name: "m1", sees: ["c1"]) {}
		}
		def model1 = mm.getModel()
		mm = mm.make {
			machine(name: "m1") {}
		}
		def model2 = mm.getModel()

		then:
		model1.m1.getSees() == [model1.c1]
		model2.m1.getSees() == [model2.c1]
	}

	def "adding a machine with sees should add sees to existing sees if they are new"() {
		when:
		def mm = new ModelModifier().make {
			context(name: "c1") {}

			machine(name: "m1", sees: ["c1"]) {}
		}
		def model1 = mm.getModel()
		mm = mm.make {
			context(name: "c2") {}

			machine(name: "m1", sees: ["c2"]) {}
		}
		def model2 = mm.getModel()
		mm = mm.make {
			context(name: "c3") {}

			machine(name: "m1", sees: ["c2", "c3"]) {}
		}
		def model3 = mm.getModel()

		then:
		model1.m1.getSees() == [model1.c1]
		model2.m1.getSees() == [model2.c1, model2.c2]
		model3.m1.getSees() == [model3.c1, model3.c2, model3.c3]
	}

	def "adding a context with no extends should not overwrite existing extends"() {
		when:
		def mm = new ModelModifier().make {
			context(name: "c1") {}

			context(name: "c2", extends: "c1") {}
		}
		def model1 = mm.getModel()
		mm = mm.make {
			context(name: "c2") {}
		}
		def model2 = mm.getModel()

		then:
		model1.c2.getExtends() == [model1.c1]
		model2.c2.getExtends() == [model2.c1]
	}

	def "it is possible to easily refine a machine"() {
		when:
		def mm = new ModelModifier().make {
			machine(name: "Lift") {
				var_block "level", "level : 0..5", "level := 0"
				event(name: "up") {
					when "level < 5"
					then "level := level + 1"
				}
				event(name: "down") {
					when "level > 0"
					then "level := level - 1"
				}
			}

			refine("Lift", "Lift2")
		}
		def model = mm.getModel()

		then:
		model.getRelationship("Lift2","Lift") == ERefType.REFINES
		model.Lift.variables == model.Lift2.variables
		model.Lift2.events.INITIALISATION.isExtended()
		model.Lift2.events.up.isExtended()
		model.Lift2.events.down.isExtended()
	}
}
