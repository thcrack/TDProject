static class TurretLevelData{
  static int maxLevel = 10;
  static int cannonBuildCost = 30;
  static int laserBuildCost = 40;
  static int auraBuildCost = 50;
  
  static color CANNON_COLOR = #1FEAFF;
  static color LASER_COLOR = #F4F520;
  static color LASER_OVERHEAT_COLOR = #FF7708;
  static color AURA_COLOR = #D916F7;
  
  static float cannonCritChance = 0.08;
  static float cannonCritDamageMultiplier = 3;
  static float cannonProjSize = 15;
  static float cannonProjSpeed = 10;
  static float [] cannonDamage = {100,200,300,400,500,600,700,800,900,1000,1100};
  static float [] cannonRate = {1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,2.1,2.2};
  static float [] cannonRange = {135,150,165,180,195,210,225,240,255,270,285};
  static int [] cannonCostA = {15,21,27,33,39,45,51,57,63,69,75};
  static int [] cannonCostB = {15,21,27,33,39,45,51,57,63,69,75};
  static int [] cannonCostC = {15,21,27,33,39,45,51,57,63,69,75};
  
  static float laserCritChance = 0.08;
  static float laserCritDamageMultiplier = 3;
  static float laserCritDuration = 60;
  static float laserCritCheckInterval = 60;
  static float laserWidth = 10;
  static float laserOverheatThreshold = 240;
  static float laserPiercePenaltyMultiplier = 0.8;
  static float [] laserDamage = {3,6,9,12,15,18,21,24,27,30,33};
  static float [] laserRate = {2.5,2.3,2.1,1.9,1.7,1.5,1.3,1.1,0.9,0.7,0.5};
  static float [] laserRange = {180,200,220,240,260,280,300,320,340,360,380};
  static int [] laserCostA = {20,28,36,44,52,60,68,76,84,92,100};
  static int [] laserCostB = {20,28,36,44,52,60,68,76,84,92,100};
  static int [] laserCostC = {20,28,36,44,52,60,68,76,84,92,100};
  
  static float auraCritChance = 0.08;
  static float auraCritDamageMultiplier = 3;
  static float auraCritDuration = 60;
  static float auraCritCheckInterval = 60;
  static float [] auraDamage = {40,80,120,160,200,240,280,320,360,400,440};
  static float [] auraRate = {3.0,3.5,4.0,4.5,5.0,5.5,6.0,6.5,7.0,7.5,8.0};
  static float [] auraRange = {70,77,84,91,98,105,112,119,126,133,140};
  static int [] auraCostA = {25,35,45,55,65,75,85,95,105,115,125};
  static int [] auraCostB = {25,35,45,55,65,75,85,95,105,115,125};
  static int [] auraCostC = {25,35,45,55,65,75,85,95,105,115,125};
}