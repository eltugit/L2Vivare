package gr.sr.raidEngine.handler.voice;


import gr.sr.interf.SunriseEvents;
import gr.sr.raidEngine.manager.RaidManager;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.RadarControl;


public class RaidVCmd implements IVoicedCommandHandler {
    private static final String[] commands = new String[]{"observeraid", "findraid", "hideraid"};

    public RaidVCmd() {
    }


    public boolean useVoicedCommand(String command, L2PcInstance player, String var3) {
        if (RaidManager.getInstance()._raid == null && RaidManager.getInstance()._currentLocation == null) {
            player.sendMessage("There is no Event Raid available at the moment.");
            return false;
        } else {
            switch(command) {
                case "hideraid":
                    player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
                    break;
                case "findraid":
                    player.sendPacket(new CreatureSay(1, 15, "Info", "Open Map to see Location."));
                    player.sendPacket(new RadarControl(0, 1, RaidManager.getInstance()._currentLocation.getLocation().getX(), RaidManager.getInstance()._currentLocation.getLocation().getY(), RaidManager.getInstance()._currentLocation.getLocation().getZ()));
                    break;
                case "observeraid":
                    if (player.inObserverMode()) {
                        return false;
                    }

                    if (!player.isInsideZone(ZoneIdType.PEACE)) {
                        player.sendMessage("You cannot use observe outside of peace zones.");
                        return false;
                    }

                    if (SunriseEvents.isRegistered(player) || SunriseEvents.isInEvent(player)) {
                        player.sendMessage("You cannot use observe while in event.");
                        return false;
                    }

                    player.enterObserverMode(RaidManager.getInstance()._currentLocation.getLocation());
                    break;
            }
            return true;
        }
    }

    public String[] getVoicedCommandList() {
        return commands;
    }
}
