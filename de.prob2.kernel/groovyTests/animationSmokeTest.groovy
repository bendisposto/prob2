import de.prob.statespace.Trace

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

final trace = new Trace(s);
trace.randomAnimation(10);

"AnimationSmokeTest passed"
