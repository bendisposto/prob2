import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.ModelToXML

final mm = new ModelModifier().make {
	context(name: "c1_ModelElements") {
		set "STATES"
		set "INVARIANTS"
		constant "truth"
		constant "transitions"
		constant "root"
		axiom "truth <: STATES ** INVARIANTS"
		axiom "transitions : STATES <-> STATES"
		axiom "root : STATES"
		axiom "finite(INVARIANTS)"
		enumerated_set name: "MCResult", constants: ["mc_ok", "counter_example", "deadlock"]
	}
	
	context(name: "c4_AnimationStateSpace", extends: "c1_ModelElements") {
		(0..5).each {
			constant "s${it}"
		}
		axiom "partition(STATES,{root},"+(0..5).collect { "{s${it}}"}.iterator().join(",") +")"
		(0..4).each {
			constant "i${it}"
		}
		axiom "partition(INVARIANTS,"+(0..4).collect { "{i${it}}" }.iterator().join(",")+")"
	}

	context(name: "c5_CorrectStateSpace", extends: "c4_AnimationStateSpace") {
		axiom "truth = STATES ** INVARIANTS"
		axiom "transitions = {root|->s0,"+
			(0..4).collect { "s${it}|->s${it+1},s${it+1}|->s${it}" }.iterator().join(",") + "}"
	}
	
	context(name: "c6_InvKOStateSpace", extends: "c4_AnimationStateSpace") {
		axiom "truth = (STATES ** INVARIANTS) \\ {s4|->i3}"
		axiom "transitions = {root|->s0,"+
			(0..4).collect { "s${it}|->s${it+1},s${it+1}|->s${it}" }.iterator().join(",") + "}"
	}
	
	context(name: "c7_DeadlockStateSpace", extends: "c4_AnimationStateSpace") {
		axiom "truth = STATES ** INVARIANTS"
		axiom "transitions = {root|->s0,"+
			(0..4).collect { "s${it}|->s${it+1}" }.iterator().join(",") + "}"
	}
	
	procedure(name: "dequeue", seen: "c1_ModelElements") {
		argument "queue", "POW(STATES)"
		result "newQueue", "POW(STATES)"
		result "element", "STATES"
		
		precondition "queue /= {} & (#z.z : queue)"
		postcondition "element : queue & newQueue = queue \\ {element}"
		
		implementation {
			var "q", "q : POW(STATES)", "q := queue"
			var "e", "e : STATES", "e :: STATES"
			invariant "q <: queue"
			algorithm {
				Assert("q /= {} & q = queue")
				Assign("e :: q")
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
		postcondition "res = bool(!iv.iv : INVARIANTS => s|->iv:truth)"
		
		implementation {
			var "r", "r : BOOL", "r := FALSE"
			var "invs", "invs <: INVARIANTS", "invs := INVARIANTS"
			var "i", "i : INVARIANTS", "i :: INVARIANTS"
			var "checked", "checked <: INVARIANTS", "checked := {}"
			theorem "finite(invs)"
			
			algorithm {
				While("invs /= {}", invariant: "(invs \\/ checked) = INVARIANTS & (!iv.iv : checked => s|->iv:truth)", variant: "card(invs)") {
					Assert("(invs \\/ checked) = INVARIANTS & (!iv.iv : checked => s|->iv:truth)")
					Assert("invs /= {}")
					Assign("i :: invs")
					If ("not(s|->i:truth)") {
						Then {
							Assign("r := FALSE")
							//Assert("r = FALSE & not(s|->i:truth)")
							Assert("r = bool(!iv.iv : INVARIANTS => s|->iv:truth)")
							Return("r")
						}
					}
					Assign("checked := checked \\/ {i}")
					Assign("invs := invs \\ {i}")
				}
				Assign("r := TRUE")
				//Assert("r = TRUE & checked = INVARIANTS & (!iv.iv : INVARIANTS => s|->iv:truth)")
				Assert("r = bool(!iv.iv : INVARIANTS => s|->iv:truth)")
				Return("r")
			}
		}
	}
	
	procedure(name: "successors", seen: "c1_ModelElements") {
		argument "s", "STATES"
		result "successors", "POW(STATES)"
		
		precondition "TRUE=TRUE"
		postcondition "successors = {t | s|->t : transitions}"
		
		implementation {
			var "succs", "succs : POW(STATES)", "succs := {}"
			var "notsuccs", "notsuccs : POW(STATES)", "notsuccs := {}"
			var "unchecked", "unchecked <: ran(transitions)", "unchecked := ran(transitions)"
			var "to", "to : STATES", "to :: STATES"
			algorithm {
				While("unchecked /= {}", invariant: "succs <: {t | s|->t : transitions} & notsuccs <: {t | s|->t /: transitions} & succs \\/ notsuccs \\/ unchecked = ran(transitions)") {
					Assert("succs <: {t | s|->t : transitions} & notsuccs <: {t | s|->t /: transitions} & succs \\/ notsuccs \\/ unchecked = ran(transitions)")
					Call("dequeue",["unchecked"],["unchecked","to"])
					If ("s |-> to : transitions") {
						Then {
							Assign("succs := succs \\/ {to}")
						}
						Else {
							Assign("notsuccs := notsuccs \\/ {to}")
						}
					}
				}
				Assert("succs = {t | s|->t : transitions}")
				Return("succs")
			}
		}
	}
	
	procedure(name: "model_check", seen: "c1_ModelElements") {
		result "result", "MCResult"
		result "state", "STATES"
		
		precondition "TRUE=TRUE"
		postcondition "(result = mc_ok => state = root) &"+
					"(result = counter_example => (#i.i : INVARIANTS & state|->i /: truth)) &"+
					"(result = deadlock => {t | state|->t : transitions} = {})"
		
		implementation {
			var "queue", "queue : POW(STATES)", "queue := {root}"
			var "known", "known : POW(STATES)", "known := {root}"
			var "s", "s : STATES", "s :: STATES"
			var "invok", "invok : BOOL", "invok := TRUE"
			var "res", "res : MCResult", "res :: MCResult"
			var "succs", "succs : POW(STATES)", "succs := {}"
			algorithm {
				While("queue /= {}") {
					Call("dequeue",["queue"],["queue","s"])
					Call("check_inv",["s"],["invok"])
					If ("invok = FALSE") {
						Then {
							Assign("res := counter_example")
							//Assert("res = counter_example & (#i.i : INVARIANTS & s|->i /: truth)")
							Assert("(res = mc_ok => s = root) & (res = counter_example => (#i.i : INVARIANTS & s|->i /: truth)) & (res = deadlock => {t | s|->t : transitions} = {})")
							Return("res", "s")
						}
					}
					Call("successors", ["s"], ["succs"])
					If ("succs = {}") {
						Then {
							Assign("res := deadlock")
							//Assert("res = deadlock & {t | s|->t : transitions} = {}")
							Assert("(res = mc_ok => s = root) & (res = counter_example => (#i.i : INVARIANTS & s|->i /: truth)) & (res = deadlock => {t | s|->t : transitions} = {})")
							Return("res", "s")
						}
					}
					While("succs /= {}") {
						Call("dequeue", ["succs"], ["succs","s"])
						If ("s /: known") {
							Then {
								Assign("queue := queue \\/ {s}")
								Assign("known := known \\/ {s}")
							}
						}
					}
				}
				Assign("res := mc_ok")
				Assign("s := root")
				//Assert("res = mc_ok & s = root")
				Assert("(res = mc_ok => s = root) & (res = counter_example => (#i.i : INVARIANTS & s|->i /: truth)) & (res = deadlock => {t | s|->t : transitions} = {})")
				Return("res","s")
			} 
		}
	}
}

final m = new AlgorithmTranslator(mm.model, new AlgorithmGenerationOptions().DEFAULT.terminationAnalysis(true)).run()

final mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "ModelCheck", "/tmp")
//d.deleteDir()

//s.kill()
"created model of model checking algorithm"
