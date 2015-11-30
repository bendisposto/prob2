import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


final workspace = dir + File.separator + "TheoryExamples"
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
			theorem "x0 mod 2 /= 0 => (x0 / 2) * y0 * 2 = (x0 - 1) * y0"
			theorem "x mod 2 /= 0 => x / 2 * 2 = x - 1"
			theorem "x / 2*y*2 = x / 2*2*y"
			algorithm {
				While("x0 > 0", invariant: "p + (x0*y0) = x*y") {
					If("x0 mod 2 /= 0") {
						Then {
							Assign("p := p + y0")
						}
					}
					Assign("x0 := x0 / 2")
					Assign("y0 := y0 * 2")
					Assert("x0 = 0 => p = x*y")
				}
				Assert("p = x*y")
				Return("p")
			}
		}
	}
	
	/*procedure(name: "map_square") {
		argument "s", "INT +-> INT"
		result "mapped", "INT +-> INT"
		
		precondition "finite(s)"
		postcondition  "mapped = (λx·x∈dom(s) ∣ s(x)∗s(x))"
		//postcondition "mapped : INT +-> INT & dom(s) = dom(mapped) & (!i.i : dom(s) => (mapped(i) = s(i) * s(i)))"
		
		implementation {
			var "key", "key : INT", "key := 0"
			var "value", "value : INT", "value := 0"
			var "res", "res : INT +-> INT", "res := {}"
			var "l", "l : INT +-> INT", "l := s"
			var "element", "element : INT**INT", "element := 0|->0"
			var "product", "product : NAT", "product :: NAT"
			invariant "finite(l)"
			algorithm {
				While("card(l) > 0", invariant: "!i.i : dom(res) & i : dom(s) => res(i) = s(i)*s(i)") {
					Assign("element :: l")
					Assign("key, value :| key'|->value'=element")
					Assert("key : dom(s) & s(key) = value")
					Call("mult", ["value", "value"], ["product"])
					Assert("key : dom(s) & product = s(key) * s(key)")
					Assign("res(key) := product")
					Assign("l := {key} <<| l")
				}
				Return("res")
			}
		}
	}*/
	
	/*machine(name: "apply_mult") {
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
	}*/
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().DEFAULT).run()

mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "MultWithProcedures", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"generate and animate a model"