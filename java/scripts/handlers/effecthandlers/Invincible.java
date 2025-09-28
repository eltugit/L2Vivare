/*
 * Copyright (C) 2004-2013 L2J DataPack
 *
 * This file is part of L2J DataPack.
 *
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package scripts.handlers.effecthandlers;

import gabriel.config.GabConfig;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectFlag;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.stats.Env;

public class Invincible extends L2Effect
{
	public Invincible(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.INVINCIBLE;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.INVUL.getMask();
	}


	@Override
	public boolean checkCondition(Object obj) {
		System.out.println(obj.getClass().getSimpleName());
		return super.checkCondition(obj);
	}

	@Override
	public int getAbnormalTime() {
		if(getEffected() != null && getEffected().isPlayer() && GabConfig.BLOCK_SKILL_INVUL_IDS_ON_TWFLAG_OR_SIEGE_CAST.contains(getSkill().getId())){
			L2PcInstance p = getEffected().getActingPlayer();
			if(p.isCombatFlagEquipped() || (p.getLastSkillCasted() != null && p.getLastSkillCasted().getSkill().getId() == 246)){
				return 0;
			}
		}
		return super.getAbnormalTime();
	}
	@Override
	public int getTime() {
		return super.getAbnormalTime();
	}

	@Override
	public int getRemainingTime() {
		return super.getRemainingTime();
	}

	@Override
	public boolean onActionTime() {
		return super.onActionTime();
	}

	@Override
	public boolean onStart() {
		return super.onStart();
	}
}
