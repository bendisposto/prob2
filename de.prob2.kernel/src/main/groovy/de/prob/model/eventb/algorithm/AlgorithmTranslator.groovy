package de.prob.model.eventb.algorithm

import de.prob.model.eventb.MachineModifier

class AlgorithmTranslator {
	def MachineModifier machineM

	def AlgorithmTranslator(MachineModifier machineM) {
		this.machineM = machineM
	}

	def MachineModifier create(Block block) {
		machineM.var_block("pc", "pc : NAT", "pc := 0")
		def nextpc = translate(0, block)
		machineM.addEvent("evt$nextpc").guard("pc = $nextpc")
		machineM
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
		def name = "evt${pc}_enter_while"
		def npc = pc + 1
		def exitpc = nextpc(pc, statement)
		machineM.addEvent(name)
				.guard("pc = $pc")
				.guard(statement.condition)
				.action("pc := $npc")
		npc = translate(npc, statement.block)
		machineM.addEvent("evt${npc}_loop")
				.guard("pc = $npc")
				.action("pc := $pc")
		name = "evt${pc}_exit_while"
		machineM.addEvent(name)
				.guard("pc = $pc")
				.guard("not(${statement.condition})")
				.action("pc := $exitpc")
		exitpc
	}

	def int translate(int pc, If statement) {
		def name = "evt${pc}_if"
		def npc = pc + 1
		def exitpc = nextpc(pc, statement)
		machineM.addEvent(name)
				.guard("pc = $pc")
				.guard(statement.condition)
				.action("pc := $npc")
		npc = translate(npc, statement.Then)
		if (statement.Else.statements.isEmpty()) {
			return npc
		}

		machineM.addEvent("evt${npc}_exit_if")
				.guard("pc = $npc")
				.action("pc := $exitpc")
		npc = npc + 1
		machineM.addEvent("evt${pc}_else")
				.guard("pc = $pc")
				.guard("not(${statement.condition})")
				.action("pc := $npc")
		translate(npc, statement.Else)
	}

	def int translate(int pc, Assignments statement) {
		def name = "evt$pc"
		def nextpc = pc + 1
		def eventM = machineM.addEvent(name)
				.guard("pc = $pc")
				.action("pc := $nextpc")
		statement.assignments.each { eventM.action(it) }
		nextpc
	}

	def int translate(int pc, Assertion statement) {
		def name = "evt$pc"
		def nextpc = pc + 1
		machineM.addEvent(name)
				.guard("pc = $pc")
				.theorem(statement.assertion)
				.action("pc := $nextpc")
		nextpc
	}
}
