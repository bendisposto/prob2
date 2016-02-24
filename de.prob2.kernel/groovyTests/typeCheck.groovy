import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")


def typecheck(formula) {
	s.typeCheck(formula as ClassicalB)
}

res = typecheck("1 = BOOL")
assert !res.isOk()
assert !res.getErrors().isEmpty()
assert res.getType() == "pred"

res = typecheck("1") 
assert res.isOk()
assert res.getErrors().isEmpty()
assert res.getType() == "integer"

res = typecheck("TRUE") 
assert res.isOk()
assert res.getErrors().isEmpty()
assert res.getType() == "boolean"

res = typecheck("FALSE")
assert res.isOk()
assert res.getErrors().isEmpty()
assert res.getType() == "boolean"

res = typecheck("{1,2,3}")
assert res.isOk()
assert res.getErrors().isEmpty()
assert res.getType() == "set(integer)"

res = typecheck("1 |-> 2")
assert res.isOk()
assert res.getErrors().isEmpty()
assert res.getType() == "couple(integer,integer)"

res = typecheck("PID1") 
assert res.isOk()
assert res.getErrors().isEmpty()
assert res.getType() == "global('PID')"

res = typecheck('"BLAH"')
assert res.isOk()
assert res.getErrors().isEmpty()
assert res.getType() == "string"

res = typecheck("rec(PID1:1,PID2:2,PID3:3)")
assert res.isOk()
assert res.getErrors().isEmpty()
assert res.getType() == "record([field('PID1',integer),field('PID2',integer),field('PID3',integer)])"

"it is possible to type check a formula"