package l2r.geoserver.geodata;

import l2r.Config;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.Location;

import java.util.ArrayList;
import java.util.List;

import static l2r.geoserver.geodata.GeoEngine.*;

public class PathFind {
    private int geoIndex = 0;
    private PathFindBuffers.PathFindBuffer buff;
    private List<Location> path;
    private final short[] hNSWE = new short[2];
    private final Location startPoint, endPoint;
    private PathFindBuffers.GeoNode startNode, endNode, currentNode;

    public PathFind(int x, int y, int z, int destX, int destY, int destZ, L2Object obj, int geoIndex) {
        this.geoIndex = geoIndex;

        startPoint = Config.PATHFIND_BOOST == 0 ? new Location(x, y, z) : GeoEngine.moveCheckWithCollision(x, y, z, destX, destY, true, geoIndex);
        endPoint = Config.PATHFIND_BOOST != 2 || Math.abs(destZ - z) > 200 ? new Location(destX, destY, destZ) : GeoEngine.moveCheckBackwardWithCollision(destX, destY, destZ, startPoint.getX(), startPoint.getY(), true, geoIndex);

        startPoint.world2geo();
        endPoint.world2geo();

        startPoint.setZ(GeoEngine.NgetHeight(startPoint.getX(), startPoint.getY(), startPoint.getZ(), geoIndex));
        endPoint.setZ(GeoEngine.NgetHeight(endPoint.getX(), endPoint.getY(), endPoint.getZ(), geoIndex));

        int xdiff = Math.abs(endPoint.getX() - startPoint.getX());
        int ydiff = Math.abs(endPoint.getY() - startPoint.getY());

        if (xdiff == 0 && ydiff == 0) {
            if (Math.abs(endPoint.getZ() - startPoint.getZ()) < 32) {
                path = new ArrayList<>();
                path.add(0, startPoint);
            }
            return;
        }

        int mapSize = 2 * Math.max(xdiff, ydiff);

        if ((buff = PathFindBuffers.alloc(mapSize)) != null) {
            buff.offsetX = startPoint.getX() - buff.mapSize / 2;
            buff.offsetY = startPoint.getY() - buff.mapSize / 2;

            //статистика
            buff.totalUses++;
            if (obj.isPlayable())
                buff.playableUses++;

            findPath();

            buff.free();

            PathFindBuffers.recycle(buff);
        }
    }

    private List<Location> findPath() {
        startNode = buff.nodes[startPoint.getX() - buff.offsetX][startPoint.getY() - buff.offsetY].set(startPoint.getX(), startPoint.getY(), (short) startPoint.getZ());

        GeoEngine.NgetHeightAndNSWE(startPoint.getX(), startPoint.getY(), (short) startPoint.getZ(), hNSWE, geoIndex);
        startNode.z = hNSWE[0];
        startNode.nswe = hNSWE[1];
        startNode.costFromStart = 0f;
        startNode.state = PathFindBuffers.GeoNode.OPENED;
        startNode.parent = null;

        endNode = buff.nodes[endPoint.getX() - buff.offsetX][endPoint.getY() - buff.offsetY].set(endPoint.getX(), endPoint.getY(), (short) endPoint.getZ());

        startNode.costToEnd = pathCostEstimate(startNode);
        startNode.totalCost = startNode.costFromStart + startNode.costToEnd;

        buff.open.add(startNode);

        long nanos = System.nanoTime();
        long searhTime = 0;
        int itr = 0;

        while ((searhTime = System.nanoTime() - nanos) < Config.PATHFIND_MAX_TIME && (currentNode = buff.open.poll()) != null) {
            itr++;
            if (currentNode.x == endPoint.getX() && currentNode.y == endPoint.getY() && Math.abs(currentNode.z - endPoint.getZ()) < 64) {
                path = tracePath(currentNode);
                break;
            }

            handleNode(currentNode);
            currentNode.state = PathFindBuffers.GeoNode.CLOSED;
        }

        PathFindBuffers.PathFindBuffer bf = this.buff;
        bf.totalTime += searhTime;
        bf = this.buff;
        bf.totalItr += itr;

        if (path != null)
            buff.successUses++;
        else if (searhTime > Config.PATHFIND_MAX_TIME)
            buff.overtimeUses++;

        return path;
    }

    private List<Location> tracePath(PathFindBuffers.GeoNode f) {
        List<Location> locations = new ArrayList<>();
        do {
            locations.add(0, f.getLoc());
            f = f.parent;
        }
        while (f.parent != null);
        return locations;
    }

