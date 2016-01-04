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
static final int UI_PAUSE = 6;

static final int CLICKABLE = 0;
static final int UNCLICKABLE = 1;
static final int ENABLED = 2;

static final int COLOR_WHITE = 0;
static final int COLOR_RED = 1;
static final int COLOR_GREEN = 2;
static final int COLOR_RAINBOW = 3;

static final int TEXT_MOVE = 0;
static final int TEXT_NOMOVE = 1;

static final int DIFFICULTY_EASY = 0;
static final int DIFFICULTY_NORMAL = 1;
static final int DIFFICULTY_HARD = 2;


final int gameplayScreenX = 1200; //The width of screen for gameplay
final int gameplayScreenY = 600; //The height of screen for gameplay
final int gridSize = 60; //The size of grids in the gameplay screen
final int gridCount = (gameplayScreenX / gridSize) * (gameplayScreenY / gridSize); //Total grid count
final int maxEnemyCount = 100; //The limit of the amount of enemies; for indexing during initialization
final int maxBulletCount = 10; //The limit of the amount of projectiles; for indexing during initialization
final int screenOffsetX = 0; //The horizonal offset of gameplay screen
final int screenOffsetY = 50; //The vertical offset of gameplay screen
PFont [] font = new PFont [10];
PImage targetArrow;
PImage [] bgTiles = new PImage [20];
PImage [] mainBG = new PImage [10];
PImage [] mapPreview = new PImage [3];
PImage laserBeam;
PImage shockwave;
boolean [] routeGrid = new boolean[gridCount]; //Creates an array to store whether each grid is on the route
boolean skillMenuState;
boolean assaultMode;
boolean endMenuState;
boolean pauseState;
int UIMode;
int previousMode;
int gold;
int [] startGold = {150,100,100};
int realFrameCount;
int endFrame;
int gameSpeed;
int buildMode;
int buildCost;
int mouseOnGrid; //Store the information of the grid where the mouse places on
int lastGrid; // The last grid of route given by mapData
int sentEnemy = 0; // The amount of enemies who are already sent out
int currentWaveMaxEnemy = 0; // The total amount of enemies in this wave
int currentWave = 0; // The number of the current wave
int targetTurretID = -1;
int targetEnemy = -1;
int difficulty;
int difficultySelection = -1;
int mapSelection = -1;
int startTime;
float baseHealth;
float glitch = 12;
float baseMaxHealth = 100;
float startpointX, startpointY; //Where the enemies spawn; given by mapData

mapData currentMap;
waveData wave;
Turret [] turret = new Turret [gridCount];
Turret [] demoTurret = new Turret [3];
Enemy [] enemy = new Enemy [maxEnemyCount];
Enemy [] demoEnemy = new Enemy [4];
Projectile [][] proj = new Projectile [gridCount][maxBulletCount]; //Use two-dimension array to store projectiles and their correspondant turrets
Button [] upgrade = new Button [3];
Button [] build = new Button [3];
Button [] skillPurchase = new Button [15];
Button sell, skillMenu, gameSpeedChange, nextWave, pause;
Button [] mapSelect = new Button [3];
Button [] diffSelect = new Button [3];
Button pauseResume, pauseMainmenu;
Button startGame;
Button endMainmenu, endRestart;
PopupText [] popupTextArray = new PopupText [25];

public void setup(){
  frameRate(60);
  
  //smooth(8);
  UIMode = UI_MAINMENU;
  menuInit();
  realFrameCount = 0;
  laserBeam = loadImage("img/laserbeam.png");
  targetArrow = loadImage("img/green_arrow.png");
  shockwave = loadImage("img/shockwave.png");
  for(int i = 0; i < 20; i++){
    bgTiles[i] = loadImage("img/bg" + i + ".png");
  }
  for(int i = 0; i < 10; i++){
    mainBG[i] = loadImage("img/mainBG/bg" + i + ".png");
  }
  for(int i = 0; i < 3; i++){
    mapPreview[i] = loadImage("img/mapPreview/map" + (i+1) + ".png");
  }
  font[1] = createFont("whitrabt.ttf", 38, true);
  font[2] = createFont("whitrabt.ttf", 25, true);
  font[3] = createFont("DilleniaUPC Bold", 26);
  font[4] = createFont("SourceCodePro-Bold.ttf", 45);
  font[5] = createFont("whitrabt.ttf", 130);
  font[6] = createFont("whitrabt.ttf", 70);
  imageMode(CENTER);
  currentMap = new mapData(0);
}

public void menuInit(){
  gameInit();
  endMenuState = false;
  difficultySelection = -1;
  mapSelection = -1;
  targetEnemy = -1;
  difficulty = DIFFICULTY_EASY;
  demoTurret[0] = new Turret(21,CANNON);
  demoTurret[1] = new Turret(34,LASER);
  demoTurret[2] = new Turret(62,AURA);
  enemy[0] = new Enemy(ENEMY_NORMAL,0,true);
  enemy[1] = new Enemy(ENEMY_FAST,1,true);
  enemy[2] = new Enemy(ENEMY_TANK,2,true);
  enemy[3] = new Enemy(ENEMY_SUPPORT,3,true);
  sentEnemy = 4;
  gameSpeed = 1;
  currentMap = new mapData(0);
}

public void draw(){
  switch(UIMode){
    case UI_MAINMENU:
      drawMainMenu();
      break;
    case UI_BUILD:
      drawGameplay();
      break;
    case UI_PLACEMENT:
      drawGameplay();
      break;
    case UI_UPGRADE:
      drawGameplay();
      break;
    case UI_SKILL:
      drawGameplay();
      break;
    case UI_GAMEOVER:
      drawEndScene();
      break;
    case UI_PAUSE:
      drawPauseUI();
      break;
  }
}

public void drawMainMenu(){
  background(0,20);
  pushStyle();
  tint(255,30+10*sin(frameCount/(PI*10)));
  image(mainBG[(frameCount)%10],width/2,height/2);
  textFont(font[5]);
  textAlign(LEFT,TOP);
  fill(0,255,0);
  noStroke();
  text("DIGITAL",130,100);
  text("ASSAULT",130,230);
  
  if((frameCount)%5==0) glitch = (random(14));
  if(glitch<7){
    rect(132+74*floor(glitch),95+130*(floor(glitch%2)),70,100);
  }
  menuDemo();
  popStyle();
  pushStyle();
  imageMode(CORNER);
  startGame = new Button(760, 620, 400, 80, "START", font[6], CLICKABLE);
  if(difficultySelection == -1 || mapSelection == -1) startGame.showState = UNCLICKABLE;
  startGame.show();
  diffSelect[0] = new Button(760, 220, 140, 50, "EASY", font[1]);
  diffSelect[1] = new Button(760, 320, 140, 50, "NORMAL", font[1]);
  diffSelect[2] = new Button(760, 420, 140, 50, "HARD", font[1]);
  mapSelect[0] = new Button(920, 220, 140, 50, "MAP 1", font[1]);
  mapSelect[1] = new Button(920, 320, 140, 50, "MAP 2", font[1]);
  mapSelect[2] = new Button(920, 420, 140, 50, "MAP 3", font[1]);
  for(int i = 0; i < mapPreview.length; i++){
    if(mapSelection == i+1){
      tint(0xffF7CF2A);
    }else if(mouseCheck(mapSelect[i])){
      tint(0,255,0);
    }else{
      tint(255,160);
    }
    image(mapPreview[i],1060,220+100*i,100,50);
  }
  for(int i = 0; i < diffSelect.length; i++){
    if(difficultySelection == i) diffSelect[i].showState = ENABLED;
    diffSelect[i].show();
  }
  for(int i = 0; i < mapSelect.length; i++){
    if(mapSelection - 1 == i) mapSelect[i].showState = ENABLED;
    mapSelect[i].show();
  }
  popStyle();
}

public void menuDemo(){
  noStroke();
  pushMatrix();
  translate(141,380);
  for(int i = 0; i < 1200/gridSize; i++){
    for(int j = 0; j < 600/gridSize; j++){
      int gridX = i*gridSize;
      int gridY = j*gridSize;
      pushStyle();
      imageMode(CORNER);
      if(routeGrid[i*10+j]){
        // The grids which are route grids are green; if it's the last grid, red
        fill(80+80*sin(frameCount/(PI*4)),255,80+80*sin(frameCount/(PI*4)));
      }else{
        // The rest of the grids are not filled with color
        noFill();
      }
      rect(gridX,gridY,gridSize,gridSize);
      popStyle();
    }
  }
  for(int i = 0; i < demoTurret.length; i++){
    demoTurret[i].show();
    demoTurret[i].detect();
  }
  for(int i = 0; i < sentEnemy; i++){
    enemy[i].demoShow();
    enemy[i].move();
  }
  popMatrix();
}

public void drawGameplay(){
  for(int g = 0; g < gameSpeed; g++){
    background(0);
    fill(0);
    pushMatrix();
    translate(screenOffsetX,screenOffsetY);
    rect(0,0,gameplayScreenX,gameplayScreenY);
    stroke(255);
    
    // Draw grids
    drawGrids();

    //Turret's actions
    
    for(int i = 0; i < gridCount; i++){ // Scan through each grid because the data of turrets is bound to it
      if(turret[i].builtState){ // Check if there is a turret on the grid
        turret[i].show();
        turret[i].detect();
      }
    }
    
    //Enemy's actions
    if(assaultMode){
      for(int i = 0; i < sentEnemy; i++){ // Command only enemies who are already sent out
        if(enemy[i].state){ // Check if the enemy is alive or not
          enemy[i].show();
          enemy[i].move();
        }
      }
      
      for(int i = 0; i < sentEnemy; i++){
        enemy[i].popShow();
        if(i == targetEnemy && enemy[i].state) enemy[i].showInfoBox();
      }
      
      if((enemy[sentEnemy-1].x>=30 || !enemy[sentEnemy-1].state) && sentEnemy < currentWaveMaxEnemy){ // When the timer is up and there are still enemies not sent out yet in the current wave
        sentEnemy ++; // Add the amount of enemies sent
      }
      if(sentEnemy == currentWaveMaxEnemy){ // Check if there's no more enemy not sent out in the current wave
        if(!enemyCheck() && baseHealth > 0){ // Call the boolean method enemyCheck() to check if all enemies in the current wave are dead
          waveEnd(); // Call the method waveEnd
        }
      }
    }
    if(targetTurretID!=-1){
      rangeIndicate();
    }
    
    popMatrix();
    showUI();
    showPopup();
    realFrameCount++;
    if(pauseState){
      noStroke();
      fill(0,150);
      rect(0,0,width,height);
      previousMode = UIMode;
      UIMode = UI_PAUSE;
    }
    if(baseHealth<=0){
      endFrame = frameCount;
      UIMode = UI_GAMEOVER;
    }
  }
}

public void drawPauseUI(){
  pushStyle();
  fill(0,255,0);
  textFont(font[5]);
  textAlign(CENTER,CENTER);
  text("PAUSED",640,250);
  pauseResume = new Button(520, 360, 240, 50, "RESUME", font[1], CLICKABLE);
  pauseResume.show();
  pauseMainmenu = new Button(520, 470, 240, 50, "MAIN MENU", font[1], CLICKABLE);
  pauseMainmenu.show();
  popStyle();
}

public void drawEndScene(){
  pushStyle();
  if(frameCount-endFrame > 180){
    endMenuState = true;
    String [] randomWord = {"madman", "planet", "seaweed", "theatre", "shadow", "satellite", "coin", "powder", str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-9000,60000)), str(random(-100000,30000)), "RED", "BLU", "GRN", "DED", "LOL", "A", "g", "oW", "DL", "****", "MY", "PIG", "TEAMMATES"};
    textFont(font[floor(random(1,7))]);
    textAlign(CENTER,CENTER);
    textSize(floor(random(5,100)));
    stroke(0,(frameCount-endFrame)-250);
    float trans = min(100,(frameCount-endFrame));
    fill(random(255),random(255),random(255),trans);
    rect(random(-100,width), random(-100,height), random(150), random(150),(frameCount-endFrame));
    fill(random(255),random(255),random(255),trans);
    rect(random(-100,width), random(-100,height), random(150), random(150));
    fill(random(255),random(255),random(255),trans);
    rect(random(-100,width), random(-100,height), random(150), random(150));
    fill(random(255),random(255),random(255),trans);
    ellipse(random(-100,width), random(-100,height), random(150), random(150));
    fill(random(255),random(255),random(255),trans);
    ellipse(random(-100,width), random(-100,height), random(150), random(150));
    fill(random(255),random(255),random(255),trans);
    ellipse(random(-100,width), random(-100,height), random(150), random(150));
    fill(random(255),random(255),random(255),trans);
    text(randomWord[floor(random(randomWord.length))], random(-100,width), random(-100,height));
    fill(random(255),random(255),random(255),trans);
    text(randomWord[floor(random(randomWord.length))], random(-100,width), random(-100,height));
    fill(random(255),random(255),random(255),trans);
    text(randomWord[floor(random(randomWord.length))], random(-100,width), random(-100,height));
    fill(random(255),random(255),random(255),trans);
    text(randomWord[floor(random(randomWord.length))], random(-100,width), random(-100,height));
    fill(random(255),random(255),random(255),trans);
    text(randomWord[floor(random(randomWord.length))], random(-100,width), random(-100,height));
    fill(0,10);
    rect(0,0,width,height);
    endMainmenu = new Button(520, 510, 240, 50, "MAIN MENU", font[1]);
    endRestart = new Button(520, 610, 240, 50, "PLAY AGAIN", font[1]);
    endMainmenu.show();
    endRestart.show();
    colorMode(HSB,360,100,100);
    fill((frameCount*8)%360,100,100,(frameCount-endFrame)-150);
    noStroke();
    float angle = frameCount/(4*PI);
    ellipse(width/2+250*cos(angle*0.5f),height/2+250*sin(angle*0.5f),20,20);
    ellipse(width/2+250*cos(angle*0.5f+PI),height/2+250*sin(angle*0.5f+PI),20,20);
    ellipse(width/2+400*cos(angle),height/2+400*sin(angle),30,30);
    ellipse(width/2+400*cos(angle+PI),height/2+400*sin(angle+PI),30,30);
    ellipse(width/2+500*cos(angle*2),height/2+500*sin(angle*2),40,40);
    ellipse(width/2+500*cos(angle*2+PI),height/2+500*sin(angle*2+PI),40,40);
    textFont(font[6]);
    textSize(100);
    fill(0,0,100,(frameCount-endFrame)-150);
    text("YOU'VE SURVIVED", 640, 220);
    fill((frameCount)%360,100,100,(frameCount-endFrame)-150);
    text("" + (currentWave-1) + " WAVES", 640, 340);
  }
  if(frameCount-endFrame < 300){
    imageMode(CENTER);
    colorMode(HSB,360,100,100);
    noStroke();
    fill(0,0,100,2);
    rect(0,0,width,height);
    tint((frameCount*8)%360,100,100,300-(frameCount-endFrame));
    image(shockwave,turret[lastGrid].x,turret[lastGrid].y,(frameCount-endFrame)*20,(frameCount-endFrame)*20);
  }
  popStyle();
}

