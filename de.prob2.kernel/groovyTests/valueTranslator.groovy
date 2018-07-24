import de.prob.translator.types.Atom
import de.prob.translator.types.Number
import de.prob.translator.types.Record
import de.prob.translator.types.Set
import de.prob.translator.types.String

import static de.prob.translator.Translator.translate

assert translate("1") == 1

assert translate("TRUE") == true

assert translate("FALSE") == false

final expected1 = new Set([Number.build(1), Number.build(2), Number.build(3)].toSet())
assert translate("{1,2,3}") == expected1

final expected2 = new Tuple(Number.build(1), Number.build(2))
assert translate("1 |-> 2") == expected2

assert translate("PID1") == new Atom("PID1")

assert translate('"BLAH"') == new String("BLAH")

final expected3 = new Record(new LinkedHashMap<>(["PID1": Number.build(1), "PID2": Number.build(2), "PID3": Number.build(3)]))
assert translate("rec(PID1:1,PID2:2,PID3:3)") == expected3

"values are translated correctly"
