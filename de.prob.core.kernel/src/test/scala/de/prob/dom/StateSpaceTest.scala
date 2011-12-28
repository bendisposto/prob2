package de.prob.dom

import org.junit.Test
import org.junit.Assert._
import de.prob.prolog.term.PrologTerm
import de.prob.prolog.term.CompoundPrologTerm
import de.prob.parser.ProBResultParser
import de.prob.parser.BindingGenerator
import de.prob.cli.StateError

class StateSpaceTest {

  @Test
  def testProlog {

    val query = "computeOperationsForState(22,AAAPLOps),getStateValues(22,AABBindings),state_property(initialised,22,AACPropResult),state_property(invariantKO,22,AADPropResult),state_property(max_operations_reached,22,AAEPropResult),state_property(timeout_occurred,22,AAFPropResult),op_timeout_occurred(22,AAGTO),get_state_errors(22,AAHErrors),setCurrentState(22),getErrorMessages(BErrors)."
    val input = "yes('.'(=('AAGTO',[]),'.'(=('AABBindings','.'(binding(abs,avl_set(node(','(int(1),int(1)),true,0,node(','(int(-1),int(1)),true,1,empty,node(','(int(0),int(0)),true,0,empty,empty)),node(','(int(2),int(2)),true,1,empty,node(','(int(3),int(3)),true,0,empty,empty)))),'{(-1|->1),(0|->0),(1|->1),(2|->2),(3|->3)}'),'.'(binding(dv,avl_set(node(','(int(1),int(0)),true,0,node(','(int(-1),int(1)),true,0,node(','(int(-1),int(-1)),true,1,empty,node(','(int(-1),int(0)),true,0,empty,empty)),node(','(int(-1),int(3)),true,0,node(','(int(-1),int(2)),true,0,empty,empty),node(','(int(1),int(-1)),true,0,empty,empty))),node(','(int(2),int(0)),true,0,node(','(int(1),int(2)),true,0,node(','(int(1),int(1)),true,0,empty,empty),node(','(int(1),int(3)),true,0,empty,empty)),node(','(int(3),int(0)),true,0,node(','(int(2),int(2)),true,0,empty,empty),node(','(int(3),int(3)),true,0,empty,empty))))),'{(-1|->-1),(-1|->0),(-1|->1),(-1|->2),(-1|->3),(1|->-1),(1|->0),(1|->1),(1|->2),(1|->3),(2|->0),(2|->2),(3|->0),(3|->3)}'),'.'(binding(gcd,avl_set(node(','(','(int(1),int(1)),int(-1)),true,0,node(','(','(int(-1),int(3)),int(1)),true,0,node(','(','(int(-1),int(1)),int(-1)),true,0,node(','(','(int(-1),int(-1)),int(1)),true,1,node(','(','(int(-1),int(-1)),int(-1)),true,0,empty,empty),node(','(','(int(-1),int(0)),int(-1)),true,1,empty,node(','(','(int(-1),int(0)),int(1)),true,0,empty,empty))),node(','(','(int(-1),int(2)),int(-1)),true,1,node(','(','(int(-1),int(1)),int(1)),true,0,empty,empty),node(','(','(int(-1),int(2)),int(1)),true,1,empty,node(','(','(int(-1),int(3)),int(-1)),true,0,empty,empty)))),node(','(','(int(0),int(2)),int(2)),true,0,node(','(','(int(0),int(-1)),int(1)),true,1,node(','(','(int(0),int(-1)),int(-1)),true,0,empty,empty),node(','(','(int(0),int(1)),int(-1)),true,1,empty,node(','(','(int(0),int(1)),int(1)),true,0,empty,empty))),node(','(','(int(1),int(-1)),int(1)),true,0,node(','(','(int(0),int(3)),int(3)),true,1,empty,node(','(','(int(1),int(-1)),int(-1)),true,0,empty,empty)),node(','(','(int(1),int(0)),int(-1)),true,1,empty,node(','(','(int(1),int(0)),int(1)),true,0,empty,empty))))),node(','(','(int(2),int(2)),int(2)),true,0,node(','(','(int(1),int(3)),int(1)),true,0,node(','(','(int(1),int(2)),int(-1)),true,1,node(','(','(int(1),int(1)),int(1)),true,0,empty,empty),node(','(','(int(1),int(2)),int(1)),true,1,empty,node(','(','(int(1),int(3)),int(-1)),true,0,empty,empty))),node(','(','(int(2),int(0)),int(2)),true,0,node(','(','(int(2),int(-1)),int(-1)),true,1,empty,node(','(','(int(2),int(-1)),int(1)),true,0,empty,empty)),node(','(','(int(2),int(1)),int(-1)),true,1,empty,node(','(','(int(2),int(1)),int(1)),true,0,empty,empty)))),node(','(','(int(3),int(0)),int(3)),true,0,node(','(','(int(2),int(3)),int(1)),true,1,node(','(','(int(2),int(3)),int(-1)),true,0,empty,empty),node(','(','(int(3),int(-1)),int(-1)),true,1,empty,node(','(','(int(3),int(-1)),int(1)),true,0,empty,empty))),node(','(','(int(3),int(2)),int(-1)),true,0,node(','(','(int(3),int(1)),int(-1)),true,1,empty,node(','(','(int(3),int(1)),int(1)),true,0,empty,empty)),node(','(','(int(3),int(2)),int(1)),true,1,empty,node(','(','(int(3),int(3)),int(3)),true,0,empty,empty))))))),'{((-1|->-1)|->-1),((-1|->-1)|->1),((-1|->0)|->-1),((-1|->0)|->1),((-1|->1)|->-1),((-1|->1)|->1),((-1|->2)|->-1),((-1|->2)|->1),((-1|->3)|->-1),((-1|->3)|->1),((0|->-1)|->-1),((0|->-1)|->1),((0|->1)|->-1),((0|->1)|->1),((0|->2)|->2),((0|->3)|->3),((1|->-1)|->-1),((1|->-1)|->1),((1|->0)|->-1),((1|->0)|->1),((1|->1)|->-1),((1|->1)|->1),((1|->2)|->-1),((1|->2)|->1),((1|->3)|->-1),((1|->3)|->1),((2|->-1)|->-1),((2|->-1)|->1),((2|->0)|->2),((2|->1)|->-1),((2|->1)|->1),((2|->2)|->2),((2|->3)|->-1),((2|->3)|->1),((3|->-1)|->-1),((3|->-1)|->1),((3|->0)|->3),((3|->1)|->-1),((3|->1)|->1),((3|->2)|->-1),((3|->2)|->1),((3|->3)|->3)}'),'.'(binding(f,int(0),'0'),'.'(binding(dk,int(0),'0'),'.'(binding(dn,pred_false,'FALSE'),'.'(binding(k,int(0),'0'),'.'(binding(uk,int(0),'0'),'.'(binding(vk,int(0),'0'),'.'(binding(h,int(0),'0'),'.'(binding(q,avl_set(node(','(int(0),int(0)),true,0,empty,empty)),'{(0|->0)}'),'.'(binding(r,avl_set(node(','(int(0),int(0)),true,0,empty,empty)),'{(0|->0)}'),'.'(binding(s,avl_set(node(','(int(0),int(0)),true,0,empty,empty)),'{(0|->0)}'),'.'(binding(t,avl_set(node(','(int(0),int(1)),true,0,empty,empty)),'{(0|->1)}'),'.'(binding(up,pred_true,'TRUE'),'.'(binding(u,int(0),'0'),'.'(binding(v,int(0),'0'),'.'(binding(a,int(0),'0'),'.'(binding(b,int(1),'1'),'.'(binding(d,int(0),'0'),[]))))))))))))))))))))),'.'(=('AAAPLOps','.'(op(6,switch,22,23,[],[],'.'(event('.'(event(switch,[]),'.'(event(switch,[]),'.'(event(switch,[]),'.'(event(switch,[]),[]))))),[])),[])),'.'(=('BErrors',[]),'.'(=('AAFPropResult',false),'.'(=('AAEPropResult',false),'.'(=('AADPropResult',false),'.'(=('AACPropResult',true),'.'(=('AAHErrors',[]),[]))))))))))"

    val ast = ProBResultParser.parse(input);
    val b = BindingGenerator.createBindingMustNotFail(query, ast);
    //println(b.keySet)
    val result = b.get("AACPropResult")
    val a = result.toString
    assertEquals("true", a)
    assertTrue(b.get("AABBindings").isInstanceOf[java.util.Collection[CompoundPrologTerm]])
    val list = b.get("AABBindings").asInstanceOf[java.util.Collection[CompoundPrologTerm]]
    var newL = Iterable[CompoundPrologTerm]()
    import scala.collection.JavaConversions._
    newL = list

    //println(b.get("AAGTO"))

    val c = Initialized()
    val result2 = c.fkt(b, "1")
    //assertTrue(result2.value)

    //println(b.get("AAHErrors"))
    val d = b.get("AAHErrors")
    assertTrue(d.isList())
    //println(d.isInstanceOf[java.util.Collection[StateError]])
    //println(d.asInstanceOf[java.util.Collection[StateError]])
  }

}