package l2r.gameserver.communitybbs.Managers;

import l2r.features.museum.MuseumCategory;
import l2r.features.museum.MuseumManager;
import l2r.features.museum.RefreshTime;
import l2r.features.museum.TopPlayer;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.text.NumberFormat;
import java.util.*;

public class MuseumBBSManager extends BaseBBSManager {
    public static String MUSEUM_BBS_CMD;

    @Override
    public void cbByPass(String command, L2PcInstance activeChar) {
        String html = "<html><body scroll=no><title>Ranking Statistics</title><img src=L2UI.SquareBlank width=1 height=6/>";
        command = command.substring((command.length() > MuseumBBSManager.MUSEUM_BBS_CMD.length()) ? (MuseumBBSManager.MUSEUM_BBS_CMD.length() + 1) : MuseumBBSManager.MUSEUM_BBS_CMD.length());
        StringTokenizer st = new StringTokenizer(command, ";");
        String cmd = "main";
        int type = 0;
        int postType = 0;
        if (st.hasMoreTokens()) {
            cmd = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            type = Integer.parseInt(st.nextToken());
        }
        if (st.hasMoreTokens()) {
            postType = Integer.parseInt(st.nextToken());
        }
        if (cmd.startsWith("main")) {
            html += this.showTops(type, postType);
        } else if (cmd.startsWith("personal")) {
            html += this.showPlayerTops(activeChar, type);
        }
        html += "</body></html>";
        this.separateAndSend(html, activeChar);
    }

