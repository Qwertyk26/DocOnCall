package ru.handh.doctor.io.db;

import io.realm.RealmObject;

/**
 * Created by sgirn on 11.11.2015.
 */
public class SpecializationDoc extends RealmObject {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
