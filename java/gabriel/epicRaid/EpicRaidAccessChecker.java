package gabriel.epicRaid;


import gabriel.config.GabConfig;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.L2CommandChannel;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author Gabriel Costa Souza
 * Discord: gabsoncs
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class EpicRaidAccessChecker implements Runnable {
    private boolean running = false;
    private boolean cancel = false;


    public List<AccessGranter> getListOfAccesses() {
        return listOfAccesses;
    }

    private AccessGranter winningAC;
    private int percentageOwned = 0;
    private List<AccessGranter> listOfAccesses = new ArrayList<>();

    public void cancel() {
        if (running)
            cancel = true;
    }

    @Override
    public void run() {
        running = true;
        winningAC = null;
        if (cancel) {
            running = false;
            return;
        }
        L2Npc npc = EpicRaidManager.getInstance().getNpcSpawn();
        if (EpicRaidManager.getInstance().isClosed()) {
            npc.getTemplate().setTitle("Raid is Dying.");
            npc.broadcastInfo();
            npc.broadcastStatusUpdate();
            cancel = true;
            running = false;
            return;
        }

        for (L2PcInstance player : npc.getKnownList().getKnownPlayersInRadius(750)) {
            if (player.isGM()) {
                continue;
            }
            if ((!GabConfig.ER_EVENT_RADIUS_CHECK && player.isInsideZone(ZoneIdType.EPIC_RAID_CHECKER)) ||
                    (GabConfig.ER_EVENT_RADIUS_CHECK && player.isInsideRadius(npc.getLocation(), GabConfig.ER_EVENT_RADIUS_VALUE, true, false) && player.getInstanceId() == npc.getInstanceId())) {
                if (GabConfig.ER_EVENT_DRAW_LINES) {
                    if (GabConfig.ER_EVENT_RADIUS_CHECK) {
                        EpicRaidManager.getInstance().handleDrawn(false, true);
                    } else {
                        EpicRaidManager.getInstance().handleDrawn(false, false);
                    }
                }
                if (player.isDead())
                    continue;

                L2Party party = player.getParty();
                L2Clan clan = player.getClan();
                L2CommandChannel cc = party == null ? null : party.isInCommandChannel() ? party.getCommandChannel() : null;

                AccessGranter ag = listOfAccesses.stream().filter(e -> e.getRightAccess(player) != null).findFirst().orElse(null);

                if (ag == null) {
                    ag = new AccessGranter(player, party, clan, cc);
                    listOfAccesses.add(ag);
                } else {
                    ag.setParty(party);
                    ag.setClan(clan);
                    ag.setCommandChannel(cc);
                }

                if (winningAC == null) {
                    winningAC = ag;
                }

                if (winningAC != ag) {
                    winningAC = null;
                    percentageOwned = 0;
                }
            } else {
                continue;
            }
        }
        npc.getTemplate().setName("Epic Access");
        if (winningAC != null && EpicRaidManager.getInstance().getAccessGranted() != winningAC) {
            percentageOwned += 15;
            if (percentageOwned >= 100) {
                EpicRaidManager.getInstance().setAccessGranted(winningAC);
                percentageOwned = 0;
                npc.getTemplate().setTitle("Granted: " + winningAC.getName());
                String mm = winningAC.getName() + " has captured the Epic Boss.";
                announceToAllInYellow(mm);
                npc.broadcastInfo();
                npc.broadcastStatusUpdate();
                winningAC = null;
            } else {
                String msg = percentageOwned + "% captured by " + winningAC.getName();
                announceToAllInYellow(msg);
            }
        } else {
            percentageOwned = 0;
        }
        ThreadPoolManager.getInstance().scheduleGeneral(this, 10 * 1000);
    }

    private void announceToAllInYellow(String message) {
        announceToOnlinePlayers(message);
    }

    private static void toAllOnlinePlayers(L2GameServerPacket mov) {
        Collection<L2PcInstance> pls = L2World.getInstance().getPlayers();
        {
            for (L2PcInstance onlinePlayer : pls)
                if (onlinePlayer != null && onlinePlayer.isOnline())
                    onlinePlayer.sendPacket(mov);
        }
    }

    private static void announceToOnlinePlayers(String text) {
        CreatureSay cs = new CreatureSay(0, Say2.BATTLEFIELD, "Epic Access", text);
        toAllOnlinePlayers(cs);
    }
}
