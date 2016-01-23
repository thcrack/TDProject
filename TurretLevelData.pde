static class TurretLevelData{
  static int maxLevel = 10;
  static int cannonBuildCost = 30;
  static int laserBuildCost = 40;
  static int auraBuildCost = 50;
  
  static color CANNON_COLOR = #1FEAFF;
  static color LASER_COLOR = #F4F520;
  static color LASER_OVERHEAT_COLOR = #FF7708;
  static color AURA_COLOR = #D916F7;
  
  static float cannonCritChance = 0.12;
  static float cannonCritDamageMultiplier = 3;
  static float cannonProjSize = 15;
  static float cannonProjSpeed = 8;
  static float [] cannonDamage = {120,240,360,480,600,720,840,960,1080,1200,1320};
  static float [] cannonRate = {1.00,1.20,1.40,1.60,1.80,2.00,2.20,2.40,2.60,2.80,3.00};
  static float [] cannonRange = {150,165,180,195,210,225,240,255,270,285,300};
  static int [] cannonCostA = {24,27,30,33,36,39,42,45,48,51,54};
  static int [] cannonCostB = {24,27,30,33,36,39,42,45,48,51,54};
  static int [] cannonCostC = {24,27,30,33,36,39,42,45,48,51,54};
  
  static float laserCritChance = 0.08;
  static float laserCritDamageMultiplier = 2;
  static float laserCritDuration = 60;
  static float laserCritCheckInterval = 60;
  static float laserWidth = 10;
  static float laserOverheatThreshold = 240;
  static float laserPiercePenaltyMultiplier = 0.8;
  static float [] laserDamage = {4,8,12,16,20,24,28,32,36,40,44};
  static float [] laserRate = {3.0,2.75,2.50,2.25,2.0,1.75,1.5,1.25,1.0,0.75,0.5};
  static float [] laserRange = {160,180,200,220,240,260,280,300,320,340,360};
  static int [] laserCostA = {32,36,40,44,48,52,56,60,64,68,72};
  static int [] laserCostB = {32,36,40,44,48,52,56,60,64,68,72};
  static int [] laserCostC = {32,36,40,44,48,52,56,60,64,68,72};
  
  static float auraCritChance = 0.08;
  static float auraCritDamageMultiplier = 2;
  static float auraCritDuration = 60;
  static float auraCritCheckInterval = 60;
  static float [] auraDamage = {40,80,120,160,200,240,280,320,360,400,440};
  static float [] auraRate = {3.0,3.5,4.0,4.5,5.0,5.5,6.0,6.5,7.0,7.5,8.0};
  static float [] auraRange = {70,77,84,91,98,105,112,119,126,133,140};
  static int [] auraCostA = {40,45,50,55,60,65,70,75,80,85,90};
  static int [] auraCostB = {40,45,50,55,60,65,70,75,80,85,90};
  static int [] auraCostC = {40,45,50,55,60,65,70,75,80,85,90};
}