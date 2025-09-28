package gr.sr.events.engine.main.events;

import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.base.RewardPosition;
import gr.sr.events.engine.main.MainEventManager;

public class TreasureHuntPvp extends TreasureHunt
{
    public TreasureHuntPvp(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Looser, RewardPosition.Tie, RewardPosition.Numbered, RewardPosition.Range, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.OnKill });
        this._allowPvp = true;
    }
}
