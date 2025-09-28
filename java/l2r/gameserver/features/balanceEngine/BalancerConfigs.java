package l2r.gameserver.features.balanceEngine;

import gr.sr.utils.L2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class BalancerConfigs
{
	private static final Logger _log = LoggerFactory.getLogger(BalancerConfigs.class);
	private static final String BALANCER_CONFIG_FILE = "./config/extra/Balancer.ini";
	
	// Balancers
	public static long CLASS_BALANCER_UPDATE_DELAY;
	public static boolean CLASS_BALANCER_AFFECTS_SECOND_PROFFESION;
	public static boolean CLASS_BALANCER_AFFECTS_MONSTERS;
	
	public static long SKILLS_BALANCER_UPDATE_DELAY;
	public static boolean SKILLS_BALANCER_AFFECTS_SECOND_PROFFESION;
	public static boolean SKILLS_BALANCER_AFFECTS_MONSTERS;
	
	public static void loadConfigs()
	{
		// Load Balancer L2Properties file (if exists)
		L2Properties L2BalancerProperties = new L2Properties();
		final File l2jbalancer = new File(BALANCER_CONFIG_FILE);
		try (InputStream is = new FileInputStream(l2jbalancer))
		{
			L2BalancerProperties.load(is);
		}
		catch (Exception e)
		{
			_log.error("Error while loading Balancer settings!", e);
		}
		
		CLASS_BALANCER_UPDATE_DELAY = Integer.parseInt(L2BalancerProperties.getProperty("ClassBalancerUpdateDelay", "300")) * 1000;
		CLASS_BALANCER_AFFECTS_SECOND_PROFFESION = Boolean.parseBoolean(L2BalancerProperties.getProperty("ClassBalancerAffectSecondProffesion", "false"));
		CLASS_BALANCER_AFFECTS_MONSTERS = Boolean.parseBoolean(L2BalancerProperties.getProperty("ClassBalancerAffectMonsters", "false"));
		
		SKILLS_BALANCER_UPDATE_DELAY = Integer.parseInt(L2BalancerProperties.getProperty("SkillsBalancerUpdateDelay", "300")) * 1000;
		SKILLS_BALANCER_AFFECTS_SECOND_PROFFESION = Boolean.parseBoolean(L2BalancerProperties.getProperty("SkillsBalancerAffectSecondProffesion", "false"));
		SKILLS_BALANCER_AFFECTS_MONSTERS = Boolean.parseBoolean(L2BalancerProperties.getProperty("SkillsBalancerAffectMonsters", "false"));
	}
}