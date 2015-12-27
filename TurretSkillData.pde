static class TurretSkillData{
  //Tier Limitation
  
  static float [] MIN_LEVEL = { 2, 4, 6, 8, 10 };
  
  //Cannon
  
  static float CANNON_SKILL_T1_COST = 60;
  static float CANNON_SKILL_T2_COST = 90;
  static float CANNON_SKILL_T3_COST = 150;
  static float CANNON_SKILL_T4_COST = 240;
  static float CANNON_SKILL_T5_COST = 450;
  
    //LevelA
    
      //T1
  static String CANNON_SKILL_A_T1_NAME = "Steady Aim";
  static String CANNON_SKILL_A_T1_DESCRIPTION = "Greatly increases the damage.";
  static float CANNON_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.5;
  
      //T2
  static String CANNON_SKILL_A_T2_NAME = "Saboteur";
  static String CANNON_SKILL_A_T2_DESCRIPTION = "Deals additional damage based on target’s current health.";
  static float CANNON_SKILL_A_T2_HP_PERCENTAGE = 0.07;
  
      //T3
  static String CANNON_SKILL_A_T3_NAME = "Acid Infusion";
  static String CANNON_SKILL_A_T3_DESCRIPTION = "Applies a debuff that cut the victim's armor in half.";
  static float CANNON_SKILL_A_T3_ARMOR_REDUCTION_PERCENTAGE = 0.5;
  static float CANNON_SKILL_A_T3_DURATION = 300;
  
      //T4
  static String CANNON_SKILL_A_T4_NAME = "Headhunter";
  static String CANNON_SKILL_A_T4_DESCRIPTION = "Greatly increases the critical damage.";
  static float CANNON_SKILL_A_T4_BONUS_CRITICAL_DAMAGE_MULTIPLIER = 2;
  
      //T5
  static String CANNON_SKILL_A_T5_NAME = "le Reaper";
  static String CANNON_SKILL_A_T5_DESCRIPTION = "Deals bonus damage based on the missing health of the target.";
  static float CANNON_SKILL_A_T5_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 4;
  
    //LevelB
    
      //T1
  static String CANNON_SKILL_B_T1_NAME = "Rapid Fire";
  static String CANNON_SKILL_B_T1_DESCRIPTION = "Greatly increases the fire rate.";
  static float CANNON_SKILL_B_T1_EXTRA_FIRE_RATE_MULTIPLIER = 0.4;
  
      //T2
  static String CANNON_SKILL_B_T2_NAME = "Ballistics";
  static String CANNON_SKILL_B_T2_DESCRIPTION = "Greatly increases the projectile speed.";
  static float CANNON_SKILL_B_T2_EXTRA_PROJECTILE_SPEED_MULTIPLIER = 1;
  
      //T3
  static String CANNON_SKILL_B_T3_NAME = "Fervor";
  static String CANNON_SKILL_B_T3_DESCRIPTION = "Each continuous attack on the same target stacks fire rate; loses all on changing target.";
  static float CANNON_SKILL_B_T3_BONUS_FIRE_RATE_MULTIPLIER_PER_STACK = 0.15;
  static int CANNON_SKILL_B_T3_MAX_STACK = 10;
  
      //T4
  static String CANNON_SKILL_B_T4_NAME = "Bore";
  static String CANNON_SKILL_B_T4_DESCRIPTION = "Greatly increases the chance of critical hits.";
  static float CANNON_SKILL_B_T4_BONUS_CRIT_CHANCE = 0.1;
  
      //T5
  static String CANNON_SKILL_B_T5_NAME = "Death Wish";
  static String CANNON_SKILL_B_T5_DESCRIPTION = "Increases the chance of critical hits based on the missing health of the castle.";
  static float CANNON_SKILL_B_T5_MIN_BONUS_CRIT_CHANCE = 0.1;
  static float CANNON_SKILL_B_T5_MAX_BONUS_CRIT_CHANCE = 0.3;
  
    //LevelC
    
      //T1
  static String CANNON_SKILL_C_T1_NAME = "Cold Snap";
  static String CANNON_SKILL_C_T1_DESCRIPTION = "Applies a debuff that slows enemies.";
  static float CANNON_SKILL_C_T1_SLOW_PERCENTAGE = 0.3;
  static int CANNON_SKILL_C_T1_DURATION = 90;
  
      //T2
  static String CANNON_SKILL_C_T2_NAME = "Ionic Shell";
  static String CANNON_SKILL_C_T2_DESCRIPTION = "Applies a damaging aura on the target, hurting the enemies around the carrier but not itself.";
  static float CANNON_SKILL_C_T2_BASE_CANNON_DAMAGE_PERCENTAGE = 0.05;
  static float CANNON_SKILL_C_T2_RADIUS = 200;
  static float CANNON_SKILL_C_T2_DAMAGE_INTERVAL = 12;
  static float CANNON_SKILL_C_T2_DURATION = 120;
  
      //T3
  static String CANNON_SKILL_C_T3_NAME = "Boombastics";
  static String CANNON_SKILL_C_T3_DESCRIPTION = "Cannons explode on impact, damaging and applying debuffs to all victims.";
  static float CANNON_SKILL_C_T3_EXPLOSION_RADIUS = 75;
  static float CANNON_SKILL_C_T3_EXPLOSION_DAMAGE_MULTIPLIER = 1;
  
      //T4
  static String CANNON_SKILL_C_T4_NAME = "Eagle Sight";
  static String CANNON_SKILL_C_T4_DESCRIPTION = "Greatly increases the attack range of the turret.";
  static float CANNON_SKILL_C_T4_ATTACK_RANGE_MULTIPLIER = 0.5;
  
      //T5
  static String CANNON_SKILL_C_T5_NAME = "M.A.I.M.";
  static String CANNON_SKILL_C_T5_DESCRIPTION = "On critical hit, applies a debuff that completely cripples the victim.";
  static float CANNON_SKILL_C_T5_SLOW_PERCENTAGE = 1;
  static float CANNON_SKILL_C_T5_DURATION = 30;
  
  //Laser
  
  
  //Aura
  
  
}