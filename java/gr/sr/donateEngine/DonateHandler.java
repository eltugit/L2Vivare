package gr.sr.donateEngine;


import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.L2GameClient;
import l2r.gameserver.util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.StringTokenizer;

public class DonateHandler {
    private DonateHandler() {
    }

    public static void sendDonateForm(L2PcInstance player, String form) {
        form = form.substring(14);
        StringTokenizer st = new StringTokenizer(form, " ");
        L2GameClient client = player.getClient();

        try {
            String var3 = st.nextToken();
            String var4 = st.nextToken();
            String var5 = st.nextToken();
            String var6 = st.nextToken();
            form = st.nextToken();
            if (!var3.equals("") && !var4.equals("") && !var5.equals("") && !var6.equals("") && !form.equals("")) {
                if (var3.length() <= 4 && var4.length() <= 4 && var5.length() <= 4 && var6.length() <= 4) {
                    if (Util.isDigit(var3) && Util.isDigit(var4) && Util.isDigit(var5) && Util.isDigit(var6) && Util.isDigit(form)) {
                        String file = "data/sunrise/donates/" + player.getName() + ".txt";
                        if (!(new File(file)).createNewFile()) {
                            player.sendMessage("You have already sent a donate form to a gm, he must check it first.");
                        } else {
                            FileWriter fileWriter = new FileWriter(file);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            bufferedWriter.write("Character Info: " + client + "\r\nPaysafe Pin: " + var3 + "-" + var4 + "-" + var5 + "-" + var6 + "\r\nAmount: " + form + "\r");
                            player.sendMessage("Done, wait for a gm to check and apply your donation form. We will contact with you soon.");
                            bufferedWriter.close();
                        }
                    } else {
                        player.sendMessage("Pin code and amount can only contain numbers.");
                    }
                } else {
                    player.sendMessage("Pin boxes cannot contain more than 4 digits.");
                }
            } else {
                player.sendMessage("Complete all the fields please.");
            }
        } catch (Exception e) {
            player.sendMessage("Cannot send an empty donate form.");
        }
    }
}
