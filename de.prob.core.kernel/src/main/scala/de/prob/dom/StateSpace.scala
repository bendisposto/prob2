package de.prob.dom

import scala.collection.immutable.HashSet
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.util.EdgeType
import edu.uci.ics.jung.graph.util.Pair
import edu.uci.ics.jung.graph.util.EdgeType
import java.util.ArrayList
import akka.actor.Actor
import scala.collection.Map
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.prolog.term.PrologTerm
import de.prob.parser.ProBResultParser
import de.prob.parser.BindingGenerator
import de.prob.prolog
import akka.actor.Actor._
import de.prob.cli.StateError
import akka.actor.LocalActorRef
import akka.actor.LocalActorRef
import akka.event.EventHandler
import de.prob.model.IStateSpace

case class State(id: String) { var explored = false }
object State { implicit def stringToNode(x: String): State = State(x) }
case class Operation(id: String)
object Operation { implicit def stringeToEdge(x: String): Operation = Operation(x) }

object dummyProB {
  def explore(s: State): List[Tuple3[State, Operation, State]] = {
    return List(("1", "2", "3"), ("1", "4", "5"))
  }
}

object StateSpace extends DirectedSparseMultigraph[State, Operation] with IStateSpace {
  var stateValues = Map[State, Map[String, Value]]()

  def getRoot: State = null //FIXME Implement this
  def addV(v: State) = addVertex(v)
  def addE(id: String, nodes: Tuple2[State, State]) = addEdge(id, nodes._1, nodes._2, EdgeType.DIRECTED)
  def addE(edge: Tuple3[State, Operation, State]) = addEdge(edge._2, edge._1, edge._3, EdgeType.DIRECTED)
  def containsV(n: State) = containsVertex(n)
  def containsE(e: Operation) = containsEdge(e)

  def successor(s: State): List[State] = {
    if (!s.explored) {
      val newStates = dummyProB.explore(s)
      newStates.foreach(e => addE(e))
      //TODO:  update stateValues
    }
    import scala.collection.JavaConversions._
    return List(State(s.id))
  }
}

object MainClass {
  def main(args: Array[String]) {
    val slave = actorOf[StateSpaceActor]
    slave.start()
    slave ! "start"
  }
}

//TODO: Figure out how the StateSpace is going to calculate the different states
class StateSpaceActor extends Actor {
  val annotation = actorOf[BStateSpaceAnnotation]
  val stateSpace = StateSpace
  var functions = List[(java.util.Map[String, PrologTerm], State) => Update]()

  def receive = {
    case i: Init =>
      EventHandler.info(this, "Added " + i)
      functions = i.fkt :: functions
    case "start" => begin
    case "startTest" => test
    case s: String => EventHandler.info(this, s)
    case x => println(x)
  }

  def begin = {
    annotation.start
    annotation ! self
  }

