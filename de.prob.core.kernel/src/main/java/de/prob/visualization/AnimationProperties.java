package de.prob.visualization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.inject.Singleton;

@Singleton
public class AnimationProperties {

	Map<String, Properties> properties = new HashMap<String, Properties>();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;
	}

	public synchronized String getProperty(final String filename,
			final String sessionId) {
		return getProperties(filename).getProperty(sessionId);
	}

	public synchronized void setProperty(final String filename,
			final String sessionId, final String sessionInfo) {
		Properties props = getProperties(filename);
		Object prop = props.setProperty(sessionId, sessionInfo);
		if (!sessionInfo.equals(prop)) {
			try {
				props.store(new FileOutputStream(filename), null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized String getPropFileFromModelFile(final String modelFile) {
		return modelFile.substring(0, modelFile.lastIndexOf(".")) + ".viz";
	}

}