// AREA CHECKING METHODS

public boolean mouseCheck(int x, int y, int w, int h){ // Check if the mouse is in the given area data
  return(mouseX > x && mouseX < x + w && mouseY > y&& mouseY < y + h);
}

public boolean mouseCheck(){ // Check if the mouse is in the given area data
  return(mouseX > screenOffsetX && mouseX < screenOffsetX + gameplayScreenX && mouseY > screenOffsetY&& mouseY < screenOffsetY + gameplayScreenY);
}

public boolean mouseCheck(Button buttonName){ // Check if the mouse is in the given area data
  return(mouseX > buttonName.x && mouseX < buttonName.x + buttonName.w && mouseY > buttonName.y&& mouseY < buttonName.y + buttonName.h);
}

public boolean mouseCheck(Enemy enemyName){ // Check if the mouse is in the given area data
  return(dist(mouseX-screenOffsetX,mouseY-screenOffsetY,enemyName.x,enemyName.y) <= enemyName.size/2);
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
  // Result Damage = (Input Damage * (Crit Amplification * Skill Crit Multiplier) * Skill Multiplier + Skill Additional Damage) * Armor Multiplier 
  damage = inputDamage;
  critAmp *= skillCritMultiplier(turretID, critAmp);
  damage *= critAmp;
  damage *= skillDamageMultiplier(turretID,enemyID);
  damage += skillDamageAddition(turretID,enemyID);
  damage -= armorAbsorb(turretID, damage, enemyID, enemy[enemyID].armor);
  //println(enemy[enemyID].armor + "/" + damageMultiplier + "/" + damage);
  return damage;
}

public float calDamage(int turretID, int enemyID, float inputDamage){
  float damage;
  // (NO CRIT) Result Damage = (Input Damage * Skill Multiplier + Skill Additional Damage) * Armor Multiplier 
  damage = inputDamage;
  damage *= skillDamageMultiplier(turretID,enemyID);
  damage += skillDamageAddition(turretID,enemyID);
  damage -= armorAbsorb(turretID, damage, enemyID, enemy[enemyID].armor);
  //println(enemy[enemyID].armor + "/" + damageMultiplier + "/" + damage);
  return damage;
}

public float calDamage(int enemyID, float inputDamage){
  float damage;
  // (DAMAGING BUFF) Result Damage = Input Damage * Armor Multiplier
  damage = inputDamage;
  damage -= armorAbsorb(-1, damage, enemyID, enemy[enemyID].armor);
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
      
    case AURA:
      if(turret[turretID].skillState[0][1]){
        float distance = (dist(turret[turretID].x, turret[turretID].y, enemy[enemyID].x, enemy[enemyID].y) - enemy[enemyID].size/2);
        float maxDistance = (turret[turretID].attackRange * TurretSkillData.AURA_SKILL_A_T2_MAXIMUM_EFFECTIVE_RANGE);
        damageMultiplier += max(0,(1 - ( distance / maxDistance ))) * TurretSkillData.AURA_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER;
      }
      if(turret[turretID].skillState[1][1]){
        damageMultiplier += turret[turretID].auraMeditationCharge;
      }
      if(turret[turretID].skillState[2][1]){
        damageMultiplier += turret[turretID].auraDecrepifyBonus;
      }
      break;
  }
  return damageMultiplier;
}
  
public float armorAbsorb(int turretID, float inputDmg, int targetID, float inputArmor){
  float multiplier = 1;
  if(inputArmor>0){
    if(turretID == -1){
      multiplier *= EnemyData.ARMOR_ABSORB_RATIO;
    }else{
      multiplier *= armorBypass(turretID, EnemyData.ARMOR_ABSORB_RATIO);
    }
    enemy[targetID].hurtArmor(inputDmg*multiplier);
    return inputDmg*multiplier;
  }
  return 0;
}

public float armorBypass(int turretID, float inputRate){
  switch(turret[turretID].turretType){
    case LASER:
      if(turret[turretID].skillState[0][2]){
        inputRate *= TurretSkillData.LASER_SKILL_A_T3_ARMOR_BYPASS_MULTIPLIER;
      }
      break;
    case AURA:
      inputRate = 0;
      break;
  }
  return inputRate;
}

// UI METHODS

public void showUI(){
  UIMode = UIModeChecker();
  coverUI();
  baseHealthUI();
  goldUI();
  fpsUI();
  waveUI();
  timeUI();
  nextUI();
  gameSpeedUI();
  pauseButtonUI();
  if(assaultMode) waveInformationUI();
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

public void coverUI(){
  pushStyle();
  fill(0);
  noStroke();
  rectMode(CORNER);
  rect(0,0,1280,50);
  rect(1200,0,80,800);
  rect(0,651,1280,150);
  popStyle();
}

public void goldUI(){
  textFont(font[1]);
  fill(0xffF7E005);
  text("Gold: " + gold, 70, 700);
}

public void fpsUI(){
  textFont(font[2]);
  fill(255);
  int fps = floor(frameCount*1000/millis());
  text("FPS: " + fps, 15, 33);
}

public void waveUI(){
  textFont(font[2]);
  fill(255);
  text("Current Wave: " + currentWave, 160, 33);
}

public void timeUI(){
  textFont(font[2]);
  fill(255);
  int time = millis()-startTime;
  int s = floor(time/1000)%60;
  int s1 = s%10;
  int s2 = floor(s/10);
  int m = floor(time/60000);
  text("Elapsed Time: " + m + ":" + s2 + s1, 460, 33);
}

public void nextUI(){
  if(!assaultMode){
    nextWave = new Button(730,5,170,40,"Start Wave",CLICKABLE,true);
    nextWave.show();
  }
}

public void pauseButtonUI(){
  pause = new Button(1141,5,80,40,"Pause",CLICKABLE);
  pause.show();
}

public void waveInformationUI(){
  pushStyle();
  textAlign(CENTER,CENTER);
  fill(255,100+100*sin(realFrameCount/(4*PI)),100+100*sin(realFrameCount/(4*PI)));
  textFont(font[5],40);
  textLeading(28);
  text("ASSAULT",1237,-105,25,500);
  textFont(font[5],15);
  for(int i = sentEnemy; i < sentEnemy+14; i++){
    if(i<currentWaveMaxEnemy){
      String showtext = "";
      switch(enemy[i].type){
        case ENEMY_NORMAL:
          showtext = "NORMAL";
          break;
        case ENEMY_FAST:
          showtext = "FAST";
          break;
        case ENEMY_TANK:
          showtext = "TANK";
          break;
        case ENEMY_SUPPORT:
          showtext = "SUPPORT";
          break;
      }
      fill(0,255,0);
      text(showtext, 1251, 230+(i-sentEnemy+1)*29);
    };
  }
  popStyle();
}

public void gameSpeedUI(){
  if(assaultMode){
    textFont(font[2]);
    fill(255);
    text("Game Speed: x" + gameSpeed, 780, 33);
    gameSpeedChange = new Button(1000,5,110,40,"Change",CLICKABLE);
    gameSpeedChange.show();
  }
}

public void baseHealthUI(){
  stroke(0,255,0);
  fill(0,255,0);
  rect(1200,50,20,600);
  fill(0);
  rect(1200,50,20,600*(1-constrain(baseHealth/100,0,1)));
}

public void targetIndicateUI(){
  float m = 10 + 20*sin(frameCount/(PI*4));
  image(targetArrow, turret[targetTurretID].x, turret[targetTurretID].y - m, 40, 50);
}

public void turretBuildUI(){
  if(gold >= TurretLevelData.cannonBuildCost){
    build[0] = new Button (350,660,200,38,"Build Cannon");
  }else{
    build[0] = new Button (350,660,200,38,"Build Cannon",UNCLICKABLE);
  }
  textFont(font[2]);
  fill(255);
  text("Build Cost: " + TurretLevelData.cannonBuildCost, 600, 687);
  build[0].show();
  if(gold >= TurretLevelData.laserBuildCost){
    build[1] = new Button (350,700,200,38,"Build Laser");
  }else{
    build[1] = new Button (350,700,200,38,"Build Laser",UNCLICKABLE);
  }
  textFont(font[2]);
  text("Build Cost: " + TurretLevelData.laserBuildCost, 600, 727);
  build[1].show();
  if(gold >= TurretLevelData.auraBuildCost){
    build[2] = new Button (350,740,200,38,"Build Aura");
  }else{
    build[2] = new Button (350,740,200,38,"Build Aura",UNCLICKABLE);
  }
  textFont(font[2]);
  text("Build Cost: " + TurretLevelData.auraBuildCost, 600, 767);
  build[2].show();
}

public void turretPlacementUI(){
  pushStyle();
  textFont(font[1]);
  colorMode(HSB, 360,100,100);
  fill(frameCount%360,100,100);
  text("Place a turret by mouse", 500, 690);
  if(!routeGrid[mouseOnGrid] && !turret[mouseOnGrid].builtState && mouseCheck()){
    image(targetArrow, turret[mouseOnGrid].x + screenOffsetX, turret[mouseOnGrid].y + screenOffsetY - 30, 40, 50);
  }else if(mouseCheck()){
    rectMode(CENTER);
    noStroke();
    fill(0,100,100);
    rect(turret[mouseOnGrid].x + screenOffsetX, turret[mouseOnGrid].y + screenOffsetY, gridSize, gridSize);
  }
  popStyle();
}

public void turretUpgradeUI(){
  targetIndicateUI();
  textFont(font[1]);
  fill(255);
  text(turret[targetTurretID].turretName, 330, 700);
  
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
  text("Level A: " + turret[targetTurretID].levelA, 670, 690);
  text("Level B: " + turret[targetTurretID].levelB, 670, 730);
  text("Level C: " + turret[targetTurretID].levelC, 670, 770);
  if(turret[targetTurretID].levelA < TurretLevelData.maxLevel){
    if(gold >= turret[targetTurretID].levelAUpgradeCost){
      upgrade[0] = new Button(830,660,120,38,"Upgrade");
    }else{
      upgrade[0] = new Button(830,660,120,38,"Upgrade",UNCLICKABLE);
    }
    upgrade[0].show();
    text("Cost: " + turret[targetTurretID].levelAUpgradeCost, 965, 690);
  }else{
    upgrade[0] = null;
  }
  if(turret[targetTurretID].levelB < TurretLevelData.maxLevel){
    if(gold >= turret[targetTurretID].levelBUpgradeCost){
      upgrade[1] = new Button(830,700,120,38,"Upgrade");
    }else{
      upgrade[1] = new Button(830,700,120,38,"Upgrade",UNCLICKABLE);
    }
    upgrade[1].show();
    text("Cost: " + turret[targetTurretID].levelBUpgradeCost, 965, 730);
  }else{
    upgrade[2] = null;
  }
  
  if(turret[targetTurretID].levelC < TurretLevelData.maxLevel){
    if(gold >= turret[targetTurretID].levelCUpgradeCost){
      upgrade[2] = new Button(830,740,120,38,"Upgrade");
    }else{
      upgrade[2] = new Button(830,740,120,38,"Upgrade",UNCLICKABLE);
    }
    upgrade[2].show();
    text("Cost: " + turret[targetTurretID].levelCUpgradeCost, 965, 770);
  }else{
    upgrade[2] = null;
  }
}

public void turretSkillUI(){
  targetIndicateUI();
  turretSkillLevelIndicateUI();
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
        Button skillDescBox = new Button(skillPurchase[0].x,skillPurchase[0].y-30,PApplet.parseInt(textWidth(turret[targetTurretID].skillDescription[j][i])*0.55f)+40,30,turret[targetTurretID].skillDescription[j][i],font[3]);
        skillDescBox.show();
        if(turret[targetTurretID].skillState[j][i]){
          Button skillCostBox = new Button(skillPurchase[0].x,skillPurchase[0].y-60,90,30,"BOUGHT",font[3],UNCLICKABLE);
          skillCostBox.show();
        }else if(gold < turret[targetTurretID].skillCost[j][i]){
          Button skillCostBox = new Button(skillPurchase[0].x,skillPurchase[0].y-60,90,30,"Cost: " + floor(turret[targetTurretID].skillCost[j][i]),font[3],UNCLICKABLE);
          skillCostBox.show();
        }else{
          Button skillCostBox = new Button(skillPurchase[0].x,skillPurchase[0].y-60,90,30,"Cost: " + floor(turret[targetTurretID].skillCost[j][i]),font[3], CLICKABLE, true);
          skillCostBox.show();
        }
        if(j == 0){
          if(turret[targetTurretID].levelA >= TurretSkillData.MIN_LEVEL[i]){
            Button skillReqBox = new Button(skillPurchase[0].x+90,skillPurchase[0].y-60,240,30,"Level A Requirement: " + floor(TurretSkillData.MIN_LEVEL[i]),font[3], CLICKABLE, true);
            skillReqBox.show();
          }else{
            Button skillReqBox = new Button(skillPurchase[0].x+90,skillPurchase[0].y-60,240,30,"Level A Requirement: " + floor(TurretSkillData.MIN_LEVEL[i]),font[3],UNCLICKABLE);
            skillReqBox.show();
          }
        }else if(j == 1){
          if(turret[targetTurretID].levelB >= TurretSkillData.MIN_LEVEL[i]){
            Button skillReqBox = new Button(skillPurchase[0].x+90,skillPurchase[0].y-60,240,30,"Level B Requirement: " + floor(TurretSkillData.MIN_LEVEL[i]),font[3], CLICKABLE, true);
            skillReqBox.show();
          }else{
            Button skillReqBox = new Button(skillPurchase[0].x+90,skillPurchase[0].y-60,240,30,"Level B Requirement: " + floor(TurretSkillData.MIN_LEVEL[i]),font[3], UNCLICKABLE);
            skillReqBox.show();
          }
        }else if(j == 2){
          if(turret[targetTurretID].levelB >= TurretSkillData.MIN_LEVEL[i]){
            Button skillReqBox = new Button(skillPurchase[0].x+90,skillPurchase[0].y-60,240,30,"Level C Requirement: " + floor(TurretSkillData.MIN_LEVEL[i]),font[3], CLICKABLE, true);
            skillReqBox.show();
          }else{
            Button skillReqBox = new Button(skillPurchase[0].x+90,skillPurchase[0].y-60,240,30,"Level C Requirement: " + floor(TurretSkillData.MIN_LEVEL[i]),font[3], UNCLICKABLE);
            skillReqBox.show();
          }
        }
      }
    }
  }
}

