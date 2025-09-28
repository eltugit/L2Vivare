//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package l2r.geoserver;


import gr.sr.logging.Log;
import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.xml.impl.DoorData;
import l2r.gameserver.enums.CtrlEvent;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.tasks.character.NotifyAITask;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ValidateLocation;
import l2r.gameserver.util.Broadcast;
import l2r.gameserver.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MoveController {
    private final L2Character actor;
    private final List<List<Location>> _targetRecorder = new ArrayList();
    public final Lock _moveLock = new ReentrantLock();
    public final Location movingDestTempPos = new Location(0, 0, 0);
    public double _previousSpeed = 0.0D;
    public long _followTimestamp;
    public Location destination;
    public MoveNextTask _moveTaskRunnable;
    public long _startMoveTime;
    public Future<?> _moveTask;
    public List<Location> moveList;
    public int _offset;
    public boolean isFollow;
    public boolean _forestalling;

    
    public static MoveController Create(L2Character var0) {
        return new MoveController(var0);
    }

    private MoveController(L2Character var1) {
        this.actor = var1;
    }

    
//    public boolean followToCharacter(Location loc, L2Character target, int offset, boolean foreStalling) {
    public boolean followToCharacter(Location loc, L2Character target, int offset, boolean foreStalling) {
        this._moveLock.lock();

        boolean ok;
        try {
            if (this.actor.isMovementDisabled() || target == null || this.actor.isInBoat()) {
                ok = false;
                return ok;
            }

            if (target.isInvisible()) {
                ok = false;
                return ok;
            }

            offset = Math.max(offset, 10);
            if (this.isFollow && target == this.actor.getAI().getFollowTarget() && offset == this._offset) {
                ok = true;
                return ok;
            }

            if (Math.abs(this.actor.getZ() - target.getZ()) > 1000 && !this.actor.isFlying()) {
                ok = false;
                return ok;
            }

            if (this.actor.isPlayer() && this.actor.getAI().getIntention() == CtrlIntention.AI_INTENTION_MOVE_TO) {
                this.actor.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }

            this.stopMove(false, false);
            if (this.buildPathTo(loc.getX(), loc.getY(), loc.getZ(), offset, target, foreStalling, !target.isDoor())) {
                this.movingDestTempPos.set(loc.getX(), loc.getY(), loc.getZ());
                this.actor.isMoving = true;
                this.isFollow = true;
                this._forestalling = foreStalling;
                this._offset = offset;
                this.actor.getAI().setFollowTarget(target);
                this.moveNext(true);
                ok = true;
                return ok;
            }

            ok = false;
        } finally {
            this._moveLock.unlock();
        }
        return ok;
    }


    
    public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathFinding) {
        this._moveLock.lock();

        boolean ok;
        try {
            if (this.actor.isInWater()) {
                offset = 0;
            }
            offset = Math.max(offset, 0);
            Location loc = (new Location(x_dest, y_dest, z_dest)).world2geo();
            if (!this.actor.isMoving || this.isFollow || !this.movingDestTempPos.equals(loc)) {
                if (this.actor.isMovementDisabled()) {
                    this.actor.sendActionFailed();
                    ok = false;
                    return ok;
                }

                if (this.actor.isPlayer() && this.actor.getAI().getIntention() == CtrlIntention.AI_INTENTION_MOVE_TO) {
                    this.actor.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                }

                this.actor.stopMove(false, false);
                if (this.buildPathTo(x_dest, y_dest, z_dest, offset, pathFinding)) {
                    this.movingDestTempPos.set(loc);
                    this.actor.isMoving = true;
                    this.actor.moveNext(true);
                    ok = true;
                    return ok;
                }

                this.actor.sendActionFailed();
                ok = false;
                return ok;
            }

            this.actor.sendActionFailed();
            ok = true;
        } finally {
            this._moveLock.unlock();
        }

        return ok;
    }

    
    public void moveNext(boolean firstMove) {
        if (this.actor.isMoving && !this.actor.isMovementDisabled()) {
            this._previousSpeed = this.actor.getMoveSpeed();
            if (this._previousSpeed <= 0.0D) {
                this.actor.stopMove();
            } else {
                Location first;
                if (!firstMove) {
                    first = this.destination;
                    if (first != null) {
                        this.actor.setLoc(first, true);
                    }
                }

                if (this._targetRecorder.isEmpty()) {
                    CtrlEvent ctrlEvent = this.isFollow ? CtrlEvent.EVT_ARRIVED : CtrlEvent.EVT_ARRIVED;
                    this.actor.stopMove(false, true);
                    ThreadPoolManager.getInstance().executeGeneral(new NotifyAITask(this.actor, ctrlEvent));
                } else {
                    this.moveList = this._targetRecorder.remove(0);
                    first = this.moveList.get(0).clone().geo2world();
                    Location dest = this.moveList.get(this.moveList.size() - 1).clone().geo2world();
                    this.destination = dest;
                    double dist = !this.actor.isFlying() && !this.actor.isInWater() ? first.distance(dest) : first.distance3D(dest);
                    this.actor.setHeading(Util.calculateHeadingFrom(this.actor.getX(), this.actor.getY(), this.destination.getX(), this.destination.getY()));
                    if (DoorData.getInstance().checkIfDoorsBetween(this.actor.getLocation(), this.destination, this.actor.getInstanceId())) {
                        this.actor.stopMove();
                        this.actor.sendActionFailed();
                    } else {
                        this.actor.broadcastMove();
                        this._startMoveTime = this._followTimestamp = System.currentTimeMillis();
                        if (this._moveTaskRunnable == null) {
                            this._moveTaskRunnable = new MoveNextTask();
                        }

                        this._moveTask = ThreadPoolManager.getInstance().scheduleGeneral(this._moveTaskRunnable.setDist(this.actor, dist), (int) this.actor.getMoveTickInterval());
                    }
                }
            }
        } else {
            this.actor.stopMove();
        }
    }

    
    public void broadcastMove() {
        this.actor.validateLocation(this.actor.isPlayer() ? 2 : 1);
        this.actor.broadcastPacket(this.actor.movePacket());
    }

    
    public void validateLocation(int broadcast) {
        ValidateLocation vl = new ValidateLocation(this.actor);
        if (broadcast == 0) {
            this.actor.sendPacket(vl);
        } else if (broadcast == 1) {
            this.actor.broadcastPacket(vl);
        } else {
            Broadcast.broadcastPacketToOthers(this.actor, vl);
        }

    }

    public boolean buildPathTo(int x, int y, int z, int offset, boolean pathFinding) {
        return this.buildPathTo(x, y, z, offset, null, false, pathFinding);
    }

    
    public boolean buildPathTo(int x, int y, int z, int offset, L2Character follow, boolean forestalling, boolean pathfinding) {
        int geoIndex = this.actor.getInstanceId();
        Location dest;
        if (forestalling && follow != null && follow.isMoving) {
            dest = this.getIntersectionPoint(follow);
        } else {
            dest = new Location(x, y, z);
        }

        if (!this.actor.isInBoat() && !this.actor.isVehicle() && Config.GEODATA) {
            if (!this.actor.isFlying() && !this.actor.isInWater()) {
                List<Location> moveList = GeoData.getInstance().MoveList(this.actor.getX(), this.actor.getY(), this.actor.getZ(), dest.getX(), dest.getY(), geoIndex, true);
                if (moveList != null) {
                    if (moveList.isEmpty()) {
                        return false;
                    } else {
                        this.applyOffset(moveList, offset);
                        if (moveList.isEmpty()) {
                            return false;
                        } else {
                            moveList.remove(0);
                            if (moveList.isEmpty()) {
                                return false;
                            } else {
                                this._targetRecorder.clear();
                                this._targetRecorder.add(moveList);
                                return true;
                            }
                        }
                    }
                } else {
                    if (pathfinding) {
                        List<List<Location>> movePath = GeoData.getInstance().findMovePath(this.actor.getX(), this.actor.getY(), this.actor.getZ(), dest.clone(), this.actor, true, geoIndex);
                        if (!movePath.isEmpty()) {
                            moveList = movePath.remove(movePath.size() - 1);
                            this.applyOffset(moveList, offset);
                            if (!moveList.isEmpty()) {
                                movePath.add(moveList);
                            }

                            if (!movePath.isEmpty()) {
                                this._targetRecorder.clear();
                                this._targetRecorder.addAll(movePath);
                                return true;
                            }
                        }
                    }

                    if (follow != null) {
                        return false;
                    } else {
                        this.applyOffset(dest, offset);
                        moveList = GeoData.getInstance().MoveList(this.actor.getX(), this.actor.getY(), this.actor.getZ(), dest.getX(), dest.getY(), geoIndex, false);
                        if (moveList != null && !moveList.isEmpty()) {
                            this._targetRecorder.clear();
                            this._targetRecorder.add(moveList);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                this.applyOffset(dest, offset);
                Location nextLoc;
                if (this.actor.isFlying()) {
                    if (GeoData.getInstance().canSeeCoord(this.actor, dest.getX(), dest.getY(), dest.getZ(), true)) {
                        return this.setSimplePath(dest);
                    } else {
                        nextLoc = GeoData.getInstance().moveCheckInAir(this.actor.getX(), this.actor.getY(), this.actor.getZ(), dest.getX(), dest.getY(), dest.getZ(), this.actor.getTemplate().getCollisionRadius(), geoIndex);
                        return nextLoc != null && !nextLoc.equals(this.actor.getX(), this.actor.getY(), this.actor.getZ()) && this.setSimplePath(nextLoc);
                    }
                } else {
                    int waterZ = this.actor.getWaterZ();
                    nextLoc = GeoData.getInstance().moveInWaterCheck(this.actor.getX(), this.actor.getY(), this.actor.getZ(), dest.getX(), dest.getY(), dest.getZ(), waterZ, geoIndex);
                    if (nextLoc == null) {
                        return false;
                    } else {
                        List<Location> moveList = GeoData.getInstance().constructMoveList(this.actor.getLocation(), nextLoc.clone());
                        this._targetRecorder.clear();
                        if (!moveList.isEmpty()) {
                            this._targetRecorder.add(moveList);
                        }

                        int dz = dest.getZ() - nextLoc.getZ();
                        if (dz > 0 && dz < 128) {
                            moveList = GeoData.getInstance().MoveList(nextLoc.getX(), nextLoc.getY(), nextLoc.getZ(), dest.getX(), dest.getY(), geoIndex, false);
                            if (moveList != null && !moveList.isEmpty()) {
                                this._targetRecorder.add(moveList);
                            }
                        }

                        return !this._targetRecorder.isEmpty();
                    }
                }
            }
        } else {
            this.applyOffset(dest, offset);
            return this.setSimplePath(dest);
        }
    }

    public List<Location> applyOffset(List<Location> points, int offset) {
        offset >>= 4;
        if (offset <= 0) {
            return points;
        } else {
            long dx = points.get(points.size() - 1).getX() - points.get(0).getX();
            long dy = points.get(points.size() - 1).getY() - points.get(0).getY();
            long dz = points.get(points.size() - 1).getZ() - points.get(0).getZ();
            double distance = Math.sqrt((double) (dx * dx + dy * dy + dz * dz));
            if (distance <= (double) offset) {
                Location point = points.get(0);
                points.clear();
                points.add(point);
                return points;
            } else {
                if (distance >= 1.0D) {
                    double cut = (double) offset / distance;
                    int num = (int) ((double) points.size() * cut + 0.5D);

                    for (int i = 1; i <= num && points.size() > 0; ++i) {
                        points.remove(points.size() - 1);
                    }
                }

                return points;
            }
        }
    }

    private boolean setSimplePath(Location dest) {
        GeoData.getInstance();
        List<Location> moveList = GeoData.getInstance().constructMoveList(this.actor.getLocation(), dest);
        if (moveList.isEmpty()) {
            return false;
        } else {
            this._targetRecorder.clear();
            this._targetRecorder.add(moveList);
            return true;
        }
    }

    
    public void stopMove(boolean stop, boolean validate) {
        if (this.actor.isMoving) {
            this._moveLock.lock();

            try {
                //TODO GEODATA TEST
//                if (this.actor.isMoving) {
//                    this.actor.isMoving = false;
//                    this.isFollow = false;
//                    if (this._moveTask != null) {
//                        this._moveTask.cancel(false);
//                        this._moveTask = null;
//                    }
//
//                    this.destination = null;
//                    this.moveList = null;
//                    this._targetRecorder.clear();
//                    if (validate) {
//                        this.validateLocation(this.actor.isPlayer() ? 2 : 1);
//                    }
//
//                    if (stop) {
//                        this.actor.broadcastPacket(this.actor.stopMovePacket());
//                    }
//                }
                if(!this.actor.isMoving){
                    return;
                }
                this.actor.isMoving = false;
                this.isFollow = false;
                if(this._moveTask != null){
                    this._moveTask.cancel(true);
                    this._moveTask = null;
                }
                this.destination = null;
                this.moveList = null;
                this._targetRecorder.clear();
                if(validate){
                    this.validateLocation(this.actor.isPlayer() ? 2 : 1);
                }
                if(stop)
                    this.actor.broadcastPacket(this.actor.stopMovePacket());


            } finally {
                this._moveLock.unlock();
            }

        }
    }

    
    public void setXYZ(int x, int y, int z, boolean MoveTask) {
        if (!MoveTask) {
            this.actor.stopMove();
        }

        this._moveLock.lock();

        try {
            this.actor.setXYZ(x, y, z);
        } finally {
            this._moveLock.unlock();
        }

        this.actor.revalidateZone(true);
    }

    
    public Location getDestination() {
        return this.destination;
    }

    private Location getIntersectionPoint(L2Character target) {
        if (!Util.isFacing(this.actor, target, 90)) {
            return new Location(target.getX(), target.getY(), target.getZ());
        } else {
            double angle = Util.convertHeadingToDegree(target.getHeading());
            double radian = Math.toRadians(angle - 90.0D);
            double range = target.getMoveSpeed() / 2.0D;
            return new Location((int) ((double) target.getX() - range * Math.sin(radian)), (int) ((double) target.getY() + range * Math.cos(radian)), target.getZ());
        }
    }

    private Location applyOffset(Location point, int offset) {
        if (offset <= 0) {
            return point;
        } else {
            long dx = point.getX() - this.actor.getX();
            long dy = point.getY() - this.actor.getY();
            long Dz = point.getZ() - this.actor.getZ();
            double off = Math.sqrt((double) (dx * dx + dy * dy + Dz * Dz));
            if (off <= (double) offset) {
                point.set(this.actor.getX(), this.actor.getY(), this.actor.getZ());
                return point;
            } else {
                if (off >= 1.0D) {
                    double var11 = (double) offset / off;
                    point.setX(this.actor.getX() - (int) ((double) dx * var11 + 0.5D));
                    point.setY(this.actor.getY() - (int) ((double) dy * var11 + 0.5D));
                    point.setZ(this.actor.getZ() - (int) ((double) Dz * var11 + 0.5D));
                    if (!this.actor.isFlying() && !this.actor.isInBoat() && !this.actor.isInWater() && !this.actor.isVehicle()) {
                        point.correctGeoZ();
                    }
                }

                return point;
            }
        }
    }

    public class MoveNextTask implements Runnable {
        private double alldist;
        private double donedist;
        private L2Character actor;

        public MoveNextTask() {
        }

        public MoveNextTask setDist(L2Character actor, double alldist) {
            this.actor = actor;
            this.alldist = alldist;
            this.donedist = 0.0D;
            return this;
        }

        public void run() {
            if (this.actor.isMoving) {
                MoveController.this._moveLock.lock();

                try {
                    if (!this.actor.isMoving) {
                        return;
                    }

                    if (this.actor.isMovementDisabled()) {
                        this.actor.stopMove();
                        return;
                    }

                    L2Character follow = null;
                    double speed = this.actor.getMoveSpeed();
                    if (speed <= 0.0D) {
                        this.actor.stopMove();
                        return;
                    }

                    long now = System.currentTimeMillis();
                    if (MoveController.this.isFollow) {
                        follow = this.actor.getAI().getFollowTarget();
                        if (follow == null || follow.isInvisible()) {
                            this.actor.stopMove();
                            return;
                        }

                        if (this.actor.isInRangeZ(follow, (long) MoveController.this._offset) && GeoData.getInstance().canSeeTarget(this.actor, follow, false)) {
                            this.actor.stopMove();
                            ThreadPoolManager.getInstance().executeGeneral(new NotifyAITask(this.actor, CtrlEvent.EVT_ARRIVED));
                            return;
                        }
                    }
                    if (this.alldist <= 0.0D) {
                        this.actor.moveNext(false);
                        return;
                    }

                    this.donedist += (double) (now - MoveController.this._startMoveTime) * MoveController.this._previousSpeed / 1000.0D;
                    double calc = this.donedist / this.alldist;
                    if (calc < 0.0D) {
                        calc = 0.0D;
                    }

                    if (calc >= 1.0D) {
                        this.actor.moveNext(false);
                        return;
                    }
                    //TODO GEODATA TEST

                    if (!this.actor.isMovementDisabled()) {
                        Location loc = null;
                        int index = (int)((double)MoveController.this.moveList.size() * calc);
                        if (index >= MoveController.this.moveList.size()) {
                            index = MoveController.this.moveList.size() - 1;
                        }

                        if (index < 0) {
                            index = 0;
                        }

                        loc = ((Location)MoveController.this.moveList.get(index)).clone().geo2world();
                        if (!this.actor.isFlying() && !this.actor.isInBoat() && !this.actor.isInWater() && !this.actor.isVehicle() && loc.getZ() - this.actor.getZ() > 256) {
                            this.actor.stopMove();
                            return;
                        }

                        if (loc != null && !this.actor.isMovementDisabled()) {
                            this.actor.setLoc(loc, true);
                            if (this.actor.isMovementDisabled()) {
                                this.actor.stopMove();
                                return;
                            }

                            if (MoveController.this.isFollow && now - MoveController.this._followTimestamp > (long)(MoveController.this._forestalling ? 500 : 1000) && follow != null && !follow.isInRange(MoveController.this.movingDestTempPos, Math.max(100, MoveController.this._offset))) {
                                if (Math.abs(this.actor.getZ() - loc.getZ()) > 1000 && !this.actor.isFlying()) {
                                    this.actor.sendPacket(SystemMessageId.CANT_SEE_TARGET);
                                    this.actor.stopMove();
                                    return;
                                }

                                if (MoveController.this.buildPathTo(follow.getX(), follow.getY(), follow.getZ(), MoveController.this._offset, follow, true, true)) {
                                    MoveController.this.movingDestTempPos.set(follow.getX(), follow.getY(), follow.getZ());
                                    this.actor.moveNext(true);
                                    return;
                                }

                                this.actor.stopMove();
                                return;
                            }

                            MoveController.this._previousSpeed = speed;
                            MoveController.this._startMoveTime = now;
                            MoveController.this._moveTask = ThreadPoolManager.getInstance().scheduleGeneral(this, (long)((int)this.actor.getMoveTickInterval()));
                            return;
                        }

                        this.actor.stopMove();
                        return;
                    }
                    this.actor.stopMove();
//
//                    if (this.actor.isMovementDisabled()) {
//                        this.actor.stopMove();
//                        return;
//                    }
//
//                    Location loc = null;
//                    int index = (int) ((double) MoveController.this.moveList.size() * calc);
//                    if (index >= MoveController.this.moveList.size()) {
//                        index = MoveController.this.moveList.size() - 1;
//                    }
//
//                    if (index < 0) {
//                        index = 0;
//                    }
//
//                    loc = MoveController.this.moveList.get(index).clone().geo2world();
//                    if (!this.actor.isFlying() && !this.actor.isInBoat() && !this.actor.isInWater() && !this.actor.isVehicle() && loc.getZ() - this.actor.getZ() > 256) {
//                        this.actor.stopMove();
//                        return;
//                    }
//
//                    if (loc == null || this.actor.isMovementDisabled()) {
//                        this.actor.stopMove();
//                        return;
//                    }
//
//                    this.actor.setLoc(loc, true);
//
//                    if (this.actor.isMovementDisabled()) {
//                        this.actor.stopMove();
//                        return;
//                    }
//
//                    if (MoveController.this.isFollow && now - MoveController.this._followTimestamp > (long) (MoveController.this._forestalling ? 500 : 1000) && follow != null && !follow.isInRange(MoveController.this.movingDestTempPos, (long) Math.max(100, MoveController.this._offset))) {
//                        if (Math.abs(this.actor.getZ() - loc.getZ()) > 1000 && !this.actor.isFlying()) {
//                            this.actor.sendPacket(SystemMessageId.CANT_SEE_TARGET);
//                            this.actor.stopMove();
//                            return;
//                        }
//
//                        if (MoveController.this.buildPathTo(follow.getX(), follow.getY(), follow.getZ(), MoveController.this._offset, follow, true, true)) {
//                            MoveController.this.movingDestTempPos.set(follow.getX(), follow.getY(), follow.getZ());
//                            this.actor.moveNext(true);
//                            return;
//                        }
//
//                        this.actor.stopMove();
//                        return;
//                    }
//                    this.actor.moveNext(false);
//                    MoveController.this._previousSpeed = speed;
//                    MoveController.this._startMoveTime = now;
//                    MoveController.this._moveTask = ThreadPoolManager.getInstance().scheduleGeneral(this, (int) this.actor.getMoveTickInterval());

                } catch (RuntimeException ex) {
                    Log.error(this.actor.getName() + ", x:" + this.actor.getLocation().getX() + ", y:" + this.actor.getLocation().getY() + ", z:" + this.actor.getLocation().getZ());
                    Log.error("Error in Creature Moving! ", ex);
                } finally {
                    MoveController.this._moveLock.unlock();
                }

            }
        }
    }
}
