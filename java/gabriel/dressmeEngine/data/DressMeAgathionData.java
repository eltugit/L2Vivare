package gabriel.dressmeEngine.data;


public class DressMeAgathionData implements DressMeData {
    private final int _id;
    private final int _agathion;
    private final String _name;
    private final int _priceId;
    private final long _priceCount;

    public DressMeAgathionData(int id, int agathion, String name, int priceId, long priceCount) {
        _id = id;
        _agathion = agathion;
        _name = name;
        _priceId = priceId;
        _priceCount = priceCount;
    }


    public int getId() {
        return _id;
    }

    public int getAgathionId() {
        return _agathion;
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

}
