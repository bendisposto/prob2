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

assert prefs.size() > 0 // there are some preferences set

cmd = new GetCurrentPreferencesCommand()
s.execute(cmd)
prefs = cmd.getPreferences()
assert prefs["MAXINT"] == "10"

cmd = new SetPreferenceCommand("MAXINT","12")
s.execute(cmd)

cmd = new GetPreferenceCommand("MAXINT")
s.execute(cmd)
assert cmd.getValue() == "12"

"the preferences for a model are as expected"
