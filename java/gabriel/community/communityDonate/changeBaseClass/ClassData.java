package gabriel.community.communityDonate.changeBaseClass;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

import java.util.HashMap;
import java.util.Map;

public class ClassData {
    private Map<Integer, String> human = new HashMap<>();
    private Map<Integer, String> elf = new HashMap<>();
    private Map<Integer, String> delf = new HashMap<>();
    private Map<Integer, String> orc = new HashMap<>();
    private Map<Integer, String> dwarf = new HashMap<>();
    private Map<Integer, String> kamael = new HashMap<>();

    public Map<Integer, String> getHuman() {
        return human;
    }

    public Map<Integer, String> getElf() {
        return elf;
    }

    public Map<Integer, String> getDelf() {
        return delf;
    }

    public Map<Integer, String> getOrc() {
        return orc;
    }

    public Map<Integer, String> getDwarf() {
        return dwarf;
    }

    public Map<Integer, String> getKamael() {
        return kamael;
    }

    private ClassData() {
        human.put(88, "Duelist");
        human.put(89, "Dreadnought");
        human.put(90, "Phoenix Knight");
        human.put(91, "Hell Knight");
        human.put(93, "Adventurer");
        human.put(92, "Sagittarius");
        human.put(94, "Archmage");
        human.put(95, "Soultaker");
        human.put(96, "Arcana Lord");
        human.put(97, "Cardinal");
        human.put(98, "Hierophant");


        elf.put(99, "Eva's Templar");
        elf.put(100, "Sword Muse");
        elf.put(101, "Wind Rider");
        elf.put(102, "Moonlight Sentinel");
        elf.put(103, "Mystic Muse");
        elf.put(104, "Elemental Master");
        elf.put(105, "Eva's Saint");


        delf.put(106, "Shillien Templar");
        delf.put(107, "Spectral Dancer");
        delf.put(108, "Ghost Hunter");
        delf.put(109, "Ghost Sentinel");
        delf.put(110, "Storm Screamer");
        delf.put(111, "Spectral Master");
        delf.put(112, "Shillien Saint");

        orc.put(113, "Titan");
        orc.put(114, "G.Khavatari");
        orc.put(115, "Dominator");
        orc.put(116, "Doomcryer");

        dwarf.put(117, "Maestro");
        dwarf.put(118, "Fortune Seeker");

        //Male
        kamael.put(131, "Doombringer");
        kamael.put(132, "M.Soul Hound");
        //Female
        kamael.put(133, "F.Soul Hound");
        kamael.put(134, "Trickster");

    }


    protected static ClassData instance;

    public static ClassData getInstance() {
        if (instance == null)
            instance = new ClassData();
        return instance;
    }

}
