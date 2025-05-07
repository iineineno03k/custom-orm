package com.iineineno03k.orm.testentity;

import com.iineineno03k.orm.annotation.*;

@Entity
public class EntityWithoutId {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
} 