package gr.sr.events.engine;

import gr.sr.events.SunriseLoader;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.SkillData;
import gr.sr.l2j.CallBack;
import gr.sr.l2j.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventBuffer {
    private final Map<String, Map<Integer, Integer>> _availableBuffs = new ConcurrentHashMap<>();
    private final Map<Integer, Map<String, List<Integer>>> _buffs = new ConcurrentHashMap<>();
    private final Map<Integer, String> _activeSchemes = new ConcurrentHashMap<>();
    private final Map<Integer, String> _activePetSchemes = new ConcurrentHashMap<>();
    private final Map<Integer, List<String>> _modified = new ConcurrentHashMap<>();
    private final DataUpdater _dataUpdater;

    public EventBuffer() {
        this._dataUpdater = new DataUpdater();
        loadAvailableBuffs(true);
    }

    public void reloadBuffer() {
        loadAvailableBuffs(false);
    }

    public void loadPlayer(PlayerEventInfo player) {
        loadData(player.getPlayersId());
    }

    public void buffPlayer(PlayerEventInfo player, boolean heal) {
        if (getBuffs(player) != null && !getBuffs(player).isEmpty()) {
            for (Iterator<Integer> iterator = getBuffs(player).iterator(); iterator.hasNext(); ) {
                int buffId = ((Integer) iterator.next()).intValue();
                player.getSkillEffects(buffId, getLevelFor(buffId));
            }
        }
        if (heal) {
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
        }
    }

    public void buffPlayer(PlayerEventInfo player) {
        if (getBuffs(player) != null && !getBuffs(player).isEmpty()) {
            for (Iterator<Integer> iterator = getBuffs(player).iterator(); iterator.hasNext(); ) {
                int buffId = ((Integer) iterator.next()).intValue();
                player.getSkillEffects(buffId, getLevelFor(buffId));
            }
        }
        if (EventConfig.getInstance().getGlobalConfigBoolean("bufferHealsPlayer")) {
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
        }
    }

    public void buffPet(PlayerEventInfo player) {
        if (player.hasPet()) {
            if (getPlayersCurrentPetScheme(player.getPlayersId()) == null) {
                return;
            }
            for (Iterator<Integer> iterator = getBuffs(player, getPlayersCurrentPetScheme(player.getPlayersId())).iterator(); iterator.hasNext(); ) {
                int buffId = ((Integer) iterator.next()).intValue();
                player.getPetSkillEffects(buffId, getLevelFor(buffId));
            }
        }
    }

    public void addModifiedBuffs(PlayerEventInfo player, String schemeName) {
        if (!this._modified.containsKey(Integer.valueOf(player.getPlayersId()))) {
            this._modified.put(Integer.valueOf(player.getPlayersId()), new LinkedList<>());
        }
        if (!((List) this._modified.get(Integer.valueOf(player.getPlayersId()))).contains(schemeName)) {
            ((List<String>) this._modified.get(Integer.valueOf(player.getPlayersId()))).add(schemeName);
        }
    }

    public void addModifiedBuffs(int player, String schemeName) {
        if (!this._modified.containsKey(Integer.valueOf(player))) {
            this._modified.put(Integer.valueOf(player), new LinkedList<>());
        }
        if (!((List) this._modified.get(Integer.valueOf(player))).contains(schemeName)) {
            ((List<String>) this._modified.get(Integer.valueOf(player))).add(schemeName);
        }
    }

    public boolean hasBuffs(PlayerEventInfo player) {
        try {
            return !((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addScheme(PlayerEventInfo player, String schemeName) {
        if (!Util.isAlphaNumeric(schemeName)) {
            player.sendMessage("Profile name must be alpha-numeric.");
            return false;
        }
        if (!isValidName(schemeName)) {
            player.sendMessage("Profile name must be alpha-numeric.");
            return false;
        }
        if (((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).containsKey(schemeName)) {
            return false;
        }
        if (((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).size() >= 6) {
            player.sendMessage("You can't have more than 6 schemes.");
            return false;
        }
        ((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).put(schemeName, new LinkedList());
        setPlayersCurrentScheme(player.getPlayersId(), schemeName);
        addModifiedBuffs(player, schemeName);
        return true;
    }

    public boolean removeScheme(PlayerEventInfo player, String schemeName) {
        if (!((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).containsKey(schemeName)) {
            return false;
        }
        ((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).remove(schemeName);
        if (schemeName.equals(getPlayersCurrentScheme(player.getPlayersId()))) {
            setPlayersCurrentScheme(player.getPlayersId(), getFirstScheme(player.getPlayersId()));
        }
        addModifiedBuffs(player, schemeName);
        return true;
    }

    public String getPlayersCurrentScheme(int player) {
        String current = this._activeSchemes.get(Integer.valueOf(player));
        if (current == null) {
            current = setPlayersCurrentScheme(player, getFirstScheme(player));
        }
        return current;
    }

    public String getPlayersCurrentPetScheme(int player) {
        return this._activePetSchemes.get(Integer.valueOf(player));
    }

    public String setPlayersCurrentScheme(int player, String schemeName) {
        return setPlayersCurrentScheme(player, schemeName, true);
    }

    public String setPlayersCurrentScheme(int player, String schemeName, boolean updateInDb) {
        if (schemeName == null) {
            this._activeSchemes.remove(Integer.valueOf(player));
            return null;
        }
        if (!((Map) this._buffs.get(Integer.valueOf(player))).containsKey(schemeName)) {
            ((Map) this._buffs.get(Integer.valueOf(player))).put(schemeName, new LinkedList());
        }
        if (updateInDb) {
            if (this._activeSchemes.containsKey(Integer.valueOf(player))) {
                addModifiedBuffs(player, this._activeSchemes.get(Integer.valueOf(player)));
            }
            addModifiedBuffs(player, schemeName);
        }
        this._activeSchemes.put(Integer.valueOf(player), schemeName);
        return schemeName;
    }

    public String setPlayersCurrentPetScheme(int player, String schemeName) {
        if (schemeName == null) {
            this._activePetSchemes.remove(Integer.valueOf(player));
            return null;
        }
        if (!((Map) this._buffs.get(Integer.valueOf(player))).containsKey(schemeName)) {
            ((Map) this._buffs.get(Integer.valueOf(player))).put(schemeName, new LinkedList());
        }
        this._activePetSchemes.put(Integer.valueOf(player), schemeName);
        return schemeName;
    }

    public String getFirstScheme(int player) {
        Iterator<Map.Entry<String, List<Integer>>> iterator = ((Map) this._buffs.get(Integer.valueOf(player))).entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<String, List<Integer>> e = iterator.next();
            return e.getKey();
        }
        return null;
    }

    public Set<Map.Entry<String, List<Integer>>> getSchemes(PlayerEventInfo player) {
        return ((Map<String, List<Integer>>) this._buffs.get(Integer.valueOf(player.getPlayersId()))).entrySet();
    }

    public boolean addBuff(int buffId, PlayerEventInfo player) {
        String scheme = getPlayersCurrentScheme(player.getPlayersId());
        if (scheme == null) {
            return false;
        }
        if (!((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).containsKey(scheme)) {
            return false;
        }
        if (((List) ((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).get(scheme)).contains(Integer.valueOf(buffId))) {
            return false;
        }
        ((List<Integer>) ((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).get(scheme)).add(Integer.valueOf(buffId));
        addModifiedBuffs(player, scheme);
        return true;
    }

    public void removeBuff(int buffId, PlayerEventInfo player) {
        String scheme = getPlayersCurrentScheme(player.getPlayersId());
        if (scheme == null) {
            return;
        }
        ((List) ((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).get(scheme)).remove(new Integer(buffId));
        addModifiedBuffs(player, scheme);
    }

    public boolean containsSkill(int buffId, PlayerEventInfo player) {
        String scheme = getPlayersCurrentScheme(player.getPlayersId());
        if (scheme == null) {
            return false;
        }
        return ((List) ((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).get(scheme)).contains(Integer.valueOf(buffId));
    }

    public List<Integer> getBuffs(PlayerEventInfo player) {
        String scheme = getPlayersCurrentScheme(player.getPlayersId());
        if (scheme == null) {
            return new LinkedList<>();
        }
        return (List<Integer>) ((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).get(scheme);
    }

    public List<Integer> getBuffs(PlayerEventInfo player, String scheme) {
        return (List<Integer>) ((Map) this._buffs.get(Integer.valueOf(player.getPlayersId()))).get(scheme);
    }

    private void loadData(int playerId) {
        synchronized (this._buffs) {
            this._buffs.put(Integer.valueOf(playerId), new LinkedHashMap<>());
            try (Connection con = CallBack.getInstance().getOut().getConnection();
                 PreparedStatement statement = con.prepareStatement("SELECT * FROM sunrise_playerbuffs WHERE playerId = " + playerId);
                 ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    String scheme = rset.getString("scheme");
                    int active = rset.getInt("active");
                    ((Map) this._buffs.get(Integer.valueOf(playerId))).put(scheme, new LinkedList());
                    for (String buffId : rset.getString("buffs").split(",")) {
                        try {
                            ((List<Integer>) ((Map) this._buffs.get(Integer.valueOf(playerId))).get(scheme)).add(Integer.valueOf(Integer.parseInt(buffId)));
                        } catch (Exception e) {
                        }
                    }
                    if (active == 1) {
                        setPlayersCurrentScheme(playerId, scheme, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected synchronized void storeData() {
        if (this._modified.isEmpty()) {
            return;
        }
        try (Connection con = CallBack.getInstance().getOut().getConnection()) {
            for (Map.Entry<Integer, List<String>> modified : this._modified.entrySet()) {
                for (String modifiedScheme : modified.getValue()) {
                    PreparedStatement statement = con.prepareStatement("DELETE FROM sunrise_playerbuffs WHERE playerId = " + modified.getKey() + " AND scheme = '" + modifiedScheme + "'");
                    statement.execute();
                    if (((Map) this._buffs.get(modified.getKey())).containsKey(modifiedScheme)) {
                        StringBuilder tb = new StringBuilder();
                        for (Iterator<Integer> iterator = ((List) ((Map) this._buffs.get(modified.getKey())).get(modifiedScheme)).iterator(); iterator.hasNext(); ) {
                            int buffId = ((Integer) iterator.next()).intValue();
                            tb.append(buffId + ",");
                        }
                        String buffs = tb.toString();
                        if (buffs.length() > 0) {
                            buffs = buffs.substring(0, buffs.length() - 1);
                        }
                        statement = con.prepareStatement("REPLACE INTO sunrise_playerbuffs VALUES (?,?,?,?)");
                        statement.setInt(1, ((Integer) modified.getKey()).intValue());
                        statement.setString(2, modifiedScheme);
                        statement.setString(3, buffs);
                        statement.setInt(4, modifiedScheme.equals(getPlayersCurrentScheme(((Integer) modified.getKey()).intValue())) ? 1 : 0);
                        statement.executeUpdate();
                        statement.close();
                    }
                }
            }
        } catch (Exception exception) {
        }
        this._modified.clear();
    }

    public Map<String, Map<Integer, Integer>> getAvailableBuffs() {
        return this._availableBuffs;
    }

    public int getLevelFor(int skillId) {
        for (Map<Integer, Integer> e : this._availableBuffs.values()) {
            for (Map.Entry<Integer, Integer> entry : e.entrySet()) {
                if (((Integer) entry.getKey()).intValue() == skillId) {
                    return ((Integer) entry.getValue()).intValue();
                }
            }
        }
        return -1;
    }

    private void loadAvailableBuffs(boolean test) {
        if (!this._availableBuffs.isEmpty()) {
            this._availableBuffs.clear();
        }
        int count = 0;
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM sunrise_buffs");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                String category = rset.getString("category");
                int buffId = rset.getInt("buffId");
                int level = rset.getInt("level");
                if (test) {
                    String name = rset.getString("name");
                    if (name == null || name.length() == 0) {
                        try {
                            name = (new SkillData(buffId, level)).getName();
                            if (name != null) {
                                try (PreparedStatement statement2 = con.prepareStatement("UPDATE sunrise_buffs SET name = '" + name + "' WHERE buffId = " + buffId + " AND level = " + level + "")) {
                                    statement2.execute();
                                }
                            }
                        } catch (Exception exception) {
                        }
                    }
                }
                if (!this._availableBuffs.containsKey(category)) {
                    this._availableBuffs.put(category, new LinkedHashMap<>());
                }
                ((Map<Integer, Integer>) this._availableBuffs.get(category)).put(Integer.valueOf(buffId), Integer.valueOf(level));
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SunriseLoader.debug("Loaded " + count + " buffs for Event Buffer.", Level.INFO);
    }

    private class DataUpdater
            implements Runnable {
        protected DataUpdater() {
            CallBack.getInstance().getOut().scheduleGeneralAtFixedRate(this, 10000L, 10000L);
        }

        public void run() {
            EventBuffer.this.storeData();
        }
    }

    private boolean isValidName(String text) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9]*");
        Matcher regexp = pattern.matcher(text);
        if (!regexp.matches()) {
            return false;
        }
        return true;
    }

    public static final EventBuffer getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final EventBuffer _instance = new EventBuffer();
    }
}


