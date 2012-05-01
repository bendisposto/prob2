package de.prob.model.representation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.classicalb.ClassicalBEntity;

public class NamedEntityTest {

	@Test
	public void test() {
		Node astPart = mock(Node.class);
		ClassicalBEntity ne = new ClassicalBEntity("name", astPart);
		assertEquals("name->" + astPart.getClass().getSimpleName(),
				ne.toString());
	}
}
