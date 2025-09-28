package gr.sr.configsEngine;


import gr.sr.configsEngine.configs.impl.*;


public class ConfigsController {
    public ConfigsController() {
    }

    
    public void reloadSunriseConfigs() {
        AioItemsConfigs.getInstance().loadConfigs();
        AntibotConfigs.getInstance().loadConfigs();
        AutoRestartConfigs.getInstance().loadConfigs();
        BackupManagerConfigs.getInstance().loadConfigs();
        BufferConfigs.getInstance().loadConfigs();
        ChaoticZoneConfigs.getInstance().loadConfigs();
        ColorSystemConfigs.getInstance().loadConfigs();
        CommunityDonateConfigs.getInstance().loadConfigs();
        CommunityServicesConfigs.getInstance().loadConfigs();
        CustomNpcsConfigs.getInstance().loadConfigs();
        CustomServerConfigs.getInstance().loadConfigs();
        DonateManagerConfigs.getInstance().loadConfigs();
        FlagZoneConfigs.getInstance().loadConfigs();
        FormulasConfigs.getInstance().loadConfigs();
        GetRewardVoteSystemConfigs.getInstance().loadConfigs();
        IndividualVoteSystemConfigs.getInstance().loadConfigs();
        LeaderboardsConfigs.getInstance().loadConfigs();
        PcBangConfigs.getInstance().loadConfigs();
        PremiumServiceConfigs.getInstance().loadConfigs();
        PvpRewardSystemConfigs.getInstance().loadConfigs();
        SecuritySystemConfigs.getInstance().loadConfigs();
        SmartCommunityConfigs.getInstance().loadConfigs();
    }

    private static ConfigsController instance;

    
    public static ConfigsController getInstance() {
        if(instance == null)
            instance = new ConfigsController();
        return instance;
    }
}
