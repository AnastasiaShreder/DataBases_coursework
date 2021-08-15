package Models;

import java.util.Objects;

public class Good {
    private String name;
    private int priority;
    private int count;

    public Good(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public Good(String name, int priority, int count) {
        this.name = name;
        this.priority = priority;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Good{" +
                "name='" + name + '\'' +
                ", priority=" + priority +
                ", count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Good good = (Good) o;
        return count == good.count && Objects.equals(name, good.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, count);
    }
}
