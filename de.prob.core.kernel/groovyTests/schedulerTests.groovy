c = api.b_load(dir+"/machines/scheduler.mch")
s = c.statespace
s.explore "root"
s.step 0
s.step 3
assert s.getCurrentState() == "2"