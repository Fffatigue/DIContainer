package ru.nsu.fit.g20221.DIContainer.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Lifetime scope.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scope")
@XmlEnum
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
