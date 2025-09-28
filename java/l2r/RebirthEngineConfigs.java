package l2r;

import gr.sr.configsEngine.AbstractConfigs;
import gr.sr.utils.L2Properties;
import l2r.gameserver.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author vGodFather
 */
public class RebirthEngineConfigs extends AbstractConfigs
{
	private static final String CONFIG_FILE = "./config/extra/rebirth.ini";
	
	public static int REBIRTH_MIN_LEVEL;
	public static int REBIRTH_MAX;
	public static int REBIRTH_RETURN_TO_LEVEL;
	public static List<Integer> REBIRTH_SKILL_IDS;
	public static int REBIRTH_SKILL_SELLER_ID;
	public static String[] REBIRTH_ITEMS;
    public static HashMap<Integer, Integer> REBIRTH_REQUIREMENTS;
    public static HashMap<Integer, Integer> REBIRTH_REQUIREMENTS_LEVEL;
    public static HashMap<Integer, Integer> REBIRTH_LEVEL_MAX;

	@Override
	public void loadConfigs()
	{
		// Load Server L2Properties file (if exists)
		L2Properties rebirth = new L2Properties();
		final File file = new File(CONFIG_FILE);
		try (InputStream is = new FileInputStream(file))
		{
			rebirth.load(is);
		}
		catch (Exception e)
		{
			_log.error("Error while loading rebirth system settings!", e);
		}
		
		REBIRTH_SKILL_SELLER_ID = Integer.parseInt(rebirth.getProperty("RebirthSkillSellerId", "500"));
		REBIRTH_MIN_LEVEL = Integer.parseInt(rebirth.getProperty("RebirthMinLevel", "78"));
		REBIRTH_MAX = Integer.parseInt(rebirth.getProperty("RebirthMaxAllowed", "3"));
		REBIRTH_RETURN_TO_LEVEL = Integer.parseInt(rebirth.getProperty("RebirthReturnToLevel", "1"));
		REBIRTH_ITEMS = rebirth.getProperty("RebirthItems", "").split(";");
		
		String[] ids = rebirth.getProperty("RebirthSkillIds", "5200,35201,35202,35203,35203,35204,35205,35206,35207,35208,35209,35210,35211").split(",");
		REBIRTH_SKILL_IDS = new ArrayList<>(ids.length);
		for (String id : ids)
		{
			if (Util.isDigit(id))
			{
				REBIRTH_SKILL_IDS.add(Integer.parseInt(id));
			}
			else
			{
				_log.warn("Wrong skill id format (RebirthSkillIds) rebirth.ini");
			}
		}
        REBIRTH_REQUIREMENTS = new HashMap<>();
        String[] rebirth_requirements =  rebirth.getProperty("RebirthNecessaryForLevel", "86,5;87,10;").split(";");
        for (String rebirth_requirement : rebirth_requirements) {
            String[] reqs = rebirth_requirement.split(",");
            int level = Integer.parseInt(reqs[0]);
            int amountOfRebirth = Integer.parseInt(reqs[1]);
            REBIRTH_REQUIREMENTS.put(level, amountOfRebirth);
        }

        REBIRTH_REQUIREMENTS_LEVEL = new HashMap<>();
        String[] rebirth_requirements_levels =  rebirth.getProperty("LevelNecessaryForRebirth", "86,85;87,86;").split(";");
        for (String rebirth_requirement_level : rebirth_requirements_levels) {
            String[] reqs = rebirth_requirement_level.split(",");
            int level = Integer.parseInt(reqs[0]);
            int levelToGetLevelRebirth = Integer.parseInt(reqs[1]);
            REBIRTH_REQUIREMENTS_LEVEL.put(level, levelToGetLevelRebirth);
        }

        REBIRTH_LEVEL_MAX = new HashMap<>();
        String[] rebirth_level_max =  rebirth.getProperty("MaxRebirthPerLevel", "85,10;90,20;95,30;100,50;").split(";");
        for (String rebth_level_maxRebs : rebirth_level_max) {
            String[] reqs = rebth_level_maxRebs.split(",");
            int level = Integer.parseInt(reqs[0]);
            int maxRebirthLevel = Integer.parseInt(reqs[1]);
            REBIRTH_LEVEL_MAX.put(level, maxRebirthLevel);
        }
	}
	
	public static RebirthEngineConfigs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final RebirthEngineConfigs _instance = new RebirthEngineConfigs();
	}
}