package gabriel.events.challengerZone;

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
public class ChallengerAccessChecker implements Runnable {
    private boolean running = false;
    private boolean cancel = false;

    public List<AccessGranterCH> getListOfAccesses() {
        return listOfAccesses;
    }

    private AccessGranterCH winningAC;
    private int percentageOwned = 0;
    private List<AccessGranterCH> listOfAccesses = new ArrayList<>();

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
        L2Npc npc = ChallengerZoneManager.getInstance().getNpcSpawn();
        if (npc == null) {
            ThreadPoolManager.getInstance().scheduleGeneral(this, 5 * 1000);
            return;
        }

        for (L2PcInstance player : npc.getKnownList().getKnownPlayersInRadius(750)) {

//            if(player.isGM()){
//                continue;
//            }

            if ((!GabConfig.CHALLENGER_EVENT_RADIUS_CHECK && player.isInsideZone(ZoneIdType.EXTREME_CHECKER)) ||
                    (GabConfig.CHALLENGER_EVENT_RADIUS_CHECK && player.isInsideRadius(npc.getLocation(), GabConfig.CHALLENGER_EVENT_RADIUS_VALUE, true, false) && player.getInstanceId() == npc.getInstanceId())) {

                if (GabConfig.CHALLENGER_EVENT_DRAW_LINES) {
                    if (GabConfig.CHALLENGER_EVENT_RADIUS_CHECK) {
                        ChallengerZoneManager.getInstance().handleDrawn(false, true);
                    } else {
                        ChallengerZoneManager.getInstance().handleDrawn(false, false);
                    }
                }

                if (player.isDead())
                    continue;

                L2Party party = player.getParty();
                L2Clan clan = player.getClan();
                L2CommandChannel cc = party == null ? null : party.getCommandChannel();

                AccessGranterCH ag = listOfAccesses.stream().filter(e -> e.getRightAccess(player) != null).findFirst().orElse(null);

                if (ag == null) {
                    ag = new AccessGranterCH(player, party, clan, cc);
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
        npc.getTemplate().setName("Competitive Zone");
        if (winningAC != null && ChallengerZoneManager.getInstance().getAccessGranted() != winningAC) {
            percentageOwned += 15;
            if (percentageOwned >= 100) {
                AccessGranterCH previousOwner = ChallengerZoneManager.getInstance().getAccessGranted();
                ChallengerZoneManager.getInstance().setAccessGranted(winningAC);
                percentageOwned = 0;

                String extra = previousOwner == null ? "." : " and revoked the access of " + previousOwner.getName();
                String mm = winningAC.getName() + " has captured Competitive Zone" + extra;

                GabUtils.yellowBroadcast(mm, "Competitive Zone", GabConfig.CHALLENGER_TEXT_KIND);
                npc.getTemplate().setTitle("Granted: " + winningAC.getName());
                npc.broadcastInfo();
                npc.broadcastStatusUpdate();


                winningAC = null;
            } else {
                String msg = percentageOwned + "% captured by " + winningAC.getName() + ".";
                GabUtils.yellowBroadcast(msg, "Competitive Zone", GabConfig.CHALLENGER_TEXT_KIND);
            }
        } else {
            percentageOwned = 0;
        }
        ThreadPoolManager.getInstance().scheduleGeneral(this, 5 * 1000);
    }


    private void rewardEveryoneFromAcess(L2PcInstance player, L2Npc npc) {

        if (player.isDead())
            return;

        boolean canReward = (!GabConfig.CHALLENGER_EVENT_RADIUS_CHECK && player.isInsideZone(ZoneIdType.CHALLENGER_CHECKER)) ||
                (GabConfig.CHALLENGER_EVENT_RADIUS_CHECK && player.isInsideRadius(npc.getLocation(), GabConfig.CHALLENGER_EVENT_RADIUS_VALUE, true, false));

        if (canReward) {
            AccessGranterCH playerAccess = listOfAccesses.stream().filter(e -> e.getRightAccess(player) != null).findFirst().orElse(null);
            AccessGranterCH currentOwner = ChallengerZoneManager.getInstance().getAccessGranted();

            if (currentOwner == null)
                return;

            if (currentOwner == playerAccess) {
                for (String s : GabConfig.CHALLENGER_EVENT_CAPTURED_REWARD) {
                    String[] reward = s.split(",");
                    int itemId = Integer.parseInt(reward[0]);
                    int itemCount = Integer.parseInt(reward[1]);
                    player.addItem("Custom Reward Competitive Zone", itemId, itemCount, player, true);
                }
            }

        }

    }
}
