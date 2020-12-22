package ru.nsu.fit.g20221.DIContainer.model;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JavaObjectConfig {
    private final String name;
    private final List<Class> dependenciesByClass;
    private final List<String> dependenciesByName;
    private final Method creationMethod;

    public JavaObjectConfig(Method creationMethod) {
        this.creationMethod = creationMethod;
        this.name = null;
        this.dependenciesByClass = null;
        this.dependenciesByName = null;
        //TODO достать параметры без аннотаций и добавить в dependenciesByClass,
        // а имена из параметров с аннотациями в dependenciesByName(Ayya)
    }


    public Collection<Class> getDependenciesByClass() {
        return dependenciesByClass;
    }

    public Collection<String> getDependenciesByNames() {
        return dependenciesByName;
    }

    public String getName() {
        return name;
    }

    public ObjectMeta createObject(Map<Class, Object> dependenciesByClass, Map<String, Object> dependenciesByName) {
        //TODO создание объекта на основе замапленных сущностей.
        return null;
    }
}
