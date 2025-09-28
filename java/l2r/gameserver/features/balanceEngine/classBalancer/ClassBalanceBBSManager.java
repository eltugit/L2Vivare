package l2r.gameserver.features.balanceEngine.classBalancer;

import l2r.gameserver.communitybbs.Managers.BaseBBSManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ClassBalanceBBSManager extends BaseBBSManager
{
	public static String CLASS_BALANCE_BBS_CMD = "_bbsbalancer";
	
	@Override
	public void cbByPass(String command, final L2PcInstance activeChar)
	{
		if (!activeChar.isGM())
		{
			return;
		}
		ClassBalanceManager.getInstance().loadSecondProffessions();
		if (command.equals("admin_classbalancer"))
		{
			command = ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";main";
		}
		String html = "<html><body><br><br>";
		command = command.substring((command.length() > ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD.length()) ? (ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD.length() + 1) : ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD.length());
		if (command.startsWith("main"))
		{
			int classId = -1;
			int targetClassId = -1;
			boolean forOlympiad = false;
			if (command.length() > 4)
			{
				final StringTokenizer st = new StringTokenizer(command.substring(5), ";");
				if (st.hasMoreTokens())
				{
					forOlympiad = st.nextToken().equals("1");
				}
				if (st.hasMoreTokens())
				{
					classId = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens())
				{
					targetClassId = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens())
				{
					classId = Integer.parseInt(st.nextToken());
				}
			}
			html += showMainPage(classId, targetClassId, forOlympiad);
		}
		else if (command.startsWith("show"))
		{
			final StringTokenizer st2 = new StringTokenizer(command.substring(5), ";");
			final boolean forOlympiad2 = st2.nextToken().equals("1");
			final int page = Integer.parseInt(st2.nextToken());
			final int classId2 = Integer.parseInt(st2.nextToken());
			final int targetClassId2 = Integer.parseInt(st2.nextToken());
			html += showPage(page, classId2, targetClassId2, forOlympiad2);
		}
		else if (command.startsWith("increase"))
		{
			final StringTokenizer st2 = new StringTokenizer(command.substring(9), ";");
			final boolean forOlympiad2 = st2.nextToken().equals("1");
			final int page = Integer.parseInt(st2.nextToken());
			final int classId2 = Integer.parseInt(st2.nextToken());
			final int targetClassId2 = Integer.parseInt(st2.nextToken());
			final int cId = Integer.parseInt(st2.nextToken());
			final int tcId = Integer.parseInt(st2.nextToken());
			final int key = Integer.parseInt(st2.nextToken());
			final int type = Integer.parseInt(st2.nextToken());
			final double value = Double.parseDouble(st2.nextToken());
			ClassBalanceManager.getInstance().updateBalance(key, cId, tcId, type, value, forOlympiad2);
			html += showPage(page, classId2, targetClassId2, forOlympiad2);
		}
		else if (command.startsWith("delete"))
		{
			final StringTokenizer st2 = new StringTokenizer(command.substring(7), ";");
			final boolean forOlympiad2 = st2.nextToken().equals("1");
			final int page = Integer.parseInt(st2.nextToken());
			final int classId2 = Integer.parseInt(st2.nextToken());
			final int targetClassId2 = Integer.parseInt(st2.nextToken());
			final int cId = Integer.parseInt(st2.nextToken());
			final int tcId = Integer.parseInt(st2.nextToken());
			final int key = Integer.parseInt(st2.nextToken());
			ClassBalanceManager.getInstance().removeBalance(key, cId, tcId, forOlympiad2);
			html += showPage(page, classId2, targetClassId2, forOlympiad2);
		}
		else if (command.startsWith("addpage"))
		{
			final StringTokenizer st2 = new StringTokenizer(command.substring(8), ";");
			final boolean forOlympiad2 = st2.nextToken().equals("1");
			final int classId3 = Integer.parseInt(st2.nextToken());
			final int targetClassId3 = Integer.parseInt(st2.nextToken());
			int race = 0;
			int tRace = 0;
			if (st2.hasMoreTokens())
			{
				race = Integer.parseInt(st2.nextToken());
			}
			if (st2.hasMoreTokens())
			{
				tRace = Integer.parseInt(st2.nextToken());
			}
			final double[] values =
			{
				1.0,
				1.0,
				1.0,
				1.0,
				1.0,
				1.0,
				1.0
			};
			for (int i = 0; (i < 7) && st2.hasMoreTokens(); ++i)
			{
				values[i] = Double.parseDouble(st2.nextToken());
			}
			html += showAddPage(classId3, targetClassId3, race, tRace, values, forOlympiad2);
		}
		else if (command.startsWith("add"))
		{
			final StringTokenizer st2 = new StringTokenizer(command.substring(4), ";");
			final boolean forOlympiad2 = st2.nextToken().equals("1");
			String className = st2.nextToken();
			if (className.startsWith(" "))
			{
				className = className.substring(1);
			}
			if (className.endsWith(" "))
			{
				className = className.substring(0, className.length() - 1);
			}
			className = className.replaceAll(" ", "");
			String targetClassName = st2.nextToken();
			if (targetClassName.startsWith(" "))
			{
				targetClassName = targetClassName.substring(1);
			}
			if (targetClassName.endsWith(" "))
			{
				targetClassName = targetClassName.substring(0, targetClassName.length() - 1);
			}
			targetClassName = targetClassName.replaceAll(" ", "");
			int classId4 = -1;
			int targetClassId4 = -1;
			if (!className.equals(""))
			{
				for (final ClassId cId2 : ClassId.values())
				{
					if (cId2.name().equalsIgnoreCase(className))
					{
						classId4 = cId2.getId();
					}
				}
			}
			if (!targetClassName.equals(""))
			{
				for (final ClassId cId2 : ClassId.values())
				{
					if (cId2.name().equalsIgnoreCase(targetClassName))
					{
						targetClassId4 = cId2.getId();
					}
				}
			}
			final double[] values =
			{
				1.0,
				1.0,
				1.0,
				1.0,
				1.0,
				1.0,
				1.0,
				-1.0,
				-1.0
			};
			for (int i = 0; (i < 7) && st2.hasMoreTokens(); ++i)
			{
				values[i] = Double.parseDouble(st2.nextToken());
			}
			values[7] = classId4;
			values[8] = targetClassId4;
			final int key = (classId4 * 256 * ((targetClassId4 == -1) ? -1 : 1)) + ((targetClassId4 == -1) ? 0 : targetClassId4);
			ClassBalanceManager.getInstance().updateBalance(key, classId4, targetClassId4, values, forOlympiad2);
			html += showPage(1, classId4, targetClassId4, forOlympiad2);
		}
		html += "</body></html>";
		separateAndSend(html, activeChar);
	}
	
	public String showMainPage(final int classId, final int targetClassId, final boolean forOlympiad)
	{
		String html = "<center>";
		html += "<font name=\"ScreenMessageLarge\">Used classes in balancer</font><br>";
		html = html + "<table width=220><tr><td width=20 align=center><img width=1 height=3 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";main;" + (forOlympiad ? 0 : 1) + ";" + classId + ";" + targetClassId + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + (forOlympiad ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + (forOlympiad ? "_checked" : "") + "\"/></td><td width=200 align=left><font name=ScreenMessageSmall color=BABABA>Show for Olympiad</font></td></tr></table>";
		html += "<table width=700><tr>";
		int i = 0;
		for (final ClassId cl : ClassId.values())
		{
			if (cl.level() >= 3)
			{
				if ((i % 12) == 0)
				{
					html += "<td width=200>";
				}
				html = html + "<table width=200 align=center bgcolor=" + (((i % 2) == 0) ? "333333" : "111111") + ">";
				String name = cl.name();
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				int cId = classId;
				int tcId = targetClassId;
				if ((cId == -1) && (tcId == -1))
				{
					cId = cl.getId();
				}
				else if ((cId == -1) && (tcId != -1))
				{
					if (tcId == cl.getId())
					{
						tcId = -1;
					}
					else
					{
						cId = cl.getId();
					}
				}
				else if ((cId != -1) && (tcId == -1))
				{
					if (cId == cl.getId())
					{
						cId = -1;
					}
					else
					{
						tcId = cl.getId();
					}
				}
				else if (tcId == cl.getId())
				{
					tcId = -1;
				}
				else if (cId == cl.getId())
				{
					cId = -1;
				}
				else
				{
					cId = tcId;
					tcId = cl.getId();
				}
				html = html + "<tr><td width=20 align=center><img width=1 height=3 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";main;" + (forOlympiad ? 1 : 0) + ";" + cId + ";" + tcId + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + (((cl.getId() == classId) || (cl.getId() == targetClassId)) ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + (((cl.getId() == classId) || (cl.getId() == targetClassId)) ? "_checked" : "") + "\"/></td><td width=200><font name=ScreenMessageSmall color=BABABA>" + name + "</font></td></tr>";
				html += "</table>";
				if ((i % 12) == 11)
				{
					html += "</td>";
				}
				++i;
			}
		}
		if (!html.endsWith("</td>"))
		{
			html += "</td>";
		}
		html += "</tr></table><br>";
		int count = 0;
		if ((classId != -1) && (targetClassId != -1))
		{
			if (ClassBalanceManager.getInstance().getAllBalances(forOlympiad).containsKey((classId * 256) + targetClassId))
			{
				++count;
			}
			if (ClassBalanceManager.getInstance().getAllBalances(forOlympiad).containsKey((targetClassId * 256) + classId))
			{
				++count;
			}
		}
		else if ((classId == -1) && (targetClassId != -1))
		{
			if (ClassBalanceManager.getInstance().getAllBalancesForIngame(forOlympiad).containsKey(targetClassId))
			{
				count += ClassBalanceManager.getInstance().getAllBalancesForIngame(forOlympiad).get(targetClassId).size();
			}
		}
		else if ((classId != -1) && (targetClassId == -1) && ClassBalanceManager.getInstance().getAllBalancesForIngame(forOlympiad).containsKey(classId))
		{
			count += ClassBalanceManager.getInstance().getAllBalancesForIngame(forOlympiad).get(classId).size();
		}
		html += "<table bgcolor=111111><tr>";
		html = html + "<td width=220><font name=ScreenMessageSmall color=CBCBCB>Available " + count + " balances:</font></td>";
		html = html + "<td><button value=\"Show\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";show;" + (forOlympiad ? 1 : 0) + ";1;" + classId + ";" + targetClassId + "\" width=\"60\" height=\"28\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" ></td>";
		html += "<td width=200></td>";
		html = html + "<td><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;0;" + classId + ";" + targetClassId + "\" width=\"28\" height=\"28\" back=\"L2UI_CT1.MiniMap_DF_PlusBtn_Red_Down\" fore=\"L2UI_CT1.MiniMap_DF_PlusBtn_Red\" ></td>";
		html += "</tr></table>";
		html += "</center>";
		return html;
	}
	
	public String showPage(final int page, final int classId, final int targetClassId, final boolean forOlympiad)
	{
		String html = "<center>";
		final HashMap<Integer, double[]> _used = new HashMap<>();
		if ((classId != -1) && (targetClassId != -1))
		{
			if (ClassBalanceManager.getInstance().getAllBalances(forOlympiad).containsKey((classId * 256) + targetClassId))
			{
				_used.put((classId * 256) + targetClassId, ClassBalanceManager.getInstance().getBalance((classId * 256) + targetClassId, forOlympiad));
			}
			if (ClassBalanceManager.getInstance().getAllBalances(forOlympiad).containsKey((targetClassId * 256) + classId))
			{
				_used.put((targetClassId * 256) + classId, ClassBalanceManager.getInstance().getBalance((targetClassId * 256) + classId, forOlympiad));
			}
		}
		else
		{
			if (classId != -1)
			{
				final ArrayList<Integer> data = ClassBalanceManager.getInstance().getBalanceForIngame(classId, forOlympiad);
				if ((data != null) && (data.size() > 0))
				{
					for (final int cl : data)
					{
						final double[] d = ClassBalanceManager.getInstance().getBalance(cl, forOlympiad);
						if (d == null)
						{
							continue;
						}
						_used.put(cl, ClassBalanceManager.getInstance().getBalance(cl, forOlympiad));
					}
				}
			}
			if (targetClassId != -1)
			{
				final ArrayList<Integer> data = ClassBalanceManager.getInstance().getBalanceForIngame(targetClassId, forOlympiad);
				if ((data != null) && (data.size() > 0))
				{
					for (final int cl : data)
					{
						final double[] d = ClassBalanceManager.getInstance().getBalance(cl, forOlympiad);
						if (d == null)
						{
							continue;
						}
						_used.put(cl, ClassBalanceManager.getInstance().getBalance(cl, forOlympiad));
					}
				}
			}
		}
		html += "<font name=\"ScreenMessageLarge\">Class balancer</font><br>";
		html += "<table width=700 align=center bgcolor=888888>";
		html += "<tr>";
		html += "<td width=30 align=center></td>";
		html += "<td width=250><img width=1 height=5 src=\"L2UI.SquareBlank\"/><font name=\"ScreenMessageSmall\" color=BBBBBB><font color=2E8424 name=ScreenMessageSmall>Class</font> -> <font color=ED792C name=ScreenMessageSmall>Target Class</font></td>";
		html += "<td width=70 align=center>N</td>";
		html += "<td width=70 align=center>C</td>";
		html += "<td width=70 align=center>M</td>";
		html += "<td width=70 align=center>MC</td>";
		html += "<td width=70 align=center>B</td>";
		html += "<td width=70 align=center>PS</td>";
		html += "<td width=70 align=center>PSC</font></td>";
		html += "</tr>";
		html += "</table>";
		html += "<img src=\"L2UI.SquareGray\" width=700 height=1/>";
		int i = 0;
		int f = 0;
		final int objectsInPage = 3;
		for (final Map.Entry<Integer, double[]> entry : _used.entrySet())
		{
			if ((i < ((page - 1) * objectsInPage)) || (i >= (page * objectsInPage)))
			{
				++i;
			}
			else
			{
				String className = ClassId.getClassId((int) entry.getValue()[7]).name();
				className = className.substring(0, 1).toUpperCase() + className.substring(1);
				String targetClassName = "All";
				if ((int) entry.getValue()[8] > -1)
				{
					targetClassName = ClassId.getClassId((int) entry.getValue()[8]).name();
					targetClassName = targetClassName.substring(0, 1).toUpperCase() + targetClassName.substring(1);
				}
				html += "<table width=700 align=center bgcolor=333333>";
				html += "</tr><tr><td><img width=1 height=3 src=\"L2UI.SquareBlank\"/></td></tr>";
				html += "<tr>";
				html = html + "<td width=30 align=center><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";delete;" + (forOlympiad ? 1 : 0) + ";" + page + ";" + classId + ";" + targetClassId + ";" + (int) entry.getValue()[7] + ";" + (int) entry.getValue()[8] + ";" + entry.getKey() + "\" back=\"L2UI_ct1.Button_DF_Delete_Down\" width=14 height=14 fore=\"L2UI_ct1.Button_DF_Delete\" ></td>";
				html += "<td width=250><font name=\"ScreenMessageSmall\">";
				html = html + "<table><tr><td align=center width=200><font color=2E8424 name=ScreenMessageSmall>" + className + "</font></td></tr><tr><td width=100 align=center><font color=ED792C name=ScreenMessageSmall>" + targetClassName + "</font></td></tr></table>";
				html += "</font></td>";
				html += "<td>";
				html += "<table>";
				String h1 = "<tr>";
				String h2 = "<tr>";
				String h3 = "<tr>";
				for (int h4 = 0; h4 < 7; ++h4)
				{
					final int val = (int) ((entry.getValue()[h4] - 1.0) * 100.0);
					h1 = h1 + "<td width=35 align=center><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";increase;" + (forOlympiad ? 1 : 0) + ";" + page + ";" + classId + ";" + targetClassId + ";" + (int) entry.getValue()[7] + ";" + (int) entry.getValue()[8] + ";" + entry.getKey() + ";" + h4 + ";" + (entry.getValue()[h4] + 0.1) + "\" back=\"L2UI_ct1.Button_DF_Up_Down\" width=14 height=14 fore=\"L2UI_ct1.Button_DF_Up\" ></td>";
					h2 = h2 + "<td width=70 align=center>" + ((val >= 0) ? ("+" + val) : val) + "%</td>";
					h3 = h3 + "<td width=35 align=center><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";increase;" + (forOlympiad ? 1 : 0) + ";" + page + ";" + classId + ";" + targetClassId + ";" + (int) entry.getValue()[7] + ";" + (int) entry.getValue()[8] + ";" + entry.getKey() + ";" + h4 + ";" + (entry.getValue()[h4] - 0.1) + "\" back=\"L2UI_ct1.Button_DF_Down_Down\" width=14 height=14 fore=\"L2UI_ch3.DownButton\" ></td>";
				}
				html = html + h1 + "</tr>" + h2 + "</tr>" + h3 + "</tr>";
				html += "</table>";
				html += "</td>";
				html += "</tr><tr><td><img width=1 height=5 src=\"L2UI.SquareBlank\"/></td></tr>";
				html += "</table>";
				html += "<img src=\"L2UI.SquareGray\" width=700 height=1/>";
				++i;
				++f;
			}
		}
		if ((i == 0) || (f == 0))
		{
			html += "<table width=701 align=center bgcolor=333333>";
			html += "<tr><td align=center width=701><font color=CF1616 name=ScreenMessageSmall>No balances found!</font></td></tr>";
			html += "</table>";
			html += "<img src=\"L2UI.SquareGray\" width=700 height=1/>";
		}
		html += "<table bgcolor=111111><tr>";
		if (page > 1)
		{
			html = html + "<td><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";show;" + (forOlympiad ? 1 : 0) + ";" + (page - 1) + ";" + classId + ";" + targetClassId + "\" width=\"14\" height=\"14\" back=\"L2UI_CT1.Button_DF_Left_Down\" fore=\"L2UI_CT1.Button_DF_Left\" ></td>";
		}
		else
		{
			html += "<td width=14></td>";
		}
		html = html + "<td width=20 align=center><font name=ScreenMessageSmall color=CBCBCB>" + page + "</font></td>";
		if ((page * objectsInPage) < _used.size())
		{
			html = html + "<td><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";show;" + (forOlympiad ? 1 : 0) + ";" + (page + 1) + ";" + classId + ";" + targetClassId + "\" width=\"14\" height=\"14\" back=\"L2UI_CT1.Button_DF_Right_Down\" fore=\"L2UI_CT1.Button_DF_Right\" ></td>";
		}
		else
		{
			html += "<td width=14></td>";
		}
		html += "</tr></table>";
		html = html + "<button value=\"Back\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";main;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + "\" width=\"150\" height=\"28\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" >";
		html += "</center>";
		return html;
	}
	
	public String showAddPage(final int classId, final int targetClassId, final int race, final int tRace, final double[] values, final boolean forOlympiad)
	{
		String vals = "";
		for (int i = 0; i < 7; ++i)
		{
			vals = vals + ";" + values[i];
		}
		String classes = "";
		for (final ClassId cl : ClassId.values())
		{
			if ((cl.level() == 3) && (cl.getRace().ordinal() == race))
			{
				String className = cl.name();
				className = className.substring(0, 1).toUpperCase() + className.substring(1);
				classes = classes + className + ";";
			}
		}
		String tClasses = "";
		for (final ClassId cl2 : ClassId.values())
		{
			if ((cl2.level() == 3) && (cl2.getRace().ordinal() == tRace))
			{
				String className2 = cl2.name();
				className2 = className2.substring(0, 1).toUpperCase() + className2.substring(1);
				tClasses = tClasses + className2 + ";";
			}
		}
		String content = "<br><font name=\"ScreenMessageSmall\" color=BBBBBB>";
		content += "<table width=\"640\">";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=10></td></tr>";
		content += "<tr><td></td><td align=\"center\">Class Id</td><td align=\"center\">Target Class Id</td><td width=\"40\"></td></tr>";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=10></td></tr>";
		content += "<tr><td><table>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";0;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 0) ? "_checked" : "") + "\">";
		content += "</td><td>Human</td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";1;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 1) ? "_checked" : "") + "\">";
		content += "</td><td>Elf</td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";2;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 2) ? "_checked" : "") + "\">";
		content += "</td><td>Dark Elf</td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";3;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 3) ? "_checked" : "") + "\">";
		content += "</td><td>Orc</td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";4;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 4) ? "_checked" : "") + "\">";
		content += "</td><td>Dwarf</td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";5;" + tRace + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 5) ? "_checked" : "") + "\">";
		content += "</td><td>Kamael</td></tr>";
		content += "</table></td>";
		content = content + "<td align=\"center\"><combobox var=\"classId\" list=\"" + classes + "\" width=110></td>";
		content = content + "<td align=\"center\"><combobox var=\"tClassId\" list=\"All;" + tClasses + "\" width=110></td>";
		content += "<td><table>";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";0" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((tRace == 0) ? "_checked" : "") + "\">";
		content += "</td><td>Human</td></tr>";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";1" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((tRace == 1) ? "_checked" : "") + "\">";
		content += "</td><td>Elf</td></tr>";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";2" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((tRace == 2) ? "_checked" : "") + "\">";
		content += "</td><td>Dark Elf</td></tr>";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";3" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((tRace == 3) ? "_checked" : "") + "\">";
		content += "</td><td>Orc</td></tr>";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";4" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((tRace == 4) ? "_checked" : "") + "\">";
		content += "</td><td>Dwarf</td></tr>";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";5" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((tRace == 5) ? "_checked" : "") + "\">";
		content += "</td><td>Kamael</td></tr>";
		content += "</table></td>";
		content += "</tr>";
		content += "</table>";
		content += "<table>";
		content += "<tr><td width=105></td>";
		content += "<td width=70 align=center>N</td>";
		content += "<td width=70 align=center>C</td>";
		content += "<td width=70 align=center>M</td>";
		content += "<td width=70 align=center>MC</td>";
		content += "<td width=70 align=center>B</td>";
		content += "<td width=70 align=center>PS</td>";
		content += "<td width=70 align=center>PSC</td>";
		content += "</tr>";
		String h1 = "<tr><td width=100></td>";
		String h2 = "<tr><td width=100></td>";
		String h3 = "<tr><td width=100></td>";
		for (int h4 = 0; h4 < 7; ++h4)
		{
			String v = "";
			for (int x = 0; x < h4; ++x)
			{
				v = v + ";" + values[x];
			}
			String v2 = "";
			for (int x2 = h4 + 1; x2 < 7; ++x2)
			{
				v2 = v2 + ";" + values[x2];
			}
			final int val = (int) ((values[h4] - 1.0) * 100.0);
			h1 = h1 + "<td width=70 align=center><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";" + tRace + v + ";" + (values[h4] + 0.1) + v2 + "\" back=\"L2UI_ct1.Button_DF_Up_Down\" width=14 height=14 fore=\"L2UI_ct1.Button_DF_Up\" ></td>";
			h2 = h2 + "<td width=70 align=center>" + ((val >= 0) ? ("+" + val) : val) + "%</td>";
			h3 = h3 + "<td width=70 align=center><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + targetClassId + ";" + race + ";" + tRace + v + ";" + (values[h4] - 0.1) + v2 + "\" back=\"L2UI_ct1.Button_DF_Down_Down\" width=14 height=14 fore=\"L2UI_ch3.DownButton\" ></td>";
		}
		content = content + h1 + "</tr>" + h2 + "</tr>" + h3 + "</tr>";
		content += "</table>";
		content += "<center><br>";
		content = content + "<table width=170><tr><td width=20 align=center><img width=1 height=3 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 0 : 1) + ";" + classId + ";" + targetClassId + ";" + race + ";" + tRace + "" + vals + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + (forOlympiad ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + (forOlympiad ? "_checked" : "") + "\"/></td><td width=150 align=left><font name=ScreenMessageSmall color=BABABA>For Olympiad</font></td></tr></table>";
		content += "<br>";
		content = content + "<button value=\"Add\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";add;" + (forOlympiad ? 1 : 0) + "; $classId ; $tClassId " + vals + "\" width=65 height=21 back=\"L2UI_CT1.Button_DF_down\" fore=\"L2UI_CT1.Button_DF\">";
		content += "</center><br>";
		content += "<table width=700><tr><td width=700 align=right>";
		content = content + "<button value=\"Back\" action=\"bypass " + ClassBalanceBBSManager.CLASS_BALANCE_BBS_CMD + ";main;0;" + classId + ";" + targetClassId + "\" width=65 height=21 back=\"L2UI_CT1.Button_DF_down\" fore=\"L2UI_CT1.Button_DF\">";
		content += "</td></tr></table><br>";
		content += "<center>";
		content += "N -> Normal, C -> Critical, M -> Magic, MC -> Magic Critical,</br1>B -> Blow, PS -> Physical Skill, PSC -> Physical Skill Critical.";
		content += "</center></font>";
		return content;
	}
	
	@Override
	public void parsewrite(final String url, final String ar1, final String ar2, final String ar3, final String ar4, final String ar5, final L2PcInstance activeChar)
	{
	}
	
	public static ClassBalanceBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ClassBalanceBBSManager _instance;
		
		static
		{
			_instance = new ClassBalanceBBSManager();
		}
	}
}
