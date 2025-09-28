package l2r.gameserver.features.balanceEngine.skillBalancer;

import l2r.gameserver.communitybbs.Managers.BaseBBSManager;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.serverpackets.ConfirmDlg;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SkillsBalanceBBSManager extends BaseBBSManager
{
	public static String SKILLS_BALANCE_BBS_CMD = "_bbsskillsbalancer";
	
	@Override
	public void cbByPass(String command, final L2PcInstance activeChar)
	{
		if (!activeChar.isGM())
		{
			return;
		}
		if (command.equals("admin_skillsbalancer"))
		{
			command = SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";main";
		}
		String html = "<html><body><br><br>";
		command = command.substring((command.length() > SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD.length()) ? (SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD.length() + 1) : SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD.length());
		final StringTokenizer st = new StringTokenizer(command, ";");
		String cmd = "main";
		if (st.hasMoreTokens())
		{
			cmd = st.nextToken();
		}
		boolean forOlympiad = false;
		if (st.hasMoreTokens())
		{
			forOlympiad = st.nextToken().equals("1");
		}
		int classId = -1;
		if (st.hasMoreTokens() && !cmd.equalsIgnoreCase("add"))
		{
			classId = Integer.parseInt(st.nextToken());
		}
		if (!cmd.startsWith("add"))
		{
			html += showHeading(forOlympiad, classId);
		}
		if (cmd.equalsIgnoreCase("main"))
		{
			html += showMain(forOlympiad, classId);
		}
		else if (cmd.equalsIgnoreCase("search"))
		{
			html += showSearchResults(st, activeChar, forOlympiad, classId);
		}
		else if (cmd.equalsIgnoreCase("delete"))
		{
			final int key = Integer.parseInt(st.nextToken());
			final int skillId = Integer.parseInt(st.nextToken());
			SkillsBalanceManager.getInstance().removeBalance(key, skillId, classId, forOlympiad);
			html += showSearchResults(st, activeChar, forOlympiad, classId);
		}
		else if (cmd.equalsIgnoreCase("increase"))
		{
			final int key = Integer.parseInt(st.nextToken());
			final int skillId = Integer.parseInt(st.nextToken());
			final int type = Integer.parseInt(st.nextToken());
			final double value = Double.parseDouble(st.nextToken());
			SkillsBalanceManager.getInstance().updateBalance(key, skillId, classId, type, value, forOlympiad);
			html += showSearchResults(st, activeChar, forOlympiad, classId);
		}
		else if (cmd.equalsIgnoreCase("addpage"))
		{
			final double[] values =
			{
				1.0,
				1.0,
				-1.0,
				-1.0
			};
			final int race = Integer.parseInt(st.nextToken());
			for (int i = 0; (i < 7) && st.hasMoreTokens(); ++i)
			{
				values[i] = Double.parseDouble(st.nextToken());
			}
			html += showAddPage(classId, race, values, forOlympiad);
		}
		else if (cmd.equalsIgnoreCase("add"))
		{
			String className = st.nextToken();
			if (className.startsWith(" "))
			{
				className = className.substring(1);
			}
			if (className.endsWith(" "))
			{
				className = className.substring(0, className.length() - 1);
			}
			className = className.replaceAll(" ", "");
			if (!className.equals(""))
			{
				for (final ClassId cId : ClassId.values())
				{
					if (cId.name().equalsIgnoreCase(className))
					{
						classId = cId.getId();
					}
				}
			}
			final String skill = st.nextToken().replaceAll(" ", "");
			boolean isNumber = true;
			int skillId2 = -1;
			try
			{
				skillId2 = Integer.parseInt(skill);
			}
			catch (NumberFormatException e)
			{
				isNumber = false;
			}
			final double[] values2 =
			{
				1.0,
				1.0,
				-1.0,
				-1.0
			};
			final int race2 = Integer.parseInt(st.nextToken());
			for (int j = 0; (j < 7) && st.hasMoreTokens(); ++j)
			{
				values2[j] = Double.parseDouble(st.nextToken());
			}
			if (!isNumber)
			{
				activeChar.sendPacket(new ConfirmDlg("Implemented skill id is not number [" + skill + "]!"));
				html += showAddPage(classId, race2, values2, forOlympiad);
			}
			else
			{
				values2[2] = skillId2;
				values2[3] = classId;
				final int key2 = (skillId2 * ((classId < 0) ? -1 : 1)) + (classId * 65536);
				SkillsBalanceManager.getInstance().updateBalance(key2, skillId2, classId, values2, forOlympiad);
				final StringTokenizer st2 = new StringTokenizer("" + skillId2);
				html += showHeading(forOlympiad, classId);
				html += showSearchResults(st2, activeChar, forOlympiad, classId);
			}
		}
		html += "</body></html>";
		separateAndSend(html, activeChar);
	}
	
	public String showSearchResults(final StringTokenizer st, final L2PcInstance activeChar, final boolean forOlympiad, final int classId)
	{
		String html = "";
		String skill = "";
		if (st.hasMoreTokens())
		{
			skill = st.nextToken();
		}
		skill = skill.replace(" ", "");
		int page = 1;
		if (st.hasMoreTokens())
		{
			page = Integer.parseInt(st.nextToken());
		}
		boolean isId = true;
		int skillId = -1;
		try
		{
			skillId = Integer.parseInt(skill);
		}
		catch (NumberFormatException e)
		{
			isId = false;
		}
		if (!isId && (skill.length() < 4))
		{
			activeChar.sendPacket(new ConfirmDlg("You can not imput less than 4 characters for name search!"));
			html += showMain(forOlympiad, classId);
		}
		else if (!isId && (skill.length() > 3))
		{
			final ArrayList<Integer> skills = SkillsBalanceManager.getInstance().getSkillsByName(forOlympiad, skill, classId);
			if (skills.size() < 1)
			{
				String cl = "";
				if (classId >= 0)
				{
					String name = ClassId.getClassId(classId).name();
					name = name.substring(0, 1).toUpperCase() + name.substring(1);
					cl = " to target " + name;
				}
				activeChar.sendPacket(new ConfirmDlg("No used skills were found using " + skill + cl + "!"));
				html += showMain(forOlympiad, classId);
			}
			else
			{
				html += showSkills(forOlympiad, classId, skill, skills, page);
			}
		}
		else
		{
			final ArrayList<Integer> skills = SkillsBalanceManager.getInstance().getUsedSkillsById(forOlympiad, skillId, classId);
			if ((skills == null) || (skills.size() < 1))
			{
				String cl = "";
				if (classId >= 0)
				{
					String name = ClassId.getClassId(classId).name();
					name = name.substring(0, 1).toUpperCase() + name.substring(1);
					cl = " to target " + name;
				}
				activeChar.sendPacket(new ConfirmDlg("No used skills were found using ID[" + skillId + "]" + cl + "!"));
				html += showMain(forOlympiad, classId);
			}
			else
			{
				html += showSkills(forOlympiad, classId, String.valueOf(skillId), skills, page);
			}
		}
		return html;
	}
	
	public String showHeading(final boolean forOlympiad, final int classId)
	{
		String html = "<center>";
		html += "<font name=\"ScreenMessageLarge\">Skills balancer</font><br>";
		html += "<table width=500><tr>";
		html = html + "<td width=20 align=center><img width=1 height=8 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";main;" + (forOlympiad ? 0 : 1) + ";" + classId + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + (forOlympiad ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + (forOlympiad ? "_checked" : "") + "\"/></td>";
		html += "<td width=200 align=left><font name=ScreenMessageSmall color=BABABA>Show for Olympiad</font></td>";
		html += "<td width=140 align=left><img width=1 height=5 src=\"L2UI.SquareBlank\"/><edit var=\"skill\" width=140 height=15></td>";
		html = html + "<td width=140 align=left><button value=\"Search\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";search;" + (forOlympiad ? 1 : 0) + ";" + classId + "; $skill \" width=140 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
		html += "</tr></table><br>";
		return html;
	}
	
	public String showMain(final boolean forOlympiad, final int classId)
	{
		String html = "";
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
				html = html + "<tr><td width=20 align=center><img width=1 height=3 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";main;" + (forOlympiad ? 1 : 0) + ";" + ((cl.getId() == classId) ? -1 : cl.getId()) + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + ((cl.getId() == classId) ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + ((cl.getId() == classId) ? "_checked" : "") + "\"/></td><td width=200><font name=ScreenMessageSmall color=BABABA>" + name + "</font></td></tr>";
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
		html += "<table bgcolor=111111><tr>";
		html += "<td width=400></td>";
		html += "<td width=200></td>";
		html = html + "<td><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";addpage;0;" + classId + ";0\" width=\"28\" height=\"28\" back=\"L2UI_CT1.MiniMap_DF_PlusBtn_Red_Down\" fore=\"L2UI_CT1.MiniMap_DF_PlusBtn_Red\" ></td>";
		html += "</tr></table>";
		return html;
	}
	
	public String showSkills(final boolean forOlympiad, final int classId, final String search, final ArrayList<Integer> skills, final int page)
	{
		String html = "<center>";
		html += "<table width=600 align=center bgcolor=888888>";
		html += "<tr>";
		html += "<td width=30 align=center></td>";
		html += "<td width=400><img width=1 height=5 src=\"L2UI.SquareBlank\"/><font name=\"ScreenMessageSmall\" color=BBBBBB><font color=2E8424 name=ScreenMessageSmall>Skill</font> -> <font color=ED792C name=ScreenMessageSmall>Target Class</font></td>";
		html += "<td width=85 align=center>Chance</td>";
		html += "<td width=85 align=center>Power</td>";
		html += "</tr>";
		html += "</table>";
		int i = 0;
		int f = 0;
		final int objectsInPage = 3;
		if (skills != null)
		{
			for (final int key : skills)
			{
				if ((i < ((page - 1) * objectsInPage)) || (i >= (page * objectsInPage)))
				{
					++i;
				}
				else
				{
					final double[] values = SkillsBalanceManager.getInstance().getBalance(key, forOlympiad);
					String targetClassName = "All";
					if ((int) values[3] > -1)
					{
						targetClassName = ClassId.getClassId((int) values[3]).name();
						targetClassName = targetClassName.substring(0, 1).toUpperCase() + targetClassName.substring(1);
					}
					final L2Skill sk = SkillData.getInstance().getInfo((int) values[2], 1);
					html += "<table width=600 align=center bgcolor=333333>";
					html += "</tr><tr><td><img width=1 height=3 src=\"L2UI.SquareBlank\"/></td></tr>";
					html += "<tr>";
					html = html + "<td width=30 align=center><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";delete;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + key + ";" + sk.getId() + "; " + search + ";" + page + "\" back=\"L2UI_ct1.Button_DF_Delete_Down\" width=14 height=14 fore=\"L2UI_ct1.Button_DF_Delete\" ></td>";
					html += "<td width=400><font name=\"ScreenMessageSmall\">";
					html = html + "<font color=2E8424 name=ScreenMessageSmall>" + sk.getName() + "</font> -> <font color=ED792C name=ScreenMessageSmall>" + targetClassName + "</font>";
					html += "</font></td>";
					html += "<td>";
					html += "<table>";
					String h1 = "<tr>";
					String h2 = "<tr>";
					String h3 = "<tr>";
					for (int h4 = 0; h4 < 2; ++h4)
					{
						final int val = (int) ((values[h4] - 1.0) * 100.0);
						h1 = h1 + "<td width=35 align=center><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";increase;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + key + ";" + sk.getId() + ";" + h4 + ";" + (values[h4] + 0.1) + "; " + search + "\" back=\"L2UI_ct1.Button_DF_Up_Down\" width=14 height=14 fore=\"L2UI_ct1.Button_DF_Up\" ></td>";
						h2 = h2 + "<td width=85 align=center>" + ((val >= 0) ? ("+" + val) : val) + "%</td>";
						h3 = h3 + "<td width=35 align=center><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";increase;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + key + ";" + sk.getId() + ";" + h4 + ";" + (values[h4] - 0.1) + "; " + search + "\" back=\"L2UI_ct1.Button_DF_Down_Down\" width=14 height=14 fore=\"L2UI_ch3.DownButton\" ></td>";
					}
					html = html + h1 + "</tr>" + h2 + "</tr>" + h3 + "</tr>";
					html += "</table>";
					html += "</td>";
					html += "</tr><tr><td><img width=1 height=5 src=\"L2UI.SquareBlank\"/></td></tr>";
					html += "</table>";
					html += "<img src=\"L2UI.SquareGray\" width=600 height=1/>";
					++i;
					++f;
				}
			}
		}
		if ((i == 0) || (f == 0))
		{
			html += "<table width=501 align=center bgcolor=333333>";
			html += "<tr><td align=center width=701><font color=CF1616 name=ScreenMessageSmall>No balances found!</font></td></tr>";
			html += "</table>";
			html += "<img src=\"L2UI.SquareGray\" width=501 height=1/>";
		}
		html += "<table bgcolor=111111><tr>";
		if (page > 1)
		{
			html = html + "<td><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";search;" + (forOlympiad ? 1 : 0) + ";" + classId + "; " + search + ";" + (page - 1) + "\" width=\"14\" height=\"14\" back=\"L2UI_CT1.Button_DF_Left_Down\" fore=\"L2UI_CT1.Button_DF_Left\" ></td>";
		}
		else
		{
			html += "<td width=14></td>";
		}
		html = html + "<td width=20 align=center><font name=ScreenMessageSmall color=CBCBCB>" + page + "</font></td>";
		if ((skills != null) && ((page * objectsInPage) < skills.size()))
		{
			html = html + "<td><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";search;" + (forOlympiad ? 1 : 0) + ";" + classId + "; " + search + ";" + (page + 1) + "\" width=\"14\" height=\"14\" back=\"L2UI_CT1.Button_DF_Right_Down\" fore=\"L2UI_CT1.Button_DF_Right\" ></td>";
		}
		else
		{
			html += "<td width=14></td>";
		}
		html += "</tr></table>";
		html = html + "<button value=\"Back\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";main\" width=\"150\" height=\"28\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" >";
		html += "</center>";
		html = html + "<font name=\"ScreenMessageSmall\">Indicated search keywords [" + search + "]</font><br>";
		return html;
	}
	
	public String showAddPage(final int classId, final int race, final double[] values, final boolean forOlympiad)
	{
		final String vals = ";" + values[0] + ";" + values[1];
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
		String content = "<br><font name=\"ScreenMessageSmall\" color=BBBBBB>";
		content += "<table width=\"640\">";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=10></td></tr>";
		content += "<tr>";
		content += "<td><table>";
		content += "<tr><td width=100></td><td></td><td width=200>Target Class Id</td></tr>";
		content += "<tr><td width=100></td><td></td><td><img src=\"L2UI.SquareBlank\" width=40 height=10></td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";0;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 0) ? "_checked" : "") + "\">";
		content += "</td><td>Human</td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";1;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 1) ? "_checked" : "") + "\">";
		content += "</td><td>Elf</td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";2;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 2) ? "_checked" : "") + "\">";
		content += "</td><td>Dark Elf</td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";3;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 3) ? "_checked" : "") + "\">";
		content += "</td><td>Orc</td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";4;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 4) ? "_checked" : "") + "\">";
		content += "</td><td>Dwarf</td></tr>";
		content += "<tr><td width=100></td><td><img src=\"L2UI.SquareBlank\" width=1 height=3/>";
		content = content + "<button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";5;" + vals + "\" width=12 height=12 back=\"L2UI.CheckBox_checked\" fore=\"L2UI.CheckBox" + ((race == 5) ? "_checked" : "") + "\">";
		content += "</td><td>Kamael</td></tr>";
		content = content + "<tr><td width=100></td><td></td><td><combobox var=\"classId\" list=\"All;" + classes + "\" width=110></td></tr>";
		content += "</table></td>";
		content += "<td><table>";
		content += "<tr><td><img src=\"L2UI.SquareBlank\" width=40 height=100></td></tr>";
		content += "<tr><td width=100></td><td><edit var=\"skillId\" width=140 height=15></td><td width=200>Skill Id</td></tr>";
		content += "</table></td>";
		content += "</tr>";
		content += "</table><br><br>";
		content += "<table width=540 align=center>";
		content += "<tr>";
		content += "<td width=370 align=center></td>";
		content += "<td width=340 align=center>Chance</td>";
		content += "<td width=340 align=center>Power</td>";
		content += "</tr>";
		String h1 = "<tr><td width=370></td>";
		String h2 = "<tr><td width=370></td>";
		String h3 = "<tr><td width=370></td>";
		for (int h4 = 0; h4 < 2; ++h4)
		{
			final int val = (int) ((values[h4] - 1.0) * 100.0);
			h1 = h1 + "<td width=170 align=center><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + race + ";" + (values[0] + ((h4 == 0) ? 0.1 : 0.0)) + ";" + (values[1] + ((h4 == 1) ? 0.1 : 0.0)) + "\" back=\"L2UI_ct1.Button_DF_Up_Down\" width=14 height=14 fore=\"L2UI_ct1.Button_DF_Up\" ></td>";
			h2 = h2 + "<td width=170 align=center>" + ((val >= 0) ? ("+" + val) : val) + "%</td>";
			h3 = h3 + "<td width=170 align=center><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 1 : 0) + ";" + classId + ";" + race + ";" + (values[0] - ((h4 == 0) ? 0.1 : 0.0)) + ";" + (values[1] - ((h4 == 1) ? 0.1 : 0.0)) + "\" back=\"L2UI_ct1.Button_DF_Down_Down\" width=14 height=14 fore=\"L2UI_ch3.DownButton\" ></td>";
		}
		content = content + h1 + "</tr>" + h2 + "</tr>" + h3 + "</tr>";
		content += "</table>";
		content += "<center><br>";
		content = content + "<table width=170><tr><td width=20 align=center><img width=1 height=3 src=\"L2UI.SquareBlank\"/><button value=\"\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";addpage;" + (forOlympiad ? 0 : 1) + ";" + classId + ";" + race + vals + "\" width=\"14\" height=\"14\" back=\"L2UI.CheckBox" + (forOlympiad ? "" : "_checked") + "\" fore=\"L2UI.CheckBox" + (forOlympiad ? "_checked" : "") + "\"/></td><td width=150 align=left><font name=ScreenMessageSmall color=BABABA>For Olympiad</font></td></tr></table>";
		content += "<br>";
		content = content + "<button value=\"Add\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";add;" + (forOlympiad ? 1 : 0) + "; $classId ; $skillId ;" + race + vals + "\" width=65 height=21 back=\"L2UI_CT1.Button_DF_down\" fore=\"L2UI_CT1.Button_DF\">";
		content += "</center><br>";
		content += "<table width=700><tr><td width=700 align=right>";
		content = content + "<button value=\"Back\" action=\"bypass " + SkillsBalanceBBSManager.SKILLS_BALANCE_BBS_CMD + ";main;0\" width=65 height=21 back=\"L2UI_CT1.Button_DF_down\" fore=\"L2UI_CT1.Button_DF\">";
		content += "</td></tr></table><br>";
		content += "</font>";
		return content;
	}
	
	@Override
	public void parsewrite(final String url, final String ar1, final String ar2, final String ar3, final String ar4, final String ar5, final L2PcInstance activeChar)
	{
	}
	
	public static SkillsBalanceBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final SkillsBalanceBBSManager _instance;
		
		static
		{
			_instance = new SkillsBalanceBBSManager();
		}
	}
}
