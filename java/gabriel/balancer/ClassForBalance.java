package gabriel.balancer;

public class ClassForBalance {
    private int _id;
    private int _firstClass;
    private int _secondClass;
    private float _autoAttack;
    private float _magicAttack;
    private float _skillAttack;
    private float _critDamage;
    private float _magicCrit;
    private float _backstabDamage;
    private float _blowDamage;

    public ClassForBalance(int id, int firstClass, int secondClass, float autDmg, float magicDmg, float skillDmg, float critMulti, float magicCrit, float backstabDamage, float blowDamage) {
        this._id = id;
        this._firstClass = firstClass;
        this._secondClass = secondClass;
        this._autoAttack = autDmg;
        this._magicAttack = magicDmg;
        this._skillAttack = skillDmg;
        this._critDamage = critMulti;
        this._magicCrit = magicCrit;
        this._backstabDamage = backstabDamage;
        this._blowDamage = blowDamage;
    }

    public int getId() {
        return this._id;
    }

    public int getFirstClass() {
        return this._firstClass;
    }

    public int getSecondClass() {
        return this._secondClass;
    }

    public float getAutoAttackDamage() {
        return this._autoAttack;
    }

    public float getMagicAttackDamage() {
        return this._magicAttack;
    }

    public float getSkillAttackDamage() {
        return this._skillAttack;
    }

    public float getPhysCriticalDamage() {
        return this._critDamage;
    }

    public float getMagicCriticalDamage() {
        return this._magicCrit;
    }

    public float getBackstabDamage() {
        return this._backstabDamage;
    }

    public float getBlowDamage() {
        return this._blowDamage;
    }

    @Override
    public String toString() {
        return "[id: " + _id + "]; [firstClass: " + _firstClass + "]; [secondClass: " + _secondClass + "]; [physDamage: " + _autoAttack + "]; [magicDamage: " + _magicAttack + "]; [physSkillDamage: " + _skillAttack + "]; [physCrit: " + _critDamage + "]; [magicCrit: " + _magicCrit + "]";
    }
}