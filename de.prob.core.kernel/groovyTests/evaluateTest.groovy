import de.prob.statespace.*
c = api.b_load(dir+"/machines/scheduler.mch")
s = c.statespace
h = new History(s)
h = h.add(0)
h = h.add(3)
assert "2" == h.current.getCurrentState().getId()
assert ['1'] == s.eval("3","2-1").collect { it.toString() }
assert ['{}'] == s.eval("0","waiting").collect { it.toString() }
assert ['{PID2}'] == s.eval("2","waiting").collect { it.toString() }
x = s.eval("2","x : waiting & x = PID2").get(0)
assert x.value == "TRUE"
assert x.solution.contains("x=PID2")