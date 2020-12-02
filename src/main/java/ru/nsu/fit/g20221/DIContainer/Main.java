package ru.nsu.fit.g20221.DIContainer;

import ru.nsu.fit.g20221.DIContainer.impl.ConfigurationReaderImpl;
import ru.nsu.fit.g20221.DIContainer.model.ObjectConfig;

import java.util.Collection;

public class Main {
    public static void main(String[] args) throws Exception {
        Collection<ObjectConfig> xmlConfigure = (new ConfigurationReaderImpl()).readConfigurationFromStream(Main.class.getClassLoader().getResourceAsStream("xmlConfigure.xml"));

        return;
    }
}
