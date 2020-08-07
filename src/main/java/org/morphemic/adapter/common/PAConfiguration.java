package org.morphemic.adapter.common;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.File;

public class PAConfiguration {

    /**
     * The default configuration file
     */
    public static final String PROPERTIES_FILE = "proactive.properties";

    public static final String REST_URL = "pa.rest.url";

    public static final String REST_LOGIN = "pa.rest.login";

    public static final String REST_PASSWORD = "pa.rest.password";

    public static final String SCHEDULER_REST_URL = "pa.scheduler.rest.url";

    public static final String RM_REST_URL = "pa.rm.rest.url";

    private static final ListDelimiterHandler DELIMITER = new DefaultListDelimiterHandler(';');

    private static final String SEPARATOR = File.separator;

    private static final Logger LOGGER = Logger.getLogger(PAConfiguration.class);

    private PAConfiguration() {
    }

    /**
     * Load ProActive configuration
     * @return A ProActive configuration
     * @throws ConfigurationException If a problem occurs when loading ProActive configuration
     */
    public static Configuration loadPAConfiguration() throws ConfigurationException {
        String path = PAConfiguration.class.getClassLoader().getResource(PAConfiguration.PROPERTIES_FILE).toExternalForm();

        Configuration config = loadConfig(path);

        LOGGER.debug("ProActive configuration loaded from file: " + path);

        return config;
    }

    /**
     * Loads the configuration of ProActive.
     * @param path configuration file to load
     * @return A ProActive configuration
     * @throws ConfigurationException If a problem occurs when loading ProActive configuration
     */
    private static Configuration loadConfig(String path) throws ConfigurationException {

        Configuration config;

        PropertiesBuilderParameters propertyParameters = new Parameters().properties();
        propertyParameters.setPath(path);
        propertyParameters.setThrowExceptionOnMissing(true);
        propertyParameters.setListDelimiterHandler(DELIMITER);

        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class);

        builder.configure(propertyParameters);

        config = builder.getConfiguration();

        LOGGER.debug("ProActive configuration loaded");

        return config;
    }
}