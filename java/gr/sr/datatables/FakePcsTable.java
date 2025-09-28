package gr.sr.datatables;


import l2r.L2DatabaseFactory;
import l2r.gameserver.model.actor.FakePc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakePcsTable {
    private static Logger log = LoggerFactory.getLogger(FakePcsTable.class);
    private final Map<Integer, FakePc> fakePcs = new ConcurrentHashMap();

    protected FakePcsTable() {
        this.init();
    }

    private void init() {
        this.fakePcs.clear();

        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM `fake_pcs`")){
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    FakePc fakePc = new FakePc();
                    int npcId = rs.getInt("npc_id");
                    fakePc.race = rs.getInt("race");
                    fakePc.sex = rs.getInt("sex");
                    fakePc.clazz = rs.getInt("class");
                    fakePc.title = rs.getString("title");
                    fakePc.titleColor = Integer.decode("0x" + rs.getString("title_color"));
                    fakePc.name = rs.getString("name");
                    fakePc.nameColor = Integer.decode("0x" + rs.getString("name_color"));
                    fakePc.hairStyle = rs.getInt("hair_style");
                    fakePc.hairColor = rs.getInt("hair_color");
                    fakePc.face = rs.getInt("face");
                    fakePc.mount = rs.getByte("mount");
                    fakePc.team = rs.getByte("team");
                    fakePc.hero = rs.getByte("hero");
                    fakePc.pdUnder = rs.getInt("pd_under");
                    fakePc.pdUnderAug = rs.getInt("pd_under_aug");
                    fakePc.pdHead = rs.getInt("pd_head");
                    fakePc.pdHeadAug = rs.getInt("pd_head_aug");
                    fakePc.pdRHand = rs.getInt("pd_rhand");
                    fakePc.pdRHandAug = rs.getInt("pd_rhand_aug");
                    fakePc.pdLHand = rs.getInt("pd_lhand");
                    fakePc.pdLHandAug = rs.getInt("pd_lhand_aug");
                    fakePc.pdGloves = rs.getInt("pd_gloves");
                    fakePc.pdGlovesAug = rs.getInt("pd_gloves_aug");
                    fakePc.pdChest = rs.getInt("pd_chest");
                    fakePc.pdChestAug = rs.getInt("pd_chest_aug");
                    fakePc.pdLegs = rs.getInt("pd_legs");
                    fakePc.pdLegsAug = rs.getInt("pd_legs_aug");
                    fakePc.pdFeet = rs.getInt("pd_feet");
                    fakePc.pdFeetAug = rs.getInt("pd_feet_aug");
                    fakePc.pdBack = rs.getInt("pd_back");
                    fakePc.pdBackAug = rs.getInt("pd_back_aug");
                    fakePc.pdLRHand = rs.getInt("pd_lrhand");
                    fakePc.pdLRHandAug = rs.getInt("pd_lrhand_aug");
                    fakePc.pdHair = rs.getInt("pd_hair");
                    fakePc.pdHairAug = rs.getInt("pd_hair_aug");
                    fakePc.pdHair2 = rs.getInt("pd_hair2");
                    fakePc.pdHair2Aug = rs.getInt("pd_hair2_aug");
                    fakePc.pdRBracelet = rs.getInt("pd_rbracelet");
                    fakePc.pdRBraceletAug = rs.getInt("pd_rbracelet_aug");
                    fakePc.pdLBracelet = rs.getInt("pd_lbracelet");
                    fakePc.pdLBraceletAug = rs.getInt("pd_lbracelet_aug");
                    fakePc.pdDeco1 = rs.getInt("pd_deco1");
                    fakePc.pdDeco1Aug = rs.getInt("pd_deco1_aug");
                    fakePc.pdDeco2 = rs.getInt("pd_deco2");
                    fakePc.pdDeco2Aug = rs.getInt("pd_deco2_aug");
                    fakePc.pdDeco3 = rs.getInt("pd_deco3");
                    fakePc.pdDeco3Aug = rs.getInt("pd_deco3_aug");
                    fakePc.pdDeco4 = rs.getInt("pd_deco4");
                    fakePc.pdDeco4Aug = rs.getInt("pd_deco4_aug");
                    fakePc.pdDeco5 = rs.getInt("pd_deco5");
                    fakePc.pdDeco5Aug = rs.getInt("pd_deco5_aug");
                    fakePc.pdDeco6 = rs.getInt("pd_deco6");
                    fakePc.pdDeco6Aug = rs.getInt("pd_deco6_aug");
                    fakePc.enchantEffect = rs.getInt("enchant_effect");
                    fakePc.pvpFlag = rs.getInt("pvp_flag");
                    fakePc.karma = rs.getInt("karma");
                    fakePc.fishing = rs.getByte("fishing");
                    fakePc.fishingX = rs.getInt("fishing_x");
                    fakePc.fishingY = rs.getInt("fishing_y");
                    fakePc.fishingZ = rs.getInt("fishing_z");
                    fakePc.invisible = rs.getByte("invisible");
                    this.fakePcs.put(npcId, fakePc);
                }
            }
        } catch (SQLException e) {
            log.error("Error while creating fake pc table: " + e.getMessage(), e);
        }

    }

    public void reloadData() {
        this.init();
    }

    public FakePc getFakePc(int index) {
        return (FakePc)this.fakePcs.get(index);
    }

    protected static FakePcsTable instance;

    public static FakePcsTable getInstance() {
        if (instance == null)
            instance = new FakePcsTable();
        return instance;
    }
}

