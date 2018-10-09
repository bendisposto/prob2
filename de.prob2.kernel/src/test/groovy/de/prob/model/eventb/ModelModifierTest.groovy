package de.prob.model.eventb

import de.prob.model.representation.DependencyGraph.ERefType
import de.prob.model.representation.ElementComment

import spock.lang.Specification

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
				var "level", "level : 0..5", "level := 0"
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

	def "deleting machine works"() {
		when:
		def mm = new ModelModifier().make {
			context(name: "ctx0") {}
			context(name: "ctx1", "extends": "ctx0") {}
			machine(name: "mch0", sees: ["ctx0"]) {}
			machine(name: "mch1", refines: "mch0", sees: ["ctx0", "ctx1"]) {}
		}
		def model = mm.getModel()
		def model2 = mm.removeMachine(mm.getModel().mch0).getModel()

		then:
		model.getGraph().getVertices() == [
			"ctx0",
			"ctx1",
			"mch0",
			"mch1"] as Set
		model.getRelationship("ctx1", "ctx0") == ERefType.EXTENDS
		model.getRelationship("mch0", "ctx0") == ERefType.SEES
		model.getRelationship("mch1", "ctx0") == ERefType.SEES
		model.getRelationship("mch1", "ctx1") == ERefType.SEES
		model.getRelationship("mch1", "mch0") == ERefType.REFINES

		model2.getGraph().getVertices() == [
			"ctx0",
			"ctx1",
			"mch1"] as Set
		model2.getRelationship("ctx1", "ctx0") == ERefType.EXTENDS
		model2.getRelationship("mch1", "ctx0") == ERefType.SEES
		model2.getRelationship("mch1", "ctx1") == ERefType.SEES
		model2.graph.getIncomingEdges("ctx0").size() == 2
		model2.graph.getIncomingEdges("ctx1").size() == 1
		model2.graph.getIncomingEdges("mch1").size() == 0
		model2.mch1.getRefines().isEmpty()
	}

	def "deleting context works"() {
		when:
		def mm = new ModelModifier().make {
			context(name: "ctx0") {}
			context(name: "ctx1", "extends": "ctx0") {}
			machine(name: "mch0", sees: ["ctx0"]) {}
			machine(name: "mch1", refines: "mch0", sees: ["ctx0", "ctx1"]) {}
		}
		def model = mm.getModel()
		def model2 = mm.removeContext(mm.getModel().ctx0).getModel()

		then:
		model.getGraph().getVertices() == [
			"ctx0",
			"ctx1",
			"mch0",
			"mch1"] as Set
		model.getRelationship("ctx1", "ctx0") == ERefType.EXTENDS
		model.getRelationship("mch0", "ctx0") == ERefType.SEES
		model.getRelationship("mch1", "ctx0") == ERefType.SEES
		model.getRelationship("mch1", "ctx1") == ERefType.SEES
		model.getRelationship("mch1", "mch0") == ERefType.REFINES

		model2.getGraph().getVertices() == [
			"mch0",
			"ctx1",
			"mch1"] as Set
		model.getRelationship("mch1", "mch0") == ERefType.REFINES
		model2.getRelationship("mch1", "ctx1") == ERefType.SEES
		model2.graph.getIncomingEdges("ctx1").size() == 1
		model2.graph.getIncomingEdges("mch0").size() == 1
		model2.graph.getIncomingEdges("mch1").size() == 0
		model2.mch0.getSees().isEmpty()
		model2.mch1.getSees().size() == 1
		model2.ctx1.getExtends().isEmpty()
	}

	def "replacing machine (2) works"() {
		when:
		def mm = new ModelModifier().make {
			context(name: "ctx0") {}
			context(name: "ctx1", "extends": "ctx0") {}
			machine(name: "mch0", sees: ["ctx0"]) {}
			machine(name: "mch1", refines: "mch0", sees: ["ctx0", "ctx1"]) {}
		}
		def model = mm.getModel()
		def m = new MachineModifier(new EventBMachine("mymch")).setSees(model.mch0.getSees()).getMachine()
		def model2 = mm.replaceMachine(mm.getModel().mch0, m).getModel()

		then:
		model.getGraph().getVertices() == [
			"ctx0",
			"ctx1",
			"mch0",
			"mch1"] as Set
		model.getRelationship("ctx1", "ctx0") == ERefType.EXTENDS
		model.getRelationship("mch0", "ctx0") == ERefType.SEES
		model.getRelationship("mch1", "ctx0") == ERefType.SEES
		model.getRelationship("mch1", "ctx1") == ERefType.SEES
		model.getRelationship("mch1", "mch0") == ERefType.REFINES

		model2.getGraph().getVertices() == [
			"ctx0",
			"ctx1",
			"mymch",
			"mch1"] as Set
		model2.getRelationship("ctx1", "ctx0") == ERefType.EXTENDS
		model2.getRelationship("mymch", "ctx0") == ERefType.SEES
		model2.getRelationship("mch1", "ctx0") == ERefType.SEES
		model2.getRelationship("mch1", "ctx1") == ERefType.SEES
		model2.getRelationship("mch1", "mymch") == ERefType.REFINES
		model2.mch1.getRefines() == [m]
	}

	def "replacing context (2) works"() {
		when:
		def mm = new ModelModifier().make {
			context(name: "ctx0") {}
			context(name: "ctx1", "extends": "ctx0") {}
			machine(name: "mch0", sees: ["ctx0"]) {}
			machine(name: "mch1", refines: "mch0", sees: ["ctx0", "ctx1"]) {}
		}
		def model = mm.getModel()
		def ctx = new ContextModifier(new Context("myctx")).getContext()
		def model2 = mm.replaceContext(mm.getModel().ctx0, ctx).getModel()

		then:
		model.getGraph().getVertices() == [
			"ctx0",
			"ctx1",
			"mch0",
			"mch1"] as Set
		model.getRelationship("ctx1", "ctx0") == ERefType.EXTENDS
		model.getRelationship("mch0", "ctx0") == ERefType.SEES
		model.getRelationship("mch1", "ctx0") == ERefType.SEES
		model.getRelationship("mch1", "ctx1") == ERefType.SEES
		model.getRelationship("mch1", "mch0") == ERefType.REFINES

		model2.getGraph().getVertices() == [
			"myctx",
			"ctx1",
			"mch0",
			"mch1"] as Set
		model2.getRelationship("ctx1", "myctx") == ERefType.EXTENDS
		model2.getRelationship("mch0", "myctx") == ERefType.SEES
		model2.getRelationship("mch1", "myctx") == ERefType.SEES
		model2.getRelationship("mch1", "ctx1") == ERefType.SEES
		model2.getRelationship("mch1", "mch0") == ERefType.REFINES
		model2.mch0.getSees() == [ctx]
		model2.mch1.getSees().contains(ctx)
		model2.ctx1.getExtends() == [ctx]
	}

	def "load theories map cannot be empty"() {
		when:
		mm = new ModelModifier().loadTheories([:])

		then:
		thrown IllegalArgumentException
	}

	def "load theories requires workspace"() {
		when:
		def mm = new ModelModifier().loadTheories([project: "MyProject", theories: []])

		then:
		thrown IllegalArgumentException
	}

	def "load theories requires project"() {
		when:
		def mm = new ModelModifier().loadTheories([workspace: "MyWkspc", theories: []])

		then:
		thrown IllegalArgumentException
	}

	def "load theories requires theories"() {
		when:
		def mm = new ModelModifier().loadTheories([workspace: "MyWkspc", project: "MyProject"])

		then:
		thrown IllegalArgumentException
	}

	def "load theories theories cannot be null"() {
		when:
		def mm = new ModelModifier().loadTheories([workspace: "MyWkspc", project: "MyProject", theories: null])

		then:
		thrown IllegalArgumentException
	}

	def "load theories workspace cannot be null"() {
		when:
		def mm = new ModelModifier().loadTheories([workspace: null, project: "MyProject", theories: ["Atheory"]])

		then:
		thrown IllegalArgumentException
	}

	def "load theories project cannot be null"() {
		when:
		def mm = new ModelModifier().loadTheories([workspace: "MyWkspc", project: null, theories: ["Atheory"]])

		then:
		thrown IllegalArgumentException
	}

	def "load theories single theories cannot be null"() {
		when:
		def mm = new ModelModifier().loadTheories([workspace: "MyWkspc", project: "MyProject", theories: [null]])

		then:
		thrown IllegalArgumentException
	}
}
