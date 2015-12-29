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
  
  void show(){
    switch(turretType){
      case CANNON:
        fill(#1FEAFF);
        noStroke();
        ellipse(x,y,size,size);
        if(skillState[1][2]) cannonFervorVisual();
        moveBullet();
        break;
        
      case LASER:
        if(cooldown){
          fill(#F4F520);
        }else{
          fill(#FF750A);
        }
        noStroke();
        ellipse(x,y,size,size);
        laserHeatVisual();
        break;
        
      case AURA:
        fill(#D916F7);
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
  
  void cannonFervorVisual(){
    pushStyle();
    fill(0);
    textFont(font[3]);
    textAlign(CENTER,CENTER);
    text(cannonFervorStackCount,x,y);
    popStyle();
  }
  
  void laserHeatVisual(){
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
  
  void critCheck(){
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
  
  void critDurationTimer(){
    critDurationCounter -= 1;
    if(critDurationCounter <= 0){
      critMode = false;
    }
  }
  
  void skillFervorReset(){
    cannonFervorStackCount = 0;
  }
  
  void loadStat(){
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
  
  void loadSkill(){
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
  
  float skillRateMultiplier(){
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
  
  float skillCritModeDurationMultiplier(){
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
  
  float skillProjSpeedMultiplier(){
    float multiplier = 1;
    if(skillState[1][1]){
      multiplier += TurretSkillData.CANNON_SKILL_B_T2_EXTRA_PROJECTILE_SPEED_MULTIPLIER;
    }
    return multiplier;
  }
  
  float skillRangeMultiplier(){
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
  
  void rateConvert(){
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

  void detect(){
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
  
  int detectPitchfork(int currentTarget, int exception1, int exception2){
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
  
  void attack(){
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
  
  void applyBuff(int enemyID){
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
  
  void moveBullet(){
    for(int i = 0; i < maxBulletCount; i++){
      if(proj[turretID][i].projstate){
        proj[turretID][i].projshow();
        proj[turretID][i].projmove();
      }
    }
  }
  
  void laserPitchforkAttack(int id){
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
  
  void cooldownTimer(){
    cooldownTime --;
    if(turretType == LASER && cooldownTime > attackRate){
      cooldownTime = attackRate;
    }
    if(cooldownTime <= 0){
      cooldown = true;
    }
  }
  
  int [] checkLaserPierceOrder(int [] idList, float [] idDist, int inputID, float d){
    for(int i = 0; i < idList.length; i++){
      if( d <= idDist[i]){
        idList = splice(idList,inputID,i);
        return idList;
      }
    }
    return idList;
  }
  
  float [] checkLaserPierceDist(int [] idList, float [] idDist, int inputID, float d){
    for(int i = 0; i < idList.length; i++){
      if( d <= idDist[i]){
        idDist = splice(idDist,d,i);
        return idDist;
      }
    }
    return idDist;
  }
  
  void laserPierceDamageProcess(int [] idList){
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
  
  void laserPierceDamageProcess(int [] idList, float dmgPct){
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
  
  void laserDeathstar(int [] idList){
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
  
  float laserOverdriveProcess(float inputRate){
    float m = 1 - (laserCheckOverdriveCount() * TurretSkillData.LASER_SKILL_B_T1_COOLDOWN_REDUCTION_MULTIPLIER_PER_ENEMY);
    inputRate *= m;
    return inputRate;
  }
  
  int laserCheckOverdriveCount(){
    int enemyCount = 0;
    for(int i = 0; i < sentEnemy; i++){
      if(dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= attackRange){
        enemyCount ++;
      }
    }
    return min(5,enemyCount);
  }
  
  float laserOverheatThresholdMultiplier(){
    float multiplier = 1;
    if(skillState[1][3]){
      multiplier += TurretSkillData.LASER_SKILL_B_T4_OVERHEAT_THRESHOLD_MULTIPLIER;
    }
    return multiplier;
  }
  
  
  void turretInit(int type){
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
        critChance = 0.15;
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
  
  void loadSkillInit(){
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