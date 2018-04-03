import java.nio.file.Paths

import de.prob.statespace.Trace

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final trace = new Trace(s)
trace.randomAnimation(10)

"AnimationSmokeTest passed"
