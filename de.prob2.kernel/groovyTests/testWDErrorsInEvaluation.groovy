import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
t = new Trace(s)
t = t.$initialise_machine()

// expression
wderror = t.evalCurrent("2 / 0")
assert wderror instanceof WDError
assert wderror.errors.size == 1
assert wderror.errors[0].contains("division by zero")

// predicate
wderror = t.evalCurrent("3 / 0 = 1")
assert wderror instanceof WDError
assert wderror.errors.size == 1
assert wderror.errors[0].contains("division by zero")

"Evaluation errors can be caught and handled simply"
