package quan.mongo.wrapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期
 * Created by quanchangnai on 2017/6/2.
 */
public class DateWrapper implements TypeWrapper {
    //日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //当前值
    private Date current;
    //原始值
    private Date origin;

    public DateWrapper(Date value) {
        this.origin = value;
        this.current = value;
    }

    public DateWrapper(String value) {
        Date date = parseDate(value);
        this.origin = date;
        this.current = date;
    }

    public void set(Date value) {
        this.current = value;
    }

    public Date get() {
        return this.current;
    }

    @Override
    public void commit() {
        this.origin = current;
    }

    @Override
    public void rollback() {
        this.current = origin;
    }

    @Override
    public String toString() {
        return String.valueOf(current);
    }

    public static Date parseDate(String dateStr) {
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            return Date.from(zonedDateTime.toInstant());
        }
        return null;
    }



    @Override
    public String toDebugString() {
        return "{" +
                "current=" + current +
                ", origin=" + origin +
                '}';
    }
}