    public String showTops(int type, int postType) {
        MuseumCategory cat = MuseumManager.getInstance().getAllCategories().get((type * 256) + postType);
        String html = "";
        html += "<table cellspacing=-4><tr>";
        html += "<td width=12><img src=L2UI_CT1.Tab_DF_Bg_line height=23 width=12/></td>";
        html += "<td align=center><img src=L2UI.SquareBlank width=1 height=5/><table background=L2UI_CT1.Tab_DF_Tab_Selected width=150 height=24><tr><td align=center width=150><font color=e6dcbe>View Server Record</font></td></tr></table></td>";
        html = html + "<td><img src=L2UI.SquareBlank width=1 height=5/><button value=\"View My Record\" action=\"bypass " + MuseumBBSManager.MUSEUM_BBS_CMD + ";personal\" fore=\"L2UI_CT1.Tab_DF_Tab_Unselected\" back=\"L2UI_CT1.Tab_DF_Tab_Unselected_Over\" width=\"150\" height=\"24\"/></td>";
        html += "<td><img src=L2UI_CT1.Tab_DF_Bg_line height=23 width=800/></td>";
        html += "</tr></table>";
        html += "<img src=L2UI.SquareBlank width=1 height=5/>";
        html += "<table><tr><td width=5></td>";
        html += "<td>";
        html += "<table cellspacing=-6>";
        for (Map.Entry<Integer, String> entry : MuseumManager.getInstance().getAllCategoryNames().entrySet()) {
            ArrayList<MuseumCategory> categories = MuseumManager.getInstance().getAllCategoriesByCategoryId(entry.getKey());
            if (categories == null) {
                continue;
            }
            if (entry.getKey() == type) {
                html = html + "<tr><td><table background=L2UI_CT1.Button_DF_Down width=255 height=24><tr><td align=center width=255>[-] " + entry.getValue() + "</td></tr></table></td></tr>";
                html += "<tr><td width=240 align=center><img src=L2UI.SquareBlank width=1 height=11/>";
                for (MuseumCategory category : categories) {
                    html = html + "<table width=240 bgcolor=" + ((category.getTypeId() == postType) ? "4C3D28" : (((category.getTypeId() % 2) == 0) ? "000000" : "111111")) + "><tr><td width=240 align=center><font color=FFFFFF name=hs><a action=\"bypass " + MuseumBBSManager.MUSEUM_BBS_CMD + ";main;" + type + ";" + category.getTypeId() + "\">" + category.getTypeName() + "</a></font></td></tr></table>";
                }
                html += "<img src=L2UI.SquareBlank width=1 height=10/></td></tr>";
            } else {
                html = html + "<tr><td><button value=\"[+] " + entry.getValue() + "\" action=\"bypass " + MuseumBBSManager.MUSEUM_BBS_CMD + ";main;" + entry.getKey() + "\" fore=\"L2UI_CT1.Button_DF\" back=\"L2UI_CT1.Button_DF_Down\" width=\"255\" height=\"24\"/></td></tr>";
            }
        }
        html += "</table>";
        html += "</td>";
        html += "<td>";
        html += "<table cellspacing=-6><tr>";
        if (!cat.getRefreshTime().equals(RefreshTime.Total)) {
            html = html + "<td><button value=\"" + cat.getRefreshTime().name() + " Rankings\" action=\"\" fore=\"L2UI_CT1.Button_DF_Calculator\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" width=\"257\" height=\"24\"/></td><td width=12></td>";
        }
        html = html + "<td><button value=\"Total Rankings\" action=\"\" fore=\"L2UI_CT1.Button_DF_Calculator\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" width=\"" + (cat.getRefreshTime().equals(RefreshTime.Total) ? 517 : 257) + "\" height=\"24\"/></td>";
        html += "</tr></table>";
        html += "<table cellspacing=-3><tr>";
        html += "<td>";
        html += "<table><tr><td><img src=\"L2UI.SquareGray\" width=249 height=1/>";
        HashMap<Integer, TopPlayer> players = cat.getRefreshTime().equals(RefreshTime.Total) ? cat.getAllTotalTops() : cat.getAllTops();
        for (int i = 0; i < 10; ++i) {
            String name = "No information.";
            String value = "No information.";
            int cellSpacing = -1;
            if (players.size() > i) {
                TopPlayer player = players.get(i + 1);
                if (player != null) {
                    name = player.getName();
                    long count = player.getCount();
                    value = this.convertToValue(count, cat.isTimer(), cat.getAdditionalText());
                    cellSpacing = ((count > 999L) ? -3 : -2);
                }
            }
            String bgColor = (i == 0) ? "746833" : ((i == 1) ? "4e4332" : ((i == 2) ? "352e1e" : (((i % 2) == 1) ? "171612" : "23221e")));
            String numberColor = (i == 0) ? "ffca37" : ((i == 1) ? "949499" : ((i == 2) ? "b37a4d" : "dededf"));
            String nameColor = (i == 0) ? "eac842" : ((i == 1) ? "dbdbdb" : ((i == 2) ? "d29b65" : "e2e2e0"));
            String valueColor = (i == 0) ? "eee79f" : "a78d6c";
            html = html + "<table width=250 bgcolor=" + bgColor + " height=42><tr>";
            html = html + "<td width=50 align=center><font color=" + numberColor + " name=ScreenMessageLarge />" + ((i < 3) ? ("{" + (i + 1) + "}") : (i + 1)) + "</font></td>";
            html += "<td width=200 align=left>";
            html = html + "<table cellspacing=" + cellSpacing + "><tr><td width=200><font color=" + nameColor + " name=ScreenMessageSmall>" + name + "</font></td></tr><tr><td width=200><font color=" + valueColor + " name=ScreenMessageSmall>" + value + "</font></td></tr></table>";
            html += "<img src=\"L2UI.SquareBlank\" width=1 height=5/></td>";
            html += "";
            html += "</tr></table><img src=\"L2UI.SquareGray\" width=250 height=1/>";
        }
        html += "</td></tr></table>";
        html += "</td>";
        html += "<td>";
        html += "<table><tr><td><img src=\"L2UI.SquareGray\" width=249 height=1/>";
        for (int i = 10 - (cat.getRefreshTime().equals(RefreshTime.Total) ? 0 : 10); i < (20 - (cat.getRefreshTime().equals(RefreshTime.Total) ? 0 : 10)); ++i) {
            String name = "No information.";
            String value = "No information.";
            int cellSpacing = -1;
            if (cat.getAllTotalTops().size() > i) {
                TopPlayer player = cat.getAllTotalTops().get(i + 1);
                if (player != null) {
                    name = player.getName();
                    value = this.convertToValue(player.getCount(), cat.isTimer(), cat.getAdditionalText());
                    cellSpacing = ((player.getCount() > 999L) ? -3 : -2);
                }
            }
            String bgColor = (i == 0) ? "746833" : ((i == 1) ? "4e4332" : ((i == 2) ? "352e1e" : (((i % 2) == 1) ? "171612" : "23221e")));
            String numberColor = (i == 0) ? "ffca37" : ((i == 1) ? "949499" : ((i == 2) ? "b37a4d" : "dededf"));
            String nameColor = (i == 0) ? "eac842" : ((i == 1) ? "dbdbdb" : ((i == 2) ? "d29b65" : "e2e2e0"));
            String valueColor = (i == 0) ? "eee79f" : "a78d6c";
            html = html + "<table width=250 bgcolor=" + bgColor + " height=42><tr>";
            html = html + "<td width=50 align=center><font color=" + numberColor + " name=ScreenMessageLarge />" + ((i < 3) ? ("{" + (i + 1) + "}") : (i + 1)) + "</font></td>";
            html += "<td width=200 align=left>";
            html = html + "<table cellspacing=" + cellSpacing + "><tr><td width=200><font color=" + nameColor + " name=ScreenMessageSmall>" + name + "</font></td></tr><tr><td width=200><font color=" + valueColor + " name=ScreenMessageSmall>" + value + "</font></td></tr></table>";
            html += "<img src=\"L2UI.SquareBlank\" width=1 height=5/></td>";
            html += "";
            html += "</tr></table><img src=\"L2UI.SquareGray\" width=249 height=1/>";
        }
        html += "</td></tr></table>";
        html += "</td></tr></table>";
        html += "</td>";
        html += "</tr></table>";
        return html;
    }

