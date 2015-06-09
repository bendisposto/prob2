import de.prob.animator.domainobjects.*
import de.prob.statespace.*
import de.prob.model.eventb.translate.*
import static de.prob.model.eventb.Event.EventType.CONVERGENT
import static de.prob.model.eventb.Event.EventType.ANTICIPATED

// You can change the model you are testing here.
mm = new ModelModifier()
mm.make {
	context(name: "cd") {
		constant "d"
		axioms "d : NAT", "d > 0"
	}
	
	machine(name: "m0", sees: ["cd"]) {
		var_block name: "n",
		          invariant: "n : NAT",
				  init: "n:=0"
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
	
	machine(name: "m1", refines: ["m0"], sees: ["cd"]) {
		var_block name: "a",
		          invariant: "a : NAT",
				  init: "a := 0"
		var_block name: "b",
		          invariant: "b : NAT",
				  init: "b := 0"
		var_block name: "c",
				  invariant: "c : NAT",
				  init: "c := 0"
		invariants gluing: "n=a+b+c",
		           oneway: "a=0 or c=0"
		theorems  "a+b+c : NAT",
		          "c > 0 or a > 0 or (a+b<d & c=0) or (0<b & a=0)" 
		
		variant "2*a+b"
		
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
				   
		event(name: "IL_out", type: CONVERGENT) {
			when "0<b","a=0"
			then "b:=b-1", "c:=c+1"
		}
	}
}

m = mm.getModifiedModel("m1")
s = m as StateSpace
t = m as Trace

t = t.randomAnimation(10)

mtx = new ModelToXML()
d = mtx.writeToRodin(m, "cars", "/tmp")
//d.deleteDir()

s.animator.cli.shutdown();
"testing convergent and anticipated events"