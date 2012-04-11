package de.prob.model.representation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import de.be4.classicalb.core.parser.node.Node;

public class NamedEntityTest {

	@Test
	public void test() {
		Node astPart = mock(Node.class);
		NamedEntity ne = new NamedEntity("name", astPart);
		assertEquals("name->" + astPart.getClass().getSimpleName(),
				ne.toString());
	}
}
