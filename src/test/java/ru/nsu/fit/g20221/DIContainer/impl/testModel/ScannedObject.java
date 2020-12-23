package ru.nsu.fit.g20221.DIContainer.impl.testModel;

import ru.nsu.fit.g20221.DIContainer.annotation.Component;
import ru.nsu.fit.g20221.DIContainer.annotation.Name;

@Component(name = "scanned")
public class ScannedObject {
    private final House house;
    private final Human human;

    public ScannedObject(@Name("house1") House house, Human human) {
        this.house = house;
        this.human = human;
    }

    public House getHouse() {
        return house;
    }

    public Human getHuman() {
        return human;
    }
}
