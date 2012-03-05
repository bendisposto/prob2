package exploratory.java;

import static org.junit.Assert.*;

import org.junit.Test;

public class RefactoredNestedExceptionTest {

	static class ExceptionOne extends Exception {
		private static final long serialVersionUID = -6723739469084198030L;
	}

	static class ExceptionTwo extends Exception {
		private static final long serialVersionUID = 2383431696698789083L;
	}

	public void raise(final Exception e) throws Exception {
		throw e;
	}

	public String exceptional(final int n) throws Exception {
		try {
			int call1 = call1(n);
			System.out.println("R1: " + call1);
		} catch (RuntimeException e) {
			throw new ExceptionOne();
		} finally {
			try {
				int call2 = call2(n);
				System.out.println("R1: " + call2);
			} catch (RuntimeException e) {
				throw new ExceptionTwo();
			}
		}
		return "done";
	}

	private int call2(final int n) throws Exception {
		if (n == 2 || n == 3) {
			if (n == 2) {
				raise(new ExceptionTwo());
			} else
				throw new RuntimeException();
		}
		return 2;
	}

	private int call1(final int n) throws Exception {
		if (n % 2 == 1) {
			if (n == 1) {
				raise(new ExceptionOne());
			} else
				throw new RuntimeException();
		}
		return 1;
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
