import java.nio.file.Paths

import de.prob.animator.command.GetEnableMatrixCommand

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final p1 = new GetEnableMatrixCommand.EventPair("new", "del")
final p2 = new GetEnableMatrixCommand.EventPair("del", "del")

final c = new GetEnableMatrixCommand(p1, p2)

s.execute(c)

final x = c.getEnableInfo(p1)
final y = c.getEnableInfo(p2)

assert x.enable == "ok"
assert y.enable == "false"
assert y.disable == "ok"

"EnableMatrixTest passed"
