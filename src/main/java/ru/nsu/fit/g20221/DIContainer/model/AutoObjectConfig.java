package ru.nsu.fit.g20221.DIContainer.model;

import java.lang.reflect.Executable;
import java.util.List;
import java.util.Map;

public interface AutoObjectConfig {
    String getName();

    List<Class> getDependenciesByClass();

    List<String> getDependenciesByName();

    Executable getCreationMethod();

    ObjectMeta createObject(
            Map<Class, Object> dependenciesByClass,
            Map<String, Object> dependenciesByName
    );
}
