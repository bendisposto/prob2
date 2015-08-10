package de.prob.model.eventb.algorithm

import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.Variant
import de.prob.model.representation.Machine

class AlgorithmTranslator {
	def EventBModel model
	def MachineModifier machineM

	def AlgorithmTranslator(EventBModel model) {
		this.model = model
	}

	def EventBModel run() {
		model.getMachines().each { oldM ->
			model = model.replaceIn(Machine.class, oldM, runAlgorithm(oldM))
		}
		model
	}

	def EventBMachine runAlgorithm(EventBMachine machine) {
		def nextpc = 0
		machineM = new MachineModifier(machine)
		machineM = machineM.var_block("pc", "pc : NAT", "pc := 0")
		machine.getChildrenOfType(Block.class).each { Block b ->
			machineM = machineM.addComment(new AlgorithmPrettyPrinter(b).prettyPrint())
			nextpc = translate(nextpc, b)
		}
		machineM = machineM.event(name: "evt$nextpc") {
			guard("pc = $nextpc")
		}
		machineM.getMachine()
	}

	def int nextpc(int pc, Assertion s) {
		pc + 1
	}

	def int nextpc(int pc, Assignments s) {
		pc + 1
	}

	def int nextpc(int pc, While s) {
		2 + nextpc(pc, s.block)
	}

	def int nextpc(int pc, Block b) {
		b.statements.inject(pc) { pc2, s -> nextpc(pc2, s) }
	}

	def int nextpc(int pc, If s) {
		if (s.Else.statements.isEmpty()) {
			return 1 + nextpc(pc, s.Then)
		}
		nextpc(nextpc(pc + 1, s.Then) + 1, s.Else)
	}

	def translate(int pc, Block block) {
		block.statements.inject(pc) { pc2, statement ->
			translate(pc2, statement)
		}
	}

	def int translate(int pc, While statement) {
		def enter_while_name = "evt${pc}_enter_while"
		def npc = pc + 1
		def exitpc = nextpc(pc, statement)
		machineM = machineM.event(name: enter_while_name, comment: statement.toString()) {
			guard("pc = $pc")
			guard(statement.condition)
			action("pc := $npc")
		}

		npc = translate(npc, statement.block)
		def loop_name = "evt${npc}_loop"
		machineM = machineM.event(name: loop_name) {
			guard("pc = $npc")
			action("pc := $pc")
		}
		if (statement.variant) {
			def machine = machineM.getMachine()
			def loopInfo = new LoopInformation(new Variant(statement.variant, Collections.emptySet()), machine.events.getElement(enter_while_name), machine.events.getElement(loop_name), pc, npc)
			machineM = new MachineModifier(machine.addTo(LoopInformation.class, loopInfo))
		}

		def name = "evt${pc}_exit_while"
		machineM = machineM.event(name: name) {
			guard("pc = $pc")
			guard("not(${statement.condition})")
			action("pc := $exitpc")
		}
		exitpc
	}

	def int translate(int pc, If statement) {
		def name = "evt${pc}_if"
		def npc = pc + 1
		def exitpc = nextpc(pc, statement)
		machineM = machineM.event(name: name, comment: statement.toString()) {
			guard("pc = $pc")
			guard(statement.condition)
			action("pc := $npc")
		}
		npc = translate(npc, statement.Then)
		if (statement.Else.statements.isEmpty()) {
			machineM = machineM.event(name: "evt${pc}_else") {
				guard("pc = $pc")
				guard("not(${statement.condition})")
				action("pc := $npc")
			}
			return npc
		}

		machineM = machineM.event(name: "evt${npc}_exit_if") {
			guard("pc = $npc")
			action("pc := $exitpc")
		}
		npc = npc + 1
		machineM = machineM.event(name: "evt${pc}_else") {
			guard("pc = $pc")
			guard("not(${statement.condition})")
			action("pc := $npc")
		}
		translate(npc, statement.Else)
	}

	def int translate(int pc, Assignments statement) {
		def name = "evt$pc"
		def nextpc = pc + 1
		machineM = machineM.event(name: name, comment: statement.toString()) {
			guard("pc = $pc")
			action("pc := $nextpc")
			actions(statement.assignments as String[])
		}
		nextpc
	}

	def int translate(int pc, Assertion statement) {
		def name = "evt$pc"
		def nextpc = pc + 1
		machineM = machineM.event(name: name, comment: statement.toString()) {
			guard("pc = $pc")
			theorem(statement.assertion)
			action("pc := $nextpc")
		}
		nextpc
	}
}
