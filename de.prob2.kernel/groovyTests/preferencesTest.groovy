import de.prob.animator.command.GetCurrentPreferencesCommand;
import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch", ["MAXINT":"10"])

expected_size = 26

cmd = new GetDefaultPreferencesCommand()
s.execute(cmd)
ps = cmd.getPreferences()

prefs = [:]
ps.each { prefs[it.name] = it.defaultValue }

assert prefs["MAXINT"] == "3"
assert prefs["MININT"] == "-1"
assert prefs["DEFAULT_SETSIZE"] == "2"
assert prefs["MAX_INITIALISATIONS"] == "4"
assert prefs["MAX_OPERATIONS"] == "10"
assert prefs["SYMBOLIC"] == "false"
assert prefs["CLPFD"] == "true"
assert prefs["CHR"] == "false"
assert prefs["SMT"] == "false"
assert prefs["CSE"] == "false"
assert prefs["CSE_PRED"] == "true"
assert prefs["CSE_WD_ONLY"] == "false"
assert prefs["STATIC_ORDERING"] == "false"
assert prefs["COMPRESSION"] == "false"
assert prefs["SYMMETRY_MODE"] == "off"
assert prefs["TIME_OUT"] == "2500"
assert prefs["PROOF_INFO"] == "true"
assert prefs["TRY_FIND_ABORT"] == "false"
assert prefs["NUMBER_OF_ANIMATED_ABSTRACTIONS"] == "20"
assert prefs["USE_RECORD_CONSTRUCTION"] == "true"
assert prefs["KODKOD"] == "false"
assert prefs["KODKOD_ONLY_FULL"] == "true"
assert prefs["MEMO"] == "false"

cmd = new GetCurrentPreferencesCommand()
s.execute(cmd)
prefs = cmd.getPreferences()
assert prefs["MAXINT"] == "10"

cmd = new SetPreferenceCommand("MAXINT","12")
s.execute(cmd)

cmd = new GetPreferenceCommand("MAXINT")
s.execute(cmd)
assert cmd.getValue() == "12"

s.animator.cli.shutdown();
"the preferences for a model are as expected"
