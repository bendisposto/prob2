package exploratory.java;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionExploreTest {

	@Test
	public void testException1() throws Exception {
		Assert.assertEquals(1, exceptionWrapper(false));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testException2() throws Exception {
		exceptionWrapper(true);
	}

	private int exceptionWrapper(final boolean b) {
		int i = 0;
		try {
			i = throwOrNot(b);
		} catch (IOException e) {
			throw new IllegalArgumentException();
		}
		return i;
	}

	private int throwOrNot(final boolean b) throws IOException {
		if (b)
			throw new IOException("bang!");
		return 1;
	}

}
