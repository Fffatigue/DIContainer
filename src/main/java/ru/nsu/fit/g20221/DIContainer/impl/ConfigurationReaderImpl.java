package ru.nsu.fit.g20221.DIContainer.impl;

import java.io.InputStream;
import java.util.Collection;

import ru.nsu.fit.g20221.DIContainer.ConfigurationReader;
import ru.nsu.fit.g20221.DIContainer.model.ObjectConfig;
import ru.nsu.fit.g20221.DIContainer.model.XMLConfigure;
import ru.nsu.fit.g20221.DIContainer.util.JaxbUtil;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class ConfigurationReaderImpl implements ConfigurationReader {
    @Override
    public Collection<ObjectConfig> readConfigurationFromStream(InputStream stream) throws Exception {
        XMLConfigure xmlConfigure = JaxbUtil.unmarshall(XMLConfigure.class, stream);

        return xmlConfigure.getObjectConfig();
    }
}