  def test = {
    val query = "computeOperationsForState(22,AAAPLOps),getStateValues(22,AABBindings),state_property(initialised,22,AACPropResult),state_property(invariantKO,22,AADPropResult),state_property(max_operations_reached,22,AAEPropResult),state_property(timeout_occurred,22,AAFPropResult),op_timeout_occurred(22,AAGTO),get_state_errors(22,AAHErrors),setCurrentState(22),getErrorMessages(BErrors)."
    val input = "yes('.'(=('AAGTO',[]),'.'(=('AABBindings','.'(binding(abs,avl_set(node(','(int(1),int(1)),true,0,node(','(int(-1),int(1)),true,1,empty,node(','(int(0),int(0)),true,0,empty,empty)),node(','(int(2),int(2)),true,1,empty,node(','(int(3),int(3)),true,0,empty,empty)))),'{(-1|->1),(0|->0),(1|->1),(2|->2),(3|->3)}'),'.'(binding(dv,avl_set(node(','(int(1),int(0)),true,0,node(','(int(-1),int(1)),true,0,node(','(int(-1),int(-1)),true,1,empty,node(','(int(-1),int(0)),true,0,empty,empty)),node(','(int(-1),int(3)),true,0,node(','(int(-1),int(2)),true,0,empty,empty),node(','(int(1),int(-1)),true,0,empty,empty))),node(','(int(2),int(0)),true,0,node(','(int(1),int(2)),true,0,node(','(int(1),int(1)),true,0,empty,empty),node(','(int(1),int(3)),true,0,empty,empty)),node(','(int(3),int(0)),true,0,node(','(int(2),int(2)),true,0,empty,empty),node(','(int(3),int(3)),true,0,empty,empty))))),'{(-1|->-1),(-1|->0),(-1|->1),(-1|->2),(-1|->3),(1|->-1),(1|->0),(1|->1),(1|->2),(1|->3),(2|->0),(2|->2),(3|->0),(3|->3)}'),'.'(binding(gcd,avl_set(node(','(','(int(1),int(1)),int(-1)),true,0,node(','(','(int(-1),int(3)),int(1)),true,0,node(','(','(int(-1),int(1)),int(-1)),true,0,node(','(','(int(-1),int(-1)),int(1)),true,1,node(','(','(int(-1),int(-1)),int(-1)),true,0,empty,empty),node(','(','(int(-1),int(0)),int(-1)),true,1,empty,node(','(','(int(-1),int(0)),int(1)),true,0,empty,empty))),node(','(','(int(-1),int(2)),int(-1)),true,1,node(','(','(int(-1),int(1)),int(1)),true,0,empty,empty),node(','(','(int(-1),int(2)),int(1)),true,1,empty,node(','(','(int(-1),int(3)),int(-1)),true,0,empty,empty)))),node(','(','(int(0),int(2)),int(2)),true,0,node(','(','(int(0),int(-1)),int(1)),true,1,node(','(','(int(0),int(-1)),int(-1)),true,0,empty,empty),node(','(','(int(0),int(1)),int(-1)),true,1,empty,node(','(','(int(0),int(1)),int(1)),true,0,empty,empty))),node(','(','(int(1),int(-1)),int(1)),true,0,node(','(','(int(0),int(3)),int(3)),true,1,empty,node(','(','(int(1),int(-1)),int(-1)),true,0,empty,empty)),node(','(','(int(1),int(0)),int(-1)),true,1,empty,node(','(','(int(1),int(0)),int(1)),true,0,empty,empty))))),node(','(','(int(2),int(2)),int(2)),true,0,node(','(','(int(1),int(3)),int(1)),true,0,node(','(','(int(1),int(2)),int(-1)),true,1,node(','(','(int(1),int(1)),int(1)),true,0,empty,empty),node(','(','(int(1),int(2)),int(1)),true,1,empty,node(','(','(int(1),int(3)),int(-1)),true,0,empty,empty))),node(','(','(int(2),int(0)),int(2)),true,0,node(','(','(int(2),int(-1)),int(-1)),true,1,empty,node(','(','(int(2),int(-1)),int(1)),true,0,empty,empty)),node(','(','(int(2),int(1)),int(-1)),true,1,empty,node(','(','(int(2),int(1)),int(1)),true,0,empty,empty)))),node(','(','(int(3),int(0)),int(3)),true,0,node(','(','(int(2),int(3)),int(1)),true,1,node(','(','(int(2),int(3)),int(-1)),true,0,empty,empty),node(','(','(int(3),int(-1)),int(-1)),true,1,empty,node(','(','(int(3),int(-1)),int(1)),true,0,empty,empty))),node(','(','(int(3),int(2)),int(-1)),true,0,node(','(','(int(3),int(1)),int(-1)),true,1,empty,node(','(','(int(3),int(1)),int(1)),true,0,empty,empty)),node(','(','(int(3),int(2)),int(1)),true,1,empty,node(','(','(int(3),int(3)),int(3)),true,0,empty,empty))))))),'{((-1|->-1)|->-1),((-1|->-1)|->1),((-1|->0)|->-1),((-1|->0)|->1),((-1|->1)|->-1),((-1|->1)|->1),((-1|->2)|->-1),((-1|->2)|->1),((-1|->3)|->-1),((-1|->3)|->1),((0|->-1)|->-1),((0|->-1)|->1),((0|->1)|->-1),((0|->1)|->1),((0|->2)|->2),((0|->3)|->3),((1|->-1)|->-1),((1|->-1)|->1),((1|->0)|->-1),((1|->0)|->1),((1|->1)|->-1),((1|->1)|->1),((1|->2)|->-1),((1|->2)|->1),((1|->3)|->-1),((1|->3)|->1),((2|->-1)|->-1),((2|->-1)|->1),((2|->0)|->2),((2|->1)|->-1),((2|->1)|->1),((2|->2)|->2),((2|->3)|->-1),((2|->3)|->1),((3|->-1)|->-1),((3|->-1)|->1),((3|->0)|->3),((3|->1)|->-1),((3|->1)|->1),((3|->2)|->-1),((3|->2)|->1),((3|->3)|->3)}'),'.'(binding(f,int(0),'0'),'.'(binding(dk,int(0),'0'),'.'(binding(dn,pred_false,'FALSE'),'.'(binding(k,int(0),'0'),'.'(binding(uk,int(0),'0'),'.'(binding(vk,int(0),'0'),'.'(binding(h,int(0),'0'),'.'(binding(q,avl_set(node(','(int(0),int(0)),true,0,empty,empty)),'{(0|->0)}'),'.'(binding(r,avl_set(node(','(int(0),int(0)),true,0,empty,empty)),'{(0|->0)}'),'.'(binding(s,avl_set(node(','(int(0),int(0)),true,0,empty,empty)),'{(0|->0)}'),'.'(binding(t,avl_set(node(','(int(0),int(1)),true,0,empty,empty)),'{(0|->1)}'),'.'(binding(up,pred_true,'TRUE'),'.'(binding(u,int(0),'0'),'.'(binding(v,int(0),'0'),'.'(binding(a,int(0),'0'),'.'(binding(b,int(1),'1'),'.'(binding(d,int(0),'0'),[]))))))))))))))))))))),'.'(=('AAAPLOps','.'(op(6,switch,22,23,[],[],'.'(event('.'(event(switch,[]),'.'(event(switch,[]),'.'(event(switch,[]),'.'(event(switch,[]),[]))))),[])),[])),'.'(=('BErrors',[]),'.'(=('AAFPropResult',false),'.'(=('AAEPropResult',false),'.'(=('AADPropResult',false),'.'(=('AACPropResult',true),'.'(=('AAHErrors',[]),[]))))))))))"

    val ast = ProBResultParser.parse(input);
    val b = BindingGenerator.createBindingMustNotFail(query, ast);

    functions.foreach(f => annotation ! f(b, "1"))
    //test if StateErrors are serializable
    var iterable = Iterable[StateError](new StateError("a", "b", "c"), new StateError("b", "c", "d"))
    annotation ! UpdateStateErrors("1", iterable)
  }
}