public void turretSkillLevelIndicateUI(){
  pushStyle();
  colorMode(HSB, 360, 100, 100);
  noStroke();
  fill(frameCount%360,100,100);
  rect(340,696,map(turret[targetTurretID].levelA,0,TurretLevelData.maxLevel,0,848),9);
  rect(340,741,map(turret[targetTurretID].levelB,0,TurretLevelData.maxLevel,0,848),9);
  rect(340,786,map(turret[targetTurretID].levelC,0,TurretLevelData.maxLevel,0,848),9);
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
      imageMode(CORNER);
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
          if(assaultMode){
            fill(80+80*sin(frameCount/(PI*4)),255,80+80*sin(frameCount/(PI*4)));
          }else{
            fill(0,200,0);
          }
        }
      }else{
        // The rest of the grids are not filled with color
        noFill();
        if(assaultMode){
          tint(255, 100);
          image(bgTiles[floor(random(20))],gridX,gridY);
        }else{
          tint(255, 40);
          image(bgTiles[(i*j)%20],gridX,gridY);
        }
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

public void enoughGoldIndicate(){
  callPopup("Not Enough Gold", width/2, height*3/4, 1, 26, TEXT_NOMOVE);
}

//

public void gameInit(){ // Game initialization
  skillMenuState = false;
  assaultMode = false;
  gameSpeed = 1;
  realFrameCount = 0;
  startTime = millis();
  difficulty = difficultySelection;
  sentEnemy = 0;
  if(difficulty!=-1) gold = startGold[difficulty];
  baseHealth = baseMaxHealth;
  buildMode = -1;
  targetTurretID = -1;
  if(mapSelection!=-1) currentMap = new mapData(mapSelection); // Load the first data in mapData
  wave = new waveData(); // Initialize the data for waves 
  currentWave = 1; // Set the number of current wave to 1
  if(difficulty!=-1) wave.load(1); //Load the first wave
  for(int i = 0; i < gridCount; i++){ //Initialize each turrets
    turret[i] = new Turret(i);
    turret[i].builtState = false;
    for(int j = 0; j < maxBulletCount; j++){ //Initialize each projectiles
      proj[i][j] = new Projectile();
    }
  }
  for(int i = 1; i < popupTextArray.length; i++){
    popupTextArray[i] = new PopupText();
  }
}

public void waveEnd(){ 
  waveEndGoldBounty(currentWave);
  gameSpeed = 1;
  callPopup("Wave Cleared", PApplet.parseFloat(width/2), PApplet.parseFloat(height/5), 3, 60, TEXT_MOVE);
  assaultMode = false;
  currentWave ++;
}

public void waveEndGoldBounty(int w){
  int amount = max(1,ceil(w*baseHealth/baseMaxHealth));
  addGold(amount);
}

// ENEMY GROWTH METHODS

public float enemyMaxHealthGrowth(int enemyType){
  float mult = pow(0.3f*(currentWave-1),2);
  switch(enemyType){
    case ENEMY_NORMAL:
      return EnemyData.NORMAL_HEALTH_GROWTH[difficulty]*mult;
    case ENEMY_FAST:
      return EnemyData.FAST_HEALTH_GROWTH[difficulty]*mult;
    case ENEMY_TANK:
      return EnemyData.TANK_HEALTH_GROWTH[difficulty]*mult;
    case ENEMY_SUPPORT:
      return EnemyData.SUPPORT_HEALTH_GROWTH[difficulty]*mult;
  }
  return 0;
}

public float enemyArmorGrowth(int enemyType){
  float mult = pow(0.3f*(currentWave-1),2);
  switch(enemyType){
    case ENEMY_NORMAL:
      return EnemyData.NORMAL_ARMOR_GROWTH[difficulty]*mult;
    case ENEMY_FAST:
      return EnemyData.FAST_ARMOR_GROWTH[difficulty]*mult;
    case ENEMY_TANK:
      return EnemyData.TANK_ARMOR_GROWTH[difficulty]*mult;
    case ENEMY_SUPPORT:
      return EnemyData.SUPPORT_ARMOR_GROWTH[difficulty]*mult;
  }
  return 0;
}

public float enemySpeedGrowth(int enemyType){
  switch(enemyType){
    case ENEMY_NORMAL:
      return EnemyData.NORMAL_SPEED_GROWTH[difficulty]*(currentWave-1);
    case ENEMY_FAST:
      return EnemyData.FAST_SPEED_GROWTH[difficulty]*(currentWave-1);
    case ENEMY_TANK:
      return EnemyData.TANK_SPEED_GROWTH[difficulty]*(currentWave-1);
    case ENEMY_SUPPORT:
      return EnemyData.SUPPORT_SPEED_GROWTH[difficulty]*(currentWave-1);
  }
  return 0;
}

public int enemyBountyGrowth(int enemyType){
  switch(enemyType){
    case ENEMY_NORMAL:
      return floor(EnemyData.NORMAL_BOUNTY_GROWTH[difficulty]*(currentWave-1));
    case ENEMY_FAST:
      return floor(EnemyData.FAST_BOUNTY_GROWTH[difficulty]*(currentWave-1));
    case ENEMY_TANK:
      return floor(EnemyData.TANK_BOUNTY_GROWTH[difficulty]*(currentWave-1));
    case ENEMY_SUPPORT:
      return floor(EnemyData.SUPPORT_BOUNTY_GROWTH[difficulty]*(currentWave-1));
  }
  return 0;
}

// UTILITY METHODS

public void callPopup(String showText, float startX, float startY, int textColor, int fontSize, int moveMode){
  for(int i = 1; i < popupTextArray.length; i++){
    if(!popupTextArray[i].state){
      popupTextArray[i] = new PopupText(showText, startX, startY, textColor, fontSize, moveMode);
      break;
    }
  }
}

public void showPopup(){
  for(int i = 1; i < popupTextArray.length; i++){
    if(popupTextArray[i].state){
      popupTextArray[i].show();
    }
  }
}

public void addGold(int amount){
  gold += amount;
  callPopup("+" + amount, 110 + screenOffsetX, 605 + screenOffsetY, 0, 45, TEXT_MOVE);
}

public void spendGold(int amount){
  gold -= amount;
  callPopup("-" + amount, 110 + screenOffsetX, 605 + screenOffsetY, 1, 45, TEXT_MOVE);
}

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
  addGold(100);
  //for(int i = 0; i < sentEnemy; i++){
  //  enemy[i].speed *= 0.5;
  //}
  //noLoop();
  //if(mouseX>mouseY) loop();
}

public void mousePressed(){
  targetEnemy = -1;
}

public void mouseReleased(){
  switch(UIMode){
    case UI_MAINMENU:
      if(mouseCheck(startGame.x,startGame.y,startGame.w,startGame.h) && startGame.showState == CLICKABLE){
        gameInit();
        UIMode = UI_BUILD;
      }
      for(int i = 0; i < diffSelect.length; i++){
        if(mouseCheck(diffSelect[i].x,diffSelect[i].y,diffSelect[i].w,diffSelect[i].h)){
          difficultySelection = i;
        }
      }
      for(int i = 0; i < mapSelect.length; i++){
        if(mouseCheck(mapSelect[i].x,mapSelect[i].y,mapSelect[i].w,mapSelect[i].h)){
          mapSelection = i+1;
        }
      }
      break;
    
    case UI_BUILD:
      universalUICheck();
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
          enoughGoldIndicate();
        }
      }else if(mouseCheck(build[1].x,build[1].y,build[1].w,build[1].h)){
        if(gold >= TurretLevelData.laserBuildCost){
          buildMode = 1;
          buildCost = TurretLevelData.laserBuildCost;
        }else{
          enoughGoldIndicate();
        }
      }else if(mouseCheck(build[2].x,build[2].y,build[2].w,build[2].h)){
        if(gold >= TurretLevelData.auraBuildCost){
          buildMode = 2;
          buildCost = TurretLevelData.auraBuildCost;
        }else{
          enoughGoldIndicate();
        }
      }else{
        mouseActionOnCancelSelect();
      }
      break;
      
    case UI_PLACEMENT:
      universalUICheck();
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
        spendGold(buildCost);
      }else{
        buildMode = -1;
      }
      break;
      
    case UI_UPGRADE:
      universalUICheck();
      if(mouseCheckOnTurret()){
        targetTurretID = mouseOnGrid;
        buildMode = -1;
        break;
      }
      if(upgrade[0]!=null && mouseCheck(upgrade[0].x,upgrade[0].y,upgrade[0].w,upgrade[0].h) && turret[targetTurretID].levelA < TurretLevelData.maxLevel){
        if(gold >= turret[targetTurretID].levelAUpgradeCost){
          spendGold(turret[targetTurretID].levelAUpgradeCost);
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
          enoughGoldIndicate();
        }
      }else if(upgrade[1]!=null && mouseCheck(upgrade[1].x,upgrade[1].y,upgrade[1].w,upgrade[1].h) && turret[targetTurretID].levelB < TurretLevelData.maxLevel){
        if(gold >= turret[targetTurretID].levelBUpgradeCost){
          spendGold(turret[targetTurretID].levelBUpgradeCost);
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
          enoughGoldIndicate();
        }
      }else if(upgrade[2]!=null && mouseCheck(upgrade[2].x,upgrade[2].y,upgrade[2].w,upgrade[2].h) && turret[targetTurretID].levelC < TurretLevelData.maxLevel){
        if(gold >= turret[targetTurretID].levelCUpgradeCost){
          spendGold(turret[targetTurretID].levelCUpgradeCost);
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
          enoughGoldIndicate();
        }
      }else if(mouseCheck(sell.x,sell.y,sell.w,sell.h)){
        addGold(turret[targetTurretID].sellPrice);
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
      universalUICheck();
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
                spendGold(turret[targetTurretID].skillCost[i][j]);
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
    case UI_PAUSE:
      if(mouseCheck(pauseResume.x,pauseResume.y,pauseResume.w,pauseResume.h)){
        pauseState = false;
        UIMode = previousMode;
      }
      if(mouseCheck(pauseMainmenu.x,pauseMainmenu.y,pauseMainmenu.w,pauseMainmenu.h)){
        pauseState = false;
        UIMode = UI_MAINMENU;
        menuInit();
      }
      break;
    case UI_GAMEOVER:
      if(endMenuState){
        if(mouseCheck(endMainmenu)){
          pauseState = false;
          UIMode = UI_MAINMENU;
          menuInit();
        }
        if(mouseCheck(endRestart)){
          gameInit();
          UIMode = UI_BUILD;
        }
      }
      break;
  }
}

