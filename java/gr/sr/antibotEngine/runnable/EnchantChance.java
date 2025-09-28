package gr.sr.antibotEngine.runnable;

import gr.sr.antibotEngine.dynamicHtmls.GenerateHtmls;
import gr.sr.configsEngine.configs.impl.AntibotConfigs;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public class EnchantChance implements Runnable {
    private L2PcInstance player;

    public EnchantChance(L2PcInstance p) {
        this.player = p;
    }

    public void run() {
        if (this.player.isEnchantBot()) {
            GenerateHtmls.captchaHtml(this.player, "ENCHANT");
            this.player._enchantChanceTimer = ThreadPoolManager.getInstance().scheduleGeneral(new EnchantChance(this.player), (long)(AntibotConfigs.ENCHANT_CHANCE_TIMER * 1000));
            if (this.player.getEnchantChance() > 10.0D) {
                this.player.setEnchantChance(this.player.getEnchantChance() - (double)AntibotConfigs.ENCHANT_CHANCE_PERCENT_TO_LOW);
            }
        }

    }
}


