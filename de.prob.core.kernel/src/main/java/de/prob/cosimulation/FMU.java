package de.prob.cosimulation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ptolemy.fmi.FMICallbackFunctions;
import org.ptolemy.fmi.FMILibrary;
import org.ptolemy.fmi.FMIModelDescription;
import org.ptolemy.fmi.FMIScalarVariable;
import org.ptolemy.fmi.FMUFile;
import org.ptolemy.fmi.FMULibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

public class FMU {

	private static final Logger logger = LoggerFactory.getLogger(FMU.class);

	/** The modelIdentifier from modelDescription.xml. */
	String _modelIdentifier;

	/** The NativeLibrary that contains the functions. */
	NativeLibrary _nativeLibrary;
	private Pointer component;

	private final FMIModelDescription modelDescription;

	private final Map<String, FMIScalarVariable> variables = new HashMap<String, FMIScalarVariable>();

	public FMU(String fmuFileName) throws IOException {
		modelDescription = FMUFile.parseFMUFile(fmuFileName);
		String sharedLibrary = FMUFile.fmuSharedLibrary(modelDescription);

		for (FMIScalarVariable fmiScalarVariable : modelDescription.modelVariables) {
			variables.put(fmiScalarVariable.name, fmiScalarVariable);
		}

		_nativeLibrary = NativeLibrary.getInstance(sharedLibrary);

		// The modelName may have spaces in it.
		_modelIdentifier = modelDescription.modelIdentifier;

		// The URL of the fmu file.
		String fmuLocation = new File(fmuFileName).toURI().toURL().toString();

		// The tool to use if we have tool coupling.
		String mimeType = "application/x-fmu-sharedlibrary";

		// Timeout in ms., 0 means wait forever.
		double timeout = 1000;

		// There is no simulator UI.
		byte visible = 0;
		// Run the simulator without user interaction.
		byte interactive = 0;
		// Callbacks
		FMICallbackFunctions.ByValue callbacks = new FMICallbackFunctions.ByValue(
				new FMULibrary.FMULogger(), new FMULibrary.FMUAllocateMemory(),
				new FMULibrary.FMUFreeMemory(),
				new FMULibrary.FMUStepFinished());
		// Logging tends to cause segfaults because of vararg callbacks.
		byte loggingOn = (byte) 0;

		component = instantiateFMU(fmuLocation, mimeType, timeout, visible,
				interactive, callbacks, loggingOn);

		if (component.equals(Pointer.NULL)) {
			throw new RuntimeException("Could not instantiate model.");
		}
	}

	public String getFmiVersion() {
		assert _nativeLibrary != null;
		Function function = getFunction("_fmiGetVersion");
		return (String) function.invoke(String.class, new Object[0]);
	}

	public void initialize(double startTime, double endTime) {
		invoke("_fmiInitializeSlave", new Object[] { component, startTime,
				(byte) 1, endTime }, "Could not initialize slave: ");
	}

	public boolean getBoolean(String name) {
		FMIScalarVariable fmiScalarVariable = variables.get(name);
		return fmiScalarVariable.getBoolean(component);
	}

	public double getDouble(String name) {
		FMIScalarVariable fmiScalarVariable = variables.get(name);
		return fmiScalarVariable.getDouble(component);
	}

	public int getInt(String name) {
		FMIScalarVariable fmiScalarVariable = variables.get(name);
		return fmiScalarVariable.getInt(component);
	}

	public String getString(String name) {
		FMIScalarVariable fmiScalarVariable = variables.get(name);
		return fmiScalarVariable.getString(component);
	}

	public void set(String name, boolean b) {
		FMIScalarVariable fmiScalarVariable = variables.get(name);
		fmiScalarVariable.setBoolean(component, b);
	}

	public void set(String name, int i) {
		FMIScalarVariable fmiScalarVariable = variables.get(name);
		fmiScalarVariable.setInt(component, i);
	}

	public void set(String name, double d) {
		FMIScalarVariable fmiScalarVariable = variables.get(name);
		fmiScalarVariable.setDouble(component, d);
	}

	public void set(String name, String s) {
		FMIScalarVariable fmiScalarVariable = variables.get(name);
		fmiScalarVariable.setString(component, s);
	}

	public double doStep(double time, double delta_t) {
		Function doStep = getFunction("_fmiDoStep");
		invoke(doStep, new Object[] { component, time, delta_t, (byte) 1 },
				"Could not simulate, time was " + time + ": ");
		return time + delta_t;
	}

	public void terminate() {
		invoke("_fmiTerminateSlave", new Object[] { component },
				"Could not terminate slave: ");
		invoke("_fmiFreeSlaveInstance", new Object[] { component },
				"Could not dispose resources of slave: ");
		component = null;
	}

	@Override
	protected void finalize() throws Throwable {
		this.terminate();
		super.finalize();
	}

	public void reset() {
		invoke("_fmiResetSlave", new Object[] { component },
				"Could not reset slave: ");
	}

	public FMIModelDescription getModelDescription() {
		return modelDescription;
	}

	private Pointer instantiateFMU(String fmuLocation, String mimeType,
			double timeout, byte visible, byte interactive,
			FMICallbackFunctions.ByValue callbacks, byte loggingOn) {
		Function instantiateSlave = getFunction("_fmiInstantiateSlave");
		Pointer fmiComponent = (Pointer) instantiateSlave.invoke(Pointer.class,
				new Object[] { _modelIdentifier, modelDescription.guid,
						fmuLocation, mimeType, timeout, visible, interactive,
						callbacks, loggingOn });
		return fmiComponent;
	}


	/**
	 * Return a function by name.
	 * 
	 * @param name
	 *            The name of the function. The value of the modelIdentifier is
	 *            prepended to the value of this parameter to yield the function
	 *            name.
	 * @return the function.
	 */
	public Function getFunction(String name) {
		// This is syntactic sugar.
		logger.debug("Getting the {} function.", name);
		return _nativeLibrary.getFunction(_modelIdentifier + name);
	}

	/**
	 * Invoke a function that returns an integer representing the FMIStatus
	 * return value.
	 * 
	 * @param name
	 *            The name of the function.
	 * @param arguments
	 *            The arguments to be passed to the function.
	 * @param message
	 *            The error message to be used if there is a problem. The
	 *            message should end with ": " because the return value of the
	 *            function will be printed after the error message.
	 */
	public void invoke(String name, Object[] arguments, String message) {
		Function function = getFunction(name);
		invoke(function, arguments, message);
	}

	/**
	 * Invoke a function that returns an integer representing the FMIStatus
	 * return value.
	 * 
	 * @param function
	 *            The function to be invoked.
	 * @param arguments
	 *            The arguments to be passed to the function.
	 * @param message
	 *            The error message to be used if there is a problem. The
	 *            message should end with ": " because the return value of the
	 *            function will be printed after the error message.
	 */
	public void invoke(Function function, Object[] arguments, String message) {
		logger.debug("Invoking  {}", function.getName());
		int fmiFlag = ((Integer) function.invoke(Integer.class, arguments))
				.intValue();
		if (fmiFlag > FMILibrary.FMIStatus.fmiWarning) {
			throw new RuntimeException(message + fmiFlag);
		}
	}

}
