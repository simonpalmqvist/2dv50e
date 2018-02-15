package com.sp222kh.miner;

import com.sp222kh.miner.csv.ProjectItem;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "forked_from")
    private Long forkedFromId;


    protected Project() {}

    public Project(ProjectItem item) {
        this.id = item.id;
        this.url = item.url;
        this.name = item.name;
        this.description = item.description;
        this.createdAt = item.createdAt;
        this.updatedAt = item.updatedAt;
        this.forkedFromId = item.forkedFromId;
    }

    @Override
    public String toString() {
        return String.format("Project[id=%d, url='%s', name='%s', description='%s']", id, url, name, description);
    }
}
