package ru.nsu.fit.g20221.DIContainer.model;

/**
 * Lifetime scope.
 */
public enum Scope {
    /**
     * Creating new object for every DI.
     */
    PROTOTYPE,
    /**
     * Using single object for all DI.
     */
    SINGLETON
}