class BStateSpaceAnnotation extends Actor {
  var initialized = Map[State, Boolean]()
  var invariantViolated = Map[State, Boolean]()
  var timeoutOccured = Map[State, Boolean]()
  var maxOperationsReached = Map[State, Boolean]()
  var stateErrors = Map[State, Iterable[StateError]]()
  var timeout = Map[Operation, Boolean]()
  var stateSpace: LocalActorRef = _

  def receive = {
    case UpdateInit(state, value) =>
      initialized += (state -> value)
      EventHandler.info(initialized, "initialized updated!")
    case UpdateInv(state, value) =>
      invariantViolated += (state -> value)
      EventHandler.info(invariantViolated, "invariant violated updated!")
    case UpdateTimeoutOccured(state, value) =>
      timeoutOccured += (state -> value)
      EventHandler.info(timeoutOccured, "timeout occured updated!")
    case UpdateMaxOpReached(state, value) =>
      maxOperationsReached += (state -> value)
      EventHandler.info(maxOperationsReached, "maximum operations reached updated!")
    case UpdateStateErrors(state, errors) =>
      stateErrors += (state -> errors)
      EventHandler.info(stateErrors, "state errors updated!")
    case UpdateTimeout(edge, value) =>
      timeout += (edge -> value)
      EventHandler.info(timeout, "timeout updated!")
    case l: LocalActorRef =>
      stateSpace = l
      stateSpace ! "connected"
      stateSpace ! Initialized()
      stateSpace ! InvariantViolated()
      stateSpace ! TimeoutOccured()
      stateSpace ! MaxOperationsReached()
      stateSpace ! StateErrors()
      stateSpace ! Timeout()
      stateSpace ! "startTest"
    case x => println("blah")
  }
}

