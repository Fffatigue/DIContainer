package ru.nsu.fit.g20221.DIContainer.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import ru.nsu.fit.g20221.DIContainer.annotation.Name;

public class ScanObjectConfig implements AutoObjectConfig {
    private final String name;
    private final List<Class> dependenciesByClass;
    private final List<String> dependenciesByName;
    private final Constructor constructor;

    public ScanObjectConfig(String name, Constructor constructor) {
        this.name = name;
        this.constructor = constructor;
        this.dependenciesByClass = new ArrayList<>();
        this.dependenciesByName = new ArrayList<>();
        Class[] types = constructor.getParameterTypes();
        Annotation[][] annotations = constructor.getParameterAnnotations();
        for (int i = 0; i < types.length; i++) {
            String argName = checkAnnotated(annotations[i]);
            if (argName != null) {
                dependenciesByName.add(argName);
            } else {
                dependenciesByClass.add(types[i]);
            }
        }
    }

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

    public String getName() {
        return name;
    }

    public List<Class> getDependenciesByClass() {
        return dependenciesByClass;
    }

    public List<String> getDependenciesByName() {
        return dependenciesByName;
    }

    public Constructor getCreationMethod() {
        return constructor;
    }

    public ObjectMeta createObject(Map<Class, Object> dependenciesByClass,
                                   Map<String, Object> dependenciesByName) {
        List<Object> objects = new ArrayList<>();
        Class[] types = constructor.getParameterTypes();
        Annotation[][] annotations = constructor.getParameterAnnotations();
        for (int i = 0; i < types.length; i++) {
            String argName = checkAnnotated(annotations[i]);
            if (argName != null) {
                objects.add(dependenciesByName.get(argName));
            } else {
                objects.add(dependenciesByClass.get(types[i]));
            }
        }
        try {
            Object instance = constructor.newInstance(objects.toArray());
            return new ObjectMeta(Scope.SINGLETON, () -> instance);
        } catch (Exception e) {
            throw new RuntimeException("Can't instance object");
        }
    }
}
