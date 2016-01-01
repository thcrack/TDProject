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

static final int COLOR_WHITE = 0;
static final int COLOR_RED = 1;
static final int COLOR_GREEN = 2;
static final int COLOR_RAINBOW = 3;

static final int TEXT_MOVE = 0;
static final int TEXT_NOMOVE = 1;


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
PImage [] bgTiles = new PImage [20];
PImage laserBeam;
boolean [] routeGrid = new boolean[gridCount]; //Creates an array to store whether each grid is on the route
boolean skillMenuState;
int UIMode;
int gold;
int realFrameCount;
int gameSpeed;
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
Button sell, skillMenu, gameSpeedChange;
PopupText [] popupTextArray = new PopupText [25];

void setup(){
  frameRate(60);
  realFrameCount = 0;
  gameSpeed = 1;
  size(1280,800,P2D);
  //smooth(8);
  laserBeam = loadImage("img/laserbeam.png");
  targetArrow = loadImage("img/green_arrow.png");
  for(int i = 0; i < 20; i++){
    bgTiles[i] = loadImage("img/bg" + i + ".png");
  }
  font[1] = createFont("ACaslonPro-Regular", 50, true);
  font[2] = createFont("ACaslonPro-Regular", 28, true);
  font[3] = createFont("DilleniaUPC Bold", 26);
  font[4] = createFont("Source Code Pro Bold", 45);
  gameInit(); //Call the method gameInit() to initialize the game
  imageMode(CENTER);
}

