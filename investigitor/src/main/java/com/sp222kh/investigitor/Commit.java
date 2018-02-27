package com.sp222kh.investigitor;

import com.sp222kh.investigitor.csv.CommitItem;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "commit", indexes = {
        @Index(columnList = "project_id", name = "project_id_index"),
        @Index(columnList = "sha", name = "sha_index")
})
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "project_id", nullable = false)
    private long projectId;

    @Column(nullable = false)
    private String sha;

    @Column(name = "author_id", nullable = false)
    private long authorId;

    @Column(name = "committer_id", nullable = false)
    private long committerId;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    protected Commit() {}

    public Commit(Long projectId, CommitItem item) {
        this.projectId = projectId;
        this.sha = item.sha;
        this.authorId = item.authorId;
        this.committerId = item.committerId;
        this.createdAt = item.createdAt;
    }
}
