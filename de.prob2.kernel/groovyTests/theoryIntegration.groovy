import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.theory.Theory
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.eventb_load(dir+File.separator+"TheoryExamples"+File.separator+"Sequences"+File.separator+"Mch.bcm")
t = s as Trace
t = t.$initialise_machine()
t = t.$add("p = 5")
assert t.evalCurrent("s(1)").value == "5"
t = t.$add("p = 9")
assert t.evalCurrent("s(2)").value == "9"
m = s.getModel()
assert !m.getChildrenOfType(Theory.class).Seq.getTypeEnvironment().isEmpty()

final workspace = dir + File.separator + "TheoryExamples"
m = new ModelModifier().make {
	
	loadTheories workspace: workspace,
				project:  "BasicTheory",
				theories: ["Seq"]
	
	machine(name: "UseSeq") {
		var_block "s", "s : seq(INT)", "s := emptySeq"
		
		event(name: "add") {
			any "p"
			where "p : 1..10"
			then "s := seqAppend(s,p)"
		}
	}
}.getModel()
s = m.load(m.UseSeq)
t = s as Trace
t = t.$initialise_machine()
t = t.$add("p = 5")
assert t.evalCurrent("s(1)").value == "5"
t = t.$add("p = 9")
assert t.evalCurrent("s(2)").value == "9"

//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "Example", "/tmp")
//d.deleteDir()

"it is possible to load or create and animate Event-B models that use the theory plugin"