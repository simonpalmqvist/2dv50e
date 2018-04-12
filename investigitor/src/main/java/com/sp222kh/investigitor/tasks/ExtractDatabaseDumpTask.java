package com.sp222kh.investigitor.tasks;

import org.rauschig.jarchivelib.Archiver;


import java.io.File;

public class ExtractDatabaseDumpTask implements Task {


    private Archiver unArchiver;
    private File archive;
    private File destFolder;

    public ExtractDatabaseDumpTask(Archiver unArchiver, File archive, File destFolder) {
        this.unArchiver = unArchiver;
        this.archive = archive;
        this.destFolder = destFolder.getParentFile();
    }

    @Override
    public void run() throws Exception {
        destFolder.mkdir();
        unArchiver.extract(archive, destFolder);
    }
}
