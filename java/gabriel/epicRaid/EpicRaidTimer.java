package gabriel.epicRaid;


import gabriel.config.GabConfig;
import gr.sr.utils.Tools;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.clientpackets.RequestSendPost;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Gabriel Costa Souza
 * Discord: gabsoncs
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class EpicRaidTimer {

    private final int instId;
    private ScheduledFuture<?> timer;
    private final Map<String, Integer> playersInside = new HashMap<>();

    public Map<String, Integer> getPlayersInside() {
        return playersInside;
    }


    public void stop() {
        if (timer != null) {
            timer.cancel(true);
            timer = null;
        }
    }

    private EpicRaidTimer(Instance instance) {
        instId = instance.getId();
    }

    protected static EpicRaidTimer instance;


    public static EpicRaidTimer getInstance(Instance inst) {
        if (instance == null)
            instance = new EpicRaidTimer(inst);
        return instance;
    }

    public void startInsideTimer() {
        if (instId == GabConfig.ER_EVENT_INSTANCE_ID) {
            int delay = 30000;
            int period = 30000;
            if(InstanceManager.getInstance().getInstance(GabConfig.ER_EVENT_INSTANCE_ID) == null)
                delay *= 2;

            timer = ThreadPoolManager.getInstance().scheduleEventAtFixedRate(() -> {
                for (L2PcInstance player : InstanceManager.getInstance().getInstance(GabConfig.ER_EVENT_INSTANCE_ID).getAllPlayers()) {
                    if(player == null)
                        continue;
                    if (player.isOnline() && !player.isDead() && (player.getInstanceId() == GabConfig.ER_EVENT_INSTANCE_ID)) {
                        if (!playersInside.containsKey(player.getName())) {
                            playersInside.put(player.getName(), 0);
                        }
                        int secondsInside = playersInside.get(player.getName());
                        secondsInside += 30;
                        playersInside.put(player.getName(), secondsInside);
                    }
                }

            }, delay, period, TimeUnit.MILLISECONDS);
        }
    }

    public void rewardPlayers() {
        L2PcInstance admin = L2World.getInstance().getPlayer(GabConfig.ER_EVENT_ADMIN_OBJ_ID);
        if (admin == null || !admin.isOnline()) {
            admin = L2PcInstance.load(GabConfig.ER_EVENT_ADMIN_OBJ_ID);
        }

        List<String> received = new LinkedList<>();


        for (Map.Entry<String, Integer> playerInside : playersInside.entrySet()) {
            String playerName = playerInside.getKey();
            L2PcInstance player = L2World.getInstance().getPlayer(playerName);
            if(player == null || received.contains(player.getClient().getHWID()))
                continue;
            int timeInside = playerInside.getValue();


            if (timeInside < GabConfig.ER_EVENT_MINIMUM_INSIDE_SECONDS) {
                continue;
            }
            RequestSendPost post = new RequestSendPost();
            post.set_receiver(playerName);
            post.set_isCod(false);
            post.set_subject("Epic Raid Event Participation");
            post.set_text("Thank you for participating the Epic Raid Event! Here are your Rewards!");
            post.setAdminSend(true);

            RequestSendPost.AttachmentItem[] itemArr = new RequestSendPost.AttachmentItem[GabConfig.ER_EVENT_PARTICIPATION_REWARD.length];
            int i = 0;
            int minutes = (int) Math.floor(timeInside / 60);

            for (String s : GabConfig.ER_EVENT_PARTICIPATION_REWARD) {
                String[] reward = s.split(",");
                int itemId = Integer.parseInt(reward[0]);
                int itemCount = Integer.parseInt(reward[1]);

//                if(minutes == 0)
//                    minutes = 1;

                itemCount = (GabConfig.ER_EVENT_ITEM_COUNT_DIVIDER / itemCount) * minutes;

                if (itemCount == 0)
                    continue;

                if (itemCount > GabConfig.ER_EVENT_ITEM_MAX_COUNT) {
                    itemCount = GabConfig.ER_EVENT_ITEM_MAX_COUNT;
                }
                if (itemCount > 1) {
                    admin.getInventory().addItem("MailERToPlayer", itemId, itemCount, admin, admin);
                    L2ItemInstance itemInstance = admin.getInventory().getItemByItemId(itemId);
                    RequestSendPost.AttachmentItem requestItem = new RequestSendPost.AttachmentItem(itemInstance.getObjectId(), itemCount);
                    itemArr[i] = requestItem;
                    i++;
                }

            }

            if (minutes > 0) {
                received.add(player.getClient().getHWID());
                post.set_items(itemArr);
            }
            post.runImpl();
        }
        if (admin != null && admin.isOnline())
            admin.deleteMe();
        admin = null;
        playersInside.clear();
    }
}
