package gabriel.cbbCertif;


import gabriel.Utils.SafeSkill;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */


public class CertiData {
    private final L2PcInstance l2PcInstance;
    private final int _index;

    private final int[] _emergent_skill_id = new int[]
            {
                    631,
                    632,
                    633,
                    634
            };

    public int[] _emergent = new int[]
            {
                    0,
                    0,
                    0,
                    0
            };

    public int[] _skill = new int[]
            {
                    0,
                    0,
                    0
            };

    public int[] _transform = new int[]
            {
                    0,
                    0,
                    0
            };

    public CertiData(L2PcInstance l2PcInstance, int index) {
        this.l2PcInstance = l2PcInstance;
        _index = index;
    }


    public int getIndex() {
        return _index;
    }


    public int getEmergent1() {
        return _emergent[0];
    }


    public int getEmergent2() {
        return _emergent[1];
    }


    public int getEmergent3() {
        return _emergent[2];
    }


    public int getEmergent4() {
        return _emergent[3];
    }


    public int getSkill1() {
        return _skill[0];
    }


    public int getSkill2() {
        return _skill[1];
    }


    public int getSkill3() {
        return _skill[2];
    }


    public int getTransform1() {
        return _transform[0];
    }


    public int getTransform2() {
        return _transform[1];
    }


    public int getTransform3() {
        return _transform[2];
    }


    public int[] getEmergents() {
        return _emergent;
    }


    public int[] getSkills() {
        return _skill;
    }


    public int[] getTransforms() {
        return _transform;
    }

    // EMERGENT

    public boolean canLearnEmergent() {
        return (_emergent[0] + _emergent[1] + _emergent[2] + _emergent[3]) < 6;
    }


    public void updateEmergentLvL(int type, boolean inc) {
        if ((type > 3) || (type < 0)) {
            return;
        }
        if (inc) {
            if (_emergent[type] < 6) {
                _emergent[type]++;
                l2PcInstance.addSkill(SkillData.getInstance().getInfo(_emergent_skill_id[type], _emergent[type]), false);
                l2PcInstance.sendSkillList();
                SafeSkill.checkCertEmergents(_emergent_skill_id[type], _emergent[type], this, l2PcInstance);
            }
        } else {
            if (_emergent[type] > 0) {
                _emergent[type]--;
                if (_emergent[type] == 0) {
                    l2PcInstance.removeSkill(l2PcInstance.getKnownSkill(_emergent_skill_id[type]), false, true);
                } else {
                    l2PcInstance.addSkill(SkillData.getInstance().getInfo(_emergent_skill_id[type], _emergent[type]), false);
                    SafeSkill.checkCertEmergents(_emergent_skill_id[type], _emergent[type], this, l2PcInstance);
                }
                l2PcInstance.sendSkillList();
            }
        }

    }


    public int containsSkillAmount(List<SkillInfo> table) {
        int amount = 0;
        for (int val : _skill) {
            if (val == 0) {
                continue;
            }
            for (SkillInfo skill : table) {
                if (skill.getId() == val) {
                    amount++;
                }
            }
        }
        return amount;
    }


    public int containsTransformAmount(int transform) {
        int amount = 0;
        for (int val : _transform) {
            if (val == 0) {
                continue;
            }
            if (val == transform) {
                amount++;
            }
        }
        return amount;
    }

    // SKILL

    public boolean canLearnSkill() {
        for (int val : _skill) {
            if (val == 0) {
                return true;
            }
        }
        return false;
    }


    public void AddSkill(int id) {
        if (_skill[0] == 0) {
            _skill[0] = id;
        } else if (_skill[1] == 0) {
            _skill[1] = id;
        } else if (_skill[2] == 0) {
            _skill[2] = id;
        }
        l2PcInstance.addSkill(SkillData.getInstance().getInfo(id, 1), false);
        l2PcInstance.sendSkillList();
        SafeSkill.checkCertSkills(id, 1, this, l2PcInstance);
    }


