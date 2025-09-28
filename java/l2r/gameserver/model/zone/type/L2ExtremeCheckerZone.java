/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.model.zone.type;


import gabriel.config.GabConfig;
import gabriel.events.extremeZone.ExtremeZoneManager;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.zone.L2ZoneType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 */
public class L2ExtremeCheckerZone extends L2ZoneType {

    private boolean active = false;

    List<int[]> rs = new ArrayList<>();

    public void setCoors(List<int[]> rs) {
        this.rs = rs;
    }

    public List<int[]> getCoors() {
        return rs;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public L2ExtremeCheckerZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(L2Character character) {
        if(active && !GabConfig.EXTREME_EVENT_RADIUS_CHECK && ExtremeZoneManager.getInstance().isStarted()) {
            character.setInsideZone(ZoneIdType.NO_SUMMON_FRIEND, true);
            character.setInsideZone(ZoneIdType.EXTREME_CHECKER, true);
            if (ExtremeZoneManager.getInstance().isStarted()) {
                character.sendMessage("You entered the Extreme Zone Contest! Stay inside to gain rewards!");
            }
        }
    }

    @Override
    protected void onExit(L2Character character) {
        if(active && !GabConfig.EXTREME_EVENT_RADIUS_CHECK && ExtremeZoneManager.getInstance().isStarted()) {
            character.setInsideZone(ZoneIdType.NO_SUMMON_FRIEND, false);
            character.setInsideZone(ZoneIdType.EXTREME_CHECKER, false);
            if (ExtremeZoneManager.getInstance().isStarted()) {
                character.sendMessage("You left the Extreme Zone!");
            }
        }
    }

    @Override
    public void onDieInside(L2Character character)
    {
    }

    @Override
    public void onReviveInside(L2Character character)
    {
    }
}