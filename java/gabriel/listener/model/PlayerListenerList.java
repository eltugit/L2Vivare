package gabriel.listener.model;


import gabriel.listener.actor.player.*;
import gabriel.listener.commons.Listener;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public class PlayerListenerList extends CharListenerList {
    public PlayerListenerList(L2PcInstance actor) {
        super(actor);
    }

    @Override
    public L2PcInstance getActor() {
        return (L2PcInstance) actor;
    }

    public void onEnter() {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnPlayerEnterListener.class.isInstance(listener))
                    ((OnPlayerEnterListener) listener).onPlayerEnter(getActor());

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnPlayerEnterListener.class.isInstance(listener))
                    ((OnPlayerEnterListener) listener).onPlayerEnter(getActor());
    }

    public void onExit() {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnPlayerExitListener.class.isInstance(listener))
                    ((OnPlayerExitListener) listener).onPlayerExit(getActor());

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnPlayerExitListener.class.isInstance(listener))
                    ((OnPlayerExitListener) listener).onPlayerExit(getActor());
    }

    public void onTeleport(int x, int y, int z, int reflection) {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnTeleportListener.class.isInstance(listener))
                    ((OnTeleportListener) listener).onTeleport(getActor(), x, y, z, reflection);

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnTeleportListener.class.isInstance(listener))
                    ((OnTeleportListener) listener).onTeleport(getActor(), x, y, z, reflection);
    }

    public void onPartyInvite() {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnPlayerPartyInviteListener.class.isInstance(listener))
                    ((OnPlayerPartyInviteListener) listener).onPartyInvite(getActor());

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnPlayerPartyInviteListener.class.isInstance(listener))
                    ((OnPlayerPartyInviteListener) listener).onPartyInvite(getActor());
    }

    public void onPartyLeave() {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnPlayerPartyLeaveListener.class.isInstance(listener))
                    ((OnPlayerPartyLeaveListener) listener).onPartyLeave(getActor());

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnPlayerPartyLeaveListener.class.isInstance(listener))
                    ((OnPlayerPartyLeaveListener) listener).onPartyLeave(getActor());
    }

    public void onQuestionMarkClicked(int questionMarkId) {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnQuestionMarkListener.class.isInstance(listener))
                    ((OnQuestionMarkListener) listener).onQuestionMarkClicked(getActor(), questionMarkId);

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnQuestionMarkListener.class.isInstance(listener))
                    ((OnQuestionMarkListener) listener).onQuestionMarkClicked(getActor(), questionMarkId);
    }
}
