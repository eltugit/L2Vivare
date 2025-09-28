package gr.sr.interf.delegate;

import l2r.gameserver.enums.ShortcutType;
import l2r.gameserver.model.Shortcut;

public class ShortCutData {
    private final Shortcut _shortcut;

    public ShortCutData(int slotId, int pageId, ShortcutType shortcutType, int shortcutId, int shortcutLevel, int characterType) {
        this._shortcut = new Shortcut(slotId, pageId, shortcutType, shortcutId, shortcutLevel, characterType);
    }

    public int getId() {
        return this._shortcut.getId();
    }

    public int getLevel() {
        return this._shortcut.getLevel();
    }

    public int getPage() {
        return this._shortcut.getPage();
    }

    public int getSlot() {
        return this._shortcut.getSlot();
    }

    public ShortcutType getType() {
        return this._shortcut.getType();
    }

    public int getCharacterType() {
        return this._shortcut.getCharacterType();
    }
}


