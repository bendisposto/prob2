import de.prob.statespace.*

c = api.b_load(dir+"/machines/scheduler.mch")
StateSpace s = c.getStatespace()
h = new Trace(s)
h = h.add(0)
h = h.add(3)
assert "2" == h.current.getCurrentState().getId()
assert ['1']== s.eval(new StateId("3",s),["2-1" as EventB]).collect { it.toString() }
assert ['{}']== s.eval(new StateId("0",s),["waiting" as EventB]).collect { it.toString() }
assert ['{PID2}']== s.eval(new StateId("2",s),["waiting" as EventB]).collect { it.toString() }
x = s.eval(new StateId("2",s),["x : waiting & x = PID2" as EventB]).get(0)
assert x.value == "TRUE"
assert x.solution.contains("x=PID2")