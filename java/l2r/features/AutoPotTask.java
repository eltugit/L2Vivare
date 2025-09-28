package l2r.features;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ExAutoSoulShot;
import l2r.gameserver.network.serverpackets.MagicSkillUse;

import scripts.handlers.itemhandlers.ItemSkills;

/**
 * @author Gabriel Costa Souza Discord: Gabriel 'GCS'#2589 Skype - email: gabriel_costa25@hotmail.com website: l2jgabdev.com
 */
public class AutoPotTask
{

	final static int[] PROTECTED_SKILLS_IDS = // Skill Ids that will pause auto potions
			{
					922,
					443,
					837,
					1418,
					1427,
					442
			};

	Future<?> taskHp = null;
	Future<?> taskMana = null;
	Future<?> taskCp = null;
	Future<?> taskKamaels = null;

	private final List<PotHelper> playersHp = Collections.synchronizedList(new LinkedList<>());
	private final List<PotHelper> playersMana = Collections.synchronizedList(new LinkedList<>());
	private final List<PotHelper> playersCp = Collections.synchronizedList(new LinkedList<>());
	private final List<PotHelper> playersKamaels = Collections.synchronizedList(new LinkedList<>());

	protected static AutoPotTask instance;

	public static AutoPotTask getInstance()
	{
		if (instance == null)
		{
			instance = new AutoPotTask();
		}
		return instance;
	}

	/**
	 * Checks if a player has a certain pot active
	 * @param player
	 * @param itemId
	 * @return true if player is registered on the task.
	 */
	public boolean containsPot(L2PcInstance player, int itemId)
	{
		switch (itemId)
		{
			case 728: // mana potion
			{
				synchronized (playersMana){
					return playersMana.stream().anyMatch(e -> (e.getOwner() == player) && (e.getItemId() == itemId));
				}
			}
			case 1539: // greater healing potion
			{
				synchronized (playersHp){
					return playersHp.stream().anyMatch(e -> (e.getOwner() == player) && (e.getItemId() == itemId));
				}
			}
			case 5591: // greater cp potion
			case 5592: // greater cp potion
			{
				synchronized (playersCp){
					return playersCp.stream().anyMatch(e -> (e.getOwner() == player) && (e.getItemId() == itemId));
				}
			}
			case 10410: // Full Bottle of Souls - 5 Souls
			{
				synchronized (playersKamaels){
					return playersKamaels.stream().anyMatch(e -> (e.getOwner() == player) && (e.getItemId() == itemId));
				}
			}
		}
		return false;
	}

	/**
	 * Starts autopot tasks
	 */
	private AutoPotTask()
	{
		taskHp = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() ->
		{
			synchronized(playersHp){
				for (PotHelper potHelper : playersHp)
				{
					if (canUse(potHelper, 15000) && (potHelper.getOwner().getCurrentHp() < (0.90 * potHelper.getOwner().getMaxHp())))
					{
						SkillAndItemUse(potHelper.getOwner(), 2037, 1, 1539);
						potHelper.setUsed();
					}
				}
			}

		}, 3000, 3000);

