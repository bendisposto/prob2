package de.prob.dom

import org.junit.Test
import org.junit.Assert._
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.uci.ics.jung.graph.util.EdgeType
import edu.uci.ics.jung.graph.util.Pair
import edu.uci.ics.jung.graph.util.EdgeType

class JungTest {
  @Test
  def testGraph {
    val graph = DSMgraph
    graph.addV(Node("1"))
    graph.addV("3")
    graph.addE("2", (Node("1"), Node("3")))
    graph.addE("4", ("3", "1"))
    assertTrue(graph.containsVertex(Node("3")))
    assertFalse(graph.containsVertex(Node("2")))
    assertTrue(graph.containsV("3"))
    assertFalse(graph.containsV("2"))
    assertFalse(graph.containsEdge("3"))
    assertTrue(graph.containsEdge("2"))
    assertTrue(graph.containsE("4"))
    assertFalse(graph.containsE("7"))
    graph.addE("5", ("7", "9"))
    assertTrue(graph.containsE("5"))
    println(graph)
  }

  object DSMgraph extends DirectedSparseMultigraph[Node, String] {
    def addV(v: Node) = addVertex(v)
    def addE(id: String, nodes: Tuple2[Node, Node]) = addEdge(id, nodes._1, nodes._2, EdgeType.DIRECTED)
    def containsV(n: Node) = containsVertex(n)
    def containsE(e: String) = containsEdge(e)
    def getAdjacent(e: Node) = getNeighbors(e)
  }

  case class Node(id: String)
  object Node {
    implicit def stringToNode(x: String): Node = Node(x)
  }
}