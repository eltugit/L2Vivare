package gr.sr.javaBuffer;


import gr.sr.configsEngine.configs.impl.BufferConfigs;
import gr.sr.configsEngine.configs.impl.PremiumServiceConfigs;
import gr.sr.javaBuffer.runnable.BuffDelay;
import gr.sr.main.Conditions;
import l2r.Config;
import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


public class PlayerMethods {
    private static Logger log = LoggerFactory.getLogger(PlayerMethods.class);
    private static final int[] buffList = new int[]{264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 304, 305, 306, 307, 308, 309, 310, 311, 349, 363, 364, 365, 366, 529, 530, 914, 915};

    public PlayerMethods() {
    }

    public static void reloadProfileBuffs(L2PcInstance var0) {
        clearProfiles(var0);
        loadProfileBuffs(var0);
    }

    
    public static void loadProfileBuffs(L2PcInstance player) {
        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT buff_id, profile FROM aio_scheme_profiles_buffs WHERE charId = ?")){
            ps.setInt(1, player.getObjectId());
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    String profile = rs.getString("profile");
                    int buffId = rs.getInt("buff_id");
                    addBuffToProfile(profile, buffId, false, player);
                }

            }
        } catch (SQLException var14) {
            var14.printStackTrace();
        }

    }

    public static void addBuffToProfile(String profile, int buffId, L2PcInstance player) {
        addBuffToProfile(profile, buffId, true, player);
    }

    public static void addBuffToProfile(String profile, int buffId, boolean var2, L2PcInstance player) {
        List<Integer> object;
        if ((object = player._profileBuffs.get(profile)) == null) {
            object = new ArrayList<>();
        }

        object.add(buffId);
        addProfile(profile, object, player);
        if (var2) {
            saveBuff(player, profile, buffId);
        }

    }

    public static void delBuffFromProfile(final String s, final int n, final L2PcInstance l2PcInstance) {
        final List<Integer> list = l2PcInstance._profileBuffs.get(s);
        if (list.contains(n)) {
            list.remove(new Integer(n));
            addProfile(s, list, l2PcInstance);
            deleteBuff(l2PcInstance, s, n);
        }
    }

    public static boolean createProfile(String profile, L2PcInstance player) {
        if (!saveProfile(player, profile)) {
            return false;
        } else {
            addProfile(profile, new ArrayList<>(), player);
            return true;
        }
    }

    public static void addProfile(String profile, List<Integer> buffs, L2PcInstance player) {
        player._profileBuffs.put(profile, buffs);
    }

    
    public static void clearProfiles(L2PcInstance player) {
        player._profileBuffs.keySet().forEach((profile) -> {
            delProfile(profile, false, false, player);
        });
        player._profileBuffs.clear();
    }

    
    public static void delProfile(String profile, L2PcInstance player) {
        delProfile(profile, true, true, player);
    }

    public static void delProfile(String profile, boolean var1, boolean var2, L2PcInstance player) {
        ((List<Integer>)player._profileBuffs.get(profile)).clear();
            if (var1) {
                player._profileBuffs.remove(profile);
            }

        if (var2) {
            deleteProfile(player, profile);
        }

        player.sendMessage("Scheme: " + profile + " deleted.");
    }

    public static List<String> getProfiles(L2PcInstance player) {
        LinkedList list = new LinkedList();
        Iterator iterator = player._profileBuffs.keySet().iterator();

        while(iterator.hasNext()) {
            String var2 = (String)iterator.next();
            list.add(var2);
        }

        return list;
    }

    public static int getProfileSize(String profile, L2PcInstance player) {
        return player._profileBuffs.size();
    }

    public static boolean checkDanceAmount(L2PcInstance player, String profile, BufferMenuCategories category, int mode) {
        if (loadProfile(profile, player) >= BufferConfigs.MAX_DANCE_PERPROFILE) {
            player.sendMessage("You cannot add more than " + BufferConfigs.MAX_DANCE_PERPROFILE + " dances-songs.");
            JavaBufferBypass.callBuffToAdd(category, player, profile, mode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkBuffsAmount(L2PcInstance player, String profile, BufferMenuCategories category, int mode) {
        if (checkProfile(profile, player) >= BufferConfigs.MAX_BUFFS_PERPROFILE) {
            player.sendMessage("You cannot add more than " + BufferConfigs.MAX_BUFFS_PERPROFILE + " buffs.");
            JavaBufferBypass.callBuffToAdd(category, player, profile, mode);
            return false;
        } else {
            return true;
        }
    }

    private static int loadProfile(String profile, L2PcInstance player) {
        int var2 = 0;

        try {
            if (profile == null) {
                if (Config.DEBUG) {
                    log.warn("PROFILE IS NULL!! REPORT TO STAFF TEAM!");
                }

                player.sendMessage("Please restart your char something was wrong.");
                return 0;
            } else if (player._profileBuffs != null && player._profileBuffs.get(profile) != null) {

                for(int var4 = 0; var4 < 31; ++var4) {
                    int var5 = buffList[var4];
                    if (((List)player._profileBuffs.get(profile)).contains(var5)) {
                        ++var2;
                    }
                }

                return var2;
            } else {
                if (Config.DEBUG) {
                    log.warn("PLAYER PROFILE BUFFS IS NULL!! REPORT TO REUNION TEAM!");
                }

                player.sendMessage("Please restart your char something was wrong.");
                return 0;
            }
        } catch (Exception var6) {
            if (Config.DEBUG) {
                log.warn("PLAYER PROFILE BUFFS IS NULL!! REPORT TO REUNION TEAM!");
            }

            player.sendMessage("Please restart your char something was wrong.");
            return var2;
        }
    }

    private static int checkProfile(String profile, L2PcInstance player) {
        byte var2 = 0;

        try {
            if (profile == null) {
                if (Config.DEBUG) {
                    log.warn("PROFILE IS NULL!! REPORT TO REUNION TEAM!");
                }

                player.sendMessage("Please restart your char something was wrong.");
                return 0;
            } else if (player._profileBuffs != null && player._profileBuffs.get(profile) != null) {
                List var7;
                int sizeList = (var7 = (List)player._profileBuffs.get(profile)).size();

                for(int var4 = 0; var4 < 31; ++var4) {
                    int var5 = buffList[var4];
                    if (var7.contains(var5)) {
                        --sizeList;
                    }
                }

                return sizeList;
            } else {
                if (Config.DEBUG) {
                    log.warn("PLAYER PROFILE BUFFS IS NULL!! REPORT TO REUNION TEAM!");
                }

                player.sendMessage("Please restart your char something was wrong.");
                return 0;
            }
        } catch (Exception e) {
            if (Config.DEBUG) {
                log.warn("PLAYER PROFILE BUFFS IS NULL!! REPORT TO REUNION TEAM!");
            }

            player.sendMessage("Please restart your char something was wrong.");
            return var2;
        }
    }

    public static void addDelay(L2PcInstance player) {
        if (BufferConfigs.BUFFER_ENABLE_DELAY) {
            ThreadPoolManager.getInstance().executeGeneral(new BuffDelay(player));
        }

    }

    public static boolean checkDelay(L2PcInstance player) {
        if (BufferConfigs.BUFFER_ENABLE_DELAY && BuffDelay._delayers.contains(player)) {
            if (BufferConfigs.BUFFER_DELAY_SENDMESSAGE) {
                player.sendMessage("In order to use buffer functions again, you will have to wait " + BufferConfigs.BUFFER_DELAY + "!");
            }

            return false;
        } else {
            return true;
        }
    }

    public static boolean saveProfile(L2PcInstance player, String profile) {
        if (!Util.isAlphaNumeric(profile.substring(1))) {
            player.sendMessage("Profile name must be alpha-numeric.");
            return false;
        } else {
            String var2 = profile.substring(1);
            if (!Pattern.compile("[A-Za-z0-9]*").matcher(var2).matches()) {
                player.sendMessage("Profile name must be alpha-numeric.");
                return false;
            } else if (hasProfile(profile, player)) {
                player.sendMessage("Your profile is already in use.");
                return false;
            } else {
                if (player.isPremium()) {
                    if (getProfileSize(profile, player) >= PremiumServiceConfigs.PREMIUM_MAX_SCHEME) {
                        player.sendMessage("Cannot create more profiles.");
                        return false;
                    }
                } else if (getProfileSize(profile, player) >= BufferConfigs.MAX_SCHEME_PROFILES) {
                    player.sendMessage("Cannot create more profiles.");
                    return false;
                }

                return true;
            }
        }
    }

    public static void saveBuff(L2PcInstance player, String profike, int buffId) {

        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO aio_scheme_profiles_buffs VALUES (?, ?, ?)")){
            ps.setInt(1, player.getObjectId());
            ps.setString(2, profike);
            ps.setInt(3, buffId);
            ps.execute();
        } catch (SQLException var29) {
            player.sendMessage("Something went wrong check your profile name, if problem still exists please rr your char.");
        }

    }

    public static void deleteProfile(L2PcInstance player, String profile) {
        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM aio_scheme_profiles_buffs WHERE charId = ? AND profile = ?")){

            ps.setInt(1, player.getObjectId());
            ps.setString(2, profile);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteBuff(L2PcInstance player, String profile, int buffId) {
        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM aio_scheme_profiles_buffs WHERE charId = ? AND profile = ? AND buff_id = ?")){
            ps.setInt(1, player.getObjectId());
            ps.setString(2, profile);
            ps.setInt(3, buffId);
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean checkPriceConsume(L2PcInstance player, int price) {
        boolean freeBuffs = true;
        if (BufferConfigs.FREE_BUFFS_TILL_LEVEL > 0 && player.getLevel() <= BufferConfigs.FREE_BUFFS_TILL_LEVEL) {
            freeBuffs = false;
        }

        if (freeBuffs) {
            if (!Conditions.checkPlayerItemCount(player, BufferConfigs.BUFF_ITEM_ID, price * BufferConfigs.PRICE_PERBUFF)) {
                return false;
            }

            player.destroyItemByItemId("Scheme system", BufferConfigs.BUFF_ITEM_ID, (long)(price * BufferConfigs.PRICE_PERBUFF), player, true);
        }

        return true;
    }

    public static boolean hasProfile(String profile, L2PcInstance Player) {
        return Player._profileBuffs.containsKey(profile);
    }

    public static List<Integer> getProfileBuffs(String profile, L2PcInstance player) {
        return (List)player._profileBuffs.get(profile);
    }
}
