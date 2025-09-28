package gr.sr.main;

import java.util.Base64;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class Tour {
    public static String getText(String toDecode) {
        return new String(Base64.getDecoder().decode(toDecode));
    }
}
