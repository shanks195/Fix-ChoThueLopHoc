package vn.thuephonghoc.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * sql field to java
 */
public class ColUtil {

    /**
     * Convert mysql data type to java data type
     * @param type database field type
     * @return String
     */
    static String cloToJava(String type) {
        Configuration configuration = getConfig();
        assert configuration != null;
        return configuration.getString(type, "unknowType");
    }

    /**
     * Get configuration information
     */
    private static PropertiesConfiguration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
