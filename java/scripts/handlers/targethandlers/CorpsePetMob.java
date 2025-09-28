package scripts.handlers.targethandlers;

import l2r.Config;
import l2r.gameserver.handler.ITargetTypeHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.L2SkillType;
import l2r.gameserver.model.skills.targets.L2TargetType;
import l2r.gameserver.network.SystemMessageId;

public class CorpsePetMob implements ITargetTypeHandler {
	@Override
	public L2Object[] getTargetList(L2Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
		if (activeChar.isPlayer()) {
			target = activeChar.getSummon();
			if ((target != null) && target.isDead()) {
				return new L2Character[]
						{
								target
						};
			}
		}

		if ((target == null) || !target.isAttackable() || !target.isDead()) {
			activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			return _emptyTargetList;
		}

		if ((skill.getSkillType() == L2SkillType.SUMMON) && target.isServitor() && (target.getActingPlayer() != null) && (target.getActingPlayer().getObjectId() == activeChar.getObjectId())) {
			return _emptyTargetList;
		}

		if ((skill.getSkillType() == L2SkillType.DRAIN) && ((L2Attackable) target).isOldCorpse(activeChar.getActingPlayer(), Config.CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY, true)) {
			return _emptyTargetList;
		}

		return new L2Character[]
				{
						target
				};

//        return _emptyTargetList;
	}

	@Override
	public Enum<L2TargetType> getTargetType() {
		return L2TargetType.CORPSE_PET_MOB;
	}
}
