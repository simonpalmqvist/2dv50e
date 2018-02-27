package com.sp222kh.investigitor.tasks;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class DownloadDatabaseDumpTask implements Task {

    private URL url;
    private File destination;

    public DownloadDatabaseDumpTask(URL url, File destination) {
        this.url = url;
        this.destination = destination;
    }

    @Override
    public void run() throws Exception {
        FileUtils.copyURLToFile(url, destination);
    }
}