void draw(){
  for(int g = 0; g < gameSpeed; g++){
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
    
    for(int i = 0; i < sentEnemy; i++){
      enemy[i].popShow();
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
    showPopup();
    realFrameCount++;
  }
}

// AREA CHECKING METHODS

boolean mouseCheck(int x, int y, int w, int h){ // Check if the mouse is in the given area data
  return(mouseX > x && mouseX < x + w && mouseY > y&& mouseY < y + h);
}

boolean mouseCheck(){ // Check if the mouse is in the given area data
  return(mouseX > screenOffsetX && mouseX < screenOffsetX + gameplayScreenX && mouseY > screenOffsetY&& mouseY < screenOffsetY + gameplayScreenY);
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
  critAmp *= skillCritMultiplier(turretID, critAmp);
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

float skillCritMultiplier(int turretID, float critAmp){
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
  }
  return damageMultiplier;
}
  
float armorAbsorb(int turretID, float inputDmg, int targetID, float inputArmor){
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
  baseHealthUI();
  goldUI();
  fpsUI();
  waveUI();
  timeUI();
  gameSpeedUI();
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

void goldUI(){
  textFont(font[1]);
  fill(#F7E005);
  text("Gold: " + gold, 60, 700);
}

void fpsUI(){
  textFont(font[2]);
  fill(255);
  int fps = floor(frameCount*1000/millis());
  text("FPS: " + fps, 5, 30);
}

void waveUI(){
  textFont(font[2]);
  fill(255);
  text("Current Wave: " + currentWave, 125, 30);
}

void timeUI(){
  textFont(font[2]);
  fill(255);
  int s = floor(millis()/1000)%60;
  int s1 = s%10;
  int s2 = floor(s/10);
  int m = floor(millis()/60000);
  text("Elapsed Time: " + m + ":" + s2 + s1, 450, 30);
}

void gameSpeedUI(){
  textFont(font[2]);
  fill(255);
  text("Game Speed: x" + gameSpeed, 900, 30);
  gameSpeedChange = new Button(1090,3,110,40,"Change",CLICKABLE);
  gameSpeedChange.show();
}

void baseHealthUI(){
  stroke(0,255,0);
  fill(0,255,0);
  rect(1200,50,25,600);
  fill(0);
  rect(1200,50,25,600*(1-constrain(baseHealth/100,0,1)));
}

void targetIndicateUI(){
  float m = 10 + 20*sin(frameCount/(PI*4));
  image(targetArrow, turret[targetTurretID].x, turret[targetTurretID].y - m, 40, 50);
}

void turretBuildUI(){
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

void turretPlacementUI(){
  pushStyle();
  textFont(font[1]);
  colorMode(HSB, 360,100,100);
  fill(frameCount%360,100,100);
  text("Place a turret by mouse", 620, 690);
  if(!routeGrid[mouseOnGrid] && !turret[mouseOnGrid].builtState && mouseCheck()) image(targetArrow, turret[mouseOnGrid].x + screenOffsetX, turret[mouseOnGrid].y + screenOffsetY - 30, 40, 50);
  popStyle();
}

void turretUpgradeUI(){
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

void turretSkillUI(){
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
        Button skillDescBox = new Button(skillPurchase[0].x,skillPurchase[0].y-40,int(textWidth(turret[targetTurretID].skillDescription[j][i])*0.7)+35,40,turret[targetTurretID].skillDescription[j][i],font[3]);
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

void turretSkillLevelIndicateUI(){
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
        }else{
          fill(80+80*sin(frameCount/(PI*4)),255,80+80*sin(frameCount/(PI*4)));
        }
      }else{
        // The rest of the grids are not filled with color
        noFill();
        tint(255, 100);
        image(bgTiles[floor(random(20))],gridX,gridY);
      }
      rect(gridX,gridY,gridSize,gridSize);
      popStyle();
    }
  }
}

void rangeIndicate(){
  stroke(255);
  noFill();
  ellipse(turret[targetTurretID].x, turret[targetTurretID].y,turret[targetTurretID].attackRange*2,turret[targetTurretID].attackRange*2);
}

void enoughGoldIndicate(){
  callPopup("Not Enough Gold", width/2, height*3/4, 1, 26, TEXT_NOMOVE);
}

//

void gameInit(){ // Game initialization
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
  for(int i = 1; i < popupTextArray.length; i++){
    popupTextArray[i] = new PopupText();
  }
}

void waveEnd(){ 
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

void waveEndGoldBounty(int w){
  addGold(max(1,ceil(w*baseHealth/baseMaxHealth)));
}

// ENEMY GROWTH METHODS

float enemyMaxHealthGrowth(int enemyType){
  float mult = pow(0.3*(currentWave-1),2);
  switch(enemyType){
    case ENEMY_NORMAL:
      return 200*mult;
    case ENEMY_FAST:
      return 120*mult;
    case ENEMY_TANK:
      return 3000*mult;
    case ENEMY_SUPPORT:
      return 50*mult;
  }
  return 0;
}

float enemyArmorGrowth(int enemyType){
  float mult = pow(0.3*(currentWave-1),2);
  switch(enemyType){
    case ENEMY_NORMAL:
      return 20*mult;
    case ENEMY_FAST:
      return 20*mult;
    case ENEMY_TANK:
      return 500*mult;
    case ENEMY_SUPPORT:
      return 2*mult;
  }
  return 0;
}

float enemySpeedGrowth(int enemyType){
  switch(enemyType){
    case ENEMY_NORMAL:
      return 0.03*(currentWave-1);
    case ENEMY_FAST:
      return 0.04*(currentWave-1);
    case ENEMY_TANK:
      return 0.02*(currentWave-1);
    case ENEMY_SUPPORT:
      return 0.05*(currentWave-1);
  }
  return 0;
}

int enemyBountyGrowth(int enemyType){
  switch(enemyType){
    case ENEMY_NORMAL:
      return floor(0.5*(currentWave-1));
    case ENEMY_FAST:
      return floor(0.25*(currentWave-1));
    case ENEMY_TANK:
      return floor(4*(currentWave-1));
    case ENEMY_SUPPORT:
      return floor(2*(currentWave-1));
  }
  return 0;
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
  addGold(100);
  //for(int i = 0; i < sentEnemy; i++){
  //  enemy[i].speed *= 0.5;
  //}
  //noLoop();
  //if(mouseX>mouseY) loop();
}

void mouseReleased(){
  switch(UIMode){
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
  }
}

void universalUICheck(){
  if(mouseCheck(gameSpeedChange.x,gameSpeedChange.y,gameSpeedChange.w,gameSpeedChange.h)){
    if(gameSpeed == 1){
      gameSpeed = 2;
    }else if(gameSpeed == 2){
      gameSpeed = 4;
    }else if(gameSpeed == 4){
      gameSpeed = 1;
    }
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