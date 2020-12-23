package ru.nsu.fit.g20221.DIContainer;

import java.io.InputStream;
import java.util.Collection;

import ru.nsu.fit.g20221.DIContainer.model.JavaObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.ScanObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.XmlObjectConfig;

public interface ConfigurationReader {
    Collection<XmlObjectConfig> readConfigurationFromStream(InputStream stream) throws Exception;

    Collection<JavaObjectConfig> readConfigurationFromClass(String className);

    Collection<ScanObjectConfig> readConfigurationFromComponentScan();
}
