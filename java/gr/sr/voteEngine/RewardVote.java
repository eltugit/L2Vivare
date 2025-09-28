package gr.sr.voteEngine;


import gr.sr.configsEngine.configs.impl.GetRewardVoteSystemConfigs;
import gr.sr.premiumEngine.PremiumDuration;
import gr.sr.premiumEngine.PremiumHandler;
import gr.sr.utils.db.DbUtils;
import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class RewardVote implements IVoicedCommandHandler {
    private static final Logger log = LoggerFactory.getLogger(RewardVote.class);
    private static final String[] commands;
    private final Long[][] rewardList;
    public static List<L2PcInstance> _cannotUsePlayers;

    public RewardVote() {
        this.rewardList = GetRewardVoteSystemConfigs.REWARDS_LIST;
    }
    
    public boolean useVoicedCommand(String command, L2PcInstance player, String playerHWID) {
        if (player == null) {
            return false;
        } else {
            if (command.equalsIgnoreCase(GetRewardVoteSystemConfigs.COMMAND)) {
                sendHtml(player, "main", (String)null, (String)null);
            } else if (command.equalsIgnoreCase("getRewardInfo")) {
                sendHtml(player, "information", "%url%", GetRewardVoteSystemConfigs.VOTE_SITE_BANNERS_URL);
            } else {
                int itemId;
                if (command.equalsIgnoreCase("getRewardList")) {
                    String html = "";
                    if (this.rewardList != null) {
                        Long[][] listReward;
                        int rewardlistLength = (listReward = this.rewardList).length;

                        for(int i = 0; i < rewardlistLength; ++i) {
                            Long[] rewardForVote = listReward[i];
                            itemId = getRnd((rewardForVote)[0]);
                            String amount = String.valueOf(rewardForVote[1]);
                            String chance = String.valueOf(rewardForVote[2]);
                            html = html + "<table border=0 cellspacing=0 cellpadding=0 width=275>";
                            html = html + "<tr>";
                            html = html + "\t<td width=250 height=58 align=center valign=top>";
                            html = html + "\t\t<table border=0 width=240 height=38 cellspacing=4 cellpadding=3 bgcolor=232836>";
                            html = html + "\t\t\t<tr>";
                            html = html + "\t\t\t\t<td width=40 align=right valign=top>";
                            html = html + "\t\t\t\t\t<img src=" + ItemData.getInstance().getTemplate(itemId).getIcon() + " width=32 height=32>";
                            html = html + "\t\t\t\t</td>";
                            html = html + "\t\t\t\t<td width=340 align=left valign=center>";
                            html = html + "\t\t\t\t\t<font color=ac9887>" + ItemData.getInstance().getTemplate(itemId).getName() + "</font><br1><font color=7f7f7f>Amount:</font> " + amount + " <font color=7f7f7f>, Chance:</font> " + chance + "%";
                            html = html + "\t\t\t\t</td>";
                            html = html + "\t\t\t</tr>";
                            html = html + "\t\t</table>";
                            html = html + "\t</td>";
                            html = html + "</tr>";
                            html = html + "</table>";
                        }
                    }

                    sendHtml(player, "rewardList", "%rewardlist%", html);
                } else if (command.equalsIgnoreCase("getRewardRequest")) {
                    if (player.getLevel() < GetRewardVoteSystemConfigs.NEEDED_LEVEL) {
                        player.sendMessage("You need to be at least level 20 to use vote reward.");
                        sendHtml(player, "main", (String)null, (String)null);
                        return false;
                    }

                    if (_cannotUsePlayers.contains(player)) {
                        player.sendMessage("You can use this command only once at " + GetRewardVoteSystemConfigs.WAIT_TIME + " minutes.");
                        sendHtml(player, "main", (String)null, (String)null);
                        return false;
                    }

                    player.sendMessage("Checking if you voted... It may take a while!");
                    _cannotUsePlayers.add(player);
                    ThreadPoolManager.getInstance().scheduleGeneral(new CheckVote(this, player), (long)(GetRewardVoteSystemConfigs.WAIT_TIME * 60 * 1000));
                    String ip = player.getClient().getConnectionAddress().getHostAddress();
                    playerHWID = player.getClient().getHWID();
                    if (playerHWID == null) {
                        playerHWID = "";
                    }

                    long timeVoted = VoteRead.checkVotedIP(ip);
                    if (timeVoted <= 0L) {
                        player.sendMessage("You didnt voted. Try again later!");
                        sendHtml(player, "main", null, null);
                        return false;
                    }

                    if (canVote(timeVoted, ip, player, playerHWID)) {
                        updateVoted(timeVoted, ip, player, playerHWID);

                        Long[][] rwdList = this.rewardList;
                        itemId = rwdList.length;

                        for(int i = 0; i < itemId; ++i) {
                            Long[] rwd = rwdList[i];
                            if (Rnd.get(100) <= getRnd(rwd[2])) {
                                player.addItem("VoteReward", getRnd(rwd[0]), (long) getRnd(rwd[1]), player, true);
                            }
                        }

                        if (GetRewardVoteSystemConfigs.REWARD_PREMIUM_TIME > 0 && !player.isPremium()) {
                            PremiumHandler.addPremiumServices(GetRewardVoteSystemConfigs.REWARD_PREMIUM_TIME, player, PremiumDuration.DAYS);
                        }

                        sendHtml(player, "main", (String)null, (String)null);
                        player.sendMessage("Successfully rewarded.");
                        if (GetRewardVoteSystemConfigs.ENABLE_CONSOLE_LOG) {
                            log.info(player.getName() + "[IP:" + command + "] got rewarded for voting.");
                        }
                    }
                }
            }

            return false;
        }
    }
    private static void updateVoted(long timeVoted, String ip, L2PcInstance player, String playerHWID) {
        updateVotes(timeVoted, player.getAccountName(), VoteTypeRV.ACCOUNT_NAME);
        updateVotes(timeVoted, ip, VoteTypeRV.NETWORK_IP);
        if (GetRewardVoteSystemConfigs.ENABLE_HWID_CHECK) {
            updateVotes(timeVoted, playerHWID, VoteTypeRV.HWID_CHECK);
        }

    }
    private static boolean canVote(long var0, String var2, L2PcInstance var3, String var4) {
        int var5 = canTakeReward(var0, var3.getAccountName(), VoteTypeRV.ACCOUNT_NAME);
        int var6 = canTakeReward(var0, var2, VoteTypeRV.NETWORK_IP);
        int var7 = canTakeReward(var0, var4, VoteTypeRV.HWID_CHECK);
        var5 = Math.max(var5, Math.max(var6, var7));
        if (var5 > 0) {
            if (var5 > 60) {
                var3.sendMessage("You can vote only once at 12 hours and 5 minutes. You still have to wait " + var5 / 60 + " hours " + var5 % 60 + " minutes.");
            } else {
                var3.sendMessage("You can vote only once at 12 hours and 5 minutes. You still have to wait " + var5 + " minutes.");
            }

            return false;
        } else {
            return true;
        }
    }
    
    public String[] getVoicedCommandList() {
        return commands;
    }

    private static void updateVotes(long var0, String var2, VoteTypeRV var3) {
        Connection var4 = null;
        PreparedStatement var5 = null;
        ResultSet var6 = null;

        try {
            (var5 = (var4 = L2DatabaseFactory.getInstance().getConnection()).prepareStatement("SELECT * FROM votes WHERE value=? AND value_type=?")).setString(1, var2);
            var5.setInt(2, var3.ordinal());
            if ((var6 = var5.executeQuery()).next()) {
                int var7 = var6.getInt("vote_count");
                PreparedStatement var8 = null;

                try {
                    try {
                        (var8 = var4.prepareStatement("UPDATE votes SET date_voted_website=?, date_take_reward_in_game=?, vote_count=? WHERE value=? AND value_type=?")).setLong(1, var0);
                        var8.setLong(2, System.currentTimeMillis() / 1000L);
                        var8.setInt(3, var7 + 1);
                        var8.setString(4, var2);
                        var8.setInt(5, var3.ordinal());
                        var8.executeUpdate();
                    } catch (SQLException var29) {
                        log.error("RewardVote:insertInDataBase(long,String,ValueType): " + var29, var29);
                    }

                    return;
                } finally {
                    DbUtils.closeQuietly(var8);
                }
            } else {
                PreparedStatement var33 = null;

                try {
                    try {
                        (var33 = var4.prepareStatement("INSERT INTO votes(value, value_type, date_voted_website, date_take_reward_in_game, vote_count) VALUES (?, ?, ?, ?, ?)")).setString(1, var2);
                        var33.setInt(2, var3.ordinal());
                        var33.setLong(3, var0);
                        var33.setLong(4, System.currentTimeMillis() / 1000L);
                        var33.setInt(5, 1);
                        var33.execute();
                    } catch (SQLException var27) {
                        log.error("RewardVote:insertInDataBase(long,String,ValueType): " + var27, var27);
                    }

                    return;
                } finally {
                    DbUtils.closeQuietly(var33);
                }
            }
        } catch (SQLException var31) {
            log.error("RewardVote:insertInDataBase(long,String,ValueType): " + var31, var31);
        } finally {
            DbUtils.closeQuietly(var4, var5, var6);
        }

    }

    private static int canTakeReward(long var0, String var2, VoteTypeRV var3) {
        int var4 = 0;
        Connection var5 = null;
        PreparedStatement var6 = null;
        ResultSet var7 = null;

        try {
            (var6 = (var5 = L2DatabaseFactory.getInstance().getConnection()).prepareStatement("SELECT date_take_reward_in_game FROM votes WHERE value=? AND value_type=?")).setString(1, var2);
            var6.setInt(2, var3.ordinal());
            if ((var7 = var6.executeQuery()).next()) {
                var4 = var7.getInt("date_take_reward_in_game");
            }
        } catch (SQLException var10) {
            log.error("RewardVote:canTakeReward(long,String,String): " + var10, var10);
        } finally {
            DbUtils.closeQuietly(var5, var6, var7);
        }

        int var12;
        if (var4 == 0) {
            var12 = (int)((var0 - System.currentTimeMillis() / 1000L) / 60L);
        } else {
            var12 = (int)(((long)(var4 + 'ꣀ' + 300) - System.currentTimeMillis() / 1000L) / 60L);
        }

        return var12;
    }

    private static int getRnd(Long var0) {
        return (int)Math.max(Math.min(2147483647L, var0), -2147483648L);
    }

    private static void sendHtml(L2PcInstance var0, String var1, String var2, String var3) {
        var1 = "data/html/sunrise/getreward/" + var1 + ".htm";
        NpcHtmlMessage var4;
        (var4 = new NpcHtmlMessage()).setFile(var0, var0.getHtmlPrefix(), var1);
        if (var2 != null) {
            var4.replace(var2, var3);
        }

        var0.sendPacket(var4);
    }

    static {
        commands = new String[]{GetRewardVoteSystemConfigs.COMMAND, "getRewardList", "getRewardInfo", "getRewardRequest"};
        _cannotUsePlayers = new ArrayList();
    }

    private static enum VoteTypeRV {
        ACCOUNT_NAME, NETWORK_IP, HWID_CHECK;

        public static VoteTypeRV getType(String name) {
            return Enum.valueOf(VoteTypeRV.class, name);
        }

        VoteTypeRV() {
        }
    }
}
