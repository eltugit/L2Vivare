package gr.sr.interf;

import gr.sr.interf.delegate.FenceData;
import gr.sr.interf.delegate.InstanceData;
import gr.sr.interf.handlers.AdminCommandHandlerInstance;
import gr.sr.l2j.CallBack;
import gr.sr.l2j.IEventOut;
import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.data.xml.impl.DoorData;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.handler.AdminCommandHandler;
import l2r.gameserver.idfactory.IdFactory;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.L2WorldRegion;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.instance.L2DoorInstance;
import l2r.gameserver.model.actor.instance.L2FenceInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.items.type.CrystalType;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.util.Broadcast;
import l2r.util.Rnd;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

public class SunriseOut implements IEventOut {
    public SunriseOut() {
    }

    public void load() {
        CallBack.getInstance().setSunriseOut(this);
    }

    public ScheduledFuture<?> scheduleGeneral(Runnable task, long delay) {
        return ThreadPoolManager.getInstance().scheduleGeneral(task, delay);
    }

    public ScheduledFuture<?> scheduleGeneralAtFixedRate(Runnable task, long initial, long delay) {
        return ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(task, initial, delay);
    }

    public void executeTask(Runnable task) {
        ThreadPoolManager.getInstance().executeGeneral(task);
    }

    public void purge() {
        ThreadPoolManager.getInstance().purge();
    }

    public int getNextObjectId() {
        return IdFactory.getInstance().getNextId();
    }

    public int random(int min, int max) {
        return Rnd.get(min, max);
    }

    public int random(int max) {
        return Rnd.get(max);
    }

