static class BuffData{
  static int BUFF_COUNT = 25;
  
  static String [] BUFF_NAME = {
    "Acid Infusion", // 0
    "Cold Snap",
    "Ionic Shell",
    "Crippled",
    "DNA Mutated", 
    "Imflammation", // 5
    "Breached",
    "Volatile Compound",
    "Courage",
    "Morale", 
    "Cancer", // 10
    "BloodLust",
    "Curse",
    "Freezed",
    "Shockwave", 
    "Synchronize", //15
    "Fatal Bond",
    "Repel",
    "Fortified",
    "Haste", 
    "Health Regen", // 20
    "Tough Skin",
    "Weave",
  };
  
  static float [] BUFF_DURATION = {
    TurretSkillData.CANNON_SKILL_A_T3_DURATION, // 0
    TurretSkillData.CANNON_SKILL_C_T1_DURATION,
    TurretSkillData.CANNON_SKILL_C_T2_DURATION,
    TurretSkillData.CANNON_SKILL_C_T5_DURATION,
    TurretSkillData.LASER_SKILL_B_T3_DURATION, 
    TurretSkillData.LASER_SKILL_C_T2_DURATION, // 5
    TurretSkillData.LASER_SKILL_C_T5_DURATION,
    TurretSkillData.LASER_SKILL_C_T4_DURATION,
    3,
    3, 
    TurretSkillData.AURA_SKILL_A_T5_DURATION, // 10
    3,
    TurretSkillData.AURA_SKILL_B_T5_DURATION,
    TurretSkillData.AURA_SKILL_C_T1_DURATION,
    TurretSkillData.AURA_SKILL_C_T3_SLOW_DURATION, 
    TurretSkillData.AURA_SKILL_C_T4_DURATION, //15
    TurretSkillData.AURA_SKILL_C_T5_DURATION,
    TurretSkillData.AURA_SKILL_B_T4_DURATION,
    3,
    3, 
    3, // 20
    3,
    3,
  };
  
  static int [] BUFF_TYPE = {
    DEBUFF, // 0
    DEBUFF,
    DEBUFF,
    DEBUFF,
    DEBUFF, 
    DEBUFF, //5
    DEBUFF,
    DEBUFF,
    BUFF,
    BUFF, 
    DEBUFF, //10
    DEBUFF,
    DEBUFF,
    DEBUFF,
    DEBUFF,
    DEBUFF, //15
    DEBUFF,
    DEBUFF,
    BUFF,
    BUFF,
    BUFF, //20
    BUFF,
    BUFF,
  };
}