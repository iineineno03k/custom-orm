package com.iineineno03k.orm.testentity;

import com.iineineno03k.orm.annotation.*;

@Entity
@Table(name = "test_entities")
public class TestEntity {
    @Id
    private Long id;

    @Column(name = "entity_name", nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private String description;

    @Column
    private boolean active;

    // Getter, Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
} 