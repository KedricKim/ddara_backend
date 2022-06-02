package epg.ddara_backend.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class dateFormat {

    public static String sbsDateFormat(String today){

        String formattingDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/M/d");

        try {
            Date date = dateFormat.parse(today); // 기존 string을 date 클래스로 변환
            formattingDate = dateFormat2.format(date); // 변환한 값의 format 변경

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return formattingDate;
    }
}
