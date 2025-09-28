package gabriel;


import gabriel.interServerExchange.ISEDatabaseFactory;
import gabriel.interServerExchange.InterServerExchangeManager;
import gabriel.Utils.FindParty;
import gabriel.Utils.NewDayScheduler;
import gabriel.balancer.GabrielBalancerLoader;
import gabriel.community.specialCraft.SpecialCraftManager;
import gabriel.config.GabConfig;
import gabriel.epicRaid.AdminEpicRaidEvent;
import gabriel.epicRaid.EpicRaidManager;
import gabriel.events.castleSiegeKoth.CSKOTHManager;
import gabriel.events.extremeZone.AdminExtremeEvent;
import gabriel.events.extremeZone.ExtremeZoneManager;
import gabriel.events.tournament.lol.LOLRankDAO;
import gabriel.events.tournament.lol.LOLTournamentManager;
import gabriel.events.tournament.lol.LOLTournamentScheduler;
import gabriel.grandbosses.GrandBossesConfig;
import gabriel.others.CharCustomHeroTable;
import gabriel.others.DragonStatus;
import gabriel.pvpInstanceZone.PvPInstanceInit;
import gabriel.scriptsGab.donatesystem.xml.DonationParse;
import gabriel.scriptsGab.forge.xml.FoundationParser;
import gabriel.events.tournament.TTournamentManager;
import gabriel.events.tournament.TTournamentScheduler;
import l2r.Config;
import l2r.gameserver.handler.AdminCommandHandler;
import l2r.gameserver.handler.VoicedCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Calendar;

import static l2r.gameserver.GameServer.printSection;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class GabrielModsInit {
    private static final Logger _log = LoggerFactory.getLogger(GabrielModsInit.class);

    private GabrielModsInit() {
        printSection("Gabe Mods Init");
        printSection("This addon packed was made by Gabriel Costa Souza for Elton Santos.\n" +
                "/**\n" +
                " * @author Gabriel Costa Souza\n" +
                " * Discord: Gabriel 'GCS'#2589\n" +
                " * Skype - email: gabriel_costa25@hotmail.com\n" +
                " */");

        GabConfig.getInstance();
        PvPInstanceInit.getInstance();
//        SkillCheckRunnable.getInstance();
        GrandBossesConfig.getInstance();
        if (GabConfig.GABRIEL_BALANCER) {
            GabrielBalancerLoader.getInstance();
        }
        printSection("Gabe Hero Donate Days - Mods");
        CharCustomHeroTable.getInstance();
        printSection("Donation Alt B");
        DonationParse.getInstance().load();
        FoundationParser.getInstance().load();

        printSection("Tournaments");
        Calendar todayCalender = Calendar.getInstance();
        String todayDay = String.valueOf(todayCalender.get(Calendar.DAY_OF_WEEK));

        if (Config.ARENA_DIAS_RUN.contains(todayDay)) {
            if (Config.ARENA_EVENT_ENABLED) {
                TTournamentScheduler.getInstance();
                TTournamentManager.getInstance();
            }
        }
        if (Config.ARENA_DIAS_RUN_LOL.contains(todayDay)) {
            if (Config.ARENA_EVENT_ENABLED_LOL) {
                LOLRankDAO.getInstance();
                LOLTournamentScheduler.getInstance();
                LOLTournamentManager.getInstance();
            }
        }
        printSection("Gab Epic Raids");
        if (GabConfig.ER_EVENT_DIAS_RUN.contains(todayDay)) {
            if (GabConfig.ER_EVENT_ENABLED) {
                EpicRaidManager.getInstance();
                printSection("Gabriel: Epic Raid is Enabled");
            } else {
                printSection("Gabriel: Epic Raid is Disabled");
            }
        }

        printSection("Gab Extreme Zone");
        if (GabConfig.EXTREME_EVENT_DIAS_RUN.contains(todayDay)) {
            if (GabConfig.EXTREME_EVENT_ENABLED) {
                ExtremeZoneManager.getInstance();
                printSection("Gabriel: Extreme Zone is Enabled");
            } else {
                printSection("Gabriel: Extreme Zone is Disabled");
            }
        }
        SpecialCraftManager.getInstance();

        AdminCommandHandler.getInstance().registerHandler(new AdminEpicRaidEvent());
        VoicedCommandHandler.getInstance().registerHandler(new DragonStatus());
        VoicedCommandHandler.getInstance().registerHandler(new FindParty());
        AdminCommandHandler.getInstance().registerHandler(new AdminExtremeEvent());
        NewDayScheduler.getInstance();
        CSKOTHManager.getInstance();

        try {
            InterServerExchangeManager.getInstance();
            ISEDatabaseFactory.getInstance().getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        _log.info("Gabriel Mods loaded with success!");

    }

    protected static GabrielModsInit instance;

    public static GabrielModsInit getInstance() {
        if (instance == null)
            instance = new GabrielModsInit();
        return instance;
    }
}
