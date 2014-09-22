package exploratory.guava;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.base.Objects;

public class HashCode {

	@Test
	public void testHashBuilder() {

		String[] a = { "a", "b", "c" };
		String[] b = { "a", "c", "c" };
		String src = "foo";
		String dst = "bar";
		String name = "me";

		int h1 = Objects.hashCode(src, dst, name, a);
		int h2 = Objects.hashCode(src, dst, name, b);
		int h3 = Objects.hashCode(src, dst, "xx", a);
		assertFalse(h1 == h2);
		assertFalse(h1 == h3);
	}

}
