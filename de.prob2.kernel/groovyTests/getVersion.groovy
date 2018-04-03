import java.nio.file.Paths

import de.prob.animator.command.GetVersionCommand

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final cmd = new GetVersionCommand()
s.execute(cmd)

assert cmd.major != ""
assert cmd.minor != ""
assert cmd.service != ""
assert cmd.qualifier != ""
assert cmd.svnrevision != ""
assert cmd.prologinfo != ""
assert cmd.version != null

"version accessed successfully"
