package gr.sr.zones;


import gr.sr.configsEngine.configs.impl.FlagZoneConfigs;
import gr.sr.utils.Tools;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.util.Map.Entry;


public class FlagZoneHandler {
    private static int maxContinousKills;

    public FlagZoneHandler() {
    }


    public static void validateRewardConditions(L2PcInstance killer, L2PcInstance target) {
        if (!FlagZoneConfigs.ENABLE_PC_IP_PROTECTION || !Tools.isDualBox(killer, target)) {
            if ((long)target.getObjectId() == killer.getPreviousVictimId()) {
                if (killer.getSameTargetCounter() < maxContinousKills) {
                    handleReward(killer);
                    killer.increaseSameTargetCounter();
                }
            } else {
                handleReward(killer);
                killer.setSameTargetCounter(0);
                killer.setPreviousVictimId(target.getObjectId());
            }
        }
    }

    private static void handleReward(L2PcInstance killer) {

        for (Entry<Integer, Long> integerLongEntry : FlagZoneConfigs.FLAG_ZONE_REWARDS.entrySet()) {
            Entry reward;
            if ((reward = integerLongEntry) != null) {
                killer.addItem("FlagZone", (Integer) reward.getKey(), (Long) reward.getValue(), killer, true);
            }
        }

    }

    static {
        maxContinousKills = FlagZoneConfigs.MAX_SAME_TARGET_CONTINUOUS_KILLS;
    }
}
