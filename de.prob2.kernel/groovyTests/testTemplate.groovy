import java.nio.file.Paths

import de.prob.statespace.Trace

// You can change the model you are testing here.
final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
def t = new Trace(s)
t = t.$initialise_machine()

// You can use these to debug a generated model
//final mtx = new ModelToXML()
//final d = mtx.writeToRodin(m, "Factorial", "/tmp")
//d.deleteDir()
//s.kill()

"add a description of the test here"