    public String showPlayerTops(L2PcInstance player, int type) {
        String[] dailyType =
                {
                        "Monthly",
                        "Weekly",
                        "Daily"
                };
        String html = "";
        html += "<table cellspacing=-4><tr>";
        html += "<td width=12><img src=L2UI_CT1.Tab_DF_Bg_line height=23 width=12/></td>";
        html = html + "<td><img src=L2UI.SquareBlank width=1 height=5/><button value=\"View Server Record\" action=\"bypass " + MuseumBBSManager.MUSEUM_BBS_CMD + ";main\" fore=\"L2UI_CT1.Tab_DF_Tab_Unselected\" back=\"L2UI_CT1.Tab_DF_Tab_Unselected_Over\" width=\"150\" height=\"24\"/></td>";
        html += "<td align=center><img src=L2UI.SquareBlank width=1 height=5/><table background=L2UI_CT1.Tab_DF_Tab_Selected width=150 height=24><tr><td align=center width=150><font color=e6dcbe>View My Record</font></td></tr></table></td>";
        html += "<td><img src=L2UI_CT1.Tab_DF_Bg_line height=23 width=800/></td>";
        html += "</tr></table>";
        html += "<img src=L2UI.SquareBlank width=1 height=5/>";
        html += "<table><tr><td width=5></td>";
        html += "<td>";
        html += "<table cellspacing=-6>";
        for (Map.Entry<Integer, String> entry : MuseumManager.getInstance().getAllCategoryNames().entrySet()) {
            ArrayList<MuseumCategory> categories = MuseumManager.getInstance().getAllCategoriesByCategoryId(entry.getKey());
            if (categories == null) {
                continue;
            }
            if (entry.getKey() == type) {
                html = html + "<tr><td><table background=L2UI_CT1.Button_DF_Down width=155 height=24><tr><td align=center width=155>[-] " + entry.getValue() + "</td></tr></table><img src=L2UI.SquareBlank width=1 height=6/></td></tr>";
            } else {
                html = html + "<tr><td><button value=\"[+] " + entry.getValue() + "\" action=\"bypass " + MuseumBBSManager.MUSEUM_BBS_CMD + ";personal;" + entry.getKey() + "\" fore=\"L2UI_CT1.Button_DF\" back=\"L2UI_CT1.Button_DF_Down\" width=\"155\" height=\"24\"/></td></tr>";
            }
        }
        html += "</table>";
        html += "</td>";
        html += "<td>";
        ArrayList<MuseumCategory> categories2 = MuseumManager.getInstance().getAllCategoriesByCategoryId(type);
        String[] typeHtml1 =
                {
                        "",
                        "",
                        "",
                        ""
                };
        String[] typeHtml2 =
                {
                        "",
                        "",
                        "",
                        ""
                };
        String[] typeHtml3 =
                {
                        "",
                        "",
                        "",
                        ""
                };
        int[] c =
                {
                        0,
                        0,
                        0,
                        0
                };
        for (MuseumCategory cat : categories2) {
            int h = cat.getRefreshTime().ordinal();
            if (typeHtml1[h].equals("")) {
                StringBuilder sb = new StringBuilder();
                String[] array = typeHtml1;
                int n = h;
                array[n] = sb.append(array[n]).append("<table cellspacing=-5><tr>").toString();
                StringBuilder sb2 = new StringBuilder();
                String[] array2 = typeHtml1;
                int n2 = h;
                array2[n2] = sb2.append(array2[n2]).append("<td width=10></td>").toString();
                StringBuilder sb3 = new StringBuilder();
                String[] array3 = typeHtml1;
                int n3 = h;
                array3[n3] = sb3.append(array3[n3]).append("<td><button value=\"Item\" action=\"\" fore=\"L2UI_CT1.Button_DF_Calculator\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" width=\"").append((h > 0) ? 267 : 400).append("\" height=\"24\"/></td>").toString();
                if (h > 0) {
                    StringBuilder sb4 = new StringBuilder();
                    String[] array4 = typeHtml1;
                    int n4 = h;
                    array4[n4] = sb4.append(array4[n4]).append("<td><button value=\"").append(dailyType[h - 1]).append(" Total\" action=\"\" fore=\"L2UI_CT1.Button_DF_Calculator\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" width=\"176\" height=\"24\"/></td>").toString();
                }
                StringBuilder sb5 = new StringBuilder();
                String[] array5 = typeHtml1;
                int n5 = h;
                array5[n5] = sb5.append(array5[n5]).append("<td><button value=\"Total\" action=\"\" fore=\"L2UI_CT1.Button_DF_Calculator\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" width=\"").append((h > 0) ? 176 : 216).append("\" height=\"24\"/></td>").toString();
                StringBuilder sb6 = new StringBuilder();
                String[] array6 = typeHtml1;
                int n6 = h;
                array6[n6] = sb6.append(array6[n6]).append("</tr></table>").toString();
                StringBuilder sb7 = new StringBuilder();
                String[] array7 = typeHtml1;
                int n7 = h;
                array7[n7] = sb7.append(array7[n7]).append("<table><tr>").toString();
                StringBuilder sb8 = new StringBuilder();
                String[] array8 = typeHtml1;
                int n8 = h;
                array8[n8] = sb8.append(array8[n8]).append("<td>").toString();
                StringBuilder sb9 = new StringBuilder();
                String[] array9 = typeHtml1;
                int n9 = h;
                array9[n9] = sb9.append(array9[n9]).append("<font name=hs12>").toString();
                StringBuilder sb10 = new StringBuilder();
                String[] array10 = typeHtml1;
                int n10 = h;
                array10[n10] = sb10.append(array10[n10]).append("<img src=L2UI.SquareGray width=660 height=1 />").toString();
            }
            if (typeHtml3[h].equals("")) {
                StringBuilder sb11 = new StringBuilder();
                String[] array11 = typeHtml3;
                int n11 = h;
                array11[n11] = sb11.append(array11[n11]).append("</font>").toString();
                StringBuilder sb12 = new StringBuilder();
                String[] array12 = typeHtml3;
                int n12 = h;
                array12[n12] = sb12.append(array12[n12]).append("</td>").toString();
                StringBuilder sb13 = new StringBuilder();
                String[] array13 = typeHtml3;
                int n13 = h;
                array13[n13] = sb13.append(array13[n13]).append("</tr>").toString();
                StringBuilder sb14 = new StringBuilder();
                String[] array14 = typeHtml3;
                int n14 = h;
                array14[n14] = sb14.append(array14[n14]).append("</table>").toString();
                if (h < 3) {
                    StringBuilder sb15 = new StringBuilder();
                    String[] array15 = typeHtml3;
                    int n15 = h;
                    array15[n15] = sb15.append(array15[n15]).append("<br><br>").toString();
                }
            }
            StringBuilder sb16 = new StringBuilder();
            String[] array16 = typeHtml2;
            int n16 = h;
            array16[n16] = sb16.append(array16[n16]).append("<table bgcolor=").append(((c[h] % 2) == 0) ? "111111" : "000000").append("><tr><td width=").append((h > 0) ? 270 : 400).append(" align=center>").append(cat.getTypeName()).append("</td>").toString();
            long[] d = player.getMuseumPlayer().getData(cat.getType());
            if (d == null) {
                d = new long[]
                        {
                                0L,
                                0L,
                                0L,
                                0L
                        };
            }
            String value = "";
            value = this.convertToValue(d[h], cat.isTimer(), cat.getAdditionalText());
            String totalValue = "";
            if (h > 0) {
                totalValue = this.convertToValue(d[0], cat.isTimer(), cat.getAdditionalText());
            }
            if (h > 0) {
                StringBuilder sb17 = new StringBuilder();
                String[] array17 = typeHtml2;
                int n17 = h;
                array17[n17] = sb17.append(array17[n17]).append("<td width=168 align=center>").append(value).append("</td>").toString();
            }
            StringBuilder sb18 = new StringBuilder();
            String[] array18 = typeHtml2;
            int n18 = h;
            array18[n18] = sb18.append(array18[n18]).append("<td width=").append((h > 0) ? 168 : 208).append(" align=center>").append((h > 0) ? totalValue : value).append("</td>").toString();
            StringBuilder sb19 = new StringBuilder();
            String[] array19 = typeHtml2;
            int n19 = h;
            array19[n19] = sb19.append(array19[n19]).append("</tr></table>").toString();
            StringBuilder sb20 = new StringBuilder();
            String[] array20 = typeHtml2;
            int n20 = h;
            array20[n20] = sb20.append(array20[n20]).append("<img src=L2UI.SquareGray width=660 height=1 />").toString();
            int[] array21 = c;
            int n21 = h;
            ++array21[n21];
        }
        for (int i = 0; i < 4; ++i) {
            html += typeHtml1[i];
            html += typeHtml2[i];
            html += typeHtml3[i];
        }
        html += "</td>";
        html += "</tr></table>";
        return html;
    }

    public String convertToValue(long count, boolean isTimer, String additionalText) {
        String value = "";
        if (!isTimer) {
            value = NumberFormat.getNumberInstance(Locale.US).format(count);
            value = value + " " + additionalText;
        } else {
            long days = count / 86400L;
            long hours = (count % 86400L) / 3600L;
            long mins = (count % 3600L) / 60L;
            value = "";
            if (days > 0L) {
                value = value + days + " day(s) ";
            }
            value = value + hours + " hour(s) ";
            if ((mins > 0L) && (days < 1L)) {
                value = value + mins + " min(s) ";
            }
            if ((days < 1L) && (hours < 1L) && (mins < 1L)) {
                value = "0 min(s) " + count + " sec(s)";
            }
        }
        return value;
    }

    @Override
    public void parsewrite(String url, String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar) {
    }

    public static MuseumBBSManager getInstance() {
        return SingletonHolder._instance;
    }

    static {
        MuseumBBSManager.MUSEUM_BBS_CMD = "_bbsmuseum";
    }

    private static class SingletonHolder {
        protected static MuseumBBSManager _instance;

        static {
            _instance = new MuseumBBSManager();
        }
    }
}
