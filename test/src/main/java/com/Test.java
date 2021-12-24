package com;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class Test {

    public static final Map<Integer, String> currCourseStatusMap = new LinkedHashMap<>();

    static {
        currCourseStatusMap.put(0, "老师请假");
        currCourseStatusMap.put(1, "学生请假");
        currCourseStatusMap.put(2, "老师调课");
        currCourseStatusMap.put(3, "学生调课");
        currCourseStatusMap.put(4, "老师旷课");
        currCourseStatusMap.put(5, "学生旷课");
        currCourseStatusMap.put(10, "正在上课");
        currCourseStatusMap.put(11, "已结束");
    }

    public static void main(String[] args) throws ParseException, UnsupportedEncodingException {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, 04, 01);
        long pastTime = (System.currentTimeMillis() - calendar.getTime().getTime()) / (60 * 60 * 1000);

//        Calendar calendar = Calendar.getInstance();
//        calendar.setFirstDayOfWeek(Calendar.MONDAY);
//        calendar.set(Calendar.YEAR, 2021);
//        calendar.set(Calendar.WEEK_OF_YEAR,17);
//        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
//        String beginDate = DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(calendar.getTime());
//        calendar.add(Calendar.DAY_OF_WEEK, 6);
//        String endDate = DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(calendar.getTime());
//        System.out.println(beginDate);
//        System.out.println(endDate);
//        System.out.println(10>>1);

//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2020, 11, 06);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        System.out.println(sdf.format(calendar.getTime()));
//
//        long diffTime = System.currentTimeMillis() - calendar.getTime().getTime();
//
//        long s = Long.valueOf(800) * 60 * 60 * 1000;
//        System.out.println(System.currentTimeMillis());
//        System.out.println(s);
//        System.out.println(diffTime);
//        System.out.println(diffTime > s);


//        System.out.println(currCourseStatusMap.get(null));
//        System.out.println(currCourseStatusMap);
//
//        currCourseStatusMap.forEach((k, v) -> {
//            System.out.println(k + "\t" + v);
//        });
//
//        System.out.println();

//        List<String> timeList = Arrays.asList(new String[]{"30", "25", "40"});
//        timeList.sort((o1, o2) -> {
//            Integer a = Integer.valueOf(o1);
//            Integer b = Integer.valueOf(o2);
//            return b.compareTo(a);
//        });
//
//        System.out.println(timeList);

//        Calendar sunday = Calendar.getInstance();
//        sunday.set(Calendar.DAY_OF_WEEK, 7);
//        sunday.add(Calendar.DATE, 1);
//        sunday.set(Calendar.HOUR_OF_DAY, 23);
//        sunday.set(Calendar.MINUTE, 59);
//        sunday.set(Calendar.SECOND, 59);
//
//        System.out.println(DateFormat.getInstance().format(sunday.getTime()));
//
//
//        Float leftTeachinghours = Float.valueOf("1.333");
//        System.out.println(leftTeachinghours > 2f
//        );
//
//        Calendar startCalendar = Calendar.getInstance();
//        startCalendar.set(Calendar.DAY_OF_WEEK, 7);
//        startCalendar.add(Calendar.DATE, 1);
//        startCalendar.set(Calendar.HOUR_OF_DAY, 9);
//        startCalendar.set(Calendar.MINUTE, 20);
//        startCalendar.set(Calendar.SECOND, 0);
//        System.out.println(DateFormat.getDateTimeInstance().format(startCalendar.getTime()));
//        Calendar teacherStartCalendar = Calendar.getInstance();
//        teacherStartCalendar.set(Calendar.DAY_OF_WEEK, Integer.parseInt("1"));
//        teacherStartCalendar.set(Calendar.HOUR_OF_DAY, 23);
//        teacherStartCalendar.add(Calendar.DATE,7);
//        teacherStartCalendar.set(Calendar.MINUTE, 59);
//        teacherStartCalendar.set(Calendar.SECOND, 59);
////        teacherStartCalendar.add(Calendar.DATE,1);
//        System.out.println(DateFormat.getDateTimeInstance().format(teacherStartCalendar.getTime()));
//        teacherStartCalendar.add(Calendar.DATE,7);
//        System.out.println(DateFormat.getDateTimeInstance().format(teacherStartCalendar.getTime()));

//        System.out.println(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }
}
