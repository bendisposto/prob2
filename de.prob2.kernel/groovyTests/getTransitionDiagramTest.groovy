import de.prob.animator.command.CheckInitialisationStatusCommand
import de.prob.animator.command.CheckInvariantStatusCommand
import de.prob.animator.command.CheckMaxOperationReachedStatusCommand
import de.prob.animator.command.CheckTimeoutStatusCommand
import de.prob.animator.command.GetEnabledOperationsCommand
import de.prob.animator.command.GetOperationByPredicateCommand
import de.prob.animator.command.IStateSpaceModifier
import de.prob.animator.domainobjects.*
import de.prob.statespace.*

m = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
s = m as StateSpace
t = new Trace(s)
t.getCurrentState().explore()

GetTransitionDiagramCommand cmd = new GetTransitionDiagramCommand(new ClassicalB("waiting"))
s.execute(cmd)

assert !cmd.getNodes().isEmpty()
assert !cmd.getEdges().isEmpty()

s.animator.cli.shutdown();

//println "Nodes: "  + cmd.getNodes()
//println "Edges: " + cmd.getEdges()

"All tests passed!"