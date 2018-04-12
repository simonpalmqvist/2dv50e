package com.sp222kh.investigitor.models;

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
}
