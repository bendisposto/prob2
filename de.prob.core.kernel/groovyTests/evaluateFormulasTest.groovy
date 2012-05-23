s = api.s()
s.explore "root"
s.step 0
s.step 4
s.step 6
assert !s.info.stateHasVariable(s.getCurrentState(),"waiting\\/ready")
s.addUserFormula("waiting\\/ready")
assert s.info.stateHasVariable(s.getCurrentState(),"waiting\\/ready")
assert s.info.getVariable(s.getCurrentState(),"waiting\\/ready")=="{PID1,PID3}"
assert s.formulas.size() == 1
s.goToState(3)
assert s.info.stateHasVariable("3","waiting\\/ready")
assert s.info.getVariable(s.getCurrentState(),"waiting\\/ready")=="{PID3}"
s.back()
s.back()
s.back()
assert s.getCurrentState() == "0"
assert s.info.stateHasVariable(s.getCurrentState(),"waiting\\/ready")
assert s.info.getVariable("0","waiting\\/ready")=="{}"