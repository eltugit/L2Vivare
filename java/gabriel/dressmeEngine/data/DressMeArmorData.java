package gabriel.dressmeEngine.data;


public class DressMeArmorData implements DressMeData {
    private final int _id;
    private final String _name;
    private final String _type;
    private final int _chest;
    private final int _legs;
    private final int _gloves;
    private final int _feet;
    private final int _priceId;
    private final long _priceCount;
    private final boolean _isSuit;

    public DressMeArmorData(int id, String name, String type, int chest, int legs, int gloves, int feet, int priceId, long priceCount, boolean isSuit) {
        _id = id;
        _name = name;
        _type = type;
        _chest = chest;
        _legs = legs;
        _gloves = gloves;
        _feet = feet;
        _priceId = priceId;
        _priceCount = priceCount;
        _isSuit = isSuit;
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


    public int getChest() {
        return _chest;
    }


    public int getLegs() {
        return _legs;
    }


    public int getGloves() {
        return _gloves;
    }


    public int getFeet() {
        return _feet;
    }

    public int getPriceId() {
        return _priceId;
    }

    public long getPriceCount() {
        return _priceCount;
    }


    public boolean isSuit() {
        return _isSuit;
    }
}
