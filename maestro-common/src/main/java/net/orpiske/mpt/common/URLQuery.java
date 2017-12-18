package net.orpiske.mpt.common;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Utility class for parsing test target URLs. It makes it easier to parse implementation
 * specific parameters (such as 'parameter1' and 'parameter2' in
 * "amqp://host/queue&parameter1=true&parameter2=value".
 */
public class URLQuery {
    private static final Logger logger = LoggerFactory.getLogger(URLQuery.class);
    private List<NameValuePair> params;

    /**
     * Constructor
     * @param uri a URI string
     * @throws URISyntaxException
     */
    public URLQuery(final String uri) throws URISyntaxException {
        this(new URI(uri));
    }

    /**
     * Constructor
     * @param uri a URI object
     * @throws URISyntaxException
     */
    public URLQuery(URI uri) throws URISyntaxException {
         params = URLEncodedUtils.parse(uri, "UTF-8");

        for (NameValuePair param : params) {
            logger.trace("{}: {}", param.getName(), param.getValue());
        }
    }


    /**
     * Get a parameter value as a string
     * @param name the parameter name
     * @param defaultValue the default value if not given
     * @return the parameter value or defaultValue if not given
     */
    public String getString(final String name, final String defaultValue) {
        for (NameValuePair param : params) {
            if (param.getName().equals(name)) {
                return param.getValue();
            }
        }

        return defaultValue;
    }


    /**
     * Get a parameter value as a boolean
     * @param name the parameter name
     * @param defaultValue the default value if not given
     * @return the parameter value or defaultValue if not given
     */
    public boolean getBoolean(final String name, boolean defaultValue) {
        String value = getString(name, null);

        if (value == null) {
            return defaultValue;
        }

        if (value.toLowerCase().equals("true")) {
            return true;
        }
        else {
            if (value.toLowerCase().equals("false")) {
                return false;
            }
        }

        return defaultValue;
    }


    /**
     * Get a parameter value as an Integer
     * @param name the parameter name
     * @param defaultValue the default value if not given
     * @return the parameter value or defaultValue if not given
     */
    public Integer getInteger(final String name, final Integer defaultValue) {
        String value = getString(name, null);

        if (value == null) {
            return defaultValue;
        }

        return Integer.parseInt(value);
    }

    /**
     * Get a parameter value as a Long
     * @param name the parameter name
     * @param defaultValue the default value if not given
     * @return the parameter value or defaultValue if not given
     */
    public Long getLong(final String name, final Long defaultValue) {
        String value = getString(name, null);

        if (value == null) {
            return defaultValue;
        }

        return Long.parseLong(value);
    }
}
