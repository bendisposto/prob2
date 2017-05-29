import de.prob.animator.domainobjects.*
import de.prob.statespace.*
import de.prob.model.eventb.translate.*
import static de.prob.model.eventb.Event.EventType.CONVERGENT
import static de.prob.model.eventb.Event.EventType.ANTICIPATED

// You can change the model you are testing here.

mm = new ModelModifier().make {
	context(name: "cd") {
		constant "d"
		axioms "d : NAT",
				"d > 0"
	}
	
	machine(name: "m0", sees: ["cd"]) {
		var "n", "n : NAT", "n := 0"
		/*var_block name: "n",
		          invariant: "n : NAT",
				  init: "n:=0"*/
		invariant "n <= d"
		theorem "n > 0 or n < d"
		
		event(name: "ML_out") {
			when "n<d"
			then "n:=n+1"
		}
		
		event(name: "ML_in") {
			when "n>0"
			then "n:=n-1"
		}
	}
	
	machine(name: "m1", sees: ["cd"], refines: "m0") { // refines was the last parameter in other tests
		var "a", "a : NAT", "a := 0"
		var "b", "b : NAT", "b := 0"
		var "c", "c : NAT", "c := 0"
		/*var_block name: "a",
		          invariant: "a : NAT",
				  init: "a := 0"
		var_block name: "b",
		          invariant: "b : NAT",
				  init: "b := 0"
		var_block name: "c",
				  invariant: "c : NAT",
				  init: "c := 0"*/
		invariants gluing: "n = a+b+c",
		           oneway: "a=0 or c=0"
		theorem "a+b+c : NAT"
		theorem "c > 0 or a > 0 or (a+b<d & c=0) or (0<b & a=0)"
		
		variant "2*a+b"

		/*refine(name: "ML_out") {
			parameter "a"
			witness   for: "a", with: "a>0"
			action    "a:=a-1", "b:=b+1"
		}

		refine(name: "ML_in") {
			parameter "c"
			witness   for: "x", with: "c>0"
			action    "c := c-1"
		}*/
		
		refine(name: "ML_out") {
			when "a+b<d", "c=0"
			then "a:=a+1"
		}

		refine(name:  "ML_in") {
			when "c>0"
			then "c:=c-1"
		}

		event(name: "IL_in", type: CONVERGENT) {
			when "a>0"
			then "a:=a-1", "b:=b+1"
		}

		event(name: "IL_out", type: ANTICIPATED) { // just to check that anticipated works too
			when "0<b","a=0"
			then "b:=b-1", "c:=c+1"
		}
	}
}

m = mm.getModifiedModel()
s = m.load(m.getComponent("m1"))
t = s as Trace

assert m.m1.variant.getExpression().getCode() == "2*a+b"
assert m.m1.events.IL_in.type == CONVERGENT
assert m.m1.events.IL_out.type == ANTICIPATED
t = t.randomAnimation(10)

//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "cars", "/tmp")
//d.deleteDir()

"testing convergent and anticipated events"