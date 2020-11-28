package ru.nsu.fit.g20221.DIContainer.model;

import java.util.function.Supplier;

/**
 * Meta information of object.
 */
public class ObjectMeta {
    /**
     * Scope of object.
     */
    private final Scope scope;
    /**
     * Object's supplier
     */
    private final Supplier<Object> objectSupplier;

    public ObjectMeta(Scope scope, Supplier<Object> objectSupplier) {
        this.scope = scope;
        this.objectSupplier = objectSupplier;
    }

    public Scope getScope() {
        return scope;
    }

    public Object getObject() {
        return objectSupplier.get();
    }
}
