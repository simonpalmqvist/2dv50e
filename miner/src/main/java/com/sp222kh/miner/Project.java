package com.sp222kh.miner;

import javax.persistence.*;

@Entity
@Table(name = "project")
public class Project {

    @Id
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    protected Project() {}

    public Project(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Project[id=%d, name='%s', description='%s']", id, name, description);
    }
}
