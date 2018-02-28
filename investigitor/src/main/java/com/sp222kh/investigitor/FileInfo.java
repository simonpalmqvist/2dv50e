package com.sp222kh.investigitor;

import org.apache.commons.io.FilenameUtils;

import javax.persistence.*;
import java.io.File;

@Entity
@Table(name = "file", indexes = {
        @Index(columnList = "project_id", name = "file_project_id_index")
})
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "project_id", nullable = false)
    private long projectId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1024)
    private String path;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private long size;

    protected FileInfo() {}

    public FileInfo(long projectId, File file) {
        this.projectId = projectId;
        this.name = file.getName();
        this.path = file.getPath();
        this.type = FilenameUtils.getExtension(file.getName());
        this.size = file.length();
    }
}
