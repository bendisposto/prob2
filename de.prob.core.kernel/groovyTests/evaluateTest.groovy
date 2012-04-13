c = api.b_load(dir+"/machines/scheduler.mch")
s = c.statespace
s.explore "root"
s.step 0
s.step 3 
assert ['1'] == s.evaluate("2-1").collect { it.toString() }
assert ['{PID2}'] == s.evaluate("waiting").collect { it.toString() }
assert ['{}'] == s.eval("0","waiting").collect { it.toString() }
assert ['{PID2}'] == s.eval("2","waiting").collect { it.toString() }
x = s.evaluate("x : waiting & x = PID2").get(0)
assert x.value == "TRUE"
assert x.solution.contains("x=PID2")