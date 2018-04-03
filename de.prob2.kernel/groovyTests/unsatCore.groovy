import java.nio.file.Paths

import de.prob.animator.command.UnsatCoreCommand
import de.prob.animator.domainobjects.ClassicalB

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString()) // machine is not needed...

final pred = new ClassicalB("x > 7 & 1=4")
final c = new UnsatCoreCommand(pred, [])

s.execute(c)
assert c.core.toString() == "1=4"

" The UnsatCoreCommand and MinimumUnsatCoreCommand work as expected."
