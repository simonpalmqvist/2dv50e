package com.sp222kh.investigitor.csv;

import net.sf.jsefa.common.converter.SimpleTypeConverter;

public class BoolConverter implements SimpleTypeConverter {

    private static final BoolConverter INSTANCE = new BoolConverter();

    public static BoolConverter create() {
        return INSTANCE;
    }

    private BoolConverter() {
    }

    @Override
    public Object fromString(String s) {
        if(s!=null){
            return s.equals("1");
        }else{
            return false;
        }
    }

    @Override
    public String toString(Object d) {
        return d.toString();
    }

}
