s = api.b_load(dir+File.separator+"machines"+File.separator+"Lift.mch")
assert s != null
c = new GetMachineStructureCommand()

s.execute(c)

println(c)
"Machine Lift.mch was loaded"