abstract class Update
case class UpdateInit(state: State, value: Boolean) extends Update
case class UpdateInv(state: State, value: Boolean) extends Update
case class UpdateTimeoutOccured(state: State, value: Boolean) extends Update
case class UpdateMaxOpReached(state: State, value: Boolean) extends Update
case class UpdateStateErrors(state: State, errors: Iterable[StateError]) extends Update
case class UpdateTimeout(edge: Operation, value: Boolean) extends Update

//The key set is: 
// [AABBindings, AAHErrors, AADPropResult, BErrors, AAGTO, AACPropResult, AAEPropResult, AAFPropResult, AAAPLOps]
abstract class Init {
  val fkt: (java.util.Map[String, PrologTerm], State) => Update

  implicit def toBoolean(x: PrologTerm): Boolean = x match {
    case x: PrologTerm => stringToBool(x.toString)
  }

  def stringToBool(x: String): Boolean = x match {
    case "true" => true
    case "false" => false
  }
}
case class Initialized() extends Init {
  val fkt = (map: java.util.Map[String, PrologTerm], s: State) => UpdateInit(s, map.get("AACPropResult"))
}
case class InvariantViolated() extends Init {
  val fkt = (map: java.util.Map[String, PrologTerm], s: State) => UpdateInv(s, map.get("AADPropResult"))
}
case class TimeoutOccured() extends Init {
  val fkt = (map: java.util.Map[String, PrologTerm], s: State) => UpdateTimeoutOccured(s, map.get("AAFPropResult"))
}
case class MaxOperationsReached() extends Init {
  val fkt = (map: java.util.Map[String, PrologTerm], s: State) => UpdateMaxOpReached(s, map.get("AAEPropResult"))
}
case class StateErrors() extends Init {
  val fkt = (map: java.util.Map[String, PrologTerm], s: State) => {
    import scala.collection.JavaConversions._
    val a = map.get("AAHErrors")
    var b = Iterable[StateError]()
    if (a.isInstanceOf[java.util.Collection[StateError]])
      b = a.asInstanceOf[java.util.Collection[StateError]]
    UpdateStateErrors(s, b)
  }
}
//TODO: Figure out how to identify which Edge goes with each Timeout
case class Timeout() extends Init {
  val fkt = (map: java.util.Map[String, PrologTerm], s: State) => null
}

