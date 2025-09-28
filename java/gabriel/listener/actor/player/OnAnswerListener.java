package gabriel.listener.actor.player;


import gabriel.listener.PlayerListener;


public interface OnAnswerListener extends PlayerListener {

    void sayYes();

    void sayNo();
}
