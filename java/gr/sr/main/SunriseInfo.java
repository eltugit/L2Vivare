package gr.sr.main;


import gr.sr.network.handler.ServerTypeConfigs;
import gr.sr.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static l2r.gameserver.GameServer.printSection;


public class SunriseInfo {
    private static final Logger log = LoggerFactory.getLogger(SunriseInfo.class);
    private static final String protocol;
    private static final String[] LOGO;

    public SunriseInfo() {
    }

    
    public static void load() {
        log.info("=====================================================");
        log.info("Copyrights: .............: Sunrise - Coding Team - Gabriel Costa Souza");
        log.info("Years: ..........     ...: 2015-2020");
        log.info("Project Owners: .........: vGodFather - vNeverMore - Gabriel Costa Souza");
        log.info("Core Devs: ..............: vGodFather - vNeverMore - Gabriel Costa Souza");
        log.info("Data Devs: ..............: vGodFather - vNeverMore - Gabriel Costa Souza");
        log.info("Engine Devs: ............: vGodFather - vNeverMore - Gabriel Costa Souza");
        log.info("Custom Devs: ............: Gabriel Costa Souza");
        log.info("Custom Devs: ............: This addon packed was made by Gabriel Costa Souza for Elton Santos.");
        log.info("Custom Devs: ............: Discord: Gabriel 'GCS'#2589");
        log.info("Custom Devs: ............: Skype - email: gabriel_costa25@hotmail.com");
        log.info("Chronicle: ..............: High Five Part 5 (CT2.6)");
        log.info("Protocols: ..............: " + protocol);
        log.info("Core Revision: ..........: ver. 1044");
        log.info("Data Revision: ..........: ver. 911");
        log.info("Engine Revision: ........: ver. 115 (Gabriel Updates)");
        log.info("Events Revision: ........: ver. 42 (Gabriel Updates)");
        log.info("Protocol Revision: ......: ver. 52");
        log.info("Sunrise Revision: .......: ver. 1044.911.115.42.52");
        printMemUsage();
        printLogo(LOGO);
        log.info("=====================================================");
    }

    public static void printMemUsage() {
        double maxMem = (double)(Runtime.getRuntime().maxMemory() / 1024L / 1024L);
        double totalMem = (double)(Runtime.getRuntime().totalMemory() / 1024L / 1024L);
        double nonAllocMem = maxMem - totalMem;
        double freeMem = (double)(Runtime.getRuntime().freeMemory() / 1024L / 1024L);
        double remainingMem = totalMem - freeMem;
        double usableMem = maxMem - remainingMem;
        SimpleDateFormat format = new SimpleDateFormat("H:mm:ss");
        DecimalFormat percentFormat = new DecimalFormat(" (0.0000'%')");
        DecimalFormat mbFormat = new DecimalFormat(" # 'MB'");
        String[] toShowString = new String[]{"+----", "| Global Memory Informations at " + format.format(new Date()) + ":", "|    |", "| Allowed Memory:" + mbFormat.format(maxMem), "|    |= Allocated Memory:" + mbFormat.format(totalMem) + percentFormat.format(totalMem / maxMem * 100.0D), "|    |= Non-Allocated Memory:" + mbFormat.format(nonAllocMem) + percentFormat.format(nonAllocMem / maxMem * 100.0D), "| Allocated Memory:" + mbFormat.format(totalMem), "|    |= Used Memory:" + mbFormat.format(remainingMem) + percentFormat.format(remainingMem / maxMem * 100.0D), "|    |= Unused (cached) Memory:" + mbFormat.format(freeMem) + percentFormat.format(freeMem / maxMem * 100.0D), "| Useable Memory:" + mbFormat.format(usableMem) + percentFormat.format(usableMem / maxMem * 100.0D), "+----"};

        for(int i = 0; i < 11; ++i) {
            String s = toShowString[i];
            log.info(s);
        }

        printSection("This addon packed was made by Gabriel Costa Souza for Elton Santos.\n" +
                "/**\n" +
                " * @author Gabriel Costa Souza\n" +
                " * Discord: Gabriel 'GCS'#2589\n" +
                " * Skype - email: gabriel_costa25@hotmail.com\n" +
                " */");

    }

    public static final void printLogo(String[] logo) {
        int count;
        if (logo.length > 0) {
            count = logo.length;

            for(int i = 0; i < count; ++i) {
                if (logo[i].equals("nologo")) {
                    return;
                }
            }
        }

        logo = LOGO;

        for(int i = 0; i < 23; ++i) {
            String line = logo[i];
            count = 0;

            while(count < line.length()) {
                char var4 = line.charAt(count);
                System.out.print(var4);
                switch(var4) {
                    default:
                        Tools.sleep(5L);
                    case '\n':
                    case ' ':
                        ++count;
                }
            }
        }

        Tools.sleep(2000L);
    }

    static {
        protocol = ServerTypeConfigs.SERVER_TYPE.getProtocols().toString();
        LOGO = new String[]{"########################################################\n",
                "#           #####                  #####################\n",
                "#          #####   ########         ####################\n",
                "#         #####   ########           ###################\n",
                "#        #####   ##    ##             ##################\n",
                "#       #####         ##               ###           ###\n",
                "#      #####         ##                 ### Lineage2 ###\n",
                "#     #####         ##                   ###  Java   ###\n",
                "#    #####         #########              ### Server ###\n",
                "#   #####         #########                ###       ###\n",
                "#  ##############                           ############\n",
                "# ############## ########                    ###########\n",
                "#                                             ##########\n",
                "#                                              #########\n",
                "#                                               ########\n",
                "#                                                #######\n",
                "#                                                 ######\n",
                "#                                                  #####\n",
                "#                                                   ####\n",
                "#                                                    ###\n",
                "#                                                     ##\n",
                "# www.L2jSunrise.com                                   #\n",
                "########################################################\n"};
    }
}
