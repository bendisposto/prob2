import de.prob.animator.domainobjects.ClassicalB
// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")


final typecheck = {formula ->
	s.typeCheck(formula as ClassicalB)
}

final res1 = typecheck("1 = BOOL")
assert !res1.isOk()
assert !res1.getErrors().isEmpty()
assert res1.getType() == "pred"

final res2 = typecheck("1") 
assert res2.isOk()
assert res2.getErrors().isEmpty()
assert res2.getType() == "integer"

final res3 = typecheck("TRUE") 
assert res3.isOk()
assert res3.getErrors().isEmpty()
assert res3.getType() == "boolean"

final res4 = typecheck("FALSE")
assert res4.isOk()
assert res4.getErrors().isEmpty()
assert res4.getType() == "boolean"

final res5 = typecheck("{1,2,3}")
assert res5.isOk()
assert res5.getErrors().isEmpty()
assert res5.getType() == "set(integer)"

final res6 = typecheck("1 |-> 2")
assert res6.isOk()
assert res6.getErrors().isEmpty()
assert res6.getType() == "couple(integer,integer)"

final res7 = typecheck("PID1") 
assert res7.isOk()
assert res7.getErrors().isEmpty()
assert res7.getType() == "global('PID')"

final res8 = typecheck('"BLAH"')
assert res8.isOk()
assert res8.getErrors().isEmpty()
assert res8.getType() == "string"

final res9 = typecheck("rec(PID1:1,PID2:2,PID3:3)")
assert res9.isOk()
assert res9.getErrors().isEmpty()
assert res9.getType() == "record([field('PID1',integer),field('PID2',integer),field('PID3',integer)])"

"it is possible to type check a formula"
