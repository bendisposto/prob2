final s1 = api.b_load(dir+File.separator+"machines"+File.separator+"Life.mch")
assert s1 != null

final s2 = api.b_load(dir+File.separator+"machines"+File.separator+"Marriage.mch")
assert s2 != null

"Machines Life and Marriage were loaded. ProB-344 resolved."
