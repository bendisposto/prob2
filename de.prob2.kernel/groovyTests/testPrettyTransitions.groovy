import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.eventb_load(dir+File.separator+"Lift"+File.separator+"lift0.bcm")
t = new Trace(s)
t = t.$setup_constants()
t = t.$initialise_machine()
t = t.up()

list = t.getTransitionList(true)
assert list.collect { it.getName() } == ["\$setup_constants", "\$initialise_machine","up"]
assert list.collect { it.getRep() } == ["\$setup_constants({(L1|->L0),(L2|->L1),(L3|->L2)},{(L0|->L1),(L1|->L2),(L2|->L3)})","\$initialise_machine(L0)","up()"]
assert list.collect { it.getPrettyRep() } == ["SETUP_CONSTANTS({(L1|->L0),(L2|->L1),(L3|->L2)},{(L0|->L1),(L1|->L2),(L2|->L3)})", "INITIALISATION(L0)", "up()"]


s.animator.cli.shutdown();
"a Transition has a pretty represenation for internal ProB transition names"