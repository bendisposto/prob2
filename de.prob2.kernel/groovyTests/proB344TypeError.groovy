s = api.b_load(dir+File.separator+"machines"+File.separator+"Life.mch")
assert s != null

s = api.b_load(dir+File.separator+"machines"+File.separator+"Marriage.mch")
assert s != null

"Machines Life and Marriage were loaded. ProB-344 resolved."