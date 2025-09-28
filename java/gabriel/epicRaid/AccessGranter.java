package gabriel.epicRaid;


import gabriel.config.GabConfig;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.*;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.util.Rnd;

/**
 * @author Gabriel Costa Souza
 * Discord: gabsoncs
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class AccessGranter {
    private L2PcInstance player;
    private L2Party party;
    private L2Clan clan;
    private L2CommandChannel commandChannel;
    private String name;

    public AccessGranter(L2PcInstance player, L2Party party, L2Clan clan, L2CommandChannel commandChannel) {
        this.player = player;
        this.party = party;
        this.clan = clan;
        this.commandChannel = commandChannel;
        name = getRightName(player);
    }

    public void setParty(L2Party party) {
        this.party = party;
    }

    public void setClan(L2Clan clan) {
        this.clan = clan;
    }

    public void setCommandChannel(L2CommandChannel commandChannel) {
        this.commandChannel = commandChannel;
    }

    public String getName() {
        return name;
    }

    public L2PcInstance getPlayer() {
        return player;
    }

    public L2Party getParty() {
        return party;
    }

    public L2Clan getClan() {
        return clan;
    }

    public L2CommandChannel getCommandChannel() {
        return commandChannel;
    }

    private String getRightName(L2PcInstance player) {
        if (commandChannel != null) {
            if (player.getParty() != null && player.getParty().getCommandChannel() != null && commandChannel == player.getParty().getCommandChannel())
                return commandChannel.getLeader().getName() + "'s CC";
        }
        if (clan != null) {
            if (player.getClan() != null && clan == player.getClan()) {
                return clan.getName() + " Clan";
            }
        }
        if (party != null) {
            if (player.getParty() != null && party == player.getParty())
                return party.getLeader().getName() + "'s Party";
        }
        if (this.player == player) {
            return player.getName() + " player";
        }
        return "";
    }


    public AccessGranter getRightAccess(L2PcInstance player) {
        if (commandChannel != null) {
            if (player.getParty() != null && player.getParty().getCommandChannel() != null && commandChannel == player.getParty().getCommandChannel())
                return this;
        }
        if (clan != null) {
            if (player.getClan() != null && clan == player.getClan()) {
                return this;
            }
        }
        if (party != null) {
            if (player.getParty() != null && party == player.getParty())
                return this;
        }
        if (this.player == player) {
            return this;
        }
        return null;
    }


    public void teleportPlayers(Location npcLocation) {
        Location loc = EpicRaidManager.getInstance().getNextRaid().getPlayerTeleports().get(Rnd.get(EpicRaidManager.getInstance().getNextRaid().getPlayerTeleports().size() - 1));

        if (party != null) {
            for (L2PcInstance member : party.getMembers()) {
                if (canEnter(member, npcLocation)) {
                    EpicRaidManager.getInstance().teleportPlayerIntoInstance(member, loc);
                }
            }
        }
        if (commandChannel != null) {
            for (L2PcInstance member : commandChannel.getMembers()) {
                if (canEnter(member, npcLocation))
                    EpicRaidManager.getInstance().teleportPlayerIntoInstance(member, loc);
            }
        }
        if (clan != null) {
            for (L2ClanMember clmember : clan.getMembers()) {
                L2PcInstance member = clmember.getPlayerInstance();
                if (member != null && member.isOnline() && canEnter(member, npcLocation)) {
                    EpicRaidManager.getInstance().teleportPlayerIntoInstance(member, loc);
                }
            }
        }

        if (canEnter(player, npcLocation)) {
            EpicRaidManager.getInstance().teleportPlayerIntoInstance(player);
        }
    }

    private boolean canEnter(L2PcInstance player, Location npcLocation) {
        return (!GabConfig.ER_EVENT_RADIUS_CHECK && player.isInsideZone(ZoneIdType.EPIC_RAID_CHECKER)) ||
                (GabConfig.ER_EVENT_RADIUS_CHECK && player.isInsideRadius(npcLocation, GabConfig.ER_EVENT_RADIUS_VALUE, true, false));
    }
}
