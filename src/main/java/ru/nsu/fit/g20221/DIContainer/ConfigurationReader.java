package ru.nsu.fit.g20221.DIContainer;

import java.io.InputStream;
import java.util.Collection;

import ru.nsu.fit.g20221.DIContainer.model.JavaObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.ScanObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.XmlObjectConfig;

/**
 * Support class for reading configuration for {@link DIContainer}.
 */
public interface ConfigurationReader {
    /**
     * Read configuration from xml-formatted stream.
     *
     * @param stream xml-formatted stream.
     * @return configuration.
     */
    Collection<XmlObjectConfig> readConfigurationFromStream(InputStream stream);

    /**
     * Read configuration from configuration class.
     *
     * @param clazz configuration's class.
     * @return configuration.
     */
    Collection<JavaObjectConfig> readConfigurationFromClass(Class<?> clazz);

    /**
     * Scans all jvm classes and reads configuration of objects with
     * {@link ru.nsu.fit.g20221.DIContainer.annotation.Component} annotation.
     *
     * @return configuration.
     */
    Collection<ScanObjectConfig> readConfigurationFromComponentScan();
}
