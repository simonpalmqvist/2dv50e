package com.sp222kh.investigitor.tasks;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class DeleteDatabaseDumpTask implements Task {

    private File destination;

    public DeleteDatabaseDumpTask(File destination) {
        this.destination = destination;
    }

    @Override
    public void run() throws Exception {
        FileUtils.deleteDirectory(destination);
    }
}
