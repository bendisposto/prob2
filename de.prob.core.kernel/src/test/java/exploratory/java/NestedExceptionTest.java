package exploratory.java;

import static org.junit.Assert.*;

import org.junit.Test;

public class NestedExceptionTest {

	static class ExceptionOne extends Exception {
	}

	static class ExceptionTwo extends Exception {
	}

	public void raise(final Exception e) throws Exception {
		throw e;
	}

	public String exceptional(final int n) throws Exception {
		try {
			if (n % 2 == 1) {
				if (n == 1) {
					raise(new ExceptionOne());
				} else
					throw new RuntimeException();
			}
		} catch (RuntimeException e) {
			throw new ExceptionOne();
		} finally {
			try {
				if (n == 2 || n == 3) {
					if (n == 2) {
						raise(new ExceptionTwo());
					} else
						throw new RuntimeException();
				}
			} catch (RuntimeException e) {
				throw new ExceptionTwo();
			}
		}
		return "done";
	}

	@Test(expected = ExceptionOne.class)
	public void test1() throws Exception {
		exceptional(1);
	}

	@Test(expected = ExceptionTwo.class)
	public void test2() throws Exception {
		exceptional(2);
	}

	@Test(expected = ExceptionTwo.class)
	public void test3() throws Exception {
		exceptional(3);
	}

	@Test
	public void test4() throws Exception {
		String r = exceptional(4);
		assertEquals("done", r);
	}
}
