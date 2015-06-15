import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
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

s.animator.cli.shutdown();
"Evaluation errors can be caught and handled simply"