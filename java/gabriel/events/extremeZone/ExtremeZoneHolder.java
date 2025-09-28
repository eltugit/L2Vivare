package gabriel.events.extremeZone;

import gr.sr.data.xml.AbstractHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class ExtremeZoneHolder extends AbstractHolder {
    private static final ExtremeZoneHolder _instance = new ExtremeZoneHolder();

    public static ExtremeZoneHolder getInstance() {
        return _instance;
    }

    private final List<ExtremeZone> _raids = new ArrayList<>();

    public void addRaid(ExtremeZone raid) {
        _raids.add(raid);
    }

    public List<ExtremeZone> getAllExtremes() {
        return _raids;
    }

    public ExtremeZone getRaid(int id) {
        for (ExtremeZone raid : _raids) {
            if (raid.getNpcId() == id) {
                return raid;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return _raids.size();
    }

    @Override
    public void clear() {
        _raids.clear();
    }
}
