/*
 * Copyright (C) 2004-2015 L2J DataPack
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

import l2r.gameserver.enums.ShotType;
import l2r.gameserver.features.balanceEngine.BalancerConfigs;
import l2r.gameserver.features.balanceEngine.classBalancer.ClassBalanceManager;
import l2r.gameserver.features.balanceEngine.skillBalancer.SkillsBalanceManager;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.stats.Formulas;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

/**
 * Magical Attack MP effect.
 *
 * @author Adry_85
 */
public final class MagicalAttackMp extends L2Effect {
	public MagicalAttackMp(Env env, EffectTemplate template) {
		super(env, template);
	}

	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.MAGICAL_ATTACK_MP;
	}

	@Override
	public boolean isInstant() {
		return true;
	}

	@Override
	public boolean onStart() {
		if (getEffected().isInvul() || getEffected().isMpBlocked()) {
			return false;
		}
//		if (!Formulas.calcMagicAffected(getEffector(), getEffected(), getSkill())) {
//			if (getEffector().isPlayer()) {
//				getEffector().sendPacket(SystemMessageId.ATTACK_FAILED);
//			}
//			if (getEffected().isPlayer()) {
//				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_RESISTED_C2_DRAIN2);
//				sm.addCharName(getEffected());
//				sm.addCharName(getEffector());
//				getEffected().sendPacket(sm);
//			}
//			return false;
//		}

		L2Character target = getEffected();
		L2Character activeChar = getEffector();

		if (activeChar.isAlikeDead()) {
			return false;
		}

		boolean sps = getSkill().useSpiritShot() && activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = getSkill().useSpiritShot() && activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		final byte shld = Formulas.calcShldUse(activeChar, target, getSkill());
		final boolean mcrit = Formulas.calcMCrit(activeChar.getMCriticalHit(target, getSkill()));
		double damage = Formulas.calcManaDam(activeChar, target, getSkill(), shld, sps, bss, mcrit);

		int skillId = getSkill().getId();
		double[] svsAll = SkillsBalanceManager.getInstance().getBalance((skillId * -1) - 65536, getEffector().isPlayer() && ((L2PcInstance) getEffector()).isInOlympiadMode());
		if ((svsAll != null) && (BalancerConfigs.SKILLS_BALANCER_AFFECTS_MONSTERS || (target instanceof L2Playable))) {
			damage *= svsAll[1];
		}
		if ((target instanceof L2PcInstance) || (target instanceof L2Summon)) {
			L2PcInstance t = target instanceof L2PcInstance ? target.getActingPlayer() : ((L2Summon) target).getOwner();
			int targetClassId = SkillsBalanceManager.getInstance().getClassId(t.getClassId().getId());
			double[] vsTarget = SkillsBalanceManager.getInstance().getBalance(skillId + (targetClassId * 65536), t.isInOlympiadMode());
			if (vsTarget != null) {
				damage *= vsTarget[1];
			}
		}

		if ((getEffector() instanceof L2PcInstance) || (getEffector() instanceof L2Summon)) {
			L2PcInstance player = getEffector() instanceof L2PcInstance ? getEffector().getActingPlayer() : ((L2Summon) getEffector()).getOwner();
			int playerClassId = ClassBalanceManager.getInstance().getClassId(player.getClassId().getId());
			double[] vsAll = ClassBalanceManager.getInstance().getBalance((playerClassId * -256), player.isInOlympiadMode());
			if ((vsAll != null) && (BalancerConfigs.CLASS_BALANCER_AFFECTS_MONSTERS || (target instanceof L2Playable))) {
				damage *= vsAll[4];
			}
			if ((target instanceof L2PcInstance) || (target instanceof L2Summon)) {
				L2PcInstance t = target instanceof L2PcInstance ? target.getActingPlayer() : ((L2Summon) target).getOwner();
				int targetClassId = ClassBalanceManager.getInstance().getClassId(t.getClassId().getId());
				double[] vsTarget = ClassBalanceManager.getInstance().getBalance((playerClassId * 256) + targetClassId, player.isInOlympiadMode());
				if (vsTarget != null) {
					damage *= vsTarget[4];
				}
			}
		}
		double mp = (damage > target.getCurrentMp() ? target.getCurrentMp() : damage);

		if (damage > 0) {
			target.stopEffectsOnDamage(true);
			target.setCurrentMp(target.getCurrentMp() - mp);
		}

		if (target.isPlayer()) {
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_MP_HAS_BEEN_DRAINED_BY_C1);
			sm.addCharName(activeChar);
			sm.addInt((int) mp);
			target.sendPacket(sm);
		}

		if (activeChar.isPlayer()) {
			SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENTS_MP_WAS_REDUCED_BY_S1);
			sm2.addInt((int) mp);
			activeChar.sendPacket(sm2);
		}
		return true;
	}
}