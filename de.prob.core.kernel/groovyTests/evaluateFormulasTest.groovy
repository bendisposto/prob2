import de.prob.statespace.*
s = api.b_load(dir+"/machines/scheduler.mch").statespace
h = new History(s)
h = h.add(0)
h = h.add(4)
h = h.add(6)
//a = h.current.getCurrentState()
//assert a == s.states.get("4")
//assert a.getClass() == de.prob.statespace.StateId
//assert !s.info.stateHasVariable(a,"waiting\\/ready")
//s.addUserFormula("waiting\\/ready")
//assert s.info.stateHasVariable(h.current.getCurrentState(),"waiting\\/ready")
//assert s.info.getVariable(h.current.getCurrentState(),"waiting\\/ready")=="{PID1,PID3}"
//assert s.formulas.size() == 1
//println h
//assert s.info.stateHasVariable(s.states.get("3"),"waiting\\/ready")
//assert s.info.getVariable(h.current.getCurrentState(),"waiting\\/ready")=="{PID3}"
//s.back()
//s.back()
//s.back()
//assert h.current.getCurrentState() == s.states.get("0")
//assert s.info.stateHasVariable(h.current.getCurrentState(),"waiting\\/ready")
//assert s.info.getVariable(s.states.get("0"),"waiting\\/ready")=="{}"