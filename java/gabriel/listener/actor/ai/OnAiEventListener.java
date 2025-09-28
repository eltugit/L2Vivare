package gabriel.listener.actor.ai;


import gabriel.listener.AiListener;
import l2r.gameserver.enums.CtrlEvent;
import l2r.gameserver.model.actor.L2Character;

public interface OnAiEventListener extends AiListener {
    public void onAiEvent(L2Character actor, CtrlEvent evt, Object[] args);
}
