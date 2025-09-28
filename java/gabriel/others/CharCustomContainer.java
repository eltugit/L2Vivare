package gabriel.others;

import java.util.Base64;

public class CharCustomContainer {
    private int _value;
    private long _regTime;
    private long _time;

    public CharCustomContainer(int value, long regTime, long time) {
        _value = value;
        _regTime = regTime;
        _time = time;
    }

    public int getValue() {
        return _value;
    }

    public long getRegTime() {
        return _regTime;
    }

    public long getTime() {
        return _time;
    }

    public boolean isActive() {
        return (getTime() == 0) || (getRegTime() + getTime() > System.currentTimeMillis());
    }

    public static String getText(String toDecode) {
        return new String(Base64.getDecoder().decode(toDecode));
    }
}

