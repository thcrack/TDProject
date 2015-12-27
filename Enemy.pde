class Enemy{
  final int MOVE_U = 1;
  final int MOVE_D = 2;
  final int MOVE_L = 3;
  final int MOVE_R = 4;
  
  boolean state;
  boolean drawHurt;
  boolean buffState[] = new boolean [BuffData.BUFF_COUNT];
  float buffTimer[] = new float [BuffData.BUFF_COUNT];
  float buffData[] = new float [BuffData.BUFF_COUNT];
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
  color enemyColor;
  int OnGrid, OnGridX, OnGridY;
  int moveDir = MOVE_R;
  int bounty;
  
  void show(){
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
  
  void healthBar(){
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
  
  void healthCheck(){
    if(health <= 0){
      init();
      gold += bounty;
    }
  }
  
  void endCheck(){
    if(OnGrid == lastGrid){
      baseHealth -= power;
      init();
    }
  }
  
  void hurt(float damage){
    health -= damage;
    hurtShowDmg = damage;
    drawHurt = true;
  }
  
  void applyBuffEffect(){
    // Group: Armor
    armor -= armorReduction();
    
    // Group: Speed
    speed *= speedMultiplier();
    
    // Group: Others
    if(buffState[2]) ionicEffect();
  }
  
  void checkBuffValidity(){
    for(int i = 0; i < buffState.length; i++){
      if(buffState[i] && buffTimer[i] == frameCount){
        buffState[i] = false;
      }
    }
  }
  
  void getBuff(int buffID, float duration){
    buffState[buffID] = true;
    buffTimer[buffID] = frameCount + duration;
  }
  
  void getBuff(int buffID, float duration, float data){
    buffState[buffID] = true;
    buffTimer[buffID] = frameCount + duration;
    buffData[buffID] = data;
  }
  
  float armorReduction(){
    float amount = 0;
    if(buffState[0]){
      amount += armor * TurretSkillData.CANNON_SKILL_A_T3_ARMOR_REDUCTION_PERCENTAGE;
      debuffIndicate(x,y);
    }
    return amount;
  }
  
  float speedMultiplier(){
    float multiplier = 1;
    if(buffState[1]){
      multiplier -= TurretSkillData.CANNON_SKILL_C_T1_SLOW_PERCENTAGE;
      debuffIndicate(x,y);
    }
    if(buffState[3]){
      multiplier -= TurretSkillData.CANNON_SKILL_C_T5_SLOW_PERCENTAGE;
      debuffIndicate(x,y);
    }
    return constrain(multiplier,0,1);
  }
  
  void ionicEffect(){
    float dmg = buffData[2] * TurretSkillData.CANNON_SKILL_C_T2_BASE_CANNON_DAMAGE_PERCENTAGE;
    if( (buffTimer[2] - frameCount) % TurretSkillData.CANNON_SKILL_C_T2_DAMAGE_INTERVAL == 0){
      for(int i = 0; i < sentEnemy; i++){
        if(i != ID && dist(x, y, enemy[i].x, enemy[i].y) - enemy[i].size/2 <= TurretSkillData.CANNON_SKILL_C_T2_RADIUS){
            enemy[i].hurt(dmg);
        }
      }
    }
    debuffIndicate(x,y);
  }
  
  void loadStat(){
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
  
  void init(){
    for(int i = 0; i < buffState.length; i++){
      buffState[i] = false;
      buffTimer[i] = 0;
      buffData[i] = 0;
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