    public boolean canResetSkill() {
        if (_skill[0] != 0) {
            return true;
        }
        if (_skill[1] != 0) {
            return true;
        }
        if (_skill[2] != 0) {
            return true;
        }
        return false;
    }


    public void resetSkills() {
        for (int id : _skill) {
            if (l2PcInstance.getSkillLevel(id) > 0) {
                l2PcInstance.removeSkill(l2PcInstance.getKnownSkill(id), false, true);
            }
        }
        l2PcInstance.sendSkillList();
        _skill[0] = 0;
        _skill[1] = 0;
        _skill[2] = 0;
    }

    public boolean containsSkill(List<SkillInfo> table) {
        for (int val : _skill) {
            if (val == 0) {
                continue;
            }
            for (SkillInfo skill : table) {
                if (skill.getId() == val) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsSkill(int skill) {
        for (int val : _skill) {
            if (val == 0) {
                continue;
            }
            if (val == skill) {
                return true;
            }
        }
        return false;
    }

    public boolean containsTransform(int transform, int skillLv) {
        int expectedLevel = 0;
        boolean found = false;
        for (int val : _transform) {
            if (val == 0) {
                continue;
            }
            if (val == transform) {
                found = true;
                expectedLevel++;
            }
        }
        if (found) {
            return expectedLevel == skillLv;
        }
        return false;

    }

    public boolean containsEmergent(int containsEmergent, int emergentLv) {
        int index = find(_emergent_skill_id, containsEmergent);
        if (index == -1) {
            return false;
        }
        int levelExpected = _emergent[index];
        return levelExpected == emergentLv;
    }

    private int find(int[] a, int target) {
        return IntStream.range(0, a.length).filter(i -> target == a[i]).findFirst().orElse(-1); // return -1 if target is not found
    }

    // TRANSFORM
    public boolean canLearnTransform() {
        for (int val : _transform) {
            if (val == 0) {
                return true;
            }
        }
        return false;
    }


    public void AddTransform(int id) {
        if (_transform[0] == 0) {
            _transform[0] = id;
        } else if (_transform[1] == 0) {
            _transform[1] = id;
        } else if (_transform[2] == 0) {
            _transform[2] = id;
        }
        if (l2PcInstance.getSkillLevel(id) > 0) {
            l2PcInstance.addSkill(SkillData.getInstance().getInfo(id, l2PcInstance.getSkillLevel(id) + 1), false);
        } else {
            l2PcInstance.addSkill(SkillData.getInstance().getInfo(id, 1), false);
        }
        l2PcInstance.sendSkillList();
        SafeSkill.checkCertTransform(id, l2PcInstance.getSkillLevel(id), this, l2PcInstance);
    }


    public boolean canResetTransforms() {
        if (_transform[0] != 0) {
            return true;
        }
        if (_transform[1] != 0) {
            return true;
        }
        if (_transform[2] != 0) {
            return true;
        }
        return false;
    }


    public void resetTransforms() {
        for (int id : _transform) {
            if (l2PcInstance.getSkillLevel(id) > 0) {
                l2PcInstance.removeSkill(l2PcInstance.getKnownSkill(id), false, true);
            }
        }
        l2PcInstance.sendSkillList();
        _transform[0] = 0;
        _transform[1] = 0;
        _transform[2] = 0;
    }


    public void updateEffects() {
        for (int type = 0; type < 4; type++) {
            if (_emergent[type] > 0) {
                l2PcInstance.addSkill(SkillData.getInstance().getInfo(_emergent_skill_id[type], _emergent[type]), false);
            }
        }
        for (int id : _skill) {
            if (id > 0) {
                l2PcInstance.addSkill(SkillData.getInstance().getInfo(id, 1), false);
            }
        }
        for (int id : _transform) {
            if (id > 0) {
                if (l2PcInstance.getSkillLevel(id) > 0) {
                    l2PcInstance.addSkill(SkillData.getInstance().getInfo(id, l2PcInstance.getSkillLevel(id) + 1), false);
                } else {
                    l2PcInstance.addSkill(SkillData.getInstance().getInfo(id, 1), false);
                }
            }
        }
        l2PcInstance.sendSkillList();
    }
}
