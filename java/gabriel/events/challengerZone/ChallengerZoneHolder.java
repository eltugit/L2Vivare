package gabriel.events.challengerZone;

import gr.sr.data.xml.AbstractHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class ChallengerZoneHolder extends AbstractHolder {
    private static final ChallengerZoneHolder _instance = new ChallengerZoneHolder();

    public static ChallengerZoneHolder getInstance() {
        return _instance;
    }

    private final List<ChallengerZone> _raids = new ArrayList<>();

    public void addRaid(ChallengerZone raid) {
        _raids.add(raid);
    }

    public List<ChallengerZone> getAllExtremes() {
        return _raids;
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
