package gabriel.proxies;


/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class ProxyB {
    public int serverId;
    public int proxyId;
    public byte[] ips;

    public ProxyB(final int serverId, final byte[] ips, final int proxyId) {
        this.serverId = serverId;
        this.ips = ips;
        this.proxyId = proxyId;
    }
}
