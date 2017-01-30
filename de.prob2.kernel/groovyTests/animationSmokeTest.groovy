import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

Trace trace = new Trace(s);
trace.randomAnimation(10);

"AnimationSmokeTest passed"