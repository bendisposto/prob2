package de.prob.visualization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import de.prob.model.representation.AbstractModel;

/**
 * @author joy
 * 
 *         A {@link Singleton} class that contains all of the properties
 *         associated with the visualizations that are currently opened. This
 *         class is synchronized so that reading and writing the different
 *         {@link Properties} is safe. A {@link Properties} object is created
 *         for every {@link AbstractModel} that is being visualized, and the
 *         file name containing the {@link Properties} specification for a given
 *         {@link AbstractModel} can be retrieved using the
 *         {@link #getPropFileFromModel(AbstractModel)} with the
 *         {@link AbstractModel#getModelFile()#toString()} method used as an
 *         input.
 */
@Singleton
public class AnimationProperties {

	Logger logger = LoggerFactory.getLogger(AnimationProperties.class);
	Map<String, Properties> properties = new HashMap<String, Properties>();

	/**
	 * If the properties file has already been loaded, this {@link Properties}
	 * object is returned. Otherwise, the {@link Properties} file is loaded and
	 * saved in the {@link #properties} map. If loading is not successful (i.e.
	 * either a {@link FileNotFoundException} or {@link IOException} is thrown),
	 * the error is logged and an empty {@link Properties} is returned.
	 * 
	 * @param filename
	 * @return {@link Properties} object associated with filename
	 */
	public synchronized Properties getProperties(final String filename) {
		if (properties.containsKey(filename)) {
			return properties.get(filename);
		}
		Properties props = new Properties();
		try {
			File propertyFile = new File(filename);
			if (!propertyFile.exists()) {
				propertyFile.createNewFile();
			}

			props.load(new FileInputStream(propertyFile));
			properties.put(filename, props);
		} catch (FileNotFoundException e) {
			logger.error("Could not open Properties file with filename "
					+ filename, e);
		} catch (IOException e) {
			logger.error("Could not open Properties file with filename "
					+ filename, e);
		}
		return props;
	}

	/**
	 * Finds the {@link Properties} file associated with the specified filename
	 * (using {@link #getProperties(String)}) and searches it to find the
	 * property associated with the given session id. This calls
	 * {@link Properties#getProperty(String)}.
	 * 
	 * @param filename
	 * @param sessionId
	 * @return the property associated with the specified {@link Properties}
	 *         file and the specified session id
	 */
	public synchronized String getProperty(final String filename,
			final String sessionId) {
		return getProperties(filename).getProperty(sessionId);
	}

	/**
	 * Overrides the property sessionId in the {@link Properties} file specified
	 * by the filename parameter with the sessionInfo that is specified.
	 * 
	 * @param filename
	 * @param sessionId
	 * @param sessionInfo
	 */
	public synchronized void setProperty(final String filename,
			final String sessionId, final String sessionInfo) {
		Properties props = getProperties(filename);
		Object prop = props.setProperty(sessionId, sessionInfo);
		if (!sessionInfo.equals(prop)) {
			try {
				props.store(new FileOutputStream(filename), null);
			} catch (FileNotFoundException e) {
				logger.error("Writing property " + sessionId
						+ " to properties file " + filename + " did not work",
						e);
			} catch (IOException e) {
				logger.error("Writing property " + sessionId
						+ " to properties file " + filename + " did not work",
						e);
			}
		}
	}

	/**
	 * Determines the properties file for a given {@link AbstractModel}
	 * 
	 * @param model
	 *            {@link AbstractModel} for which the {@link Properties} should
	 *            be extracted
	 * @return name of file that contains the {@link Properties} for the given
	 *         model file
	 */
	public synchronized String getPropFileFromModel(final AbstractModel model) {
		String modelFile = model.getModelFile().getAbsolutePath();
		return modelFile.substring(0, modelFile.lastIndexOf(".")) + ".viz";
	}

}
