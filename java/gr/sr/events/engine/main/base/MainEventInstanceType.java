package gr.sr.events.engine.main.base;

import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.main.events.AbstractMainEvent;
import gr.sr.interf.delegate.InstanceData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class MainEventInstanceType
{
    private final int _id;
    private String _name;
    private final String _visibleName;
    private final AbstractMainEvent _event;
    private String _params;
    private InstanceData _tempInstance;
    private final Map<String, ConfigModel> _configs;
    private int _min;
    private int _max;
    private int _rate;

    public MainEventInstanceType(final int id, final AbstractMainEvent event, final String name, final String visibleName, final String params) {
        this._configs = new ConcurrentHashMap<String, ConfigModel>();
        this._id = id;
        this._name = name;
        this._visibleName = visibleName;
        this._event = event;
        this._params = ((params == null) ? "" : params);
        this._configs.clear();
    }

    public MainEventInstanceType(final int id, final AbstractMainEvent event, final String name, final String visibleName, final String params, final int min, final int max, final int rate) {
        this._configs = new ConcurrentHashMap<String, ConfigModel>();
        this._id = id;
        this._name = name;
        this._visibleName = visibleName;
        this._event = event;
        this._params = ((params == null) ? "" : params);
        this._configs.clear();
        this._min = min;
        this._max = max;
        this._rate = rate;
    }

    public int getMinPlayers() {
        return this._min;
    }

    public int getMaxPlayers() {
        return this._max;
    }

    public void loadConfigs() {
        if (this._params.length() == 0) {
            return;
        }
        for (final String criteria : this._params.split(";")) {
            final String name = criteria.split(":")[0];
            String value;
            if (criteria.split(":").length > 1) {
                value = criteria.split(":")[1];
            }
            else {
                value = "";
            }
            this.setConfig(name, value, false);
        }
    }

    public String encodeParams() {
        final StringBuilder tb = new StringBuilder();
        for (final Map.Entry<String, ConfigModel> e : this._configs.entrySet()) {
            tb.append(e.getValue().encode());
        }
        final String result = tb.toString();
        return (result.length() > 0) ? result.substring(0, result.length() - 1) : result;
    }

    public void addDefaultConfig(final String name, final String value, final String desc, final String defaultVal, final ConfigModel.InputType input, final String inputParams) {
        this.addParam(name, value, desc, defaultVal, input, inputParams, true);
    }

    public void addParam(final String name, final String value, final String desc, final String defaultVal, final ConfigModel.InputType input, final String inputParams, final boolean override) {
        if (this._configs.containsKey(name) && !override) {
            return;
        }
        final ConfigModel config = new ConfigModel(name, value, desc, defaultVal, input, inputParams);
        this._configs.put(name, config);
    }

    public int getId() {
        return this._id;
    }

    public String getName() {
        return this._name;
    }

    public void setName(final String name) {
        this._name = name;
    }

    public String getVisibleName() {
        return this._visibleName;
    }

    public AbstractMainEvent getEvent() {
        return this._event;
    }

    public String getParams() {
        return this._params;
    }

    public void setParams(final String p) {
        this._params = p;
    }

    public InstanceData getInstance() {
        return this._tempInstance;
    }

    public void setInstance(final InstanceData instance) {
        this._tempInstance = instance;
    }

    public Map<String, ConfigModel> getConfigs() {
        return this._configs;
    }

    public void setConfig(final String name, final String value, final boolean addToValue) {
        if (!this._configs.containsKey(name)) {
            return;
        }
        if (!addToValue) {
            this._configs.get(name).setValue(value);
        }
        else {
            this._configs.get(name).addToValue(value);
        }
    }

    public String getConfig(final String name) {
        return this._configs.get(name).getValue();
    }

    public int getConfigInt(final String name) {
        final String v = this.getConfig(name);
        try {
            return Integer.parseInt(v);
        }
        catch (Exception ex) {
            return 0;
        }
    }

    public boolean getConfigBoolean(final String name) {
        final String v = this.getConfig(name);
        try {
            return Boolean.parseBoolean(v);
        }
        catch (Exception ex) {
            return false;
        }
    }

    public int getTempRate() {
        return this._rate;
    }

    public int getStrenghtRate() {
        return this.getConfigInt("strenghtRate");
    }
}

