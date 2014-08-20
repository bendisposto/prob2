package test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class PolySuite extends Suite {

	  // //////////////////////////////
	  // Public helper interfaces

	  /**
	   * Annotation for a method which returns a {@link Configuration}
	   * to be injected into the test class constructor
	   */
	  @Retention(RetentionPolicy.RUNTIME)
	  @Target(ElementType.METHOD)
	  public static @interface Config {
	  }

	  public static interface Configuration {
	    int size();
	    Object getTestValue(int index);
	    String getTestName(int index);
	  }

	  // //////////////////////////////
	  // Fields

	  private final List<Runner> runners;

	  // //////////////////////////////
	  // Constructor

	  /**
	   * Only called reflectively. Do not use programmatically.
	   * @param c the test class
	   * @throws Throwable if something bad happens
	   */
	  public PolySuite(Class<?> c) throws Throwable {
	    super(c, Collections.<Runner>emptyList());
	    TestClass testClass = getTestClass();
	    Class<?> jTestClass = testClass.getJavaClass();
	    Configuration configuration = getConfiguration(testClass);
	    List<Runner> runners = new ArrayList<Runner>();
	    for (int i = 0, size = configuration.size(); i < size; i++) {
	      SingleRunner runner = new SingleRunner(jTestClass, configuration.getTestValue(i), configuration.getTestName(i));
	      runners.add(runner);
	    }
	    this.runners = runners;
	  }

	  // //////////////////////////////
	  // Overrides

	  @Override
	  protected List<Runner> getChildren() {
	    return runners;
	  }

	  // //////////////////////////////
	  // Private

	  private Configuration getConfiguration(TestClass testClass) throws Throwable {
	    return (Configuration) getConfigMethod(testClass).invokeExplosively(null);
	  }

	  private FrameworkMethod getConfigMethod(TestClass testClass) {
	    List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Config.class);
	    if (methods.isEmpty()) {
	      throw new IllegalStateException("@" + Config.class.getSimpleName() + " method not found");
	    }
	    if (methods.size() > 1) {
	      throw new IllegalStateException("Too many @" + Config.class.getSimpleName() + " methods");
	    }
	    FrameworkMethod method = methods.get(0);
	    int modifiers = method.getMethod().getModifiers();
	    if (!(Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))) {
	      throw new IllegalStateException("@" + Config.class.getSimpleName() + " method \"" + method.getName() + "\" must be public static");
	    }
	    return method;
	  }

	  // //////////////////////////////
	  // Helper classes

	  private static class SingleRunner extends BlockJUnit4ClassRunner {

	    private final Object testVal;
	    private final String testName;

	    SingleRunner(Class<?> testClass, Object testVal, String testName) throws InitializationError {
	      super(testClass);
	      this.testVal = testVal;
	      this.testName = testName;
	    }

	    @Override
	    protected Object createTest() throws Exception {
	      return getTestClass().getOnlyConstructor().newInstance(testVal);
	    }

	    @Override
	    protected String getName() {
	      return testName;
	    }

	    @Override
	    protected String testName(FrameworkMethod method) {
	      return testName + ": " + method.getName();
	    }

	    @Override
	    protected void validateConstructor(List<Throwable> errors) {
	      validateOnlyOneConstructor(errors);
	    }

	    @Override
	    protected Statement classBlock(RunNotifier notifier) {
	      return childrenInvoker(notifier);
	    }
	  }
	}