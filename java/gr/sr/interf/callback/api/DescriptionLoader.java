package gr.sr.interf.callback.api;

import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.interf.callback.api.descriptions.*;

public class DescriptionLoader {
    public static void load() {
        EventDescriptionSystem.getInstance().addDescription(EventType.Battlefields, (EventDescription) new BattlefieldsDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.CTF, (EventDescription) new CTFDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.DM, (EventDescription) new DMDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.Domination, (EventDescription) new DominationDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.HuntingGround, (EventDescription) new HuntingGroundsDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.LastMan, (EventDescription) new LMSDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.LuckyChests, (EventDescription) new LuckyChestsDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.MassDomination, (EventDescription) new MassDominationDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.Mutant, (EventDescription) new MutantDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.TreasureHunt, (EventDescription) new TreasureHuntDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.TreasureHuntPvp, (EventDescription) new TreasureHuntVipDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.TvT, (EventDescription) new TvTDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.TvTAdv, (EventDescription) new TvTAdvancedDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.Zombies, (EventDescription) new ZombiesDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.Classic_1v1, (EventDescription) new SinglePlayersFightsDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.PartyvsParty, (EventDescription) new PartyFightsDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.Korean, (EventDescription) new KoreanDescription());
        EventDescriptionSystem.getInstance().addDescription(EventType.MiniTvT, (EventDescription) new MiniTvTDescription());
    }
}


