package gabriel.dressmeEngine.data;


public class DressMeEnchantData implements DressMeData {
    private final int _id;
    private final int _enchant;
    private final String _name;
    private final int _priceId;
    private final long _priceCount;

    public DressMeEnchantData(int id, int enchant, String name, int priceId, long priceCount) {
        _id = id;
        _enchant = enchant;
        _name = name;
        _priceId = priceId;
        _priceCount = priceCount;
    }


    public int getId() {
        return _id;
    }

    public int getEnchantId() {
        return _enchant;
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
