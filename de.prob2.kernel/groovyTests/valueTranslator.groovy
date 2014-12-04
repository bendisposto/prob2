import de.be4.classicalb.core.parser.node.TRestrictHeadSequence;
import de.prob.animator.domainobjects.*
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.*

// You can change the model you are testing here.

def wrap(String formula) {
	return new TranslateFormula(new ClassicalB(formula))
}

m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
t = new Trace(s)
t = t.anyEvent()


res = t.evalCurrent(wrap("1"))
assert res.value == 1

res = t.evalCurrent(wrap("TRUE = TRUE"))
assert res.value == true

res = t.evalCurrent(wrap("TRUE = FALSE"))
assert res.value == false

res = t.evalCurrent(wrap("{1,2,3}"))
expected = new HashSet([1,2,3])
assert res.value == expected

res = t.evalCurrent(wrap("1 |-> 2"))
expected = new Tuple(1,2)
assert res.value == expected

res = t.evalCurrent(wrap("PID1"))
assert res.value == "PID1"

res = t.evalCurrent(wrap('"BLAH"'))
assert res.value == new BString("BLAH")

res = t.evalCurrent(wrap("rec(PID1:1,PID2:2,PID3:3)"))
expected = new LinkedHashMap(["PID1":1,"PID2":2,"PID3":3])
assert res.value == expected

// Symbolic sets
res = t.evalCurrent(wrap("{x|x : NATURAL & x mod 2 = 1}"))
assert res.value instanceof PrologTerm // Symbolic Sets are translated as the Prolog Representation


assert t.evalCurrent(wrap("1 + b")) instanceof ComputationNotCompletedResult
// TODO: Need test case for PossiblyTrue

s.animator.cli.shutdown();
"values are translated correctly"