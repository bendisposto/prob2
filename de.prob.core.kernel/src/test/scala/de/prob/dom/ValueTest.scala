package de.prob.dom

import org.junit.Test
import org.junit.Assert._

class ValueTest {

  @Test
  def testPairValue {
    val pair = PairV(AtomV("foo"), AtomV("bar"))
    assertEquals(pair.toString, "(foo,bar)")
  }

  @Test
  def testListValue {
    val list = ListV(List(AtomV("foo"), AtomV("bar"), AtomV("baz")))
    assertEquals(list.toString, "{foo,bar,baz}")
  }

  @Test
  def testComplexValue {
    val complex = ListV(List(AtomV("foo"), PairV(AtomV("bar"), ListV(List(AtomV("foo"), PairV(AtomV("a"), AtomV("b")), AtomV("c")))), AtomV("baz")))
    assertEquals(complex.toString, "{foo,(bar,{foo,(a,b),c}),baz}")
  }
}
