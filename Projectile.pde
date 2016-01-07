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
  
  void projshoot(int turretID, float x, float y, float destX, float destY, float speed, float size){
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
  
  void projmove(){
    x -= xSpeed;
    y -= ySpeed;
    projcheckhit();
  }
  
  void projcheckhit(){
    for(int i = 0; i < sentEnemy; i++){
      if(hitDetection(i)){
        if(turret[turretID].skillState[2][2]){
          explosion(x,y,i);
        }else{
          applyBuff(i);
          if(UIMode != UI_MAINMENU) sfxEnemyHit.trigger();
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
  
  void explosion(float x, float y, int primaryID){
    debuffIndicate(x,y,TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_RADIUS,200);
    boolean crit = checkCritTrigger(turretID,turret[turretID].critChance);
    float splashDmg = turret[turretID].attackDmg * TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_DAMAGE_MULTIPLIER;
    for(int i = 0; i < sentEnemy; i++){
      if(dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= TurretSkillData.CANNON_SKILL_C_T3_EXPLOSION_RADIUS){
        float distanceDecay;
        if(i == primaryID){
          sfxEnemyHit.trigger();
          applyBuff(i);
          distanceDecay = turret[turretID].attackDmg;
        }else{
          distanceDecay = splashDmg;
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
  
  void explosion(float x, float y){
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
  
  void applyBuff(int enemyID){
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
  
  void applyBuffOnCrit(int enemyID){
    if(turret[turretID].skillState[2][4]){
      enemy[enemyID].getBuff(3,TurretSkillData.CANNON_SKILL_C_T5_DURATION,0);
    }
  }
  
  void applyBuffOnMiss(){
  }
  
  void projshow(){
    pushStyle();
    colorMode(HSB, 360, 100, 50);
    fill(frameCount*10%360,100,100);
    ellipse(x,y,size,size);
    popStyle();
  }
  
  boolean hitDetection(int i){
    if(dist(x,y,enemy[i].x,enemy[i].y) <= size/2 + enemy[i].size/2){
      return true;
    }
    return false;
  }
  
  void init(){
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