package ru.nsu.fit.g20221.DIContainer.model;

import java.util.List;

/**
 * Object configuration. Used for creation.
 */
public class ObjectConfig {
    /**
     * Scope of object.
     */
    private final Scope scope;
    /**
     * Object's name.
     */
    private final String name;
    /**
     * Class name of object.
     */
    private final String className;
    /**
     * Ordered constructor args names.
     */
    private final List<String> constructorArgs;

    public ObjectConfig(Scope scope, String name, String className, List<String> constructorArgs) {
        this.scope = scope;
        this.name = name;
        this.className = className;
        this.constructorArgs = constructorArgs;
    }

    public Scope getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getConstructorArgs() {
        return constructorArgs;
    }
}
