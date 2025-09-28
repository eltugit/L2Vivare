package gabriel.events.challengerZone;

import gabriel.config.GabConfig;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.*;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.util.Rnd;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class AccessGranterCH {
    private L2PcInstance player;
    private L2Party party;
    private L2Clan clan;
    private L2CommandChannel commandChannel;
    private String name;

    public AccessGranterCH(L2PcInstance player, L2Party party, L2Clan clan, L2CommandChannel commandChannel) {
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

    public AccessGranterCH getRightAccess(L2PcInstance player) {
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
        Location loc = ChallengerZoneManager.getInstance().getNextZone().getPlayerLocs().get(Rnd.get(ChallengerZoneManager.getInstance().getNextZone().getPlayerLocs().size()));

        if (party != null) {
            for (L2PcInstance member : party.getMembers()) {
                if (canEnter(member, npcLocation)) {
                    ChallengerZoneManager.getInstance().teleportPlayerIntoInstance(member, loc);
                    deleteLines(member);
                }
            }
        }
        if (commandChannel != null) {
            for (L2PcInstance member : commandChannel.getMembers()) {
                if (canEnter(member, npcLocation)) {
                    ChallengerZoneManager.getInstance().teleportPlayerIntoInstance(member, loc);
                    deleteLines(member);
                }
            }
        }
        if (clan != null) {
            for (L2ClanMember clmember : clan.getMembers()) {
                L2PcInstance member = clmember.getPlayerInstance();
                if (member != null && member.isOnline() && canEnter(member, npcLocation)) {
                    ChallengerZoneManager.getInstance().teleportPlayerIntoInstance(member, loc);
                    deleteLines(member);
                }
            }
        }

        if (canEnter(player, npcLocation)) {
            ChallengerZoneManager.getInstance().teleportPlayerIntoInstance(player);
            deleteLines(player);
        }
    }

    private void deleteLines(L2PcInstance player) {
        if (GabConfig.CHALLENGER_EVENT_DRAW_LINES) {
            if (GabConfig.CHALLENGER_EVENT_RADIUS_CHECK) {
                ChallengerZoneManager.getInstance().drawn(player, true, true);
            } else {
                ChallengerZoneManager.getInstance().drawn(player, true, false);
            }
        }
    }

    private boolean canEnter(L2PcInstance player, Location npcLocation) {
        return (!GabConfig.CHALLENGER_EVENT_RADIUS_CHECK && player.isInsideZone(ZoneIdType.CHALLENGER_CHECKER)) ||
                (GabConfig.CHALLENGER_EVENT_RADIUS_CHECK && player.isInsideRadius(npcLocation, GabConfig.CHALLENGER_EVENT_RADIUS_VALUE, true, false));
    }
}
