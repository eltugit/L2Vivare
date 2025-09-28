package gr.sr.main;


import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;


public class PacketsDebugger {
    protected static final Logger _log = LoggerFactory.getLogger(PacketsDebugger.class);
    private static final boolean PACKET_COUNT;
    private static final boolean PACKET_NAMES;
    private static final int PACKETS_INTERVAL;
    private static boolean debugStarted;
    private static int extraCount;
    private static int expectedListSize;
    private static final List<String> list;
    private static ScheduledFuture<?> scheduler;

    public PacketsDebugger() {
    }

    
    public static void checkDebugger(L2GameServerPacket var0) {
        if (PACKET_COUNT) {
            ++extraCount;
            if (PACKET_NAMES && list.size() < expectedListSize) {
                list.add(var0.toString());
            }

            if (!debugStarted) {
                startDebug(true, false);
                return;
            }
        } else if (!PACKET_COUNT && debugStarted) {
            startDebug(false, false);
        }

    }

    private static void startDebug(boolean var0, boolean var1) {
        if (var0) {
            debugStarted = true;
            _log.info(PacketsDebugger.class.getSimpleName() + ": Packets debugging started.");
            startScheduler();
        } else {
            if (PACKET_NAMES) {
                for(int i = 0; i < list.size(); ++i) {
                    try {
                        _log.info(PacketsDebugger.class.getSimpleName() + ": Packet Name sent: " + ((String) list.get(i)).replace("l2r.gameserver.network.serverpackets", ""));
                    } catch (Exception e) {
                        list.clear();
                        startScheduler();
                    }
                }

                list.clear();
            }

            _log.info(PacketsDebugger.class.getSimpleName() + ": Packets sent is this session: " + extraCount);
            extraCount = 0;
        }
    }

    private static void startScheduler() {
        if (scheduler != null) {
            scheduler.cancel(true);
            scheduler = null;
        }

        scheduler = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> {
            startDebug(false, false);
        }, 1000L, (long)(PACKETS_INTERVAL * 1000));
    }

    static {
        PACKET_COUNT = Config.DEBUG_PACKETS_COUNT;
        PACKET_NAMES = Config.DEBUG_PACKETS_NAMES;
        PACKETS_INTERVAL = Config.DEBUG_PACKETS_INTERVAL;
        debugStarted = false;
        extraCount = 0;
        expectedListSize = 200;
        list = new ArrayList();
        scheduler = null;
    }
}
