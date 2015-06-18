import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.translate.*
import de.prob.statespace.*

mm = new ModelModifier()
mm.make {
	
	context(name: "definitions") {
		constants "m", "n", "k"
		axioms "m : 1..k",
		       "n : 1..k",
			   "k = 100"
	}
	
	machine(name: "euclid", sees: ["definitions"]) {
		variables "u", "v"
		invariants "u : 1..100",
				   "v : 1..100"
		initialisation {
			actions "u := m",
					"v := n"
		}
		event(name: "switch") {
			then "u := v",
			     "v := u"
		}
	}
}

m = mm.getModifiedModel()
s = m.load(m.euclid)
t = s as Trace
println t.getNextTransitions(true)


for (op in t.getNextTransitions(true)) {
	t1 = t.add(op).$initialise_machine()
	println t1
	println t1.evalCurrent("m")
	println t1.evalCurrent("n")
	println t1.evalCurrent("u")
	println t1.evalCurrent("v")
	println ""
	t2 = t1.switch()
	println t2.evalCurrent("u")
	println t2.evalCurrent("v")
}

//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "BowlOfCherries", dir)
//d.deleteDir()

s.animator.cli.shutdown();
"generate and animate a model"