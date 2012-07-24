s = api.b_load(dir+"/machines/Lift.mch").getStatespace()
x = api.b_load(dir+"/machines/Lift.mch").getStatespace()
import de.prob.statespace.*
h = new SyncHistory([s,x],["inc"])
assert h.toString() == "0: [] Current Transition is: null\n1: [] Current Transition is: null\n"
h = h.add("\$initialise_machine",["4"],0)
assert h.toString() == "0: [0] Current Transition is: 0\n1: [] Current Transition is: null\n"
h = h.add("\$initialise_machine",["4"],1)
assert h.toString() == "0: [0] Current Transition is: 0\n1: [0] Current Transition is: 0\n"
h = h.add("inc",[])
assert h.toString() == "0: [0, 1] Current Transition is: 1\n1: [0, 1] Current Transition is: 1\n"
h = h.add("inc",[])
assert h.toString() == "0: [0, 1, 4] Current Transition is: 4\n1: [0, 1, 4] Current Transition is: 4\n"
h = h.add("dec",[],1)
assert h.toString() == "0: [0, 1, 4] Current Transition is: 4\n1: [0, 1, 4, 8] Current Transition is: 8\n"
h = h.back()
assert h.toString() == "0: [0, 1, 4] Current Transition is: 4\n1: [0, 1, 4] Current Transition is: 4\n"
h = h.forward()
assert h.toString() == "0: [0, 1, 4] Current Transition is: 4\n1: [0, 1, 4, 8] Current Transition is: 8\n"
h = h.add("inc", [], 1)
assert h.toString() ==  "0: [0, 1, 4, 7] Current Transition is: 7\n1: [0, 1, 4, 8, 4] Current Transition is: 4\n"