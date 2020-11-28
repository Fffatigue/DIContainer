package ru.nsu.fit.g20221.DIContainer;

import java.io.InputStream;
import java.util.Collection;

import ru.nsu.fit.g20221.DIContainer.model.ObjectConfig;

public interface ConfigurationReader {

    /**
     * @return configurations of objects.
     */
    Collection<ObjectConfig> readConfiguration();

    /**
     * Read configuration from {@link InputStream}
     * @param stream configuration in special format.
     * @return parsed configurations of objects.
     */
    Collection<ObjectConfig> readConfigurationFromStream(InputStream stream);
}
