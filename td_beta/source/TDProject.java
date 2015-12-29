import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class TDProject extends PApplet {

static final int CANNON = 0;
static final int LASER = 1;
static final int AURA = 2;

static final int BUFF = 0;
static final int DEBUFF = 1;

static final int ENEMY_NORMAL = 1;
static final int ENEMY_FAST = 2;
static final int ENEMY_TANK = 3;
static final int ENEMY_SUPPORT = 4;

static final int UI_BUILD = 0;
static final int UI_PLACEMENT = 1;
static final int UI_UPGRADE = 2;
static final int UI_SKILL = 3;
static final int UI_MAINMENU = 4;
static final int UI_GAMEOVER = 5;

static final int CLICKABLE = 0;
static final int UNCLICKABLE = 1;
static final int ENABLED = 2;


final int gameplayScreenX = 1200; //The width of screen for gameplay
final int gameplayScreenY = 600; //The height of screen for gameplay
final int gridSize = 60; //The size of grids in the gameplay screen
final int gridCount = (gameplayScreenX / gridSize) * (gameplayScreenY / gridSize); //Total grid count
final int maxEnemyCount = 100; //The limit of the amount of enemies; for indexing during initialization
final int maxBulletCount = 10; //The limit of the amount of projectiles; for indexing during initialization
final int screenOffsetX = 0; //The horizonal offset of gameplay screen
final int screenOffsetY = 50; //The vertical offset of gameplay screen
PFont [] font = new PFont [5];
PImage targetArrow;
boolean [] routeGrid = new boolean[gridCount]; //Creates an array to store whether each grid is on the route
boolean skillMenuState;
int UIMode;
int gold;
int buildMode;
int buildCost;
int mouseOnGrid; //Store the information of the grid where the mouse places on
int lastGrid; // The last grid of route given by mapData
int sentEnemy = 0; // The amount of enemies who are already sent out
int currentWaveMaxEnemy = 0; // The total amount of enemies in this wave
int currentWave = 0; // The number of the current wave
int timer = 0; // Timer for the interval between each enemy spawn
int gapTimer = 0; // Timer for the interval between each wave
int gap = 60; // The interval between each wave
int targetTurretID = -1;
float baseHealth;
float baseMaxHealth = 100;
float startpointX, startpointY; //Where the enemies spawn; given by mapData

mapData currentMap;
waveData wave;
Turret [] turret = new Turret [gridCount];
Enemy [] enemy = new Enemy [maxEnemyCount];
Projectile [][] proj = new Projectile [gridCount][maxBulletCount]; //Use two-dimension array to store projectiles and their correspondant turrets
Button [] upgrade = new Button [3];
Button [] build = new Button [3];
Button [] skillPurchase = new Button [15];
Button sell, skillMenu;

public void setup(){
  frameRate(60);
  
  targetArrow = loadImage("img/green_arrow.png");
  font[1] = createFont("ACaslonPro-Regular", 50);
  font[2] = createFont("ACaslonPro-Regular", 28);
  font[3] = createFont("DilleniaUPC Bold", 26);
  gameInit(); //Call the method gameInit() to initialize the game
  imageMode(CENTER);
}

public void draw(){
  background(0);
  fill(0);
  pushMatrix();
  translate(screenOffsetX,screenOffsetY);
  rect(0,0,gameplayScreenX,gameplayScreenY);
  stroke(255);
  
  // Draw grids
  drawGrids();
  
  //Enemy's actions
  
  for(int i = 0; i < sentEnemy; i++){ // Command only enemies who are already sent out
    if(enemy[i].state){ // Check if the enemy is alive or not
      enemy[i].show();
      enemy[i].move();
    }
  }
  
  //Turret's actions
  
  for(int i = 0; i < gridCount; i++){ // Scan through each grid because the data of turrets is bound to it
    if(turret[i].builtState){ // Check if there is a turret on the grid
      turret[i].show();
      turret[i].detect();
    }
  }
  timer ++;
  if(timer==45 && sentEnemy < currentWaveMaxEnemy){ // When the timer is up and there are still enemies not sent out yet in the current wave
    timer = 0; // Reset the timer
    sentEnemy ++; // Add the amount of enemies sent
  }
  if(sentEnemy == currentWaveMaxEnemy){ // Check if there's no more enemy not sent out in the current wave
    if(!enemyCheck()){ // Call the boolean method enemyCheck() to check if all enemies in the current wave are dead
      waveEnd(); // Call the method waveEnd
    }
  }
  if(targetTurretID!=-1){
    rangeIndicate();
  }
  
  popMatrix();
  showUI();
}

// AREA CHECKING METHODS

public boolean mouseCheck(int x, int y, int w, int h){ // Check if the mouse is in the given area data
  return(mouseX > x && mouseX < x + w && mouseY > y&& mouseY < y + h);
}

public boolean mouseCheck(){ // Check if the mouse is in the given area data
  return(mouseX > screenOffsetX && mouseX < screenOffsetX + gameplayScreenX && mouseY > screenOffsetY&& mouseY < screenOffsetY + gameplayScreenY);
}

public boolean rectHitCheck(float ax, float ay, float aw, float ah, float bx, float by)
{
    boolean collisionX = (ax + aw >= bx) && (bx >= ax);
    boolean collisionY = (ay + ah >= by) && (by >= ay);
    return collisionX && collisionY;
}

public boolean enemyCheck(){ // Check if every enemy in the wave is dead
  for(int i = 0; i < sentEnemy; i++){
    if(enemy[i].state){
      return true;
    }
  }
  return false;
}

// DAMAGE CALCULATING METHODS

public boolean checkCritTrigger(int turretID, float critChance){
  critChance += skillCritChanceAddition(turretID);
  if(random(0,1) <= critChance){
    return true;
  }
  return false;
}

public float calDamage(int turretID, int enemyID, float inputDamage, float critAmp){
  float damage;
  // Result Damage = (Input Damage + Skill Additional Damage) * (Crit Amplification * Skill Crit Multiplier) * Skill Multiplier * Armor Multiplier
  damage = inputDamage + skillDamageAddition(turretID,enemyID);
  critAmp *= skillCritMultiplier(turretID, critAmp);
  damage *= critAmp;
  damage *= skillDamageMultiplier(turretID,enemyID);
  damage *= armorMultiplier(turretID, enemy[enemyID].armor);
  //println(enemy[enemyID].armor + "/" + damageMultiplier + "/" + damage);
  return damage;
}

public float calDamage(int turretID, int enemyID, float inputDamage){
  float damage;
  // (NO CRIT) Result Damage = (Input Damage + Skill Additional Damage) * Skill Multiplier * Armor Multiplier
  damage = inputDamage + skillDamageAddition(turretID,enemyID);
  damage *= skillDamageMultiplier(turretID,enemyID);
  damage *= armorMultiplier(turretID, enemy[enemyID].armor);
  //println(enemy[enemyID].armor + "/" + damageMultiplier + "/" + damage);
  return damage;
}

public float skillCritMultiplier(int turretID, float critAmp){
  float multiplier = 1;
  switch(turret[turretID].turretType){
    case CANNON:
      if(turret[turretID].skillState[0][3]){
        multiplier += TurretSkillData.CANNON_SKILL_A_T4_BONUS_CRITICAL_DAMAGE_MULTIPLIER;
      }
      break;
      
    case LASER:
      if(turret[turretID].skillState[0][3]){
        multiplier += TurretSkillData.LASER_SKILL_A_T4_BONUS_CRITICAL_DAMAGE_MULTIPLIER;
      }
      break;
  }
  return multiplier;
}

public float skillCritChanceAddition(int turretID){
  float addition = 0;
  switch(turret[turretID].turretType){
    case CANNON:
      if(turret[turretID].skillState[1][3]){
        addition += TurretSkillData.CANNON_SKILL_B_T4_BONUS_CRIT_CHANCE;
      }
      if(turret[turretID].skillState[1][4]){
        addition += map( (1-(baseHealth/baseMaxHealth)) , 0, 1, TurretSkillData.CANNON_SKILL_B_T5_MIN_BONUS_CRIT_CHANCE, TurretSkillData.CANNON_SKILL_B_T5_MAX_BONUS_CRIT_CHANCE);
      }
      break;
  }
  return addition;
}

public float skillDamageAddition(int turretID, int enemyID){
  float damageAddition = 0;
  switch(turret[turretID].turretType){
    case CANNON:
      if(turret[turretID].skillState[0][4]){
        damageAddition += enemy[enemyID].health * TurretSkillData.CANNON_SKILL_A_T5_HP_PERCENTAGE;
      }
      break;
  }
  return damageAddition;
}

public float skillDamageMultiplier(int turretID, int enemyID){
  float damageMultiplier = 1;
  switch(turret[turretID].turretType){
    case CANNON:
      if(turret[turretID].skillState[0][0]){
        damageMultiplier += TurretSkillData.CANNON_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER;
      }
      if(turret[turretID].skillState[0][1]){
        damageMultiplier += (1-(enemy[enemyID].health/enemy[enemyID].maxHealth)) * TurretSkillData.CANNON_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER;
      }
      break;
      
    case LASER:
      if(turret[turretID].skillState[0][0]){
        damageMultiplier += TurretSkillData.LASER_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER;
      }
      if(turret[turretID].skillState[0][1]){
        damageMultiplier += max(0,map(turret[turretID].laserHeat, TurretSkillData.LASER_SKILL_A_T2_MIN_HEAT_THRESHOLD, TurretSkillData.LASER_SKILL_A_T2_MAX_DAMAGE_HEAT_CAP, 0, TurretSkillData.LASER_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER));
      }
      break;
  }
  return damageMultiplier;
}
  
public float armorMultiplier(int turretID, float inputArmor){
  if(inputArmor>=0){
    inputArmor = armorBypass(turretID, inputArmor);
    return constrain((1 - pow(inputArmor/4,2)/ (600+inputArmor)),0.1f,1);
  }else{
    return (1 - inputArmor/100);
  }
  //return (1 - 0.06 * inputArmor / ( 1 + ( 0.06 * abs(inputArmor))));
}

public float armorBypass(int turretID, float inputArmor){
  switch(turret[turretID].turretType){
    case LASER:
      if(turret[turretID].skillState[0][2]){
        inputArmor *= TurretSkillData.LASER_SKILL_A_T3_ARMOR_BYPASS_MULTIPLIER;
      }
      break;
  }
  return inputArmor;
}

// UI METHODS

public void showUI(){
  UIMode = UIModeChecker();
  baseHealthUI();
  goldUI();
  fpsUI();
  waveUI();
  timeUI();
  switch(UIMode){
    case UI_BUILD:
      turretBuildUI();
      break;
    case UI_PLACEMENT:
      turretPlacementUI();
      break;
    case UI_UPGRADE:
      turretUpgradeUI();
      break;
    case UI_SKILL:
      turretSkillUI();
      break;
  }
}

public int UIModeChecker(){
  if(targetTurretID == -1 && buildMode == -1){
    return UI_BUILD;
  }else if(targetTurretID == -1 && buildMode != -1){
    return UI_PLACEMENT;
  }else if(targetTurretID != -1 && !skillMenuState){
    return UI_UPGRADE;
  }else if(skillMenuState){
    return UI_SKILL;
  }
  return 0;
}

public void goldUI(){
  textFont(font[1]);
  fill(0xffF7E005);
  text("Gold: " + gold, 60, 700);
}

public void fpsUI(){
  textFont(font[2]);
  fill(255);
  int fps = floor(frameCount*1000/millis());
  text("FPS: " + fps, 5, 30);
}

public void waveUI(){
  textFont(font[2]);
  fill(255);
  text("Current Wave: " + currentWave, 125, 30);
}

public void timeUI(){
  textFont(font[2]);
  fill(255);
  int s = floor(millis()/1000)%60;
  int s1 = s%10;
  int s2 = floor(s/10);
  int m = floor(millis()/60000);
  text("Elapsed Time: " + m + ":" + s2 + s1, 450, 30);
}

public void baseHealthUI(){
  noStroke();
  fill(255,0,0);
  rect(1201,50,25,600);
  fill(0,255,0);
  rect(1201,50,25,600*(constrain(baseHealth/100,0,1)));
}

public void targetIndicateUI(){
  float m = 10 + 20*sin(frameCount/(PI*4));
  image(targetArrow, turret[targetTurretID].x, turret[targetTurretID].y - m, 40, 50);
}

public void turretBuildUI(){
  if(gold >= TurretLevelData.cannonBuildCost){
    build[0] = new Button (400,660,200,40,"Build Cannon");
  }else{
    build[0] = new Button (400,660,200,40,"Build Cannon",UNCLICKABLE);
  }
  textFont(font[2]);
  fill(255);
  text("Build Cost: " + TurretLevelData.cannonBuildCost, 620, 690);
  build[0].show();
  if(gold >= TurretLevelData.laserBuildCost){
    build[1] = new Button (400,700,200,40,"Build Laser");
  }else{
    build[1] = new Button (400,700,200,40,"Build Laser",UNCLICKABLE);
  }
  textFont(font[2]);
  text("Build Cost: " + TurretLevelData.laserBuildCost, 620, 730);
  build[1].show();
  if(gold >= TurretLevelData.auraBuildCost){
    build[2] = new Button (400,740,200,40,"Build Aura");
  }else{
    build[2] = new Button (400,740,200,40,"Build Aura",UNCLICKABLE);
  }
  textFont(font[2]);
  text("Build Cost: " + TurretLevelData.auraBuildCost, 620, 770);
  build[2].show();
}

public void turretPlacementUI(){
  pushStyle();
  textFont(font[1]);
  colorMode(HSB, 360,100,100);
  fill(frameCount%360,100,100);
  text("Place a turret by mouse", 620, 690);
  if(!routeGrid[mouseOnGrid] && !turret[mouseOnGrid].builtState && mouseCheck()) image(targetArrow, turret[mouseOnGrid].x + screenOffsetX, turret[mouseOnGrid].y + screenOffsetY - 30, 40, 50);
  popStyle();
}

public void turretUpgradeUI(){
  targetIndicateUI();
  textFont(font[1]);
  fill(255);
  text(turret[targetTurretID].turretName, 350, 700);
  
  //Laser UI
  
  /** 
  if(turret[targetTurretID].turretType == LASER){
    laserHeatUI(410,730,265,30);
  }
  **/
  
  textFont(font[2]);
  skillMenu = new Button(60,720,220,40,"Skill Menu");
  skillMenu.show();
  sell = new Button(1100,660,100,40,"Sell");
  sell.show();
  text("Price: " + turret[targetTurretID].sellPrice, 1100, 730);
  text("Level A: " + turret[targetTurretID].levelA, 700, 690);
  text("Level B: " + turret[targetTurretID].levelB, 700, 730);
  text("Level C: " + turret[targetTurretID].levelC, 700, 770);
  if(turret[targetTurretID].levelA < TurretLevelData.maxLevel){
    if(gold >= turret[targetTurretID].levelAUpgradeCost){
      upgrade[0] = new Button(840,660,120,40,"Upgrade");
    }else{
      upgrade[0] = new Button(840,660,120,40,"Upgrade",UNCLICKABLE);
    }
    upgrade[0].show();
    text("Cost: " + turret[targetTurretID].levelAUpgradeCost, 965, 690);
  }else{
    upgrade[0] = null;
  }
  if(turret[targetTurretID].levelB < TurretLevelData.maxLevel){
    if(gold >= turret[targetTurretID].levelBUpgradeCost){
      upgrade[1] = new Button(840,700,120,40,"Upgrade");
    }else{
      upgrade[1] = new Button(840,700,120,40,"Upgrade",UNCLICKABLE);
    }
    upgrade[1].show();
    text("Cost: " + turret[targetTurretID].levelBUpgradeCost, 965, 730);
  }else{
    upgrade[2] = null;
  }
  
  if(turret[targetTurretID].levelC < TurretLevelData.maxLevel){
    if(gold >= turret[targetTurretID].levelCUpgradeCost){
      upgrade[2] = new Button(840,740,120,40,"Upgrade");
    }else{
      upgrade[2] = new Button(840,740,120,40,"Upgrade",UNCLICKABLE);
    }
    upgrade[2].show();
    text("Cost: " + turret[targetTurretID].levelCUpgradeCost, 965, 770);
  }else{
    upgrade[2] = null;
  }
}

public void turretSkillUI(){
  targetIndicateUI();
  textFont(font[2]);
  skillMenu = new Button(60,720,220,40,"Upgrade Menu");
  skillMenu.show();
  text("A" , 300, 690);
  text("B" , 300, 735);
  text("C" , 300, 780);
  for(int i = 0; i < 5; i++){
    for(int j = 0; j < 3; j++){
      if(turret[targetTurretID].skillState[j][i]){
        skillPurchase[i+j*5] = new Button(340+170*i,660+45*j,168,36,turret[targetTurretID].skillName[j][i],font[3], ENABLED);
      }else if(gold < turret[targetTurretID].skillCost[j][i]){
        skillPurchase[i+j*5] = new Button(340+170*i,660+45*j,168,36,turret[targetTurretID].skillName[j][i],font[3], UNCLICKABLE);
      }else if(j == 0 && turret[targetTurretID].levelA < TurretSkillData.MIN_LEVEL[i]){
        skillPurchase[i+j*5] = new Button(340+170*i,660+45*j,168,36,turret[targetTurretID].skillName[j][i],font[3], UNCLICKABLE);
      }else if(j == 1 && turret[targetTurretID].levelB < TurretSkillData.MIN_LEVEL[i]){
        skillPurchase[i+j*5] = new Button(340+170*i,660+45*j,168,36,turret[targetTurretID].skillName[j][i],font[3], UNCLICKABLE);
      }else if(j == 2 && turret[targetTurretID].levelC < TurretSkillData.MIN_LEVEL[i]){
        skillPurchase[i+j*5] = new Button(340+170*i,660+45*j,168,36,turret[targetTurretID].skillName[j][i],font[3], UNCLICKABLE);
      }else{
        skillPurchase[i+j*5] = new Button(340+170*i,660+45*j,168,36,turret[targetTurretID].skillName[j][i],font[3]);
      }
      skillPurchase[i+j*5].show();
      if(mouseCheck(skillPurchase[i+j*5].x,skillPurchase[i+j*5].y,skillPurchase[i+j*5].w,skillPurchase[i+j*5].h)){
        Button skillDescBox = new Button(skillPurchase[0].x,skillPurchase[0].y-40,PApplet.parseInt(textWidth(turret[targetTurretID].skillDescription[j][i])*0.7f)+35,40,turret[targetTurretID].skillDescription[j][i],font[3]);
        skillDescBox.show();
        if(turret[targetTurretID].skillState[j][i]){
          Button skillCostBox = new Button(skillPurchase[0].x,skillPurchase[0].y-70,100,30,"BOUGHT",font[3],UNCLICKABLE);
          skillCostBox.show();
        }else{
          Button skillCostBox = new Button(skillPurchase[0].x,skillPurchase[0].y-70,100,30,"Cost: " + floor(turret[targetTurretID].skillCost[j][i]),font[3]);
          skillCostBox.show();
        }
        if(j == 0){
          Button skillReqBox = new Button(skillPurchase[0].x+100,skillPurchase[0].y-70,250,30,"Level A Requirement: " + floor(TurretSkillData.MIN_LEVEL[i]),font[3]);
          skillReqBox.show();
        }else if(j == 1){
          Button skillReqBox = new Button(skillPurchase[0].x+100,skillPurchase[0].y-70,250,30,"Level B Requirement: " + floor(TurretSkillData.MIN_LEVEL[i]),font[3]);
          skillReqBox.show();
        }else if(j == 2){
          Button skillReqBox = new Button(skillPurchase[0].x+100,skillPurchase[0].y-70,250,30,"Level C Requirement: " + floor(TurretSkillData.MIN_LEVEL[i]),font[3]);
          skillReqBox.show();
        }
      }
    }
  }
  turretSkillLevelIndicateUI();
}

public void turretSkillLevelIndicateUI(){
  pushStyle();
  colorMode(HSB, 360, 100, 100);
  strokeWeight(3);
  stroke(frameCount%360,100,100);
  fill(frameCount%360,100,100);
  rect(344,695,map(turret[targetTurretID].levelA,0,TurretLevelData.maxLevel,0,840),10);
  rect(344,740,map(turret[targetTurretID].levelB,0,TurretLevelData.maxLevel,0,840),10);
  rect(344,785,map(turret[targetTurretID].levelC,0,TurretLevelData.maxLevel,0,840),10);
  popStyle();
}

public void laserHeatUI(float x, float y, float w, float h){
  pushStyle();
  fill(0,255,0);
  rect(x,y,w,h);
  fill(255,0,0);
  if(turret[targetTurretID].cooldown){
    rect(x,y,floor(turret[targetTurretID].laserHeat/turret[targetTurretID].laserOverheatThreshold*w),h);
  }else{
    rect(x,y,floor(turret[targetTurretID].cooldownTime/turret[targetTurretID].attackRate*w),h);
  }
  popStyle();
}

public void drawGrids(){
  noStroke();
  for(int i = 0; i < 1200/gridSize; i++){
    for(int j = 0; j < 600/gridSize; j++){
      int gridX = i*gridSize;
      int gridY = j*gridSize;
      pushStyle();
      if(mouseCheck(gridX+screenOffsetX,gridY+screenOffsetY,gridSize,gridSize)){ //Check if the mouse is in the grid
        // The grid where the mouse places on is white
        fill(255);
        mouseOnGrid = i*10+j;
      }else if(routeGrid[i*10+j]){
        // The grids which are route grids are green; if it's the last grid, red
        if(i*10+j==lastGrid){
          colorMode(HSB,360,100,100);
          fill(frameCount%360,100,80);
        }else{
          fill(0,255,0);
        }
      }else{
        // The rest of the grids are not filled with color
        noFill();
      }
      rect(gridX,gridY,gridSize,gridSize);
      popStyle();
    }
  }
}

public void rangeIndicate(){
  stroke(255);
  noFill();
  ellipse(turret[targetTurretID].x, turret[targetTurretID].y,turret[targetTurretID].attackRange*2,turret[targetTurretID].attackRange*2);
}

//

public void gameInit(){ // Game initialization
  UIMode = UI_BUILD;
  skillMenuState = false;
  gold = 100;
  baseHealth = baseMaxHealth;
  buildMode = -1;
  targetTurretID = -1;
  currentMap = new mapData(1); // Load the first data in mapData
  wave = new waveData(); // Initialize the data for waves 
  currentWave = 1; // Set the number of current wave to 1
  wave.load(1); //Load the first wave
  for(int i = 0; i < gridCount; i++){ //Initialize each turrets
    turret[i] = new Turret(i);
    turret[i].builtState = false;
    for(int j = 0; j < maxBulletCount; j++){ //Initialize each projectiles
      proj[i][j] = new Projectile();
    }
  }
}

public void waveEnd(){ 
  gapTimer ++; //gapTimer starts counting
  if(gapTimer == gap){ //Check if gapTimer reaches the assigned interval
    gapTimer = 0; // Reset gapTimer
    sentEnemy = 0; // Reset the amounts of enemies sent
    timer = 0; // reset the enemy spawn timer
    waveEndGoldBounty(currentWave);
    currentWave ++; // Change the wave count to the next
    wave.load(currentWave); // Load the data of the incoming wave
  }
}

public void waveEndGoldBounty(int w){
  gold += ceil(w*baseHealth/baseMaxHealth);
}

// ENEMY GROWTH METHODS

public float enemyMaxHealthGrowth(int enemyType){
  float mult = pow(0.3f*(currentWave-1),2);
  switch(enemyType){
    case ENEMY_NORMAL:
      return 250*mult;
    case ENEMY_FAST:
      return 60*mult;
    case ENEMY_TANK:
      return 1000*mult;
    case ENEMY_SUPPORT:
      return 50*mult;
  }
  return 0;
}

public float enemyArmorGrowth(int enemyType){
  switch(enemyType){
    case ENEMY_NORMAL:
      return 1*(currentWave-1);
    case ENEMY_FAST:
      return 1*(currentWave-1);
    case ENEMY_TANK:
      return 1*(currentWave-1);
    case ENEMY_SUPPORT:
      return 2*(currentWave-1);
  }
  return 0;
}

public float enemySpeedGrowth(int enemyType){
  switch(enemyType){
    case ENEMY_NORMAL:
      return 0.03f*(currentWave-1);
    case ENEMY_FAST:
      return 0.04f*(currentWave-1);
    case ENEMY_TANK:
      return 0.02f*(currentWave-1);
    case ENEMY_SUPPORT:
      return 0.05f*(currentWave-1);
  }
  return 0;
}

public int enemyBountyGrowth(int enemyType){
  switch(enemyType){
    case ENEMY_NORMAL:
      return floor(0.6f*(currentWave-1));
    case ENEMY_FAST:
      return floor(0.3f*(currentWave-1));
    case ENEMY_TANK:
      return floor(5*(currentWave-1));
    case ENEMY_SUPPORT:
      return floor(2*(currentWave-1));
  }
  return 0;
}

// UTILITY METHODS

public float rateConvertFrames(float x){
  return 60/x;
}

public float secondConvertFrames(float x){
  return x*60;
}

public void debuffIndicate(float x, float y, float r, float str){
  noStroke();
  fill(255,0,0,str);
  ellipse(x,y,r,r);
}

public void debuffIndicate(float x, float y, float r, float str, float time, float maxTime){
  noStroke();
  float a = time/maxTime*TWO_PI;
  float aStart = -PI/2;
  fill(255,0,0,str);
  arc(x,y,r,r,aStart,a+aStart,PIE);
}

//INPUT METHODS

public void keyPressed(){
  gold += 100;
  for(int i = 0; i < sentEnemy; i++){
    enemy[i].speed *= 0.5f;
  }
  //noLoop();
  //if(mouseX>mouseY) loop();
}

public void mouseReleased(){
  switch(UIMode){
    case UI_BUILD:
      if(mouseCheckOnTurret()){
        targetTurretID = mouseOnGrid;
        buildMode = -1;
        break;
      }
      if(mouseCheck(build[0].x,build[0].y,build[0].w,build[0].h)){
        if(gold >= TurretLevelData.cannonBuildCost){
          buildMode = 0;
          buildCost = TurretLevelData.cannonBuildCost;
        }else{
          println("Not Enough Gold!");
        }
      }else if(mouseCheck(build[1].x,build[1].y,build[1].w,build[1].h)){
        if(gold >= TurretLevelData.laserBuildCost){
          buildMode = 1;
          buildCost = TurretLevelData.laserBuildCost;
        }else{
          println("Not Enough Gold!");
        }
      }else if(mouseCheck(build[2].x,build[2].y,build[2].w,build[2].h)){
        if(gold >= TurretLevelData.auraBuildCost){
          buildMode = 2;
          buildCost = TurretLevelData.auraBuildCost;
        }else{
          println("Not Enough Gold!");
        }
      }else{
        mouseActionOnCancelSelect();
      }
      break;
      
    case UI_PLACEMENT:
      if(mouseCheckOnTurret()){
        targetTurretID = mouseOnGrid;
        buildMode = -1;
        break;
      }
      if(!routeGrid[mouseOnGrid] && mouseCheck()){ // Check if the mouse is in the screen and the grid it's on is not a route grid
        turret[mouseOnGrid].builtState = true; // Build a turret
        turret[mouseOnGrid].turretType = buildMode;
        turret[mouseOnGrid].turretInit(buildMode);
        targetTurretID = mouseOnGrid;
        buildMode = -1;
        gold -= buildCost;
      }else{
        buildMode = -1;
      }
      break;
      
    case UI_UPGRADE:
      if(mouseCheckOnTurret()){
        targetTurretID = mouseOnGrid;
        buildMode = -1;
        break;
      }
      if(upgrade[0]!=null && mouseCheck(upgrade[0].x,upgrade[0].y,upgrade[0].w,upgrade[0].h) && turret[targetTurretID].levelA < TurretLevelData.maxLevel){
        if(gold >= turret[targetTurretID].levelAUpgradeCost){
          gold -= turret[targetTurretID].levelAUpgradeCost;
          turret[targetTurretID].totalCost += turret[targetTurretID].levelAUpgradeCost;
          turret[targetTurretID].levelA ++;
          switch(turret[targetTurretID].turretType){
            case CANNON:
              turret[targetTurretID].levelAUpgradeCost = TurretLevelData.cannonCostA[turret[targetTurretID].levelA];
              break;
            case LASER:
              turret[targetTurretID].levelAUpgradeCost = TurretLevelData.laserCostA[turret[targetTurretID].levelA];
              break;
            case AURA:
              turret[targetTurretID].levelAUpgradeCost = TurretLevelData.auraCostA[turret[targetTurretID].levelA];
              break;
          }
        }else{
          println("Not Enough Gold!");
        }
      }else if(upgrade[1]!=null && mouseCheck(upgrade[1].x,upgrade[1].y,upgrade[1].w,upgrade[1].h) && turret[targetTurretID].levelB < TurretLevelData.maxLevel){
        if(gold >= turret[targetTurretID].levelBUpgradeCost){
          gold -= turret[targetTurretID].levelBUpgradeCost;
          turret[targetTurretID].totalCost += turret[targetTurretID].levelBUpgradeCost;
          turret[targetTurretID].levelB ++;
          switch(turret[targetTurretID].turretType){
            case CANNON:
              turret[targetTurretID].levelBUpgradeCost = TurretLevelData.cannonCostB[turret[targetTurretID].levelB];
              break;
            case LASER:
              turret[targetTurretID].levelBUpgradeCost = TurretLevelData.laserCostB[turret[targetTurretID].levelB];
              break;
            case AURA:
              turret[targetTurretID].levelBUpgradeCost = TurretLevelData.auraCostB[turret[targetTurretID].levelB];
              break;
          }
        }else{
          println("Not Enough Gold!");
        }
      }else if(upgrade[2]!=null && mouseCheck(upgrade[2].x,upgrade[2].y,upgrade[2].w,upgrade[2].h) && turret[targetTurretID].levelC < TurretLevelData.maxLevel){
        if(gold >= turret[targetTurretID].levelCUpgradeCost){
          gold -= turret[targetTurretID].levelCUpgradeCost;
          turret[targetTurretID].totalCost += turret[targetTurretID].levelCUpgradeCost;
          turret[targetTurretID].levelC ++;
          switch(turret[targetTurretID].turretType){
            case CANNON:
              turret[targetTurretID].levelCUpgradeCost = TurretLevelData.cannonCostC[turret[targetTurretID].levelC];
              break;
            case LASER:
              turret[targetTurretID].levelCUpgradeCost = TurretLevelData.laserCostC[turret[targetTurretID].levelC];
              break;
            case AURA:
              turret[targetTurretID].levelCUpgradeCost = TurretLevelData.auraCostC[turret[targetTurretID].levelC];
              break;
          }
        }else{
          println("Not Enough Gold!");
        }
      }else if(mouseCheck(sell.x,sell.y,sell.w,sell.h)){
        gold += turret[targetTurretID].sellPrice;
        turret[targetTurretID].builtState = false;
        turret[targetTurretID].turretInit(0);
        targetTurretID = -1;
      }else if(mouseCheck(skillMenu.x,skillMenu.y,skillMenu.w,skillMenu.h)){
        skillMenuState = true;
      }else{
        mouseActionOnCancelSelect();
      }
      break;
    
    case UI_SKILL:
      if(mouseCheckOnTurret()){
        targetTurretID = mouseOnGrid;
        skillMenuState = false;
        buildMode = -1;
        break;
      }
      if(mouseCheck(skillMenu.x,skillMenu.y,skillMenu.w,skillMenu.h)){
        skillMenuState = false;
      }else{
        boolean clickedOnButtons = false;
        for(int i = 0; i < 3; i++){
          for(int j = 0; j < 5; j++){
            if(mouseCheck(skillPurchase[i*5+j].x,skillPurchase[i*5+j].y,skillPurchase[i*5+j].w,skillPurchase[i*5+j].h)){
              if(skillPurchase[i*5+j].showState == CLICKABLE){
                turret[targetTurretID].totalCost += turret[targetTurretID].skillCost[i][j];
                gold -= turret[targetTurretID].skillCost[i][j];
                turret[targetTurretID].skillState[i][j] = true;
              }
              clickedOnButtons = true;
              break;
            }
          }
        }
        if(!clickedOnButtons){
          mouseActionOnCancelSelect();
          skillMenuState = false;
        }
      }
      break;
  }
}

public boolean mouseCheckOnTurret(){
  if(turret[mouseOnGrid].builtState && mouseCheck()){ 
    return true;
  }
  return false;
}

public void mouseActionOnCancelSelect(){
  targetTurretID = -1;
}
static class BuffData{
  static int BUFF_COUNT = 25;
  
  static String [] BUFF_NAME = {
    "Acid Infusion",
    "Cold Snap",
    "Ionic Shell",
    "Crippled",
    "DNA Inflation",
    "Imflammation",
    "Breached",
    "Volatile Compound",
  };
  
  static int [] BUFF_TYPE = {
    DEBUFF,
    DEBUFF,
    DEBUFF,
    DEBUFF,
    DEBUFF,
    DEBUFF,
    DEBUFF,
    DEBUFF,
  };
}
class Button{
  int w;
  int h;
  int x;
  int y;
  PFont fontType;
  int showState;
  String word;
  
  public void show(){
    pushStyle();
    textAlign(CENTER, CENTER);
    if(showState == ENABLED){
      fill(0xffF7CF2A);
    }else if(showState == UNCLICKABLE){
      fill(130);
    }else if(mouseCheck(x,y,w,h)){
      fill(0,255,0);
    }else{
      fill(255);
    }
    strokeWeight(5);
    stroke(0);
    rect(x,y,w,h);
    fill(0);
    noStroke();
    textFont(fontType);
    text(word,x+w/2,y+h/2);
    popStyle();
  }
  
  
  Button(int x, int y, int w, int h, String word){
    this.w = w;
    this.h = h;
    this.x = x;
    this.y = y;
    this.word = word;
    this.fontType = font[2];
    showState = 0;
  }
  
  Button(int x, int y, int w, int h, String word, PFont font){
    this.w = w;
    this.h = h;
    this.x = x;
    this.y = y;
    this.word = word;
    this.fontType = font;
    showState = 0;
  }
  
  Button(int x, int y, int w, int h, String word, PFont font, int clickstate){
    this.w = w;
    this.h = h;
    this.x = x;
    this.y = y;
    this.word = word;
    this.fontType = font;
    showState = clickstate;
  }
  
  Button(int x, int y, int w, int h, String word, int clickstate){
    this.w = w;
    this.h = h;
    this.x = x;
    this.y = y;
    this.word = word;
    this.fontType = font[2];
    showState = clickstate;
  }
  
}
class Enemy{
  final int MOVE_U = 1;
  final int MOVE_D = 2;
  final int MOVE_L = 3;
  final int MOVE_R = 4;
  
  boolean state;
  boolean drawHurt;
  boolean buffState[] = new boolean [BuffData.BUFF_COUNT];
  float buffTimer[] = new float [BuffData.BUFF_COUNT];
  float buffData1[] = new float [BuffData.BUFF_COUNT];
  float buffData2[] = new float [BuffData.BUFF_COUNT];
  float hurtShowDmg;
  float size;
  float maxHealth;
  float health;
  float armor;
  float power;
  float x;
  float y;
  float speed;
  int ID;
  int type;
  int enemyColor;
  int OnGrid, OnGridX, OnGridY;
  int moveDir = MOVE_R;
  int bounty;
  
  public void show(){
    endCheck();
    healthCheck();
    checkBuffValidity();
    loadStat();
    applyBuffEffect();
    stroke(0);
    ellipseMode(CENTER);
    fill(enemyColor);
    ellipse(x,y,size,size);
    if(drawHurt){
      pushStyle();
      textAlign(CENTER,CENTER);
      fill(255);
      textFont(font[3]);
      text(hurtShowDmg,x,y-40);
      popStyle();
      fill(255,255,255,200);
      ellipse(x,y,size,size);
      drawHurt = false;
    }
    healthBar();
  }
  
  public void healthBar(){
    pushStyle();
    textAlign(CENTER,CENTER);
    fill(255);
    textFont(font[3]);
    text(floor(health),x,y);
    popStyle();
    stroke(0);
    fill(255,0,0);
    rect(x-20,y-20,40,4);
    fill(0,255,0);
    rect(x-20,y-20,40*(health/maxHealth),4);
  }
  
  public void move(){
    switch(moveDir){
      case MOVE_U:
        y -= speed;
        break;
      case MOVE_D:
        y += speed;
        break;
      case MOVE_L:
        x -= speed;
        break;
      case MOVE_R:
        x += speed;
        break;
    }
    OnGrid = floor(x/gridSize) * 10 + floor(y/gridSize);
    OnGridX = floor(x/gridSize);
    OnGridY = floor(y/gridSize);
    if(OnGrid != lastGrid && OnGrid >= 0){
      if(moveDir == MOVE_R && x >= OnGridX*gridSize + 30 && routeGrid[OnGrid+10] == false){
        if(!routeGrid[OnGrid+1]){
          moveDir = MOVE_U;
        }else{
          moveDir = MOVE_D;
        }
      }
      if(moveDir == MOVE_U && y <= OnGridY*gridSize + 30 && routeGrid[OnGrid-1] == false){
        if(!routeGrid[OnGrid+10]){
          moveDir = MOVE_L;
        }else{
          moveDir = MOVE_R;
        }
      }
      if(moveDir == MOVE_L && x <= OnGridX*gridSize + 30 && routeGrid[OnGrid-10] == false){
        if(!routeGrid[OnGrid+1]){
          moveDir = MOVE_U;
        }else{
          moveDir = MOVE_D;
        }
      }
      if(moveDir == MOVE_D && y >= OnGridY*gridSize + 30 && routeGrid[OnGrid+1] == false){
        if(!routeGrid[OnGrid+10]){
          moveDir = MOVE_L;
        }else{
          moveDir = MOVE_R;
        }
      }
    }
  }
  
  public void healthCheck(){
    if(health <= 0){
      if(buffState[7]) volatileEffect();
      init();
      gold += bounty;
    }
  }
  
  public void endCheck(){
    if(OnGrid == lastGrid){
      baseHealth -= power;
      init();
    }
  }
  
  public void hurt(float damage){
    damage *= damageMultiplier();
    health -= damage;
    hurtShowDmg = damage;
    //println(ID + " / " + damage);
    drawHurt = true;
  }
  
  public void applyBuffEffect(){
    // Group: Armor
    armor -= armorReduction();
    
    // Group: Speed
    speed *= speedMultiplier();
    speed = max(speed,EnemyData.MIN_SPEED);
    
    // Size
    size += sizeAddition();
    
    // Group: Others
    if(buffState[2]) ionicEffect();
    if(buffState[7]) debuffIndicate(x,y,TurretSkillData.LASER_SKILL_C_T5_RADIUS,20,(buffTimer[7]-frameCount),TurretSkillData.LASER_SKILL_C_T5_DURATION);
  }
  
  public void checkBuffValidity(){
    for(int i = 0; i < buffState.length; i++){
      if(buffState[i] && buffTimer[i] == frameCount){
        buffState[i] = false;
        buffData1[i] = 0;
        buffData2[i] = 0;
      }
    }
  }
  
  public void getBuff(int buffID, float duration){
    buffState[buffID] = true;
    buffTimer[buffID] = frameCount + duration;
  }
  
  public void getBuff(int buffID, float duration, float data1, float data2){
    buffState[buffID] = true;
    buffTimer[buffID] = frameCount + duration;
    buffDataProcess1(buffID, data1);
    buffDataProcess2(buffID, data2);
  }
  
  public void buffDataProcess1(int ID, float data){
    switch(ID){
      case 2:
        buffData1[ID] += data;
        break;
      case 4:
        if(buffData1[ID] == 0) buffData1[ID] = frameCount;
        break;
      default:
        buffData1[ID] = data;
        break;
    }
  }
  
    public void buffDataProcess2(int ID, float data){
    switch(ID){
      case 2:
        buffData2[ID] += data;
        break;
      default:
        buffData2[ID] = data;
        break;
    }
  }
  
  public float armorReduction(){
    float amount = 0;
    if(buffState[0]){
      amount += armor * TurretSkillData.CANNON_SKILL_A_T3_ARMOR_REDUCTION_PERCENTAGE;
      debuffIndicate(x,y,40,20);
    }
    return amount;
  }
  
  public float damageMultiplier(){
    float multiplier = 1;
    if(buffState[4]){
      multiplier += TurretSkillData.LASER_SKILL_B_T3_EXTRA_DAMAGE_MULTIPLER;
    }
    return multiplier;
  }
  
  public float speedMultiplier(){
    float multiplier = 1;
    if(buffState[1]){
      multiplier -= TurretSkillData.CANNON_SKILL_C_T1_SLOW_PERCENTAGE;
      debuffIndicate(x,y,100,50,(buffTimer[1]-frameCount),TurretSkillData.CANNON_SKILL_C_T1_DURATION);
    }
    if(buffState[3]){
      multiplier -= TurretSkillData.CANNON_SKILL_C_T5_SLOW_PERCENTAGE;
      debuffIndicate(x,y,100,100,(buffTimer[3]-frameCount),TurretSkillData.CANNON_SKILL_C_T5_DURATION);
    }
    if(buffState[5]){
      multiplier -= TurretSkillData.LASER_SKILL_C_T2_SLOW_PERCENTAGE;
      debuffIndicate(x,y,100,100,(buffTimer[5]-frameCount),TurretSkillData.LASER_SKILL_C_T2_DURATION);
    }
    if(buffState[6]){
      multiplier -= map(health,maxHealth,0,0,TurretSkillData.LASER_SKILL_C_T4_MAXIMUM_SLOW_PERCENTAGE);
      debuffIndicate(x,y,100,100,(buffTimer[6]-frameCount),TurretSkillData.LASER_SKILL_C_T4_DURATION);
    }
    return constrain(multiplier,0,1);
  }
  
  public float sizeAddition(){
    float addition = 0;
    if(buffState[4]){
      addition = (frameCount-buffData1[4])/60*TurretSkillData.LASER_SKILL_B_T3_SIZE_INFLATION_AMOUNT_PER_SEC;
      addition = min(addition,TurretSkillData.LASER_SKILL_B_T3_MAX_INFLATION_AMOUNT);
    }
    return addition;
  }
  
  public void ionicEffect(){
    float dmg = buffData1[2] * TurretSkillData.CANNON_SKILL_C_T2_BASE_CANNON_DAMAGE_PERCENTAGE;
    if( (buffTimer[2] - frameCount) % TurretSkillData.CANNON_SKILL_C_T2_DAMAGE_INTERVAL == 0){
      for(int i = 0; i < sentEnemy; i++){
        if(i != ID && dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= TurretSkillData.CANNON_SKILL_C_T2_RADIUS){
            enemy[i].hurt(dmg);
        }
      }
    }
    debuffIndicate(x,y,TurretSkillData.CANNON_SKILL_C_T2_RADIUS*2,20*min(buffData2[2],6),(buffTimer[2]-frameCount),TurretSkillData.CANNON_SKILL_C_T2_DURATION);
  }
  
  public void volatileEffect(){
    float dmg = maxHealth * TurretSkillData.LASER_SKILL_C_T5_MAX_HEALTH_PERCENTAGE_AS_DAMAGE;
    for(int i = 0; i < sentEnemy; i++){
      if(i != ID && dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= TurretSkillData.LASER_SKILL_C_T5_RADIUS){
          enemy[i].hurt(dmg);
      }
    }
    debuffIndicate(x,y,TurretSkillData.LASER_SKILL_C_T5_RADIUS*2,120);
  }
  
  public void loadStat(){
    switch(type){
      case 1: //normal
        enemyColor = EnemyData.NORMAL_COLOR;
        size = EnemyData.NORMAL_SIZE;
        maxHealth = EnemyData.NORMAL_MAX_HEALTH;
        power = EnemyData.NORMAL_POWER;
        armor = EnemyData.NORMAL_ARMOR;
        speed = EnemyData.NORMAL_SPEED;
        bounty = EnemyData.NORMAL_BOUNTY;
        break;
        
      case 2: //fast
        enemyColor = EnemyData.FAST_COLOR;
        size = EnemyData.FAST_SIZE;
        maxHealth = EnemyData.FAST_MAX_HEALTH;
        power = EnemyData.FAST_POWER;
        armor = EnemyData.FAST_ARMOR;
        speed = EnemyData.FAST_SPEED;
        bounty = EnemyData.FAST_BOUNTY;
        break;
        
      case 3: //tank
        enemyColor = EnemyData.TANK_COLOR;
        size = EnemyData.TANK_SIZE;
        maxHealth = EnemyData.TANK_MAX_HEALTH;
        power = EnemyData.TANK_POWER;
        armor = EnemyData.TANK_ARMOR;
        speed = EnemyData.TANK_SPEED;
        bounty = EnemyData.TANK_BOUNTY;
        break;
        
      case 4: //support
        enemyColor = EnemyData.SUPPORT_COLOR;
        size = EnemyData.SUPPORT_SIZE;
        maxHealth = EnemyData.SUPPORT_MAX_HEALTH;
        power = EnemyData.SUPPORT_POWER;
        armor = EnemyData.SUPPORT_ARMOR;
        speed = EnemyData.SUPPORT_SPEED;
        bounty = EnemyData.SUPPORT_BOUNTY;
        break;
    }
    maxHealth += enemyMaxHealthGrowth(type);
    armor += enemyArmorGrowth(type);
    speed += enemySpeedGrowth(type);
    bounty += enemyBountyGrowth(type);
  }
  
  public void init(){
    for(int i = 0; i < buffState.length; i++){
      buffState[i] = false;
      buffTimer[i] = 0;
      buffData1[i] = 0;
      buffData2[i] = 0;
    }
    speed = 0;
    state = false;
    x = -4000;
    y = -4000;
  }
  
  Enemy(int enemyType, int ID){
    this.ID = ID;
    type = enemyType;
    loadStat();
    health = maxHealth;
    drawHurt = false;
    state = true;
    
    this.x = startpointX;
    this.y = startpointY;
  }
}
static class EnemyData{
  
  //Global Attributes
  
  static float MIN_SPEED = 0.2f;
  
  //Normal
  
  static int NORMAL_COLOR = 0xffFF0000;
  static float NORMAL_SIZE = 40;
  static float NORMAL_MAX_HEALTH = 200;
  static float NORMAL_POWER = 10;
  static float NORMAL_ARMOR = 15;
  static float NORMAL_SPEED = 1;
  static int NORMAL_BOUNTY = 3;
  
  //Fast
  
  static int FAST_COLOR = 0xffFAA112;
  static float FAST_SIZE = 20;
  static float FAST_MAX_HEALTH = 100;
  static float FAST_POWER = 5;
  static float FAST_ARMOR = 8;
  static float FAST_SPEED = 1.5f;
  static int FAST_BOUNTY = 2;
  
  //Tank
  
  static int TANK_COLOR = 0xff110F52;
  static float TANK_SIZE = 55;
  static float TANK_MAX_HEALTH = 5000;
  static float TANK_POWER = 20;
  static float TANK_ARMOR = 35;
  static float TANK_SPEED = 0.6f;
  static int TANK_BOUNTY = 40;
  
  //Support
  
  static int SUPPORT_COLOR = 0xffFAFF03;
  static float SUPPORT_SIZE = 30;
  static float SUPPORT_MAX_HEALTH = 150;
  static float SUPPORT_POWER = 15;
  static float SUPPORT_ARMOR = 15;
  static float SUPPORT_SPEED = 2;
  static int SUPPORT_BOUNTY = 15;

}
class Projectile{
  boolean projstate;
  int turretID;
  float size;
  float speed;
  float x;
  float y;
  float distance;
  float angle;
  float xSpeed;
  float ySpeed;
  
  public void projshoot(int turretID, float x, float y, float destX, float destY, float speed, float size){
    this.turretID = turretID;
    projstate = true;
    this.size = size;
    this.x = x;
    this.y = y;
    distance = dist(x,y,destX,destY);
    angle = atan2(y - destY, x - destX);
    xSpeed = speed * cos(angle);
    ySpeed = speed * sin(angle);
  }
  
  public void projmove(){
    x -= xSpeed;
    y -= ySpeed;
    projcheckhit();
  }
  
  public void projcheckhit(){
    for(int i = 0; i < sentEnemy; i++){
      if(hitDetection(i)){
        if(turret[turretID].skillState[2][2]){
          explosion(x,y,i);
        }else{
          applyBuff(i);
          if(checkCritTrigger(turretID,turret[turretID].critChance)){
            enemy[i].hurt(calDamage(turretID, i, turret[turretID].attackDmg, turret[turretID].critDamageMultiplier));
            applyBuffOnCrit(i);
          }else{
            enemy[i].hurt(calDamage(turretID, i, turret[turretID].attackDmg, 1));
          }
        }
        init();
        return;
      }
    }
    if(dist(x,y,turret[turretID].x,turret[turretID].y) > distance){
      if(turret[turretID].skillState[2][2]){
        explosion(x,y);
      }
      applyBuffOnMiss();
      init();
    }
  }
  
  public void explosion(float x, float y, int primaryID){
    debuffIndicate(x,y,TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_RADIUS,200);
    boolean crit = checkCritTrigger(turretID,turret[turretID].critChance);
    float splashDmg = turret[turretID].attackDmg * TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_DAMAGE_MULTIPLIER;
    for(int i = 0; i < sentEnemy; i++){
      if(dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_RADIUS){
        float distanceDecay;
        if(i == primaryID){
          applyBuff(i);
          distanceDecay = turret[turretID].attackDmg;
        }else{
          distanceDecay = map(dist(x,y,enemy[i].x,enemy[i].y) - enemy[i].size/2,0,TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_RADIUS,splashDmg,0);
        }
        if(crit){
          enemy[i].hurt(calDamage(turretID, i, distanceDecay, turret[turretID].critDamageMultiplier));
          if(i == primaryID) applyBuffOnCrit(i);
        }else{
          enemy[i].hurt(calDamage(turretID, i, distanceDecay, 1));
        }
      }
    }
  }
  
  public void explosion(float x, float y){
    debuffIndicate(x,y,TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_RADIUS,200);
    boolean crit = checkCritTrigger(turretID,turret[turretID].critChance);
    float splashDmg = turret[turretID].attackDmg * TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_DAMAGE_MULTIPLIER;
    for(int i = 0; i < sentEnemy; i++){
      if(dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_RADIUS){
        //applyBuff(i);
        float distanceDecay;
        distanceDecay = map(dist(x,y,enemy[i].x,enemy[i].y) - enemy[i].size/2,0,TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_RADIUS,splashDmg,0);
        if(crit){
          enemy[i].hurt(calDamage(turretID, i, distanceDecay, turret[turretID].critDamageMultiplier));
          //applyBuffOnCrit(i);
        }else{
          enemy[i].hurt(calDamage(turretID, i, distanceDecay, 1));
        }
      }
    }
  }
  
  public void applyBuff(int enemyID){
    if(turret[turretID].skillState[0][2]){
      enemy[enemyID].getBuff(0,TurretSkillData.CANNON_SKILL_A_T3_DURATION);
    }
    if(turret[turretID].skillState[2][0]){
      enemy[enemyID].getBuff(1,TurretSkillData.CANNON_SKILL_C_T1_DURATION);
    }
    if(turret[turretID].skillState[2][1]){
      enemy[enemyID].getBuff(2,TurretSkillData.CANNON_SKILL_C_T2_DURATION,turret[turretID].attackDmg,1);
    }
  }
  
  public void applyBuffOnCrit(int enemyID){
    if(turret[turretID].skillState[2][4]){
      enemy[enemyID].getBuff(3,TurretSkillData.CANNON_SKILL_C_T5_DURATION);
    }
  }
  
  public void applyBuffOnMiss(){
  }
  
  public void projshow(){
    fill(0xffF01DE6);
    ellipse(x,y,size,size);
  }
  
  public boolean hitDetection(int i){
    if(dist(x,y,enemy[i].x,enemy[i].y) <= size/2 + enemy[i].size/2){
      return true;
    }
    return false;
  }
  
  public void init(){
    projstate = false;
    distance = 0;
    speed = 0;
    x = -1000;
    y = -1000;
    angle = 0;
    xSpeed = 0;
    ySpeed = 0;
    size = 15;
  }
  
  Projectile(){
    init();
  }
}
/**
final int gameplayScreenX = 1200;
final int gameplayScreenY = 600;
final int gridSize = 60;
final int gridCount = (gameplayScreenX / gridSize) * (gameplayScreenY / gridSize);
boolean [] clickedState = new boolean[gridCount];

void setup(){
  size(1280,800);
  for(int i = 0; i < gridCount; i ++){
    clickedState[i] = false;
  }
}

void draw(){
  fill(0);
  pushMatrix();
  translate(0,50);
  rect(0,0,gameplayScreenX,gameplayScreenY);
  stroke(255);
  for(int i = 0; i < 1200/gridSize; i++){
    for(int j = 0; j < 600/gridSize; j++){
      int gridX = i*gridSize;
      int gridY = j*gridSize;
      if(mouseCheck(gridX,gridY)){
        fill(255);
      }else if(clickedState[i*10+j]){
        fill(0,255,0);
      }else{
        noFill();
      }
      if(mousePressed && (mouseButton == LEFT) && mouseCheck(gridX,gridY)){
        clickedState[i*10+j] = true;
      }else if(mousePressed && (mouseButton == RIGHT) && mouseCheck(gridX,gridY)){
        clickedState[i*10+j] = false;
      }
      rect(gridX,gridY,gridSize,gridSize);
    }
  }
  popMatrix();
  println(mouseX + ", " + mouseY);
}

boolean mouseCheck(int x, int y){
  return(mouseX > x && mouseX <= x + gridSize && mouseY > y + 50&& mouseY <= y + gridSize + 50);
}
**/
class Turret{
  boolean cooldown;
  boolean critMode;
  boolean [][] skillState = new boolean [3][5];
  int turretID;
  int turretType;
  String turretName;
  String [][] skillName = new String [3][5];
  String [][] skillDescription = new String [3][5];
  int target;
  int levelA;
  int levelB;
  int levelC;
  int levelAUpgradeCost;
  int levelBUpgradeCost;
  int levelCUpgradeCost;
  int cannonFervorStackCount;
  float totalCost;
  float [][] skillCost = new float [3][5];
  int sellPrice;
  float x;
  float y;
  float size = 40;
  float attackRate;
  float attackRange;
  float attackDmg;
  float critChance;
  float critDamageMultiplier;
  float critDuration;
  float critDurationCounter;
  float critCheckInterval;
  float critCheckCounter;
  float laserWidth;
  float laserHeat;
  float laserOverheatThreshold;
  float laserPiercePenaltyMultiplier;
  float laserDeathstarTime;
  int laserPitchforkTargetID_1;
  int laserPitchforkTargetID_2;
  float cooldownTime;
  float projSpeed;
  float projSize;
  boolean builtState;
  
  public void show(){
    switch(turretType){
      case CANNON:
        fill(0xff1FEAFF);
        noStroke();
        ellipse(x,y,size,size);
        if(skillState[1][2]) cannonFervorVisual();
        moveBullet();
        break;
        
      case LASER:
        if(cooldown){
          fill(0xffF4F520);
        }else{
          fill(0xffFF750A);
        }
        noStroke();
        ellipse(x,y,size,size);
        laserHeatVisual();
        break;
        
      case AURA:
        fill(0xffD916F7);
        noStroke();
        ellipse(x,y,size,size);
        fill(217,22,245,30+2*(levelA+levelB));
        ellipse(x,y,attackRange*2,attackRange*2);
        if(target != -1) critCheck();
        break;
    }
    loadStat();
    loadSkill();
    rateConvert();
    if(target != -1 && cooldown && enemy[target].x > 0){
      attack();
    }else if(turretType == LASER){
      laserHeat -= 3;
      laserHeat = constrain(laserHeat,0,laserOverheatThreshold);
    }
    if(!cooldown){
      if(turretType == LASER){
        target = -1;
      }
      cooldownTimer();
    }
    if(critMode){
      critDurationTimer();
    }
  }
  
  public void cannonFervorVisual(){
    pushStyle();
    fill(0);
    textFont(font[3]);
    textAlign(CENTER,CENTER);
    text(cannonFervorStackCount,x,y);
    popStyle();
  }
  
  public void laserHeatVisual(){
    pushStyle();
    noStroke();
    fill(255,0,0,130);
    /**
    if(cooldown){
      float r = floor(laserHeat/laserOverheatThreshold*size);

      ellipse(x,y,r,r);
    }else{
      float r = floor(cooldownTime/attackRate*size);
      ellipse(x,y,r,r);
    }
    **/
    if(cooldown){
      float a = laserHeat/laserOverheatThreshold*TWO_PI;
      float aStart = -PI/2;
      arc(x,y,size,size,aStart,a+aStart,PIE);
    }else{
      float a = cooldownTime/attackRate*TWO_PI;
      float aStart = -PI/2;
      arc(x,y,size,size,aStart,a+aStart,PIE);
    }
    
    popStyle();
  }
  
  public void critCheck(){
    critCheckCounter ++;
    if(critCheckCounter >= critCheckInterval){
      if(checkCritTrigger(turretID,critChance)){
        critMode = true;
        critCheckCounter = 0;
        critDurationCounter = critDuration;
      }else{
        critCheckCounter = 0;
      }
    }
  }
  
  public void critDurationTimer(){
    critDurationCounter -= 1;
    if(critDurationCounter <= 0){
      critMode = false;
    }
  }
  
  public void skillFervorReset(){
    cannonFervorStackCount = 0;
  }
  
  public void loadStat(){
    switch(turretType){
      case CANNON:
        attackDmg = TurretLevelData.cannonDamage[levelA];
        attackRate = TurretLevelData.cannonRate[levelB];
        attackRange = TurretLevelData.cannonRange[levelC];
        critChance = TurretLevelData.cannonCritChance;
        critDamageMultiplier = TurretLevelData.cannonCritDamageMultiplier;
        projSize = TurretLevelData.cannonProjSize;
        projSpeed = TurretLevelData.cannonProjSpeed;
        break;
        
      case LASER:
        attackDmg = TurretLevelData.laserDamage[levelA];
        attackRate = TurretLevelData.laserRate[levelB];
        attackRange = TurretLevelData.laserRange[levelC];
        critChance = TurretLevelData.laserCritChance;
        critDamageMultiplier = TurretLevelData.laserCritDamageMultiplier;
        critDuration = TurretLevelData.laserCritDuration;
        critCheckInterval = TurretLevelData.laserCritCheckInterval;
        laserWidth = TurretLevelData.laserWidth;
        laserOverheatThreshold = TurretLevelData.laserOverheatThreshold;
        laserPiercePenaltyMultiplier = TurretLevelData.laserPiercePenaltyMultiplier;
        break;
        
      case AURA:
        attackDmg = TurretLevelData.auraDamage[levelA];
        attackRate = TurretLevelData.auraRate[levelB];
        attackRange = TurretLevelData.auraRange[levelC];
        break;
    }
    sellPrice = floor(totalCost/2);
  }
  
  public void loadSkill(){
    attackRate *= skillRateMultiplier();
    attackRange *= skillRangeMultiplier();
    switch(turretType){
      case CANNON:
        projSpeed *= skillProjSpeedMultiplier();
        break;
      
      case LASER:
        critDuration *= skillCritModeDurationMultiplier();
        laserOverheatThreshold *= laserOverheatThresholdMultiplier();
        break;
    }
  }
  
  public float skillRateMultiplier(){
    float multiplier = 1;
    switch(turretType){
      case CANNON:
        if(skillState[1][0]){
          multiplier += TurretSkillData.CANNON_SKILL_B_T1_EXTRA_FIRE_RATE_MULTIPLIER;
        }
        if(skillState[1][2]){
          multiplier += TurretSkillData.CANNON_SKILL_B_T3_BONUS_FIRE_RATE_MULTIPLIER_PER_STACK * cannonFervorStackCount;
        }
        break;
        
      case LASER:
        
        break;
    }
    return multiplier;
  }
  
  public float skillCritModeDurationMultiplier(){
    float multiplier = 1;
    switch(turretType){
      case LASER:
        if(skillState[0][3]){
          multiplier += TurretSkillData.LASER_SKILL_A_T4_BONUS_CRIT_MODE_DURATION_MULTIPLIER;
        }
        break;
    }
    return multiplier;
  }
  
  public float skillProjSpeedMultiplier(){
    float multiplier = 1;
    if(skillState[1][1]){
      multiplier += TurretSkillData.CANNON_SKILL_B_T2_EXTRA_PROJECTILE_SPEED_MULTIPLIER;
    }
    return multiplier;
  }
  
  public float skillRangeMultiplier(){
    float multiplier = 1;
    switch(turretType){
      case CANNON:
        if(skillState[2][3]){
          multiplier += TurretSkillData.CANNON_SKILL_C_T4_ATTACK_RANGE_MULTIPLIER;
        }
        break;
    }
    return multiplier;
  }
  
  public void rateConvert(){
    switch(turretType){
      case CANNON:
        attackRate = rateConvertFrames(attackRate);
        break;
        
      case LASER:
        attackRate = secondConvertFrames(attackRate);
        break;
        
      case AURA:
        attackRate = rateConvertFrames(attackRate);
        break;
    }
  }

  public void detect(){
    float targetDist = 1500;
    if(target==-1){
      for(int i = 0; i < sentEnemy; i++){
        if(dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= attackRange && dist(x, y, enemy[i].x, enemy[i].y) < targetDist){
          targetDist = dist(x, y, enemy[i].x, enemy[i].y);
          target = i;
        }
      }
    }
    if(target != -1 && dist(x, y, enemy[target].x, enemy[target].y) - enemy[target].size/2 > attackRange){
      target = -1;
      skillFervorReset();
    }
    if(turretType == LASER && skillState[2][2]){
      laserPitchforkTargetID_1 = detectPitchfork(laserPitchforkTargetID_1, target, -10);
      laserPitchforkTargetID_2 = detectPitchfork(laserPitchforkTargetID_2, target, laserPitchforkTargetID_1);
    }
  }
  
  public int detectPitchfork(int currentTarget, int exception1, int exception2){
    float targetDistP = 1500;
    int pID = currentTarget;
    if(pID == -1){
      for(int i = 0; i < sentEnemy; i++){
        if(i == exception1 || i == exception2) continue;
        if(dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= attackRange && dist(x, y, enemy[i].x, enemy[i].y) < targetDistP){
          targetDistP = dist(x, y, enemy[i].x, enemy[i].y);
          pID = i;
        }
      }
    }
    if(pID != -1 && dist(x, y, enemy[pID].x, enemy[pID].y) - enemy[pID].size/2 > attackRange){
      return -1;
    }
    if(pID == exception1 || pID == exception2) pID = -1;
    return pID;
  }
  
  public void attack(){
    switch(turretType){
      case CANNON:
        if(skillState[1][2] && cannonFervorStackCount < TurretSkillData.CANNON_SKILL_B_T3_MAX_STACK){
          cannonFervorStackCount ++;
        }
        for(int i = 0; i < maxBulletCount; i++){ 
          if(!proj[turretID][i].projstate){   //pick a bullet
            proj[turretID][i].projshoot(turretID, x, y, enemy[target].x, enemy[target].y, projSpeed, projSize);
            cooldown = false;
            cooldownTime = attackRate;
            break;
          }
        }
        break;
        
      case LASER:
        float angle = atan2(enemy[target].y - y, enemy[target].x - x);
        int [] laserPierceID = new int [1];
        laserPierceID[0] = -1;
        float [] laserPierceDist = new float [1];
        laserPierceDist[0] = 3000;
        if(skillState[2][0]) laserWidth *= TurretSkillData.LASER_SKILL_C_T1_BEAM_WIDTH_MULTIPLIER;
        pushMatrix();
        translate(x,y);
        rotate(angle);
        pushStyle();
        rectMode(CENTER);
        if(critMode){
          fill(152,9,224,100+12*levelA);
        }else{
          critCheck();
          fill(255,0,0,100+12*levelA);
        }
        rect(attackRange/2,0,attackRange, laserWidth);
        for(int i = 0; i < sentEnemy; i++){
          float trueHeight = enemy[i].size+laserWidth;
          float trueWidth = enemy[i].size/2+attackRange;
          float translatedEnemyX = (enemy[i].x - x) * cos(-angle) - (enemy[i].y - y) * sin(-angle);
          float translatedEnemyY = (enemy[i].x - x) * sin(-angle) + (enemy[i].y - y) * cos(-angle);
          if(rectHitCheck(0, -trueHeight/2, trueWidth, trueHeight, translatedEnemyX, translatedEnemyY)){
            laserPierceID = checkLaserPierceOrder(laserPierceID, laserPierceDist, i, mag(translatedEnemyX,translatedEnemyY));
            laserPierceDist = checkLaserPierceDist(laserPierceID, laserPierceDist, i, mag(translatedEnemyX,translatedEnemyY));
          }
        }
        if(skillState[1][4]) laserPiercePenaltyMultiplier = TurretSkillData.LASER_SKILL_B_T5_PENETRATION_AMP;
        laserPierceDamageProcess(laserPierceID);
        laserHeat++;
        if(laserHeat == laserOverheatThreshold){
          if(!skillState[1][1] || !critMode){
            laserHeat = 0;
            cooldown = false;
            if(skillState[1][0]) attackRate = laserOverdriveProcess(attackRate);
            cooldownTime = attackRate;
          }
        }
        popStyle();
        popMatrix();
        if (skillState[0][4]) laserDeathstar(laserPierceID);
        if (skillState[2][2]){
          if(laserPitchforkTargetID_1 != -1) laserPitchforkAttack(laserPitchforkTargetID_1);
          if(laserPitchforkTargetID_2 != -1) laserPitchforkAttack(laserPitchforkTargetID_2);
        }
        break;
      
      case AURA:
        if(critMode){
          fill(255,0,0,120);
        }else{
          fill(255,255,255,120);
        }
        ellipse(x,y,attackRange*2,attackRange*2);
        for(int i = 0; i < sentEnemy; i++){
          if(dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= attackRange){
            if(critMode){
              enemy[i].hurt(calDamage(turretID, i, attackDmg, critDamageMultiplier));
            }else{
              enemy[i].hurt(calDamage(turretID, i, attackDmg));
            }
          }
        }
        cooldown = false;
        cooldownTime = attackRate;
        break;
    }
  }
  
  public void applyBuff(int enemyID){
    switch(turretType){
      case LASER:
        if(turret[turretID].skillState[1][2]){
          enemy[enemyID].getBuff(4,TurretSkillData.LASER_SKILL_B_T3_DURATION,frameCount,0);
        }
        if(turret[turretID].skillState[2][1]){
          enemy[enemyID].getBuff(5,TurretSkillData.LASER_SKILL_C_T2_DURATION);
        }
        if(turret[turretID].skillState[2][3]){
          enemy[enemyID].getBuff(6,TurretSkillData.LASER_SKILL_C_T4_DURATION);
        }
        if(turret[turretID].skillState[2][4]){
          enemy[enemyID].getBuff(7,TurretSkillData.LASER_SKILL_C_T5_DURATION);
        }
        break;
    }
  }
  
  public void moveBullet(){
    for(int i = 0; i < maxBulletCount; i++){
      if(proj[turretID][i].projstate){
        proj[turretID][i].projshow();
        proj[turretID][i].projmove();
      }
    }
  }
  
  public void laserPitchforkAttack(int id){
    float angleP = atan2(enemy[id].y - y, enemy[id].x - x);
    int [] laserPitchforkPierceID = new int [1];
    laserPitchforkPierceID[0] = -1;
    float [] laserPitchforkPierceDist = new float [1];
    laserPitchforkPierceDist[0] = 3000;
    pushMatrix();
    translate(x,y);
    rotate(angleP);
    pushStyle();
    rectMode(CENTER);
    if(critMode){
      fill(152,9,224,100+12*levelA);
    }else{
      critCheck();
      fill(255,0,0,100+12*levelA);
    }
    rect(attackRange/2,0,attackRange, laserWidth);
    for(int i = 0; i < sentEnemy; i++){
      float trueHeight = enemy[i].size+laserWidth;
      float trueWidth = enemy[i].size/2+attackRange;
      float translatedEnemyX = (enemy[i].x - x) * cos(-angleP) - (enemy[i].y - y) * sin(-angleP);
      float translatedEnemyY = (enemy[i].x - x) * sin(-angleP) + (enemy[i].y - y) * cos(-angleP);
      if(rectHitCheck(0, -trueHeight/2, trueWidth, trueHeight, translatedEnemyX, translatedEnemyY)){
        laserPitchforkPierceID = checkLaserPierceOrder(laserPitchforkPierceID, laserPitchforkPierceDist, i, mag(translatedEnemyX,translatedEnemyY));
        laserPitchforkPierceDist = checkLaserPierceDist(laserPitchforkPierceID, laserPitchforkPierceDist, i, mag(translatedEnemyX,translatedEnemyY));
      }
    }
    if(skillState[1][4]) laserPiercePenaltyMultiplier = TurretSkillData.LASER_SKILL_B_T5_PENETRATION_AMP;
    laserPierceDamageProcess(laserPitchforkPierceID, TurretSkillData.LASER_SKILL_C_T3_MINI_BEAM_DAMAGE_MULTIPLIER);
    popStyle();
    popMatrix();
  }
  
  public void cooldownTimer(){
    cooldownTime --;
    if(turretType == LASER && cooldownTime > attackRate){
      cooldownTime = attackRate;
    }
    if(cooldownTime <= 0){
      cooldown = true;
    }
  }
  
  public int [] checkLaserPierceOrder(int [] idList, float [] idDist, int inputID, float d){
    for(int i = 0; i < idList.length; i++){
      if( d <= idDist[i]){
        idList = splice(idList,inputID,i);
        return idList;
      }
    }
    return idList;
  }
  
  public float [] checkLaserPierceDist(int [] idList, float [] idDist, int inputID, float d){
    for(int i = 0; i < idList.length; i++){
      if( d <= idDist[i]){
        idDist = splice(idDist,d,i);
        return idDist;
      }
    }
    return idDist;
  }
  
  public void laserPierceDamageProcess(int [] idList){
    for(int i = 0; i < idList.length; i++){
      if(idList[i] == -1) break;
      float pierceDmg = attackDmg * pow(laserPiercePenaltyMultiplier,i);
      applyBuff(idList[i]);
      if(critMode){
        enemy[idList[i]].hurt(calDamage(turretID, idList[i], pierceDmg, critDamageMultiplier));
      }else{
        enemy[idList[i]].hurt(calDamage(turretID, idList[i], pierceDmg));
      }
    }
  }
  
  public void laserPierceDamageProcess(int [] idList, float dmgPct){
    for(int i = 0; i < idList.length; i++){
      if(idList[i] == -1) break;
      float pierceDmg = attackDmg * pow(laserPiercePenaltyMultiplier,i);
      pierceDmg *= dmgPct;
      applyBuff(idList[i]);
      if(critMode){
        enemy[idList[i]].hurt(calDamage(turretID, idList[i], pierceDmg, critDamageMultiplier));
      }else{
        enemy[idList[i]].hurt(calDamage(turretID, idList[i], pierceDmg));
      }
    }
  }
  
  public void laserDeathstar(int [] idList){
    int deathstarTarget;
    float deathstarDamage;
    deathstarDamage = attackDmg * TurretSkillData.LASER_SKILL_A_T5_BONUS_DAMAGE_MULTIPLIER;
    if(laserDeathstarTime == 0) laserDeathstarTime = frameCount;
    if((frameCount-laserDeathstarTime)%TurretSkillData.LASER_SKILL_A_T5_DAMAGE_INTERVAL == 0){
      deathstarTarget = idList[floor(random(0,idList.length-1))];
      enemy[deathstarTarget].hurt(deathstarDamage);
      debuffIndicate(enemy[deathstarTarget].x,enemy[deathstarTarget].y,70,100);
    }
  }
  
  public float laserOverdriveProcess(float inputRate){
    float m = 1 - (laserCheckOverdriveCount() * TurretSkillData.LASER_SKILL_B_T1_COOLDOWN_REDUCTION_MULTIPLIER_PER_ENEMY);
    inputRate *= m;
    return inputRate;
  }
  
  public int laserCheckOverdriveCount(){
    int enemyCount = 0;
    for(int i = 0; i < sentEnemy; i++){
      if(dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= attackRange){
        enemyCount ++;
      }
    }
    return min(5,enemyCount);
  }
  
  public float laserOverheatThresholdMultiplier(){
    float multiplier = 1;
    if(skillState[1][3]){
      multiplier += TurretSkillData.LASER_SKILL_B_T4_OVERHEAT_THRESHOLD_MULTIPLIER;
    }
    return multiplier;
  }
  
  
  public void turretInit(int type){
    switch(type){
      case CANNON:
        turretType = CANNON;
        turretName = "Cannon Turret";
        levelAUpgradeCost = TurretLevelData.cannonCostA[levelA];
        levelBUpgradeCost = TurretLevelData.cannonCostB[levelB];
        levelCUpgradeCost = TurretLevelData.cannonCostC[levelC];
        totalCost = TurretLevelData.cannonBuildCost;
        attackRate = TurretLevelData.cannonRate[0];
        attackRange = TurretLevelData.cannonRange[0];
        attackDmg = TurretLevelData.cannonDamage[0];
        critChance = TurretLevelData.cannonCritChance;
        critDamageMultiplier = TurretLevelData.cannonCritDamageMultiplier;
        projSize = TurretLevelData.cannonProjSize;
        projSpeed = TurretLevelData.cannonProjSpeed;
        cannonFervorStackCount = 0;
        break;
        
      case LASER:
        turretType = LASER;
        turretName = "Laser Turret";
        levelAUpgradeCost = TurretLevelData.laserCostA[levelA];
        levelBUpgradeCost = TurretLevelData.laserCostB[levelB];
        levelCUpgradeCost = TurretLevelData.laserCostC[levelC];
        totalCost = TurretLevelData.laserBuildCost;
        attackRate = TurretLevelData.laserRate[0];
        attackRange = TurretLevelData.laserRange[0];
        attackDmg = TurretLevelData.laserDamage[0];
        critChance = TurretLevelData.laserCritChance;
        critDamageMultiplier = TurretLevelData.laserCritDamageMultiplier;
        critDuration = TurretLevelData.laserCritDuration;
        critDurationCounter = 0;
        critCheckInterval = TurretLevelData.laserCritCheckInterval;
        critCheckCounter = 0;
        critMode = false;
        laserWidth = TurretLevelData.laserWidth;
        laserHeat = 0;
        laserOverheatThreshold = TurretLevelData.laserOverheatThreshold;
        laserPiercePenaltyMultiplier = TurretLevelData.laserPiercePenaltyMultiplier;
        laserDeathstarTime = 0;
        laserPitchforkTargetID_1 = -1;
        laserPitchforkTargetID_2 = -1;
        break;
        
      case AURA:
        turretType = AURA;
        turretName = "Aura Turret";
        levelAUpgradeCost = TurretLevelData.auraCostA[levelA];
        levelBUpgradeCost = TurretLevelData.auraCostB[levelB];
        levelCUpgradeCost = TurretLevelData.auraCostC[levelC];
        totalCost = TurretLevelData.auraBuildCost;
        attackRate = TurretLevelData.auraRate[0];
        attackRange = TurretLevelData.auraRange[0];
        attackDmg = TurretLevelData.auraDamage[0];
        critChance = 0.15f;
        critDamageMultiplier = 2;
        critDuration = 60;
        critDurationCounter = 0;
        critCheckInterval = 60;
        critCheckCounter = 0;
        critMode = false;
        break;
    }
    levelA = 0;
    levelB = 0;
    levelC = 0;
    cooldown = true;
    cooldownTime = 0;
    loadSkillInit();
  }
  
  public void loadSkillInit(){
    for(int i = 0; i < 3; i++){
      for(int j = 0; j < 5; j++){
        skillState[i][j] = false;
      }
    }
    switch(turretType){
      case CANNON:
        skillCost[0][0] = TurretSkillData.CANNON_SKILL_T1_COST;
        skillCost[1][0] = TurretSkillData.CANNON_SKILL_T1_COST;
        skillCost[2][0] = TurretSkillData.CANNON_SKILL_T1_COST;
        skillCost[0][1] = TurretSkillData.CANNON_SKILL_T2_COST;
        skillCost[1][1] = TurretSkillData.CANNON_SKILL_T2_COST;
        skillCost[2][1] = TurretSkillData.CANNON_SKILL_T2_COST;
        skillCost[0][2] = TurretSkillData.CANNON_SKILL_T3_COST;
        skillCost[1][2] = TurretSkillData.CANNON_SKILL_T3_COST;
        skillCost[2][2] = TurretSkillData.CANNON_SKILL_T3_COST;
        skillCost[0][3] = TurretSkillData.CANNON_SKILL_T4_COST;
        skillCost[1][3] = TurretSkillData.CANNON_SKILL_T4_COST;
        skillCost[2][3] = TurretSkillData.CANNON_SKILL_T4_COST;
        skillCost[0][4] = TurretSkillData.CANNON_SKILL_T5_COST;
        skillCost[1][4] = TurretSkillData.CANNON_SKILL_T5_COST;
        skillCost[2][4] = TurretSkillData.CANNON_SKILL_T5_COST;
      
        skillName[0][0] = TurretSkillData.CANNON_SKILL_A_T1_NAME;
        skillDescription[0][0] = TurretSkillData.CANNON_SKILL_A_T1_DESCRIPTION;
        skillName[0][1] = TurretSkillData.CANNON_SKILL_A_T2_NAME;
        skillDescription[0][1] = TurretSkillData.CANNON_SKILL_A_T2_DESCRIPTION;
        skillName[0][2] = TurretSkillData.CANNON_SKILL_A_T3_NAME;
        skillDescription[0][2] = TurretSkillData.CANNON_SKILL_A_T3_DESCRIPTION;
        skillName[0][3] = TurretSkillData.CANNON_SKILL_A_T4_NAME;
        skillDescription[0][3] = TurretSkillData.CANNON_SKILL_A_T4_DESCRIPTION;
        skillName[0][4] = TurretSkillData.CANNON_SKILL_A_T5_NAME;
        skillDescription[0][4] = TurretSkillData.CANNON_SKILL_A_T5_DESCRIPTION;
        
        skillName[1][0] = TurretSkillData.CANNON_SKILL_B_T1_NAME;
        skillDescription[1][0] = TurretSkillData.CANNON_SKILL_B_T1_DESCRIPTION;
        skillName[1][1] = TurretSkillData.CANNON_SKILL_B_T2_NAME;
        skillDescription[1][1] = TurretSkillData.CANNON_SKILL_B_T2_DESCRIPTION;
        skillName[1][2] = TurretSkillData.CANNON_SKILL_B_T3_NAME;
        skillDescription[1][2] = TurretSkillData.CANNON_SKILL_B_T3_DESCRIPTION;
        skillName[1][3] = TurretSkillData.CANNON_SKILL_B_T4_NAME;
        skillDescription[1][3] = TurretSkillData.CANNON_SKILL_B_T4_DESCRIPTION;
        skillName[1][4] = TurretSkillData.CANNON_SKILL_B_T5_NAME;
        skillDescription[1][4] = TurretSkillData.CANNON_SKILL_B_T5_DESCRIPTION;
        
        skillName[2][0] = TurretSkillData.CANNON_SKILL_C_T1_NAME;
        skillDescription[2][0] = TurretSkillData.CANNON_SKILL_C_T1_DESCRIPTION;
        skillName[2][1] = TurretSkillData.CANNON_SKILL_C_T2_NAME;
        skillDescription[2][1] = TurretSkillData.CANNON_SKILL_C_T2_DESCRIPTION;
        skillName[2][2] = TurretSkillData.CANNON_SKILL_C_T3_NAME;
        skillDescription[2][2] = TurretSkillData.CANNON_SKILL_C_T3_DESCRIPTION;
        skillName[2][3] = TurretSkillData.CANNON_SKILL_C_T4_NAME;
        skillDescription[2][3] = TurretSkillData.CANNON_SKILL_C_T4_DESCRIPTION;
        skillName[2][4] = TurretSkillData.CANNON_SKILL_C_T5_NAME;
        skillDescription[2][4] = TurretSkillData.CANNON_SKILL_C_T5_DESCRIPTION;
        break;
      
      case LASER:
        skillCost[0][0] = TurretSkillData.LASER_SKILL_T1_COST;
        skillCost[1][0] = TurretSkillData.LASER_SKILL_T1_COST;
        skillCost[2][0] = TurretSkillData.LASER_SKILL_T1_COST;
        skillCost[0][1] = TurretSkillData.LASER_SKILL_T2_COST;
        skillCost[1][1] = TurretSkillData.LASER_SKILL_T2_COST;
        skillCost[2][1] = TurretSkillData.LASER_SKILL_T2_COST;
        skillCost[0][2] = TurretSkillData.LASER_SKILL_T3_COST;
        skillCost[1][2] = TurretSkillData.LASER_SKILL_T3_COST;
        skillCost[2][2] = TurretSkillData.LASER_SKILL_T3_COST;
        skillCost[0][3] = TurretSkillData.LASER_SKILL_T4_COST;
        skillCost[1][3] = TurretSkillData.LASER_SKILL_T4_COST;
        skillCost[2][3] = TurretSkillData.LASER_SKILL_T4_COST;
        skillCost[0][4] = TurretSkillData.LASER_SKILL_T5_COST;
        skillCost[1][4] = TurretSkillData.LASER_SKILL_T5_COST;
        skillCost[2][4] = TurretSkillData.LASER_SKILL_T5_COST;
      
        skillName[0][0] = TurretSkillData.LASER_SKILL_A_T1_NAME;
        skillDescription[0][0] = TurretSkillData.LASER_SKILL_A_T1_DESCRIPTION;
        skillName[0][1] = TurretSkillData.LASER_SKILL_A_T2_NAME;
        skillDescription[0][1] = TurretSkillData.LASER_SKILL_A_T2_DESCRIPTION;
        skillName[0][2] = TurretSkillData.LASER_SKILL_A_T3_NAME;
        skillDescription[0][2] = TurretSkillData.LASER_SKILL_A_T3_DESCRIPTION;
        skillName[0][3] = TurretSkillData.LASER_SKILL_A_T4_NAME;
        skillDescription[0][3] = TurretSkillData.LASER_SKILL_A_T4_DESCRIPTION;
        skillName[0][4] = TurretSkillData.LASER_SKILL_A_T5_NAME;
        skillDescription[0][4] = TurretSkillData.LASER_SKILL_A_T5_DESCRIPTION;
        
        skillName[1][0] = TurretSkillData.LASER_SKILL_B_T1_NAME;
        skillDescription[1][0] = TurretSkillData.LASER_SKILL_B_T1_DESCRIPTION;
        skillName[1][1] = TurretSkillData.LASER_SKILL_B_T2_NAME;
        skillDescription[1][1] = TurretSkillData.LASER_SKILL_B_T2_DESCRIPTION;
        skillName[1][2] = TurretSkillData.LASER_SKILL_B_T3_NAME;
        skillDescription[1][2] = TurretSkillData.LASER_SKILL_B_T3_DESCRIPTION;
        skillName[1][3] = TurretSkillData.LASER_SKILL_B_T4_NAME;
        skillDescription[1][3] = TurretSkillData.LASER_SKILL_B_T4_DESCRIPTION;
        skillName[1][4] = TurretSkillData.LASER_SKILL_B_T5_NAME;
        skillDescription[1][4] = TurretSkillData.LASER_SKILL_B_T5_DESCRIPTION;
        
        skillName[2][0] = TurretSkillData.LASER_SKILL_C_T1_NAME;
        skillDescription[2][0] = TurretSkillData.LASER_SKILL_C_T1_DESCRIPTION;
        skillName[2][1] = TurretSkillData.LASER_SKILL_C_T2_NAME;
        skillDescription[2][1] = TurretSkillData.LASER_SKILL_C_T2_DESCRIPTION;
        skillName[2][2] = TurretSkillData.LASER_SKILL_C_T3_NAME;
        skillDescription[2][2] = TurretSkillData.LASER_SKILL_C_T3_DESCRIPTION;
        skillName[2][3] = TurretSkillData.LASER_SKILL_C_T4_NAME;
        skillDescription[2][3] = TurretSkillData.LASER_SKILL_C_T4_DESCRIPTION;
        skillName[2][4] = TurretSkillData.LASER_SKILL_C_T5_NAME;
        skillDescription[2][4] = TurretSkillData.LASER_SKILL_C_T5_DESCRIPTION;
        break;
      
      case AURA:
        skillCost[0][0] = TurretSkillData.AURA_SKILL_T1_COST;
        skillCost[1][0] = TurretSkillData.AURA_SKILL_T1_COST;
        skillCost[2][0] = TurretSkillData.AURA_SKILL_T1_COST;
        skillCost[0][1] = TurretSkillData.AURA_SKILL_T2_COST;
        skillCost[1][1] = TurretSkillData.AURA_SKILL_T2_COST;
        skillCost[2][1] = TurretSkillData.AURA_SKILL_T2_COST;
        skillCost[0][2] = TurretSkillData.AURA_SKILL_T3_COST;
        skillCost[1][2] = TurretSkillData.AURA_SKILL_T3_COST;
        skillCost[2][2] = TurretSkillData.AURA_SKILL_T3_COST;
        skillCost[0][3] = TurretSkillData.AURA_SKILL_T4_COST;
        skillCost[1][3] = TurretSkillData.AURA_SKILL_T4_COST;
        skillCost[2][3] = TurretSkillData.AURA_SKILL_T4_COST;
        skillCost[0][4] = TurretSkillData.AURA_SKILL_T5_COST;
        skillCost[1][4] = TurretSkillData.AURA_SKILL_T5_COST;
        skillCost[2][4] = TurretSkillData.AURA_SKILL_T5_COST;
      
        skillName[0][0] = TurretSkillData.AURA_SKILL_A_T1_NAME;
        skillDescription[0][0] = TurretSkillData.AURA_SKILL_A_T1_DESCRIPTION;
        skillName[0][1] = TurretSkillData.AURA_SKILL_A_T2_NAME;
        skillDescription[0][1] = TurretSkillData.AURA_SKILL_A_T2_DESCRIPTION;
        skillName[0][2] = TurretSkillData.AURA_SKILL_A_T3_NAME;
        skillDescription[0][2] = TurretSkillData.AURA_SKILL_A_T3_DESCRIPTION;
        skillName[0][3] = TurretSkillData.AURA_SKILL_A_T4_NAME;
        skillDescription[0][3] = TurretSkillData.AURA_SKILL_A_T4_DESCRIPTION;
        skillName[0][4] = TurretSkillData.AURA_SKILL_A_T5_NAME;
        skillDescription[0][4] = TurretSkillData.AURA_SKILL_A_T5_DESCRIPTION;
        
        skillName[1][0] = TurretSkillData.AURA_SKILL_B_T1_NAME;
        skillDescription[1][0] = TurretSkillData.AURA_SKILL_B_T1_DESCRIPTION;
        skillName[1][1] = TurretSkillData.AURA_SKILL_B_T2_NAME;
        skillDescription[1][1] = TurretSkillData.AURA_SKILL_B_T2_DESCRIPTION;
        skillName[1][2] = TurretSkillData.AURA_SKILL_B_T3_NAME;
        skillDescription[1][2] = TurretSkillData.AURA_SKILL_B_T3_DESCRIPTION;
        skillName[1][3] = TurretSkillData.AURA_SKILL_B_T4_NAME;
        skillDescription[1][3] = TurretSkillData.AURA_SKILL_B_T4_DESCRIPTION;
        skillName[1][4] = TurretSkillData.AURA_SKILL_B_T5_NAME;
        skillDescription[1][4] = TurretSkillData.AURA_SKILL_B_T5_DESCRIPTION;
        
        skillName[2][0] = TurretSkillData.AURA_SKILL_C_T1_NAME;
        skillDescription[2][0] = TurretSkillData.AURA_SKILL_C_T1_DESCRIPTION;
        skillName[2][1] = TurretSkillData.AURA_SKILL_C_T2_NAME;
        skillDescription[2][1] = TurretSkillData.AURA_SKILL_C_T2_DESCRIPTION;
        skillName[2][2] = TurretSkillData.AURA_SKILL_C_T3_NAME;
        skillDescription[2][2] = TurretSkillData.AURA_SKILL_C_T3_DESCRIPTION;
        skillName[2][3] = TurretSkillData.AURA_SKILL_C_T4_NAME;
        skillDescription[2][3] = TurretSkillData.AURA_SKILL_C_T4_DESCRIPTION;
        skillName[2][4] = TurretSkillData.AURA_SKILL_C_T5_NAME;
        skillDescription[2][4] = TurretSkillData.AURA_SKILL_C_T5_DESCRIPTION;
        break;
    }
  }
  
  Turret(int gridPos){
    turretID = gridPos;
    x = floor(gridPos/10) * gridSize + 30;
    y = (gridPos - floor(gridPos/10)*10) * gridSize + 30;
    target = -1;
    turretInit(0);
    builtState = false;
  }
}
static class TurretLevelData{
  static int maxLevel = 10;
  static int cannonBuildCost = 30;
  static int laserBuildCost = 40;
  static int auraBuildCost = 50;
  
  static float cannonCritChance = 0.08f;
  static float cannonCritDamageMultiplier = 2.5f;
  static float cannonProjSize = 20;
  static float cannonProjSpeed = 10;
  static float [] cannonDamage = {60,120,180,240,300,360,420,480,540,600,660};
  static float [] cannonRate = {2.0f,2.2f,2.4f,2.6f,2.8f,3.0f,3.2f,3.4f,3.6f,3.8f,4.0f};
  static float [] cannonRange = {150,165,180,195,210,225,240,255,270,285,300};
  static int [] cannonCostA = {15,30,45,60,75,90,105,120,135,150,165};
  static int [] cannonCostB = {15,30,45,60,75,90,105,120,135,150,165};
  static int [] cannonCostC = {15,30,45,60,75,90,105,120,135,150,165};
  
  static float laserCritChance = 0.16f;
  static float laserCritDamageMultiplier = 2;
  static float laserCritDuration = 60;
  static float laserCritCheckInterval = 60;
  static float laserWidth = 10;
  static float laserOverheatThreshold = 240;
  static float laserPiercePenaltyMultiplier = 0.7f;
  static float [] laserDamage = {2,4,6,8,10,12,14,16,18,20,22};
  static float [] laserRate = {2.10f,1.95f,1.80f,1.65f,1.50f,1.35f,1.20f,1.05f,0.90f,0.75f,0.60f};
  static float [] laserRange = {180,200,220,240,260,280,300,320,340,360,380};
  static int [] laserCostA = {20,40,60,80,100,120,140,160,180,200,220};
  static int [] laserCostB = {20,40,60,80,100,120,140,160,180,200,220};
  static int [] laserCostC = {20,40,60,80,100,120,140,160,180,200,220};
  
  static float [] auraDamage = {10,15,20,25,30,35,40,45,50,55,60};
  static float [] auraRate = {3.0f,3.6f,4.2f,5.0f,5.8f,6.4f,7.2f,8.0f,9.0f,10.0f,12.0f};
  static float [] auraRange = {70,77,84,91,98,105,112,119,126,133,140};
  static int [] auraCostA = {25,50,75,100,125,150,175,200,225,250,275};
  static int [] auraCostB = {25,50,75,100,125,150,175,200,225,250,275};
  static int [] auraCostC = {25,50,75,100,125,150,175,200,225,250,275};
}
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
  static float CANNON_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.3f;
  
      //T2
  static String CANNON_SKILL_A_T2_NAME = "Reaper";
  static String CANNON_SKILL_A_T2_DESCRIPTION = "Deals bonus damage based on the missing health of the target.";
  static float CANNON_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 1.5f;
  
      //T3
  static String CANNON_SKILL_A_T3_NAME = "Acid Infusion";
  static String CANNON_SKILL_A_T3_DESCRIPTION = "Applies a debuff that cut the victim's armor in half.";
  static float CANNON_SKILL_A_T3_ARMOR_REDUCTION_PERCENTAGE = 0.5f;
  static float CANNON_SKILL_A_T3_DURATION = 300;
  
      //T4
  static String CANNON_SKILL_A_T4_NAME = "Headhunter";
  static String CANNON_SKILL_A_T4_DESCRIPTION = "Greatly increases the critical damage.";
  static float CANNON_SKILL_A_T4_BONUS_CRITICAL_DAMAGE_MULTIPLIER = 0.5f;
  
      //T5
  static String CANNON_SKILL_A_T5_NAME = "Saboteur";
  static String CANNON_SKILL_A_T5_DESCRIPTION = "Deals additional damage based on target\u2019s current health.";
  static float CANNON_SKILL_A_T5_HP_PERCENTAGE = 0.003f;
  
  
    //LevelB
    
      //T1
  static String CANNON_SKILL_B_T1_NAME = "Rapid Fire";
  static String CANNON_SKILL_B_T1_DESCRIPTION = "Greatly increases the fire rate.";
  static float CANNON_SKILL_B_T1_EXTRA_FIRE_RATE_MULTIPLIER = 0.25f;
  
      //T2
  static String CANNON_SKILL_B_T2_NAME = "Ballistics";
  static String CANNON_SKILL_B_T2_DESCRIPTION = "Greatly increases the projectile speed.";
  static float CANNON_SKILL_B_T2_EXTRA_PROJECTILE_SPEED_MULTIPLIER = 1;
  
      //T3
  static String CANNON_SKILL_B_T3_NAME = "Fervor";
  static String CANNON_SKILL_B_T3_DESCRIPTION = "Each continuous attack on the same target stacks fire rate; loses all on changing target.";
  static float CANNON_SKILL_B_T3_BONUS_FIRE_RATE_MULTIPLIER_PER_STACK = 0.1f;
  static int CANNON_SKILL_B_T3_MAX_STACK = 15;
  
      //T4
  static String CANNON_SKILL_B_T4_NAME = "Bore";
  static String CANNON_SKILL_B_T4_DESCRIPTION = "Greatly increases the chance of critical hits.";
  static float CANNON_SKILL_B_T4_BONUS_CRIT_CHANCE = 0.08f;
  
      //T5
  static String CANNON_SKILL_B_T5_NAME = "Death Wish";
  static String CANNON_SKILL_B_T5_DESCRIPTION = "Increases the chance of critical hits based on the missing health of the castle.";
  static float CANNON_SKILL_B_T5_MIN_BONUS_CRIT_CHANCE = 0.05f;
  static float CANNON_SKILL_B_T5_MAX_BONUS_CRIT_CHANCE = 0.30f;
  
    //LevelC
    
      //T1
  static String CANNON_SKILL_C_T1_NAME = "Cold Snap";
  static String CANNON_SKILL_C_T1_DESCRIPTION = "Applies a debuff that slows enemies.";
  static float CANNON_SKILL_C_T1_SLOW_PERCENTAGE = 0.20f;
  static int CANNON_SKILL_C_T1_DURATION = 45;
  
      //T2
  static String CANNON_SKILL_C_T2_NAME = "Ionic Shell";
  static String CANNON_SKILL_C_T2_DESCRIPTION = "Applies a damaging aura on the target, hurting the enemies around the carrier but not itself.";
  static float CANNON_SKILL_C_T2_BASE_CANNON_DAMAGE_PERCENTAGE = 0.04f;
  static float CANNON_SKILL_C_T2_RADIUS = 180;
  static float CANNON_SKILL_C_T2_DAMAGE_INTERVAL = 15;
  static float CANNON_SKILL_C_T2_DURATION = 480;
  
      //T3
  static String CANNON_SKILL_C_T3_NAME = "Boombastics";
  static String CANNON_SKILL_C_T3_DESCRIPTION = "Cannons explode on impact.";
  static float CANNON_SKILL_C_T3_EXPLOSION_RADIUS = 80;
  static float CANNON_SKILL_C_T3_EXPLOSION_DAMAGE_MULTIPLIER = 0.8f;
  
      //T4   
  static String CANNON_SKILL_C_T4_NAME = "Eagle Sight";
  static String CANNON_SKILL_C_T4_DESCRIPTION = "Greatly increases the attack range of the turret.";
  static float CANNON_SKILL_C_T4_ATTACK_RANGE_MULTIPLIER = 0.6f;
  
      //T5
  static String CANNON_SKILL_C_T5_NAME = "M.A.I.M.";
  static String CANNON_SKILL_C_T5_DESCRIPTION = "On critical hit, applies a debuff that severely cripples the victim.";
  static float CANNON_SKILL_C_T5_SLOW_PERCENTAGE = 0.5f;
  static float CANNON_SKILL_C_T5_DURATION = 30;
  
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
  static float LASER_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.5f;
  
      //T2
  static String LASER_SKILL_A_T2_NAME = "Thermocide";
  static String LASER_SKILL_A_T2_DESCRIPTION = "Increases the damage based on the heat of the laser.";
  static float LASER_SKILL_A_T2_MIN_HEAT_THRESHOLD = 120;
  static float LASER_SKILL_A_T2_MAX_DAMAGE_HEAT_CAP = 240;
  static float LASER_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 2.00f;
  
      //T3
  static String LASER_SKILL_A_T3_NAME = "Dematerialization";
  static String LASER_SKILL_A_T3_DESCRIPTION = "Greatly increases the effectiveness against armor.";
  static float LASER_SKILL_A_T3_ARMOR_BYPASS_MULTIPLIER = 0.2f;
  
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
  static float LASER_SKILL_B_T1_COOLDOWN_REDUCTION_MULTIPLIER_PER_ENEMY = 0.08f;
  static float LASER_SKILL_B_T1_MAXIMUM_COOLDOWN_REDUCTION_MULTIPLIER = 0.40f;
  
      //T2
  static String LASER_SKILL_B_T2_NAME = "Heat Lock";
  static String LASER_SKILL_B_T2_DESCRIPTION = "During crit mode, the turret is safe from overheat.";
  
      //T3
  static String LASER_SKILL_B_T3_NAME = "DNA Mutation";
  static String LASER_SKILL_B_T3_DESCRIPTION = "Applies a debuff that inflates the enemy, causing them more prone to get hit and take extra damage.";
  static float LASER_SKILL_B_T3_SIZE_INFLATION_AMOUNT_PER_SEC = 10;
  static float LASER_SKILL_B_T3_MAX_INFLATION_AMOUNT = 20;
  static float LASER_SKILL_B_T3_EXTRA_DAMAGE_MULTIPLER = 0.5f;
  static float LASER_SKILL_B_T3_DURATION = 300;
  
      //T4
  static String LASER_SKILL_B_T4_NAME = "Supercool";
  static String LASER_SKILL_B_T4_DESCRIPTION = "Increases the overheat threshold.";
  static float LASER_SKILL_B_T4_OVERHEAT_THRESHOLD_MULTIPLIER = 1.5f;
  
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
  static float LASER_SKILL_C_T2_SLOW_PERCENTAGE = 0.15f;
  static float LASER_SKILL_C_T2_DURATION = 6;
  
      //T3
  static String LASER_SKILL_C_T3_NAME = "Pitchfork";
  static String LASER_SKILL_C_T3_DESCRIPTION = "Creates two additional lasers that deals less damage; lasers cannot share the same target.";
  static float LASER_SKILL_C_T3_MINI_BEAM_COUNT = 2;
  static float LASER_SKILL_C_T3_MINI_BEAM_DAMAGE_MULTIPLIER = 0.6f;
  
      //T4
  static String LASER_SKILL_C_T4_NAME = "Breach Module";
  static String LASER_SKILL_C_T4_DESCRIPTION = "Applies a debuff that slows enemies based on their missing health.";
  static float LASER_SKILL_C_T4_MAXIMUM_SLOW_PERCENTAGE = 0.4f;
  static float LASER_SKILL_C_T4_DURATION = 6;
  
      //T5
  static String LASER_SKILL_C_T5_NAME = "Volatile Compound";
  static String LASER_SKILL_C_T5_DESCRIPTION = "Applies a debuff that when the carrier dies, its body explodes, damaging all nearby enemies.";
  static float LASER_SKILL_C_T5_MAX_HEALTH_PERCENTAGE_AS_DAMAGE = 0.1f;
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
  static float AURA_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.3f;
  
      //T2
  static String AURA_SKILL_A_T2_NAME = "Reaper";
  static String AURA_SKILL_A_T2_DESCRIPTION = "Deals bonus damage based on the missing health of the target.";
  static float AURA_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 1.5f;
  
      //T3
  static String AURA_SKILL_A_T3_NAME = "Acid Infusion";
  static String AURA_SKILL_A_T3_DESCRIPTION = "Applies a debuff that cut the victim's armor in half.";
  static float AURA_SKILL_A_T3_ARMOR_REDUCTION_PERCENTAGE = 0.5f;
  static float AURA_SKILL_A_T3_DURATION = 300;
  
      //T4
  static String AURA_SKILL_A_T4_NAME = "Headhunter";
  static String AURA_SKILL_A_T4_DESCRIPTION = "Greatly increases the critical damage.";
  static float AURA_SKILL_A_T4_BONUS_CRITICAL_DAMAGE_MULTIPLIER = 0.5f;
  
      //T5
  static String AURA_SKILL_A_T5_NAME = "Saboteur";
  static String AURA_SKILL_A_T5_DESCRIPTION = "Deals additional damage based on target\u2019s current health.";
  static float AURA_SKILL_A_T5_HP_PERCENTAGE = 0.003f;
  
  
    //LevelB
    
      //T1
  static String AURA_SKILL_B_T1_NAME = "Rapid Fire";
  static String AURA_SKILL_B_T1_DESCRIPTION = "Greatly increases the fire rate.";
  static float AURA_SKILL_B_T1_EXTRA_FIRE_RATE_MULTIPLIER = 0.25f;
  
      //T2
  static String AURA_SKILL_B_T2_NAME = "Ballistics";
  static String AURA_SKILL_B_T2_DESCRIPTION = "Greatly increases the projectile speed.";
  static float AURA_SKILL_B_T2_EXTRA_PROJECTILE_SPEED_MULTIPLIER = 1;
  
      //T3
  static String AURA_SKILL_B_T3_NAME = "Fervor";
  static String AURA_SKILL_B_T3_DESCRIPTION = "Each continuous attack on the same target stacks fire rate; loses all on changing target.";
  static float AURA_SKILL_B_T3_BONUS_FIRE_RATE_MULTIPLIER_PER_STACK = 0.1f;
  static int AURA_SKILL_B_T3_MAX_STACK = 15;
  
      //T4
  static String AURA_SKILL_B_T4_NAME = "Bore";
  static String AURA_SKILL_B_T4_DESCRIPTION = "Greatly increases the chance of critical hits.";
  static float AURA_SKILL_B_T4_BONUS_CRIT_CHANCE = 0.08f;
  
      //T5
  static String AURA_SKILL_B_T5_NAME = "Death Wish";
  static String AURA_SKILL_B_T5_DESCRIPTION = "Increases the chance of critical hits based on the missing health of the castle.";
  static float AURA_SKILL_B_T5_MIN_BONUS_CRIT_CHANCE = 0.05f;
  static float AURA_SKILL_B_T5_MAX_BONUS_CRIT_CHANCE = 0.30f;
  
    //LevelC
    
      //T1
  static String AURA_SKILL_C_T1_NAME = "Cold Snap";
  static String AURA_SKILL_C_T1_DESCRIPTION = "Applies a debuff that slows enemies.";
  static float AURA_SKILL_C_T1_SLOW_PERCENTAGE = 0.20f;
  static int AURA_SKILL_C_T1_DURATION = 60;
  
      //T2
  static String AURA_SKILL_C_T2_NAME = "Ionic Shell";
  static String AURA_SKILL_C_T2_DESCRIPTION = "Applies a damaging aura on the target, hurting the enemies around the carrier but not itself.";
  static float AURA_SKILL_C_T2_BASE_AURA_DAMAGE_PERCENTAGE = 0.04f;
  static float AURA_SKILL_C_T2_RADIUS = 180;
  static float AURA_SKILL_C_T2_DAMAGE_INTERVAL = 15;
  static float AURA_SKILL_C_T2_DURATION = 480;
  
      //T3
  static String AURA_SKILL_C_T3_NAME = "Boombastics";
  static String AURA_SKILL_C_T3_DESCRIPTION = "AURAs explode on impact, damaging and applying debuffs to all victims.";
  static float AURA_SKILL_C_T3_EXPLOSION_RADIUS = 120;
  static float AURA_SKILL_C_T3_EXPLOSION_DAMAGE_MULTIPLIER = 0.8f;
  
      //T4
  static String AURA_SKILL_C_T4_NAME = "Eagle Sight";
  static String AURA_SKILL_C_T4_DESCRIPTION = "Greatly increases the attack range of the turret.";
  static float AURA_SKILL_C_T4_ATTACK_RANGE_MULTIPLIER = 0.6f;
  
      //T5
  static String AURA_SKILL_C_T5_NAME = "M.A.I.M.";
  static String AURA_SKILL_C_T5_DESCRIPTION = "On critical hit, applies a debuff that severely cripples the victim.";
  static float AURA_SKILL_C_T5_SLOW_PERCENTAGE = 0.6f;
  static float AURA_SKILL_C_T5_DURATION = 30;
  
}
class mapData{
  int mapID;
  mapData(int inputID){
    for(int i = 0; i < gridCount; i ++){
      routeGrid[i] = false;
    }
    mapID = inputID;
    switch(mapID){
      case 1:
        startpointX = -40;
        startpointY = 210;
        routeGrid[3] = true;
        routeGrid[13] = true;
        routeGrid[23] = true;
        routeGrid[33] = true;
        routeGrid[43] = true;
        routeGrid[42] = true;
        routeGrid[41] = true;
        routeGrid[51] = true;
        routeGrid[61] = true;
        routeGrid[62] = true;
        routeGrid[72] = true;
        routeGrid[82] = true;
        routeGrid[92] = true;
        routeGrid[93] = true;
        routeGrid[103] = true;
        routeGrid[113] = true;
        routeGrid[123] = true;
        routeGrid[122] = true;
        routeGrid[121] = true;
        routeGrid[141] = true;
        routeGrid[131] = true;
        routeGrid[151] = true;
        routeGrid[161] = true;
        routeGrid[162] = true;
        routeGrid[163] = true;
        routeGrid[173] = true;
        routeGrid[183] = true;
        routeGrid[184] = true;
        routeGrid[185] = true;
        routeGrid[175] = true;
        routeGrid[165] = true;
        routeGrid[166] = true;
        routeGrid[167] = true;
        routeGrid[157] = true;
        routeGrid[147] = true;
        routeGrid[137] = true;
        routeGrid[127] = true;
        routeGrid[126] = true;
        routeGrid[125] = true;
        routeGrid[115] = true;
        routeGrid[105] = true;
        routeGrid[95] = true;
        routeGrid[96] = true;
        routeGrid[97] = true;
        routeGrid[87] = true;
        routeGrid[77] = true;
        routeGrid[76] = true;
        routeGrid[66] = true;
        routeGrid[56] = true;
        routeGrid[57] = true;
        routeGrid[58] = true;
        routeGrid[68] = true;
        routeGrid[69] = true;
        lastGrid = 69;
        break;
    }
  }
}
class waveData{
  int waveID;
  int indexCount;
  
  public void load(int inputWave){
    waveID = inputWave;
    if(waveID>5){
      waveID = ((waveID%5)+1);
    }
    switch(waveID){
      case 1:
        //index(3,1);
        index(1,10);
        break;
      case 2:
        index(1,10);
        break;
      case 3:
        index(2,15);
        break;
      case 4:
        index(1,5);
        index(2,5);
        index(1,10);
        break;
      case 5:
        index(3,1);
        break;
    }
    currentWaveMaxEnemy = indexCount;
    indexCount = 0;
  }
  
  public void index(int type, int amount){
    int processedCount = indexCount;
    for(int i = processedCount; i < processedCount + amount; i++){
      enemy[i] = new Enemy(type,i);
      enemy[i].state = true;
      indexCount++;
    }
  }
  
  waveData(){
    indexCount = 0;
    waveID = 1;
  }
  
}
  public void settings() {  size(1280,800,P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "TDProject" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
