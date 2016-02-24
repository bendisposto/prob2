import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

// assert api.upgrade("latest") == "--Cannot download the ProB binaries. Close the 1 CLIs that are open--"
t = new Trace(s)
t = t.anyEvent().anyEvent()

"checks to ensure that no CLIs are open before downloading a new CLI"