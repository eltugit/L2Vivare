package gr.sr.interf.delegate;

import gr.sr.l2j.delegate.IInstanceData;
import l2r.gameserver.model.entity.Instance;

public class InstanceData
        implements IInstanceData {
    protected Instance _instance;

    public InstanceData(Instance i) {
        this._instance = i;
    }

    public Instance getOwner() {
        return this._instance;
    }

    public int getId() {
        return this._instance.getId();
    }

    public String getName() {
        return this._instance.getName();
    }
}


