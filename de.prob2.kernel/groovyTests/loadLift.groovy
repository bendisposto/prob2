c = api.b_load(dir+File.separator+"machines"+File.separator+"Lift.mch")
assert c != null
s = c.getStateSpace()

s.animator.cli.shutdown();
"Machine Lift.mch was loaded"