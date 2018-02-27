package com.sp222kh.investigitor.csv;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

import java.util.Date;

@CsvDataType
public class CommitItem {

    @CsvField(pos = 1)
    public long id;

    @CsvField(pos = 2)
    public String sha;

    @CsvField(pos = 3, converterType = LongConverter.class)
    public long authorId;

    @CsvField(pos = 4, converterType = LongConverter.class)
    public long committerId;

    @CsvField(pos = 5, converterType = LongConverter.class)
    public long projectId;

    @CsvField(pos = 6, format = "yyyy-MM-dd HH:mm:ss")
    public Date createdAt;

}
