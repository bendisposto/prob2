import java.util.concurrent.atomic.AtomicBoolean;

import de.be4.classicalb.core.parser.node.TRestrictHeadSequence;
import de.prob.animator.domainobjects.*
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.*
import de.prob.translator.Translator;
import de.prob.translator.types.Atom;
import de.prob.translator.types.String;
import de.prob.translator.types.Record;
import de.prob.translator.types.Set;
import de.prob.translator.types.Number;
import static de.prob.translator.Translator.translate;


assert translate("1") == 1

assert translate("TRUE") == true

assert translate("FALSE") == false

expected = new Set(new HashSet([Number.build(1),Number.build(2),Number.build(3)]))
assert translate("{1,2,3}") == expected

expected = new Tuple(Number.build(1),Number.build(2))
assert translate("1 |-> 2") == expected

assert translate("PID1") == new Atom("PID1")

assert translate('"BLAH"') == new String("BLAH")

expected = new Record(new LinkedHashMap(["PID1":Number.build(1),"PID2":Number.build(2),"PID3":Number.build(3)]))
assert translate("rec(PID1:1,PID2:2,PID3:3)") == expected

"values are translated correctly"