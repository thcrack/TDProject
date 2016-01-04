class waveData{
  int waveID;
  int indexCount;
  
  void load(int inputWave){
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