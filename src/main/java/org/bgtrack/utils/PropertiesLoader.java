package org.bgtrack.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesLoader {
	
	private static final Logger LOG = LogManager.getLogger(PropertiesLoader.class);
	
	private static final String NO_PROP_FILE_FOUND = "No property file found.";
	
	public static String getPropertyValue(String propertyKey) throws IOException {
		
		InputStream inputStream = null;
		String propertyValue = null;
		
		try {
			
			Properties prop = new Properties();
			String propFileName = "season.properties";

			inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				
				prop.load(inputStream);
				
			} else {
				
				throw new FileNotFoundException("Property file " + propFileName + " not in the classpath.");
			
			}

			propertyValue = prop.getProperty(propertyKey);
			
		} catch (Exception e) {
			
			LOG.error(NO_PROP_FILE_FOUND, e);
			
		} finally {
			
			inputStream.close();
			
		}
		
		return propertyValue;
		
	}
	
}
