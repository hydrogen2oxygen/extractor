package net.hydrogen2oxygen.domain;

public class Category {

    private String name;
    private Integer count = 1;

    private String url;

    public Category(String name, Integer count, String url) {
        this.name = name;
        this.count = count;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Category{" +
                " name=' " + name +
                ", count= " + count +
                ", url= " + url +
                '}';
    }
}
