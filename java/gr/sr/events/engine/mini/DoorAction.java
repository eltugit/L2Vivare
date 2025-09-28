package gr.sr.events.engine.mini;

import java.util.StringTokenizer;

public enum DoorAction
{
    Open, 
    Close, 
    Default;
    
    public static DoorAction getAction(final String note, final int state) {
        String action = "Default";
        final StringTokenizer st = new StringTokenizer(note);
        if (state == 1) {
            action = st.nextToken();
        }
        else if (state == 2) {
            st.nextToken();
            action = st.nextToken();
        }
        for (final DoorAction d : values()) {
            if (d.toString().equalsIgnoreCase(action)) {
                return d;
            }
        }
        return DoorAction.Default;
    }
}
