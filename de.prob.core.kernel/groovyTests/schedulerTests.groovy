c = api.b_load("/home/joy/code/probcore/build/resources/main/examples/scheduler.mch")
s = c.statespace
s.explore "root"
s.step 0
s.step 3
assert s.getCurrentState() == "2"
assert s.explored.contains("2")
assert !s.explored.contains("5")
assert s.ops.containsKey("1")
assert !s.getOutEdges(s.getCurrentState()).contains("1")
s.step 8
