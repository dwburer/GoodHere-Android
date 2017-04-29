package com.danielburer.goodhere;

/**
 * Created by daniel on 4/29/17.
 */

public class Product {
    private String name;
    private String owner;
    private int pk;
    private int votes;

    public Product(String name, String owner, int pk, int votes) {
        this.name = name;
        this.owner = owner;
        this.pk = pk;
        this.votes = votes;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public int getPk() {
        return pk;
    }

    public int getVotes() {
        return votes;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                '}';
    }
}