    public Connection getConnection() {
        try {
            return L2DatabaseFactory.getInstance().getConnection();
        } catch (SQLException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public InstanceData createInstance(String name, int duration, int emptyDestroyTime, boolean isPvp) {
        int instanceId = InstanceManager.getInstance().createDynamicInstance((String)null);
        InstanceManager.getInstance().getInstance(instanceId).setName(name);
        InstanceManager.getInstance().getInstance(instanceId).setAllowSummon(false);
        InstanceManager.getInstance().getInstance(instanceId).setDuration(duration);
        if (emptyDestroyTime > 0) {
            InstanceManager.getInstance().getInstance(instanceId).setEmptyDestroyTime((long)emptyDestroyTime);
        }

        InstanceManager.getInstance().getInstance(instanceId).setPvPInstance(isPvp);
        InstanceManager.getInstance().getInstance(instanceId).disableMessages();
        return new InstanceData(InstanceManager.getInstance().getInstance(instanceId));
    }

    public void addDoorToInstance(int instanceId, int doorId, boolean opened) {
        StatsSet set = new StatsSet();
        set.add(DoorData.getInstance().getDoorTemplate(doorId));
        set.set("default_status", opened ? "open" : "close");
        InstanceManager.getInstance().getInstance(instanceId).addDoor(doorId, set);
    }

    public gr.sr.interf.delegate.DoorData[] getInstanceDoors(int instanceId) {
        List<gr.sr.interf.delegate.DoorData> doors = new LinkedList();
        Iterator var3 = InstanceManager.getInstance().getInstance(instanceId).getDoors().iterator();

        while(var3.hasNext()) {
            L2DoorInstance d = (L2DoorInstance)var3.next();
            doors.add(new gr.sr.interf.delegate.DoorData(d));
        }

        return (gr.sr.interf.delegate.DoorData[])doors.toArray(new gr.sr.interf.delegate.DoorData[doors.size()]);
    }

    public void registerAdminHandler(AdminCommandHandlerInstance handler) {
        AdminCommandHandler.getInstance().registerHandler(handler);
    }

    public PlayerEventInfo getPlayer(int playerId) {
        try {
            return L2World.getInstance().getPlayer(playerId).getEventInfo();
        } catch (Exception var3) {
            return null;
        }
    }

    public PlayerEventInfo getPlayer(String name) {
        try {
            return L2World.getInstance().getPlayer(name).getEventInfo();
        } catch (Exception var3) {
            return null;
        }
    }

    public String getClanName(int clanId) {
        try {
            return ClanTable.getInstance().getClan(clanId).getName();
        } catch (Exception var3) {
            return null;
        }
    }

    public String getAllyName(int clanId) {
        try {
            return ClanTable.getInstance().getClan(clanId).getAllyName();
        } catch (Exception var3) {
            return null;
        }
    }

    public void announceToAllScreenMessage(String message, String announcer) {
        Broadcast.toAllOnlinePlayers(new CreatureSay(0, 18, "", announcer + ": " + message));
    }

    public String getHtml(String path) {
        NpcHtmlMessage html = new NpcHtmlMessage();
        return !html.setFile((L2PcInstance)null, (String)null, path) ? null : html.getHtml();
    }

    public String getEventHtml(String path) {
        return this.getHtml(path);
    }

    public void reloadHtmls() {
        HtmCache.getInstance().reload();
    }

    public String getItemName(int id) {
        try {
            return ItemData.getInstance().getTemplate(id).getName();
        } catch (Exception var3) {
            return "Unknown item";
        }
    }

    public boolean doorExists(int id) {
        return DoorData.getInstance().getDoor(id) != null;
    }

    public FenceData createFence(int type, int width, int length, int x, int y, int z, int eventId) {
        return new FenceData(new L2FenceInstance(this.getNextObjectId(), type, width, length, x, y, z, eventId));
    }

    public void spawnFences(List<FenceData> list, int instance) {
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            FenceData fence = (FenceData)var3.next();
            if (fence.getOwner() != null) {
                if (instance > 0) {
                    fence.getOwner().setInstanceId(instance);
                }

                fence.getOwner().spawnMe(fence.getOwner().getXLoc(), fence.getOwner().getYLoc(), fence.getOwner().getZLoc());
            }
        }

    }

    public void unspawnFences(List<FenceData> list) {
        Iterator var2 = list.iterator();

        while(var2.hasNext()) {
            FenceData fence = (FenceData)var2.next();
            if (fence != null) {
                L2WorldRegion region = fence.getOwner().getWorldRegion();
                fence.getOwner().decayMe();
                if (region != null) {
                    region.removeVisibleObject(fence.getOwner());
                }

                fence.getOwner().getKnownList().removeAllKnownObjects();
                L2World.getInstance().removeObject(fence.getOwner());
            }
        }

    }

    public int getGradeFromFirstLetter(String s) {
        if (!s.equalsIgnoreCase("n") && !s.equalsIgnoreCase("ng") && !s.equalsIgnoreCase("no")) {
            if (s.equalsIgnoreCase("d")) {
                return CrystalType.D.getId();
            } else if (s.equalsIgnoreCase("c")) {
                return CrystalType.C.getId();
            } else if (s.equalsIgnoreCase("b")) {
                return CrystalType.B.getId();
            } else if (s.equalsIgnoreCase("a")) {
                return CrystalType.A.getId();
            } else if (s.equalsIgnoreCase("s")) {
                return CrystalType.S.getId();
            } else if (s.equalsIgnoreCase("s80")) {
                return CrystalType.S80.getId();
            } else {
                return s.equalsIgnoreCase("s84") ? CrystalType.S84.getId() : 0;
            }
        } else {
            return CrystalType.NONE.getId();
        }
    }

    public Set<Integer> getAllWeaponsId() {
        return ItemData.getInstance().getAllWeaponsId();
    }

    public Set<Integer> getAllArmorsId() {
        return ItemData.getInstance().getAllArmorsId();
    }

    public Integer[] getAllClassIds() {
        List<Integer> idsList = new LinkedList();
        ClassId[] var2 = ClassId.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            ClassId id = var2[var4];
            idsList.add(id.getId());
        }

        return (Integer[])idsList.toArray(new Integer[idsList.size()]);
    }

    public PlayerEventInfo[] getAllPlayers() {
        List<PlayerEventInfo> eventInfos = new LinkedList();
        Iterator var2 = L2World.getInstance().getPlayers().iterator();

        while(var2.hasNext()) {
            L2PcInstance player = (L2PcInstance)var2.next();
            eventInfos.add(player.getEventInfo());
        }

        return (PlayerEventInfo[])eventInfos.toArray(new PlayerEventInfo[eventInfos.size()]);
    }

    protected static final SunriseOut getInstance() {
        return SunriseOut.SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final SunriseOut _instance = new SunriseOut();

        private SingletonHolder() {
        }
    }
}
