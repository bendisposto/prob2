import java.nio.file.Paths

import de.prob.animator.command.DeserializeStateCommand
import de.prob.animator.command.SerializeStateCommand
import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def h = new Trace(s)
h = h.add(0)
h = h.add(1)
h = h.add(2)
final cmd1 = new SerializeStateCommand("3")
s.execute(cmd1)
assert cmd1.id == "3"
assert cmd1.state == "[bind(active,[]),bind(ready,[]),bind(waiting,avl_set(node(fd(3,'PID'),true,0,empty,empty)))]."
final cmd2 = new DeserializeStateCommand(cmd1.state)
s.execute(cmd2)
assert cmd2.id == "3"
assert cmd2.state == "[bind(active,[]),bind(ready,[]),bind(waiting,avl_set(node(fd(3,'PID'),true,0,empty,empty)))]."

"Serialization of state successful"
