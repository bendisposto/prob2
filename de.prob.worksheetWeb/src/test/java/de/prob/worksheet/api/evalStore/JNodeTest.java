package de.prob.worksheet.api.evalStore;

import org.junit.Assert;
import org.junit.Test;

public class JNodeTest {

	@Test
	public void test() {
		JNode node1 = new JNode("Node 1");
		JNode node2 = new JNode("Node 2");
		JNode node3 = new JNode("Node 3");
		node1.addChildren(node2);
		node1.addChildren(node3);
		JNode node4 = new JNode("Node 4");
		JNode node5 = new JNode("Node 5");
		node2.addChildren(node4);
		node2.addChildren(node5);
		JNode node6 = new JNode("Node 6");
		JNode node7 = new JNode("Node 7");
		node3.addChildren(node6);
		node3.addChildren(node7);
		JNode node8 = new JNode("Node 8");
		JNode node9 = new JNode("Node 9");
		node6.addChildren(node8);
		node4.addChildren(node9);

		Assert.assertEquals(node1.find("Node 9"), node9);
		Assert.assertEquals(node1.find("Node 5"), node5);
		Assert.assertEquals(node1.find("Node 2"), node2);
	}
}
