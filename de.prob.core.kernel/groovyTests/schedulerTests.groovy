c = api.b_load(dir+"/machines/scheduler.mch")
s = c.statespace
s.explore "root"
s.step 0
idAt0 = s.getCurrentState()
s.step 3
assert s.getCurrentState() == "2"
assert s.explored.contains("2")
assert !s.explored.contains("5")
assert s.ops.containsKey("1")
assert !s.getOutEdges(s.getCurrentState()).contains("1")
s.step 8
idAt8 = s.getCurrentState()
assert idAt0 == idAt8
s.goToState 6
assert s.explored.contains("0")
assert s.explored.contains("6")
assert s.explored.contains("root")
assert !s.explored.contains("5")
assert s.containsVertex("5")
varsAt6 = s.getState(6)
assert varsAt6.get("waiting") == "{}"
assert varsAt6.get("active") == "{PID2}"
assert varsAt6.get("ready") == "{}"
s.addUserFormula("1+1=2")
assert s.variables.get(s.getCurrentState()).get("1+1=2") == "TRUE"
