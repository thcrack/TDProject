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
  color enemyColor;
  int OnGrid, OnGridX, OnGridY;
  int moveDir;
  int bounty;
  float buffRange;
  boolean dsVisualState = false;
  int dsTime = -1;
  
  PopupText dmgPopup = new PopupText();
  PopupText critPopup = new PopupText();
  
  void show(){
    endCheck();
    checkBuffValidity();
    loadStat();
    applyBuffEffect();
    healthCheck();
    if(type == ENEMY_SUPPORT) supportBuffAura();
    drawShadow();
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
    if(buffState[15]) drawSyncVisual();
    if(buffState[16]) drawFatalVisual();
    healthBar();
    armorRegenCheck();
    if(armor!=0) armorBar();
    if(mouseCheck(this) && mousePressed){
        targetEnemy = ID;
    }
  }
  
  void drawShadow(){
    noStroke();
    fill(0,80);
    ellipse(x-3,y+5,size-2,size-2);
  }
  
  void showInfoBox(){
    infoIndicateUI();
    pushMatrix();
    pushStyle();
    int startOffsetX = 6;
    int startOffsetY = 2;
    int columnHeight = 25;
    int columnWidth = 200;
    noStroke();
    translate(1000,1);
    fill(0,180);
    rect(0,0,columnWidth,columnHeight*6);
    textFont(font[7]);
    textSize(17);
    textAlign(LEFT,TOP);
    fill(255);
    text("Type: " + enemyName,startOffsetX,startOffsetY + columnHeight*0);
    fill(255,0,0);
    rect(0,columnHeight*1,columnWidth * (health/maxHealth), columnHeight);
    fill(255);
    text("Health: " + ceil(health),startOffsetX,startOffsetY + columnHeight*1);
    fill(0,0,255);
    rect(0,columnHeight*2,columnWidth * (armor/maxArmor), columnHeight);
    fill(255);
    text("Armor: " + ceil(armor),startOffsetX,startOffsetY + columnHeight*2);
    text("Speed: " + float(round(speed*100))/100,startOffsetX,startOffsetY + columnHeight*3);
    text("Power: " + power,startOffsetX,startOffsetY + columnHeight*4);
    text("Bounty: " + bounty,startOffsetX,startOffsetY + columnHeight*5);
    textAlign(CENTER,TOP);
    float showBuffCount = 0;
    for(int i = 0; i < buffState.length; i++){
      if(buffState[i]){
        fill(0,180);
        rect(0,columnHeight*6+showBuffCount*columnHeight,columnWidth,columnHeight);
        if(BuffData.BUFF_TYPE[i] == BUFF){
          fill(0,0,255);
        }else{
          fill(255,0,0);
        }
        rect(0,columnHeight*6+showBuffCount*columnHeight,columnWidth*(buffTimer[i]-realFrameCount)/BuffData.BUFF_DURATION[i],columnHeight);
        fill(255);
        text(BuffData.BUFF_NAME[i],columnWidth/2,startOffsetY+(showBuffCount+6)*columnHeight);
        showBuffCount++;
      }
    }
    popStyle();
    popMatrix();
  }
  
  void infoIndicateUI(){
    pushStyle();
    float m = 30 + 8*sin(frameCount/(PI*4));
    tint(255,0,0);
    image(targetArrow, x, y - m, 30, 45);
    popStyle();
  }
  
  void demoShow(){
    if(type == ENEMY_SUPPORT) supportBuffAura();
    ellipseMode(CENTER);
    drawShadow();
    stroke(0);
    fill(enemyColor);
    ellipse(x,y,size,size);
    if(drawHurt){
      fill(255,255,255,200);
      ellipse(x,y,size,size);
      drawHurt = false;
    }
  }
  
  void popShow(){
    if(dmgPopup.state) dmgPopup.show();
    if(critPopup.state) critPopup.show();
  }
  
  void healthBar(){
    //pushStyle();
    //textAlign(CENTER,CENTER);
    //fill(255);
    //textFont(font[3]);
    //text(ceil(health),x,y-6);
    //popStyle();
    stroke(0);
    fill(0,60);
    rect(x-20,y-20,40,4);
    fill(0,255,0);
    rect(x-20,y-20,40*(health/maxHealth),4);
  }
  
  void armorBar(){
    //pushStyle();
    //textAlign(CENTER,CENTER);
    //fill(100,100,255);
    //textFont(font[3]);
    //text(ceil(armor),x,y+10);
    //popStyle();
    stroke(0);
    fill(0,60);
    rect(x-20,y-25,40,4);
    fill(0,0,255);
    rect(x-20,y-25,40*(armor/maxArmor),4);
  }
  
  void armorRegenCheck(){
    int delayTime = armorRegenDelay;
    if(buffState[17]) lastHitTime++;
    if(lastHitTime + delayTime < realFrameCount && armor < maxArmor){
      armor += (maxArmor-armor)*armorRegenRate;
    }
  }
  
  void critPop(){
    critPopup = new PopupText("CRIT", x, y-25, 3, 20, TEXT_NOMOVE);
  }
  
  void move(){
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
  
  void healthCheck(){
    if(health <= 0){
      if(buffState[7]) volatileEffect();
      if(buffState[11]) bloodlustEffect();
      init();
      addGold(bounty);
    }
  }
  
  void endCheck(){
    if(OnGrid == lastGrid){
      sfxBaseHit.trigger();
      baseHealth -= power;
      callPopup("-" + power, x, y, 0, 30, TEXT_MOVE);
      init();
    }
  }
  
  void hurt(float damage){
    lastHitTime = realFrameCount;
    damage *= damageMultiplier();
    health -= damage;
    if(buffState[16]) fatalEffect(damage);
    damagePop(damage);
    //println(ID + " / " + damage);
    drawHurt = true;
  }
  
  void fatalHurt(float damage){
    lastHitTime = realFrameCount;
    damage *= damageMultiplier();
    health -= damage;
    damagePop(damage);
    //println(ID + " / " + damage);
    drawHurt = true;
  }
  
  void hurtArmor(float damage){
    lastHitTime = realFrameCount;
    damage *= armorDamageMultiplier();
    armor -= damage;
    armor = max(0,armor);
  }
  
  void damagePop(float damage){
    String showDmg;
    if(damage > 1000000){
      showDmg = "" + float(round(damage/100000))/10 + "M";
    }else if(damage > 1000){
      showDmg = "" + float(round(damage/100))/10 + "K";
    }else{
      showDmg = "" + float(round(damage*10))/10;
    }
    dmgPopup = new PopupText(showDmg, x, y-10, 1, 22, TEXT_NOMOVE);
  }
  
  void applyBuffEffect(){
    // Group: Armor
    
    // Group: Speed
    speed *= speedMultiplier();
    speed = max(speed,EnemyData.MIN_SPEED);
    
    // Size
    size += sizeAddition();
    size = min(size,58);
    
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
  
  void checkBuffValidity(){
    for(int i = 0; i < buffState.length; i++){
      if(buffState[i] && buffTimer[i] == realFrameCount){
        buffState[i] = false;
        buffData1[i] = 0;
        buffData2[i] = 0;
      }
    }
  }
  
  void getBuff(int buffID, float duration){
    buffState[buffID] = true;
    buffTimer[buffID] = realFrameCount + duration;
  }
  
  void getBuff(int buffID, float duration, int exception){
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
  
  void getBuff(int buffID, float duration, float data1, float data2){
    buffState[buffID] = true;
    buffTimer[buffID] = realFrameCount + duration;
    buffDataProcess1(buffID, data1);
    buffDataProcess2(buffID, data2);
  }
  
  void buffDataProcess1(int ID, float data){
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
  
    void buffDataProcess2(int ID, float data){
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
  
  float armorDamageMultiplier(){
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
  
  float armorRegenMultiplier(){
    float multiplier = 1;
    if(buffState[22] && !buffState[17]){
      multiplier += EnemyData.BUFF_WEAVE_MULTIPLIER;
    }
    return multiplier;
  }
  
  float armorRegenDelayMultiplier(){
    float multiplier = 1;
    if(buffState[22] && !buffState[17]){
      multiplier -= EnemyData.BUFF_WEAVE_DELAY_REDUCTION;
    }
    return multiplier;
  }
  
  float damageMultiplier(){
    float multiplier = 1;
    if(buffState[4]){
      multiplier += TurretSkillData.LASER_SKILL_B_T3_EXTRA_DAMAGE_MULTIPLER * (buffData2[4]/TurretSkillData.LASER_SKILL_B_T3_MAX_INFLATION_AMOUNT);
    }
    if(buffState[21] && !buffState[17]){
      multiplier -= EnemyData.BUFF_TOUGH_SKIN_MULTIPLIER;
    }
    return multiplier;
  }
  
  float speedMultiplier(){
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
  
  float sizeAddition(){
    float addition = 0;
    if(buffState[4]){
      addition = (realFrameCount-buffData1[4])/60*TurretSkillData.LASER_SKILL_B_T3_SIZE_INFLATION_AMOUNT_PER_SEC;
      addition = min(addition,TurretSkillData.LASER_SKILL_B_T3_MAX_INFLATION_AMOUNT);
      buffData2[4] = addition;
    }
    return addition;
  }
  
  void ionicEffect(){
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
  
  void volatileEffect(){
    float dmg = maxHealth * TurretSkillData.LASER_SKILL_C_T4_MAX_HEALTH_PERCENTAGE_AS_DAMAGE;
    for(int i = 0; i < sentEnemy; i++){
      if(i != ID && dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= TurretSkillData.LASER_SKILL_C_T4_RADIUS){
          enemy[i].hurt(calDamage(i, dmg));
      }
    }
    //debuffIndicate(x,y,TurretSkillData.LASER_SKILL_C_T4_RADIUS*2,120);
  }
  
  void cancerEffect(){
    float dmg = buffData2[10] * (TurretSkillData.AURA_SKILL_A_T5_BASE_DAMAGE_PERCENTAGE * pow(2,buffData1[10]/3));
    if((buffTimer[10] - realFrameCount) % TurretSkillData.AURA_SKILL_A_T5_DAMAGE_INTERVAL == 0){
      hurt(dmg);
    }
    //debuffIndicate(x,y,70,5*buffData1[10],(buffTimer[10]-realFrameCount),TurretSkillData.AURA_SKILL_A_T5_DURATION);
  }
  
  void bloodlustEffect(){
    for(int i = 0; i < turret.length; i++){
      if(turret[i].turretType == AURA && turret[i].skillState[1][2] && turret[i].critMode) turret[i].critDurationCounter = turret[i].critDuration;
    }
  }
  
  void jinxEffect(){
    if(health > 0 && health < maxHealth * TurretSkillData.AURA_SKILL_B_T5_HEALTH_THRESHOLD) hurt(health*10);
  }
  
  void fatalEffect(float dmg){
    for(int i = 0; i < sentEnemy; i++){
      if(enemy[i].buffState[16]) enemy[i].fatalHurt(dmg*TurretSkillData.AURA_SKILL_C_T5_DAMAGE_SHARE_PERCENTAGE);
    }
  }
  
  void supportBuffAura(){
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
    fill(#0CEBF5, 40);
    ellipse(x,y,buffRange*2,buffRange*2);
    popStyle();
  }
  
  void supportDynamicSpeed(int [] IDList){
    float speedSum = 0;
    for(int i = 0; i < IDList.length; i++){
      speedSum += enemy[IDList[i]].speed;
    }
    speedSum /= IDList.length;
    if(speedSum > 0) speed = speedSum;
  }
  
  void drawDSVisual(){
    int dsShowTime = 30;
    pushStyle();
    noStroke();
    colorMode(HSB,360,100,100);
    fill(realFrameCount%360,100,100, 8*(dsShowTime-(realFrameCount-dsTime)));
    rect(x-25,y-700,50,700);
    ellipse(x,y,80,80);
    popStyle();
    if(realFrameCount-dsTime == dsShowTime) dsVisualState = false;
  }
  
  void drawSyncVisual(){
    for(int i = 0; i < turret.length; i++){
      if(turret[i].turretType == AURA && turret[i].skillState[2][3]){
        pushStyle();
        strokeWeight(2);
        stroke(turret[i].turretColor, 100);
        line(turret[i].x,turret[i].y-turret[i].levelC/2,x,y);
        popStyle();
      }
    }
  }
  
  void drawFatalVisual(){
    for(int i = 0; i < sentEnemy; i++){
      if(enemy[i].buffState[16] && i != ID){
        pushStyle();
        strokeWeight(2);
        stroke(255,0,0, 100);
        line(enemy[i].x,enemy[i].y,x,y);
        popStyle();
      }
    }
  }
  
  void loadStat(){
    switch(type){
      case 1: //normal
        enemyName = "NRML";
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
        enemyName = "FAST";
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
        enemyName = "TANK";
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
        enemyName = "SPRT";
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
  
  void init(){
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