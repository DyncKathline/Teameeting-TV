package org.dync.tv.teameeting.utils;

import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by zhulang on 2016/1/6 0006.
 */
public class StringHelper {
    private static GregorianCalendar calendar = new GregorianCalendar();

    public static String format(long timeStr, Resources resources) {

        Date date = new Date(timeStr);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("   HH : mm");
        String YYMMDD = TimeHelper.getCustomStr(getYYMMDD(date), resources);
        return YYMMDD + simpleDateFormat.format(date);
    }

    private static String getYYMMDD(Date date) {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
        String yueDay = time.format(date);
        return yueDay;
    }

}
