package gr.sr.events.engine.html;

import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.NpcData;

public interface IHtmlManager {
    boolean showNpcHtml(PlayerEventInfo paramPlayerEventInfo, NpcData paramNpcData);

    boolean onBypass(PlayerEventInfo paramPlayerEventInfo, String paramString);

    boolean onCbBypass(PlayerEventInfo paramPlayerEventInfo, String paramString);
}


