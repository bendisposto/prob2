s = api.b_load(dir+File.separator+"machines"+File.separator+"Lift.mch")
assert s != null

s.animator.cli.shutdown();
"Machine Lift.mch was loaded"