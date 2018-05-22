package com.sp222kh.investigitor.models;

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

    @Column(length = 1024)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    protected Project() {}

    public Date getCreatedAt() {
        return createdAt;
    }

    // Convert api url to clone url
    public String getPathToRepo() {
        return  url.substring(28);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
