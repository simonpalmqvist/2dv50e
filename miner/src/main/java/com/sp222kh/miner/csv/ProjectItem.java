package com.sp222kh.miner.csv;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

import java.util.Date;

@CsvDataType
public class ProjectItem {

    @CsvField(pos = 1)
    public long id;

    @CsvField(pos = 2)
    public String url;

    @CsvField(pos = 3)
    public long ownerId;

    @CsvField(pos = 4)
    public String name;

    @CsvField(pos = 5)
    public String description;

    @CsvField(pos = 6)
    public String language;

    @CsvField(pos = 7, format = "yyyy-MM-dd HH:mm:ss")
    public Date createdAt;

    @CsvField(pos = 8, converterType = LongConverter.class)
    public long forkedFromId;

    @CsvField(pos = 9, converterType = BoolConverter.class)
    public boolean deleted;

    @CsvField(pos = 10, format = "yyyy-MM-dd HH:mm:ss")
    public Date updatedAt;
}
