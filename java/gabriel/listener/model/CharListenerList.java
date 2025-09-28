package gabriel.listener.model;


import gabriel.listener.actor.*;
import gabriel.listener.actor.ai.OnAiEventListener;
import gabriel.listener.actor.ai.OnAiIntentionListener;
import gabriel.listener.commons.Listener;
import gabriel.listener.commons.ListenerList;
import l2r.gameserver.enums.CtrlEvent;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;

public class CharListenerList extends ListenerList<L2Character> {
    final static ListenerList<L2Character> global = new ListenerList<L2Character>();

    protected final L2Character actor;

    public CharListenerList(L2Character actor) {
        this.actor = actor;
    }

    public L2Character getActor() {
        return actor;
    }

    public final static boolean addGlobal(Listener<L2Character> listener) {
        return global.add(listener);
    }

    public final static boolean removeGlobal(Listener<L2Character> listener) {
        return global.remove(listener);
    }

    public void onAiIntention(CtrlIntention intention, Object arg0, Object arg1) {
        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnAiIntentionListener.class.isInstance(listener))
                    ((OnAiIntentionListener) listener).onAiIntention(getActor(), intention, arg0, arg1);
    }

    public void onAiEvent(CtrlEvent evt, Object[] args) {
        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnAiEventListener.class.isInstance(listener))
                    ((OnAiEventListener) listener).onAiEvent(getActor(), evt, args);
    }

    public void onAttack(L2Character target) {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnAttackListener.class.isInstance(listener))
                    ((OnAttackListener) listener).onAttack(getActor(), target);

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnAttackListener.class.isInstance(listener))
                    ((OnAttackListener) listener).onAttack(getActor(), target);
    }

    public void onAttackHit(L2Character attacker) {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnAttackHitListener.class.isInstance(listener))
                    ((OnAttackHitListener) listener).onAttackHit(getActor(), attacker);

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnAttackHitListener.class.isInstance(listener))
                    ((OnAttackHitListener) listener).onAttackHit(getActor(), attacker);
    }

    public void onMagicUse(L2Skill skill, L2Character target, boolean alt) {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnMagicUseListener.class.isInstance(listener))
                    ((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt);

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnMagicUseListener.class.isInstance(listener))
                    ((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt);
    }

    public void onMagicHit(L2Skill skill, L2Character caster) {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnMagicHitListener.class.isInstance(listener))
                    ((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster);

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnMagicHitListener.class.isInstance(listener))
                    ((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster);
    }

    public void onDeath(L2Character killer) {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnDeathListener.class.isInstance(listener))
                    ((OnDeathListener) listener).onDeath(getActor(), killer);

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnDeathListener.class.isInstance(listener))
                    ((OnDeathListener) listener).onDeath(getActor(), killer);
    }

    public void onKill(L2Character victim) {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnKillListener.class.isInstance(listener) && !((OnKillListener) listener).ignorePetOrSummon())
                    ((OnKillListener) listener).onKill(getActor(), victim);

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnKillListener.class.isInstance(listener) && !((OnKillListener) listener).ignorePetOrSummon())
                    ((OnKillListener) listener).onKill(getActor(), victim);
    }

    public void onKillIgnorePetOrSummon(L2Character victim) {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnKillListener.class.isInstance(listener) && ((OnKillListener) listener).ignorePetOrSummon())
                    ((OnKillListener) listener).onKill(getActor(), victim);

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnKillListener.class.isInstance(listener) && ((OnKillListener) listener).ignorePetOrSummon())
                    ((OnKillListener) listener).onKill(getActor(), victim);
    }

    public void onCurrentHpDamage(double damage, L2Character attacker, L2Skill skill) {
        if (!global.getListeners().isEmpty())
            for (Listener<L2Character> listener : global.getListeners())
                if (OnCurrentHpDamageListener.class.isInstance(listener))
                    ((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill);

        if (!getListeners().isEmpty())
            for (Listener<L2Character> listener : getListeners())
                if (OnCurrentHpDamageListener.class.isInstance(listener))
                    ((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill);
    }
}
