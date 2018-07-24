import java.nio.file.Paths

final s1 = api.b_load(Paths.get(dir, "machines", "Life.mch").toString())
assert s1 != null

final s2 = api.b_load(Paths.get(dir, "machines", "Marriage.mch").toString())
assert s2 != null

"Machines Life and Marriage were loaded. ProB-344 resolved."
