package de.prob.model.eventb.algorithm.ast.transform

import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.transform.DeadCodeRemover
import de.prob.model.eventb.algorithm.graph.NodeNaming

import spock.lang.Specification

class DeadCodeRemovalTest extends Specification {

	def Block astTrans(Closure cls) {
		Block b = new Block().make(cls)
		return new DeadCodeRemover().transform(b)
	}

	def NodeNaming naming(Block ast) {
		return new NodeNaming(ast)
	}

	def "return cuts off dead code"() {
		when:
		def ast = astTrans({
			If ("x = 1") {
				Then {
					Assign("x := 2")
					Return("x")
					Assign("y := 4")
					Return("y")
				}
			}
			While("z = 2") {
				Assign("y := 4")
				Return("y")
				If("x + 1 > 5") { Then("z := 24") }
			}
			Return("m")
			Assign("x := 0")
		})

		then:
		naming(ast).nodes == [
			if0: ast.statements[0],
			assign0: ast.statements[0].Then.statements[0],
			return0: ast.statements[0].Then.statements[1],
			while0: ast.statements[1],
			assign1: ast.statements[1].block.statements[0],
			return1: ast.statements[1].block.statements[1],
			return2: ast.statements[2]
		]
	}
}
