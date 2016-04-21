package com.wisenut.tea20.types;

import org.apache.commons.lang3.StringUtils;

public class ScheduleInfo {
    public static final String CYCLE_D = "1";
    public static final String CYCLE_DS = "D";
    public static final String CYCLE_W = "2";
    public static final String CYCLE_WS = "W";
    private String collectionId;
    private String enableStatus;  // Y,N

    //실행주기 1:매일 2:매주 3:매월 4:직접입력
    private String executeCycle;  // D, W, M, U

    // 일
    private String[] dayBeginDay;
    private String[] dayBeginHour;
    private String[] dayBeginMinute;

    // 주
    private String[] weekDay;
    private String[] weekDay2;
    private String[] weekMinute;
    private String[] weekHour;

    // 직접입력
    private String userMinute;
    private String userHour;
    private String userDay;   // 일
    private String userMonth;
    private String userDay2;  // 주
    private String userDay3;  // 요일


    public String getUserDay3() {
        return userDay3;
    }

    public void setUserDay3(String userDay3) {
        this.userDay3 = userDay3;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(String enableStatus) {
        this.enableStatus = enableStatus;
    }

    public String getExecuteCycle() {
        return executeCycle;
    }

    public void setExecuteCycle(String executeCycle) {
        this.executeCycle = executeCycle;
    }

    public String getUserMinute() {
        return userMinute;
    }

    public void setUserMinute(String userMinute) {
        this.userMinute = userMinute;
    }

    public String getUserHour() {
        return userHour;
    }

    public void setUserHour(String userHour) {
        this.userHour = userHour;
    }

    public String getUserDay() {
        return userDay;
    }

    public void setUserDay(String userDay) {
        this.userDay = userDay;
    }

    public String getUserMonth() {
        return userMonth;
    }

    public void setUserMonth(String userMonth) {
        this.userMonth = userMonth;
    }

    public String getUserDay2() {
        return userDay2;
    }

    public void setUserDay2(String userDay2) {
        this.userDay2 = userDay2;
    }

    public String[] getDayBeginDay() {
        return dayBeginDay;
    }

    public void setDayBeginDay(String[] dayBeginDay) {
        this.dayBeginDay = dayBeginDay;
    }

    public String[] getDayBeginHour() {
        return dayBeginHour;
    }

    public void setDayBeginHour(String[] dayBeginHour) {
        this.dayBeginHour = dayBeginHour;
    }

    public String[] getDayBeginMinute() {
        return dayBeginMinute;
    }

    public void setDayBeginMinute(String[] dayBeginMinute) {
        this.dayBeginMinute = dayBeginMinute;
    }

    public String[] getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String[] weekDay) {
        this.weekDay = weekDay;
    }

    public String[] getWeekDay2() {
        return weekDay2;
    }

    public void setWeekDay2(String[] weekDay2) {
        this.weekDay2 = weekDay2;
    }

    public String[] getWeekMinute() {
        return weekMinute;
    }

    public void setWeekMinute(String[] weekMinute) {
        this.weekMinute = weekMinute;
    }

    public String[] getWeekHour() {
        return weekHour;
    }

    public void setWeekHour(String[] weekHour) {
        this.weekHour = weekHour;
    }

    public String makeScheduleString() {
        if (executeCycle.equals(CYCLE_D)) {
            StringBuilder sb = new StringBuilder();
            //sb.append(CYCLE_D);
            //sb.append(",");
            sb.append(StringUtils.join(dayBeginDay, "/"));
            sb.append(",");
            sb.append(StringUtils.join(dayBeginHour, "/"));
            sb.append(",");
            sb.append(StringUtils.join(dayBeginMinute, "/"));
            return sb.toString();
        } else if (executeCycle.equals(CYCLE_W)) {
            StringBuilder sb = new StringBuilder();
            //sb.append(CYCLE_W);
            //sb.append(",");
            sb.append(StringUtils.join(weekDay, "/"));
            sb.append(",");
            sb.append(StringUtils.join(weekDay2, "/"));
            sb.append(",");
            sb.append(StringUtils.join(weekHour, "/"));
            sb.append(",");
            sb.append(StringUtils.join(weekMinute, "/"));
            return sb.toString();
        }
        return "";
    }

    public void parseSchedule(String timeStr) {
        String[] timeTokens = timeStr.split(",");
        if (timeTokens.length >= 4) {
            String executeCycle = timeTokens[0];
            if (executeCycle.equals(CYCLE_D)) {
                String userDay = timeTokens[1];
                String userHour = timeTokens[2];
                String userMinute = timeTokens[3];
                this.setExecuteCycle(CYCLE_DS);
                this.setUserDay(userDay);
                this.setUserHour(userHour);
                this.setUserMinute(userMinute);

                if (userDay != null && userDay.equals("*")) {
                    String[] arr = new String[31];
                    for (Integer i=1; i<32; i++) {
                        arr[i-1] = i.toString();
                    }
                    dayBeginDay = arr;
                } else {
                    // "," "-" 처리는 안함
                    String[] arr = userDay.split("/");
                    dayBeginDay = arr;
                }

                if (userHour != null && userHour.equals("*")) {
                    String[] arr = new String[24];
                    for (Integer i=0; i<24; i++) {
                        arr[i] = i.toString();
                    }
                    dayBeginHour = arr;
                } else {
                    String[] arr = userHour.split("/");
                    dayBeginHour = arr;
                }

                if (userMinute != null && userMinute.equals("*")) {
                    String[] arr = new String[60];
                    for (Integer i=0; i<60; i++) {
                        arr[i] = i.toString();
                    }
                    dayBeginMinute = arr;
                } else {
                    String[] arr = userMinute.split("/");
                    dayBeginMinute = arr;
                }
            } else if (executeCycle.equals(CYCLE_W)) {
                String userDay2 = timeTokens[1];
                String userDay3 = timeTokens[2];
                String userHour = timeTokens[3];
                String userMinute = timeTokens[4];
                this.setExecuteCycle(CYCLE_WS);
                this.setUserDay2(userDay2);
                this.setUserDay3(userDay3);
                this.setUserHour(userHour);
                this.setUserMinute(userMinute);

                if (userDay2 != null && userDay2.equals("*")) {
                    String[] arr = new String[] {
                            "1ST", "2ND", "3RD", "4TH", "5TH"
                    };
                    weekDay = arr;
                } else {
                    String[] arr = userDay2.split("/");
                    weekDay = arr;
                }

                if (userDay3 != null && userDay3.equals("*")) {
                    String[] arr = new String[] {
                            "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"
                    };
                    weekDay2 = arr;
                } else {
                    String[] arr = userDay3.split("/");
                    weekDay2 = arr;
                }

                if (userHour != null && userHour.equals("*")) {
                    String[] arr = new String[24];
                    for (Integer i=0; i<24; i++) {
                        arr[i] = i.toString();
                    }
                    weekHour = arr;
                } else {
                    String[] arr = userHour.split("/");
                    weekHour = arr;
                }

                if (userMinute != null && userMinute.equals("*")) {
                    String[] arr = new String[60];
                    for (Integer i=0; i<60; i++) {
                        arr[i] = i.toString();
                    }
                    weekMinute = arr;
                } else {
                    String[] arr = userMinute.split("/");
                    weekMinute = arr;
                }

            }
        }


    }
}
