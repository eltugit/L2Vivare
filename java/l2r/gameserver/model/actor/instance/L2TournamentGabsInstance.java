package l2r.gameserver.model.actor.instance;


import gabriel.events.tournament.TFight;
import gabriel.events.tournament.TTournamentManager;
import gr.sr.interf.SunriseEvents;
import l2r.Config;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.Arrays;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 */
public class L2TournamentGabsInstance extends L2NpcInstance {


    public L2TournamentGabsInstance(final L2NpcTemplate template) {
        super(template);
    }

    @Override
    public void onBypassFeedback(final L2PcInstance player, final String command) {

        final TTournamentManager manager = TTournamentManager.getInstance();
        final NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
        String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/default.htm");;

        if (command.startsWith("goPage")) {
            String quantity = command.split("-")[1];
            html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/goPage.htm");
            html = html.replace("%quantity%", quantity);
            html = html.replace("%objectId%", String.valueOf(getObjectId()));
        }
        else if(command.startsWith("register")){
            String quantity = command.split("-")[1];

            if(player.getParty() == null){
                html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/no-party.htm");
            }
            else if(!player.getParty().isLeader(player)){
                html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/no-leader.htm");
            }
            else if(player.isInOlympiadMode()){
                html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/nooly.htm");
            }
            else if(SunriseEvents.isRegistered(player) || SunriseEvents.isInEvent(player) || player.isInArenaEvent()){
                html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/noevt.htm");
            }
            else if(!player.isNoble()){
                html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/only-noble.htm");
            }
            else if((Arrays.stream(Config.ARENA_BANNED_CLASSES_ARENAS).anyMatch(i -> i == Integer.parseInt(quantity))) && checkSupport(player)){
                html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/nosupport.htm");
            }
            else if(player.getParty().getMembers().size() != Integer.parseInt(quantity)){
                html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/only-qnt.htm");
                html = html.replace("%quantity%", quantity);
            }else if(manager.registerTeam(player, Integer.parseInt(quantity))){
                html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/registered.htm");
            }else{
                return;
            }
        }
        else if(command.startsWith("unregister")){
            String quantity = command.split("-")[1];
            if(manager.unregisterTeam(player, Integer.parseInt(quantity))) {
                html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/unregistered.htm");
            }else {
                return;
            }
        }
        else if(command.startsWith("observe")){
            String quantity = command.split("-")[1];
            final StringBuilder tb = new StringBuilder();

            List<TFight> fights = manager.getFights(Integer.parseInt(quantity));

            if (fights == null || fights.isEmpty()){
                tb.append("No fights running<br>\n");
            }else{
                for (TFight fight : fights) {
                    tb.append("<button value=\""+fight.getFirstTeam().getLeaderName()+" x "+fight.getSecondTeam().getLeaderName()+"\" action=\"bypass -h npc_%objectId%_obsGo-"+quantity+"-"+fight.getInstanceId()+"\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">\n");
                }
            }

            html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/observe.htm");
            html = html.replace("%observeList%", tb.toString());
            html = html.replace("%objectId%", String.valueOf(getObjectId()));
            html = html.replaceAll("%quantity%", quantity);

        }
        else if(command.startsWith("obsGo")){
            if(player.isInTournament() || player.isInArenaEvent() || SunriseEvents.isRegistered(player) || SunriseEvents.isInEvent(player)){
                player.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "You cannot observe if registered in event!"));
                return;
            }else if(player.isInOlympiadMode()){
                player.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "You cannot observe if registered in oly!"));
                return;
            }
            int quantity = Integer.parseInt(command.split("-")[1]);
            int instanceId = Integer.parseInt(command.split("-")[2]);
            TFight fight = manager.getFight(quantity, instanceId);
            if(fight != null){
                fight.addSpectator(player);
            }else{
                player.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "Fight already ended!"));
                player.sendMessage("Fight already ended!");
            }

            return;
        }

        msg.setHtml(html);
        player.sendPacket(msg);
    }

    private boolean checkSupport(L2PcInstance player) {
        L2Party party = player.getParty();
        for (L2PcInstance partyMember : party.getMembers()) {
            if (Arrays.stream(Config.ARENA_BANNED_CLASSES).anyMatch(i -> i == partyMember.getClassId().getId())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void showChatWindow(final L2PcInstance player, final int val) {
        final StringBuilder tb = new StringBuilder();
        final NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());

        if (Config.ARENA_EVENT_ENABLED && TTournamentManager.getInstance().getPeriod() == 1) {
            String[] arenaMatches = Config.ARENA_EVENT_DUELS.split(",");

            for (String arenaMatch : arenaMatches) {
                int match = Integer.parseInt(arenaMatch);
                if (match >= 2 && match <= 9)
                    tb.append("<button value=\"Arena "+match+" x "+match+"\" action=\"bypass -h npc_%objectId%_goPage-"+match+"\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">\n");
            }
        } else {
            tb.append("Not running at this moment.");
        }

        String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/mods/TournamentGabriel/Home.htm");
        html = html.replace("%arenas%", tb.toString());
        html = html.replace("%objectId%", String.valueOf(getObjectId()));

        msg.setHtml(html);
        player.sendPacket(msg);
    }

}