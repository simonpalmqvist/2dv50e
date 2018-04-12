package com.sp222kh.investigitor.models;

import javax.persistence.*;

@Entity
@Table(name = "project_commit", indexes = {
        @Index(columnList = "project_id", name = "project_commit_id_index"),
        @Index(columnList = "commit_id", name = "project_commit_commit_id_index")
})
public class ProjectCommit {

    @Id
    @Column(columnDefinition = "BIGSERIAL")
    private long id;

    @Column(name = "project_id", nullable = false)
    private long projectId;

    @Column(name = "commit_id", nullable = false)
    private long commitId;

    protected ProjectCommit() {}
}
