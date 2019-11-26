import de.hhu.stups.prob.translator.BAtom
import de.hhu.stups.prob.translator.BNumber
import de.hhu.stups.prob.translator.BRecord
import de.hhu.stups.prob.translator.BSet
import de.hhu.stups.prob.translator.BString
import de.hhu.stups.prob.translator.BTuple

import static de.hhu.stups.prob.translator.Translator.translate

assert translate("1").intValue() == 1

assert translate("TRUE").booleanValue() == true

assert translate("FALSE").booleanValue() == false

final expected1 = new BSet([new BNumber(1), new BNumber(2), new BNumber(3)].toSet())
assert translate("{1,2,3}") == expected1

final expected2 = new BTuple(new BNumber(1), new BNumber(2))
assert translate("1 |-> 2") == expected2

assert translate("PID1") == new BAtom("PID1")

assert translate('"BLAH"') == new BString("BLAH")

final expected3 = new BRecord(["PID1": new BNumber(1), "PID2": new BNumber(2), "PID3": new BNumber(3)])
assert translate("rec(PID1:1,PID2:2,PID3:3)") == expected3

"values are translated correctly"
