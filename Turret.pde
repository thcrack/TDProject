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
  float cooldownTime;
  float projSpeed;
  float projSize;
  boolean builtState;
  
  void show(){
    switch(turretType){
      case CANNON:
        fill(#1FEAFF);
        noStroke();
        ellipse(x,y,40,40);
        moveBullet();
        break;
        
      case LASER:
        if(cooldown){
          fill(#F4F520);
        }else{
          fill(255,0,0);
        }
        noStroke();
        ellipse(x,y,40,40);
        break;
        
      case AURA:
        fill(#D916F7);
        noStroke();
        ellipse(x,y,40,40);
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
  }
  
  void attack(){
    //pick a bullet
    switch(turretType){
      case CANNON:
        for(int i = 0; i < maxBulletCount; i++){
          if(!proj[turretID][i].projstate){
            proj[turretID][i].projshoot(turretID, x, y, enemy[target].x, enemy[target].y, projSpeed, projSize);
            cooldown = false;
            cooldownTime = attackRate;
            break;
          }
        }
        break;
        
      case LASER:
        float angle = atan2(enemy[target].y - y, enemy[target].x - x);
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
            if(critMode){
              enemy[i].hurt(calDamage(turretID, i, attackDmg, critDamageMultiplier));
            }else{
              enemy[i].hurt(calDamage(turretID, i, attackDmg, 1));
            }
          }
        }
        laserHeat++;
        if(laserHeat == laserOverheatThreshold){
          laserHeat = 0;
          cooldown = false;
          cooldownTime = attackRate;
        }
        popStyle();
        popMatrix();
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
              enemy[i].hurt(calDamage(turretID, i, attackDmg, 1));
            }
          }
        }
        cooldown = false;
        cooldownTime = attackRate;
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
  
  void cooldownTimer(){
    cooldownTime --;
    if(turretType == LASER && cooldownTime > attackRate){
      cooldownTime = attackRate;
    }
    if(cooldownTime <= 0){
      cooldown = true;
    }
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
        critChance = 0.15;
        critDamageMultiplier = 2;
        critDuration = 60;
        critDurationCounter = 0;
        critCheckInterval = 60;
        critCheckCounter = 0;
        critMode = false;
        laserWidth = 15;
        laserHeat = 0;
        laserOverheatThreshold = 300;
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