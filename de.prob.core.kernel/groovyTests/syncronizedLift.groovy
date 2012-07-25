s = api.b_load(dir+"/machines/Lift.mch").getStatespace()
x = api.b_load(dir+"/machines/Lift.mch").getStatespace()
import de.prob.statespace.*
h = new SyncHistory([s,x],["inc"])
assert h.toString() == "0: [] Current Transition is: null\n1: [] Current Transition is: null\n"
h = h.add("\$initialise_machine",["4"],0)
assert h.toString() == "0: [\$initialise_machine(4)] Current Transition is: \$initialise_machine(4)\n1: [] Current Transition is: null\n"
h = h.add("\$initialise_machine",["4"],1)
assert h.toString() == "0: [\$initialise_machine(4)] Current Transition is: \$initialise_machine(4)\n1: [\$initialise_machine(4)] Current Transition is: \$initialise_machine(4)\n"
h = h.add("inc",[])
assert h.toString() == "0: [\$initialise_machine(4), inc()] Current Transition is: inc()\n1: [\$initialise_machine(4), inc()] Current Transition is: inc()\n"
h = h.add("inc",[])
assert h.toString() == "0: [\$initialise_machine(4), inc(), inc()] Current Transition is: inc()\n1: [\$initialise_machine(4), inc(), inc()] Current Transition is: inc()\n"
h = h.add("dec",[],1)
assert h.toString() == "0: [\$initialise_machine(4), inc(), inc()] Current Transition is: inc()\n1: [\$initialise_machine(4), inc(), inc(), dec()] Current Transition is: dec()\n"
h = h.back()
assert h.toString() == "0: [\$initialise_machine(4), inc(), inc()] Current Transition is: inc()\n1: [\$initialise_machine(4), inc(), inc()] Current Transition is: inc()\n"
h = h.forward()
assert h.toString() == "0: [\$initialise_machine(4), inc(), inc()] Current Transition is: inc()\n1: [\$initialise_machine(4), inc(), inc(), dec()] Current Transition is: dec()\n"
h = h.add("inc", [], 1)
assert h.toString() == "0: [\$initialise_machine(4), inc(), inc(), inc()] Current Transition is: inc()\n1: [\$initialise_machine(4), inc(), inc(), dec(), inc()] Current Transition is: inc()\n"