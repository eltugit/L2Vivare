package gr.sr.interf;


import gr.sr.events.EventGame;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventBuffer;
import gr.sr.events.engine.EventConfig;
import gr.sr.events.engine.EventManagement;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.events.engine.mini.MiniEventManager;
import gr.sr.events.engine.mini.features.AbstractFeature;
import gr.sr.events.engine.mini.features.EnchantFeature;
import gr.sr.interf.callback.HtmlManager;
import gr.sr.interf.callback.api.DescriptionLoader;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.SkillData;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.skills.L2Skill;


public class SunriseEvents {
    public static final String _desc = "HighFive";
    public static final SunriseLoader.Branch _branch = SunriseLoader.Branch.Hi5;
    public static final double _interfaceVersion = 2.1D;
    public static final boolean _allowInstances = true;
    public static final String _libsFolder = "../libs/";
    public static final boolean _limitedHtml = false;

    
    public static void start() {
        SunriseOut.getInstance().load();
        PlayerBase.getInstance().load();
        Values.getInstance().load();
        SunriseLoader.init(_branch, 2.1D, "HighFive", true, "../libs/", false, true);
    }

    public static void loadHtmlManager() {
        HtmlManager.load();
        DescriptionLoader.load();
    }

    
    public static void serverShutDown() {
    }

    
    public static void onLogin(L2PcInstance player) {
        EventBuffer.getInstance().loadPlayer(player.getEventInfo());
        EventManager.getInstance().onPlayerLogin(player.getEventInfo());
    }

