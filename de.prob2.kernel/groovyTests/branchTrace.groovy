import de.prob.statespace.Trace

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
def t = new Trace(s)
t = t.$initialise_machine()
t = t.new('pp = PID1')

assert t.getTransitionList(true).collect { it.getRep() } == ["\$initialise_machine()","new(PID1)"]

t = t.back()
t = t.new('pp = PID2')
assert t.getTransitionList(true).collect { it.getRep() } == ["\$initialise_machine()","new(PID2)"]

// list underneath doesn't change
t = s as Trace
final t1 = t.$initialise_machine()
final t2 = t.$initialise_machine()

assert t1.getTransitionList(true).collect { it.getRep() } == ["\$initialise_machine()"]
assert t2.getTransitionList(true).collect { it.getRep() } == ["\$initialise_machine()"]

"performing some animations, going back, different step results in a correct trace"
