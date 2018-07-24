import java.nio.file.Paths

import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.theory.Theory
import de.prob.statespace.Trace

final s1 = api.eventb_load(Paths.get(dir, "TheoryExamples", "Sequences", "Mch.bcm").toString())
def t = s1 as Trace
t = t.$initialise_machine()
t = t.$add("p = 5")
assert t.evalCurrent("s(1)").value == "5"
t = t.$add("p = 9")
assert t.evalCurrent("s(2)").value == "9"
final m1 = s1.model
assert !m1.getChildrenOfType(Theory.class).Seq.typeEnvironment.empty

final workspace = Paths.get(dir, "TheoryExamples").toString()
final m2 = new ModelModifier().make {
	loadTheories workspace: workspace,
		project: "BasicTheory",
		theories: ["Seq"]
	
	machine(name: "UseSeq") {
		var "s", "s : seq(INT)", "s := emptySeq"
		
		event(name: "add") {
			any "p"
			where "p : 1..10"
			then "s := seqAppend(s,p)"
		}
	}
}.model
final s2 = m2.load(m2.UseSeq)
t = s2 as Trace
t = t.$initialise_machine()
t = t.$add("p = 5")
assert t.evalCurrent("s(1)").value == "5"
t = t.$add("p = 9")
assert t.evalCurrent("s(2)").value == "9"

"it is possible to load or create and animate Event-B models that use the theory plugin"
