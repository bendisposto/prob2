import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.*
import de.prob.statespace.*

/*
 * x≔ 10
   while (x > 0)
     x≔ x + 1
     x ≔ x − 2
     variant x
   end
 */

mm = new ModelModifier().make {
	machine(name: "MyLoop") {
		var_block "x", "x : NAT", "x := 10"
		invariant "x : 0..11"
		algorithm {
			While("x > 0", variant: "x") {
				Assign("x := x + 1")
				Assign("x := x - 2")
			}
		}
	}
}

def guards(evt) {
	evt.guards.collect { it.getPredicate().getCode() }
}

def actions(evt) {
	evt.actions.collect { it.getCode().getCode() }
}

m = new AlgorithmTranslator(mm.getModel(), new AlgorithmGenerationOptions()).run()
//m = new NaiveTerminationAnalysis(m).run()
//e = m.MyLoop
//TODO: retranslate this
//evt0_enter_while = e.events.evt0_enter_while
//assert evt0_enter_while != null
//assert guards(evt0_enter_while) == ["pc = 0", "x > 0"]
//assert actions(evt0_enter_while) == ["pc := 1"]
//
//evt0_exit_while = e.events.evt0_exit_while
//assert evt0_exit_while != null
//assert guards(evt0_exit_while) == ["pc = 0", "not(x > 0)"]
//assert actions(evt0_exit_while) == ["pc := 4"]
//
//evt1 = e.events.evt1
//assert evt1 != null
//assert guards(evt1) == ["pc = 1"]
//assert actions(evt1) == ["pc := 2", "x := x + 1"]
//
//evt2 = e.events.evt2
//assert evt2 != null
//assert guards(evt2) == ["pc = 2"]
//assert actions(evt2) == ["pc := 3", "x := x - 2"]
//
//evt3_loop = e.events.evt3_loop
//assert evt3_loop != null
//assert guards(evt3_loop) == ["pc = 3"]
//assert actions(evt3_loop) == ["pc := 0"]
//
//evt4 = e.events.evt4
//assert evt4 != null
//assert guards(evt4) == ["pc = 4"]
//assert actions(evt4) == []
//
//loopInfos = e.getChildrenOfType(LoopInformation.class)
//assert loopInfos.size() == 1
//loopInfo = loopInfos[0]
//assert loopInfo.variant.getExpression().getCode() == "x"
//assert loopInfo.loopStatements[0].getName() == "evt3_loop"
//
//e = m.MyLoop_while0
//assert e.events.INITIALISATION.isExtended()
//assert actions(e.events.INITIALISATION) == ["var := 10"]
//assert e.events.evt0_enter_while.isExtended()
//assert e.events.evt0_exit_while.isExtended()
//assert e.events.evt1.isExtended()
//assert e.events.evt2.isExtended()
//assert e.events.evt4.isExtended()
//
//evt3_loop = e.events.evt3_loop
//assert evt3_loop.isExtended()
//assert actions(evt3_loop) == ["var := x"]




//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "MyLoopProject", "/tmp")

"it is possible to generate models to check the termination of loops"