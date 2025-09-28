package gr.sr.player;

import l2r.gameserver.data.xml.impl.TransformData;
import l2r.gameserver.enums.Race;
import l2r.gameserver.enums.Team;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.appearance.PcAppearance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.transform.Transform;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.effects.AbnormalEffect;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.network.L2GameClient;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;
import l2r.log.filter.Log;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

public class PlayerData {
    public Future<?> _exitZoneTask = null;
    public Future<?> _fireZoneDeflagTask = null;
    public ScheduledFuture<?> _antiFeedRemoveTask = null;
    private PlayerData a;
    protected final L2PcInstance _owner;

    public PlayerData(L2PcInstance var1) {
        this._owner = var1;
        this.a = this;
    }

    public L2PcInstance getPlayer() {
        return this._owner;
    }

    public int getObjectId() {
        return this._owner.getObjectId();
    }

    public int getLevel() {
        return this._owner.getLevel();
    }

    public String getAccountName() {
        return this._owner.getAccountName();
    }

    public void setVisibleTitle(String var1) {
        this._owner.getAppearance().setVisibleTitle(var1);
    }

    public void restoreTitle() {
        this._owner.getAppearance().setVisibleTitle(this._owner.getTitle());
    }

    public Team getTeam() {
        return this._owner.getTeam();
    }

    public void setTeam(Team var1) {
        this._owner.setTeam(var1);
    }

    public boolean isOnline() {
        return this._owner.isOnline();
    }

    public boolean isDead() {
        return this._owner.isDead();
    }

    public ClassId getClassId() {
        return this._owner.getClassId();
    }

    public void setPvpFlag(int val) {
        this._owner.setPvpFlag(val);
    }

    public void startFlag() {
        this._owner.startFlag();
    }

    public void sendMessage(String msg) {
        this._owner.sendMessage(msg);
    }

    public void sendMessageS(String msg, int timeonscreenins) {
        this._owner.sendMessageS(msg, timeonscreenins);
    }

    public void teleToLocation(Location var1) {
        Log.info("", new IllegalArgumentException());
        this._owner.teleToLocation(var1);
    }

    public boolean hasSummon() {
        return this._owner.hasSummon();
    }

    public L2Summon getSummon() {
        return this._owner.getSummon();
    }

    public void removeSummons() {
        if (this._owner.hasSummon()) {
            this.getSummon().unSummon(this._owner);
        }

    }

    public void setInsideZone(ZoneIdType var1, boolean var2) {
        this._owner.setInsideZone(var1, var2);
    }

    public Inventory getInventory() {
        return this._owner.getInventory();
    }

    public void stopPvpRegTask() {
        this._owner.stopPvpRegTask();
    }

    public void setVar(String var1, String var2) {
        this._owner.setVar(var1, var2);
    }

    public boolean isHero() {
        return this._owner.isHero();
    }

    public void setHero(boolean var1) {
        this._owner.setHero(var1);
    }

    public String getName() {
        return this._owner.getName();
    }

    public void broadcastInfo() {
        this._owner.broadcastInfo();
    }

    public void removeReviving() {
        this._owner.removeReviving();
    }

    public void doRevive() {
        this._owner.doRevive();
    }

    public void addItem(String var1, int var2, long var3, PlayerData var5, boolean var6) {
        this._owner.addItem(var1, var2, var3, var5.getPlayer(), var6);
    }

    public void setIsParalyzed(boolean var1) {
        this._owner.setIsParalyzed(var1);
    }

    public void setIsInvul(boolean var1) {
        this._owner.setIsInvul(var1);
    }

    public void stopAbnormalEffect(AbnormalEffect var1) {
        this._owner.stopAbnormalEffect(var1);
    }

    public void startAbnormalEffect(AbnormalEffect var1) {
        this._owner.startAbnormalEffect(var1);
    }

    public void teleToLocation(int var1, int var2, int var3) {
        this._owner.teleToLocation(var1, var2, var3);
    }

    public boolean isPlayer() {
        return this._owner.isPlayer();
    }

    public L2Party getParty() {
        return this._owner.getParty();
    }

    public void leaveParty() {
        this._owner.leaveParty();
    }

    public Race getRace() {
        return this._owner.getRace();
    }

    public PcAppearance getAppearance() {
        return this._owner.getAppearance();
    }

    public void startAntifeedProtection(boolean var1) {
        this._owner.startAntifeedProtection(var1);
    }

    public void untransform() {
        this._owner.untransform();
    }

    public int getMaxHp() {
        return this._owner.getMaxHp();
    }

    public int getMaxCp() {
        return this._owner.getMaxCp();
    }

    public int getMaxMp() {
        return this._owner.getMaxMp();
    }

    public void setCurrentHpMp(int var1, int var2) {
        this._owner.setCurrentHpMp((double) var1, (double) var2);
    }

    public void setCurrentCp(int var1) {
        this._owner.setCurrentCp((double) var1);
    }

    public boolean isPremium() {
        return this._owner.isPremium();
    }

    public PlayerData getPreviousVictim() {
        return this.a;
    }

    public void setPreviousVictim(PlayerData var1) {
        this.a = var1;
    }

    public int getQuickVarI(String var1, int var2) {
        return this._owner.getQuickVarI(var1, new int[]{var2});
    }

    public void setQuickVar(String var1, int var2) {
        this._owner.setQuickVar(var1, var2);
    }

    public boolean getVarB(String var1, boolean var2) {
        return this._owner.getVarB(var1, var2);
    }

    public void unsetVar(String var1) {
        this._owner.unsetVar(var1);
    }

    public L2GameClient getClient() {
        return this._owner.getClient();
    }

    public void broadcastPacket(L2GameServerPacket var1) {
        this._owner.broadcastPacket(var1);
    }

    public void broadcastUserInfo() {
        this._owner.broadcastUserInfo();
    }

    public void sendPacket(L2GameServerPacket var1) {
        this._owner.sendPacket(var1);
    }

    public L2PcInstance getActingPlayer() {
        return this._owner.getActingPlayer();
    }

    public boolean isFriend(PlayerData var1) {
        return this._owner.isFriend(var1.getActingPlayer());
    }

    public boolean isInsideRadius(PlayerData var1, int var2, boolean var3, boolean var4) {
        return this._owner.isInsideRadius(var1.getPlayer(), var2, var3, var4);
    }

    public boolean isGM() {
        return this._owner.isGM();
    }

    public int getPvpFlag() {
        return this._owner.getPvpFlag();
    }

    public int getTransformationId() {
        return this._owner.getTransformationId();
    }

    public void transform(int var1) {
        Transform var2;
        if ((var2 = TransformData.getInstance().getTransform(var1)) != null) {
            this._owner.transform(var2);
        }

    }

    public void unTransform(int var1) {
        this._owner.untransform();
    }
}
