package com.sp222kh.investigitor.tasks;

import java.io.File;

public class DeleteDatabaseDumpArchiveTask implements Task {

    private File archive;

    public DeleteDatabaseDumpArchiveTask(File archive) {
        this.archive = archive;
    }

    @Override
    public void run() throws Exception {
        archive.delete();
    }
}
