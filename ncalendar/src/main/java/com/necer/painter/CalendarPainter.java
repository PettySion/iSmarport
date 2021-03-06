package com.necer.painter;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.necer.calendar.BaseCalendar;
import com.necer.view.CalendarView;
import com.necer.view.ICalendarView;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * Created by necer on 2019/1/3.
 */
public interface CalendarPainter {

    /**
     * 绘制月日历或这日历背景，如数字背景等
     *
     * @param iCalendarView   ICalendarView 日历页面，可判断是月日历或者周日历
     * @param canvas
     * @param rectF
     * @param localDate
     * @param totalDistance   滑动的全部距离
     * @param currentDistance 当前位置的距离
     */
    void onDrawCalendarBackground(ICalendarView iCalendarView, Canvas canvas, RectF rectF, LocalDate localDate, int totalDistance, int currentDistance);

    /**
     * 绘制今天的日期
     *
     * @param canvas
     * @param rectF
     * @param localDate
     * @param selectedDateList 全部选中的日期集合
     */
    void onDrawToday(Canvas canvas, RectF rectF, LocalDate localDate, List<LocalDate> selectedDateList,int flag);

    /**
     * 绘制当前月或周的日期
     *
     * @param canvas
     * @param rectF
     * @param localDate
     * @param selectedDateList 全部选中的日期集合
     */
    void onDrawCurrentMonthOrWeek(Canvas canvas, RectF rectF, LocalDate localDate, List<LocalDate> selectedDateList,int flag);

    /**
     * 绘制上一月，下一月的日期，周日历不用实现
     *
     * @param canvas
     * @param rectF
     * @param localDate
     * @param selectedDateList 全部选中的日期集合
     */
    void onDrawLastOrNextMonth(Canvas canvas, RectF rectF, LocalDate localDate, List<LocalDate> selectedDateList,int flag);

    /**
     * 绘制不可用的日期，和方法setDateInterval(startFormatDate, endFormatDate)对应,
     * 如果没有使用setDateInterval设置日期范围 此方法不用实现
     *
     * @param canvas
     * @param rectF
     * @param localDate
     */
    void onDrawDisableDate(Canvas canvas, RectF rectF, LocalDate localDate);


}
