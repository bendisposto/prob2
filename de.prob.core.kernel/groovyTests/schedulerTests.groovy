import de.prob.statespace.*
c = api.b_load(dir+"/machines/scheduler.mch")
s = c.statespace
s.explore "root"
s.step 0
idAt0 = s.getCurrentState()
s.step 3
assert s.getCurrentState() == "2"
assert s.isExplored("2")
assert !s.isExplored("5")
assert s.info.ops.containsKey(new OperationId("1"))
assert !s.isOutEdge(new StateId(s.getCurrentState()),new OperationId("1"))
s.step 8
idAt8 = s.getCurrentState()
assert idAt0 == idAt8
s.goToState 6
assert s.isExplored("0")
assert s.isExplored("6")
assert s.isExplored("root")
assert !s.isExplored("5")
assert s.containsVertex("5")
varsAt6 = s.info.getState(6)
assert varsAt6.get("waiting") == "{}"
assert varsAt6.get("active") == "{PID2}"
assert varsAt6.get("ready") == "{}"
s.addUserFormula("1+1=2")
assert s.info.getVariable(s.getCurrentState(),"1+1=2") == "TRUE"
