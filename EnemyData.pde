static class EnemyData{
  
  //Global Attributes
  
  static float MIN_SPEED = 0.05;
  static float [] ARMOR_ABSORB_RATIO = {0.5,0.6,0.8};
  static int [] ARMOR_REGEN_DELAY = {600,300,180};
  
  static float BUFF_FORTIFIED_MULTIPLIER = 0.3;
  static float BUFF_HASTE_MULTIPLIER = 0.5;
  static float BUFF_HEALTH_REGEN_RATE = 0.002;
  static float BUFF_TOUGH_SKIN_MULTIPLIER = 0.3;
  static float BUFF_WEAVE_MULTIPLIER = 1;
  static float BUFF_WEAVE_DELAY_REDUCTION = 0.5;
  
  //Normal
  
  static color NORMAL_COLOR = #FF0000;
  static float NORMAL_SIZE = 35;
  static float [] NORMAL_MAX_HEALTH = {150,200,300};
  static float [] NORMAL_POWER = {10,10,20};
  static float [] NORMAL_MAX_ARMOR = {25,50,100};
  static float [] NORMAL_ARMOR_REGEN_RATE = {0,0.001,0.005};
  static float [] NORMAL_SPEED = {0.8,1.0,1.5};
  static int [] NORMAL_BOUNTY = {5,4,3};
  static float [] NORMAL_HEALTH_GROWTH = {200,300,400};
  static float [] NORMAL_ARMOR_GROWTH = {30,40,60};
  static float [] NORMAL_SPEED_GROWTH = {0.02,0.03,0.04};
  static float [] NORMAL_BOUNTY_GROWTH = {0.25,0.15,0.15};
  
  //Fast
  
  static color FAST_COLOR = #FAA112;
  static float FAST_SIZE = 20;
  static float [] FAST_MAX_HEALTH = {75,100,150};
  static float [] FAST_POWER = {5,5,30};
  static float [] FAST_MAX_ARMOR = {15,25,100};
  static float [] FAST_ARMOR_REGEN_RATE = {0,0.002,0.1};
  static float [] FAST_SPEED = {1.2,1.5,1.8};
  static int [] FAST_BOUNTY = {4,3,3};
  static float [] FAST_HEALTH_GROWTH = {120,180,270};
  static float [] FAST_ARMOR_GROWTH = {20,40,80};
  static float [] FAST_SPEED_GROWTH = {0.03,0.04,0.06};
  static float [] FAST_BOUNTY_GROWTH = {0.2,0.12,0.12};
  
  //Tank
  
  static color TANK_COLOR = #110F52;
  static float TANK_SIZE = 50;
  static float [] TANK_MAX_HEALTH = {1800,2000,3000};
  static float [] TANK_POWER = {15,20,50};
  static float [] TANK_MAX_ARMOR = {200,600,1200};
  static float [] TANK_ARMOR_REGEN_RATE = {0,0.0005,0.003};
  static float [] TANK_SPEED = {0.5,0.6,0.9};
  static int [] TANK_BOUNTY = {80,60,40};
  static float [] TANK_HEALTH_GROWTH = {3000,4000,5000};
  static float [] TANK_ARMOR_GROWTH = {350,500,750};
  static float [] TANK_SPEED_GROWTH = {0.02,0.02,0.03};
  static float [] TANK_BOUNTY_GROWTH = {2,2,1};
  
  //Support
  
  static color SUPPORT_COLOR = #2C75F0;
  static float SUPPORT_SIZE = 30;
  static float [] SUPPORT_MAX_HEALTH = {100,150,350};
  static float [] SUPPORT_POWER = {10,15,35};
  static float [] SUPPORT_MAX_ARMOR = {10,20,100};
  static float [] SUPPORT_ARMOR_REGEN_RATE = {0,0,0.002};
  static float [] SUPPORT_SPEED = {1,1.2,1.5};
  static int [] SUPPORT_BOUNTY = {10,5,5};
  static float [] SUPPORT_BUFF_RANGE = {100,175,250};
  static float [] SUPPORT_HEALTH_GROWTH = {200,300,400};
  static float [] SUPPORT_ARMOR_GROWTH = {3,5,15};
  static float [] SUPPORT_SPEED_GROWTH = {0.02,0.03,0.05};
  static float [] SUPPORT_BOUNTY_GROWTH = {1,0.5,0.5};

}