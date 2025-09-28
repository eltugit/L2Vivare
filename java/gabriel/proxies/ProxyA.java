package gabriel.proxies;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class ProxyA {

    public static Map<Integer, ProxyB> a;


    public static List<ProxyB> a(final int n) {
        final ArrayList<ProxyB> list = new ArrayList<ProxyB>();
        for (final ProxyB b : a.values()) {
            if (b.proxyId == n) {
                list.add(b);
            }
        }
        return list;
    }

    public static boolean b(final int serverId) {
        boolean has = false;

        for (ProxyB value : a.values()) {
            if (value.serverId == serverId) {
                has = true;
                break;
            }
        }

        return has;
    }


    public static int size() {
        return a.size();
    }

    static {
        a = new HashMap<Integer, ProxyB>();
    }
}
