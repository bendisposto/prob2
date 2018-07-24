import java.nio.file.Paths

import de.prob.animator.domainobjects.ClassicalB

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())


final typecheck = {String formula ->
	s.typeCheck(formula as ClassicalB)
}

final res1 = typecheck("1 = BOOL")
assert !res1.ok
assert !res1.errors.empty
assert res1.type == "pred"

final res2 = typecheck("1") 
assert res2.ok
assert res2.errors.empty
assert res2.type == "integer"

final res3 = typecheck("TRUE") 
assert res3.ok
assert res3.errors.empty
assert res3.type == "boolean"

final res4 = typecheck("FALSE")
assert res4.ok
assert res4.errors.empty
assert res4.type == "boolean"

final res5 = typecheck("{1,2,3}")
assert res5.ok
assert res5.errors.empty
assert res5.type == "set(integer)"

final res6 = typecheck("1 |-> 2")
assert res6.ok
assert res6.errors.empty
assert res6.type == "couple(integer,integer)"

final res7 = typecheck("PID1") 
assert res7.ok
assert res7.errors.empty
assert res7.type == "global('PID')"

final res8 = typecheck('"BLAH"')
assert res8.ok
assert res8.errors.empty
assert res8.type == "string"

final res9 = typecheck("rec(PID1:1,PID2:2,PID3:3)")
assert res9.ok
assert res9.errors.empty
assert res9.type == "record([field('PID1',integer),field('PID2',integer),field('PID3',integer)])"

"it is possible to type check a formula"
