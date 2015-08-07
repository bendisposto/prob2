import de.prob.statespace.*
import de.prob.animator.command.*

s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
h = new Trace(s)
h = h.add(0)
h = h.add(1)
h = h.add(2)
cmd = new SerializeStateCommand("3")
s.execute(cmd)
assert cmd.getId() == "3"
assert cmd.getState() == "[bind(active,[]),bind(ready,[]),bind(waiting,avl_set(node(fd(3,'PID'),true,0,empty,empty)))]."
cmd1 = new DeserializeStateCommand(cmd.getState())
s.execute(cmd1)
assert cmd1.getId() == "3"
assert cmd1.getState() == "[bind(active,[]),bind(ready,[]),bind(waiting,avl_set(node(fd(3,'PID'),true,0,empty,empty)))]."

s.animator.cli.shutdown();
"Serialization of state successful"