/*
 * In StateSpaceObject
 * 	=> Map[String,Map[String,Value]]
 * 
 * Implement BState Object
 * With:
 * 	initialized => Map[String,Boolean]
 *  invariantViolated => Map[String,Boolean]
 *  timeoutOccured => Map[String,Boolean]
 *  maxOperationenReached => Map[String,Boolean]
 *  stateErrors => Map[String,Collection[StateError]]
 *  timeout => Map[String,Set[String]]
 *  
 *  Implement with akka actors
 *  
 *  
 *  
 *  Hallo, 
 *  
 *  hier ist ein Beispiel. Die Query ist das, was die Kommandos erzeugen und an 
 *  Prolog schicken, Input ist das, was wir von Prolog als Antwort bekommen.
 *  
 *  Die parse Methode von ProBResultParser zerlegt die Antwort, das sind aber noch keine 
 *  PrologTerme. Die werden von  der createBindingMustNotFail Methode in BindingGenerator
 *  erzeugt.
 *  
 *  Die Map wird noch an die einzelnen Kommandos geschickt, die sich die richtigen 
 *  Informationen rausholen. Zum Beispiel sind die Werte für die B-Variablen in der Map 
 *  unterAABBindings gespeichert. Die Informationen über die boolschen Werte 
 *  (Invariante, timeout, …) sind in der Map unter den verschiedenen AAXPropResult 
 *  gespeichert. 
 *  
 *  Du kannst auch Daniel fragen, er hat grosse Teile des Kerns geschrieben und kennt 
 *  sich sehr gut damit aus. 
 *  
 *  Gruss Jens
 *  
 *  String query="computeOperationsForState(22,AAAPLOps),getStateValues(22,AABBindings),state_property(initialised,22,AACPropResult),state_property(invariantKO,22,AADPropResult),state_property(max_operations_reached,22,AAEPropResult),state_property(timeout_occurred,22,AAFPropResult),op_timeout_occurred(22,AAGTO),get_state_errors(22,AAHErrors),setCurrentState(22),getErrorMessages(BErrors).";
 *  String input="yes('.'(=('AAGTO',[]),'.'(=('AABBindings','.'(binding(abs,avl_set(node(','(int(1),int(1)),true,0,node(','(int(-1),int(1)),true,1,empty,node(','(int(0),int(0)),true,0,empty,empty)),node(','(int(2),int(2)),true,1,empty,node(','(int(3),int(3)),true,0,empty,empty)))),'{(-1|->1),(0|->0),(1|->1),(2|->2),(3|->3)}'),'.'(binding(dv,avl_set(node(','(int(1),int(0)),true,0,node(','(int(-1),int(1)),true,0,node(','(int(-1),int(-1)),true,1,empty,node(','(int(-1),int(0)),true,0,empty,empty)),node(','(int(-1),int(3)),true,0,node(','(int(-1),int(2)),true,0,empty,empty),node(','(int(1),int(-1)),true,0,empty,empty))),node(','(int(2),int(0)),true,0,node(','(int(1),int(2)),true,0,node(','(int(1),int(1)),true,0,empty,empty),node(','(int(1),int(3)),true,0,empty,empty)),node(','(int(3),int(0)),true,0,node(','(int(2),int(2)),true,0,empty,empty),node(','(int(3),int(3)),true,0,empty,empty))))),'{(-1|->-1),(-1|->0),(-1|->1),(-1|->2),(-1|->3),(1|->-1),(1|->0),(1|->1),(1|->2),(1|->3),(2|->0),(2|->2),(3|->0),(3|->3)}'),'.'(binding(gcd,avl_set(node(','(','(int(1),int(1)),int(-1)),true,0,node(','(','(int(-1),int(3)),int(1)),true,0,node(','(','(int(-1),int(1)),int(-1)),true,0,node(','(','(int(-1),int(-1)),int(1)),true,1,node(','(','(int(-1),int(-1)),int(-1)),true,0,empty,empty),node(','(','(int(-1),int(0)),int(-1)),true,1,empty,node(','(','(int(-1),int(0)),int(1)),true,0,empty,empty))),node(','(','(int(-1),int(2)),int(-1)),true,1,node(','(','(int(-1),int(1)),int(1)),true,0,empty,empty),node(','(','(int(-1),int(2)),int(1)),true,1,empty,node(','(','(int(-1),int(3)),int(-1)),true,0,empty,empty)))),node(','(','(int(0),int(2)),int(2)),true,0,node(','(','(int(0),int(-1)),int(1)),true,1,node(','(','(int(0),int(-1)),int(-1)),true,0,empty,empty),node(','(','(int(0),int(1)),int(-1)),true,1,empty,node(','(','(int(0),int(1)),int(1)),true,0,empty,empty))),node(','(','(int(1),int(-1)),int(1)),true,0,node(','(','(int(0),int(3)),int(3)),true,1,empty,node(','(','(int(1),int(-1)),int(-1)),true,0,empty,empty)),node(','(','(int(1),int(0)),int(-1)),true,1,empty,node(','(','(int(1),int(0)),int(1)),true,0,empty,empty))))),node(','(','(int(2),int(2)),int(2)),true,0,node(','(','(int(1),int(3)),int(1)),true,0,node(','(','(int(1),int(2)),int(-1)),true,1,node(','(','(int(1),int(1)),int(1)),true,0,empty,empty),node(','(','(int(1),int(2)),int(1)),true,1,empty,node(','(','(int(1),int(3)),int(-1)),true,0,empty,empty))),node(','(','(int(2),int(0)),int(2)),true,0,node(','(','(int(2),int(-1)),int(-1)),true,1,empty,node(','(','(int(2),int(-1)),int(1)),true,0,empty,empty)),node(','(','(int(2),int(1)),int(-1)),true,1,empty,node(','(','(int(2),int(1)),int(1)),true,0,empty,empty)))),node(','(','(int(3),int(0)),int(3)),true,0,node(','(','(int(2),int(3)),int(1)),true,1,node(','(','(int(2),int(3)),int(-1)),true,0,empty,empty),node(','(','(int(3),int(-1)),int(-1)),true,1,empty,node(','(','(int(3),int(-1)),int(1)),true,0,empty,empty))),node(','(','(int(3),int(2)),int(-1)),true,0,node(','(','(int(3),int(1)),int(-1)),true,1,empty,node(','(','(int(3),int(1)),int(1)),true,0,empty,empty)),node(','(','(int(3),int(2)),int(1)),true,1,empty,node(','(','(int(3),int(3)),int(3)),true,0,empty,empty))))))),'{((-1|->-1)|->-1),((-1|->-1)|->1),((-1|->0)|->-1),((-1|->0)|->1),((-1|->1)|->-1),((-1|->1)|->1),((-1|->2)|->-1),((-1|->2)|->1),((-1|->3)|->-1),((-1|->3)|->1),((0|->-1)|->-1),((0|->-1)|->1),((0|->1)|->-1),((0|->1)|->1),((0|->2)|->2),((0|->3)|->3),((1|->-1)|->-1),((1|->-1)|->1),((1|->0)|->-1),((1|->0)|->1),((1|->1)|->-1),((1|->1)|->1),((1|->2)|->-1),((1|->2)|->1),((1|->3)|->-1),((1|->3)|->1),((2|->-1)|->-1),((2|->-1)|->1),((2|->0)|->2),((2|->1)|->-1),((2|->1)|->1),((2|->2)|->2),((2|->3)|->-1),((2|->3)|->1),((3|->-1)|->-1),((3|->-1)|->1),((3|->0)|->3),((3|->1)|->-1),((3|->1)|->1),((3|->2)|->-1),((3|->2)|->1),((3|->3)|->3)}'),'.'(binding(f,int(0),'0'),'.'(binding(dk,int(0),'0'),'.'(binding(dn,pred_false,'FALSE'),'.'(binding(k,int(0),'0'),'.'(binding(uk,int(0),'0'),'.'(binding(vk,int(0),'0'),'.'(binding(h,int(0),'0'),'.'(binding(q,avl_set(node(','(int(0),int(0)),true,0,empty,empty)),'{(0|->0)}'),'.'(binding(r,avl_set(node(','(int(0),int(0)),true,0,empty,empty)),'{(0|->0)}'),'.'(binding(s,avl_set(node(','(int(0),int(0)),true,0,empty,empty)),'{(0|->0)}'),'.'(binding(t,avl_set(node(','(int(0),int(1)),true,0,empty,empty)),'{(0|->1)}'),'.'(binding(up,pred_true,'TRUE'),'.'(binding(u,int(0),'0'),'.'(binding(v,int(0),'0'),'.'(binding(a,int(0),'0'),'.'(binding(b,int(1),'1'),'.'(binding(d,int(0),'0'),[]))))))))))))))))))))),'.'(=('AAAPLOps','.'(op(6,switch,22,23,[],[],'.'(event('.'(event(switch,[]),'.'(event(switch,[]),'.'(event(switch,[]),'.'(event(switch,[]),[]))))),[])),[])),'.'(=('BErrors',[]),'.'(=('AAFPropResult',false),'.'(=('AAEPropResult',false),'.'(=('AADPropResult',false),'.'(=('AACPropResult',true),'.'(=('AAHErrors',[]),[]))))))))))"
 *  
 *  Start ast = ProBResultParser.parse(input);
 *  Map<String, PrologTerm> b = BindingGenerator.createBindingMustNotFail(query, ast);
 *  
 */

