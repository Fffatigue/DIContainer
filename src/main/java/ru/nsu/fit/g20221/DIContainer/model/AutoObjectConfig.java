package ru.nsu.fit.g20221.DIContainer.model;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import ru.nsu.fit.g20221.DIContainer.annotation.Name;

public abstract class AutoObjectConfig {
    private final String name;
    private final List<Class<?>> dependenciesByClass;
    private final List<String> dependenciesByName;

    public AutoObjectConfig(String name, Class<?>[] types, Annotation[][] annotations) {
        this.name = name;
        this.dependenciesByClass = new ArrayList<>();
        this.dependenciesByName = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            String argName = checkAnnotated(annotations[i]);
            if (argName != null) {
                dependenciesByName.add(argName);
            } else {
                dependenciesByClass.add(types[i]);
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<Class<?>> getDependenciesByClass() {
        return dependenciesByClass;
    }

    public List<String> getDependenciesByName() {
        return dependenciesByName;
    }

    public abstract Class<?> getCreatedClass();

    /**
     * Creates an object by invocation creationMethod.
     *
     * @param  dependenciesByClass dependencies by class for method invocation.
     * @param dependenciesByName dependencies by name for method invocation.
     * @return created an ObjectMeta instance.
     */
    public abstract ObjectMeta createObject(
            Map<Class<?>, Object> dependenciesByClass,
            Map<String, Object> dependenciesByName
    );

    @Nullable
    private String checkAnnotated(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Name.class)) {
                Name paramName = (Name) annotation;
                return paramName.value();
            }
        }
        return null;
    }

    protected List<Object> computeObjectParams(Map<Class<?>, Object> dependenciesByClass,
                                               Map<String, Object> dependenciesByName,
                                               Class<?>[] types,
                                               Annotation[][] annotations) {
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            String argName = checkAnnotated(annotations[i]);
            if (argName != null) {
                objects.add(dependenciesByName.get(argName));
            } else {
                objects.add(dependenciesByClass.get(types[i]));
            }
        }
        return objects;

    }
}