public void universalUICheck(){
  if(assaultMode && mouseCheck(gameSpeedChange.x,gameSpeedChange.y,gameSpeedChange.w,gameSpeedChange.h)){
    if(gameSpeed == 1){
      gameSpeed = 2;
    }else if(gameSpeed == 2){
      gameSpeed = 4;
    }else if(gameSpeed == 4){
      gameSpeed = 1;
    }
  }
  if(!assaultMode && mouseCheck(nextWave.x,nextWave.y,nextWave.w,nextWave.h)){
    assaultMode = true;
    sentEnemy = 1;
    wave.load(currentWave);
  }
  if(mouseCheck(pause.x,pause.y,pause.w,pause.h)){
    pauseState = true;
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
    "Acid Infusion", // 0
    "Cold Snap",
    "Ionic Shell",
    "Crippled",
    "DNA Inflation", 
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
class Button{
  int w;
  int h;
  int x;
  int y;
  PFont fontType;
  int showState;
  boolean noticeMe;
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
      if(noticeMe){
        colorMode(HSB, 360, 100, 100);
        fill(frameCount*8%360,30,100);
      }else{
        fill(255);
      }
    }
    //strokeWeight(5);
    //stroke(0);
    noStroke();
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
  
  Button(int x, int y, int w, int h, String word, int clickstate, boolean notice){
    this.w = w;
    this.h = h;
    this.x = x;
    this.y = y;
    this.word = word;
    this.fontType = font[2];
    noticeMe = notice;
    showState = clickstate;
  }
  
  Button(int x, int y, int w, int h, String word, PFont font, int clickstate, boolean notice){
    this.w = w;
    this.h = h;
    this.x = x;
    this.y = y;
    this.word = word;
    this.fontType = font;
    noticeMe = notice;
    showState = clickstate;
  }
  
}
class Enemy{
  final int MOVE_U = 1;
  final int MOVE_D = 2;
  final int MOVE_L = 3;
  final int MOVE_R = 4;
  
  String enemyName;
  boolean state;
  boolean drawHurt;
  boolean demoMode;
  boolean buffState[] = new boolean [BuffData.BUFF_COUNT];
  float buffTimer[] = new float [BuffData.BUFF_COUNT];
  float buffData1[] = new float [BuffData.BUFF_COUNT];
  float buffData2[] = new float [BuffData.BUFF_COUNT];
  float hurtShowDmg;
  float size;
  float maxHealth;
  float health;
  float maxArmor;
  float armor;
  float armorRegenRate;
  int armorRegenDelay;
  float power;
  float x;
  float y;
  float speed;
  int lastHitTime;
  int maimTime;
  int ID;
  int type;
  int enemyColor;
  int OnGrid, OnGridX, OnGridY;
  int moveDir;
  int bounty;
  float buffRange;
  
  PopupText dmgPopup = new PopupText();
  PopupText critPopup = new PopupText();
  
  public void show(){
    endCheck();
    checkBuffValidity();
    loadStat();
    applyBuffEffect();
    healthCheck();
    if(type == ENEMY_SUPPORT) supportBuffAura();
    stroke(0);
    ellipseMode(CENTER);
    fill(enemyColor);
    ellipse(x,y,size,size);
    if(drawHurt){
      //pushStyle();
      //textAlign(CENTER,CENTER);
      //fill(255);
      //textFont(font[3]);
      //text(hurtShowDmg,x,y-40);
      //popStyle();
      fill(255,255,255,200);
      ellipse(x,y,size,size);
      drawHurt = false;
    }
    healthBar();
    armorRegenCheck();
    if(armor!=0) armorBar();
    if(mouseCheck(this) && mousePressed){
        targetEnemy = ID;
    }
  }
  
  public void showInfoBox(){
    infoIndicateUI();
    pushMatrix();
    pushStyle();
    int startOffsetX = 3;
    int startOffsetY = 5;
    int columnHeight = 30;
    int columnWidth = 200;
    noStroke();
    translate(1000,1);
    fill(0,180);
    rect(0,0,columnWidth,columnHeight*4);
    textFont(font[3]);
    textSize(28);
    textAlign(LEFT,TOP);
    fill(255);
    text("Type: " + enemyName,startOffsetX,startOffsetY + columnHeight*0);
    text("Health: " + ceil(health),startOffsetX,startOffsetY + columnHeight*1);
    text("Armor: " + ceil(armor),startOffsetX,startOffsetY + columnHeight*2);
    text("Speed: " + PApplet.parseFloat(round(speed*100))/100,startOffsetX,startOffsetY + columnHeight*3);
    textAlign(CENTER,CENTER);
    float showBuffCount = 0;
    for(int i = 0; i < buffState.length; i++){
      if(buffState[i]){
        fill(0,180);
        rect(0,columnHeight*4+showBuffCount*columnHeight,columnWidth,columnHeight);
        if(BuffData.BUFF_TYPE[i] == BUFF){
          fill(0,0,255,180);
        }else{
          fill(255,0,0,180);
        }
        rect(0,columnHeight*4+showBuffCount*columnHeight,columnWidth*(buffTimer[i]-realFrameCount)/BuffData.BUFF_DURATION[i],columnHeight);
        fill(255);
        text(BuffData.BUFF_NAME[i],columnWidth/2,columnHeight*4+(showBuffCount+0.5f)*columnHeight);
        showBuffCount++;
      }
    }
    popStyle();
    popMatrix();
  }
  
  public void infoIndicateUI(){
    pushStyle();
    float m = 30 + 8*sin(frameCount/(PI*4));
    tint(255,0,0);
    image(targetArrow, x, y - m, 30, 45);
    popStyle();
  }
  
  public void demoShow(){
    if(type == ENEMY_SUPPORT) supportBuffAura();
    stroke(0);
    ellipseMode(CENTER);
    fill(enemyColor);
    ellipse(x,y,size,size);
    if(drawHurt){
      fill(255,255,255,200);
      ellipse(x,y,size,size);
      drawHurt = false;
    }
  }
  
  public void popShow(){
    if(dmgPopup.state) dmgPopup.show();
    if(critPopup.state) critPopup.show();
  }
  
  public void healthBar(){
    //pushStyle();
    //textAlign(CENTER,CENTER);
    //fill(255);
    //textFont(font[3]);
    //text(ceil(health),x,y-6);
    //popStyle();
    stroke(0);
    fill(255,0,0);
    rect(x-20,y-20,40,4);
    fill(0,255,0);
    rect(x-20,y-20,40*(health/maxHealth),4);
  }
  
  public void armorBar(){
    //pushStyle();
    //textAlign(CENTER,CENTER);
    //fill(100,100,255);
    //textFont(font[3]);
    //text(ceil(armor),x,y+10);
    //popStyle();
    stroke(0);
    fill(255,0,0);
    rect(x-20,y-25,40,4);
    fill(0,0,255);
    rect(x-20,y-25,40*(armor/maxArmor),4);
  }
  
  public void armorRegenCheck(){
    int delayTime = armorRegenDelay;
    if(buffState[17]) lastHitTime++;
    if(lastHitTime + delayTime < realFrameCount && armor < maxArmor){
      armor += (maxArmor-armor)*armorRegenRate;
    }
  }
  
  public void critPop(){
    critPopup = new PopupText("CRIT", x, y-25, 3, 20, TEXT_NOMOVE);
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
      if(moveDir == MOVE_R && x >= OnGridX*gridSize + 30 && (OnGrid + 10 > routeGrid.length || !routeGrid[OnGrid+10])){
        if(!routeGrid[OnGrid+1]){
          moveDir = MOVE_U;
        }else{
          moveDir = MOVE_D;
        }
      }
      if(moveDir == MOVE_U && y <= OnGridY*gridSize + 30 && (OnGrid - 1 < 0 || !routeGrid[OnGrid-1])){
        if(!routeGrid[OnGrid+10]){
          moveDir = MOVE_L;
        }else{
          moveDir = MOVE_R;
        }
      }
      if(moveDir == MOVE_L && x <= OnGridX*gridSize + 30 && (OnGrid - 10 < 0 || !routeGrid[OnGrid-10])){
        if(!routeGrid[OnGrid+1]){
          moveDir = MOVE_U;
        }else{
          moveDir = MOVE_D;
        }
      }
      if(moveDir == MOVE_D && y >= OnGridY*gridSize + 30 && (OnGrid + 1 > routeGrid.length || !routeGrid[OnGrid+1])){
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
      if(buffState[11]) bloodlustEffect();
      init();
      addGold(bounty);
    }
  }
  
  public void endCheck(){
    if(OnGrid == lastGrid){
      baseHealth -= power;
      callPopup("-" + power, x, y, 0, 30, TEXT_MOVE);
      init();
    }
  }
  
  public void hurt(float damage){
    lastHitTime = realFrameCount;
    damage *= damageMultiplier();
    health -= damage;
    if(buffState[16]) fatalEffect(damage);
    damagePop(damage);
    //println(ID + " / " + damage);
    drawHurt = true;
  }
  
  public void fatalHurt(float damage){
    lastHitTime = realFrameCount;
    damage *= damageMultiplier();
    health -= damage;
    damagePop(damage);
    //println(ID + " / " + damage);
    drawHurt = true;
  }
  
  public void hurtArmor(float damage){
    lastHitTime = realFrameCount;
    damage *= armorDamageMultiplier();
    armor -= damage;
    armor = max(0,armor);
  }
  
  public void damagePop(float damage){
    dmgPopup = new PopupText("" + PApplet.parseFloat(round(damage*10))/10, x, y-10, 1, 22, TEXT_NOMOVE);
  }
  
  public void applyBuffEffect(){
    // Group: Armor
    
    // Group: Speed
    speed *= speedMultiplier();
    speed = max(speed,EnemyData.MIN_SPEED);
    
    // Size
    size += sizeAddition();
    
    //Armor
    
    armorRegenRate *= armorRegenMultiplier();
    armorRegenDelay *= armorRegenDelayMultiplier();
    
    // Group: Others
    if(buffState[2]) ionicEffect();
    //if(buffState[7]) debuffIndicate(x,y,TurretSkillData.LASER_SKILL_C_T4_RADIUS,20,(buffTimer[7]-realFrameCount),TurretSkillData.LASER_SKILL_C_T4_DURATION);
    if(buffState[10]) cancerEffect();
    if(buffState[12]) jinxEffect();
    //if(buffState[15]){
    //  debuffIndicate(x,y,100,100,(buffTimer[15]-realFrameCount),TurretSkillData.AURA_SKILL_C_T4_DURATION);
    //}
    //if(buffState[17]){
    //  debuffIndicate(x,y,100,100,(buffTimer[17]-realFrameCount),TurretSkillData.AURA_SKILL_B_T4_DURATION);
    //}
    if(buffState[20] && !buffState[17] && health < maxHealth){
      health += (maxHealth - health) * EnemyData.BUFF_HEALTH_REGEN_RATE;
    }
  }
  
  public void checkBuffValidity(){
    for(int i = 0; i < buffState.length; i++){
      if(buffState[i] && buffTimer[i] == realFrameCount){
        buffState[i] = false;
        buffData1[i] = 0;
        buffData2[i] = 0;
      }
    }
  }
  
  public void getBuff(int buffID, float duration){
    buffState[buffID] = true;
    buffTimer[buffID] = realFrameCount + duration;
  }
  
  public void getBuff(int buffID, float duration, int exception){
    if(exception == 0){
      if(!buffState[buffID]){
        buffState[buffID] = true;
        buffTimer[buffID] = realFrameCount + duration;
        maimTime += 1;
      }
    }else{
      buffTimer[buffID] = realFrameCount + duration;
      buffState[buffID] = true;
    }
  }
  
  public void getBuff(int buffID, float duration, float data1, float data2){
    buffState[buffID] = true;
    buffTimer[buffID] = realFrameCount + duration;
    buffDataProcess1(buffID, data1);
    buffDataProcess2(buffID, data2);
  }
  
  public void buffDataProcess1(int ID, float data){
    switch(ID){
      case 2:
        buffData1[ID] += data;
        break;
      case 4:
        if(buffData1[ID] == 0) buffData1[ID] = realFrameCount;
        break;
      case 10:
        if(buffData1[ID] < TurretSkillData.AURA_SKILL_A_T5_STACK_CAP) buffData1[ID] += data;
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
      case 4:
        if(buffData2[ID]!=0) break;
        buffData2[ID] = data;
        break;
      default:
        buffData2[ID] = data;
        break;
    }
  }
  
  public float armorDamageMultiplier(){
    float multiplier = 1;
    if(buffState[0]){
      multiplier += TurretSkillData.CANNON_SKILL_A_T3_ARMOR_DAMAGE_MULTIPLIER;
      //debuffIndicate(x,y,40,20);
    }
    if(buffState[18] && !buffState[17]){
      multiplier -= EnemyData.BUFF_FORTIFIED_MULTIPLIER;
    }
    return multiplier;
  }
  
  public float armorRegenMultiplier(){
    float multiplier = 1;
    if(buffState[22] && !buffState[17]){
      multiplier += EnemyData.BUFF_WEAVE_MULTIPLIER;
    }
    return multiplier;
  }
  
  public float armorRegenDelayMultiplier(){
    float multiplier = 1;
    if(buffState[22] && !buffState[17]){
      multiplier -= EnemyData.BUFF_WEAVE_DELAY_REDUCTION;
    }
    return multiplier;
  }
  
  public float damageMultiplier(){
    float multiplier = 1;
    if(buffState[4]){
      multiplier += TurretSkillData.LASER_SKILL_B_T3_EXTRA_DAMAGE_MULTIPLER * (buffData2[4]/TurretSkillData.LASER_SKILL_B_T3_MAX_INFLATION_AMOUNT);
    }
    if(buffState[21] && !buffState[17]){
      multiplier -= EnemyData.BUFF_TOUGH_SKIN_MULTIPLIER;
    }
    return multiplier;
  }
  
  public float speedMultiplier(){
    float multiplier = 1;
    if(buffState[1]){
      multiplier -= TurretSkillData.CANNON_SKILL_C_T1_SLOW_PERCENTAGE;
      //debuffIndicate(x,y,100,50,(buffTimer[1]-realFrameCount),TurretSkillData.CANNON_SKILL_C_T1_DURATION);
    }
    if(buffState[3]){
      multiplier -= max(TurretSkillData.CANNON_SKILL_C_T5_MIN_SLOW_PERCENTAGE,pow((TurretSkillData.CANNON_SKILL_C_T5_SLOW_PERCENTAGE),maimTime));
      //debuffIndicate(x,y,100,100,(buffTimer[3]-realFrameCount),TurretSkillData.CANNON_SKILL_C_T5_DURATION);
    }
    if(buffState[5]){
      multiplier -= TurretSkillData.LASER_SKILL_C_T2_SLOW_PERCENTAGE;
      //debuffIndicate(x,y,100,100,(buffTimer[5]-realFrameCount),TurretSkillData.LASER_SKILL_C_T2_DURATION);
    }
    if(buffState[6]){
      multiplier -= map(health,maxHealth,0,0,TurretSkillData.LASER_SKILL_C_T5_MAXIMUM_SLOW_PERCENTAGE);
      //debuffIndicate(x,y,100,100,(buffTimer[6]-realFrameCount),TurretSkillData.LASER_SKILL_C_T5_DURATION);
    }
    if(buffState[13]){
      multiplier -= TurretSkillData.AURA_SKILL_C_T1_SLOW_PERCENTAGE;
      //debuffIndicate(x,y,100,100,(buffTimer[13]-realFrameCount),TurretSkillData.AURA_SKILL_C_T1_DURATION);
    }
    if(buffState[14]){
      multiplier -= TurretSkillData.AURA_SKILL_C_T3_SLOW_PERCENTAGE;
      //debuffIndicate(x,y,100,100,(buffTimer[14]-realFrameCount),TurretSkillData.AURA_SKILL_C_T3_SLOW_DURATION);
    }
    if(buffState[19] && !buffState[17]){
      multiplier += EnemyData.BUFF_HASTE_MULTIPLIER;
    }
    //println(multiplier);
    return constrain(multiplier,0,10);
  }
  
  public float sizeAddition(){
    float addition = 0;
    if(buffState[4]){
      addition = (realFrameCount-buffData1[4])/60*TurretSkillData.LASER_SKILL_B_T3_SIZE_INFLATION_AMOUNT_PER_SEC;
      addition = min(addition,TurretSkillData.LASER_SKILL_B_T3_MAX_INFLATION_AMOUNT);
      buffData2[4] = addition;
    }
    return addition;
  }
  
  public void ionicEffect(){
    float dmg = buffData1[2] * TurretSkillData.CANNON_SKILL_C_T2_BASE_CANNON_DAMAGE_PERCENTAGE;
    if( (buffTimer[2] - realFrameCount) % TurretSkillData.CANNON_SKILL_C_T2_DAMAGE_INTERVAL == 0){
      for(int i = 0; i < sentEnemy; i++){
        if(i != ID && dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= TurretSkillData.CANNON_SKILL_C_T2_RADIUS){
            enemy[i].hurt(calDamage(i, dmg));
        }
      }
    }
    //debuffIndicate(x,y,TurretSkillData.CANNON_SKILL_C_T2_RADIUS*2,20*min(buffData2[2],6),(buffTimer[2]-realFrameCount),TurretSkillData.CANNON_SKILL_C_T2_DURATION);
  }
  
  public void volatileEffect(){
    float dmg = maxHealth * TurretSkillData.LASER_SKILL_C_T4_MAX_HEALTH_PERCENTAGE_AS_DAMAGE;
    for(int i = 0; i < sentEnemy; i++){
      if(i != ID && dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= TurretSkillData.LASER_SKILL_C_T4_RADIUS){
          enemy[i].hurt(calDamage(i, dmg));
      }
    }
    //debuffIndicate(x,y,TurretSkillData.LASER_SKILL_C_T4_RADIUS*2,120);
  }
  
  public void cancerEffect(){
    float dmg = buffData2[10] * (TurretSkillData.AURA_SKILL_A_T5_BASE_DAMAGE_PERCENTAGE * pow(2,buffData1[10]/3));
    if((buffTimer[10] - realFrameCount) % TurretSkillData.AURA_SKILL_A_T5_DAMAGE_INTERVAL == 0){
      hurt(dmg);
    }
    //debuffIndicate(x,y,70,5*buffData1[10],(buffTimer[10]-realFrameCount),TurretSkillData.AURA_SKILL_A_T5_DURATION);
  }
  
  public void bloodlustEffect(){
    for(int i = 0; i < turret.length; i++){
      if(turret[i].turretType == AURA && turret[i].skillState[1][2] && turret[i].critMode) turret[i].critDurationCounter = turret[i].critDuration;
    }
  }
  
  public void jinxEffect(){
    if(health > 0 && health < maxHealth * TurretSkillData.AURA_SKILL_B_T5_HEALTH_THRESHOLD) hurt(health*10);
  }
  
  public void fatalEffect(float dmg){
    for(int i = 0; i < sentEnemy; i++){
      if(enemy[i].buffState[16]) enemy[i].fatalHurt(dmg*TurretSkillData.AURA_SKILL_C_T5_DAMAGE_SHARE_PERCENTAGE);
    }
  }
  
  public void supportBuffAura(){
    int [] affectedIDList = new int [0];
    for(int i = 0; i < sentEnemy; i++){
      if(dist(x,y,enemy[i].x,enemy[i].y) - enemy[i].size <= buffRange){
        if(i != ID) affectedIDList = splice(affectedIDList, i, affectedIDList.length);
        enemy[i].getBuff(18,3);
        enemy[i].getBuff(19,3);
        enemy[i].getBuff(20,3);
        enemy[i].getBuff(21,3);
        enemy[i].getBuff(22,3);
      }
    }
    supportDynamicSpeed(affectedIDList);
    pushStyle();
    noStroke();
    fill(0xff0CEBF5, 40);
    ellipse(x,y,buffRange*2,buffRange*2);
    popStyle();
  }
  
  public void supportDynamicSpeed(int [] IDList){
    float speedSum = 0;
    for(int i = 0; i < IDList.length; i++){
      speedSum += enemy[IDList[i]].speed;
    }
    speedSum /= IDList.length;
    if(speedSum > 0) speed = speedSum;
  }
  
  public void loadStat(){
    switch(type){
      case 1: //normal
        enemyName = "Normal";
        enemyColor = EnemyData.NORMAL_COLOR;
        size = EnemyData.NORMAL_SIZE;
        maxHealth = EnemyData.NORMAL_MAX_HEALTH[difficulty];
        power = EnemyData.NORMAL_POWER[difficulty];
        maxArmor = EnemyData.NORMAL_MAX_ARMOR[difficulty];
        armorRegenRate = EnemyData.NORMAL_ARMOR_REGEN_RATE[difficulty];
        speed = EnemyData.NORMAL_SPEED[difficulty];
        bounty = EnemyData.NORMAL_BOUNTY[difficulty];
        break;
        
      case 2: //fast
        enemyName = "Fast";
        enemyColor = EnemyData.FAST_COLOR;
        size = EnemyData.FAST_SIZE;
        maxHealth = EnemyData.FAST_MAX_HEALTH[difficulty];
        power = EnemyData.FAST_POWER[difficulty];
        maxArmor = EnemyData.FAST_MAX_ARMOR[difficulty];
        armorRegenRate = EnemyData.FAST_ARMOR_REGEN_RATE[difficulty];
        speed = EnemyData.FAST_SPEED[difficulty];
        bounty = EnemyData.FAST_BOUNTY[difficulty];
        break;
        
      case 3: //tank
        enemyName = "Tank";
        enemyColor = EnemyData.TANK_COLOR;
        size = EnemyData.TANK_SIZE;
        maxHealth = EnemyData.TANK_MAX_HEALTH[difficulty];
        power = EnemyData.TANK_POWER[difficulty];
        maxArmor = EnemyData.TANK_MAX_ARMOR[difficulty];
        armorRegenRate = EnemyData.TANK_ARMOR_REGEN_RATE[difficulty];
        speed = EnemyData.TANK_SPEED[difficulty];
        bounty = EnemyData.TANK_BOUNTY[difficulty];
        break;
        
      case 4: //support
        enemyName = "Support";
        enemyColor = EnemyData.SUPPORT_COLOR;
        size = EnemyData.SUPPORT_SIZE;
        maxHealth = EnemyData.SUPPORT_MAX_HEALTH[difficulty];
        power = EnemyData.SUPPORT_POWER[difficulty];
        maxArmor = EnemyData.SUPPORT_MAX_ARMOR[difficulty];
        armorRegenRate = EnemyData.SUPPORT_ARMOR_REGEN_RATE[difficulty];
        speed = EnemyData.SUPPORT_SPEED[difficulty];
        bounty = EnemyData.SUPPORT_BOUNTY[difficulty];
        buffRange = EnemyData.SUPPORT_BUFF_RANGE[difficulty];
        break;
    }
    armorRegenDelay = EnemyData.ARMOR_REGEN_DELAY[difficulty];
    maxHealth += enemyMaxHealthGrowth(type);
    maxArmor += enemyArmorGrowth(type);
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
    maimTime = 0;
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
    armor = maxArmor;
    drawHurt = false;
    state = true;
    demoMode = false;
    moveDir = MOVE_R;
    this.x = startpointX;
    this.y = startpointY;
  }
  
  Enemy(int enemyType, int ID, boolean demo){
    this.ID = ID;
    type = enemyType;
    demoMode = demo;
    loadStat();
    health = maxHealth;
    armor = maxArmor;
    drawHurt = false;
    state = true;
    moveDir = MOVE_R;
    x = ID * gridSize + 30;
    y = 30;
  }
}
static class EnemyData{
  
  //Global Attributes
  
  static float MIN_SPEED = 0.05f;
  static float ARMOR_ABSORB_RATIO = 0.75f;
  static int [] ARMOR_REGEN_DELAY = {600,300,180};
  
  static float BUFF_FORTIFIED_MULTIPLIER = 0.3f;
  static float BUFF_HASTE_MULTIPLIER = 0.5f;
  static float BUFF_HEALTH_REGEN_RATE = 0.002f;
  static float BUFF_TOUGH_SKIN_MULTIPLIER = 0.3f;
  static float BUFF_WEAVE_MULTIPLIER = 1;
  static float BUFF_WEAVE_DELAY_REDUCTION = 0.5f;
  
  //Normal
  
  static int NORMAL_COLOR = 0xffFF0000;
  static float NORMAL_SIZE = 40;
  static float [] NORMAL_MAX_HEALTH = {150,200,300};
  static float [] NORMAL_POWER = {10,10,40};
  static float [] NORMAL_MAX_ARMOR = {25,50,100};
  static float [] NORMAL_ARMOR_REGEN_RATE = {0,0.002f,0.005f};
  static float [] NORMAL_SPEED = {0.8f,1.0f,1.5f};
  static int [] NORMAL_BOUNTY = {3,3,2};
  static float [] NORMAL_HEALTH_GROWTH = {150,200,300};
  static float [] NORMAL_ARMOR_GROWTH = {15,20,30};
  static float [] NORMAL_SPEED_GROWTH = {0.02f,0.03f,0.04f};
  static float [] NORMAL_BOUNTY_GROWTH = {0.6f,0.5f,0.4f};
  
  //Fast
  
  static int FAST_COLOR = 0xffFAA112;
  static float FAST_SIZE = 20;
  static float [] FAST_MAX_HEALTH = {75,100,150};
  static float [] FAST_POWER = {5,5,30};
  static float [] FAST_MAX_ARMOR = {15,25,100};
  static float [] FAST_ARMOR_REGEN_RATE = {0,0.008f,0.1f};
  static float [] FAST_SPEED = {1.2f,1.5f,1.8f};
  static int [] FAST_BOUNTY = {3,2,2};
  static float [] FAST_HEALTH_GROWTH = {80,120,200};
  static float [] FAST_ARMOR_GROWTH = {10,20,40};
  static float [] FAST_SPEED_GROWTH = {0.03f,0.04f,0.06f};
  static float [] FAST_BOUNTY_GROWTH = {0.3f,0.25f,0.2f};
  
  //Tank
  
  static int TANK_COLOR = 0xff110F52;
  static float TANK_SIZE = 55;
  static float [] TANK_MAX_HEALTH = {1800,2000,3000};
  static float [] TANK_POWER = {15,20,50};
  static float [] TANK_MAX_ARMOR = {200,600,1200};
  static float [] TANK_ARMOR_REGEN_RATE = {0,0.001f,0.003f};
  static float [] TANK_SPEED = {0.5f,0.6f,0.9f};
  static int [] TANK_BOUNTY = {60,40,30};
  static float [] TANK_HEALTH_GROWTH = {2000,3000,4000};
  static float [] TANK_ARMOR_GROWTH = {350,500,750};
  static float [] TANK_SPEED_GROWTH = {0.02f,0.02f,0.03f};
  static float [] TANK_BOUNTY_GROWTH = {4,4,3};
  
  //Support
  
  static int SUPPORT_COLOR = 0xff1BE3F7;
  static float SUPPORT_SIZE = 30;
  static float [] SUPPORT_MAX_HEALTH = {100,150,350};
  static float [] SUPPORT_POWER = {10,15,35};
  static float [] SUPPORT_MAX_ARMOR = {10,20,100};
  static float [] SUPPORT_ARMOR_REGEN_RATE = {0,0,0.002f};
  static float [] SUPPORT_SPEED = {1,1.2f,1.5f};
  static int [] SUPPORT_BOUNTY = {20,10,10};
  static float [] SUPPORT_BUFF_RANGE = {100,175,250};
  static float [] SUPPORT_HEALTH_GROWTH = {100,150,250};
  static float [] SUPPORT_ARMOR_GROWTH = {3,5,15};
  static float [] SUPPORT_SPEED_GROWTH = {0.02f,0.03f,0.05f};
  static float [] SUPPORT_BOUNTY_GROWTH = {3,2,2};

}
class PopupText{
  boolean state;
  String showText;
  int fontSize;
  int startFrame;
  int showTime;
  int moveMode;
  float alpha;
  float speed;
  float startX;
  float startY;
  float destY;
  float x;
  float y;
  int textColor;
  
  public void show(){
    pushStyle();
    textAlign(CENTER, CENTER);
    alpha = 255*(1 - 0.2f*(y - destY)/30 - max(0,(realFrameCount - (startFrame + 0.5f*showTime))/showTime)*2);
    switch(textColor){
      case COLOR_WHITE:
        fill(255,alpha);
        break;
        
      case COLOR_RED:
        fill(255,0,0,alpha);
        break;
      
      case COLOR_GREEN:
        fill(0,0,255,alpha);
        break;
        
      case COLOR_RAINBOW:
        colorMode(HSB, 360, 100, 100);
        fill(frameCount*20%360,100,100,alpha);
        break;
    }
    textFont(font[4]);
    switch(moveMode){
      case TEXT_MOVE:
        textSize(fontSize*(0.8f + 0.2f*(1 - (y - destY)/30)));
        text(showText, x, y);
        break;
        
      case TEXT_NOMOVE:
        textSize(fontSize);
        text(showText, x, destY);
        break;
    }
    if(y > destY){
      y -= speed;
    }
    if(realFrameCount - startFrame > showTime){
      init();
    }
    popStyle();
  }
  
  public void init(){
    state = false;
  }
  
  
  PopupText(String showText, float startX, float startY, int textColor, int fontSize, int moveMode){
    startFrame = realFrameCount;
    state = true;
    this.moveMode = moveMode;
    this.showText = showText;
    this.startX = startX + random(-3,3);
    this.startY = startY + random(-2,2);
    this.textColor = textColor;
    this.fontSize = fontSize;
    x = this.startX;
    y = this.startY;
    showTime = 36;
    speed = 6;
    destY = startY - speed*5;
  }
  
  PopupText(){
    state = false;
  }
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
            enemy[i].critPop();
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
          enemy[i].critPop();
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
          enemy[i].critPop();
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
      enemy[enemyID].getBuff(3,TurretSkillData.CANNON_SKILL_C_T5_DURATION,0);
    }
  }
  
  public void applyBuffOnMiss(){
  }
  
  public void projshow(){
    pushStyle();
    colorMode(HSB, 360, 100, 50);
    fill(frameCount*10%360,100,100);
    ellipse(x,y,size,size);
    popStyle();
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
  int [][] skillCost = new int [3][5];
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
  int [] auraOrbTarget = new int [TurretSkillData.AURA_SKILL_A_T4_ORB_COUNT];
  float auraMeditationCharge;
  float auraDecrepifyBonus;
  float auraShockwaveRadius;
  boolean auraShockwaveState;
  float cooldownTime;
  float projSpeed;
  float projSize;
  boolean builtState;
  boolean buffState[] = new boolean [BuffData.BUFF_COUNT];
  float buffTimer[] = new float [BuffData.BUFF_COUNT];
  
  public void show(){
    
    loadStat();
    loadSkill();
    applyAllyBuff();
    checkBuffValidity();
    applyBuffEffect();
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
      if(!skillState[1][1] || target != -1 || turretType != LASER) critDurationTimer();
    }
    if(turretType == AURA && auraShockwaveState) auraShockwave();
    turretGraphic();
  }
  
  public void turretGraphic(){
    switch(turretType){
      case CANNON:
        fill(0xff1FEAFF);
        noStroke();
        ellipse(x,y,size,size);
        if(skillState[1][2]) cannonFervorVisual();
        moveBullet();
        break;
        
      case LASER:
        if(critMode) laserCritModeVisual();
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
        if(critMode) auraCritModeVisual();
        fill(0xffD916F7);
        noStroke();
        ellipse(x,y,size,size);
        fill(217,22,245,30+2*(levelA+levelB));
        ellipse(x,y,attackRange*2,attackRange*2);
        if(target != -1) critCheck();
        if(skillState[0][3]) auraDrawOrb();
        if(skillState[1][1]){
          auraMeditationProcess();
          auraMeditationVisual();
        }
        break;
    }
  }
  
  public void cannonFervorVisual(){
    pushStyle();
    colorMode(HSB, 360, 100, 100);
    fill(frameCount*10%360,100,100,200*(PApplet.parseFloat(cannonFervorStackCount)/TurretSkillData.CANNON_SKILL_B_T3_MAX_STACK));
    ellipse(x,y,size+cannonFervorStackCount/2,size+cannonFervorStackCount/2);
    popStyle();
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
  
  public void laserCritModeVisual(){
    pushStyle();
    noStroke();
    colorMode(HSB, 360, 100, 100);
    fill(frameCount*20%360,100,100,200+5*levelA);
    float a = critDurationCounter/critDuration*TWO_PI;
    float aStart = -PI/2;
    arc(x,y,size+10,size+10,aStart,a+aStart,PIE);
    popStyle();
  }
  
  public void auraCritModeVisual(){
    pushStyle();
    noStroke();
    colorMode(HSB, 360, 100, 100);
    fill(frameCount*20%360,100,100,200+5*levelA);
    float a = critDurationCounter/critDuration*TWO_PI;
    float aStart = -PI/2;
    arc(x,y,size+10,size+10,aStart,a+aStart,PIE);
    popStyle();
  }
  
  public void auraDrawOrb(){
    pushStyle();
    noStroke();
    colorMode(HSB, 360, 100, 100);
    fill(realFrameCount*10%360,50,100);
    float leadAngle = (realFrameCount+x+y)/(4*PI);
    float angleSpace = TWO_PI/auraOrbTarget.length;
    float radius = 20 + 20*sin(realFrameCount/(4*PI));
    float orbSize = 20;
    for(int i = 0; i < auraOrbTarget.length; i++){
      if(auraOrbTarget[i] != -1){
        if(!enemy[auraOrbTarget[i]].state || dist(x, y, enemy[auraOrbTarget[i]].x, enemy[auraOrbTarget[i]].y) - enemy[auraOrbTarget[i]].size/2 > attackRange) auraOrbTarget[i] = -1;
      }
      if(auraOrbTarget[i] == -1){
        ellipse(x + (radius+size/2)*cos(leadAngle+i*angleSpace), y + (radius+size/2)*sin(leadAngle+i*angleSpace), orbSize, orbSize);
      }else{
        ellipse(enemy[auraOrbTarget[i]].x + (radius+enemy[auraOrbTarget[i]].size/2)*cos(leadAngle+i*angleSpace), enemy[auraOrbTarget[i]].y + (radius+enemy[auraOrbTarget[i]].size/2)*sin(leadAngle+i*angleSpace),orbSize,+ orbSize);
        strokeWeight(3);
        stroke(realFrameCount*10%360,50,100);
        line(enemy[auraOrbTarget[i]].x + (radius+enemy[auraOrbTarget[i]].size/2)*cos(leadAngle+i*angleSpace), enemy[auraOrbTarget[i]].y + (radius+enemy[auraOrbTarget[i]].size/2)*sin(leadAngle+i*angleSpace), enemy[auraOrbTarget[i]].x, enemy[auraOrbTarget[i]].y);
      }
    }
    popStyle();
  }
  
  public void auraMeditationVisual(){
    float ratio = auraMeditationCharge/TurretSkillData.AURA_SKILL_B_T2_MAXIMUM_BONUS_DAMAGE;
    pushStyle();
    noStroke();
    fill(255,120);
    rect(x-5,y-15,10,30);
    fill(255,255*(1-ratio),255*(1-ratio));
    rect(x-5, y-15+30*(1-ratio),10,30*ratio);
    textFont(font[3]);
    textAlign(CENTER,CENTER);
    fill(255);
    text("x" + (1+round(auraMeditationCharge)),x,y);
    popStyle();
  }
  
  public void critCheck(){
    critCheckCounter ++;
    if(critCheckCounter >= critCheckInterval){
      if(checkCritTrigger(turretID,critChance)){
        critMode = true;
        if(skillState[2][2]) auraShockwaveState  = true;
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
  
  public void applyBuffEffect(){
    attackDmg *= damageMultiplier();
    critChance *= critChanceMultiplier();
  }
  
  public void applyAllyBuff(){
    switch(turretType){
      case AURA:
        if(skillState[0][0]){
          for(int i = 0; i < turret.length; i++){
            if(dist(x, y, turret[i].x, turret[i].y) - turret[i].size/2 <= attackRange && i != turretID){
              turret[i].getBuff(8,3);
            }
          }
        }
        if(skillState[1][0]){
          for(int i = 0; i < turret.length; i++){
            if(dist(x, y, turret[i].x, turret[i].y) - turret[i].size/2 <= attackRange && i != turretID){
              turret[i].getBuff(9,3);
            }
          }
        }
        break;
    }
  }
  
  public void getBuff(int buffID, float duration){
    buffState[buffID] = true;
    buffTimer[buffID] = realFrameCount + duration;
  }
  
  public void checkBuffValidity(){
    for(int i = 0; i < buffState.length; i++){
      if(buffState[i] && buffTimer[i] == realFrameCount){
        buffState[i] = false;
      }
    }
  }
  
  public float damageMultiplier(){
    float multiplier = 1;
    if(buffState[8]) multiplier += TurretSkillData.AURA_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER;
    return multiplier;
  }
  
  public float critChanceMultiplier(){
    float multiplier = 1;
    if(buffState[9]) multiplier += TurretSkillData.AURA_SKILL_B_T1_EXTRA_CRIT_CHANCE;
    return multiplier;
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
        critChance = TurretLevelData.auraCritChance;
        critDamageMultiplier = TurretLevelData.auraCritDamageMultiplier;
        critDuration = TurretLevelData.auraCritDuration;
        critCheckInterval = TurretLevelData.auraCritCheckInterval;
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
        laserDrawBeam();
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
        int [] affectedIDList = new int [0];
        int [] syncIDList = new int [0];
        float [] distList = new float [sentEnemy];
        for(int i = 0; i < sentEnemy; i++){
          distList[i] = dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2;
          if(distList[i] <= attackRange){
            affectedIDList = splice(affectedIDList, i, affectedIDList.length);
          }else if(enemy[i].buffState[15]){
            syncIDList = splice(syncIDList, i, syncIDList.length);
          }
        }
        
        if(auraShockwaveState) affectedIDList = auraShockwaveListing(affectedIDList, distList);
        
        if(skillState[2][1]) auraCalDecrepifyBonus(affectedIDList);
        
        for(int i = 0; i < affectedIDList.length; i++){
          if(critMode){
            enemy[affectedIDList[i]].hurt(calDamage(turretID, affectedIDList[i], attackDmg, critDamageMultiplier));
            applyBuffOnCrit(affectedIDList[i]);
            enemy[affectedIDList[i]].critPop();
          }else{
            enemy[affectedIDList[i]].hurt(calDamage(turretID, affectedIDList[i], attackDmg));
          }
          applyBuff(affectedIDList[i]);
          if(skillState[0][2] && enemy[affectedIDList[i]].armor > 0){
            enemy[affectedIDList[i]].hurtArmor(enemy[affectedIDList[i]].maxArmor*TurretSkillData.AURA_SKILL_A_T3_ARMOR_DRAIN_PERCENTAGE);
          }
        }
        
        if(skillState[2][3]){
          for(int i = 0; i < syncIDList.length; i++){
            if(critMode){
              enemy[syncIDList[i]].hurt(calDamage(turretID, syncIDList[i], attackDmg, critDamageMultiplier));
              enemy[syncIDList[i]].critPop();
            }else{
              enemy[syncIDList[i]].hurt(calDamage(turretID, syncIDList[i], attackDmg));
            }
          }
        }
        
        if(skillState[0][3]){
          auraOrbDetect(affectedIDList);
          auraOrbEffect();
        }
        cooldown = false;
        cooldownTime = attackRate;
        break;
    }
  }
  
  public void laserDrawBeam(){
    if(critMode){
      colorMode(HSB, 360, 100, 100);
      tint(frameCount*20%360,100,100,200+5*levelA);
    }else{
      critCheck();
      tint(255,0,0,150+8*levelA);
    }
    imageMode(CENTER);
    image(laserBeam, attackRange/2,0,attackRange, laserWidth);
  }
  
  public void applyBuff(int enemyID){
    switch(turretType){
      case LASER:
        if(turret[turretID].skillState[1][2]){
          enemy[enemyID].getBuff(4,TurretSkillData.LASER_SKILL_B_T3_DURATION,realFrameCount,0);
        }
        if(turret[turretID].skillState[2][1]){
          enemy[enemyID].getBuff(5,TurretSkillData.LASER_SKILL_C_T2_DURATION);
        }
        if(turret[turretID].skillState[2][3]){
          enemy[enemyID].getBuff(7,TurretSkillData.LASER_SKILL_C_T4_DURATION);
        }
        if(turret[turretID].skillState[2][4]){
          enemy[enemyID].getBuff(6,TurretSkillData.LASER_SKILL_C_T5_DURATION);
        }
        break;
      case AURA:
        if(turret[turretID].skillState[0][4]){
          enemy[enemyID].getBuff(10,TurretSkillData.AURA_SKILL_A_T5_DURATION,1,attackDmg);
        }
        if(turret[turretID].skillState[1][3]){
          enemy[enemyID].getBuff(17,TurretSkillData.AURA_SKILL_B_T4_DURATION);
        }
        if(turret[turretID].skillState[1][4]){
          enemy[enemyID].getBuff(12,TurretSkillData.AURA_SKILL_B_T5_DURATION);
        }
        if(turret[turretID].skillState[2][0]){
          enemy[enemyID].getBuff(13,TurretSkillData.AURA_SKILL_C_T1_DURATION);
        }
        if(turret[turretID].skillState[2][3]){
          enemy[enemyID].getBuff(15,TurretSkillData.AURA_SKILL_C_T4_DURATION);
        }
        if(turret[turretID].skillState[2][4]){
          enemy[enemyID].getBuff(16,TurretSkillData.AURA_SKILL_C_T5_DURATION);
        }
        break;
    }
  }
  
  public void applyBuffOnCrit(int enemyID){
    switch(turretType){
      case AURA:
        if(turret[turretID].skillState[1][2]){
          enemy[enemyID].getBuff(11,3);
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
    laserDrawBeam();
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
        enemy[idList[i]].critPop();
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
      if(critMode){
        enemy[idList[i]].hurt(calDamage(turretID, idList[i], pierceDmg, critDamageMultiplier));
        enemy[idList[i]].critPop();
      }else{
        enemy[idList[i]].hurt(calDamage(turretID, idList[i], pierceDmg));
      }
    }
  }
  
  public void laserDeathstar(int [] idList){
    int deathstarTarget;
    float deathstarDamage;
    deathstarDamage = attackDmg * TurretSkillData.LASER_SKILL_A_T5_BONUS_DAMAGE_MULTIPLIER;
    if(laserDeathstarTime == 0) laserDeathstarTime = realFrameCount;
    if((realFrameCount-laserDeathstarTime)%TurretSkillData.LASER_SKILL_A_T5_DAMAGE_INTERVAL == 0){
      deathstarTarget = idList[floor(random(0,idList.length-1))];
      if(deathstarTarget != -1){
        enemy[deathstarTarget].hurt(deathstarDamage);
        debuffIndicate(enemy[deathstarTarget].x,enemy[deathstarTarget].y,70,100);
      }
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
  
  public void auraOrbDetect(int [] IDList){
    for(int i = 0; i < auraOrbTarget.length; i++){
      if(auraOrbTarget[i] == -1){
        auraOrbTarget[i] = IDList[floor(random(IDList.length))];
      }
    }
  }
  
  public void auraOrbEffect(){
    for(int i = 0; i < auraOrbTarget.length; i++){
      if(auraOrbTarget[i] != -1){
        enemy[auraOrbTarget[i]].hurt(attackDmg*TurretSkillData.AURA_SKILL_A_T4_DAMAGE_PERCENTAGE);
      }
    }
  }
  
  public void auraMeditationProcess(){
    if(target==-1){
      auraMeditationCharge += TurretSkillData.AURA_SKILL_B_T2_CHARGE_RATE_PER_SEC / 60;
      auraMeditationCharge = min(auraMeditationCharge, TurretSkillData.AURA_SKILL_B_T2_MAXIMUM_BONUS_DAMAGE);
    }else{
      auraMeditationCharge -= TurretSkillData.AURA_SKILL_B_T2_DRAIN_RATE_PER_SEC / 60;
      auraMeditationCharge = max(auraMeditationCharge, 0);
    }
  }
  
  public void auraCalDecrepifyBonus(int [] IDList){
    float amount = 0;
    for(int i = 0; i < IDList.length; i++){
      amount += (1-(enemy[IDList[i]].health/enemy[IDList[i]].maxHealth))*100*TurretSkillData.AURA_SKILL_C_T2_BONUS_DAMAGE_PER_PERCENT_OF_MISSING_HEALTH;
    }
    auraDecrepifyBonus = amount;
  }
  
  public void auraShockwave(){
    float speed = attackRange * TurretSkillData.AURA_SKILL_C_T3_SHOCKWAVE_SPEED_RATIO;
    float maxRange = attackRange * TurretSkillData.AURA_SKILL_C_T3_MAX_RADIUS_RATIO;
    auraShockwaveRadius += speed;
    if(auraShockwaveRadius > maxRange){
      auraShockwaveInit();
    }
    auraShockwaveRadius = min(auraShockwaveRadius, maxRange);
    pushStyle();
    imageMode(CENTER);
    if(auraShockwaveRadius >= maxRange*0.7f) tint(255,255*map(auraShockwaveRadius,maxRange*0.7f,maxRange,1,0));
    image(shockwave,x,y,auraShockwaveRadius*2,auraShockwaveRadius*2);
    popStyle();
  }
  
  public int [] auraShockwaveListing(int [] IDList, float [] distList){
    for(int i = 0; i < sentEnemy; i++){
      float d = distList[i] - enemy[i].size/2;
      if(d <= auraShockwaveRadius){
        IDList = splice(IDList, i, IDList.length);
        enemy[i].getBuff(14,TurretSkillData.AURA_SKILL_C_T3_SLOW_DURATION);
      }
    }
    return IDList;
  }
  
  public void auraShockwaveInit(){
    auraShockwaveRadius = 0;
    auraShockwaveState = false;
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
        for(int i = 0; i < auraOrbTarget.length; i++){
          auraOrbTarget[i] = -1;
        }
        auraMeditationCharge = 0;
        auraDecrepifyBonus = 0;
        auraShockwaveInit();
        break;
    }
    for(int i = 0; i < buffState.length; i++){
      buffState[i] = false;
      buffTimer[i] = 0;
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
  
  Turret(int gridPos, int type){
    turretID = gridPos;
    turretType = type;
    x = floor(gridPos/10) * gridSize + 30;
    y = (gridPos - floor(gridPos/10)*10) * gridSize + 30;
    target = -1;
    turretInit(type);
    builtState = true;
  }
}
static class TurretLevelData{
  static int maxLevel = 10;
  static int cannonBuildCost = 30;
  static int laserBuildCost = 40;
  static int auraBuildCost = 50;
  
  static float cannonCritChance = 0.12f;
  static float cannonCritDamageMultiplier = 3;
  static float cannonProjSize = 15;
  static float cannonProjSpeed = 10;
  static float [] cannonDamage = {80,160,240,320,400,480,560,640,720,800,880};
  static float [] cannonRate = {1.5f,1.6f,1.7f,1.8f,1.9f,2.0f,2.1f,2.2f,2.3f,2.4f,2.5f};
  static float [] cannonRange = {150,165,180,195,210,225,240,255,270,285,300};
  static int [] cannonCostA = {15,30,45,60,75,90,105,120,135,150,165};
  static int [] cannonCostB = {15,30,45,60,75,90,105,120,135,150,165};
  static int [] cannonCostC = {15,30,45,60,75,90,105,120,135,150,165};
  
  static float laserCritChance = 0.12f;
  static float laserCritDamageMultiplier = 3;
  static float laserCritDuration = 60;
  static float laserCritCheckInterval = 60;
  static float laserWidth = 10;
  static float laserOverheatThreshold = 240;
  static float laserPiercePenaltyMultiplier = 0.8f;
  static float [] laserDamage = {2,4,6,8,10,12,14,16,18,20,22};
  static float [] laserRate = {2.10f,1.95f,1.80f,1.65f,1.50f,1.35f,1.20f,1.05f,0.90f,0.75f,0.60f};
  static float [] laserRange = {180,200,220,240,260,280,300,320,340,360,380};
  static int [] laserCostA = {20,40,60,80,100,120,140,160,180,200,220};
  static int [] laserCostB = {20,40,60,80,100,120,140,160,180,200,220};
  static int [] laserCostC = {20,40,60,80,100,120,140,160,180,200,220};
  
  static float auraCritChance = 0.12f;
  static float auraCritDamageMultiplier = 3;
  static float auraCritDuration = 60;
  static float auraCritCheckInterval = 60;
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
  
  static int CANNON_SKILL_T1_COST = 30;
  static int CANNON_SKILL_T2_COST = 60;
  static int CANNON_SKILL_T3_COST = 120;
  static int CANNON_SKILL_T4_COST = 240;
  static int CANNON_SKILL_T5_COST = 480;
  
    //LevelA
    
      //T1
  static String CANNON_SKILL_A_T1_NAME = "Steady Aim";
  static String CANNON_SKILL_A_T1_DESCRIPTION = "Greatly increases the damage.";
  static float CANNON_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 1.0f;
  
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
  static String CANNON_SKILL_A_T5_DESCRIPTION = "Deals additional damage based on target\u2019s current health.";
  static float CANNON_SKILL_A_T5_HP_PERCENTAGE = 0.004f;
  
  
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
  static int CANNON_SKILL_B_T3_MAX_STACK = 10;
  
      //T4
  static String CANNON_SKILL_B_T4_NAME = "Bore";
  static String CANNON_SKILL_B_T4_DESCRIPTION = "Greatly increases the chance of critical hits.";
  static float CANNON_SKILL_B_T4_BONUS_CRIT_CHANCE = 0.12f;
  
      //T5
  static String CANNON_SKILL_B_T5_NAME = "Death Wish";
  static String CANNON_SKILL_B_T5_DESCRIPTION = "Increases the chance of critical hits based on the missing health of the castle.";
  static float CANNON_SKILL_B_T5_MIN_BONUS_CRIT_CHANCE = 0.08f;
  static float CANNON_SKILL_B_T5_MAX_BONUS_CRIT_CHANCE = 0.24f;
  
    //LevelC
    
      //T1
  static String CANNON_SKILL_C_T1_NAME = "Cold Snap";
  static String CANNON_SKILL_C_T1_DESCRIPTION = "Applies a debuff that slows enemies.";
  static float CANNON_SKILL_C_T1_SLOW_PERCENTAGE = 0.15f;
  static int CANNON_SKILL_C_T1_DURATION = 60;
  
      //T2
  static String CANNON_SKILL_C_T2_NAME = "Ionic Shell";
  static String CANNON_SKILL_C_T2_DESCRIPTION = "Applies a damaging aura on the target, hurting the enemies around the carrier but not itself.";
  static float CANNON_SKILL_C_T2_BASE_CANNON_DAMAGE_PERCENTAGE = 0.01f;
  static float CANNON_SKILL_C_T2_RADIUS = 180;
  static float CANNON_SKILL_C_T2_DAMAGE_INTERVAL = 3;
  static float CANNON_SKILL_C_T2_DURATION = 240;
  
      //T3
  static String CANNON_SKILL_C_T3_NAME = "Boombastics";
  static String CANNON_SKILL_C_T3_DESCRIPTION = "Cannons explode on impact.";
  static float CANNON_SKILL_C_T3_EXPLOSION_RADIUS = 80;
  static float CANNON_SKILL_C_T3_EXPLOSION_DAMAGE_MULTIPLIER = 0.8f;
  
      //T4   
  static String CANNON_SKILL_C_T4_NAME = "Eagle Sight";
  static String CANNON_SKILL_C_T4_DESCRIPTION = "Greatly increases the attack range of the turret.";
  static float CANNON_SKILL_C_T4_ATTACK_RANGE_MULTIPLIER = 0.5f;
  
      //T5
  static String CANNON_SKILL_C_T5_NAME = "M.A.I.M.";
  static String CANNON_SKILL_C_T5_DESCRIPTION = "Cripples the victim on crit; the slow becomes weaker after each trigger.";
  static float CANNON_SKILL_C_T5_SLOW_PERCENTAGE = 0.6f;
  static float CANNON_SKILL_C_T5_MIN_SLOW_PERCENTAGE = 0.1f;
  static float CANNON_SKILL_C_T5_DURATION = 30;
  
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
  static float LASER_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.8f;
  
      //T2
  static String LASER_SKILL_A_T2_NAME = "Thermocide";
  static String LASER_SKILL_A_T2_DESCRIPTION = "Increases the damage based on the heat of the laser.";
  static float LASER_SKILL_A_T2_MIN_HEAT_THRESHOLD = 120;
  static float LASER_SKILL_A_T2_MAX_DAMAGE_HEAT_CAP = 240;
  static float LASER_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 3.00f;
  
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
  static float LASER_SKILL_B_T1_COOLDOWN_REDUCTION_MULTIPLIER_PER_ENEMY = 0.05f;
  static float LASER_SKILL_B_T1_MAXIMUM_COOLDOWN_REDUCTION_MULTIPLIER = 0.50f;
  
      //T2
  static String LASER_SKILL_B_T2_NAME = "Heat Lock";
  static String LASER_SKILL_B_T2_DESCRIPTION = "During crit mode, the turret is safe from overheat, and the countdown stops when not attacking.";
  
      //T3
  static String LASER_SKILL_B_T3_NAME = "DNA Mutation";
  static String LASER_SKILL_B_T3_DESCRIPTION = "Applies a debuff that inflates the enemy, causing them more prone to get hit and take extra damage.";
  static float LASER_SKILL_B_T3_SIZE_INFLATION_AMOUNT_PER_SEC = 10;
  static float LASER_SKILL_B_T3_MAX_INFLATION_AMOUNT = 20;
  static float LASER_SKILL_B_T3_EXTRA_DAMAGE_MULTIPLER = 0.25f;
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
  static float LASER_SKILL_C_T2_DURATION = 3;
  
      //T3
  static String LASER_SKILL_C_T3_NAME = "Pitchfork";
  static String LASER_SKILL_C_T3_DESCRIPTION = "Creates two additional lasers that deals less damage; lasers cannot share the same target.";
  static float LASER_SKILL_C_T3_MINI_BEAM_COUNT = 2;
  static float LASER_SKILL_C_T3_MINI_BEAM_DAMAGE_MULTIPLIER = 0.6f;
  
      //T4
  static String LASER_SKILL_C_T4_NAME = "Volatile Compound";
  static String LASER_SKILL_C_T4_DESCRIPTION = "Applies a debuff that when the carrier dies, its body explodes, damaging nearby enemies.";
  static float LASER_SKILL_C_T4_MAX_HEALTH_PERCENTAGE_AS_DAMAGE = 0.1f;
  static float LASER_SKILL_C_T4_RADIUS = 150;
  static float LASER_SKILL_C_T4_DURATION = 600;
  
      //T5
  static String LASER_SKILL_C_T5_NAME = "Breach Module";
  static String LASER_SKILL_C_T5_DESCRIPTION = "Applies a debuff that slows enemies based on their missing health.";
  static float LASER_SKILL_C_T5_MAXIMUM_SLOW_PERCENTAGE = 0.20f;
  static float LASER_SKILL_C_T5_DURATION = 3;
  
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
  static float AURA_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER = 0.5f;
  
      //T2
  static String AURA_SKILL_A_T2_NAME = "Tension";
  static String AURA_SKILL_A_T2_DESCRIPTION = "Deals bonus damage based on the distance of the victim.";
  static float AURA_SKILL_A_T2_MAXIMUM_BONUS_DAMAGE_MULTIPLIER = 3;
  static float AURA_SKILL_A_T2_MAXIMUM_EFFECTIVE_RANGE = 0.8f;
  
      //T3
  static String AURA_SKILL_A_T3_NAME = "Corrosive Gas";
  static String AURA_SKILL_A_T3_DESCRIPTION = "Drains a percentage of armor on every strike.";
  static float AURA_SKILL_A_T3_ARMOR_DRAIN_PERCENTAGE = 0.03f;
  
      //T4
  static String AURA_SKILL_A_T4_NAME = "Nano Death Machine";
  static String AURA_SKILL_A_T4_DESCRIPTION = "Creates orbs that damage random enemies, sticking on the victim until it's dead or out of range.";
  static float AURA_SKILL_A_T4_DAMAGE_PERCENTAGE = 0.6f;
  static int AURA_SKILL_A_T4_ORB_COUNT = 6;
  
      //T5
  static String AURA_SKILL_A_T5_NAME = "Cancer";
  static String AURA_SKILL_A_T5_DESCRIPTION = "Applies a stacking debuff, dealing damage that is multiplied every 3 stacks.";
  static float AURA_SKILL_A_T5_BASE_DAMAGE_PERCENTAGE = 0.01f;
  static float AURA_SKILL_A_T5_STACK_CAP = 36;
  static float AURA_SKILL_A_T5_DURATION = 150;
  static float AURA_SKILL_A_T5_DAMAGE_INTERVAL = 15;
  
  
    //LevelB
    
      //T1
  static String AURA_SKILL_B_T1_NAME = "Morale Module";
  static String AURA_SKILL_B_T1_DESCRIPTION = "During crit mode, applies a buff that increases crit chance to nearby turrets.";
  static float AURA_SKILL_B_T1_EXTRA_CRIT_CHANCE = 2;
  
      //T2
  static String AURA_SKILL_B_T2_NAME = "Meditation";
  static String AURA_SKILL_B_T2_DESCRIPTION = "When there's no enemy in the range, charges the turret to provide extra damage for later use.";
  static float AURA_SKILL_B_T2_MAXIMUM_BONUS_DAMAGE = 4;
  static float AURA_SKILL_B_T2_CHARGE_RATE_PER_SEC = 2;
  static float AURA_SKILL_B_T2_DRAIN_RATE_PER_SEC = 1;
  
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
  static float AURA_SKILL_B_T5_HEALTH_THRESHOLD = 0.15f;
  static float AURA_SKILL_B_T5_DURATION = 300;
  
    //LevelC
    
      //T1
  static String AURA_SKILL_C_T1_NAME = "Freeze Module";
  static String AURA_SKILL_C_T1_DESCRIPTION = "Applies a debuff that slows the enemies.";
  static float AURA_SKILL_C_T1_SLOW_PERCENTAGE = 0.20f;
  static int AURA_SKILL_C_T1_DURATION = 15;
  
      //T2
  static String AURA_SKILL_C_T2_NAME = "Decrepify";
  static String AURA_SKILL_C_T2_DESCRIPTION = "Deals bonus damage based on the missing health of enemies in the attack range.";
  static float AURA_SKILL_C_T2_BONUS_DAMAGE_PER_PERCENT_OF_MISSING_HEALTH = 0.01f;
  
      //T3
  static String AURA_SKILL_C_T3_NAME = "A.W.E.";
  static String AURA_SKILL_C_T3_DESCRIPTION = "When entering crit mode, releases a shockwave that slows and deals damage.";
  static float AURA_SKILL_C_T3_MAX_RADIUS_RATIO = 2.5f;
  static float AURA_SKILL_C_T3_SHOCKWAVE_SPEED_RATIO = 0.025f;
  static float AURA_SKILL_C_T3_SLOW_PERCENTAGE = 0.3f;
  static float AURA_SKILL_C_T3_SLOW_DURATION = 60;
  
      //T4
  static String AURA_SKILL_C_T4_NAME = "Synchronize";
  static String AURA_SKILL_C_T4_DESCRIPTION = "Applies a debuff that makes the carrier feel the pain even when out of range.";
  static float AURA_SKILL_C_T4_DURATION = 300;
  
      //T5
  static String AURA_SKILL_C_T5_NAME = "Fatal Bond";
  static String AURA_SKILL_C_T5_DESCRIPTION = "Applies a debuff that causes the damage dealt to one of the enemies to be felt by the others.";
  static float AURA_SKILL_C_T5_DAMAGE_SHARE_PERCENTAGE = 0.2f;
  static float AURA_SKILL_C_T5_DURATION = 300;
  
}
class mapData{
  int mapID;
  mapData(int inputID){
    for(int i = 0; i < gridCount; i ++){
      routeGrid[i] = false;
    }
    mapID = inputID;
    switch(mapID){
      case 0:
        routeGrid[0] = true;
        routeGrid[1] = true;
        routeGrid[2] = true;
        routeGrid[3] = true;
        routeGrid[4] = true;
        routeGrid[5] = true;
        routeGrid[10] = true;
        routeGrid[15] = true;
        routeGrid[20] = true;
        routeGrid[25] = true;
        routeGrid[30] = true;
        routeGrid[35] = true;
        routeGrid[40] = true;
        routeGrid[45] = true;
        routeGrid[50] = true;
        routeGrid[55] = true;
        routeGrid[60] = true;
        routeGrid[65] = true;
        routeGrid[70] = true;
        routeGrid[71] = true;
        routeGrid[72] = true;
        routeGrid[73] = true;
        routeGrid[74] = true;
        routeGrid[75] = true;
        lastGrid = 100;
        break;

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
        
      case 2:
        startpointX = -40;
        startpointY = 150;
        routeGrid[2] = true;
        routeGrid[12] = true;
        routeGrid[22] = true;
        routeGrid[32] = true;
        routeGrid[42] = true;
        routeGrid[51] = true;
        routeGrid[52] = true;
        routeGrid[61] = true;
        routeGrid[71] = true;
        routeGrid[81] = true;
        routeGrid[91] = true;
        routeGrid[94] = true;
        routeGrid[95] = true;
        routeGrid[96] = true;
        routeGrid[97] = true;
        routeGrid[101] = true;
        routeGrid[104] = true;
        routeGrid[107] = true;
        routeGrid[111] = true;
        routeGrid[114] = true;
        routeGrid[117] = true;
        routeGrid[121] = true;
        routeGrid[124] = true;
        routeGrid[127] = true;
        routeGrid[131] = true;
        routeGrid[134] = true;
        routeGrid[137] = true;
        routeGrid[141] = true;
        routeGrid[144] = true;
        routeGrid[147] = true;
        routeGrid[151] = true;
        routeGrid[152] = true;
        routeGrid[153] = true;
        routeGrid[154] = true;
        routeGrid[157] = true;
        routeGrid[167] = true;
        routeGrid[177] = true;
        routeGrid[187] = true;
        routeGrid[188] = true;
        routeGrid[189] = true
;
        lastGrid = 189;
        break;
        
      case 3:
        startpointX = -40;
        startpointY = 510;
        routeGrid[8] = true;
        routeGrid[14] = true;
        routeGrid[15] = true;
        routeGrid[16] = true;
        routeGrid[17] = true;
        routeGrid[18] = true;
        routeGrid[24] = true;
        routeGrid[32] = true;
        routeGrid[33] = true;
        routeGrid[34] = true;
        routeGrid[41] = true;
        routeGrid[42] = true;
        routeGrid[51] = true;
        routeGrid[54] = true;
        routeGrid[55] = true;
        routeGrid[56] = true;
        routeGrid[57] = true;
        routeGrid[58] = true;
        routeGrid[59] = true;
        routeGrid[61] = true;
        routeGrid[63] = true;
        routeGrid[64] = true;
        routeGrid[71] = true;
        routeGrid[73] = true;
        routeGrid[81] = true;
        routeGrid[83] = true;
        routeGrid[91] = true;
        routeGrid[93] = true;
        routeGrid[101] = true;
        routeGrid[103] = true;
        routeGrid[105] = true;
        routeGrid[106] = true;
        routeGrid[107] = true;
        routeGrid[111] = true;
        routeGrid[113] = true;
        routeGrid[115] = true;
        routeGrid[117] = true;
        routeGrid[118] = true;
        routeGrid[121] = true;
        routeGrid[123] = true;
        routeGrid[125] = true;
        routeGrid[128] = true;
        routeGrid[131] = true;
        routeGrid[133] = true;
        routeGrid[135] = true;
        routeGrid[136] = true;
        routeGrid[138] = true;
        routeGrid[141] = true;
        routeGrid[143] = true;
        routeGrid[144] = true;
        routeGrid[146] = true;
        routeGrid[148] = true;
        routeGrid[151] = true;
        routeGrid[154] = true;
        routeGrid[155] = true;
        routeGrid[156] = true;
        routeGrid[158] = true;
        routeGrid[161] = true;
        routeGrid[162] = true;
        routeGrid[167] = true;
        routeGrid[168] = true;
        routeGrid[172] = true;
        routeGrid[173] = true;
        routeGrid[174] = true;
        routeGrid[175] = true;
        routeGrid[176] = true;
        routeGrid[177] = true;
        lastGrid = 59;
    }
  }
}
class waveData{
  int waveID;
  int indexCount;
  
  public void load(int inputWave){
    waveID = inputWave;
    if(waveID>10){
      waveID = ((waveID%10)+1);
    }
    switch(waveID){
      case 1:
        index(1,10);
        break;
      case 2:
        index(1,10);
        break;
      case 3:
        index(2,15);
        break;
      case 4:
        index(1,8);
        index(2,5);
        break;
      case 5:
        index(3,1);
        break;
      case 6:
        index(1,12);
        break;
      case 7:
        index(1,10);
        index(2,5);
        break;
      case 8:
        index(1,5);
        index(2,5);
        index(1,5);
        break;
      case 9:
        index(1,5);
        index(2,10);
        break;
      case 10:
        index(3,1);
        index(4,3);
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
