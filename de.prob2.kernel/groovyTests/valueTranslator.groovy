import de.be4.classicalb.core.parser.node.TRestrictHeadSequence;
import de.prob.animator.domainobjects.*
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
t = new Trace(s)
t = t.anyEvent()

thrown = false
try {
	res = t.evalCurrent("1")
	assert res.value == "1"
	tres = res.translate(s)
	assert tres.value == 1
} catch(IllegalArgumentException e) {
	thrown = true
}
assert thrown

/* TODO: Implement	
res = t.evalCurrent("TRUE = TRUE")
assert res.value == "TRUE"
tres = res.translate(s)
assert tres.value == true

res = t.evalCurrent("TRUE = FALSE")
assert res.value == "FALSE"
tres = res.translate(s)
assert tres.value == false

res = t.evalCurrent("{1,2,3}")
assert res.value == "{1,2,3}"
tres = res.translate(s)
expected = new HashSet([1,2,3])
assert tres.value == expected

res = t.evalCurrent("1 |-> 2")
assert res.value == "(1|->2)"
tres = res.translate(s)
expected = new Tuple(1,2)
assert tres.value == expected

res = t.evalCurrent("PID1")
assert res.value == "PID1"
tres = res.translate(s)
assert tres.value == "PID1"

res = t.evalCurrent('"BLAH"')
assert res.value == '"BLAH"'
tres = res.translate(s)
assert tres.value == new BString("BLAH")

res = t.evalCurrent("rec(PID1:1,PID2:2,PID3:3)")
assert res.value == "rec(PID1:1,PID2:2,PID3:3)"
tres = res.translate(s)
expected = new LinkedHashMap(["PID1":1,"PID2":2,"PID3":3])
assert tres.value == expected

// Symbolic sets
res = t.evalCurrent("{x|x : NATURAL & x mod 2 = 1}")
assert res.value == "{x|x : NATURAL & x mod 2 = 1}"
tres = res.translate(s)
assert tres.value instanceof PrologTerm // Symbolic Sets are translated as the Prolog Representation

// TODO: Need test case for PossiblyTrue
*/
s.animator.cli.shutdown();
"values are translated correctly"