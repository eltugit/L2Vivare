package gr.sr.events.engine.base;

import gr.sr.events.engine.EventManager;

import java.util.StringTokenizer;

public class EventSpawn {
    private Loc _loc;
    private int _spawnId;
    private int _teamId;
    private final int _mapId;
    private SpawnType _type;
    private int _fenceWidth;
    private int _fenceLength;
    private String _note = null;
    private boolean _saved;

    public EventSpawn(int mapId, int spawnId, Loc loc, int teamId, String type) {
        this._loc = loc;
        this._spawnId = spawnId;
        this._teamId = teamId;
        this._mapId = mapId;
        this._type = assignSpawnType(type);
    }

    private static SpawnType assignSpawnType(String typeString) {
        for (SpawnType st : SpawnType.values()) {
            if (st.toString().equalsIgnoreCase(typeString)) {
                return st;
            }
        }
        return SpawnType.Regular;
    }

    public SpawnType getSpawnType() {
        return this._type;
    }

    public int getDoorId() {
        if (this._type == SpawnType.Door) {
            return this._loc.getX();
        }
        return -1;
    }

    public int getNpcId() {
        try {
            if (this._type == SpawnType.Npc) {
                return Integer.parseInt(this._note);
            }
            return -1;
        } catch (Exception exception) {
            return -1;
        }
    }

    public Loc getLoc() {
        return new Loc(this._loc.getX(), this._loc.getY(), this._loc.getZ(), this._loc.getHeading());
    }

    public int getMapId() {
        return this._mapId;
    }

    public void setType(String s) {
        this._type = assignSpawnType(s);
        this._saved = false;
    }

    public int getSpawnTeam() {
        return this._teamId;
    }

    public int getSpawnId() {
        return this._spawnId;
    }

    public void setNote(String note) {
        this._note = note;
        if (this._type == SpawnType.Fence) {
            try {
                StringTokenizer st = new StringTokenizer(note, " ");
                this._fenceWidth = Integer.parseInt(st.nextToken());
                this._fenceLength = Integer.parseInt(st.nextToken());
            } catch (Exception e) {
                EventManager.getInstance().debug("The value for fence's length / weight can be only a number! Reseting back to default values.");
                this._fenceWidth = 100;
                this._fenceLength = 100;
            }
        }
        this._saved = false;
    }

    public void setId(int i) {
        this._spawnId = i;
        this._saved = false;
    }

    public void setTeamId(int i) {
        this._teamId = i;
        this._saved = false;
    }

    public void setX(int i) {
        Loc newLoc = new Loc(i, this._loc.getY(), this._loc.getZ());
        this._loc = newLoc;
        this._saved = false;
    }

    public void setY(int i) {
        Loc newLoc = new Loc(this._loc.getX(), i, this._loc.getZ());
        this._loc = newLoc;
        this._saved = false;
    }

    public void setZ(int i) {
        Loc newLoc = new Loc(this._loc.getX(), this._loc.getY(), i);
        this._loc = newLoc;
        this._saved = false;
    }

    public int getImportance() {
        String note = getNote();
        try {
            return Integer.parseInt(note.split("-")[0]);
        } catch (Exception e) {
            setNote("1-false");
            return getImportance();
        }
    }

    public boolean canRespawnHere() {
        String note = getNote();
        try {
            return Boolean.parseBoolean(note.split("-")[1]);
        } catch (Exception e) {
            setNote("1-false");
            return canRespawnHere();
        }
    }

    public void setImportance(int i) {
        String respawnHere, note = getNote();
        try {
            String importance = note.split("-")[0];
            respawnHere = note.split("-")[1];
        } catch (Exception e) {
            setNote("1-false");
            note = getNote();
            String importance = note.split("-")[0];
            respawnHere = note.split("-")[1];
        }
        String str1 = String.valueOf(i);
        note = str1 + "-" + respawnHere;
        setNote(note);
    }

    public void setRespawnHere(boolean b) {
        String importance, note = getNote();
        try {
            importance = note.split("-")[0];
            String respawnHere = note.split("-")[1];
        } catch (Exception e) {
            setNote("1-false");
            note = getNote();
            importance = note.split("-")[0];
            String respawnHere = note.split("-")[1];
        }
        String str1 = String.valueOf(b);
        note = importance + "-" + str1;
        setNote(note);
    }

    public int getRadius() {
        try {
            return Integer.parseInt(this._note);
        } catch (Exception exception) {
            return -1;
        }
    }

    public int getFenceWidth() {
        if (this._type == SpawnType.Fence) {
            return this._fenceWidth;
        }
        return 0;
    }

    public int getFenceLength() {
        if (this._type == SpawnType.Fence) {
            return this._fenceLength;
        }
        return 0;
    }

    public boolean isSaved() {
        return this._saved;
    }

    public void setSaved(boolean b) {
        this._saved = b;
    }

    public String getNote() {
        return this._note;
    }
}


