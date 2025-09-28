package gabriel.events.extremeZone;

import gabriel.Utils.GabUtils;
import gabriel.config.GabConfig;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.L2CommandChannel;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class ExtremeAccessChecker implements Runnable {
    private boolean running = false;
    private boolean cancel = false;

    public List<AccessGranterEX> getListOfAccesses() {
        return listOfAccesses;
    }

    private AccessGranterEX winningAC;
    private int percentageOwned = 0;
    private List<AccessGranterEX> listOfAccesses = new ArrayList<>();

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
        L2Npc npc = ExtremeZoneManager.getInstance().getNpcSpawn();
        if (npc == null) {
            ThreadPoolManager.getInstance().scheduleGeneral(this, 5 * 1000);

        }

        for (L2PcInstance player : npc.getKnownList().getKnownPlayersInRadius(750)) {

//            if(player.isGM()){
//                continue;
//            }

            if ((!GabConfig.EXTREME_EVENT_RADIUS_CHECK && player.isInsideZone(ZoneIdType.EPIC_RAID_CHECKER)) ||
                    (GabConfig.EXTREME_EVENT_RADIUS_CHECK && player.isInsideRadius(npc.getLocation(), GabConfig.EXTREME_EVENT_RADIUS_VALUE, true, false) && player.getInstanceId() == npc.getInstanceId())) {

                if (GabConfig.EXTREME_EVENT_DRAW_LINES) {
                    if (GabConfig.EXTREME_EVENT_RADIUS_CHECK) {
                        ExtremeZoneManager.getInstance().handleDrawn(false, true);
                    } else {
                        ExtremeZoneManager.getInstance().handleDrawn(false, false);
                    }
                }

                if (player.isDead())
                    continue;

                L2Party party = player.getParty();
                L2Clan clan = player.getClan();
                L2CommandChannel cc = party == null ? null : party.getCommandChannel();

                AccessGranterEX ag = listOfAccesses.stream().filter(e -> e.getRightAccess(player) != null).findFirst().orElse(null);

                if (ag == null) {
                    ag = new AccessGranterEX(player, party, clan, cc);
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

                rewardEveryoneFromAcess(player, npc);

            } else {
                continue;
            }
        }
        npc.getTemplate().setName("Extreme Zone");
        if (winningAC != null && ExtremeZoneManager.getInstance().getAccessGranted() != winningAC) {
            percentageOwned += 15;
            if (percentageOwned >= 100) {
                AccessGranterEX previousOwner = ExtremeZoneManager.getInstance().getAccessGranted();
                ExtremeZoneManager.getInstance().setAccessGranted(winningAC);
                percentageOwned = 0;

                String extra = previousOwner == null ? "." : " and revoked the access of " + previousOwner.getName();
                String mm = winningAC.getName() + " has captured Extreme Zone" + extra;

                GabUtils.yellowBroadcast(mm, "Extreme Zone", GabConfig.EX_TEXT_KIND);
                npc.getTemplate().setTitle("Granted: " + winningAC.getName());
                npc.broadcastInfo();
                npc.broadcastStatusUpdate();


                winningAC = null;
            } else {
                String msg = percentageOwned + "% captured by " + winningAC.getName() + ".";
                GabUtils.yellowBroadcast(msg, "Extreme Zone", GabConfig.EX_TEXT_KIND);
            }
        } else {
            percentageOwned = 0;
        }
        ThreadPoolManager.getInstance().scheduleGeneral(this, 5 * 1000);
    }


    private void rewardEveryoneFromAcess(L2PcInstance player, L2Npc npc) {

        if (player.isDead())
            return;

        boolean canReward = (!GabConfig.EXTREME_EVENT_RADIUS_CHECK && player.isInsideZone(ZoneIdType.EPIC_RAID_CHECKER)) ||
                (GabConfig.EXTREME_EVENT_RADIUS_CHECK && player.isInsideRadius(npc.getLocation(), GabConfig.EXTREME_EVENT_RADIUS_VALUE, true, false));

        if (canReward) {
            AccessGranterEX playerAccess = listOfAccesses.stream().filter(e -> e.getRightAccess(player) != null).findFirst().orElse(null);
            AccessGranterEX currentOwner = ExtremeZoneManager.getInstance().getAccessGranted();

            if (currentOwner == null)
                return;

            if (currentOwner == playerAccess) {
                for (String s : GabConfig.EXTREME_EVENT_CAPTURED_REWARD) {
                    String[] reward = s.split(",");
                    int itemId = Integer.parseInt(reward[0]);
                    int itemCount = Integer.parseInt(reward[1]);
                    player.addItem("Custom Reward Extreme Zone", itemId, itemCount, player, true);
                }
            }

        }

    }
}
