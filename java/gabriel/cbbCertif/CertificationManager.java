package gabriel.cbbCertif;


import gabriel.config.GabConfig;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassLevel;
import l2r.gameserver.model.base.PlayerClass;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.ShowBoard;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Gabriel Costa Souza
 * Discord: gabsoncs
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class CertificationManager {
    private static final String COMMAND = "_certi";
    public Map<Integer, EmergentInfo> _emergent = new HashMap<>();
    public Map<Integer, SkillInfo> _skills = new HashMap<>();
    public Map<Integer, TransformInfo> _transforms = new HashMap<>();
    public Map<String, List<SkillInfo>> _skill_table = new HashMap<>();

    private CertificationManager() {
        addEmergent(631, "Attack", "icon.skill0141", new String[]{"Lv 0 P.Atk.", "Lv 1 P.Atk.", "Lv 2 P.Atk.", "Lv 3 P.Atk.", "Lv 4 P.Atk.", "Lv 5 P.Atk.", "Lv 6 P.Atk."});
        addEmergent(632, "Def", "icon.skill0142", new String[]{"Lv 0 P.Def.", "Lv 1 P.Def.", "Lv 2 P.Def.", "Lv 3 P.Def.", "Lv 4 P.Def.", "Lv 5 P.Def.", "Lv 6 P.Def."});
        addEmergent(633, "Empower", "icon.skill0141", new String[]{"Lv 0 M.Atk.", "Lv 1 M.Atk.", "Lv 2 M.Atk.", "Lv 3 M.Atk.", "Lv 4 M.Atk.", "Lv 5 M.Atk.", "Lv 6 M.Atk."});
        addEmergent(634, "M.Def", "icon.skill0146", new String[]{"Lv 0 M.Def.", "Lv 1 M.Def.", "Lv 2 M.Def.", "Lv 3 M.Def.", "Lv 4 M.Def.", "Lv 5 M.Def.", "Lv 6 M.Def."});
        // Master Ability skills
        addSkill(637, "icon.skill1068", "Master Abillity - Attack", "Lv 1 Required SP: 0", "Increases P Atk.");
        addSkill(638, "icon.skill1059", "Master Abillity - Empower", "Lv 1 Required SP: 0", "Increases M Atk.");
        addSkill(639, "icon.skill1085", "Master Abillity - Casting", "Lv 1 Required SP: 0", "Increases Casting Spd.");
        addSkill(640, "icon.skill1077", "Master Abillity - Focus", "Lv 1 Required SP: 0", "Increases Critical Rate.");
        addSkill(799, "icon.skill0142", "Master Abillity - Defense", "Lv 1 Required SP: 0", "Increases P Def.");
        addSkill(800, "icon.skill0146", "Master Abillity - Magic Defense", "Lv 1 Required SP: 0", "Increases M Def.");
        // Class-Specific Ability skills
        // Warrior Class (Gladiator, Warlord, Bounty Hunter, Tyrant, Destroyer, Soul Breaker, Berserker)
        addSkill(801, "icon.skill0440", "Warrior Ability - Boost CP", "Lv 1 Required SP: 0", "Increases Max CP.");
        addSkill(650, "icon.skill0287", "Warrior Ability - Resist Trait", "Lv 1 Required SP: 0", "increases the tolerance to paralysis/hold/sleep/shock/buff cancel attack.");
        addSkill(651, "icon.skill1086", "Warrior Ability - Haste", "Lv 1 Required SP: 0", "Has a chance to increases Atk Spd. while attacking.");
        // Rogue Class (Hawkeye, Silver Ranger, Phantom Ranger, Treasure Hunter, Plainswalker, Abyss Walker, Arbalester)
        addSkill(644, "icon.skill1087", "Roque Ability - Evasion", "Lv 1 Required SP: 0", "Increases Evasion.");
        addSkill(645, "icon.skill0113", "Roque Ability - Long Shot", "Lv 1 Required SP: 0", "Increases bow range.");
        addSkill(653, "icon.skill1077", "Roque Ability - Critical Chance", "Lv 1 Required SP: 0", "Has a chance to increase Critical rate while attacking.");
        // Knight Class (Paladin, Dark Avenger, Temple Knight, Shillien Knight)
        addSkill(641, "icon.skill0211", "Knight Ability - Boost HP", "Lv 1 Required SP: 0", "Increases Max HP.");
        addSkill(804, "icon.skill0232", "Knight Ability - Resist Critical", "Lv 1 Required SP: 0", "Received critical damage is decreased.");
        addSkill(652, "icon.skill0110", "Knight Abilitiy - Defense", "Lv 1 Required SP: 0", "Has a chance to increase P Def/M Def while being attacked.");
        // Summoner Class (Warlock, Elemental Summoner, Phantom Summoner)
        addSkill(643, "icon.skill0211", "Summoner Abilitiy - Boost HP/MP", "Lv 1 Required SP: 0", "Increases Max HP and Max MP.");
        addSkill(1489, "icon.skill0433", "Summoner Abilitiy - Resist Attribute", "Lv 1 Required SP: 0", "Increases the tolerance to elemental attacks.");
        addSkill(1491, "icon.skill1349", "Summoner Abilitiy - Spirit", "Lv 1 Required SP: 0", "Has a chance to channel the spirit when being attacked.");
        // Wizard Class (Sorcerer, Spellsinger, Spellhowler, Necromancer)
        addSkill(802, "icon.skill0146", "Wizard Abilitiy - Anti Magic", "Lv 1 Required SP: 0", "The probability of resisting damage magic.");
        addSkill(646, "icon.skill0285", "Wizard Abilitiy - Mana Gain", "Lv 1 Required SP: 0", "Increases the recovery rate of MP.");
        addSkill(654, "icon.skill1398", "Wizard Abilitiy - Mana Steal", "Lv 1 Required SP: 0", "Has a chance to recover MP while attacking.");
        // Healer Class (Shillien Elder, Elven Elder, Bishop)
        addSkill(648, "icon.skill1307", "Healer Abilitiy - Prayer", "Lv 1 Required SP: 0", "Receiving HP recovery magic.");
        addSkill(803, "icon.skill1353", "Healer Abilitiy - Divine Protection", "Lv 1 Required SP: 0", "Resistance to darkness and divinity.");
        addSkill(1490, "icon.skill1011", "Healer Abilitiy - Heal", "Lv 1 Required SP: 0", "Has a chance to recover HP while being attacked.");
        // Enchanter Class (Prophet, Warcryer, Inspector, Swordsinger, Bladedancer)
        addSkill(642, "icon.skill0213", "Enchanter Abilitiy - Boost Mana", "Lv 1 Required SP: 0", "Increases Max MP.");
        addSkill(647, "icon.skill0229", "Enchanter Abilitiy - Mana Recovery", "Lv 1 Required SP: 0", "Increases MP regeneration.");
        addSkill(655, "icon.skill1418", "Enchanter Abilitiy - Barrier", "Lv 1 Required SP: 0", "Has a chance to invincibility while being attacked.");
        // master
        addTable("Master", new Integer[]
                {
                        637,
                        638,
                        639,
                        640,
                        799,
                        800
                });
        addTable("Warrior", new Integer[]
                {
                        801,
                        650,
                        651,
                });
        addTable("Rogue", new Integer[]
                {
                        644,
                        645,
                        653,
                });
        addTable("Knight", new Integer[]
                {
                        641,
                        804,
                        652,
                });
        addTable("Summoner", new Integer[]
                {
                        643,
                        1489,
                        1491,
                });
        addTable("Wizard", new Integer[]
                {
                        802,
                        646,
                        654,
                });
        addTable("Healer", new Integer[]
                {
                        648,
                        803,
                        1490,
                });
        addTable("Enchanter", new Integer[]
                {
                        642,
                        647,
                        655,
                });
        addTransform(656, "icon.skilltransform1", "Transform Divine Warrior", "", "");
        addTransform(657, "icon.skilltransform1", "Transform Divine Knight", "", "");
        addTransform(658, "icon.skilltransform1", "Transform Divine Rogue", "", "");
        addTransform(659, "icon.skilltransform1", "Transform Divine Wizard", "", "");
        addTransform(660, "icon.skilltransform1", "Transform Divine Summoner", "", "");
        addTransform(661, "icon.skilltransform1", "Transform Divine Healer", "", "");
        addTransform(662, "icon.skilltransform1", "Transform Divine Enchanter", "", "");
    }


    public void parseCommand(String command, L2PcInstance player) {
        if (!GabConfig.ALLOW_COMMUNITY_CERTIFIC) {
            player.sendMessage("You cant use this service!");
            return;
        }

        if (player.isInOlympiadMode()) {
            player.sendMessage("You cannot change certification in olympiad");
            return;
        }

        if (player.getInstanceId() != 0) {
            player.sendMessage("You cannot change certification in an instance");
            return;
        }
        if (player.isInCombat()) {
            player.sendMessage("You cannot change certification in combat");
            return;
        }
        if (player.isInTournament()) {
            player.sendMessage("You cannot change certification in tournament");
            return;
        }

        if (command.equals(COMMAND)) {
            String html = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/gabriel/indexTemplateNoBack.htm");
            String template = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/gabriel/template/sideTemplate.htm");
            String actualText = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/gabriel/extraTemplate/certiMain.htm");

            CertiData data = player.getCertiData();
            html = html.replace("template", template);
            html = html.replace("CHANGE", actualText);
            html = html.replace("%emergents%", "" + getEmergents(data));
            html = html.replace("%skills%", "" + getSkills(data));
            html = html.replace("%transforms%", "" + getTransforms(data));
            separateAndSend(html, player);
        } else if (command.startsWith(COMMAND)) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken();
            if (!st.hasMoreTokens()) {
                return;
            }
            String action = st.nextToken();
            CertiData data = player.getCertiData();
            if (action.equals("inc")) {
                if (st.hasMoreTokens()) {
                    int val = Integer.parseInt(st.nextToken());
                    data.updateEmergentLvL(val, true);
                }
            } else if (action.equals("dec")) {
                if (st.hasMoreTokens()) {
                    int val = Integer.parseInt(st.nextToken());
                    data.updateEmergentLvL(val, false);
                }
            } else if (action.contentEquals("learnS")) {
                if (st.hasMoreTokens()) {
                    int val = Integer.parseInt(st.nextToken());
                    if(!canLearnSkill(player,val)){
                        player.sendMessage("You cannot learn this skill...");
                        player.sendPacket(new NpcHtmlMessage("You cannot learn this skill"));
                        return;
                    }
                    if (_skills.containsKey(val)) {
                        data.AddSkill(val);
                    }
                }
                parseCommand(COMMAND, player);
                final NpcHtmlMessage html = new NpcHtmlMessage(0);
                String str = "";
                str += "<html>";
                str += "<title>Learn Skill</title>";
                str += getLearnSkills(player, data);
                str += "</html>";
                html.setHtml(str);
                player.sendPacket(html);
                return;
            } else if (action.contentEquals("resetS")) {
                if (data.canResetSkill()) {
                    data.resetSkills();
                    parseCommand(COMMAND, player);
                    final NpcHtmlMessage html = new NpcHtmlMessage(0);
                    String str = "";
                    str += "<html>";
                    str += "<title>Learn Skill</title>";
                    str += getLearnSkills(player, data);
                    str += "</html>";
                    html.setHtml(str);
                    player.sendPacket(html);
                    return;
                }
            } else if (action.contentEquals("learnT")) {
                if (st.hasMoreTokens()) {
                    int val = Integer.parseInt(st.nextToken());
                    if(!canLearnTrans(player,val)){
                        player.sendMessage("You cannot learn this skill...");
                        player.sendPacket(new NpcHtmlMessage("You cannot learn this skill"));
                        return;
                    }
                    if (_transforms.containsKey(val)) {
                        data.AddTransform(val);
                    }
                }
                parseCommand(COMMAND, player);
                final NpcHtmlMessage html = new NpcHtmlMessage(0);
                String str = "";
                str += "<html>";
                str += "<title>Learn Transforms</title>";
                str += getLearnTransforms(player, data);
                str += "</html>";
                html.setHtml(str);
                player.sendPacket(html);
                return;
            } else if (action.contentEquals("resetT")) {
                if (data.canResetTransforms()) {
                    data.resetTransforms();
                    parseCommand(COMMAND, player);
                    final NpcHtmlMessage html = new NpcHtmlMessage(0);
                    String str = "";
                    str += "<html>";
                    str += "<title>Learn Transforms</title>";
                    str += getLearnTransforms(player, data);
                    str += "</html>";
                    html.setHtml(str);
                    player.sendPacket(html);
                    return;
                }
            }
            parseCommand(COMMAND, player);
        }
    }

    private void addEmergent(int id, String name, String icon, String[] rates) {
        _emergent.put(id, new EmergentInfo(id, name, icon, rates));
    }

    private void addSkill(int id, String icon, String name, String desk1, String desk2) {
        _skills.put(id, new SkillInfo(id, icon, name, desk1, desk2));
    }

    private void addTransform(int id, String icon, String name, String desk1, String desk2) {
        _transforms.put(id, new TransformInfo(id, icon, name, desk1, desk2));
    }

    private void addTable(String name, Integer[] ids) {
        _skill_table.put(name, new ArrayList<>());
        for (int id : ids) {
            _skill_table.get(name).add(_skills.get(id));
        }
    }

    private String getEmergents(CertiData data) {
        String html = "";
        int num = 0;
        for (EmergentInfo skill : _emergent.values()) {
            final int level = data.getEmergents()[num];
            html += "<td>";
            html += "<table width=100>";
            html += "<tr>";
            html += "<td height=23 align=center>";
            if (level > 0) {
                html += "<font name=hs12 color=LEVEL>" + skill.getName() + "</font>";
            } else {
                html += "" + skill.getName() + "";
            }
            html += "</td>";
            html += "</tr>";
            html += "<tr>";
            html += "<td height=35 align=center>";
            html += "<img src=" + skill.getIcon() + " width=32 height=32>";
            html += "</td>";
            html += "</tr>";
            html += "<tr>";
            html += "<td height=30>";
            html += "<table width=100>";
            html += "<tr>";
            html += "<td width=60 align=right height=26>";
            if (level != 0) {
                html += "<button action=\"bypass " + COMMAND + " dec " + num + "\" back=L2UI_CT1.RadarMap_DF_MinusBtn_down fore=L2UI_CT1.RadarMap_DF_MinusBtn width=20 height=20 />";
            }
            html += "</td>";
            html += "<td width=20 valign=center align=center>";
            if (level > 0) {
                html += "<font name=hs4 color=LEVEL>" + level + "</font>";
            } else {
                html += "" + level + "";
            }
            html += "</td>";
            html += "<td width=70 align=left>";
            if (level != 6) {
                if (data.canLearnEmergent()) {
                    html += "<button action=\"bypass " + COMMAND + " inc " + num + "\" back=L2UI_CT1.RadarMap_DF_PlusBtn_down fore=L2UI_CT1.RadarMap_DF_PlusBtn width=20 height=20 />";
                }
            }
            html += "</td>";
            html += "</tr>";
            html += "</table>";
            html += "</td>";
            html += "</tr>";
            html += "<tr>";
            html += "<td height=20 align=center>";
            if (level > 0) {
                html += "<font name=hs4 color=LEVEL>" + skill.getRates(level) + "</font>";
            } else {
                html += "" + skill.getRates(level) + "";
            }
            html += "</td>";
            html += "</tr>";
            html += "</table>";
            html += "</td>";
            num++;
        }
        return html;
    }

    private String getSkills(CertiData data) {
        String html = "";
        for (int id : data.getSkills()) {
            SkillInfo skill = _skills.get(id);
            html += "<td>";
            html += "<table width=100 height=60>";
            html += "<tr>";
            html += "<td height=40 width=100 align=center valign=top>";
            html += "<img src=" + (skill == null ? "icon.skill0000" : skill.getIcon()) + " width=32 height=32>";
            html += "</td>";
            html += "</tr>";
            html += "<tr>";
            html += "<td height=20 align=center valign=top>";
            if (skill == null) {
                html += "" + "Add" + "";
            } else {
                html += "<font name=hs1 color=LEVEL>" + skill.getName() + "</font>";
            }
            html += "</td>";
            html += "</tr>";
            html += "</table>";
            html += "</td>";
        }
        return html;
    }

    private String getTransforms(CertiData data) {
        String html = "";
        for (int id : data.getTransforms()) {
            TransformInfo skill = _transforms.get(id);
            html += "<td>";
            html += "<table width=100 height=60>";
            html += "<tr>";
            html += "<td height=40 width=100 align=center valign=top>";
            html += "<img src=" + (skill == null ? "icon.skill0000" : skill.getIcon()) + " width=32 height=32>";
            html += "</td>";
            html += "</tr>";
            html += "<tr>";
            html += "<td height=20 align=center valign=top>";
            if (skill == null) {
                html += "" + "Add" + "";
            } else {
                html += "<font name=hs1 color=LEVEL>" + skill.getName().split("Transform Divine ")[1] + "</font>";
            }
            html += "</td>";
            html += "</tr>";
            html += "</table>";
            html += "</td>";
        }
        return html;
    }

    private boolean canLearnSkill(L2PcInstance player, int skillId){
        CertiData data = player.getCertiData();
        if (data.canLearnSkill()) {
            List<SkillInfo> learnableSkills = new LinkedList<>();
            for (Entry<String, List<SkillInfo>> info : _skill_table.entrySet()) {
                if (!doChecksForClass(info, player, data)) {
                    continue;
                }
                for (SkillInfo skill : info.getValue()) {
                    if (data.containsSkill(skill.getId())) {
                        continue;
                    }

                    learnableSkills.add(skill);
                }
            }
            return learnableSkills.stream().anyMatch(e->e.getId() == skillId);
        }

        return false;
    }

    private boolean canLearnTrans(L2PcInstance player, int transformId){
        CertiData data = player.getCertiData();
        if (data.canLearnTransform()) {
            List<TransformInfo> learnableTransforms = new LinkedList<>();
            for (TransformInfo info : _transforms.values()) {
                if(!doChecksForTransform(info, player, data))
                    continue;
                learnableTransforms.add(info);
            }
            return learnableTransforms.stream().anyMatch(e->e.getId() == transformId);
        }

        return false;
    }

    private boolean doChecksForTransform(TransformInfo info, L2PcInstance player, CertiData data){
        if (info.getId() == 657) {
            switch (player.getClassId().getId()) {
                case 90: // Phoenix Knight
                case 91: // Hell Knight
                case 99: // Evas Templar
                case 106: // Shillien Templar
                    //
                case 134:// Trickster
                case 116:// Doomcryer
                case 136:// Judicator
                case 131:// Doombriger
                case 132:// Male Soulhound
                case 133:// Female Soulhould
                {
                    return false;
                }
            }
        }
        if (info.getId() == 660) {
            switch (player.getClassId().getId()) {
                case 134:// Trickster
                case 116:// Doomcryer
                case 136:// Judicator
                case 131:// Doombriger
                case 132:// Male Soulhound
                case 133:// Female Soulhould
                {
                    return false;
                }
            }
        }
        if ((info.getId() == 659) || (info.getId() == 661)) {
            switch (player.getClassId().getId()) {
                case 134:// Trickster
                case 136:// Judicator
                case 131:// Doombriger
                case 132:// Male Soulhound
                case 133:// Female Soulhould
                {
                    return false;
                }
            }
        }

        if (info.getId() == 658) // rogue
        {
            switch (player.getClassId().getId()) {
                case 134:// Trickster
                    return false;
                case 136:// Judicator
                case 131:// Doombriger warr
                case 132:// Male Soulhound warr
                case 133:// Female Soulhould warr
                    if (data.containsTransformAmount(info.getId()) == 1) {
                        return false;
                    }
            }
        }
        if (info.getId() == 662) // enchanter
        {
            switch (player.getClassId().getId()) {
                case 136:// Judicator enchanter
                    return false;
                case 134:// Trickster rogue
                case 131:// Doombriger warr
                case 132:// Male Soulhound warr
                case 133:// Female Soulhould warr
                    if (data.containsTransformAmount(info.getId()) == 1) {
                        return false;
                    }
            }
        }
        if (info.getId() == 656)// warrior
        {
            switch (player.getClassId().getId()) {
                case 134:// Trickster rogue
                case 136:// Judicator enchanter
                case 131:// Doombriger warr
                case 132:// Male Soulhound warr
                case 133:// Female Soulhould warr
                    if (data.containsTransformAmount(info.getId()) == 2) {
                        return false;
                    }
            }
        }

        return true;
    }


    private boolean doChecksForClass(Entry<String, List<SkillInfo>> info, L2PcInstance player, CertiData data){
        if (info.getKey().equals("Knight")) {
            switch (player.getClassId().getId()) {
                case 90: // Phoenix Knight
                case 91: // Hell Knight
                case 99: // Evas Templar
                case 106: // Shillien Templar
                    //
                case 134:// Trickster
                case 116:// Doomcryer
                case 136:// Judicator
                case 131:// Doombriger
                case 132:// Male Soulhound
                case 133:// Female Soulhould
                {
                    return false;
                }
            }
        }
        if (info.getKey().equals("Summoner")) {
            switch (player.getClassId().getId()) {
                case 134:// Trickster
                case 116:// Doomcryer
                case 136:// Judicator
                case 131:// Doombriger
                case 132:// Male Soulhound
                case 133:// Female Soulhould
                {
                    return false;
                }
            }
        }
        if (info.getKey().equals("Wizard") || info.getKey().equals("Healer")) {
            switch (player.getClassId().getId()) {
                case 134:// Trickster
                case 136:// Judicator
                case 131:// Doombriger
                case 132:// Male Soulhound
                case 133:// Female Soulhould
                {
                    return false;
                }
            }
        }
        if (info.getKey().equals("Rogue")) {
            switch (player.getClassId().getId()) {
                case 134:// Trickster
                    return false;
                case 136:// Judicator
                case 131:// Doombriger warr
                case 132:// Male Soulhound warr
                case 133:// Female Soulhould warr
                    if (data.containsSkillAmount(info.getValue()) == 1) {
                        return false;
                    }
            }
        }
        if (info.getKey().equals("Enchanter")) {
            switch (player.getClassId().getId()) {
                case 136:// Judicator enchanter
                    return false;
                case 134:// Trickster rogue
                case 131:// Doombriger warr
                case 132:// Male Soulhound warr
                case 133:// Female Soulhould warr
                    if (data.containsSkillAmount(info.getValue()) == 1) {
                        return false;
                    }
            }
        }
        if (info.getKey().equals("Warrior")) {
            switch (player.getClassId().getId()) {
                case 134:// Trickster rogue
                case 136:// Judicator enchanter
                case 131:// Doombriger warr
                case 132:// Male Soulhound warr
                case 133:// Female Soulhould warr
                    if (data.containsSkillAmount(info.getValue()) == 2) {
                        return false;
                    }
            }
        }

        return true;
    }


    private String getLearnSkills(L2PcInstance player, CertiData data) {
        int i = 0;
        String html = "";
        html += "<table width=280 height=30>";
        html += "<tr>";
        html += "<td align=center>";
        boolean clazz = PlayerClass.values()[player.getClassId().getId()].getLevel() == ClassLevel.Fourth;
        if (clazz) {
            if (data.canLearnSkill()) {
                html += "<font name=hs15 color=LEVEL>Click Icon To Learn</font>";
            } else {
                html += "<font name=hs15 color=FF0000>Skill List Full</font>";
            }
        } else {
            html += "<font name=hs15 color=FF0000>Fourth Class Only</font>";
        }
        html += "</td>";
        html += "</tr>";
        html += "</table>";
        if (clazz) {
            if (data.canLearnSkill()) {
                for (Entry<String, List<SkillInfo>> info : _skill_table.entrySet()) {
                    // This will limit only one per class thing
                    /*
                     * if (data.containsSkill(info.getValue())) { continue; }
                     */

                    if (!doChecksForClass(info, player, data)) {
                        continue;
                    }

                    i++;
                    for (SkillInfo skill : info.getValue()) {
                        if (data.containsSkill(skill.getId())) {
                            continue;
                        }

                        html += "<table cellpadding=4 cellspacing=4 bgcolor=" + ((i % 2) == 1 ? "333333" : "555555") + " >"; // background=L2UI_CT1.Windows.Windows_DF_TooltipBG
                        html += "<tr>";
                        html += "<td fixwidth=30>";
                        html += "<button value=\"\" action=\"bypass _certi learnS " + skill.getId() + "\" back=" + skill.getIcon() + " fore=" + skill.getIcon() + " width=32 height=32 />";
                        html += "</td>";
                        html += "<td fixwidth=224>";
                        html += "<font color=LEVEL>" + skill.getName() + "</font>";
                        html += "<br1>";
                        html += "<font color=FFFFFF>" + skill.getDesk1() + "</font>";
                        html += "<br1>";
                        html += "<font color=00FF00>" + skill.getDesk2() + "</font>";
                        html += "</td>";
                        html += "</tr>";
                        html += "</table>";
                    }
                }
            }
        }
        return html;
    }

    private String getLearnTransforms(L2PcInstance player, CertiData data) {
        int i = 0;
        String html = "";
        html += "<table width=280 height=35>";
        html += "<tr>";
        html += "<td align=center>";
        boolean clazz = PlayerClass.values()[player.getClassId().getId()].getLevel() == ClassLevel.Fourth;
        if (clazz) {
            if (data.canLearnTransform()) {
                html += "<font name=hs15 color=LEVEL>Click Icon To Transform</font>";
            } else {
                html += "<font name=hs15 color=FF0000>Transform List Full</font>";
            }
        } else {
            html += "<font name=hs15 color=FF0000>Fourth Class Only</font>";
        }
        html += "</td>";
        html += "</tr>";
        html += "</table>";
        if (clazz) {
            if (data.canLearnTransform()) {
                for (TransformInfo info : _transforms.values()) {

                    if(!doChecksForTransform(info, player, data))
                        continue;

                    i++;
                    html += "<table cellpadding=3 cellspacing=3 bgcolor=" + ((i % 2) == 1 ? "333333" : "555555") + " >"; // background=L2UI_CT1.Windows.Windows_DF_TooltipBG
                    html += "<tr>";
                    html += "<td fixwidth=30>";
                    html += "<button value=\"\" action=\"bypass _certi learnT " + info.getId() + "\" back=" + info.getIcon() + " fore=" + info.getIcon() + " width=32 height=32 />";
                    html += "</td>";
                    html += "<td fixwidth=224>";
                    html += "<font color=LEVEL>" + info.getName() + "</font>";
                    html += "<br1>";
                    html += "<font color=FFFFFF>" + info.getDesk1() + "</font>";
                    html += "<br1>";
                    html += "<font color=00FF00>" + info.getDesk2() + "</font>";
                    html += "</td>";
                    html += "</tr>";
                    html += "</table>";
                }
            }
        }
        return html;
    }

    private void separateAndSend(String html, L2PcInstance acha) {
        if (html == null) {
            return;
        }
        if (html.length() < 8180) {
            acha.sendPacket(new ShowBoard(html, "101"));
            acha.sendPacket(new ShowBoard(null, "102"));
            acha.sendPacket(new ShowBoard(null, "103"));
        } else if (html.length() < (8180 * 2)) {
            acha.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
            acha.sendPacket(new ShowBoard(html.substring(8180, html.length()), "102"));
            acha.sendPacket(new ShowBoard(null, "103"));
        } else if (html.length() < (8180 * 3)) {
            acha.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
            acha.sendPacket(new ShowBoard(html.substring(8180, 8180 * 2), "102"));
            acha.sendPacket(new ShowBoard(html.substring(8180 * 2, html.length()), "103"));
        }
    }


    public static CertificationManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final CertificationManager INSTANCE = new CertificationManager();
    }
}
