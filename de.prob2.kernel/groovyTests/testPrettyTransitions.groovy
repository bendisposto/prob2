import de.prob.statespace.Trace

// You can change the model you are testing here.
final s = api.eventb_load(dir+File.separator+"Lift"+File.separator+"lift0.bcm")
def t = new Trace(s)
t = t.$setup_constants()
t = t.$initialise_machine()
t = t.up()

final list = t.getTransitionList(true)
assert list.collect { it.getName() } == ["\$setup_constants", "\$initialise_machine","up"]
assert list.collect { it.getRep() } == ["\$setup_constants()","\$initialise_machine()","up()"]
assert list.collect { it.getPrettyRep() } == ["SETUP_CONSTANTS()", "INITIALISATION()", "up()"]

"a Transition has a pretty representation for internal ProB transition names"
