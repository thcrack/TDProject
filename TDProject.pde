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
boolean shiftMode = false;
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

void setup(){
  frameRate(60);
  size(1280,800,P2D);
  cursor(HAND);
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
  font[7] = createFont("Consolas Bold", 70);
  font[8] = createFont("Consolas Bold", 15, true);
  imageMode(CENTER);
  currentMap = new mapData(0);
}

void menuInit(){
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

void draw(){
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

void drawMainMenu(){
  background(0,20);
  pushStyle();
  tint(255,40+20*sin(frameCount/(PI*10)));
  image(mainBG[(frameCount)%10],width/2,height/2);
  pushMatrix();
  pushStyle();
  colorMode(HSB,360,100,100);
  translate(4*cos(frameCount/(8*PI)),8*sin(frameCount/(8*PI)));
  textFont(font[5]);
  textAlign(LEFT,TOP);
  fill(frameCount%360,100,100);
  noStroke();
  float titleX = 130;
  float titleY = 100;
  text("DIGITAL",titleX,titleY);
  text("ASSAULT",titleX,titleY+130);
  if((frameCount)%5==0) glitch = (random(14));
  if(glitch<7){
    rect(132+74*floor(glitch),95+130*(floor(glitch%2)),70,100);
  }
  popStyle();
  popMatrix();
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
      tint(#F7CF2A);
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

void menuDemo(){
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

void drawGameplay(){
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
        turret[i].detect();
        turret[i].show();
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
      
      laserDeathstarEffect();
      
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

void drawPauseUI(){
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

void drawEndScene(){
  pushStyle();
  if(frameCount-endFrame > 180){
    endMenuState = true;
    String [] randomWord = {"computer", "bring", "true", "ten", "prong", "honor", "panic", "contestant", "practical", "duel", "console", "gloves", "fool", "logic", "arcane", "graphic", "brainwash", "absorbable", "ambivalent", "groovy", "guilt", "couple", "teargas", "beggar", "design", "prank", "excuse", "glass", "boa", "penguin", "downward", "agent", "hangar", "coat", "deep", "limousine", "wake", "alibi", "gang", "sexless", "consumer", "feudal", "clubhouse", "lust", "decay", "nervous", "accord", "top", "useless", "numbskull", "pilgrim", "anyplace", "periodic", "bellybutton", "series", "fuck", "my", "pig", "teammates", "amphibian", "extensive", "deduction", "racket", "teen", "madman", "planet", "seaweed", "theatre", "shadow", "satellite", "coin", "powder", str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-15000,300000)), str(random(-9000,60000)), str(random(-100000,30000)), "RED", "BLU", "GRN", "DED", "LOL", "A", "g", "oW", "DL"};
    textFont(font[floor(random(1,7))]);
    textAlign(CENTER,CENTER);
    textSize(floor(random(5,100)));
    noStroke();
    //stroke(0,(frameCount-endFrame)-250);
    float trans = min(40,(frameCount-endFrame));
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
    colorMode(HSB,360,100,100);
    fill((frameCount*8)%360,100,100,trans+50);
    noStroke();
    float angle = frameCount/(4*PI);
    ellipse(mouseX+250*cos(angle*0.5),mouseY+250*sin(angle*0.5),20,20);
    ellipse(mouseX+250*cos(angle*0.5+PI),mouseY+250*sin(angle*0.5+PI),20,20);
    ellipse(mouseX+400*cos(angle),mouseY+400*sin(angle),30,30);
    ellipse(mouseX+400*cos(angle+PI),mouseY+400*sin(angle+PI),30,30);
    ellipse(mouseX+500*cos(angle*2),mouseY+500*sin(angle*2),40,40);
    ellipse(mouseX+500*cos(angle*2+PI),mouseY+500*sin(angle*2+PI),40,40);
    textFont(font[6]);
    textSize(100);
    fill(0,0,100,(frameCount-endFrame)-150);
    text("YOU'VE SURVIVED", random(640,645), random(220,225));
    fill((frameCount)%360,100,100,(frameCount-endFrame)-150);
    String s = " WAVE";
    if((currentWave-1)>=2){
      s = " WAVES";
    }
    text("" + (currentWave-1) + s, random(640,645), 350+8*sin(frameCount/(8*PI)));
    endMainmenu = new Button(520, 510, 240, 50, "MAIN MENU", font[1]);
    endRestart = new Button(520, 610, 240, 50, "PLAY AGAIN", font[1]);
    endMainmenu.show();
    endRestart.show();
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

boolean mouseCheck(int x, int y, int w, int h){ // Check if the mouse is in the given area data
  return(mouseX > x && mouseX < x + w && mouseY > y&& mouseY < y + h);
}

boolean mouseCheck(){ // Check if the mouse is in the given area data
  return(mouseX > screenOffsetX && mouseX < screenOffsetX + gameplayScreenX && mouseY > screenOffsetY&& mouseY < screenOffsetY + gameplayScreenY);
}

boolean mouseCheck(Button buttonName){ // Check if the mouse is in the given area data
  return(mouseX > buttonName.x && mouseX < buttonName.x + buttonName.w && mouseY > buttonName.y&& mouseY < buttonName.y + buttonName.h);
}

boolean mouseCheck(Enemy enemyName){ // Check if the mouse is in the given area data
  return(dist(mouseX-screenOffsetX,mouseY-screenOffsetY,enemyName.x,enemyName.y) <= enemyName.size/2);
}

boolean rectHitCheck(float ax, float ay, float aw, float ah, float bx, float by)
{
    boolean collisionX = (ax + aw >= bx) && (bx >= ax);
    boolean collisionY = (ay + ah >= by) && (by >= ay);
    return collisionX && collisionY;
}

boolean enemyCheck(){ // Check if every enemy in the wave is dead
  for(int i = 0; i < sentEnemy; i++){
    if(enemy[i].state){
      return true;
    }
  }
  return false;
}

// DAMAGE CALCULATING METHODS

boolean checkCritTrigger(int turretID, float critChance){
  critChance += skillCritChanceAddition(turretID);
  if(random(0,1) <= critChance){
    return true;
  }
  return false;
}

float calDamage(int turretID, int enemyID, float inputDamage, float critAmp){
  float damage;
  // Result Damage = (Input Damage * (Crit Amplification * Skill Crit Multiplier) * Skill Multiplier + Skill Additional Damage) * Armor Multiplier 
  damage = inputDamage;
  critAmp *= skillCritMultiplier(turretID);
  damage *= critAmp;
  damage *= skillDamageMultiplier(turretID,enemyID);
  damage += skillDamageAddition(turretID,enemyID);
  damage -= armorAbsorb(turretID, damage, enemyID, enemy[enemyID].armor);
  //println(enemy[enemyID].armor + "/" + damageMultiplier + "/" + damage);
  return damage;
}

float calDamage(int turretID, int enemyID, float inputDamage){
  float damage;
  // (NO CRIT) Result Damage = (Input Damage * Skill Multiplier + Skill Additional Damage) * Armor Multiplier 
  damage = inputDamage;
  damage *= skillDamageMultiplier(turretID,enemyID);
  damage += skillDamageAddition(turretID,enemyID);
  damage -= armorAbsorb(turretID, damage, enemyID, enemy[enemyID].armor);
  //println(enemy[enemyID].armor + "/" + damageMultiplier + "/" + damage);
  return damage;
}

float calDamage(int enemyID, float inputDamage){
  float damage;
  // (DAMAGING BUFF) Result Damage = Input Damage * Armor Multiplier
  damage = inputDamage;
  damage -= armorAbsorb(-1, damage, enemyID, enemy[enemyID].armor);
  //println(enemy[enemyID].armor + "/" + damageMultiplier + "/" + damage);
  return damage;
}

float skillCritMultiplier(int turretID){
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

float skillCritChanceAddition(int turretID){
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

float skillDamageAddition(int turretID, int enemyID){
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

float skillDamageMultiplier(int turretID, int enemyID){
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

float skillDamageMultiplier(int turretID){
  float damageMultiplier = 1;
  switch(turret[turretID].turretType){
    case CANNON:
      if(turret[turretID].skillState[0][0]){
        damageMultiplier += TurretSkillData.CANNON_SKILL_A_T1_BONUS_DAMAGE_MULTIPLIER;
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
  
float armorAbsorb(int turretID, float inputDmg, int targetID, float inputArmor){
  float multiplier = 1;
  if(inputArmor>0){
    if(turretID == -1){
      multiplier *= EnemyData.ARMOR_ABSORB_RATIO[difficulty];
    }else{
      multiplier *= armorBypass(turretID, EnemyData.ARMOR_ABSORB_RATIO[difficulty]);
    }
    enemy[targetID].hurtArmor(inputDmg*multiplier);
    return inputDmg*multiplier;
  }
  return 0;
}

float armorBypass(int turretID, float inputRate){
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

void showUI(){
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
  waveInformationUI();
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

int UIModeChecker(){
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

void coverUI(){
  pushStyle();
  fill(0);
  rectMode(CORNER);
  noStroke();
  rect(0,0,1280,50);
  stroke(0,255,0);
  line(0,50,1280,50);
  noStroke();
  rect(1200,0,80,800);
  rect(0,651,1280,150);
  popStyle();
}

void goldUI(){
  textFont(font[1]);
  fill(#F7E005);
  text("Gold: " + gold, 70, 700);
}

void fpsUI(){
  textFont(font[2]);
  fill(255);
  int fps = floor(frameCount*1000/millis());
  text("FPS: " + fps, 15, 33);
}

void waveUI(){
  textFont(font[2]);
  fill(255);
  text("Current Wave: " + currentWave, 160, 33);
}

void timeUI(){
  textFont(font[2]);
  fill(255);
  int time = millis()-startTime;
  int s = floor(time/1000)%60;
  int s1 = s%10;
  int s2 = floor(s/10);
  int m = floor(time/60000);
  text("Elapsed Time: " + m + ":" + s2 + s1, 460, 33);
}

void nextUI(){
  if(!assaultMode){
    nextWave = new Button(940,5,170,40,"Start Wave",CLICKABLE,true);
    nextWave.show();
  }
}

void pauseButtonUI(){
  pause = new Button(1141,5,80,40,"Pause",CLICKABLE);
  pause.show();
}

void waveInformationUI(){
  pushStyle();
  textAlign(CENTER,CENTER);
  
  
  if(assaultMode){
    textFont(font[5],40);
    textLeading(28);
    pushStyle();
    colorMode(HSB,360,100,100);
    fill(realFrameCount*4%360,40,100);
    text("ASSAULT",random(1236,1238),random(-105,-108),25,500);
    popStyle();
  }else{
    textFont(font[5],30);
    textLeading(24);
    fill(255);
    text("INCOMING",1237,-102+2*sin(realFrameCount/(8*PI)),25,500);
  }
  textFont(font[5],15);
  int showMax = 15;
  for(int i = sentEnemy; i < sentEnemy+showMax; i++){
    if(i<currentWaveMaxEnemy){
      fill(lerpColor(enemy[i].enemyColor,color(255),0.35+0.1*sin(realFrameCount/(4*PI))));
      //fill(0,255,0);
      text(enemy[i].enemyName, 1251, 236+(i-sentEnemy+1)*27);
      if(mouseCheck(1223,224+(i-sentEnemy+1)*27,55,27) && targetEnemy == -1){
        pushMatrix();
        translate(screenOffsetX,screenOffsetY);
        enemy[i].showInfoBox();
        popMatrix();
      }
    }
  }
  popStyle();
}

void gameSpeedUI(){
  if(assaultMode){
    textFont(font[2]);
    fill(255);
    text("Game Speed: x" + gameSpeed, 780, 33);
    gameSpeedChange = new Button(1000,5,110,40,"Change",CLICKABLE);
    gameSpeedChange.show();
  }
}

void baseHealthUI(){
  stroke(0,255,0);
  fill(0,255,0);
  rect(1200,50,20,600);
  fill(0);
  rect(1200,50,20,600*(1-constrain(baseHealth/100,0,1)));
}

void targetIndicateUI(){
  float m = 10 + 20*sin(frameCount/(PI*4));
  image(targetArrow, turret[targetTurretID].x, turret[targetTurretID].y - m, 40, 50);
}

void turretBuildUI(){
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

void turretPlacementUI(){
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

void turretUpgradeUI(){
  targetIndicateUI();
  textFont(font[1]);
  fill(255);
  text(turret[targetTurretID].turretName, 330, 690);
  textFont(font[8]);
  switch(turret[targetTurretID].turretType){
    case CANNON:
      text("ATTACK DAMAGE: " + roundToSecond(turret[targetTurretID].attackDmg*skillDamageMultiplier(targetTurretID)), 330, 710);
      text("ATTACK RATE: " + roundToSecond(turret[targetTurretID].frameConvert()) + " hit/s", 330, 725);
      break;
    case LASER:
      text("ATTACK DPS: " + roundToSecond(60*turret[targetTurretID].attackDmg*skillDamageMultiplier(targetTurretID)), 330, 710);
      text("COOLDOWN: " + roundToSecond(turret[targetTurretID].frameConvert()) + " s", 330, 725);
      text("CRIT DURATION: " + roundToSecond(framesConvertSecond(turret[targetTurretID].critDuration)) + " s", 330, 785);
      break;
    case AURA:
      text("ATTACK DAMAGE: " + roundToSecond(turret[targetTurretID].attackDmg*skillDamageMultiplier(targetTurretID)), 330, 710);
      text("ATTACK RATE: " + roundToSecond(turret[targetTurretID].frameConvert()) + " hit/s", 330, 725);
      text("CRIT DURATION: " + roundToSecond(framesConvertSecond(turret[targetTurretID].critDuration)) + " s", 330, 785);
      break;
  }
  text("ATTACK RANGE: " + round(turret[targetTurretID].attackRange), 330, 740);
  text("CRIT DAMAGE: " + roundToSecond(turret[targetTurretID].critDamageMultiplier*skillCritMultiplier(targetTurretID)) + "x", 330, 755);
  text("CRIT CHANCE: " + round((turret[targetTurretID].critChance+skillCritChanceAddition(targetTurretID))*100) + "%", 330, 770);
  textFont(font[2]);
  skillMenu = new Button(60,720,220,40,"Skill Menu");
  skillMenu.show();
  sell = new Button(1100,660,100,40,"Sell");
  sell.show();
  text("Price: " + turret[targetTurretID].sellPrice, 1100, 730);
  text("ARC: lv " + turret[targetTurretID].levelA, 680, 690);
  text("BIT: lv " + turret[targetTurretID].levelB, 680, 730);
  text("COS: lv " + turret[targetTurretID].levelC, 680, 770);
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

void turretSkillUI(){
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
        Button skillDescBox = new Button(skillPurchase[0].x,skillPurchase[0].y-30,int(textWidth(turret[targetTurretID].skillDescription[j][i])*0.55)+40,30,turret[targetTurretID].skillDescription[j][i],font[3]);
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

void turretSkillLevelIndicateUI(){
  pushStyle();
  colorMode(HSB, 360, 100, 100);
  noStroke();
  fill(frameCount%360,100,100);
  rect(340,696,map(turret[targetTurretID].levelA,0,TurretLevelData.maxLevel,0,848),9);
  rect(340,741,map(turret[targetTurretID].levelB,0,TurretLevelData.maxLevel,0,848),9);
  rect(340,786,map(turret[targetTurretID].levelC,0,TurretLevelData.maxLevel,0,848),9);
  popStyle();
}

void laserHeatUI(float x, float y, float w, float h){
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

void drawGrids(){
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
        }else if(assaultMode){
          fill(80+80*sin(frameCount/(PI*4)),255,80+80*sin(frameCount/(PI*4)));
        }else{
          fill(0,200,0);
        }
      }else if(!routeGrid[i*10+j]){
        // The rest of the grids are not filled with color
        if(assaultMode){
          tint(100);
          image(bgTiles[floor(random(20))],gridX,gridY);
        }else{
          tint(40);
          image(bgTiles[(i*j)%20],gridX,gridY);
        }
        noFill();
      }
      rect(gridX,gridY,gridSize,gridSize);
      popStyle();
    }
  }
}

void rangeIndicate(){
  stroke(255);
  noFill();
  ellipse(turret[targetTurretID].x, turret[targetTurretID].y-turret[targetTurretID].levelC/2,turret[targetTurretID].attackRange*2,turret[targetTurretID].attackRange*2);
}

void enoughGoldIndicate(){
  callPopup("Not Enough Gold", width/2, height*3/4, 1, 26, TEXT_NOMOVE);
}

//

void gameInit(){ // Game initialization
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

void waveEnd(){ 
  waveEndGoldBounty(currentWave);
  for(int i = 0; i < gridCount; i++){ // Scan through each grid because the data of turrets is bound to it
    if(turret[i].builtState){ // Check if there is a turret on the grid
      turret[i].target = -1;
    }
  }
  gameSpeed = 1;
  targetEnemy = -1;
  callPopup("Wave Cleared", float(width/2), float(height/5), 3, 60, TEXT_MOVE);
  assaultMode = false;
  currentWave ++;
  wave.load(currentWave);
  sentEnemy = 0;
}

void waveEndGoldBounty(int w){
  int amount = max(1,ceil(w*baseHealth/baseMaxHealth));
  addGold(amount);
}

// ENEMY GROWTH METHODS

float enemyMaxHealthGrowth(int enemyType){
  float mult = pow(0.3*(currentWave-1),2);
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

float enemyArmorGrowth(int enemyType){
  float mult = pow(0.3*(currentWave-1),2);
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

float enemySpeedGrowth(int enemyType){
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

int enemyBountyGrowth(int enemyType){
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

// VISUAL METHODS

void laserDeathstarEffect(){
  
  for(int i = 0; i < sentEnemy; i++){
    if(enemy[i].state && enemy[i].dsVisualState){
      enemy[i].drawDSVisual();
    }
  }
  
  //pushStyle();
  //noStroke();
  //colorMode(HSB,360,100,100);
  //fill(random(360),100,100, 165);
  //for(int i = 1; i < laserDeathstarEffectList.length; i++){
  //  if(enemy[laserDeathstarEffectList[i]].state){
  //    rect(enemy[laserDeathstarEffectList[i]].x-25,enemy[laserDeathstarEffectList[i]].y-700,50,700);
  //    ellipse(enemy[laserDeathstarEffectList[i]].x,enemy[laserDeathstarEffectList[i]].y,80,80);
  //  }
  //}
  //laserDeathstarEffectList = new int [] {-1};
  //popStyle();
}

// UTILITY METHODS

void callPopup(String showText, float startX, float startY, int textColor, int fontSize, int moveMode){
  for(int i = 1; i < popupTextArray.length; i++){
    if(!popupTextArray[i].state){
      popupTextArray[i] = new PopupText(showText, startX, startY, textColor, fontSize, moveMode);
      break;
    }
  }
}

void showPopup(){
  for(int i = 1; i < popupTextArray.length; i++){
    if(popupTextArray[i].state){
      popupTextArray[i].show();
    }
  }
}

void addGold(int amount){
  gold += amount;
  callPopup("+" + amount, 110 + screenOffsetX, 605 + screenOffsetY, 0, 45, TEXT_MOVE);
}

void spendGold(int amount){
  gold -= amount;
  callPopup("-" + amount, 110 + screenOffsetX, 605 + screenOffsetY, 1, 45, TEXT_MOVE);
}

float rateConvertFrames(float x){
  return 60/x;
}

float secondConvertFrames(float x){
  return x*60;
}

float framesConvertRate(float x){
  return 60/x;
}

float framesConvertSecond(float x){
  return x/60;
}

float roundToSecond(float input){
  return float(round(input*100))/100;
}

void debuffIndicate(float x, float y, float r, float str){
  noStroke();
  fill(255,0,0,str);
  ellipse(x,y,r,r);
}

void debuffIndicate(float x, float y, float r, float str, float time, float maxTime){
  noStroke();
  float a = time/maxTime*TWO_PI;
  float aStart = -PI/2;
  fill(255,0,0,str);
  arc(x,y,r,r,aStart,a+aStart,PIE);
}

//INPUT METHODS

void keyPressed(){
  if(key == CODED && keyCode == SHIFT){
    shiftMode = true;
  }
  if(key == ' '){
    if(!assaultMode){
      assaultMode = true;
      sentEnemy = 1;
    }else{
      gameSpeedCycle();
    }
  }
}


void keyReleased(){
  //for(int i = 0; i < sentEnemy; i++){
  //  enemy[i].speed *= 0.5;
  //}
  //noLoop();
  //if(mouseX>mouseY) loop();
  if(key == CODED){
    switch(keyCode){
      case SHIFT:
        shiftMode = false;
        break;
      case UP:
        baseHealth = 100;
        break;
      case DOWN:
        for(int i = 0; i < sentEnemy; i++){
          if(enemy[i].state) enemy[i].hurt(9999999);
        }
        break;
      case RIGHT:
        waveEnd();
        break;
      default:
        addGold(1000);
    }
  }
}

void mousePressed(){
  targetEnemy = -1;
}

void mouseReleased(){
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
        while(gold >= turret[targetTurretID].levelAUpgradeCost){
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
          if(!shiftMode || turret[targetTurretID].levelA >= TurretLevelData.maxLevel) break;
        }
      }else if(upgrade[1]!=null && mouseCheck(upgrade[1].x,upgrade[1].y,upgrade[1].w,upgrade[1].h) && turret[targetTurretID].levelB < TurretLevelData.maxLevel){
        while(gold >= turret[targetTurretID].levelBUpgradeCost){
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
          if(!shiftMode || turret[targetTurretID].levelB >= TurretLevelData.maxLevel) break;
        }
      }else if(upgrade[2]!=null && mouseCheck(upgrade[2].x,upgrade[2].y,upgrade[2].w,upgrade[2].h) && turret[targetTurretID].levelC < TurretLevelData.maxLevel){
        while(gold >= turret[targetTurretID].levelCUpgradeCost){
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
          if(!shiftMode || turret[targetTurretID].levelC >= TurretLevelData.maxLevel) break;
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

void universalUICheck(){
  if(assaultMode && mouseCheck(gameSpeedChange.x,gameSpeedChange.y,gameSpeedChange.w,gameSpeedChange.h)){
    gameSpeedCycle();
  }
  if(!assaultMode && mouseCheck(nextWave.x,nextWave.y,nextWave.w,nextWave.h)){
    assaultMode = true;
    sentEnemy = 1;
  }
  if(mouseCheck(pause.x,pause.y,pause.w,pause.h)){
    pauseState = true;
  }
}

void gameSpeedCycle(){
  if(gameSpeed == 1){
    gameSpeed = 2;
  }else if(gameSpeed == 2){
    gameSpeed = 4;
  }else if(gameSpeed == 4){
    gameSpeed = 1;
  }
}

boolean mouseCheckOnTurret(){
  if(turret[mouseOnGrid].builtState && mouseCheck()){ 
    return true;
  }
  return false;
}

void mouseActionOnCancelSelect(){
  targetTurretID = -1;
}