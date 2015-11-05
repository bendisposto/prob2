import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


mm = new ModelModifier().make {
	
	procedure(name: "mult") {
		argument "x", "NAT"
		argument "y", "NAT"
		result "product", "NAT"
		
		precondition "x >= 0 & y >= 0"
		postcondition "product = x * y"
			
		implementation {
			var "x0", "x0 : NAT", "x0 := x"
			var "y0", "y0 : NAT", "y0 := y"
			var "p", "p : NAT", "p := 0"
			algorithm {
				While("x0 > 0", invariant: "p + (x0*y0) = x*y") {
					If("x0 mod 2 /= 0") {
						Then {
							Assume("x0 / 2 * 2 = x0 - 1")
							Assign("x0 := x0 / 2", "y0 := y0 * 2", "p := p + y0")
						}
						Else("x0 := x0 / 2", "y0 := y0 * 2")
					}
				}
				Assert("p = x * y")
				Return("p")
			}
		}
		
	}
	
	machine(name: "apply_mult") {
		var "product", "product : NAT", "product := 0"
		var "ctr", "ctr : NAT", "ctr := 0"
		var "s", "s : NAT +-> INT", "s := {}"
		
		algorithm {
			While("ctr < 10", invariant: "!i.i : dom(s) => s(i) = i*i") {
				Call("mult", ["ctr", "ctr"], ["product"])
				Assert("product = ctr*ctr")
				Assert( "!i.i : dom(s) => s(i) = i*i")
				Assign("s(ctr) := product","ctr := ctr + 1")
			}
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().DEFAULT).run()

mtx = new ModelToXML()
d = mtx.writeToRodin(m, "MultWithProcedures", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"