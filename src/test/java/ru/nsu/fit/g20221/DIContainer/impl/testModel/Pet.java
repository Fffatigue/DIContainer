package ru.nsu.fit.g20221.DIContainer.impl.testModel;

import javax.annotation.PostConstruct;

public class Pet {
    public static int number = 0;

    private House house;

    private Human human;

    private String name;

    public Pet() {
    }

    public Pet(House house, Human human) {
        this.house = house;
        this.human = human;
    }

    public Pet(House house, Human human, String name) {
        this.house = house;
        this.human = human;
        this.name = name;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Human getHuman() {
        return human;
    }

    public void setHuman(Human human) {
        this.human = human;
    }

    @PostConstruct
    public void postConstruct() {
        name = "Pet" + ++number;
    }
}