		taskMana = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() ->
		{
			synchronized(playersMana){
				for (PotHelper potHelper : playersMana)
				{
					if (canUse(potHelper, 10000) && (potHelper.getOwner().getCurrentMp() < (0.90 * potHelper.getOwner().getMaxMp())))
					{
						SkillAndItemUse(potHelper.getOwner(), 10001, 1, 728);
						potHelper.setUsed();
					}
				}
			}

		}, 3000, 3000);

		taskCp = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() ->
		{
			synchronized(playersCp){
				for (PotHelper potHelper : playersCp)
				{
					if (potHelper.getItemId() == 5592)
					{
						if (canUse(potHelper, 500) && (potHelper.getOwner().getCurrentCp() < (0.90 * potHelper.getOwner().getMaxCp())))
						{
							SkillAndItemUse(potHelper.getOwner(), 2166, 2, 5592);
							potHelper.setUsed();
						}
					}
					else
					{
						if (canUse(potHelper, 500) && (potHelper.getOwner().getCurrentCp() < (0.90 * potHelper.getOwner().getMaxCp())))
						{
							SkillAndItemUse(potHelper.getOwner(), 2166, 1, 5591);
							potHelper.setUsed();
						}
					}
				}
			}

		}, 500, 500);

		taskKamaels = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() ->
		{
			synchronized(playersKamaels){
				for (PotHelper potHelper : playersKamaels)
				{
					if (canUse(potHelper, 500) && (potHelper.getOwner().getChargedSouls() < 40))
					{
						SkillAndItemUse(potHelper.getOwner(), 2499, 1, 10410);
						potHelper.setUsed();
					}
				}
			}

		}, 500, 500);
	}

	/**
	 * Handle if the user can use a certain pot or not.
	 * @param potHelper pot info
	 * @return
	 */
	private boolean canUse(PotHelper potHelper, int time)
	{
		boolean potOk = true;
		int id = potHelper.getItemId();
		L2PcInstance activeChar = potHelper.getOwner();
		if(activeChar == null || !activeChar.isOnline())
			return false;

		switch (id)
		{
			case 728: // mana potion
			{
				potOk = activeChar.manaPot;
				break;
			}
			case 1539: // greater healing potion
			{
				potOk = activeChar.hpPot;
				break;
			}
			case 5592: // greater cp potion
			{
				potOk = activeChar.cp1Pot;
				break;
			}
			case 5591: // cp potion
			{
				potOk = activeChar.cp2Pot;
				break;
			}
			case 10410: // Full Bottle of Souls - 5 Souls
			{
				potOk = activeChar.kamaelPot;
				break;
			}
		}

		if(!potOk)
			return false;


		if (activeChar.isDead() || activeChar.isInvisible() || activeChar.isInvul())
		{
			return false;
		}

		for (int skillId : PROTECTED_SKILLS_IDS)
		{
			if (activeChar.getEffectList().isAffectedBySkill(skillId))
			{
				return false;
			}
		}

		if (activeChar.getInventory().getItemByItemId(id) == null)
		{
			activeChar.sendPacket(new ExAutoSoulShot(id, 0));
			removePlayer(activeChar, id);
			return false;
		}

		if ((potHelper.getUsed() != 0) && ((Calendar.getInstance().getTimeInMillis() - potHelper.used) < time))
		{
			return false;
		}

		return true;
	}

	/**
	 * Checks if player is currently using autopot for a certain itemId
	 * @param player player in question
	 * @param itemId requested id
	 * @return true if is using auto pot
	 */
	public boolean isAutoPot(L2PcInstance player, int itemId)
	{
		switch (itemId)
		{
			case 728: // mana potion
			{
				return player.manaPot;
			}
			case 1539: // greater healing potion
			{
				return player.hpPot;
			}
			case 5592: // greater cp potion
			{
				return player.cp1Pot;
			}
			case 5591: // cp potion
			{
				return player.cp2Pot;
			}
			case 10410: // Full Bottle of Souls - 5 Souls
			{
				return player.kamaelPot;
			}
		}
		return false;
	}

	public boolean isAutoPotContains(L2PcInstance player, int itemId)
	{
		switch (itemId)
		{
			case 728: // mana potion
			{
				synchronized (playersMana){
					return playersMana.stream().anyMatch(e -> (e.getOwner().getObjectId() == player.getObjectId()) && (e.getItemId() == itemId));
				}
			}
			case 1539: // greater healing potion
			{
				synchronized (playersHp){
					return playersHp.stream().anyMatch(e -> (e.getOwner().getObjectId() == player.getObjectId()) && (e.getItemId() == itemId));
				}
			}
			case 5592: // greater cp potion
			case 5591: // cp potion
			{
				synchronized (playersCp){
					return playersCp.stream().anyMatch(e -> (e.getOwner().getObjectId() == player.getObjectId()) && (e.getItemId() == itemId));
				}
			}
			case 10410: // Full Bottle of Souls - 5 Souls
			{
				synchronized (playersKamaels){
					return playersKamaels.stream().anyMatch(e -> (e.getOwner().getObjectId() == player.getObjectId()) && (e.getItemId() == itemId));
				}
			}
		}
		return false;
	}

	public boolean checkId(int itemId)
	{
		switch (itemId)
		{
			case 728: // mana potion
			case 1539: // greater healing potion
			case 5592: // greater cp potion
			case 5591: // cp potion
			case 10410: // Full Bottle of Souls - 5 Souls
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds the player to a current autopot task
	 * @param player player in question
	 * @param itemId item Id
	 */
	public void addPlayer(L2PcInstance player, int itemId)
	{
		switch (itemId)
		{
			case 728: // mana potion
			{
				synchronized (playersMana){
					if(!isAutoPotContains(player, itemId))
						playersMana.add(new PotHelper(player, 0, itemId));
					player.manaPot = true;
				}
				break;
			}
			case 1539: // greater healing potion
			{
				synchronized (playersHp){
					if(!isAutoPotContains(player, itemId))
						playersHp.add(new PotHelper(player, 0, itemId));
					player.hpPot = true;
				}
				break;
			}
			case 5592: // greater cp potion
			{
				synchronized (playersCp) {
					if (!isAutoPotContains(player, itemId))
						playersCp.add(new PotHelper(player, 0, itemId));
					player.cp1Pot = true;
				}
				break;
			}
			case 5591: // cp potion
			{
				synchronized (playersCp) {
					if (!isAutoPotContains(player, itemId))
						playersCp.add(new PotHelper(player, 0, itemId));
					player.cp2Pot = true;
				}
				break;
			}
			case 10410: // Full Bottle of Souls - 5 Souls
			{
				synchronized (playersKamaels) {
					if (!isAutoPotContains(player, itemId))
						playersKamaels.add(new PotHelper(player, 0, itemId));
					player.kamaelPot = true;
				}
				break;
			}
		}
	}

	/**
	 * Remove player from current autopot task for a certain itemId
	 * @param player player in question
	 * @param itemId
	 */
	public void removePlayer(L2PcInstance player, int itemId)
	{
		List<PotHelper> temp = new LinkedList<>();
		switch (itemId)
		{
			case 728: // mana potion
			{
				player.manaPot = false;
//                synchronized (playersMana){
//                    playersMana.stream().filter(e -> (e.getOwner().getObjectId() == player.getObjectId()) && (e.getItemId() == itemId)).forEach(temp::add);
//                    playersMana.removeAll(temp);
//                }
				break;
			}
			case 1539: // greater healing potion
			{
				player.hpPot = false;
//                synchronized (playersHp){
//                    playersHp.stream().filter(e -> (e.getOwner().getObjectId() == player.getObjectId()) && (e.getItemId() == itemId)).forEach(temp::add);
//                    playersHp.removeAll(temp);
//                }
				break;
			}
			case 5592: // greater cp potion
			{
				player.cp1Pot = false;
				break;
			}
			case 5591: // cp potion
			{
//                synchronized (playersCp){
//                    playersCp.stream().filter(e -> (e.getOwner().getObjectId() == player.getObjectId()) && (e.getItemId() == itemId)).forEach(temp::add);
//                    playersCp.removeAll(temp);
//                }
				player.cp2Pot = false;
				break;
			}
			case 10410: // Full Bottle of Souls - 5 Souls
			{
//                synchronized (playersKamaels){
//                    playersKamaels.stream().filter(e -> (e.getOwner().getObjectId() == player.getObjectId()) && (e.getItemId() == itemId)).forEach(temp::add);
//                    playersKamaels.removeAll(temp);
//                }
				player.kamaelPot = false;
				break;
			}
		}
	}

	/**
	 * Uses the Skill and Item requested by the pot
	 * @param activeChar
	 * @param skillId
	 * @param skillLvl
	 * @param idItem
	 */
	public void SkillAndItemUse(L2PcInstance activeChar, Integer skillId, Integer skillLvl, Integer idItem)
	{
		MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, skillId, skillLvl, 0, 100);
		activeChar.broadcastPacket(msu);
		ItemSkills is = new ItemSkills();
		is.useItem(activeChar, activeChar.getInventory().getItemByItemId(idItem), true);
	}

	/**
	 * Helper class
	 */
	private class PotHelper
	{
		private final L2PcInstance owner;
		private long used;
		private final int itemId;

		public PotHelper(L2PcInstance owner, long used, int itemId)
		{
			this.owner = owner;
			this.used = used;
			this.itemId = itemId;
		}

		public L2PcInstance getOwner()
		{
			return owner;
		}

		public long getUsed()
		{
			return used;
		}

		public int getItemId()
		{
			return itemId;
		}

		public void setUsed()
		{
			used = Calendar.getInstance().getTimeInMillis();
		}

	}

}