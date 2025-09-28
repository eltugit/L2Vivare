package gabriel.dressmeEngine.data;


public class DressMeWeaponData implements DressMeData {
    private final int _id;
    private final String _name;
    private final String _type;
    private final boolean _isBig;
    private final boolean _isMagic;
    private final int _priceId;
    private final long _priceCount;

    public DressMeWeaponData(int id, String name, String type, boolean isBig, boolean isMagic, int priceId, long priceCount) {
        _id = id;
        _name = name;
        _type = type;
        _isBig = isBig;
        _isMagic = isMagic;
        _priceId = priceId;
        _priceCount = priceCount;
    }


    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getType() {
        return _type;
    }

    public boolean isBig() {
        return _isBig;
    }

    public int getPriceId() {
        return _priceId;
    }

    public long getPriceCount() {
        return _priceCount;
    }

    public boolean isMagic() {
        return _isMagic;
    }
}
