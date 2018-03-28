import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
def t = new Trace(s)
t = t.$initialise_machine()

// expression
final wderror1 = t.evalCurrent("2 / 0")
assert wderror1 instanceof WDError
assert wderror1.errors.size == 1
assert wderror1.errors[0].contains("division by zero")

// predicate
final wderror2 = t.evalCurrent("3 / 0 = 1")
assert wderror2 instanceof WDError
assert wderror2.errors.size == 1
assert wderror2.errors[0].contains("division by zero")

"Evaluation errors can be caught and handled simply"
