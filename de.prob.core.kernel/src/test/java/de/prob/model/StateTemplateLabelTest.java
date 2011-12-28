package de.prob.model;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class StateTemplateLabelTest {

	@Test
	public void testSingleValue() {
		StateTemplateEntry bar = new StateTemplateValue("bar", "0");
		StateTemplateLabel foo = new StateTemplateLabel("foo");
		foo.addChild(bar);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("bar", "3");
		assertEquals("foo\nbar=3", foo.prettyPrint(map));
	}

	@Test
	public void testMultiVals() throws Exception {
		StateTemplateEntry x = new StateTemplateValue("x", "0");
		StateTemplateEntry y = new StateTemplateValue("y", "0");
		StateTemplateLabel foo = new StateTemplateLabel("foo");
		foo.addChild(x);
		foo.addChild(y);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("x", "3");
		map.put("y", "2");
		assertEquals("foo\nx=3, y=2", foo.prettyPrint(map));
	}

	@Test
	public void testDefault() throws Exception {
		StateTemplateEntry x = new StateTemplateValue("x", "5");
		StateTemplateEntry y = new StateTemplateValue("y", "0");
		StateTemplateLabel foo = new StateTemplateLabel("foo");
		foo.addChild(x);
		foo.addChild(y);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("y", "2");
		assertEquals("foo\nx=5, y=2", foo.prettyPrint(map));
	}

}
