package gr.sr.events.engine.mini;

import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.features.AbstractFeature;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.CallBack;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EventMode implements Runnable
{
    private final EventType _event;
    private boolean _gmAllowed;
    private String _name;
    private String _visibleName;
    private int _npcId;
    private final List<AbstractFeature> _features;
    private final List<Integer> _disallowedMaps;
    private final ScheduleInfo _scheduleInfo;
    private boolean _running;
    private ScheduledFuture<?> _future;
    
    public EventMode(final EventType event) {
        this._features = new LinkedList<AbstractFeature>();
        this._disallowedMaps = new LinkedList<Integer>();
        this._event = event;
        this._name = "Default";
        this._visibleName = this._name;
        this._features.clear();
        this._disallowedMaps.clear();
        this._scheduleInfo = new ScheduleInfo(this._event, this._name);
        this.refreshScheduler();
    }
    
    @Override
    public void run() {
        if (this._running) {
            this._running = false;
            final MiniEventManager manager = EventManager.getInstance().getMiniEvent(this._event, this.getModeId());
            if (manager != null) {
                manager.cleanMe(false);
            }
            this.scheduleRun();
        }
        else {
            this._running = true;
            this.scheduleStop();
        }
    }
    
    public void refreshScheduler() {
        if (this.isNonstopRun()) {
            this._running = true;
            return;
        }
        if (this._running) {
            boolean running = false;
            for (final ScheduleInfo.RunTime time : this._scheduleInfo.getTimes().values()) {
                if (time.isActual()) {
                    running = true;
                    this.run();
                }
            }
            if (running) {
                this.scheduleStop();
            }
            else {
                this.run();
            }
        }
        else {
            boolean running = false;
            for (final ScheduleInfo.RunTime time : this._scheduleInfo.getTimes().values()) {
                if (time.isActual()) {
                    running = true;
                    this.run();
                }
            }
            if (!running) {
                this.scheduleRun();
            }
        }
    }
    
    public void scheduleRun() {
        final long runTime = this._scheduleInfo.getNextStart(false);
        if (!this.isNonstopRun() && runTime > -1L) {
            this._future = CallBack.getInstance().getOut().scheduleGeneral(this, runTime);
        }
        else {
            this._running = true;
        }
    }
    
    public void scheduleStop() {
        final long endTime = this._scheduleInfo.getEnd(false);
        if (!this.isNonstopRun() && endTime != -1L) {
            this._future = CallBack.getInstance().getOut().scheduleGeneral(this, endTime);
        }
    }
    
    public boolean isNonstopRun() {
        return this._scheduleInfo.isNonstopRun();
    }
    
    public List<AbstractFeature> getFeatures() {
        return this._features;
    }
    
    public void addFeature(final PlayerEventInfo gm, final FeatureType type, final String parameters) {
        Constructor<?> _constructor = null;
        AbstractFeature feature = null;
        final Class[] classParams = { EventType.class, PlayerEventInfo.class, String.class };
        try {
            _constructor = Class.forName("gr.sr.events.engine.mini.features." + type.toString() + "Feature").getConstructor((Class<?>[])classParams);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            final Object[] objectParams = { this._event, gm, parameters };
            final Object tmp = _constructor.newInstance(objectParams);
            feature = (AbstractFeature)tmp;
            this._features.add(feature);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void addFeature(final AbstractFeature feature) {
        this._features.add(feature);
    }
    
    public boolean checkPlayer(final PlayerEventInfo player) {
        for (final AbstractFeature feature : this._features) {
            if (!feature.checkPlayer(player)) {
                return false;
            }
        }
        return true;
    }
    
    public long getFuture() {
        return (this._future == null) ? -1L : this._future.getDelay(TimeUnit.MILLISECONDS);
    }
    
    public List<Integer> getDisMaps() {
        return this._disallowedMaps;
    }
    
    public String getModeName() {
        return this._name;
    }
    
    public String getVisibleName() {
        if (this._visibleName == null || this._visibleName.length() == 0) {
            return this._name;
        }
        return this._visibleName;
    }
    
    public int getNpcId() {
        return this._npcId;
    }
    
    public void setNpcId(final int id) {
        this._npcId = id;
    }
    
    public void setVisibleName(final String name) {
        this._visibleName = name;
    }
    
    public void setModeName(final String s) {
        this._name = s;
    }
    
    public boolean isAllowed() {
        return this._gmAllowed;
    }
    
    public boolean isRunning() {
        return this._running;
    }
    
    public void setAllowed(final boolean b) {
        this._gmAllowed = b;
    }
    
    public ScheduleInfo getScheduleInfo() {
        return this._scheduleInfo;
    }
    
    public int getModeId() {
        for (final Map.Entry<Integer, MiniEventManager> e : EventManager.getInstance().getMiniEvents().get(this._event).entrySet()) {
            if (e.getValue().getMode().getModeName().equals(this.getModeName())) {
                return e.getKey();
            }
        }
        return 0;
    }
    
    public enum FeatureCategory
    {
        Configs, 
        Items, 
        Players;
    }
    
    public enum FeatureType
    {
        Level, 
        ItemGrades, 
        Enchant, 
        Items, 
        Delays, 
        TimeLimit, 
        Skills, 
        Buffer, 
        StrenghtChecks, 
        Rounds, 
        TeamsAmmount, 
        TeamSize;
    }
}