    private void handleNode(PathFindBuffers.GeoNode node) {
        int clX = node.x;
        int clY = node.y;
        short clZ = node.z;

        getHeightAndNSWE(clX, clY, clZ);
        short NSWE = hNSWE[1];

        if (Config.PATHFIND_DIAGONAL) {
            // Юго-восток
            if ((NSWE & SOUTH) == SOUTH && (NSWE & EAST) == EAST) {
                getHeightAndNSWE(clX + 1, clY, clZ);
                if ((hNSWE[1] & SOUTH) == SOUTH) {
                    getHeightAndNSWE(clX, clY + 1, clZ);
                    if ((hNSWE[1] & EAST) == EAST) {
                        handleNeighbour(clX + 1, clY + 1, node, true);
                    }
                }
            }

            // Юго-запад
            if ((NSWE & SOUTH) == SOUTH && (NSWE & WEST) == WEST) {
                getHeightAndNSWE(clX - 1, clY, clZ);
                if ((hNSWE[1] & SOUTH) == SOUTH) {
                    getHeightAndNSWE(clX, clY + 1, clZ);
                    if ((hNSWE[1] & WEST) == WEST) {
                        handleNeighbour(clX - 1, clY + 1, node, true);
                    }
                }
            }

            // Северо-восток
            if ((NSWE & NORTH) == NORTH && (NSWE & EAST) == EAST) {
                getHeightAndNSWE(clX + 1, clY, clZ);
                if ((hNSWE[1] & NORTH) == NORTH) {
                    getHeightAndNSWE(clX, clY - 1, clZ);
                    if ((hNSWE[1] & EAST) == EAST) {
                        handleNeighbour(clX + 1, clY - 1, node, true);
                    }
                }
            }

            // Северо-запад
            if ((NSWE & NORTH) == NORTH && (NSWE & WEST) == WEST) {
                getHeightAndNSWE(clX - 1, clY, clZ);
                if ((hNSWE[1] & NORTH) == NORTH) {
                    getHeightAndNSWE(clX, clY - 1, clZ);
                    if ((hNSWE[1] & WEST) == WEST) {
                        handleNeighbour(clX - 1, clY - 1, node, true);
                    }
                }
            }
        }

        // Восток
        if ((NSWE & EAST) == EAST) {
            handleNeighbour(clX + 1, clY, node, false);
        }

        // Запад
        if ((NSWE & WEST) == WEST) {
            handleNeighbour(clX - 1, clY, node, false);
        }

        // Юг
        if ((NSWE & SOUTH) == SOUTH) {
            handleNeighbour(clX, clY + 1, node, false);
        }

        // Север
        if ((NSWE & NORTH) == NORTH) {
            handleNeighbour(clX, clY - 1, node, false);
        }
    }

    private float pathCostEstimate(PathFindBuffers.GeoNode n) {
        int diffx = endNode.getX() - n.getX();
        int diffy = endNode.getY() - n.getY();
        int diffz = endNode.getZ() - n.getZ();

        return (float) Math.sqrt(diffx * diffx + diffy * diffy + diffz * diffz / 256);
    }

    private float traverseCost(PathFindBuffers.GeoNode from, PathFindBuffers.GeoNode n, boolean d) {
        if (n.nswe != NSWE_ALL || Math.abs(n.getZ() - from.getZ()) > 16)
            return 3f;
        else {
            getHeightAndNSWE(n.getX() + 1, n.getY(), n.getZ());
            if (hNSWE[1] != NSWE_ALL || Math.abs(n.getZ() - hNSWE[0]) > 16) {
                return 2f;
            }

            getHeightAndNSWE(n.getX() - 1, n.getY(), n.getZ());
            if (hNSWE[1] != NSWE_ALL || Math.abs(n.getZ() - hNSWE[0]) > 16) {
                return 2f;
            }

            getHeightAndNSWE(n.getX(), n.getY() + 1, n.getZ());
            if (hNSWE[1] != NSWE_ALL || Math.abs(n.getZ() - hNSWE[0]) > 16) {
                return 2f;
            }

            getHeightAndNSWE(n.getX(), n.getY() - 1, n.getZ());
            if (hNSWE[1] != NSWE_ALL || Math.abs(n.getZ() - hNSWE[0]) > 16) {
                return 2f;
            }
        }

        return d ? 1.414f : 1f;
    }


    private void handleNeighbour(int x, int y, PathFindBuffers.GeoNode from, boolean d) {
        int nX = x - buff.offsetX, nY = y - buff.offsetY;
        if (nX >= buff.mapSize || nX < 0 || nY >= buff.mapSize || nY < 0)
            return;

        PathFindBuffers.GeoNode n = buff.nodes[nX][nY];
        float newCost;

        if (!n.isSet()) {
            n = n.set(x, y, from.z);
            GeoEngine.NgetHeightAndNSWE(x, y, from.z, hNSWE, geoIndex);
            n.z = hNSWE[0];
            n.nswe = hNSWE[1];
        }

        int height = Math.abs(n.z - from.z);
        if (height > Config.PATHFIND_MAX_Z_DIFF || n.nswe == NSWE_NONE)
            return;

        newCost = from.costFromStart + traverseCost(from, n, d);
        if (n.state == PathFindBuffers.GeoNode.OPENED || n.state == PathFindBuffers.GeoNode.CLOSED) {
            if (n.costFromStart <= newCost)
                return;
        }

        if (n.state == PathFindBuffers.GeoNode.NONE)
            n.costToEnd = pathCostEstimate(n);

        n.parent = from;
        n.costFromStart = newCost;
        n.totalCost = n.costFromStart + n.costToEnd;

        if (n.state == PathFindBuffers.GeoNode.OPENED)
            return;

        n.state = PathFindBuffers.GeoNode.OPENED;
        buff.open.add(n);
    }

    private void getHeightAndNSWE(int x, int y, short z) {
        int nX = x - buff.offsetX, nY = y - buff.offsetY;
        if (nX >= buff.mapSize || nX < 0 || nY >= buff.mapSize || nY < 0) {
            hNSWE[1] = NSWE_NONE; // Затычка
            return;
        }

        PathFindBuffers.GeoNode n = buff.nodes[nX][nY];
        if (!n.isSet()) {
            n = n.set(x, y, z);
            GeoEngine.NgetHeightAndNSWE(x, y, z, hNSWE, geoIndex);
            n.z = hNSWE[0];
            n.nswe = hNSWE[1];
        } else {
            hNSWE[0] = n.z;
            hNSWE[1] = n.nswe;
        }
    }

    public List<Location> getPath() {
        return path;
    }
}
