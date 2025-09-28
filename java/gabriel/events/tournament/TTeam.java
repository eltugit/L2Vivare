package gabriel.events.tournament;



import gabriel.events.tournament.lol.LOLRankDAO;
import l2r.Config;
import l2r.gameserver.enums.Team;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;
import l2r.gameserver.network.serverpackets.ItemList;
import l2r.gameserver.network.serverpackets.SkillCoolTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class TTeam {
    private final List<L2PcInstance> players = new ArrayList<>();
    private final List<L2PcInstance> toRemovePlayers = new ArrayList<>();
    private String leaderName;
    private boolean inGame = false;

    public int getTeamElo(int size){
        int totalElo = 0;
        for (L2PcInstance player : players) {
            totalElo += LOLRankDAO.getInstance().getPlayerElo(player, size > 2);
        }
        return totalElo / players.size();
    }


    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public TFight getFight() {
        return fight;
    }

    private TFight fight;

    public TTeam() {
    }

    
    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public void addPlayer(L2PcInstance player) {
        players.add(player);
        player.settTeam(this);
    }

    public void removePlayer(L2PcInstance player) {
        players.remove(player);
        player.settTeam(null);
    }

    public List<L2PcInstance> getPlayers() {
        return players;
    }

    public void setFight(TFight fight) {
        this.fight = fight;
    }

    public void setTeam(int val) {
        Team team = null;

        switch (val) {
            case 1:
                team = Team.BLUE;
                break;
            case 2:
                team = Team.RED;
                break;
            default:
                team = Team.NONE;
                break;
        }
        for (L2PcInstance player : players) {
            if (player != null) {
                player.setTeam(team);
            }
        }
    }

    void immobilizeTeam(boolean val) {
        for (L2PcInstance player : players) {
            if (player != null) {
                player.setIsInvul(val);
                player.setIsImmobilized(val);
                L2Summon sm = player.getSummon();
                if (sm != null) {
                    sm.setIsInvul(val);
                    sm.setIsImmobilized(val);
                }
            }
        }
    }

    boolean isTeamDead() {
        int numberOfDeaths = 0;
        for (L2PcInstance player : players)
            if (player != null)
                if (player.isDead())
                    numberOfDeaths++;

        return numberOfDeaths == players.size();
    }

    void teleportTeam(Location loc, int instanceId) {
        for (L2PcInstance player : players) {
            if (player != null) {
                if (player.isDead()) {
                    player.doRevive();
                }
                player.healToMaxPvPInstance();
                player.setInstanceId(instanceId);
                if (instanceId == 0) {
                    setInEvent(false);
                    player.setArenaQuantity(0);
                    player.settTeam(null);
                } else {
                    setInEvent(true);
                    player.setArenaQuantity(fight.getNumberOfPlayers());
                    player.settTeam(this);
                    removeHeroBuffs(player);
                    refreshSkills(player);
                }
                player.teleToLocation(loc, true);
            }
        }
    }

    private void removeHeroBuffs(L2PcInstance player) {
        L2Effect[] effects = player.getAllEffects();

        for (L2Effect e : effects) {
            if ((e != null) && (e.getSkill().isHeroSkill()) && (e.getSkill().getId() == 395 || e.getSkill().getId() == 396)) // Miracle or Berserker
            {
                e.exit();
            }
        }
    }

    private void refreshSkills(L2PcInstance player) {
        for (L2Skill skill : player.getAllSkills()) {
            if (skill.getReuseDelay() <= 900000)
                player.enableSkill(skill);
        }

        player.sendSkillList();
        player.sendPacket(new SkillCoolTime(player));
    }

    public void reward() {
        for (L2PcInstance player : players) {
            if (player != null) {
                player.addItem("Custom Reward", Config.ARENA_REWARD_ID, Config.ARENA_REWARD_COUNT, player, true);
                player.sendPacket(new ItemList(player, true));
                player.sendPacket(new ExShowScreenMessage("League of Arena: You Win!", 5000));
            }
        }
    }


    public void sendMessage(String msg) {
        for (L2PcInstance player : players) {
            if (player != null) {
                player.sendMessage(msg);
                player.sendPacket(new ExShowScreenMessage(msg, 5000));
                player.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), msg));

            }
        }
    }

    private void setInEvent(boolean val) {
        for (L2PcInstance player : players) {
            if (player != null) {
                player.setInArenaEvent(val);
            }
        }
    }

    public void clean() {
        for (L2PcInstance player : players) {
            player.setInArenaEvent(false);
            player.settTeam(null);
            toRemovePlayers.add(player);
            player.setArenaQuantity(0);
        }
        cleanPlayers();
        players.clear();
        toRemovePlayers.clear();
        fight = null;
    }

    private void cleanPlayers() {
        players.removeAll(toRemovePlayers);
    }
}
