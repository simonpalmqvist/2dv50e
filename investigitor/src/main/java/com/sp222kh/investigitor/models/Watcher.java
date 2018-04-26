package com.sp222kh.investigitor.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "watcher"/*, indexes = {
        @Index(columnList = "project_id", name = "watcher_project_id_index")
}*/)
public class Watcher {

    @Id
    @Column(columnDefinition = "SERIAL")
    private long id;

    @Column(name = "project_id", nullable = false)
    private long projectId;

    @Column(name = "user_id", nullable = false)
    private long commitId;

    @Column(name = "created_at")
    private Date createdAt;

    protected Watcher() {}
}
