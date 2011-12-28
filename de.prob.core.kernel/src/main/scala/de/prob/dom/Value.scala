package de.prob.dom

abstract class Value
case class AtomV(content: String) extends Value {
  override def toString = content
}
case class PairV(left: Value, right: Value) extends Value {
  override def toString = "(" + left.toString() + "," + right.toString() + ")"
}

case class ListV(elements: List[Value]) extends Value {
  override def toString = "{" + elements.map(_.toString).reduceLeft(_ + "," + _) + "}"
}

