import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
s = m as StateSpace
t = new Trace(s)
t = t.$initialise_machine()

// expression
wderror = t.evalCurrent("2 / 0")
assert wderror instanceof EvaluationErrorResult
assert wderror.getResult() == "NOT-WELL-DEFINED"
assert wderror.getErrors() == ["division by zero 2/0;;", "division by zero 2/0;;", "division by zero 2/0;;"]

// predicate
wderror = t.evalCurrent("3 / 0 = 1")
assert wderror instanceof EvaluationErrorResult
assert wderror.getResult() == "NOT-WELL-DEFINED"
assert wderror.getErrors() == ["division by zero 3/0;;"]

// expression
f = new TranslateFormula(new ClassicalB("4 / 0"))
wderror2 = t.evalCurrent(f)
assert wderror2 instanceof EvaluationErrorResult
assert wderror2.getResult() == "NOT-WELL-DEFINED"
assert wderror2.getErrors() == ["division by zero 4/0;;", "division by zero 4/0;;", "division by zero 4/0;;"]

// predicate
f = new TranslateFormula(new ClassicalB("5 / 0 = 1"))
wderror2 = t.evalCurrent(f)
assert wderror2 instanceof EvaluationErrorResult
assert wderror2.getResult() == "NOT-WELL-DEFINED"
assert wderror2.getErrors() == ["division by zero 5/0;;"]

s.animator.cli.shutdown();
"Evaluation errors can be caught and handled simply"