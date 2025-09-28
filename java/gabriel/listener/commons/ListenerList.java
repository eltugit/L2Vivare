package gabriel.listener.commons;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @param <T>
 * @author G1ta0
 */
public class ListenerList<T> {
    protected Set<Listener<T>> listeners = new CopyOnWriteArraySet<Listener<T>>();

    public Collection<Listener<T>> getListeners() {
        return listeners;
    }

    /**
     * @param listener
     * @return true
     */
    public boolean add(Listener<T> listener) {
        return listeners.add(listener);
    }

    /**
     * @param listener
     * @return true
     */
    public boolean remove(Listener<T> listener) {
        return listeners.remove(listener);
    }

}
