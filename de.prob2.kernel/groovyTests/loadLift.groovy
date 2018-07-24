import java.nio.file.Paths

final s = api.b_load(Paths.get(dir, "machines", "Lift.mch").toString())
assert s != null

"Machine Lift.mch was loaded"
