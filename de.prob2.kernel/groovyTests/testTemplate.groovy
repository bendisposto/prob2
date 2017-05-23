import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
t = new Trace(s)
t = t.$initialise_machine()

// You can use these to debug a generated model
//mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "Factorial", "/tmp")
//d.deleteDir()
//s.animator.cli.shutdown();

"add a description of the test here"