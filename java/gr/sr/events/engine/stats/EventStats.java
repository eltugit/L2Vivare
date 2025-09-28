package gr.sr.events.engine.stats;

import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.ShowBoardData;

public abstract class EventStats
{
    public void showHtmlText(final PlayerEventInfo player, final String text) {
        if (text.length() < 4090) {
            ShowBoardData sb = new ShowBoardData(text, "101");
            sb.sendToPlayer(player);
            sb = new ShowBoardData(null, "102");
            sb.sendToPlayer(player);
            sb = new ShowBoardData(null, "103");
            sb.sendToPlayer(player);
        }
        else if (text.length() < 8180) {
            ShowBoardData sb = new ShowBoardData(text.substring(0, 4090), "101");
            sb.sendToPlayer(player);
            sb = new ShowBoardData(text.substring(4090, text.length()), "102");
            sb.sendToPlayer(player);
            sb = new ShowBoardData(null, "103");
            sb.sendToPlayer(player);
        }
        else if (text.length() < 12270) {
            ShowBoardData sb = new ShowBoardData(text.substring(0, 4090), "101");
            sb.sendToPlayer(player);
            sb = new ShowBoardData(text.substring(4090, 8180), "102");
            sb.sendToPlayer(player);
            sb = new ShowBoardData(text.substring(8180, text.length()), "103");
            sb.sendToPlayer(player);
        }
    }
    
    public abstract void load();
    
    public abstract void onLogin(final PlayerEventInfo p0);
    
    public abstract void onDisconnect(final PlayerEventInfo p0);
    
    public abstract void onCommand(final PlayerEventInfo p0, final String p1);
    
    public abstract void statsChanged(final PlayerEventInfo p0);
}
