package Models;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Analytics {
    private String name;
    private int count_reqs;
    private int count;
    private Date date;

    public Analytics(int count_reqs, int count, Date date) {
        this.count_reqs = count_reqs;
        this.count = count;
        this.date = date;
    }

    public Analytics(String name, int count_reqs) {
        this.name = name;
        this.count_reqs = count_reqs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount_reqs() {
        return count_reqs;
    }

    public void setCount_reqs(int count_reqs) {
        this.count_reqs = count_reqs;
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
    public String toString() {
        return
                name + ", " +
                 + count_reqs + " ед. спроса";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Analytics analytics = (Analytics) o;
        return count_reqs == analytics.count_reqs && count == analytics.count && Objects.equals(name, analytics.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, count_reqs, count);
    }
}
