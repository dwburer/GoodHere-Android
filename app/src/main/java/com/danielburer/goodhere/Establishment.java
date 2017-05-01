package com.danielburer.goodhere;

public class Establishment {
    private String name;
    private int pk;

    public Establishment(String name, int pk) {
        this.name = name;
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public int getPk() {
        return pk;
    }

    @Override
    public String toString() {
        return name;
    }
}
