package com.eatclub.api.util;

import java.time.LocalTime;

public record TimeWindow(LocalTime start, LocalTime end) {

    /**
     * \
     * Create a window.
     * Boundary: [start, end)
     * Rule: end time must be after the start time and cannot equals to start time.
     *
     * @param start window start time(include)
     * @param end   window end time(not include)
     */
    public TimeWindow {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start or end time can not be null");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    /**
     * Check if a time is within the window.
     *
     * @return true means the window contains the time, vise versa.
     */
    public boolean isInWindow(LocalTime time) {
        if (time == null) {
            return false;
        }
        // This to ensure the boundary [start, end)
        return !time.isBefore(start) && time.isBefore(end);
    }

    /**
     * Get the intersection between two windows.
     *
     * @param window1 first window for compare
     * @param window2 second window for compare
     * @return the intersection window
     */
    public static TimeWindow windowIntersection(TimeWindow window1, TimeWindow window2) {
        if (window1 == null || window2 == null) {
            return null;
        }

        LocalTime start = window1.start().isAfter(window2.start()) ? window1.start() : window2.start();
        LocalTime end = window1.end().isBefore(window2.end()) ? window1.end() : window2.end();

        // If the start time is before the end time, this is a valid window. Otherwise, there is no intersection.
        if (start.isBefore(end)) {
            return new TimeWindow(start, end);
        } else {
            return null;
        }
    }
}
