package gr.sr.raidEngine;

public class RaidConfigs {
    private final boolean enabled;
    private final long duration;
    private final long notificationDelay;
    private final boolean daily;
    private final int day;
    private final int hour;
    private final int minutes;
    private final int randomMin;

    public RaidConfigs(boolean enabled, long duration, long notificationDelay, boolean daily, int day, int hour, int minutes, int randomMin) {
        this.enabled = enabled;
        this.duration = duration;
        this.notificationDelay = notificationDelay;
        this.daily = daily;
        this.day = day;
        this.hour = hour;
        this.minutes = minutes;
        this.randomMin = randomMin;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public long getDuration() {
        return this.duration;
    }

    public long getNotifyDelay() {
        return this.notificationDelay;
    }

    public boolean isDaily() {
        return this.daily;
    }

    public int getDay() {
        return this.day;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public int getRandomMins() {
        return this.randomMin;
    }
}
