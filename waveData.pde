class waveData{
  int waveID;
  int indexCount;
  
  void load(int inputWave){
    waveID = inputWave;
    if(waveID>50){
      waveID = 40+(waveID%10);
      if(waveID == 40) waveID = 50;
    }
    switch(waveID){
      case 1:
        index(1,10);
        break;
      case 2:
        index(1,10);
        break;
      case 3:
        index(1,10);
        break;
      case 4:
        index(1,10);
        break;
      case 5:
        index(2,15);
        break;
      case 6:
        index(1,10);
        break;
      case 7:
        index(1,10);
        break;
      case 8:
        index(2,10);
        index(1,5);
        break;
      case 9:
        index(2,20);
        break;
      case 10:
        index(3,1);
        break;
      case 11:
        index(1,10);
        break;
      case 12:
        index(1,10);
        break;
      case 13:
        index(2,15);
        break;
      case 14:
        index(1,10);
        break;
      case 15:
        index(2,5);
        index(1,5);
        index(2,5);
        break;
      case 16:
        index(1,10);
        break;
      case 17:
        index(1,10);
        break;
      case 18:
        index(1,5);
        index(2,10);
        index(1,5);
        break;
      case 19:
        index(2,20);
        break;
      case 20:
        index(2,5);
        index(3,1);
        index(2,5);
        break;
      case 21:
        index(1,15);
        break;
      case 22:
        index(1,15);
        break;
      case 23:
        index(1,10);
        index(2,15);
        break;
      case 24:
        index(1,15);
        break;
      case 25:
        index(4,1);
        index(2,10);
        break;
      case 26:
        index(1,15);
        break;
      case 27:
        index(1,15);
        break;
      case 28:
        index(4,1);
        index(2,5);
        index(1,5);
        index(4,1);
        break;
      case 29:
        index(1,15);
        index(2,5);
        break;
      case 30:
        index(2,5);
        index(4,1);
        index(3,1);
        index(4,1);
        index(2,5);
        break;
      case 31:
        index(1,15);
        break;
      case 32:
        index(1,15);
        break;
      case 33:
        index(4,1);
        index(1,5);
        index(4,1);
        index(2,15);
        break;
      case 34:
        index(1,15);
        break;
      case 35:
        index(1,10);
        index(4,1);
        index(2,10);
        index(4,1);
        index(2,5);
        break;
      case 36:
        index(1,15);
        break;
      case 37:
        index(2,15);
        index(4,1);
        index(2,10);
        break;
      case 38:
        index(4,5);
        break;
      case 39:
        index(1,15);
        index(4,1);
        index(2,10);
        break;
      case 40:
        index(1,5);
        index(3,1);
        index(4,3);
        break;
      case 41:
        index(1,20);
        break;
      case 42:
        index(1,10);
        index(4,1);
        index(1,5);
        index(4,1);
        index(1,5);
        break;
      case 43:
        index(1,10);
        index(4,1);
        index(2,5);
        index(4,1);
        index(2,10);
        break;
      case 44:
        index(1,5);
        index(4,1);
        index(2,15);
        index(4,1);
        index(2,5);
        break;
      case 45:
        index(2,5);
        index(4,1);
        index(1,5);
        index(4,1);
        for(int i = 0; i < min(5,floor(inputWave/10)-5); i++){
          index(3,1);
          index(4,1);
        }
        index(1,5);
        index(4,1);
        index(2,5);
        index(4,1);
        index(2,5);
        break;
      case 46:
        index(2,5);
        index(4,2);
        index(1,5);
        index(4,2);
        index(2,10);
        break;
      case 47:
        index(1,5);
        index(4,1);
        index(1,5);
        index(4,1);
        index(1,5);
        index(4,1);
        index(2,3);
        index(4,1);
        index(2,3);
        index(4,1);
        index(2,3);
        index(4,1);
        index(2,3);
        index(4,1);
        index(2,3);
        break;
      case 48:
        index(2,10);
        index(4,1);
        index(1,5);
        index(4,3);
        index(2,10);
        break;
      case 49:
        index(4,1);
        index(2,10);
        index(4,1);
        index(2,10);
        index(4,1);
        break;
      case 50:
        index(2,5);
        index(3,1);
        index(4,2);
        for(int i = 0; i < min(6,floor(inputWave/10)-4); i++){
          index(1,5);
          index(3,1);
          index(4,2);
          index(2,5);
        }
        break;
    }
    currentWaveMaxEnemy = indexCount;
    indexCount = 0;
  }
  
  void index(int type, int amount){
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