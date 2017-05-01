package com.danielburer.goodhere;

public class Product {
    private String name;
    private String owner;
    private int pk;
    private int votes;
    private int score;

    public Product(String name, String owner, int pk, int votes, int score) {
        this.name = name;
        this.owner = owner;
        this.pk = pk;
        this.votes = votes;
        this.score = score;
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

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                '}';
    }
}
