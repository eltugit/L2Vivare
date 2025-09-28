package gabriel.others;


/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class TempEnchant {
    private final int id;
    private final int value;
    private final int time;

    public TempEnchant(int id, int value, int time) {
        this.id = id;
        this.value = value;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public int getTime() {
        return time;
    }
}
