package com.sp222kh.investigitor;

import com.sp222kh.investigitor.csv.ProjectItem;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "project")
public class Project {

    @Id
    private long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "forked_from")
    private Long forkedFromId;

    @Column(name = "last_commit")
    private Date lastCommit;

    @Column(nullable = false)
    private int watchers;

    @Column(nullable = false)
    private int contributors;

    protected Project() {}

    public Project(ProjectItem item) {
        this.id = item.id;
        this.url = item.url;
        this.name = item.name;
        this.description = item.description;
        this.createdAt = item.createdAt;
        this.forkedFromId = item.forkedFromId;
        this.watchers = 0;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(Date lastCommit) {
        this.lastCommit = lastCommit;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    public int getContributors() {
        return contributors;
    }

    public void setContributors(int contributors) {
        this.contributors = contributors;
    }

    // Convert api url to clone url
    public String getPathToRepo() {
        return  url.substring(28);
    }

    public long getId() {
        return id;
    }
}
