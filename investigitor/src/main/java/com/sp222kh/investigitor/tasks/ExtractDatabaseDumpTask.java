package com.sp222kh.investigitor.tasks;


import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.logging.Logger;


import java.io.File;

public class ExtractDatabaseDumpTask implements Task {


    private TarGZipUnArchiver unArchiver;
    private File archive;
    private File destFolder;

    public ExtractDatabaseDumpTask(TarGZipUnArchiver unArchiver, File archive, File destFolder) {
        this.unArchiver = unArchiver;
        this.archive = archive;
        this.destFolder = destFolder;
    }

    @Override
    public void run() throws Exception {
        unArchiver.enableLogging(new ConsoleLogger(Logger.LEVEL_INFO, ExtractDatabaseDumpTask.class.getSimpleName()));
        unArchiver.setSourceFile(archive);
        unArchiver.setDestDirectory(destFolder);
        unArchiver.extract();
    }
}
