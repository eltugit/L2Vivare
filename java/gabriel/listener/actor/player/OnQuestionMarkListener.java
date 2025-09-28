package gabriel.listener.actor.player;


import gabriel.listener.PlayerListener;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public interface OnQuestionMarkListener extends PlayerListener {

    public void onQuestionMarkClicked(L2PcInstance player, int questionMarkId);
}
