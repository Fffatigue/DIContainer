package ru.nsu.fit.g20221.DIContainer.impl;

import java.io.InputStream;
import java.util.Collection;

import ru.nsu.fit.g20221.DIContainer.ConfigurationReader;
import ru.nsu.fit.g20221.DIContainer.model.ObjectConfig;

public class ConfigurationReaderImpl implements ConfigurationReader {
    @Override
    public Collection<ObjectConfig> readConfiguration() {
        //TODO(Ayya) configuration reading
        return null;
    }

    @Override
    public Collection<ObjectConfig> readConfigurationFromStream(InputStream stream) {
        //Implementation will be later
        throw new UnsupportedOperationException();
    }
}
