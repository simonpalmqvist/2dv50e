package com.sp222kh.investigitor.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "is_finished", nullable = false)
    private boolean isFinished;

    @Column(name = "started_at")
    private Date startedAt;

    @Column(name = "finished_at")
    private Date finishedAt;

    protected Status() {}

    public Status(String name) {
        this.name = name;
        this.isFinished = false;
    }

    public void started() {
        startedAt = new Date();
    }

    public void finished() {
        finishedAt = new Date();
        isFinished = true;
    }

    public String getName() {
        return name;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public boolean getIsFinished() {
        return isFinished;
    }
}
