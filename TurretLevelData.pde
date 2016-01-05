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
  static int [] cannonCostA = {15,30,45,60,75,90,105,120,135,150,165};
  static int [] cannonCostB = {15,30,45,60,75,90,105,120,135,150,165};
  static int [] cannonCostC = {15,30,45,60,75,90,105,120,135,150,165};
  
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
  static int [] laserCostA = {20,40,60,80,100,120,140,160,180,200,220};
  static int [] laserCostB = {20,40,60,80,100,120,140,160,180,200,220};
  static int [] laserCostC = {20,40,60,80,100,120,140,160,180,200,220};
  
  static float auraCritChance = 0.08;
  static float auraCritDamageMultiplier = 3;
  static float auraCritDuration = 60;
  static float auraCritCheckInterval = 60;
  static float [] auraDamage = {15,30,45,60,75,90,105,120,135,150,165};
  static float [] auraRate = {2.0,2.5,3.0,3.5,4.0,4.5,5.0,5.5,6.0,6.5,7.0};
  static float [] auraRange = {70,77,84,91,98,105,112,119,126,133,140};
  static int [] auraCostA = {25,50,75,100,125,150,175,200,225,250,275};
  static int [] auraCostB = {25,50,75,100,125,150,175,200,225,250,275};
  static int [] auraCostC = {25,50,75,100,125,150,175,200,225,250,275};
}