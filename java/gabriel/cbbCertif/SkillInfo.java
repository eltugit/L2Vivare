package gabriel.cbbCertif;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class SkillInfo {
    private final int _id;
    private final String _icon;
    private final String _name;
    private final String _desk1;
    private final String _desk2;

    public SkillInfo(int id, String icon, String name, String desk1, String desk2) {
        _id = id;
        _icon = icon;
        _name = name;
        _desk1 = desk1;
        _desk2 = desk2;
    }

    public int getId() {
        return _id;
    }

    public String getIcon() {
        return _icon;
    }

    public String getName() {
        return _name;
    }

    public String getDesk1() {
        return _desk1;
    }

    public String getDesk2() {
        return _desk2;
    }
}
