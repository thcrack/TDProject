class waveData{
  int waveID;
  int indexCount;
  
  void load(int inputWave){
    waveID = inputWave;
    if(waveID>5){
      waveID = ((waveID%5)+1);
    }
    switch(waveID){
      case 1:
        //index(3,1);
        index(1,10);
        break;
      case 2:
        index(1,10);
        break;
      case 3:
        index(2,15);
        break;
      case 4:
        index(1,5);
        index(2,5);
        index(1,10);
        break;
      case 5:
        index(3,1);
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