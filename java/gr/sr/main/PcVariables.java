package gr.sr.main;


import l2r.L2DatabaseFactory;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PcVariables {
    protected static final Logger _log = LoggerFactory.getLogger(PcVariables.class.getName());
    private final Map<String, String> personalVariables = new ConcurrentHashMap();
    private final Map<String, Object> quickVars = new ConcurrentHashMap();
    private L2PcInstance player = null;

    public PcVariables(L2PcInstance p) {
        this.player = p;
    }

    
    public void setVar(String name, String variable) {
        this.personalVariables.put(name, variable);
        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("REPLACE INTO sunrise_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,-1)")){
            ps.setInt(1, this.player.getObjectId());
            ps.setString(2, name);
            ps.setString(3, variable);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    
    public void unsetVar(String variable) {
        if (variable != null) {
            if (this.personalVariables.remove(variable) != null) {
                try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
                     PreparedStatement ps = connection.prepareStatement("DELETE FROM `sunrise_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1")){
                    ps.setInt(1, this.player.getObjectId());
                    ps.setString(2, variable);
                    ps.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    public String getVar(String var, String value) {
        return (String)this.personalVariables.get(var) == null ? value : (String)this.personalVariables.get(var);
    }

    
    public boolean getVarB(String var, boolean value) {
        if ((var = (String)this.personalVariables.get(var)) == null) {
            return value;
        } else {
            return !var.equals("0") && !var.equalsIgnoreCase("false");
        }
    }

    
    public boolean getVarB(String var) {
        return (var = (String)this.personalVariables.get(var)) != null && !var.equals("0") && !var.equalsIgnoreCase("false");
    }

    
    public Map<String, String> getVars() {
        return this.personalVariables;
    }

    
    public void loadVariables() {
        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM sunrise_variables WHERE obj_id = ?")){
            ps.setInt(1, this.player.getObjectId());
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    String name = rs.getString("name");
                    String value = Strings.stripSlashes(rs.getString("value"));
                    this.personalVariables.put(name, value);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    
    public void setQuickVar(String var, Object value) {
        this.quickVars.put(var, value);
    }

    
    public String getQuickVar(String var, String... value) {
        if (!this.quickVars.containsKey(var)) {
            return value.length > 0 ? value[0] : null;
        } else {
            return (String)this.quickVars.get(var);
        }
    }

    
    public boolean getQuickVarB(String var, boolean... value) {
        if (!this.quickVars.containsKey(var)) {
            return value.length > 0 ? value[0] : false;
        } else {
            return (Boolean)this.quickVars.get(var);
        }
    }

    
    public int getQuickVarI(String var1, int... value) {
        if (!this.quickVars.containsKey(var1)) {
            return value.length > 0 ? value[0] : -1;
        } else {
            return (Integer)this.quickVars.get(var1);
        }
    }

    
    public long getQuickVarL(String var, long... value) {
        if (!this.quickVars.containsKey(var)) {
            return value.length > 0 ? value[0] : -1L;
        } else {
            return (Long)this.quickVars.get(var);
        }
    }

    
    public Object getQuickVarO(String var, Object... value) {
        if (!this.quickVars.containsKey(var)) {
            return value.length > 0 ? value[0] : null;
        } else {
            return this.quickVars.get(var);
        }
    }

    
    public boolean containsQuickVar(String var) {
        return this.quickVars.containsKey(var);
    }

    
    public void deleteQuickVar(String var) {
        this.quickVars.remove(var);
    }

    
    public L2PcInstance getPlayer() {
        return this.player;
    }
}
