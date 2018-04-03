import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator

final mm = new ModelModifier().make {
	context(name: "symbols") {
		set "Symbols"
		constants "N", "T", "String", "G"
		
		axioms "partition(Symbols,N,T)",
			"String = {w|w:1..card(w) --> Symbols}",
			"G : N <-> String",
			"finite(G)"
	}
	
	context(name: "symbols_prob", extends: "symbols") {
		constants "a", "b", "c", "S", "R"
		axioms "partition(T,{a},{b},{c})",
			"partition(N,{S},{R})",
			"G = { S |-> {1|->a,2|->S,3|->b}, S |-> {1|->c}, R |-> {}, R |-> {1|->S, 2|->S}}"
	}
	
	machine(name: "ll_parsing", sees: ["symbols_prob"]) {
		var "nullable", "nullable : POW(Symbols)", "nullable:={}" 
		var "chng", "chng : BOOL", "chng := TRUE"
		var "worklist", "worklist <: G", "worklist := G"
		var "next", "next : G", "next :: G"
		
		algorithm {
			While("chng = TRUE", variant: "2*(card(Symbols) - card(nullable)) + {TRUE|->1,FALSE|->0}(chng)") {
				Assign("chng := FALSE")
				While("worklist /= {}", invariant: "worklist <: G & next : G", variant: "card(worklist)") {
					// Assert("worklist /= {}")
					Assign("next :: worklist")
					Assign("worklist := worklist \\ {next}")
					If ("prj1(next) /: nullable & ran(prj2(next)) <: nullable") {
						Then {
							Assign("nullable := nullable \\/ {prj1(next)}")
							Assign("chng := TRUE")
						}
					}
				}
			}
		}
	}
}

final m = new AlgorithmTranslator(mm.model, new AlgorithmGenerationOptions().DEFAULT.terminationAnalysis(true)).run()

"generate and animate a model"
