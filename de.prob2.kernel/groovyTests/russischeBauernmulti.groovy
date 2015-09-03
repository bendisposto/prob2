import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmTranslator2
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


mm = new ModelModifier().make {
	
	context(name: "limits") {
		constants "m", "n"
		
		axioms "m : NAT",
		       "n : NAT",
			   "m = 27",
		       "n = 82"
	}
	
	machine(name: "multiplication", sees: ["limits"]) {
		var_block name: "l", invariant: "l : NAT", init: "l := m"
		var_block name: "r", invariant: "r : NAT", init: "r := n"
		var_block name: "product", invariant: "product : NAT", init: "product := 0"
		
		algorithm {
			While("l /= 0") {
				If("l mod 2 /= 0") {
					Then("product := product + r")
				}
				Assign("l := l / 2", "r := r * 2")
			}
			Assert("product = m * n")
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator2(m).run()

mtx = new ModelToXML()
d = mtx.writeToRodin(m, "Bauern", "/home/joy/workspace/")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"