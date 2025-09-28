package gabriel.listener.model;


import gabriel.listener.actor.npc.OnDecayListener;
import gabriel.listener.actor.npc.OnSpawnListener;
import gabriel.listener.commons.Listener;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2NpcInstance;

public class NpcListenerList extends CharListenerList {
    public NpcListenerList(L2NpcInstance actor) {
        super(actor);
    }

    @Override
    public L2NpcInstance getActor() {
        return (L2NpcInstance) actor;
    }

    public void onSpawn() {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnSpawnListener.class.isInstance(listener))
                    ((OnSpawnListener) listener).onSpawn(getActor());

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnSpawnListener.class.isInstance(listener))
                    ((OnSpawnListener) listener).onSpawn(getActor());
    }

    public void onDecay() {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnDecayListener.class.isInstance(listener))
                    ((OnDecayListener) listener).onDecay(getActor());

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnDecayListener.class.isInstance(listener))
                    ((OnDecayListener) listener).onDecay(getActor());
    }
}
