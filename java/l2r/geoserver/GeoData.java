package l2r.geoserver;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
import l2r.Config;
import l2r.gameserver.data.xml.impl.DoorData;
import l2r.gameserver.handler.AdminCommandHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.geoserver.geodata.GeoEngine;
import l2r.geoserver.geodata.GeoMove;
import l2r.geoserver.model.GeoCollision;
import l2r.geoserver.voiced.GeoDataVCM;

import java.util.List;

public class GeoData {
    protected static GeoData instance;
    public static final byte EAST = 1;
    public static final byte WEST = 2;
    public static final byte SOUTH = 4;
    public static final byte NORTH = 8;
    
    public static final byte NSWE_ALL = 15;
    public static final byte NSWE_NONE = 0;
    public static final byte BLOCKTYPE_FLAT = 0;
    public static final byte BLOCKTYPE_COMPLEX = 1;
    public static final byte BLOCKTYPE_MULTILEVEL = 2;
    public static final int BLOCKS_IN_MAP = 256 * 256;

    
    public static GeoData getInstance() {
        if (instance == null)
            instance = new GeoData();
        return instance;
    }

    public GeoData() {
        this.load();
    }

    private void load() {
        
        AdminCommandHandler.getInstance().registerHandler(new GeoDataVCM());
        if (Config.GEODATA) {
            
            GeoEngine.load();
        }
        
    }

    
    public boolean canSeeTarget(L2Character actor, L2Object target) {
        if (actor != null && target != null) {
            if (!target.isCharacter()) {
                return true;
            } else if (target.isDoor()) {
                return true;
            } else if (actor == target) {
                return true;
            } else {
                return (target.isDoor() || !DoorData.getInstance().checkIfDoorsBetween(actor.getX(), actor.getY(), actor.getZ(), target.getX(), target.getY(), target.getZ(), actor.getInstanceId())) && this.canSeeTarget(actor.getX(), actor.getY(), actor.getZ(), target.getX(), target.getY(), target.getZ(), actor.isFlying(), actor.getTemplate().getCollisionHeight(), ((L2Character) target).getTemplate().getCollisionHeight(), actor.getInstanceId());
            }
        } else {
            return true;
        }
    }

    public boolean canSeeTarget(L2Character actor, int tx, int ty, int tz) {
        if (actor == null) {
            return true;
        } else {
            return !DoorData.getInstance().checkIfDoorsBetween(actor.getX(), actor.getY(), actor.getZ(), tx, ty, tz, actor.getInstanceId()) && this.canSeeTarget(actor.getX(), actor.getY(), actor.getZ(), tx, ty, tz, actor.isFlying(), actor.getTemplate().getCollisionHeight(), 16, actor.getInstanceId());
        }
    }

    
    public boolean canSeeTarget(L2Character actor, Location tLoc) {
        if (actor != null && tLoc != null) {
            return !DoorData.getInstance().checkIfDoorsBetween(actor.getX(), actor.getY(), actor.getZ(), tLoc.getX(), tLoc.getY(), tLoc.getZ(), actor.getInstanceId()) && this.canSeeTarget(actor.getX(), actor.getY(), actor.getZ(), tLoc.getX(), tLoc.getY(), tLoc.getZ(), actor.isFlying(), actor.getTemplate().getCollisionHeight(), 16, actor.getInstanceId());
        } else {
            return true;
        }
    }

