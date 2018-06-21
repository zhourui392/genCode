package com.zr;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthorInfo {
    public static final String author = "zhourui";

    public static String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }
}
