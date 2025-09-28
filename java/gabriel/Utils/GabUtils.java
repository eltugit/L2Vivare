package gabriel.Utils;


import gabriel.config.GabConfig;
import gabriel.events.castleSiegeKoth.CSKOTHEvent;
import gabriel.pvpInstanceZone.ConfigPvPInstance.ConfigPvPInstance;
import gabriel.pvpInstanceZone.PvPZoneManager;
import gr.sr.imageGeneratorEngine.GlobalImagesCache;
import l2r.Config;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.instancemanager.MailManager;
import l2r.gameserver.model.BlockList;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Message;
import l2r.gameserver.model.itemcontainer.Mail;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class GabUtils {

    public static boolean isInPvPInstance(L2Character player) {
        return player.getInstanceId() == ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID;
    }
    public static void handleTutorialBypass(String bypass, L2PcInstance player){
        String[] cmds = bypass.split(" ");
        String command = cmds[1];
        switch (command){
            case "close":{
                closeTutorial(player);
                break;
            }
        }
    }
    public static void closeTutorial(L2PcInstance player) {
        player.sendPacket(new CloseTutorial());
    }

    private static class CloseTutorial extends L2GameServerPacket {
        public static void hideComm() {

        }

        @Override
        protected void writeImpl() {
            writeC(0xa9);
        }
    }
    public static int getMinutesFromStart(long start) {
        long end = System.currentTimeMillis();
        float msec = end - start;
        float sec = msec / 1000F;
        float minutes = sec / 60F;
        return (int) minutes;
    }
    public static int getSecondsFromStart(long start) {
        long end = System.currentTimeMillis();
        float msec = end - start;
        float sec = msec / 1000F;
        return (int) sec;
    }
    public static String getItemIcon(int itemId) {
        L2Item item = ItemData.getInstance().getTemplate(itemId);
        String icon = "icon.NOICON";
        if (item == null) {
            //_log.warning("DressMe: Could not find Item for item: "+itemId);
        } else {
            icon = item.getIcon();
        }
        return icon;
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<> ();
    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "KK");
        suffixes.put(1_000_000_000L, "KKK");
        suffixes.put(1_000_000_000_000L, "KKKK");
        suffixes.put(1_000_000_000_000_000L, "KKKKK");
        suffixes.put(1_000_000_000_000_000_000L, "KKKKKKK");
    }

    public static String formatNumberSulfic(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatNumberSulfic(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatNumberSulfic(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String getItemName(int itemId) {
        return ItemData.getInstance().getTemplate(itemId).getName();
    }
    public static String getTimeRemaining(long timeEnd) {
        Calendar temp = Calendar.getInstance();
        temp.setTimeInMillis(timeEnd);
        int intervall = (int) ChronoUnit.SECONDS.between(Calendar.getInstance().toInstant(), temp.toInstant());

        String remainingTime = "";
        int hour = (intervall / 60 / 60) % 60;
        int minutes = (intervall / 60) % 60;
        int seconds = intervall % 60;
        if (seconds < 10 && minutes > 10) {
            remainingTime = hour + ":" + minutes + ":0" + seconds;

        } else if (minutes < 10 && seconds > 10) {
            remainingTime = hour + ":0" + minutes + ":" + seconds;

        } else if (minutes < 10 && seconds < 10) {
            remainingTime = hour + ":0" + minutes + ":0" + seconds;
        } else {
            remainingTime = hour + ":" + minutes + ":" + seconds;
        }
        return remainingTime;
    }

    public static void sendMailToPlayer(int senderId, String eventName, int receiverObjectId, String title, String body, Map<Integer, Long> items) {
        if (title == null || receiverObjectId <= 0) {
            return;
        }

        if (items == null)
            items = new HashMap<>();

        Message msg = new Message(senderId, receiverObjectId, title, body, Message.SendBySystem.PLAYER);
        Mail attachments = msg.createAttachments();
        for (Map.Entry<Integer, Long> itm : items.entrySet()) {
            attachments.addItem("EventReward" + eventName, itm.getKey(), itm.getValue(), null, null);
        }

        MailManager.getInstance().sendMessage(msg);

    }

    public static boolean isInEpicRaid(L2Character player) {
        return player.getInstanceId() == GabConfig.ER_EVENT_INSTANCE_ID;
    }

    public static boolean isInPartyZone(L2Character player) {
        return player.getInstanceId() == GabConfig.PARTY_AREA_INSTANCE_ID;
    }


    public static boolean isInExtremeZone(L2Character player) {
        return player.getInstanceId() == GabConfig.EXTREME_EVENT_INSTANCE_ID;
    }

    public static boolean blockAllChat(L2PcInstance player) {
        if (GabUtils.isInPvPInstance(player) && PvPZoneManager.isAnnonym()) {
            CreatureSay ct = new CreatureSay(0, Say2.TELL, "Annonymous", "Chat disabled inside Annonymous Mode");
            player.sendPacket(ct);
            return true;
        }
        if (player.isInOlympiadMode() && Config.ENABLE_OLY_ANTIFEED) {
            CreatureSay ct = new CreatureSay(0, Say2.TELL, "Olympiad", "Chat disabled inside Olympiads");
            player.sendPacket(ct);
            return true;
        }
        return false;
    }

    public static int[] BISHOPS = {97};
    public static int MAX_BISHOPS = 2;

    public static boolean canInvite(L2PcInstance requestor, L2PcInstance target) {
        L2Party party = requestor.getParty();
        if (!CSKOTHEvent.isPlayerParticipant(requestor.getObjectId()) && CSKOTHEvent.isPlayerParticipant(target.getObjectId())) {
            requestor.sendMessage("Target is in King of The Hill event and you aren't");
            return false;
        }
        if (CSKOTHEvent.isPlayerParticipant(requestor.getObjectId()) && !CSKOTHEvent.isPlayerParticipant(target.getObjectId())) {
            requestor.sendMessage("Target isn' in King of The Hill event and you are");
            return false;
        }

        if (party != null) {
            if (((Arrays.stream(BISHOPS).anyMatch(i -> i == target.getClassId().getId()))) && (party.getBishops() >= MAX_BISHOPS)) {
                requestor.sendMessage("Your Party already has " + MAX_BISHOPS + " Cardinal");
                return false;
            }
            if (((Arrays.stream(GabConfig.HEALLERS).anyMatch(i -> i == target.getClassId().getId()))) && (party.getHeallers() >= GabConfig.MAX_HEALERS)) {
                requestor.sendMessage("Your Party already has " + GabConfig.MAX_HEALERS + " Evas Saint, Shillien Saint or Judicator");
                return false;
            }
            if (((Arrays.stream(GabConfig.TANKS).anyMatch(i -> i == target.getClassId().getId()))) && (party.getTanks() >= GabConfig.MAX_TANKS)) {
                requestor.sendMessage("Your Party already has " + GabConfig.MAX_TANKS + " Tanker");
                return false;
            }
            if ((target.getClassId().getId() == 115 || target.getClassId().getId() == 51) && party.getDominator() >= GabConfig.MAX_DOMINATORS) {
                requestor.sendMessage("Your Party already has " + GabConfig.MAX_DOMINATORS + "Dominator");
                return false;
            }
        }

        return true;
    }

    public static void extremeZoneBroadcast(String text) {
        yellowBroadcast(text, GabConfig.EX_TEXT_FROM, GabConfig.EX_TEXT_KIND);
    }

    public static void challengerZoneBroadcast(String text) {
        yellowBroadcast(text, GabConfig.CHALLENGER_TEXT_FROM, GabConfig.CHALLENGER_TEXT_KIND);
    }

    public static void yellowBroadcast(String text, String from) {
        yellowBroadcast(text, from, Say2.BATTLEFIELD);
    }

    public static void yellowBroadcast(String text, String from, int type) {
        toAllOnlinePlayers(new CreatureSay(0, type, from, text));
    }

    private static void toAllOnlinePlayers(L2GameServerPacket packet) {
        for (L2PcInstance player : L2World.getInstance().getPlayers()) {
            if (player.isOnline()) {
                player.sendPacket(packet);
            }
        }
    }

    public static String sendImagesToPlayer(String content, L2PcInstance activeChar) {
        Matcher m = GlobalImagesCache.HTML_PATTERN.matcher(content);
        while (m.find()) {
            String imageName = m.group(1);
            int imageId = GlobalImagesCache.getInstance().getImageId(imageName);
            content = content.replaceAll("%image:" + imageName + "%", "Crest.crest_" + Config.SERVER_ID + "_" + imageId);
        }

        GlobalImagesCache.getInstance().sendUsedImages(content, activeChar);
        return content;
    }


    public static void verifyClanCloak(L2PcInstance player) {
        if (player.getClan() != null) {
            if (GabConfig.ALLOW_CLAN_CLOAK) {

                //clearn any other clancloak that the player has that isnt from his clan
                for (Integer value : GabConfig.CLAN_CLOAK.values()) {
                    if (!player.getInventory().getItemsByItemId(value).isEmpty() && !isValidCloak(player, value)) {
                        player.destroyItemByItemId("clancloak", value, 1, null, true);
                    }
                    if (!player.getWarehouse().getItemsByItemId(value).isEmpty() && !isValidCloak(player, value)) {
                        player.getWarehouse().destroyItemByItemId("clancloak", value, 1, null, true);
                    }
                }

                if (GabConfig.CLAN_CLOAK.containsKey(player.getClanId())) {
                    int cloakId = GabConfig.CLAN_CLOAK.get(player.getClanId());
                    L2Item item = ItemData.getInstance().getTemplate(cloakId);
                    if (item == null) {
                        System.out.println("Item with Id: " + cloakId + " doesnt exist!");
                        return;
                    }
                    if (player.getInventory().getItemsByItemId(cloakId).isEmpty())
                        player.addItem("clancloak", cloakId, 1, null, true);
                } else {
                    clearCloask(player);
                }
            }
        } else {
            clearCloask(player);
        }
    }

    private static boolean isValidCloak(L2PcInstance player, int cloakId) {
        int clanId = getClanForCloak(cloakId);
        if (clanId == 0 || player.getClanId() == 0)
            return false;
        return clanId == player.getClanId();
    }

    private static int getClanForCloak(int cloakId) {
        for (Map.Entry<Integer, Integer> c : GabConfig.CLAN_CLOAK.entrySet()) {
            if (c.getValue() == cloakId) {
                return c.getKey();
            }
        }
        return 0;
    }

    private static void clearCloask(L2PcInstance player) {
        for (Integer value : GabConfig.CLAN_CLOAK.values()) {
            if (!player.getInventory().getItemsByItemId(value).isEmpty())
                player.destroyItemByItemId("clancloak", value, 1, null, true);
            if (!player.getWarehouse().getItemsByItemId(value).isEmpty())
                player.getWarehouse().destroyItemByItemId("clancloak", value, 1, null, true);
        }
    }


    public static boolean canJoinParty(L2PcInstance inviter, L2PcInstance target) {
        if (target.isInParty()) {
            return false;
        }

        if (inviter.isCursedWeaponEquipped() || target.isCursedWeaponEquipped()) {
            return false;
        }

        if (target.isInOlympiadMode() || inviter.isInOlympiadMode()) {
            return false;
        }

        if ((target.getClient() == null) || target.getClient().isDetached()) {
            return false;
        }

        if (BlockList.isBlocked(target, inviter)) {
            return false;
        }

        if (inviter.isJailed() || target.isJailed()) {
            return false;
        }
        if (GabUtils.isInPvPInstance(inviter) && (inviter.getTeam() != target.getTeam())) {
            return false;
        }
        if (GabUtils.isInPvPInstance(target) && (target.getTeam() != inviter.getTeam())) {
            return false;
        }
        return true;
    }
}