    private boolean canSeeTarget(int x, int y, int z, int tx, int ty, int tz, boolean air, int zOffset, int tzOffset, int instanceId) {
        return !Config.GEODATA || GeoEngine.canSeeTarget(x, y, z, tx, ty, tz, air, zOffset, tzOffset, instanceId);
    }

    
    public Location moveInWaterCheck(L2Character actor, Location loc) {
        if (actor == null) {
            return null;
        } else {
            return DoorData.getInstance().checkIfDoorsBetween(actor.getLocation(), loc, actor.getInstanceId()) ? null : this.moveInWaterCheck(actor.getX(), actor.getY(), actor.getZ(), loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        }
    }

    
    public Location moveCheckInAir(int x, int y, int z, int tx, int ty, int tz, int collision, int instanceId) {
        return GeoEngine.moveCheckInAir(x, y, z, tx, ty, tz, collision, instanceId);
    }

    
    public int getHeight(int x, int y, int z, int instanceId) {
        return Config.GEODATA ? GeoEngine.getHeight(x, y, z, instanceId) : z;
    }

    
    public int getHeight(int x, int y, int z) {
        return this.getHeight(x, y, z, 0);
    }

    public int getHeight(Location loc) {
        return this.getHeight(loc.getX(), loc.getY(), loc.getZ(), 0);
    }

    public Location moveCheckWithoutDoors(Location loc, Location loc2, boolean var3) {
        return this.moveCheck(loc.getX(), loc.getY(), loc.getZ(), loc2.getX(), loc2.getY(), loc2.getZ(), -1, var3);
    }

//    
//    public Location moveCheck(L2Character actor, L2Object target) {
//        return DoorData.getInstance().checkIfDoorsBetween(actor.getX(), actor.getY(), actor.getZ(), target.getX(), target.getY(), target.getZ(), actor.getInstanceId()) ? new Location(actor.getX(), actor.getY(), this.getHeight(actor.getX(), actor.getY(), actor.getZ())) : this.moveCheck(actor, target.getLocation(), false);
//    }
//
//    
//    public Location moveCheck(L2Character actor, Location loc, boolean var3) {
//        return DoorData.getInstance().checkIfDoorsBetween(actor.getX(), actor.getY(), actor.getZ(), loc.getX(), loc.getY(), loc.getZ(), actor.getInstanceId()) ? new Location(actor.getX(), actor.getY(), this.getHeight(actor.getX(), actor.getY(), actor.getZ())) : this.moveCheck(actor.getX(), actor.getY(), actor.getZ(), loc.getX(), loc.getY(), loc.getZ(), actor.getInstanceId(), var3);
//    }
//
//    
//    public Location moveCheck(L2Character actor, int tx, int ty, int tz) {
//        return DoorData.getInstance().checkIfDoorsBetween(actor.getX(), actor.getY(), actor.getZ(), tx, ty, tz, actor.getInstanceId()) ? new Location(actor.getX(), actor.getY(), this.getHeight(actor.getX(), actor.getY(), actor.getZ())) : this.moveCheck(actor.getX(), actor.getY(), actor.getZ(), tx, ty, tz, actor.getInstanceId(), false);
//    }
//    public Location moveCheck(int x, int y, int z, int tx, int ty, int tz, boolean unk, int instanceId) {
//        return DoorData.getInstance().checkIfDoorsBetween(x, y, z, tx, ty, tz, instanceId) ? new Location(x, y, this.getHeight(x, y, z)) : this.moveCheck(x, y, z, tx, ty, tz, instanceId, unk);
//    }
//    
//    public Location moveCheck(int x, int y, int z, int tx, int ty, int tz, int instanceId, boolean unk) {
//        return Config.GEODATA ? GeoEngine.moveCheck(x, y, z, tx, ty, instanceId) : new Location(tx, ty, tz);
//    }

    
    public static int getHeight(Location location, int instanceId) {
        return GeoEngine.getHeight(location.getX(), location.getY(), location.getZ(), instanceId);
    }

    
    public static byte NgetNSWE(int geoX, int geoY, int z, int geoIndex){
        return GeoEngine.NgetNSWE(geoX, geoY, z, geoIndex);
    }

    
    public Location moveCheck(L2Character actor, L2Object target) {
        return this.moveCheck(actor, target.getLocation(), false);
    }

    
    public Location moveCheck(L2Character actor, Location target, boolean unk) {
        return this.moveCheck(actor.getX(), actor.getY(), actor.getZ(), target.getX(), target.getY(), target.getZ(), actor.getInstanceId(), unk);
    }

    
    public Location moveCheck(L2Character actor, int tx, int ty, int tz) {
        return this.moveCheck(actor.getX(), actor.getY(), actor.getZ(), tx, ty, tz, actor.getInstanceId(), false);
    }

    
    public Location moveCheck(int x, int y, int z, int tx, int ty, int tz, boolean unk, int instanceId) {
        return this.moveCheck(x, y, z, tx, ty, tz, instanceId, unk);
    }

    
    public Location moveCheck(int x, int y, int z, int tx, int ty, int tz, int instanceId, boolean debug) {
        if (Config.GEODATA) {
            return DoorData.getInstance().checkIfDoorsBetween(x, y, z, tx, ty, tz, instanceId) ? new Location(x, y, this.getHeight(x, y, z)) : GeoEngine.moveCheck(x, y, z, tx, ty, instanceId);
        } else {
            return new Location(tx, ty, tz);
        }
    }

    
    public Location moveCheckForAI(L2Attackable actor, L2Object t) {
        return GeoEngine.moveCheckForAI(actor.getLocation(), t.getLocation(), actor.getInstanceId());
    }

    
    public Location moveCheckForAI(L2Attackable actor, Location t) {
        return GeoEngine.moveCheckForAI(actor.getLocation(), t, actor.getInstanceId());
    }

    
    public Location moveCheckForAI(Location loc, Location tLoc, int instanceId) {
        return GeoEngine.moveCheckForAI(loc, tLoc, instanceId);
    }


    public boolean canMoveToCoord(L2Object actor, Location loc) {
        return !DoorData.getInstance().checkIfDoorsBetween(actor.getLocation(), loc, actor.getInstanceId()) && this.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), loc.getX(), loc.getY(), loc.getZ(), false);
    }

    public boolean canMoveToCoord(L2Character actor, Location loc, boolean unk) {
        return !DoorData.getInstance().checkIfDoorsBetween(actor.getLocation(), loc, actor.getInstanceId()) && this.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), loc.getX(), loc.getY(), loc.getZ(), unk);
    }

    
    public boolean canMoveToCoord(L2Character actor, int tx, int ty, int tz, boolean unk) {
        return !DoorData.getInstance().checkIfDoorsBetween(actor.getLocation(), new Location(tx, ty, tz), actor.getInstanceId()) && this.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), tx, ty, tz, unk);
    }

    public boolean canMoveToCoord(Location loc, int x, int y, int z, int instanceId, boolean unk) {
        return !DoorData.getInstance().checkIfDoorsBetween(loc, new Location(x, y, z), instanceId) && this.canMoveToCoord(loc.getX(), loc.getY(), loc.getZ(), x, y, z, unk);
    }
    
    public boolean canMoveToCoord(int x, int y, int z, int tx, int ty, int tz, boolean unk) {
        if (Config.GEODATA) {
            return !DoorData.getInstance().checkIfDoorsBetween(x, y, z, tx, ty, tz, 0) && GeoEngine.canMoveToCoord(x, y, z, tx, ty, tz, 0);
        } else {
            return true;
        }
    }

    public Location moveInWaterCheck(int x, int y, int z, int tx, int ty, int tz, int waterZ, int instanceId) {
        return Config.GEODATA ? GeoEngine.moveInWaterCheck(x, y, z, tx, ty, tz, waterZ, instanceId) : new Location(tx, ty, tz);
    }

    
    public int getSpawnHeight(Location loc) {
        return this.getSpawnHeight(loc.getX(), loc.getY(), loc.getZ());
    }

    
    public int getSpawnHeight(int x, int y, int z) {
        return Config.GEODATA ? GeoEngine.getHeight(x, y, z, 0) : z;
    }

    public int getGeoX(int x) {
        if (x >= L2World.MAP_MIN_X && x <= L2World.MAP_MAX_X) {
            return x - L2World.MAP_MIN_X >> 4;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int getGeoY(int y) {
        if (y >= L2World.MAP_MIN_Y && y <= L2World.MAP_MAX_Y) {
            return y - L2World.MAP_MIN_Y >> 4;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int getWorldX(int x) {
        return (x << 4) + L2World.MAP_MIN_X + 8;
    }

    public int getWorldY(int y) {
        return (y << 4) + L2World.MAP_MIN_Y + 8;
    }

    
    public int getNSWE_ALL() {
        return GeoEngine.NSWE_ALL;
    }

    
    public byte getNSWE(int x, int y, int z, int instanceId) {
        return GeoEngine.getNSWE(x, y, z, instanceId);
    }

    
    public Location moveCheck(int x, int y, int z, int tx, int ty, int tz, int instanceId) {
        //TODO GEODATA TEST
//        return Config.GEODATA ? GeoEngine.moveCheck(x, y, z, tx, ty, instanceId) : new Location(tx, ty, tz);
        return this.moveCheck(x,y,z,tx,ty,tz, false, instanceId);
    }

    
    public Location moveCheck(L2Character actor, Location loc) {
        return this.moveCheck(actor, loc, false);
    }

    public boolean canMove(Location loc, L2Object target) {
        return this.canMoveToCoord(loc, target.getX(), target.getY(), target.getZ(), target.getInstanceId(), false);
    }

    
    public boolean canMove(L2Object actor, L2Object target) {
        return this.canMoveToCoord(actor, target.getLocation());
    }

    
    public boolean canMove(L2Object actor, Location target) {
        return this.canMoveToCoord(actor, target);
    }
    
    public boolean canMove(L2Object actor, int x, int y, int z) {
        return this.canMoveToCoord(actor, new Location(x, y, z));
    }
    
    public List<Location> constructMoveList(Location loc, Location tLoc) {
        return GeoMove.constructMoveList(loc, tLoc);
    }
    
    public List<Location> MoveList(int x, int y, int z, int tx, int ty, int instanceId, boolean onlyFullPath) {
        return GeoEngine.MoveList(x, y, z, tx, ty, instanceId, onlyFullPath);
    }
    
    public boolean canSeeCoord(L2Character actor, int x, int y, int z, boolean tAirOrWater) {
        return GeoEngine.canSeeCoord(actor, x, y, z, tAirOrWater);
    }
    
    public boolean canSeeTarget(L2Character actor, L2Character target, boolean tAirOrWater) {
        return GeoEngine.canSeeTarget(actor, target, tAirOrWater);
    }

    
    public List<List<Location>> findMovePath(int x, int y, int z, Location target, L2Object obj, boolean showTrace, int geoIndex) {
        return GeoMove.findMovePath(x, y, z, target, obj, showTrace, geoIndex);
    }

    
    public void removeGeoCollision(GeoCollision geo, int geoIndex) {
        GeoEngine.removeGeoCollision(geo, geoIndex);
    }

    
    public void applyGeoCollision(GeoCollision geo, int geoIndex) {
        GeoEngine.applyGeoCollision(geo, geoIndex);
    }
}

