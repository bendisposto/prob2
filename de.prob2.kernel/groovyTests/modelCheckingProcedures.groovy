import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.ModelToXML
import de.prob.statespace.*

// You can change the model you are testing here.
mm = new ModelModifier().make {
	
	context(name: "c1_ModelElements") {
		set "STATES"
		set "INVARIANTS"
		set "EVENTS"
		constant "truth"
		axiom "truth <: STATES ** INVARIANTS"
	}
	
	context(name: "c2_StateSpace", extends: "c1_ModelElements") {
		constants "transitions", "root"
		axioms "transitions : STATES <-> STATES",
				"root : STATES"
	}
	
	context(name: "c3_ModelCheckResults") {
		enumerated_set name: "MCResult",
			constants: ["mc_ok","counter_example", "deadlock"] 
	}
	
	procedure(name: "dequeue", seen: "c1_ModelElements") {
		argument "queue", "POW(STATES)"
		result "newQueue", "POW(STATES)"
		result "element", "STATES"
		
		precondition "queue /= {}"
		postcondition "element : queue & newQueue = queue \\ {element}"
		
		implementation {
			var "q", "q : POW(STATES)", "q := queue"
			var "e", "e : STATES", "e :: STATES"
			invariant "q <: queue"
			algorithm {
				Assert("q /= {}")
				Assert("q = queue")
				Assign("e :: q")
				Assert("e : queue & q = queue")
				Assign("q := q \\ {e}")
				Assert("e : queue & q = queue \\ {e}")
				Return("q", "e")
			}
		}
	}
	
	procedure(name: "check_inv", seen: "c1_ModelElements") {
		argument "s", "STATES"
		result "res", "BOOL"
		
		precondition "TRUE=TRUE"
		postcondition "res = bool(!i.i : INVARIANTS => s|->i:truth)"
		
		implementation {
			var "r", "r : BOOL", "r := FALSE"
			var "invs", "invs <: INVARIANTS", "invs := INVARIANTS"
			var "i", "i : INVARIANTS", "i :: INVARIANTS"
			var "checked", "checked <: INVARIANTS", "checked := {}"
			
			algorithm {
				While("invs /= {}", invariant: "(invs \\/ checked) = INVARIANTS & (!iv.iv : checked => s|->iv:truth)") {
					Assign("i :: invs")
					If ("not(s|->i:truth)") {
						Then {
							Assign("r := FALSE")
							Assert("r = FALSE")
							Assert("not(s|->i:truth)")
							Return("r")
						}
					}
					Assert("s|->i:truth")
					Assign("checked := checked \\/ {i}")
					Assign("invs := invs \\ {i}")
				}
				Assert("checked = INVARIANTS")
				Assign("r := TRUE")
				Assert("r = TRUE")
				Assert("checked = INVARIANTS & (!iv.iv : INVARIANTS => s|->iv:truth)")
				Return("r")
			}
		}
	}
	
	procedure(name: "take_element", "seen": "c2_StateSpace") {
		argument "trans", "STATES <-> STATES"
		result "from", "STATES"
		result "to", "STATES"
		result "tail", "STATES <-> STATES"
		
		precondition "trans /= {}"
		postcondition "from|->to : trans & tail = trans \\ {from|->to}"
		
		implementation {
			var "res", "res : STATES <-> STATES", "res := trans"
			var "e", "e : STATES ** STATES", "e :: STATES ** STATES"
			var "f", "f : STATES", "f :: STATES"
			var "t", "t : STATES", "t :: STATES"
			algorithm {
				Assert("trans /= {}")
				Assert("res = trans")
				Assign("e :: res")
				Assert("e : res & res = trans")
				Assign("f,t :| f'|->t'=e")
				Assert("e : res & f|->t = e & res = trans")
				Assign("res := res \\ {e}")
				Assert("f|->t = e & f|->t : trans & res = trans \\ {e}")
				Return("f","t","res")
			}
		}
	}
	
	procedure(name: "successors", seen: "c2_StateSpace") {
		argument "s", "STATES"
		result "successors", "POW(STATES)"
		
		precondition "TRUE=TRUE"
		postcondition "successors = {t | s|->t : transitions}"
		
		implementation {
			var "succs", "succs : POW(STATES)", "succs := {}"
			var "notsuccs", "succs : POW(STATES)", "notsuccs := {}"
			var "trans", "trans <: transitions", "trans := transitions"
			var "from", "from : STATES", "from :: STATES"
			var "to", "to : STATES", "to :: STATES"
			algorithm {
				While("trans /= {}", invariant: "succs <: {t | s|->t : transitions}") {
					Call("take_element",["trans"],["from","to","trans"])
					If ("from = s") {
						Then {
							Assign("succs := succs \\/ {to}")
						}
						Else {
							Assign("notsuccs := notsuccs \\/ {to}")
						}
					}
				}
				Return("succs")
			}
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().propagateAssertions(true).optimize(true)).run()

mtx = new ModelToXML()
d = mtx.writeToRodin(m, "ModelChecking", "/tmp")
//d.deleteDir()

//s.animator.cli.shutdown();
"created model of model checking algorithm"