package gr.sr.events.engine;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class EventWarnings {
    protected final Map<Integer, Integer> _warnings = new ConcurrentHashMap<>();
    private ScheduledFuture<?> _decTask;
    private final SaveScheduler _saveScheduler;
    public static int MAX_WARNINGS = 3;

    public EventWarnings() {
        this._decTask = null;
        this._saveScheduler = new SaveScheduler();
        loadData();
        decreasePointsTask();
        SunriseLoader.debug("Loaded EventWarnings engine.");
    }

    protected void decreasePointsTask() {
        if (this._decTask != null) {
            this._decTask.cancel(false);
        }
        Calendar cal = Calendar.getInstance();
        cal.set(11, 23);
        cal.set(12, 59);
        cal.set(13, 30);
        long delay = cal.getTimeInMillis() - System.currentTimeMillis();
        this._decTask = CallBack.getInstance().getOut().scheduleGeneral(() -> {
            for (Integer element : this._warnings.keySet()) {
                int id = element.intValue();
                decreasePoints(id, 1);
                PlayerEventInfo pi = CallBack.getInstance().getOut().getPlayer(id);
                if (pi != null)
                    pi.sendMessage(LanguageEngine.getMsg("system_warningsDecreased", new Object[]{Integer.valueOf(getPoints(id))}));
            }
            saveData();
            decreasePointsTask();
        },delay);
    }

    public int getPoints(PlayerEventInfo player) {
        if (player == null) {
            return -1;
        }
        return this._warnings.containsKey(Integer.valueOf(player.getPlayersId())) ? ((Integer) this._warnings.get(Integer.valueOf(player.getPlayersId()))).intValue() : 0;
    }

    public int getPoints(int player) {
        return this._warnings.containsKey(Integer.valueOf(player)) ? ((Integer) this._warnings.get(Integer.valueOf(player))).intValue() : 0;
    }

    public void addWarning(PlayerEventInfo player, int ammount) {
        if (player == null) {
            return;
        }
        addPoints(player.getPlayersId(), ammount);
        if (ammount > 0) {
            player.sendMessage(LanguageEngine.getMsg("system_warning", new Object[]{
                    Integer.valueOf(MAX_WARNINGS - getPoints(player))
            }));
        }
    }

    public void addPoints(int player, int ammount) {
        int points = 0;
        if (this._warnings.containsKey(Integer.valueOf(player))) {
            points = ((Integer) this._warnings.get(Integer.valueOf(player))).intValue();
        }
        points += ammount;
        if (points < 0) {
            points = 0;
        }
        if (points > 0) {
            this._warnings.put(Integer.valueOf(player), Integer.valueOf(points));
        } else {
            this._warnings.remove(Integer.valueOf(player));
        }
    }

    public void removeWarning(PlayerEventInfo player, int ammount) {
        addWarning(player, -ammount);
    }

    public void decreasePoints(int player, int ammount) {
        addPoints(player, -ammount);
    }

    private void loadData() {
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT id, points FROM sunrise_warnings");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                this._warnings.put(Integer.valueOf(rset.getInt("id")), Integer.valueOf(rset.getInt("points")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        try (Connection con = CallBack.getInstance().getOut().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM sunrise_warnings")) {
                statement.execute();
            }
            for (Map.Entry<Integer, Integer> e : this._warnings.entrySet()) {
                try (PreparedStatement statement = con.prepareStatement("INSERT INTO sunrise_warnings VALUES (" + e.getKey() + "," + e.getValue() + ")")) {
                    statement.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static final EventWarnings getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final EventWarnings _instance = new EventWarnings();
    }

    private class SaveScheduler
            implements Runnable {
        public SaveScheduler() {
            schedule();
        }

        private void schedule() {
            CallBack.getInstance().getOut().scheduleGeneral(this, 1800000L);
        }

        public void run() {
            EventWarnings.this.saveData();
            schedule();
        }
    }
}


