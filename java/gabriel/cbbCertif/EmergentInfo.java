package gabriel.cbbCertif;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class EmergentInfo {
    private final int _id;
    private final String _name;
    private final String _icon;
    private final String[] _rate;

    EmergentInfo(int id, String name, String icon, String[] rate) {
        _id = id;
        _name = name;
        _icon = icon;
        _rate = rate;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getIcon() {
        return _icon;
    }

    public String getRates(int val) {
        return _rate[val];
    }
}
