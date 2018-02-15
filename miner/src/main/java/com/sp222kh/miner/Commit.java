package com.sp222kh.miner;

import com.sp222kh.miner.csv.CommitItem;
import com.sp222kh.miner.csv.ProjectCommitItem;
import com.sp222kh.miner.csv.ProjectItem;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "commit")
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "project_id")
    private long projectId;

    @Column
    private String sha;

    @Column(name = "author_id")
    private long authorId;

    @Column(name = "committer_id")
    private long committerId;

    @Column(name = "created_at")
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
