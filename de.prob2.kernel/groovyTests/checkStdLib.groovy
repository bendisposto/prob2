import java.nio.file.Paths

import de.prob.statespace.Trace

assert System.properties["prob.stdlib"] != null // default folder has been set

final s = api.b_load(Paths.get(dir, "machines", "LoadStdLibTest.mch").toString())
final t = new Trace(s)

"the standard library is present"
