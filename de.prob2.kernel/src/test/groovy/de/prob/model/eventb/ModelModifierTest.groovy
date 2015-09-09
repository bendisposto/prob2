package de.prob.model.eventb

import spock.lang.Specification
import de.prob.model.representation.ElementComment
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
		model3.m1.getSees() == [
			model3.c1,
			model3.c2,
			model3.c3
		]
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

	def "adding a machine with a comment works"() {
		when:
		def mycomment = "This is a comment!"
		def mm = new ModelModifier().make {
			machine(name: "Lift", comment: mycomment) {
			}
		}

		then:
		mm.getModel().Lift.getChildrenOfType(ElementComment.class).collect { it.getComment() } == [mycomment]
	}

	def "if refining a machine, it must already be there"() {
		when:
		def mm = new ModelModifier().make {
			machine(name: "Lift", refines: "IDonTExist") {
			}
		}

		then:
		thrown(IllegalArgumentException)
	}

	def "when a machine sees a context, it must be there"() {
		when:
		def mm = new ModelModifier().make {
			machine(name: "Lift", sees: ["IDonTExist"]) {
			}
		}

		then:
		thrown(IllegalArgumentException)
	}

	def "when refining a machine it must be there"() {
		when:
		def mm = new ModelModifier().make { refine("Lift", "Lift2")  }

		then:
		thrown(IllegalArgumentException)
	}


	def "adding a context with a comment works"() {
		when:
		def mycomment = "This is a comment!"
		def mm = new ModelModifier().make {
			context(name: "ctx", comment: mycomment) {
			}
		}

		then:
		mm.getModel().ctx.getChildrenOfType(ElementComment.class).collect { it.getComment() } == [mycomment]
	}

	def "when extending a context, it must be there already"() {
		when:
		def mm = new ModelModifier().make {
			context(name: "ctx", extends: "IDonTExist") {
			}
		}

		then:
		thrown(IllegalArgumentException)
	}

	def "if the extended context is changed, this relationship is removed from the model"() {
		when:
		def mm = new ModelModifier().make {
			context(name: "IDoNothing") {}
			context(name: "IAlsoDoNothing", extends: "IDoNothing") {}
		}
		def beforemodel = mm.getModel()
		mm = new ModelModifier(beforemodel).make {
			context(name: "YetAnotherNothing") {}
			context(name: "IAlsoDoNothing", extends: "YetAnotherNothing") {}
		}
		def aftermodel = mm.getModel()

		then:
		beforemodel.getContexts().collect { it.getName() } == [
			"IDoNothing",
			"IAlsoDoNothing"
		]
		beforemodel.getGraph().getVertices() == [
			"IDoNothing",
			"IAlsoDoNothing"] as Set
		beforemodel.getRelationship("IAlsoDoNothing", "IDoNothing") == ERefType.EXTENDS

		aftermodel.getContexts().collect { it.getName() } == [
			"IDoNothing",
			"IAlsoDoNothing",
			"YetAnotherNothing"
		]
		aftermodel.getGraph().getVertices() == [
			"IDoNothing",
			"IAlsoDoNothing",
			"YetAnotherNothing"] as Set
		aftermodel.getRelationship("IAlsoDoNothing", "YetAnotherNothing") == ERefType.EXTENDS
		aftermodel.getRelationship("IAlsoDoNothing", "IDoNothing") == null
	}

	def "replacing contexts works"() {
		when:
		def mm = new ModelModifier().make {
			context(name: "A") {}
			context(name: "B", extends: "A") {}
			context(name: "C", extends: "B") {}
		}
		def model = mm.getModel()
		def ctx = model.B
		def ctx2 = new ContextModifier(ctx).make {
			enumerated_set name: "MySet",
			constants: ["a", "b", "c"]
		}.getContext()
		mm = mm.replaceContext(ctx, ctx2)
		def model2 = mm.getModel()

		then:
		model.getContexts().collect { it.getName() } == ["A", "B", "C"]
		model.getContexts().B.sets == []
		model.getContexts().B.constants == []
		model2.getContexts().collect { it.getName() } == ["A", "B", "C"]
		model2.getContexts().B.sets.collect { it.getName() } == ["MySet"]
		model2.getContexts().B.constants.collect { it.getName() } == ["a", "b", "c"]
	}

	def "replacing machine works"() {
		when:
		def mm = new ModelModifier().make {
			machine(name: "A") {}
			machine(name: "B", refines: "A") {}
			machine(name: "C", refines: "B") {}
		}
		def model = mm.getModel()
		def mch = model.B
		def mch2 = new MachineModifier(mch, [] as Set).make { variables "x", "y" }.getMachine()
		mm = mm.replaceMachine(mch, mch2)
		def model2 = mm.getModel()

		then:
		model.getMachines().collect { it.getName() } == ["A", "B", "C"]
		model.getMachines().B.variables == []
		model2.getMachines().collect { it.getName() } == ["A", "B", "C"]
		model2.getMachines().B.variables.collect { it.getName() } == ["x", "y"]
	}
}
