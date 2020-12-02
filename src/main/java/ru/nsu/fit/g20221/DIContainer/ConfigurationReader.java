package ru.nsu.fit.g20221.DIContainer;

import ru.nsu.fit.g20221.DIContainer.model.ObjectConfig;

import java.io.InputStream;
import java.util.Collection;

public interface ConfigurationReader {
    Collection<ObjectConfig> readConfigurationFromStream(InputStream stream) throws Exception;
}
