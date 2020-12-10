package ru.nsu.fit.g20221.DIContainer.impl.testModel;

public class House {

    private Human human;

    private String address;

    public House() {
    }

    public House(Human human) {
        this.human = human;
    }

    public House(Human human, String address) {
        this.human = human;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Human getHuman() {
        return human;
    }

    public void setHuman(Human human) {
        this.human = human;
    }
}
