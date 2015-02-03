c = api.b_load(dir+File.separator+"machines"+File.separator+"Life.mch")
assert c != null
s = c.getStateSpace()

c = api.b_load(dir+File.separator+"machines"+File.separator+"Marriage.mch")
assert c != null
s = c.getStateSpace()



s.animator.cli.shutdown();
"Machines Life and Marriage were loaded. ProB-344 resolved."