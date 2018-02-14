package com.sp222kh.miner;

import com.sp222kh.miner.csv.ProjectItem;

import javax.persistence.*;

@Entity
@Table(name = "project")
public class Project {


    @Id
    private long id;

    @Column
    private String url;

    @Column
    private String name;

    @Column
    private String description;

    protected Project() {}

    public Project(ProjectItem item) {
        id = item.id;
        url = item.url;
        name = item.name;
        description = item.description;

    }

    @Override
    public String toString() {
        return String.format("Project[id=%d, url='%s', name='%s', description='%s']", id, url, name, description);
    }
}
