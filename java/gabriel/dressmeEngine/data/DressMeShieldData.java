package gabriel.dressmeEngine.data;


public class DressMeShieldData implements DressMeData {
    private final int _id;
    private final int _shield;
    private final String _name;
    private final int _priceId;
    private final long _priceCount;
    private final boolean _isShield;

    public DressMeShieldData(int id, int shield, String name, int priceId, long priceCount, boolean isShield) {
        _id = id;
        _shield = shield;
        _name = name;
        _priceId = priceId;
        _priceCount = priceCount;
        _isShield = isShield;
    }


    public int getId() {
        return _id;
    }

    public int getShieldId() {
        return _shield;
    }

    public String getName() {
        return _name;
    }

    public int getPriceId() {
        return _priceId;
    }

    public long getPriceCount() {
        return _priceCount;
    }

    public boolean isShield() {
        return _isShield;
    }
}
