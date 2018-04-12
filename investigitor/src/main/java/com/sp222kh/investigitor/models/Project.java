package com.sp222kh.investigitor.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "project", indexes = {
        @Index(columnList = "language", name = "project_language_index")
})
public class Project {

    @Id
    private long id;

    @Column
    private String url;

    @Column(name = "owner_id")
    private int ownerId;

    @Column
    private String name;

    @Column(length = 1024)
    private String description;

    @Column
    private String language;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "forked_from")
    private Long forkedFromId;

    @Column
    private boolean deleted;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "creator")
    private Long creator;

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
