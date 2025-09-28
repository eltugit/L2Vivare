package gr.sr.interf.handlers;

import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.handler.SunriseAdminCommand;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public abstract class AdminCommandHandlerInstance
        implements IAdminCommandHandler, SunriseAdminCommand {
    public abstract boolean useAdminCommand(String paramString, PlayerEventInfo paramPlayerEventInfo);

    public final boolean useAdminCommand(String command, L2PcInstance player) {
        return useAdminCommand(command, player.getEventInfo());
    }
}


