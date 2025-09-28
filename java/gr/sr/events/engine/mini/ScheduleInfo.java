package gr.sr.events.engine.mini;

import gr.sr.events.engine.base.EventType;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScheduleInfo
{
    private final Map<Integer, RunTime> _times;
    private int currentRunTimeUsed;
    private boolean defaultTimeUsed;
    
    public ScheduleInfo(final EventType type, final String modeName) {
        this._times = new ConcurrentHashMap<Integer, RunTime>();
        this.currentRunTimeUsed = 0;
        final RunTime defaultTime = this.addTime();
        for (final Day d : Day.values()) {
            defaultTime.addDay(d.prefix);
        }
        this.defaultTimeUsed = true;
    }
    
    public String decrypt() {
        final StringBuilder tb = new StringBuilder();
        for (final RunTime time : this._times.values()) {
            tb.append(time.from + "-" + time.to + "_" + time.getDaysString(false) + ";");
        }
        final String result = tb.toString();
        if (result.length() > 0) {
            return result.substring(0, result.length() - 1);
        }
        return result;
    }
    
    public void encrypt(final String data) {
        if (data.length() == 0) {
            return;
        }
        try {
            final String[] split;
            final String[] runtimes = split = data.split(";");
            for (final String runtime : split) {
                final String hours = runtime.split("_")[0];
                final String daysString = runtime.split("_")[1];
                final String from = hours.split("-")[0];
                final String to = hours.split("-")[1];
                final String[] days = daysString.split(",");
                final RunTime time = this.addTime();
                time.from = from;
                time.to = to;
                if (days.length == 1 && days[0].equals("AllDays")) {
                    for (final Day d : Day.values()) {
                        time.addDay(d.prefix);
                    }
                }
                else {
                    for (final String s : days) {
                        time.addDay(s);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public long getNextStart(final boolean test) {
        if (this._times.size() == 0) {
            return -1L;
        }
        long lowestValue = Long.MAX_VALUE;
        long temp = 0L;
        for (final Map.Entry<Integer, RunTime> time : this._times.entrySet()) {
            temp = time.getValue().getNext(true, test);
            if (temp != -1L && temp < lowestValue) {
                lowestValue = temp;
                this.currentRunTimeUsed = time.getKey();
            }
        }
        return lowestValue;
    }
    
    public long getEnd(final boolean test) {
        if (this._times.size() == 0) {
            return -1L;
        }
        return this._times.get(this.currentRunTimeUsed).getNext(false, test);
    }
    
    public boolean isNonstopRun() {
        if (this._times.size() == 1) {
            for (final RunTime time : this._times.values()) {
                if (time.days.size() == Day.values().length && (time.from.equals("00:00") || time.from.equals("0:00")) && time.to.equals("23:59")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public RunTime addTime() {
        if (this.defaultTimeUsed) {
            this._times.clear();
            this.currentRunTimeUsed = 0;
            this.defaultTimeUsed = false;
        }
        int lastId = 0;
        for (final Integer element : this._times.keySet()) {
            final int id = element;
            if (id > lastId) {
                lastId = id;
            }
        }
        ++lastId;
        final RunTime time = new RunTime(lastId);
        this._times.put(lastId, time);
        return time;
    }
    
    public Map<Integer, RunTime> getTimes() {
        return this._times;
    }
    
    public static void main(final String[] args) {
        final ScheduleInfo info = new ScheduleInfo(EventType.Classic_1v1, "HeyTest");
        final RunTime time1 = info.addTime();
        time1.from = "14:00";
        time1.to = "20:00";
        time1.addDay("m");
        time1.addDay("tu");
        time1.addDay("su");
        final RunTime time2 = info.addTime();
        time2.from = "20:30";
        time2.to = "21:00";
        time2.addDay("m");
        final RunTime time3 = info.addTime();
        time3.from = "14:00";
        time3.to = "14:30";
        time3.addDay("su");
        final long l = info.getNextStart(false);
        System.out.println("Starting in " + l);
        System.out.println("Days: " + l / 86400000L);
        System.out.println("Hours: " + l / 3600000L);
        System.out.println("Minutes: " + l / 60000L);
    }
    
    public enum Day
    {
        Monday("m", 2, "Monday"), 
        Tuesday("tu", 3, "Tuesday"), 
        Wednesday("w", 4, "Wednesday"), 
        Thursday("th", 5, "Thursday"), 
        Friday("f", 6, "Friday"), 
        Saturday("sa", 7, "Saturday"), 
        Sunday("su", 1, "Sunday");
        
        public String prefix;
        int dayId;
        public String _fullName;
        
        private Day(final String s, final int id, final String fullName) {
            this.prefix = s;
            this.dayId = id;
            this._fullName = fullName;
        }
        
        public static Day getDayByName(final String name) {
            for (final Day d : values()) {
                if (d._fullName.equalsIgnoreCase(name)) {
                    return d;
                }
            }
            return null;
        }
        
        public static Day getDay(final String prefix) {
            for (final Day d : values()) {
                if (d.prefix.equals(prefix)) {
                    return d;
                }
            }
            return null;
        }
    }
    
    public class RunTime
    {
        public int _id;
        public List<Day> days;
        public String from;
        public String to;
        
        public RunTime(final int id) {
            this.days = new LinkedList<Day>();
            this._id = id;
            this.from = "00:00";
            this.to = "23:59";
        }
        
        private Calendar getNextRun(final boolean start) {
            if (this.days.isEmpty()) {
                return null;
            }
            final Calendar current = Calendar.getInstance();
            final List<Calendar> times = new LinkedList<Calendar>();
            for (final Day day : this.days) {
                final Calendar time = Calendar.getInstance();
                time.set(7, day.dayId);
                if (start) {
                    time.set(11, Integer.parseInt(this.from.split(":")[0]));
                    time.set(12, Integer.parseInt(this.from.split(":")[1]));
                }
                else {
                    time.set(11, Integer.parseInt(this.to.split(":")[0]));
                    time.set(12, Integer.parseInt(this.to.split(":")[1]));
                }
                times.add(time);
            }
            Calendar runTime = null;
            Calendar temp = null;
            for (final Calendar time2 : times) {
                if (time2.getTimeInMillis() > current.getTimeInMillis()) {
                    if (temp == null) {
                        temp = time2;
                    }
                    else {
                        if (time2.getTimeInMillis() - current.getTimeInMillis() >= temp.getTimeInMillis() - current.getTimeInMillis()) {
                            continue;
                        }
                        temp = time2;
                    }
                }
            }
            if (temp != null) {
                runTime = temp;
            }
            else {
                for (final Calendar time2 : times) {
                    time2.add(10, 168);
                    if (time2.getTimeInMillis() > current.getTimeInMillis()) {
                        if (temp == null) {
                            temp = time2;
                        }
                        else {
                            if (time2.getTimeInMillis() - current.getTimeInMillis() >= temp.getTimeInMillis() - current.getTimeInMillis()) {
                                continue;
                            }
                            temp = time2;
                        }
                    }
                }
            }
            if (temp == null) {
                System.out.println("No time found!! RunTime ID = " + this._id + ", from - " + this.from + ", to " + this.to);
                return null;
            }
            runTime = temp;
            runTime.set(13, 1);
            return runTime;
        }
        
        public long getNext(final boolean start, final boolean test) {
            final Calendar currentTime = Calendar.getInstance();
            final Calendar runTime = this.getNextRun(start);
            if (runTime == null) {
                return -1L;
            }
            if (test) {
                return runTime.getTimeInMillis();
            }
            long delay = runTime.getTimeInMillis() - currentTime.getTimeInMillis();
            if (delay < 0L) {
                delay = 0L;
            }
            return delay;
        }
        
        public String getDaysString(final boolean html) {
            final StringBuilder tb = new StringBuilder();
            int i = 1;
            if (this.days.size() != Day.values().length) {
                for (final Day day : this.days) {
                    tb.append(html ? day.prefix.toUpperCase() : day.prefix);
                    if (i < this.days.size()) {
                        tb.append(",");
                    }
                    ++i;
                }
                return tb.toString();
            }
            if (html) {
                return "All days";
            }
            return "AllDays";
        }
        
        public boolean isActual() {
            final Calendar current = Calendar.getInstance();
            final Calendar start = Calendar.getInstance();
            final Calendar end = Calendar.getInstance();
            start.set(11, Integer.parseInt(this.from.split(":")[0]));
            start.set(12, Integer.parseInt(this.from.split(":")[1]));
            end.set(11, Integer.parseInt(this.to.split(":")[0]));
            end.set(12, Integer.parseInt(this.to.split(":")[1]));
            if (start.getTimeInMillis() > current.getTimeInMillis() || end.getTimeInMillis() < current.getTimeInMillis()) {
                return false;
            }
            for (final Day day : this.days) {
                if (day.dayId == current.get(7)) {
                    return true;
                }
            }
            return false;
        }
        
        public void addDay(final String prefix) {
            for (final Day day : Day.values()) {
                if (prefix.equalsIgnoreCase(day.prefix)) {
                    this.days.add(day);
                    break;
                }
            }
        }
    }
}
