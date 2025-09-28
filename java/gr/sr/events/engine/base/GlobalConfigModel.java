package gr.sr.events.engine.base;

public class GlobalConfigModel {
    private final String _category;
    private final String _key;
    private String _value;
    private final String _description;
    private final int _inputType;

    public GlobalConfigModel(String category, String key, String value, String desc, int input) {
        this._category = category;
        this._key = key;
        this._value = value;
        this._description = desc;
        this._inputType = input;
    }

    public String getCategory() {
        return this._category;
    }

    public String getKey() {
        return this._key;
    }

    public String getValue() {
        return this._value;
    }

    public void setValue(String value) {
        this._value = value;
    }

    public String getDesc() {
        return this._description;
    }

    public int getInputType() {
        return this._inputType;
    }
}


