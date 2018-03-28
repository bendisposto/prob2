import de.prob.animator.command.GetCurrentPreferencesCommand
import de.prob.animator.command.GetDefaultPreferencesCommand
import de.prob.animator.command.GetPreferenceCommand
import de.prob.animator.command.SetPreferenceCommand

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch", ["MAXINT":"10"])

final cmd1 = new GetDefaultPreferencesCommand()
s.execute(cmd1)
final ps = cmd1.getPreferences()

final prefs1 = [:]
ps.each { prefs1[it.name] = it.defaultValue }

assert prefs1.size() > 0 // there are some preferences set

final cmd2 = new GetCurrentPreferencesCommand()
s.execute(cmd2)
final prefs2 = cmd2.getPreferences()
assert prefs2["MAXINT"] == "10"

final cmd3 = new SetPreferenceCommand("MAXINT","12")
s.execute(cmd3)

final cmd4 = new GetPreferenceCommand("MAXINT")
s.execute(cmd4)
assert cmd4.getValue() == "12"

"the preferences for a model are as expected"
