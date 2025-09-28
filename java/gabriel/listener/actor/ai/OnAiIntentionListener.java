package gabriel.listener.actor.ai;


import gabriel.listener.AiListener;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.actor.L2Character;

public interface OnAiIntentionListener extends AiListener {
    public void onAiIntention(L2Character actor, CtrlIntention intention, Object arg0, Object arg1);
}
