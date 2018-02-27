package com.sp222kh.investigitor.csv;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

@CsvDataType
public class WatcherItem {
    @CsvField(pos = 1)
    public long projectId;

    @CsvField(pos = 2)
    public long userId;
}
