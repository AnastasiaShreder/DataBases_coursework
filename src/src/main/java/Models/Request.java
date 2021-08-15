package Models;

import java.sql.Date;
import java.util.Objects;

public class Request {
    private int num;
    private String name;
    private int count;
    private Date date;

    public Request(int num, String name, int count, Date date) {
        this.num = num;
        this.name = name;
        this.count = count;
        this.date = date;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return num == request.num && count == request.count && Objects.equals(name, request.name) && Objects.equals(date, request.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, name, count, date);
    }

    @Override
    public String toString() {
        return "Request{" +
                "num=" + num +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", date=" + date +
                '}';
    }
}
