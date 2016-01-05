static class TurretSkillData{
  //Tier Limitation
  
  static float [] MIN_LEVEL = { 2, 4, 6, 8, 10 };
  
  //Cannon
  
  static int CANNON_SKILL_T1_COST = 30;
  static int CANNON_SKILL_T2_COST = 60;
  static int CANNON_SKILL_T3_COST = 120;
  static int CANNON_SKILL_T4_COST = 240;
  static int CANNON_SKILL_T5_COST = 480;
  
    //LevelA
    
      //T1
  static String CANNON_SKILL_A_T1_NAME = "Steady Aim";
  static String CANNON_SKILL_A_T1_DESCRIPTION = "Greatly increases the damage.";
  static float CANNON_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.5;
  
      //T2
  static String CANNON_SKILL_A_T2_NAME = "Reaper";
  static String CANNON_SKILL_A_T2_DESCRIPTION = "Deals bonus damage based on the missing health of the target.";
  static float CANNON_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 2;
  
      //T3
  static String CANNON_SKILL_A_T3_NAME = "Acid Infusion";
  static String CANNON_SKILL_A_T3_DESCRIPTION = "Applies a debuff that weakens the armor.";
  static float CANNON_SKILL_A_T3_ARMOR_DAMAGE_MULTIPLIER = 1;
  static float CANNON_SKILL_A_T3_DURATION = 300;
  
      //T4
  static String CANNON_SKILL_A_T4_NAME = "Headhunter";
  static String CANNON_SKILL_A_T4_DESCRIPTION = "Greatly increases the critical damage.";
  static float CANNON_SKILL_A_T4_BONUS_CRITICAL_DAMAGE_MULTIPLIER = 1;
  
      //T5
  static String CANNON_SKILL_A_T5_NAME = "Saboteur";
  static String CANNON_SKILL_A_T5_DESCRIPTION = "Deals additional damage based on target’s current health.";
  static float CANNON_SKILL_A_T5_HP_PERCENTAGE = 0.001;
  
  
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
  static float CANNON_SKILL_B_T3_BONUS_FIRE_RATE_MULTIPLIER_PER_STACK = 0.15;
  static int CANNON_SKILL_B_T3_MAX_STACK = 10;
  
      //T4
  static String CANNON_SKILL_B_T4_NAME = "Bore";
  static String CANNON_SKILL_B_T4_DESCRIPTION = "Greatly increases the chance of critical hits.";
  static float CANNON_SKILL_B_T4_BONUS_CRIT_CHANCE = 0.08;
  
      //T5
  static String CANNON_SKILL_B_T5_NAME = "Death Wish";
  static String CANNON_SKILL_B_T5_DESCRIPTION = "Increases the chance of critical hits based on the missing health of the base.";
  static float CANNON_SKILL_B_T5_MIN_BONUS_CRIT_CHANCE = 0.04;
  static float CANNON_SKILL_B_T5_MAX_BONUS_CRIT_CHANCE = 0.16;
  
    //LevelC
    
      //T1
  static String CANNON_SKILL_C_T1_NAME = "Cold Snap";
  static String CANNON_SKILL_C_T1_DESCRIPTION = "Applies a debuff that slows enemies.";
  static float CANNON_SKILL_C_T1_SLOW_PERCENTAGE = 0.15;
  static int CANNON_SKILL_C_T1_DURATION = 60;
  
      //T2
  static String CANNON_SKILL_C_T2_NAME = "Ionic Shell";
  static String CANNON_SKILL_C_T2_DESCRIPTION = "Applies a damaging aura on the target, hurting the enemies around the carrier but not itself.";
  static float CANNON_SKILL_C_T2_BASE_CANNON_DAMAGE_PERCENTAGE = 0.01;
  static float CANNON_SKILL_C_T2_RADIUS = 180;
  static float CANNON_SKILL_C_T2_DAMAGE_INTERVAL = 6;
  static float CANNON_SKILL_C_T2_DURATION = 240;
  
      //T3
  static String CANNON_SKILL_C_T3_NAME = "Boombastics";
  static String CANNON_SKILL_C_T3_DESCRIPTION = "Cannons explode on impact.";
  static float CANNON_SKILL_C_T3_EXPLOSION_RADIUS = 80;
  static float CANNON_SKILL_C_T3_EXPLOSION_DAMAGE_MULTIPLIER = 0.6;
  
      //T4   
  static String CANNON_SKILL_C_T4_NAME = "Eagle Sight";
  static String CANNON_SKILL_C_T4_DESCRIPTION = "Greatly increases the attack range of the turret.";
  static float CANNON_SKILL_C_T4_ATTACK_RANGE_MULTIPLIER = 0.3;
  
      //T5
  static String CANNON_SKILL_C_T5_NAME = "M.A.I.M.";
  static String CANNON_SKILL_C_T5_DESCRIPTION = "Cripples the victim on crit; the slow becomes weaker after each trigger.";
  static float CANNON_SKILL_C_T5_SLOW_PERCENTAGE = 0.4;
  static float CANNON_SKILL_C_T5_MIN_SLOW_PERCENTAGE = 0.1;
  static float CANNON_SKILL_C_T5_DURATION = 60;
  
  //Laser
  
  static int LASER_SKILL_T1_COST = 40;
  static int LASER_SKILL_T2_COST = 80;
  static int LASER_SKILL_T3_COST = 160;
  static int LASER_SKILL_T4_COST = 320;
  static int LASER_SKILL_T5_COST = 640;
  
    //LevelA
    
      //T1
  static String LASER_SKILL_A_T1_NAME = "Energy Boost";
  static String LASER_SKILL_A_T1_DESCRIPTION = "Greatly increases the damage.";
  static float LASER_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.8;
  
      //T2
  static String LASER_SKILL_A_T2_NAME = "Thermocide";
  static String LASER_SKILL_A_T2_DESCRIPTION = "Increases the damage based on the heat of the laser.";
  static float LASER_SKILL_A_T2_MIN_HEAT_THRESHOLD = 120;
  static float LASER_SKILL_A_T2_MAX_DAMAGE_HEAT_CAP = 240;
  static float LASER_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 3.00;
  
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
  static float LASER_SKILL_A_T5_DAMAGE_INTERVAL = 15;
  static float LASER_SKILL_A_T5_BONUS_DAMAGE_MULTIPLIER = 60;
  
  
    //LevelB
    
      //T1
  static String LASER_SKILL_B_T1_NAME = "Overdrive";
  static String LASER_SKILL_B_T1_DESCRIPTION = "When the overheat occurs, decreases the cool down time based on the amount of enemies in the attack range.";
  static float LASER_SKILL_B_T1_COOLDOWN_REDUCTION_MULTIPLIER_PER_ENEMY = 0.05;
  static float LASER_SKILL_B_T1_MAXIMUM_COOLDOWN_REDUCTION_MULTIPLIER = 0.50;
  
      //T2
  static String LASER_SKILL_B_T2_NAME = "Heat Lock";
  static String LASER_SKILL_B_T2_DESCRIPTION = "During crit mode, the turret is safe from overheat, and the countdown stops when not attacking.";
  
      //T3
  static String LASER_SKILL_B_T3_NAME = "DNA Mutation";
  static String LASER_SKILL_B_T3_DESCRIPTION = "Applies a debuff that inflates the enemy, causing them more prone to get hit and take extra damage.";
  static float LASER_SKILL_B_T3_SIZE_INFLATION_AMOUNT_PER_SEC = 10;
  static float LASER_SKILL_B_T3_MAX_INFLATION_AMOUNT = 20;
  static float LASER_SKILL_B_T3_EXTRA_DAMAGE_MULTIPLER = 0.25;
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
  static float LASER_SKILL_C_T2_DURATION = 15;
  
      //T3
  static String LASER_SKILL_C_T3_NAME = "Pitchfork";
  static String LASER_SKILL_C_T3_DESCRIPTION = "Creates two additional lasers that deals less damage; lasers cannot share the same target.";
  static float LASER_SKILL_C_T3_MINI_BEAM_COUNT = 2;
  static float LASER_SKILL_C_T3_MINI_BEAM_DAMAGE_MULTIPLIER = 0.8;
  
      //T4
  static String LASER_SKILL_C_T4_NAME = "Volatile Compound";
  static String LASER_SKILL_C_T4_DESCRIPTION = "Applies a debuff that when the carrier dies, its body explodes, damaging nearby enemies.";
  static float LASER_SKILL_C_T4_MAX_HEALTH_PERCENTAGE_AS_DAMAGE = 0.1;
  static float LASER_SKILL_C_T4_RADIUS = 150;
  static float LASER_SKILL_C_T4_DURATION = 600;
  
      //T5
  static String LASER_SKILL_C_T5_NAME = "Breach";
  static String LASER_SKILL_C_T5_DESCRIPTION = "Applies a debuff that slows enemies based on their missing health.";
  static float LASER_SKILL_C_T5_MAXIMUM_SLOW_PERCENTAGE = 0.20;
  static float LASER_SKILL_C_T5_DURATION = 15;
  
  //Aura
  
  static int AURA_SKILL_T1_COST = 80;
  static int AURA_SKILL_T2_COST = 160;
  static int AURA_SKILL_T3_COST = 320;
  static int AURA_SKILL_T4_COST = 640;
  static int AURA_SKILL_T5_COST = 1280;
  
    //LevelA
    
      //T1
  static String AURA_SKILL_A_T1_NAME = "Courage Module";
  static String AURA_SKILL_A_T1_DESCRIPTION = "Applies a buff that increases damage to nearby turrets.";
  static float AURA_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.4;
  
      //T2
  static String AURA_SKILL_A_T2_NAME = "Tension";
  static String AURA_SKILL_A_T2_DESCRIPTION = "Deals bonus damage based on the distance of the victim.";
  static float AURA_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 3;
  static float AURA_SKILL_A_T2_MAXIMUM_EFFECTIVE_RANGE = 0.8;
  
      //T3
  static String AURA_SKILL_A_T3_NAME = "Corrosive Gas";
  static String AURA_SKILL_A_T3_DESCRIPTION = "Drains a percentage of armor on every strike.";
  static float AURA_SKILL_A_T3_ARMOR_DRAIN_PERCENTAGE = 0.03;
  
      //T4
  static String AURA_SKILL_A_T4_NAME = "Nano Death Machine";
  static String AURA_SKILL_A_T4_DESCRIPTION = "Creates orbs that damage random enemies, sticking on the victim until it's dead or out of range.";
  static float AURA_SKILL_A_T4_DAMAGE_PERCENTAGE = 0.6;
  static int AURA_SKILL_A_T4_ORB_COUNT = 6;
  
      //T5
  static String AURA_SKILL_A_T5_NAME = "Cancer";
  static String AURA_SKILL_A_T5_DESCRIPTION = "Applies a stacking debuff, dealing damage that is multiplied every 3 stacks.";
  static float AURA_SKILL_A_T5_BASE_DAMAGE_PERCENTAGE = 0.01;
  static float AURA_SKILL_A_T5_STACK_CAP = 36;
  static float AURA_SKILL_A_T5_DURATION = 150;
  static float AURA_SKILL_A_T5_DAMAGE_INTERVAL = 15;
  
  
    //LevelB
    
      //T1
  static String AURA_SKILL_B_T1_NAME = "Morale Module";
  static String AURA_SKILL_B_T1_DESCRIPTION = "During crit mode, applies a buff that increases crit chance to nearby turrets.";
  static float AURA_SKILL_B_T1_EXTRA_CRIT_CHANCE = 3;
  
      //T2
  static String AURA_SKILL_B_T2_NAME = "Meditation";
  static String AURA_SKILL_B_T2_DESCRIPTION = "When there's no enemy in the range, charges the turret to provide extra damage for later use.";
  static float AURA_SKILL_B_T2_MAXIMUM_BONUS_DAMAGE = 24;
  static float AURA_SKILL_B_T2_CHARGE_RATE_PER_SEC = 4;
  static float AURA_SKILL_B_T2_DRAIN_RATE_PER_SEC = 8;
  
      //T3
  static String AURA_SKILL_B_T3_NAME = "Bloodlust";
  static String AURA_SKILL_B_T3_DESCRIPTION = "During crit mode, the duration is reset when an enemy dies in the attack range.";
  
      //T4
  static String AURA_SKILL_B_T4_NAME = "Repellant";
  static String AURA_SKILL_B_T4_DESCRIPTION = "Applies a debuff that nullifies all the buffs and keeps the armor from regenerating.";
  static float AURA_SKILL_B_T4_DURATION = 900;
  
      //T5
  static String AURA_SKILL_B_T5_NAME = "Curse";
  static String AURA_SKILL_B_T5_DESCRIPTION = "Applies a debuff that instantly kills the carrier when its health falls below the threshold.";
  static float AURA_SKILL_B_T5_HEALTH_THRESHOLD = 0.10;
  static float AURA_SKILL_B_T5_DURATION = 300;
  
    //LevelC
    
      //T1
  static String AURA_SKILL_C_T1_NAME = "Freeze Module";
  static String AURA_SKILL_C_T1_DESCRIPTION = "Applies a debuff that slows the enemies.";
  static float AURA_SKILL_C_T1_SLOW_PERCENTAGE = 0.20;
  static int AURA_SKILL_C_T1_DURATION = 15;
  
      //T2
  static String AURA_SKILL_C_T2_NAME = "Decrepify";
  static String AURA_SKILL_C_T2_DESCRIPTION = "Deals bonus damage based on the missing health of enemies in the attack range.";
  static float AURA_SKILL_C_T2_BONUS_DAMAGE_PER_PERCENT_OF_MISSING_HEALTH = 0.02;
  
      //T3
  static String AURA_SKILL_C_T3_NAME = "A.W.E.";
  static String AURA_SKILL_C_T3_DESCRIPTION = "When entering crit mode, releases a shockwave that slows and deals damage.";
  static float AURA_SKILL_C_T3_MAX_RADIUS_RATIO = 2.5;
  static float AURA_SKILL_C_T3_SHOCKWAVE_SPEED_RATIO = 0.025;
  static float AURA_SKILL_C_T3_SLOW_PERCENTAGE = 0.3;
  static float AURA_SKILL_C_T3_SLOW_DURATION = 60;
  
      //T4
  static String AURA_SKILL_C_T4_NAME = "Synchronize";
  static String AURA_SKILL_C_T4_DESCRIPTION = "Applies a debuff that makes the carrier feel the pain even when out of range.";
  static float AURA_SKILL_C_T4_DURATION = 300;
  
      //T5
  static String AURA_SKILL_C_T5_NAME = "Fatal Bond";
  static String AURA_SKILL_C_T5_DESCRIPTION = "Applies a debuff that causes the damage dealt to one of the enemies to be felt by the others.";
  static float AURA_SKILL_C_T5_DAMAGE_SHARE_PERCENTAGE = 0.2;
  static float AURA_SKILL_C_T5_DURATION = 300;
  
}