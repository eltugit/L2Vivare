package gabriel.others;

import gabriel.config.GabConfig;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import scripts.quests.Q00254_LegendaryTales.Q00254_LegendaryTales;

public class DragonStatus implements IVoicedCommandHandler {
    private static final String[] VOICED_COMMANDS =
            {
                    "7rb"
            };

    @Override
    public boolean useVoicedCommand(String command, L2PcInstance player, String params) {
        QuestState qs = player.getQuestState(Q00254_LegendaryTales.class.getSimpleName());
        if (qs == null) {
            player.sendMessage("LegendaryTales: innactive");
            CreatureSay np = new CreatureSay(0, Say2.TELL, "Legendary Tales", "LegendaryTales: innactive");
            player.sendPacket(np);
            return false;
        }
        QuestState st = player.getQuestState(qs.getQuest().getName());
        int var = st.getInt("raids");
        Q00254_LegendaryTales.checkKilledRaids(player, var);

        NpcHtmlMessage m = new NpcHtmlMessage();
        m.setHtml(buildHtml(st));
        player.sendPacket(m);
        return true;
    }

    private static final String buildHtml(QuestState st) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head>");
        sb.append("<title>Lineage II " + GabConfig.L2GABSON_SERVER_NAME + "</title>");
        sb.append("</head>");
        sb.append("<body><br>");
        sb.append("<br>7 Rb Quest (Legendary Tales) status:<br>");
        if (st == null) {
            sb.append("Quest is not started yet. Please visit Glimore in dragon valley in order to start it.");
            sb.append("<br>");
        } else {
            if (st.isCond(1)) {
                for (Bosses boss : Bosses.class.getEnumConstants()) {
                    sb.append(boss.getName() + ": ");
                    sb.append(checkMask(st, boss) ? "<font color=\"00FF00\">Killed.</font>" : "<font color=\"FF0000\">Not killed.</font>");
                    sb.append("<br>");
                }
            } else {
                sb.append("Legendary Tales quest is completed.");
                sb.append("<br>");
            }
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    private static boolean checkMask(QuestState qs, Bosses boss) {
        int pos = boss.getMask();
        return ((qs.getInt("raids") & pos) == pos);
    }

    public static enum Bosses {
        EMERALD_HORN("Emerald Horn"),
        DUST_RIDER("Dust Rider"),
        BLEEDING_FLY("Bleeding Fly"),
        BLACK_DAGGER("Blackdagger Wing"),
        SHADOW_SUMMONER("Shadow Summoner"),
        SPIKE_SLASHER("Spike Slasher"),
        MUSCLE_BOMBER("Muscle Bomber");

        private final String name;
        private final int _mask;

        private Bosses(String name) {
            this.name = name;
            _mask = 1 << ordinal();
        }

        public int getMask() {
            return _mask;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public String[] getVoicedCommandList() {
        return VOICED_COMMANDS;
    }
}