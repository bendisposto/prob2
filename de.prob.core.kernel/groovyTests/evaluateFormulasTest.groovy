s = api.s()
s.explore "root"
s.step 0
s.step 4
s.step 6
assert !s.info.getState(s.getCurrentState()).containsKey("waiting\\/ready")
s.addUserFormula("waiting\\/ready")
assert s.info.getState(s.getCurrentState()).containsKey("waiting\\/ready")
assert s.info.getState(s.getCurrentState()).get("waiting\\/ready")=="{PID1,PID3}"
assert s.formulas.size() == 1
s.goToState(3)
assert s.info.getState("3").containsKey("waiting\\/ready")
assert s.info.getState(s.getCurrentState()).get("waiting\\/ready")=="{PID3}"
s.back()
s.back()
s.back()
assert s.getCurrentState() == "0"
assert s.info.getState(s.getCurrentState()).containsKey("waiting\\/ready")
assert s.info.getState("0").get("waiting\\/ready")=="{}"