package gr.sr.l2j;

import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.DoorData;
import gr.sr.interf.delegate.FenceData;
import gr.sr.interf.delegate.InstanceData;
import gr.sr.interf.handlers.AdminCommandHandlerInstance;

import java.sql.Connection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

public interface IEventOut {
    ScheduledFuture<?> scheduleGeneral(Runnable paramRunnable, long paramLong);

    ScheduledFuture<?> scheduleGeneralAtFixedRate(Runnable paramRunnable, long paramLong1, long paramLong2);

    void executeTask(Runnable paramRunnable);

    void purge();

    int getNextObjectId();

    int random(int paramInt1, int paramInt2);

    int random(int paramInt);

    Connection getConnection();

    InstanceData createInstance(String paramString, int paramInt1, int paramInt2, boolean paramBoolean);

    void addDoorToInstance(int paramInt1, int paramInt2, boolean paramBoolean);

    DoorData[] getInstanceDoors(int paramInt);

    void registerAdminHandler(AdminCommandHandlerInstance paramAdminCommandHandlerInstance);

    String getClanName(int paramInt);

    String getAllyName(int paramInt);

    PlayerEventInfo getPlayer(int paramInt);

    PlayerEventInfo getPlayer(String paramString);

    Integer[] getAllClassIds();

    PlayerEventInfo[] getAllPlayers();

    void announceToAllScreenMessage(String paramString1, String paramString2);

    String getHtml(String paramString);

    String getEventHtml(String paramString);

    void reloadHtmls();

    boolean doorExists(int paramInt);

    FenceData createFence(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);

    void spawnFences(List<FenceData> paramList, int paramInt);

    void unspawnFences(List<FenceData> paramList);

    int getGradeFromFirstLetter(String paramString);

    String getItemName(int paramInt);

    Set<Integer> getAllWeaponsId();

    Set<Integer> getAllArmorsId();
}


