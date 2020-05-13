package edu.lawrence.daycare;

import java.util.Date;

public class Child {
    public int childId;
    public String name;
    public Date birthDate;
    public int parentId;

    @Override
    public String toString() {
        return name;
    }
}
