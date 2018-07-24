import java.nio.file.Paths

import de.prob.animator.command.SimpleTextCommand

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final cmd = new SimpleTextCommand("compute_operations_for_state(root,Transitions)","Transitions")
s.execute(cmd)
assert cmd.results == ["op"]

"possible to create a simple text command"
