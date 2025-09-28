package gabriel.events.extremeZone;

import gabriel.config.GabConfig;
import gr.sr.utils.Tools;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.clientpackets.RequestSendPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class ExtremeZoneTimer {

    private final Instance inst;
    private final int instId;
    private ScheduledFuture<?> timer;
    private final Map<String, Integer> playersInside = new HashMap<>();
    private L2Npc npc;

    public Map<String, Integer> getPlayersInside() {
        return playersInside;
    }

    public void stop() {
        if (timer != null) {
            timer.cancel(true);
            timer = null;
        }
    }

    private ExtremeZoneTimer(Instance instance) {
        this.inst = instance;
        instId = instance.getId();
    }

    protected static ExtremeZoneTimer instance;

    public static ExtremeZoneTimer getInstance(Instance inst) {
        if (instance == null)
            instance = new ExtremeZoneTimer(inst);
        return instance;
    }

    public void startInsideTimer() {
        if (instId == GabConfig.EXTREME_EVENT_INSTANCE_ID) {
            int delay = 30000;
            int period = 30000;
            timer = ThreadPoolManager.getInstance().scheduleEventAtFixedRate(() -> {
                npc = ExtremeZoneManager.getInstance().getNpcSpawn();
                if (npc == null)
                    npc = ExtremeZoneManager.getInstance().getBossSpawn();

                if (npc != null) {
                    for (L2PcInstance player : InstanceManager.getInstance().getInstance(GabConfig.EXTREME_EVENT_INSTANCE_ID).getAllPlayers()) {
                        boolean insideZone = (!GabConfig.EXTREME_EVENT_RADIUS_CHECK && player.isInsideZone(ZoneIdType.EPIC_RAID_CHECKER)) ||
                                (GabConfig.EXTREME_EVENT_RADIUS_CHECK && player.isInsideRadius(npc.getLocation(), GabConfig.EXTREME_EVENT_RADIUS_VALUE, true, false));
                        if (player.isOnline() && !player.isDead() && (player.getInstanceId() == GabConfig.EXTREME_EVENT_INSTANCE_ID) && insideZone) {
                            if (!playersInside.containsKey(player.getName())) {
                                playersInside.put(player.getName(), 0);
                            }
                            int secondsInside = playersInside.get(player.getName());
                            secondsInside += 30;
                            playersInside.put(player.getName(), secondsInside);
                        }
                    }
                }
            }, delay, period, TimeUnit.MILLISECONDS);
        }
    }

    //    private Map<String, String[]> alreadyReceived = new HashMap<>();
    private List<String> alreadyReceived = new ArrayList<>();

    private boolean pAlreaydReceived(L2PcInstance pp) {
        return alreadyReceived.contains(Tools.getPcIp(pp));
    }

    public void rewardPlayers() {
        alreadyReceived.clear();
        L2PcInstance admin = L2World.getInstance().getPlayer(GabConfig.EXTREME_EVENT_ADMIN_OBJ_ID);
        if (admin == null || !admin.isOnline()) {
            admin = L2PcInstance.load(GabConfig.EXTREME_EVENT_ADMIN_OBJ_ID);
        }

        for (Map.Entry<String, Integer> playerInside : playersInside.entrySet()) {
            String playerName = playerInside.getKey();
            L2PcInstance pp = L2World.getInstance().getPlayer(playerName);
            if (pp == null || pp.getClient() == null || Tools.getPcIp(pp) == null)
                continue;
//
            int timeInside = playerInside.getValue();

            if (timeInside < GabConfig.EXTREME_EVENT_MINIMUM_INSIDE_SECONDS) {
                continue;
            }
            if (pAlreaydReceived(pp))
                continue;
            RequestSendPost post = new RequestSendPost();
            post.set_receiver(playerName);
            post.set_isCod(false);
            post.set_subject("Extreme Zone Event Participation");
            post.set_text("Thank you for participating the Extreme Zone Event! Here are your Rewards!");
            post.setAdminSend(true);

            RequestSendPost.AttachmentItem[] itemArr = new RequestSendPost.AttachmentItem[GabConfig.EXTREME_EVENT_PARTICIPATION_REWARD.length];
            int i = 0;
            int minutes = (int) Math.floor(timeInside / 60);

            for (String s : GabConfig.EXTREME_EVENT_PARTICIPATION_REWARD) {
                String[] reward = s.split(",");
                int itemId = Integer.parseInt(reward[0]);
                int itemCount = Integer.parseInt(reward[1]);

//                if(minutes == 0)
//                    minutes = 1;

                itemCount = (GabConfig.EXTREME_EVENT_ITEM_COUNT_DIVIDER / itemCount) * minutes;

                if (itemCount == 0)
                    continue;

                if (itemCount > GabConfig.EXTREME_EVENT_ITEM_MAX_COUNT) {
                    itemCount = GabConfig.EXTREME_EVENT_ITEM_MAX_COUNT;
                }
                if (itemCount > 1) {
                    admin.getInventory().addItem("MailEXToPlayer", itemId, itemCount, admin, admin);
                    L2ItemInstance itemInstance = admin.getInventory().getItemByItemId(itemId);
                    RequestSendPost.AttachmentItem requestItem = new RequestSendPost.AttachmentItem(itemInstance.getObjectId(), itemCount);
                    itemArr[i] = requestItem;
                    i++;
                }
            }
            if (minutes > 0) {
                post.set_items(itemArr);
            }
            post.runImpl();
            alreadyReceived.add(Tools.getPcIp(pp));

        }
        if (admin != null && admin.isOnline())
            admin.deleteMe();
        admin = null;
        playersInside.clear();
    }
}
