package gr.sr.main;


import gr.sr.backupManager.DatabaseBackupManager;
import gr.sr.backupManager.runnable.BackUpManagerTask;
import gr.sr.configsEngine.configs.impl.*;
import gr.sr.imageGeneratorEngine.GlobalImagesCache;
import gr.sr.imageGeneratorEngine.ImagesCache;
import gr.sr.leaderboards.ArenaLeaderboard;
import gr.sr.leaderboards.CraftLeaderboard;
import gr.sr.leaderboards.FishermanLeaderboard;
import gr.sr.leaderboards.TvTLeaderboard;
import gr.sr.premiumEngine.runnable.PremiumChecker;
import gr.sr.raidEngine.manager.RaidManager;
import gr.sr.voteEngine.old.runnable.TriesResetTask;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.instancemanager.BonusExpManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SunriseServerMods {
    private static final Logger log = LoggerFactory.getLogger(SunriseServerMods.class);

    public SunriseServerMods() {
    }

    
    public void checkSunriseMods() {
        if (IndividualVoteSystemConfigs.ENABLE_VOTE_SYSTEM) {
            TriesResetTask.getInstance();
            log.info("Vote System: Enabled.");
        }

        if (BackupManagerConfigs.ENABLE_DATABASE_BACKUP_MANAGER) {
            if (BackupManagerConfigs.DATABASE_BACKUP_SCHEDULER) {
                ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new BackUpManagerTask(), (long)(BackupManagerConfigs.DATABASE_BACKUP_START_DELAY * 1000 * 60), (long)(BackupManagerConfigs.DATABASE_BACKUP_DELAY_BETWEEN_BACK_UPS * 1000 * 60));
            } else if (BackupManagerConfigs.DATABASE_BACKUP_MAKE_BACKUP_ON_STARTUP) {
                DatabaseBackupManager.makeBackup();
            }

            log.info("Backup Manager: Enabled.");
        }

        if (PremiumServiceConfigs.USE_PREMIUM_SERVICE) {
            log.info("Premium System: Enabled.");
            ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new PremiumChecker(), 3600000L, 3600000L);
        }

        if (AioItemsConfigs.GIVEANDCHECK_ATSTARTUP && AioItemsConfigs.ENABLE_AIO_NPCS) {
            log.info("Aio Item On Login: Enabled.");
        }

        if (PcBangConfigs.PC_BANG_ENABLED) {
            log.info("Pc Bang Points: Enabled.");
        }

        if (SecuritySystemConfigs.ENABLE_SECURITY_SYSTEM) {
            log.info("Security System: Enabled.");
        }

        if (AutoRestartConfigs.RESTART_BY_TIME_OF_DAY) {
            Restart.getInstance().StartCalculationOfNextRestartTime();
            log.info("Auto Restart: Enabled.");
        }

        if (CustomServerConfigs.ALTERNATE_PAYMODE_CLANHALLS || CustomServerConfigs.ALTERNATE_PAYMODE_MAILS || CustomServerConfigs.ALTERNATE_PAYMODE_SHOPS) {
            log.info("Alternative Payments: Enabled.");
        }

        if (CustomServerConfigs.EXTRA_MESSAGES) {
            log.info("Extra Messages: Enabled.");
        }

        if (CustomServerConfigs.ANNOUNCE_HEROS_ON_LOGIN) {
            log.info("Announce Heros: Enabled.");
        }

        if (CustomServerConfigs.ANNOUNCE_DEATH_REVIVE_OF_RAIDS) {
            log.info("Announce Raid's: Enabled.");
        }

        if (CustomServerConfigs.ALLOW_ONLINE_COMMAND) {
            log.info("Online Command: Enabled.");
        }

        if (CustomServerConfigs.ALLOW_REPAIR_COMMAND) {
            log.info("Repair Command: Enabled.");
        }

        if (CustomServerConfigs.ALLOW_EXP_GAIN_COMMAND) {
            log.info("Experience Command: Enabled.");
        }

        if (CustomServerConfigs.ALLOW_TELEPORTS_COMMAND) {
            log.info("Teleport Command System: Enabled.");
        }

        if (CustomServerConfigs.GIVE_HELLBOUND_MAP) {
            log.info("Give HellBound Map: Enabled.");
        }

        if (CustomServerConfigs.TOP_LISTS_RELOAD_TIME > 0) {
            log.info("Top Lists Updater: Enabled.");
            ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new TopListsUpdater(), 30000L, (long)(CustomServerConfigs.TOP_LISTS_RELOAD_TIME * 60 * 1000));
        }

        if (CustomServerConfigs.AUTO_ACTIVATE_SHOTS) {
            log.info("Auto Soul Shots: Enabled.");
        }

        if (CustomServerConfigs.PVP_SPREE_SYSTEM) {
            log.info("Pvp Spree System: Enabled.");
        }

        if (CustomServerConfigs.ALT_ALLOW_CLAN_LEADER_NAME) {
            log.info("Clan Leader Color Name: Enabled.");
        }

        if (CustomServerConfigs.ANNOUNCE_CASTLE_LORDS) {
            log.info("Announce Castle Lords: Enabled.");
        }

        if (CustomServerConfigs.ALT_ALLOW_REFINE_PVP_ITEM) {
            log.info("Allow Augment For PvP Items:: Enabled.");
        }

        if (CustomServerConfigs.ALT_ALLOW_REFINE_HERO_ITEM) {
            log.info("Allow Augment For Hero Items:: Enabled.");
        }

        if (CustomServerConfigs.EVENLY_DISTRIBUTED_ITEMS) {
            log.info("Distribute Party Items: Enabled.");
        }

        if (CustomServerConfigs.ENABLE_SKILL_MAX_ENCHANT_LIMIT) {
            log.info("Skills Max Enchant Limit: Enabled.");
        }

        if (CustomServerConfigs.ENABLE_CHARACTER_CONTROL_PANEL) {
            log.info("Character Control Panel: Enabled.");
        }

        if (CustomServerConfigs.ENABLE_STARTING_TITLE) {
            log.info("Starting Title: Enabled.");
        }

        BonusExpManager.getInstance();
        TopListsLoader.getInstance();
        ImagesCache.getInstance();
        GlobalImagesCache.getInstance();
        this.checkLeaderboardsMod();
        this.checkAntibotMod();
        RaidManager.getInstance();
    }

    public void checkAntibotMod() {
        if (AntibotConfigs.ENABLE_ANTIBOT_SYSTEMS) {
            log.info("Antibot Engine: Enabled.");
        } else {
            log.info("Antibot Engine: Disabled.");
        }

        if (AntibotConfigs.ENABLE_ANTIBOT_FARM_SYSTEM) {
            log.info("Antibot Farm Engine: Enabled.");
        } else {
            log.info("Antibot Farm Engine: Disabled.");
        }

        if (AntibotConfigs.ENABLE_ANTIBOT_ENCHANT_SYSTEM) {
            log.info("Antibot Enchant Engine: Enabled.");
        } else {
            log.info("Antibot Enchant Engine: Disabled.");
        }
    }

    public void checkLeaderboardsMod() {
        if (LeaderboardsConfigs.ENABLE_LEADERBOARD) {
            log.info("LeaderBoards: Enabled.");
            if (LeaderboardsConfigs.RANK_ARENA_ENABLED) {
                ArenaLeaderboard.getInstance();
            } else {
                log.info("ArenaLeaderboard: Disabled.");
            }

            if (LeaderboardsConfigs.RANK_FISHERMAN_ENABLED) {
                FishermanLeaderboard.getInstance();
            } else {
                log.info("FishermanLeaderboard: Disabled.");
            }

            if (LeaderboardsConfigs.RANK_CRAFT_ENABLED) {
                CraftLeaderboard.getInstance();
            } else {
                log.info("CraftLeaderboard: Disabled.");
            }

            if (LeaderboardsConfigs.RANK_TVT_ENABLED) {
                TvTLeaderboard.getInstance();
            } else {
                log.info("TvTLeaderboard: Disabled.");
            }
        } else {
            log.info("LeaderBoards: Disabled.");
        }
    }

    protected static SunriseServerMods instance;

    
    public static SunriseServerMods getInstance() {
        if (instance == null)
            instance = new SunriseServerMods();
        return instance;
    }
}
