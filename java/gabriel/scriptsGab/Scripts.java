package gabriel.scriptsGab;


import gabriel.scriptsGab.donatesystem.Donations;
import gabriel.scriptsGab.forge.Forge;
import l2r.gameserver.model.actor.instance.L2PcInstance;


public class Scripts {

    protected static Scripts instance;


    public static Scripts getInstance() {
        if (instance == null)
            instance = new Scripts();
        return instance;
    }


    public void parseCommand(String _command, L2PcInstance player) {
        String command = _command.substring(8).trim();
        String[] word = command.split("\\s+");
        String[] argsr = command.substring(word[0].length()).trim().split("\\s+");
        String[] path = word[0].split(":");
        String file = path[0];
        String method = path[1];

        switch (file) {
            case "Donations":
                Donations.getInstance().parseCommand(method, argsr, player);
                break;
            case "_bbsforge":
                Forge.getInstance().parseCommand(command, player);
                break;
        }


    }


}
