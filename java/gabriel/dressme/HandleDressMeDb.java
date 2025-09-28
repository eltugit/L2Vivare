package gabriel.dressme;


import gabriel.dressmeEngine.data.DressMeAgathionData;
import gr.sr.utils.db.DbUtils;
import l2r.L2DatabaseFactory;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HandleDressMeDb {
    protected static final Logger _log = Logger.getLogger(HandleDressMeDb.class.getName());


    private static Map<Integer, List<Integer>> loadDress(L2PcInstance player, String from, String label) {
        LinkedHashMap<Integer, List<Integer>> playerDress = new LinkedHashMap<>();
        List<Integer> list2 = new ArrayList<>();
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;
            statement = con.prepareStatement("SELECT * from " + from + " WHERE charId =?");
            statement.setInt(1, player.getObjectId());
            playerDress.put(player.getObjectId(), list2);

            ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                playerDress.get(rset.getInt("charId")).add(rset.getInt(label));

            }
            rset.close();
            statement.close();
        } catch (SQLException e) {
            _log.warning("Gabson: Couldn't select DressMe from: " + from + " to database:" + e.getMessage());
        } finally {
            DbUtils.close(con);
        }
        return playerDress;
    }


    private static boolean insertDress(L2PcInstance player, String from, gabriel.dressmeEngine.data.DressMeData dress) {
        boolean ok = false;
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;

            statement = con.prepareStatement("INSERT INTO " + from + " VALUES (?,?)");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, dress.getId());
            statement.executeUpdate();
            statement.close();
            ok = true;
        } catch (SQLException e) {
            _log.log(Level.WARNING, "Gabson: Couldn't update DressMe " + from + " to database:" + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }
        return ok;
    }


    public static <V> Map<Integer, V> order(L2PcInstance player, Map<Integer, V> map, String from, String label) {
        Map<Integer, List<Integer>> playerDress = loadDress(player, from, label);
        Map<Integer, V> sorted = new LinkedHashMap<>();

        for (Map.Entry<Integer, V> entrySet : map.entrySet()) {
            if (isInside(player, playerDress, ((gabriel.dressmeEngine.data.DressMeData) entrySet.getValue()).getId())) {
                sorted.put(entrySet.getKey(), entrySet.getValue());
            }
        }
        for (Map.Entry<Integer, V> entrySet : map.entrySet()) {
            if (!isInside(player, playerDress, ((gabriel.dressmeEngine.data.DressMeData) entrySet.getValue()).getId())) {
                sorted.put(entrySet.getKey(), entrySet.getValue());
            }
        }
        return sorted;
    }


    public static <V> List<V> orderL(L2PcInstance player, Map<Integer, V> allDresses, String from, String label) {
        Map<Integer, List<Integer>> playerDress = loadDress(player, from, label);
        List<V> sorted = new LinkedList<>();
        for (Map.Entry<Integer, V> entrySet : allDresses.entrySet()) {
            if (isInside(player, playerDress, ((gabriel.dressmeEngine.data.DressMeData) entrySet.getValue()).getId())) {
                sorted.add(entrySet.getValue());
            }
        }
        for (Map.Entry<Integer, V> entrySet : allDresses.entrySet()) {
            if (!isInside(player, playerDress, ((gabriel.dressmeEngine.data.DressMeData) entrySet.getValue()).getId())) {
                sorted.add(entrySet.getValue());
            }
        }
        return sorted;
    }


    private static boolean isInside(L2PcInstance player, Map<Integer, List<Integer>> playerDress, int dressId) {
        if (playerDress.get(player.getObjectId()).size() != 0) {
            for (int ii = 0; ii < playerDress.get(player.getObjectId()).size(); ii++) {
                if (playerDress.get(player.getObjectId()).get(ii) == dressId) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean dressMeArmorInside(L2PcInstance player, gabriel.dressmeEngine.data.DressMeArmorData dress) {
        return isInside(player, loadDress(player, "character_dressme_armor_list", "dressId"), dress.getId());
    }

    public static boolean dressMeCloakInside(L2PcInstance player, gabriel.dressmeEngine.data.DressMeCloakData cloak) {
        return isInside(player, loadDress(player, "character_dressme_cloak_list", "cloakDressId"), cloak.getId());
    }

    public static boolean dressMeShieldInside(L2PcInstance player, gabriel.dressmeEngine.data.DressMeShieldData shield) {
        return isInside(player, loadDress(player, "character_dressme_shield_list", "shieldDressId"), shield.getId());
    }

    public static boolean dressMeWeaponInside(L2PcInstance player, gabriel.dressmeEngine.data.DressMeWeaponData weapon) {
        return isInside(player, loadDress(player, "character_dressme_weapon_list", "weaponDressId"), weapon.getId());
    }

    public static boolean dressMeHatInside(L2PcInstance player, gabriel.dressmeEngine.data.DressMeHatData hat) {
        return isInside(player, loadDress(player, "character_dressme_hat_list", "hatDressId"), hat.getId());
    }

    public static boolean dressMeEnchantInside(L2PcInstance player, gabriel.dressmeEngine.data.DressMeEnchantData enchant) {
        return isInside(player, loadDress(player, "character_dressme_enchant_list", "enchantId"), enchant.getId());
    }

    public static boolean dressMeAgathionInside(L2PcInstance player, gabriel.dressmeEngine.data.DressMeAgathionData enchant) {
        return isInside(player, loadDress(player, "character_dressme_agathion_list", "agathionId"), enchant.getId());
    }

    public static boolean insertDressMeArmor(L2PcInstance player, gabriel.dressmeEngine.data.DressMeArmorData dress) {
        return insertDress(player, "character_dressme_armor_list", dress);
    }

    public static boolean insertDressMeCloak(L2PcInstance player, gabriel.dressmeEngine.data.DressMeCloakData cloak_data) {
        return insertDress(player, "character_dressme_cloak_list", cloak_data);
    }

    public static boolean insertDressMeShield(L2PcInstance player, gabriel.dressmeEngine.data.DressMeShieldData shield_data) {
        return insertDress(player, "character_dressme_shield_list", shield_data);
    }

    public static boolean insertDressMeWeapon(L2PcInstance player, gabriel.dressmeEngine.data.DressMeWeaponData weapon_data) {
        return insertDress(player, "character_dressme_weapon_list", weapon_data);
    }

    public static boolean insertDressMeHat(L2PcInstance player, gabriel.dressmeEngine.data.DressMeHatData hat_data) {
        return insertDress(player, "character_dressme_hat_list", hat_data);
    }

    public static boolean insertDressMeAgathion(L2PcInstance player, DressMeAgathionData agathion) {
        return insertDress(player, "character_dressme_agathion_list", agathion);
    }

    public static boolean insertDressMeEnchant(L2PcInstance player, gabriel.dressmeEngine.data.DressMeEnchantData enchant) {
        return insertDress(player, "character_dressme_enchant_list", enchant);
    }

}
