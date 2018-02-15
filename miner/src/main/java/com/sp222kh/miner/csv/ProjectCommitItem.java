package com.sp222kh.miner.csv;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

@CsvDataType
public class ProjectCommitItem {
    @CsvField(pos = 1)
    public long projectId;

    @CsvField(pos = 2)
    public long commitId;
}
