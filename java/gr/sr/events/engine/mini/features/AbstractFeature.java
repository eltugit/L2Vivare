package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.interf.PlayerEventInfo;

import java.util.LinkedList;
import java.util.List;


public abstract class AbstractFeature
{
    protected EventType _event;
    protected String _params;
    protected List<FeatureConfig> _configs;
    
    public abstract EventMode.FeatureType getType();
    
    protected abstract void initValues();
    
    public AbstractFeature(final EventType event) {
        this._configs = new LinkedList<FeatureConfig>();
        this._event = event;
    }
    
    public abstract boolean checkPlayer(final PlayerEventInfo p0);
    
    protected String[] splitParams(final String params) {
        return params.split(",");
    }
    
    public String getParams() {
        return this._params;
    }
    
    protected void addConfig(final String name, final String desc, final int inputFormType) {
        this._configs.add(new FeatureConfig(name, desc, inputFormType));
    }
    
    public FeatureConfig getConfig(final String name) {
        for (final FeatureConfig c : this._configs) {
            if (c._name.equals(name)) {
                return c;
            }
        }
        return null;
    }
    
    public void setValueFor(final String configName, final String value) {
        final String[] splitted = this._params.split(",");
        int index = 0;
        for (final FeatureConfig c : this._configs) {
            if (c._name.equals(configName)) {
                break;
            }
            ++index;
        }
        if (splitted.length < index) {
            return;
        }
        splitted[index] = value;
        final StringBuilder tb = new StringBuilder();
        for (final String s : splitted) {
            tb.append(s + ",");
        }
        final String result = tb.toString();
        this._params = result.substring(0, result.length() - 1);
        this.initValues();
    }
    
    public String getValueFor(final String configName) {
        final String[] splitted = this._params.split(",");
        int index = 0;
        for (final FeatureConfig c : this._configs) {
            if (c._name.equals(configName)) {
                break;
            }
            ++index;
        }
        if (splitted.length < index) {
            return "N/A";
        }
        return splitted[index];
    }
    
    public List<FeatureConfig> getConfigs() {
        return this._configs;
    }
    
    public class FeatureConfig
    {
        public String _name;
        public String _desc;
        public int _inputFormType;
        
        protected FeatureConfig(final String name, final String desc, final int inputFormType) {
            this._name = name;
            this._desc = desc;
            this._inputFormType = inputFormType;
        }
    }
}
