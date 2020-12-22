package ru.nsu.fit.g20221.DIContainer.impl;

import java.io.InputStream;
import java.util.Collection;

import ru.nsu.fit.g20221.DIContainer.ConfigurationReader;
import ru.nsu.fit.g20221.DIContainer.model.JavaObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.XmlObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.XMLConfigure;
import ru.nsu.fit.g20221.DIContainer.util.JaxbUtil;

public class ConfigurationReaderImpl implements ConfigurationReader {
    @Override
    public Collection<XmlObjectConfig> readConfigurationFromStream(InputStream stream) throws Exception {
        XMLConfigure xmlConfigure = JaxbUtil.unmarshall(XMLConfigure.class, stream);

        return xmlConfigure.getObjectConfig();
    }

    @Override
    public Collection<JavaObjectConfig> readConfigurationFromClass(String className) {
        //TODO добавить чтение конфигурации из джава класса(ayya)
        return null;
    }
}
