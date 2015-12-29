static class TurretSkillData{
  //Tier Limitation
  
  static float [] MIN_LEVEL = { 2, 4, 6, 8, 10 };
  
  //Cannon
  
  static float CANNON_SKILL_T1_COST = 60;
  static float CANNON_SKILL_T2_COST = 120;
  static float CANNON_SKILL_T3_COST = 240;
  static float CANNON_SKILL_T4_COST = 480;
  static float CANNON_SKILL_T5_COST = 960;
  
    //LevelA
    
      //T1
  static String CANNON_SKILL_A_T1_NAME = "Steady Aim";
  static String CANNON_SKILL_A_T1_DESCRIPTION = "Greatly increases the damage.";
  static float CANNON_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.3;
  
      //T2
  static String CANNON_SKILL_A_T2_NAME = "Reaper";
  static String CANNON_SKILL_A_T2_DESCRIPTION = "Deals bonus damage based on the missing health of the target.";
  static float CANNON_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 1.5;
  
      //T3
  static String CANNON_SKILL_A_T3_NAME = "Acid Infusion";
  static String CANNON_SKILL_A_T3_DESCRIPTION = "Applies a debuff that cut the victim's armor in half.";
  static float CANNON_SKILL_A_T3_ARMOR_REDUCTION_PERCENTAGE = 0.5;
  static float CANNON_SKILL_A_T3_DURATION = 300;
  
      //T4
  static String CANNON_SKILL_A_T4_NAME = "Headhunter";
  static String CANNON_SKILL_A_T4_DESCRIPTION = "Greatly increases the critical damage.";
  static float CANNON_SKILL_A_T4_BONUS_CRITICAL_DAMAGE_MULTIPLIER = 0.5;
  
      //T5
  static String CANNON_SKILL_A_T5_NAME = "Saboteur";
  static String CANNON_SKILL_A_T5_DESCRIPTION = "Deals additional damage based on target’s current health.";
  static float CANNON_SKILL_A_T5_HP_PERCENTAGE = 0.002;
  
  
    //LevelB
    
      //T1
  static String CANNON_SKILL_B_T1_NAME = "Rapid Fire";
  static String CANNON_SKILL_B_T1_DESCRIPTION = "Greatly increases the fire rate.";
  static float CANNON_SKILL_B_T1_EXTRA_FIRE_RATE_MULTIPLIER = 0.25;
  
      //T2
  static String CANNON_SKILL_B_T2_NAME = "Ballistics";
  static String CANNON_SKILL_B_T2_DESCRIPTION = "Greatly increases the projectile speed.";
  static float CANNON_SKILL_B_T2_EXTRA_PROJECTILE_SPEED_MULTIPLIER = 1;
  
      //T3
  static String CANNON_SKILL_B_T3_NAME = "Fervor";
  static String CANNON_SKILL_B_T3_DESCRIPTION = "Each continuous attack on the same target stacks fire rate; loses all on changing target.";
  static float CANNON_SKILL_B_T3_BONUS_FIRE_RATE_MULTIPLIER_PER_STACK = 0.1;
  static int CANNON_SKILL_B_T3_MAX_STACK = 15;
  
      //T4
  static String CANNON_SKILL_B_T4_NAME = "Bore";
  static String CANNON_SKILL_B_T4_DESCRIPTION = "Greatly increases the chance of critical hits.";
  static float CANNON_SKILL_B_T4_BONUS_CRIT_CHANCE = 0.08;
  
      //T5
  static String CANNON_SKILL_B_T5_NAME = "Death Wish";
  static String CANNON_SKILL_B_T5_DESCRIPTION = "Increases the chance of critical hits based on the missing health of the castle.";
  static float CANNON_SKILL_B_T5_MIN_BONUS_CRIT_CHANCE = 0.05;
  static float CANNON_SKILL_B_T5_MAX_BONUS_CRIT_CHANCE = 0.30;
  
    //LevelC
    
      //T1
  static String CANNON_SKILL_C_T1_NAME = "Cold Snap";
  static String CANNON_SKILL_C_T1_DESCRIPTION = "Applies a debuff that slows enemies.";
  static float CANNON_SKILL_C_T1_SLOW_PERCENTAGE = 0.20;
  static int CANNON_SKILL_C_T1_DURATION = 45;
  
      //T2
  static String CANNON_SKILL_C_T2_NAME = "Ionic Shell";
  static String CANNON_SKILL_C_T2_DESCRIPTION = "Applies a damaging aura on the target, hurting the enemies around the carrier but not itself.";
  static float CANNON_SKILL_C_T2_BASE_CANNON_DAMAGE_PERCENTAGE = 0.04;
  static float CANNON_SKILL_C_T2_RADIUS = 180;
  static float CANNON_SKILL_C_T2_DAMAGE_INTERVAL = 15;
  static float CANNON_SKILL_C_T2_DURATION = 480;
  
      //T3
  static String CANNON_SKILL_C_T3_NAME = "Boombastics";
  static String CANNON_SKILL_C_T3_DESCRIPTION = "Cannons explode on impact.";
  static float CANNON_SKILL_C_T3_EXPLOSION_RADIUS = 80;
  static float CANNON_SKILL_C_T3_EXPLOSION_DAMAGE_MULTIPLIER = 0.8;
  
      //T4   
  static String CANNON_SKILL_C_T4_NAME = "Eagle Sight";
  static String CANNON_SKILL_C_T4_DESCRIPTION = "Greatly increases the attack range of the turret.";
  static float CANNON_SKILL_C_T4_ATTACK_RANGE_MULTIPLIER = 0.6;
  
      //T5
  static String CANNON_SKILL_C_T5_NAME = "M.A.I.M.";
  static String CANNON_SKILL_C_T5_DESCRIPTION = "On critical hit, applies a debuff that severely cripples the victim.";
  static float CANNON_SKILL_C_T5_SLOW_PERCENTAGE = 0.9;
  static float CANNON_SKILL_C_T5_DURATION = 3;
  
  //Laser
  
  static float LASER_SKILL_T1_COST = 80;
  static float LASER_SKILL_T2_COST = 160;
  static float LASER_SKILL_T3_COST = 320;
  static float LASER_SKILL_T4_COST = 640;
  static float LASER_SKILL_T5_COST = 1280;
  
    //LevelA
    
      //T1
  static String LASER_SKILL_A_T1_NAME = "Energy Boost";
  static String LASER_SKILL_A_T1_DESCRIPTION = "Greatly increases the damage.";
  static float LASER_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.5;
  
      //T2
  static String LASER_SKILL_A_T2_NAME = "Thermocide";
  static String LASER_SKILL_A_T2_DESCRIPTION = "Increases the damage based on the heat of the laser.";
  static float LASER_SKILL_A_T2_MIN_HEAT_THRESHOLD = 120;
  static float LASER_SKILL_A_T2_MAX_DAMAGE_HEAT_CAP = 240;
  static float LASER_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 2.00;
  
      //T3
  static String LASER_SKILL_A_T3_NAME = "Dematerialization";
  static String LASER_SKILL_A_T3_DESCRIPTION = "Greatly increases the effectiveness against armor.";
  static float LASER_SKILL_A_T3_ARMOR_BYPASS_MULTIPLIER = 0.2;
  
      //T4
  static String LASER_SKILL_A_T4_NAME = "Combustion";
  static String LASER_SKILL_A_T4_DESCRIPTION = "Extends the crit mode duration and increases the critical damage.";
  static float LASER_SKILL_A_T4_BONUS_CRIT_MODE_DURATION_MULTIPLIER = 1;
  static float LASER_SKILL_A_T4_BONUS_CRITICAL_DAMAGE_MULTIPLIER = 1;
  
      //T5
  static String LASER_SKILL_A_T5_NAME = "DEATHSTAR";
  static String LASER_SKILL_A_T5_DESCRIPTION = "Calls a ray from above, damaging a random victim caught by the laser.";
  static float LASER_SKILL_A_T5_DAMAGE_INTERVAL = 12;
  static float LASER_SKILL_A_T5_BONUS_DAMAGE_MULTIPLIER = 60;
  
  
    //LevelB
    
      //T1
  static String LASER_SKILL_B_T1_NAME = "Overdrive";
  static String LASER_SKILL_B_T1_DESCRIPTION = "When the overheat occurs, decreases the cool down time based on the amount of enemies in the attack range.";
  static float LASER_SKILL_B_T1_COOLDOWN_REDUCTION_MULTIPLIER_PER_ENEMY = 0.08;
  static float LASER_SKILL_B_T1_MAXIMUM_COOLDOWN_REDUCTION_MULTIPLIER = 0.40;
  
      //T2
  static String LASER_SKILL_B_T2_NAME = "Heat Lock";
  static String LASER_SKILL_B_T2_DESCRIPTION = "During crit mode, the turret is safe from overheat.";
  
      //T3
  static String LASER_SKILL_B_T3_NAME = "DNA Mutation";
  static String LASER_SKILL_B_T3_DESCRIPTION = "Applies a debuff that inflates the enemy, causing them more prone to get hit and take extra damage.";
  static float LASER_SKILL_B_T3_SIZE_INFLATION_AMOUNT_PER_SEC = 10;
  static float LASER_SKILL_B_T3_MAX_INFLATION_AMOUNT = 20;
  static float LASER_SKILL_B_T3_EXTRA_DAMAGE_MULTIPLER = 0.5;
  static float LASER_SKILL_B_T3_DURATION = 300;
  
      //T4
  static String LASER_SKILL_B_T4_NAME = "Supercool";
  static String LASER_SKILL_B_T4_DESCRIPTION = "Increases the overheat threshold.";
  static float LASER_SKILL_B_T4_OVERHEAT_THRESHOLD_MULTIPLIER = 1.5;
  
      //T5
  static String LASER_SKILL_B_T5_NAME = "Reverse Polarity";
  static String LASER_SKILL_B_T5_DESCRIPTION = "Increases damage after each pierce.";
  static float LASER_SKILL_B_T5_PENETRATION_AMP = 2;
  
  
  
    //LevelC
    
      //T1
  static String LASER_SKILL_C_T1_NAME = "Prism";
  static String LASER_SKILL_C_T1_DESCRIPTION = "Increases the width of the laser.";
  static float LASER_SKILL_C_T1_BEAM_WIDTH_MULTIPLIER = 3;
  
      //T2
  static String LASER_SKILL_C_T2_NAME = "Imflammation";
  static String LASER_SKILL_C_T2_DESCRIPTION = "Applies a debuff that slows enemies.";
  static float LASER_SKILL_C_T2_SLOW_PERCENTAGE = 0.15;
  static float LASER_SKILL_C_T2_DURATION = 6;
  
      //T3
  static String LASER_SKILL_C_T3_NAME = "Pitchfork";
  static String LASER_SKILL_C_T3_DESCRIPTION = "Creates two additional lasers that deals less damage; lasers cannot share the same target.";
  static float LASER_SKILL_C_T3_MINI_BEAM_COUNT = 2;
  static float LASER_SKILL_C_T3_MINI_BEAM_DAMAGE_MULTIPLIER = 0.6;
  
      //T4
  static String LASER_SKILL_C_T4_NAME = "Breach Module";
  static String LASER_SKILL_C_T4_DESCRIPTION = "Applies a debuff that slows enemies based on their missing health.";
  static float LASER_SKILL_C_T4_MAXIMUM_SLOW_PERCENTAGE = 0.4;
  static float LASER_SKILL_C_T4_DURATION = 6;
  
      //T5
  static String LASER_SKILL_C_T5_NAME = "Volatile Compound";
  static String LASER_SKILL_C_T5_DESCRIPTION = "Applies a debuff that when the carrier dies, its body explodes, damaging all nearby enemies.";
  static float LASER_SKILL_C_T5_MAX_HEALTH_PERCENTAGE_AS_DAMAGE = 0.1;
  static float LASER_SKILL_C_T5_RADIUS = 150;
  static float LASER_SKILL_C_T5_DURATION = 600;
  
  
  //Aura
  
  static float AURA_SKILL_T1_COST = 60;
  static float AURA_SKILL_T2_COST = 120;
  static float AURA_SKILL_T3_COST = 240;
  static float AURA_SKILL_T4_COST = 480;
  static float AURA_SKILL_T5_COST = 960;
  
    //LevelA
    
      //T1
  static String AURA_SKILL_A_T1_NAME = "Steady Aim";
  static String AURA_SKILL_A_T1_DESCRIPTION = "Greatly increases the damage.";
  static float AURA_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.3;
  
      //T2
  static String AURA_SKILL_A_T2_NAME = "Reaper";
  static String AURA_SKILL_A_T2_DESCRIPTION = "Deals bonus damage based on the missing health of the target.";
  static float AURA_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 1.5;
  
      //T3
  static String AURA_SKILL_A_T3_NAME = "Acid Infusion";
  static String AURA_SKILL_A_T3_DESCRIPTION = "Applies a debuff that cut the victim's armor in half.";
  static float AURA_SKILL_A_T3_ARMOR_REDUCTION_PERCENTAGE = 0.5;
  static float AURA_SKILL_A_T3_DURATION = 300;
  
      //T4
  static String AURA_SKILL_A_T4_NAME = "Headhunter";
  static String AURA_SKILL_A_T4_DESCRIPTION = "Greatly increases the critical damage.";
  static float AURA_SKILL_A_T4_BONUS_CRITICAL_DAMAGE_MULTIPLIER = 0.5;
  
      //T5
  static String AURA_SKILL_A_T5_NAME = "Saboteur";
  static String AURA_SKILL_A_T5_DESCRIPTION = "Deals additional damage based on target’s current health.";
  static float AURA_SKILL_A_T5_HP_PERCENTAGE = 0.002;
  
  
    //LevelB
    
      //T1
  static String AURA_SKILL_B_T1_NAME = "Rapid Fire";
  static String AURA_SKILL_B_T1_DESCRIPTION = "Greatly increases the fire rate.";
  static float AURA_SKILL_B_T1_EXTRA_FIRE_RATE_MULTIPLIER = 0.25;
  
      //T2
  static String AURA_SKILL_B_T2_NAME = "Ballistics";
  static String AURA_SKILL_B_T2_DESCRIPTION = "Greatly increases the projectile speed.";
  static float AURA_SKILL_B_T2_EXTRA_PROJECTILE_SPEED_MULTIPLIER = 1;
  
      //T3
  static String AURA_SKILL_B_T3_NAME = "Fervor";
  static String AURA_SKILL_B_T3_DESCRIPTION = "Each continuous attack on the same target stacks fire rate; loses all on changing target.";
  static float AURA_SKILL_B_T3_BONUS_FIRE_RATE_MULTIPLIER_PER_STACK = 0.1;
  static int AURA_SKILL_B_T3_MAX_STACK = 15;
  
      //T4
  static String AURA_SKILL_B_T4_NAME = "Bore";
  static String AURA_SKILL_B_T4_DESCRIPTION = "Greatly increases the chance of critical hits.";
  static float AURA_SKILL_B_T4_BONUS_CRIT_CHANCE = 0.08;
  
      //T5
  static String AURA_SKILL_B_T5_NAME = "Death Wish";
  static String AURA_SKILL_B_T5_DESCRIPTION = "Increases the chance of critical hits based on the missing health of the castle.";
  static float AURA_SKILL_B_T5_MIN_BONUS_CRIT_CHANCE = 0.05;
  static float AURA_SKILL_B_T5_MAX_BONUS_CRIT_CHANCE = 0.30;
  
    //LevelC
    
      //T1
  static String AURA_SKILL_C_T1_NAME = "Cold Snap";
  static String AURA_SKILL_C_T1_DESCRIPTION = "Applies a debuff that slows enemies.";
  static float AURA_SKILL_C_T1_SLOW_PERCENTAGE = 0.20;
  static int AURA_SKILL_C_T1_DURATION = 60;
  
      //T2
  static String AURA_SKILL_C_T2_NAME = "Ionic Shell";
  static String AURA_SKILL_C_T2_DESCRIPTION = "Applies a damaging aura on the target, hurting the enemies around the carrier but not itself.";
  static float AURA_SKILL_C_T2_BASE_AURA_DAMAGE_PERCENTAGE = 0.04;
  static float AURA_SKILL_C_T2_RADIUS = 180;
  static float AURA_SKILL_C_T2_DAMAGE_INTERVAL = 15;
  static float AURA_SKILL_C_T2_DURATION = 480;
  
      //T3
  static String AURA_SKILL_C_T3_NAME = "Boombastics";
  static String AURA_SKILL_C_T3_DESCRIPTION = "AURAs explode on impact, damaging and applying debuffs to all victims.";
  static float AURA_SKILL_C_T3_EXPLOSION_RADIUS = 120;
  static float AURA_SKILL_C_T3_EXPLOSION_DAMAGE_MULTIPLIER = 0.8;
  
      //T4
  static String AURA_SKILL_C_T4_NAME = "Eagle Sight";
  static String AURA_SKILL_C_T4_DESCRIPTION = "Greatly increases the attack range of the turret.";
  static float AURA_SKILL_C_T4_ATTACK_RANGE_MULTIPLIER = 0.6;
  
      //T5
  static String AURA_SKILL_C_T5_NAME = "M.A.I.M.";
  static String AURA_SKILL_C_T5_DESCRIPTION = "On critical hit, applies a debuff that severely cripples the victim.";
  static float AURA_SKILL_C_T5_SLOW_PERCENTAGE = 0.6;
  static float AURA_SKILL_C_T5_DURATION = 30;
  
}