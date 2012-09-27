package de.prob.animator.domainobjects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.VariablePrologTerm;

public class ProBPreferenceTest {

	@Test
	public void test() {
		VariablePrologTerm typeAwesome = new VariablePrologTerm("TypeAwesome");
		CompoundPrologTerm cpt = new CompoundPrologTerm("cpt",
				new CompoundPrologTerm("IamAwesome"), typeAwesome,
				new CompoundPrologTerm("descriptionAwesome"),
				new CompoundPrologTerm("categoryAwesome"),
				new CompoundPrologTerm("awesomeDefaultValue"));
		ProBPreference a = new ProBPreference(cpt);

		assertEquals("IamAwesome", a.name);
		assertEquals(typeAwesome, a.type);
		assertEquals("descriptionAwesome", a.description);
		assertEquals("categoryAwesome", a.category);
		assertEquals("awesomeDefaultValue", a.defaultValue);

		assertEquals(
				"IamAwesome(cat. categoryAwesome, type TypeAwesome, default awesomeDefaultValue) descriptionAwesome",
				a.toString());

		CompoundPrologTerm cpt2 = new CompoundPrologTerm("cpt",
				new CompoundPrologTerm("IamAwesome"), typeAwesome,
				new CompoundPrologTerm("descriptionAwesome"),
				new CompoundPrologTerm("categoryAwesome"),
				new CompoundPrologTerm("awesomeDefaultValue",
						new CompoundPrologTerm("I"), new CompoundPrologTerm(
								" am"), new CompoundPrologTerm(" not"),
						new CompoundPrologTerm(" simple.")));
		a = new ProBPreference(cpt2);

		assertEquals("IamAwesome", a.name);
		assertEquals(typeAwesome, a.type);
		assertEquals("descriptionAwesome", a.description);
		assertEquals("categoryAwesome", a.category);
		assertEquals("awesomeDefaultValue('I',' am',' not',' simple.')",
				a.defaultValue);

	}

}