    public static PlayerEventInfo getPlayer(L2PcInstance player) {
        return SunriseLoader.loaded() ? PlayerBase.getInstance().getPlayer(player) : null;
    }

    
    public static boolean isRegistered(L2PcInstance player) {
        PlayerEventInfo pi = getPlayer(player);
        return (pi != null && pi.isRegistered());
    }

    
    public static boolean isInEvent(L2PcInstance player) {
        PlayerEventInfo pi = getPlayer(player);
        return (pi != null && pi.isInEvent());
    }

    
    public static boolean isInEvent(L2Character ch) {
        if (ch instanceof l2r.gameserver.model.actor.L2Playable) {
            return isInEvent(ch.getActingPlayer());
        }
        return EventManager.getInstance().isInEvent(new CharacterData(ch));
    }

    
    public static boolean allowDie(L2Character ch, L2Character attacker) {
        if (isInEvent(ch) && isInEvent(attacker)) {
            return EventManager.getInstance().allowDie(new CharacterData(ch), new CharacterData(attacker));
        }
        return true;
    }

    
    public static boolean isInMiniEvent(L2PcInstance player) {
        PlayerEventInfo pi = getPlayer(player);
        return (pi != null && pi.getActiveGame() != null);
    }

    
    public static boolean isInMainEvent(L2PcInstance player) {
        PlayerEventInfo pi = getPlayer(player);
        return (pi != null && pi.getActiveEvent() != null);
    }

    
    public static boolean canShowToVillageWindow(L2PcInstance player) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.canShowToVillageWindow();
        }
        return true;
    }

    
    public static boolean canAttack(L2PcInstance player, L2Character target) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.canAttack(target);
        }
        return true;
    }

    
    public static boolean onAttack(L2Character cha, L2Character target) {
        return EventManager.getInstance().onAttack(new CharacterData(cha), new CharacterData(target));
    }

    
    public static boolean canSupport(L2PcInstance player, L2Character target) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.canSupport(target);
        }
        return true;
    }

    
    public static boolean canTarget(L2PcInstance player, L2Object target) {
        return true;
    }

    
    public static void onHit(L2PcInstance player, L2Character target, int damage, boolean isDOT) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            pi.onDamageGive(target, damage, isDOT);
        }
    }

    
    public static void onDamageGive(L2Character cha, L2Character target, int damage, boolean isDOT) {
        EventManager.getInstance().onDamageGive(new CharacterData(cha), new CharacterData(target), damage, isDOT);
    }

    
    public static void onKill(L2PcInstance player, L2Character target) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            pi.notifyKill(target);
        }
    }

    
    public static void onDie(L2PcInstance player, L2Character killer) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            pi.notifyDie(killer);
        }
    }

    
    public static boolean onNpcAction(L2PcInstance player, L2Npc target) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.notifyNpcAction(target);
        }
        return false;
    }

    
    public static boolean canUseItem(L2PcInstance player, L2ItemInstance item) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.canUseItem(item);
        }
        return true;
    }

    
    public static void onUseItem(L2PcInstance player, L2ItemInstance item) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            pi.notifyItemUse(item);
        }
    }

    
    public static boolean onSay(L2PcInstance player, String text, int channel) {
        try {
            if (text.startsWith(".")) {
                if (EventManager.getInstance().tryVoicedCommand(player.getEventInfo(), text)) {
                    return false;
                }
                return true;
            }
        } catch (Exception exception) {
        }
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.notifySay(text, channel);
        }
        return true;
    }

    
    public static boolean canUseSkill(L2PcInstance player, L2Skill skill) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.canUseSkill(skill);
        }
        return true;
    }

    
    public static void onUseSkill(L2PcInstance player, L2Skill skill) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            pi.onUseSkill(skill);
        }
    }

    
    public static boolean canDestroyItem(L2PcInstance player, L2ItemInstance item) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.canDestroyItem(item);
        }
        return true;
    }

    
    public static boolean canInviteToParty(L2PcInstance player, L2PcInstance target) {
        PlayerEventInfo pi = getPlayer(player);
        PlayerEventInfo targetPi = getPlayer(target);
        if (pi != null) {
            if (targetPi == null) {
                return false;
            }
            return pi.canInviteToParty(pi, targetPi);
        }
        return true;
    }
    

    public static boolean canTransform(L2PcInstance player) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.canTransform(pi);
        }
        return true;
    }

    
    public static int allowTransformationSkill(L2PcInstance player, L2Skill s) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.allowTransformationSkill(s);
        }
        return 0;
    }

    
    public static boolean canBeDisarmed(L2PcInstance player) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            return pi.canBeDisarmed(pi);
        }
        return true;
    }

    
    public static boolean onBypass(L2PcInstance player, String command) {
        if (command.startsWith("nxs_")) {
            return EventManager.getInstance().onBypass(player.getEventInfo(), command.substring(4));
        }
        return false;
    }

    public static void onAdminBypass(PlayerEventInfo player, String command) {
        EventManagement.getInstance().onBypass(player, command);
    }

    public static boolean canLogout(L2PcInstance player) {
        PlayerEventInfo pi = getPlayer(player);
        return (pi == null || !pi.isInEvent());
    }
    
    public static void onLogout(L2PcInstance player) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null) {
            pi.notifyDisconnect();
        }
    }

    
    public static boolean isObserving(L2PcInstance player) {
        return player.getEventInfo().isSpectator();
    }

    
    public static void endObserving(L2PcInstance player) {
        EventManager.getInstance().removePlayerFromObserverMode(player.getEventInfo());
    }

    public static boolean canSaveShortcuts(L2PcInstance activeChar) {
        PlayerEventInfo pi = getPlayer(activeChar);
        if (pi != null) {
            pi.canSaveShortcuts();
        }
        return true;
    }

    public static int getItemAutoEnchantValue(L2PcInstance player, L2ItemInstance item) {
        if (isInEvent(player)) {
            PlayerEventInfo pi = PlayerBase.getInstance().getPlayer(player);
            MiniEventManager event = pi.getRegisteredMiniEvent();
            if (event == null) {
                return 0;
            }
            for (AbstractFeature f : event.getMode().getFeatures()) {
                if (f.getType() == EventMode.FeatureType.Enchant) {
                    switch (item.getItem().getType2()) {
                        case 0:
                            return ((EnchantFeature) f).getAutoEnchantWeapon();
                        case 1:
                            return ((EnchantFeature) f).getAutoEnchantArmor();
                        case 2:
                            return ((EnchantFeature) f).getAutoEnchantJewel();
                    }
                }
            }
            return 0;
        }
        return 0;
    }

    
    public static boolean removeCubics() {
        return EventConfig.getInstance().getGlobalConfigBoolean("removeCubicsOnDie");
    }

    
    public static boolean gainPvpPointsOnEvents() {
        return EventConfig.getInstance().getGlobalConfigBoolean("pvpPointsOnKill");
    }

    
    public static boolean cbBypass(L2PcInstance player, String command) {
        PlayerEventInfo pi = getPlayer(player);
        if (pi != null && command != null) {
            return EventManager.getInstance().getHtmlManager().onCbBypass(pi, command);
        }
        return false;
    }

    public static String consoleCommand(String cmd) {
        if (cmd.startsWith("reload_globalconfig")) {
            EventConfig.getInstance().loadGlobalConfigs();
            return "Global configs reloaded.";
        }
        return "This command doesn't exist.";
    }

    
    public static boolean adminCommandRequiresConfirm(String cmd) {
        if ((cmd.split(" ")).length > 1) {
            String command = cmd.split(" ")[1];
            return EventManagement.getInstance().commandRequiresConfirm(command);
        }
        return false;
    }

    
    public static boolean isSkillOffensive(L2PcInstance activeChar, L2Skill skill) {
        PlayerEventInfo pi = getPlayer(activeChar);
        if (pi != null) {
            if (pi.isInEvent()) {
                EventGame game = pi.getEvent();
                int val = game.isSkillOffensive(new SkillData(skill));
                if (val == 1) {
                    return true;
                }
                if (val == 0) {
                    return false;
                }
            }
        }
        return skill.isOffensive();
    }

    
    public static boolean isSkillNeutral(L2PcInstance activeChar, L2Skill skill) {
        PlayerEventInfo pi = getPlayer(activeChar);
        if (pi != null) {
            if (pi.isInEvent()) {
                EventGame game = pi.getEvent();
                return game.isSkillNeutral(new SkillData(skill));
            }
        }
        return false;
    }
}


