/**
final int gameplayScreenX = 1200;
final int gameplayScreenY = 600;
final int gridSize = 60;
final int gridCount = (gameplayScreenX / gridSize) * (gameplayScreenY / gridSize);
boolean [] clickedState = new boolean[gridCount];

void setup(){
  size(1280,800);
  for(int i = 0; i < gridCount; i ++){
    clickedState[i] = false;
  }
}

void draw(){
  fill(0);
  pushMatrix();
  translate(0,50);
  rect(0,0,gameplayScreenX,gameplayScreenY);
  stroke(255);
  for(int i = 0; i < 1200/gridSize; i++){
    for(int j = 0; j < 600/gridSize; j++){
      int gridX = i*gridSize;
      int gridY = j*gridSize;
      if(mouseCheck(gridX,gridY)){
        fill(255);
      }else if(clickedState[i*10+j]){
        fill(0,255,0);
      }else{
        noFill();
      }
      if(mousePressed && (mouseButton == LEFT) && mouseCheck(gridX,gridY)){
        clickedState[i*10+j] = true;
      }else if(mousePressed && (mouseButton == RIGHT) && mouseCheck(gridX,gridY)){
        clickedState[i*10+j] = false;
      }
      rect(gridX,gridY,gridSize,gridSize);
    }
  }
  popMatrix();
  println(mouseX + ", " + mouseY);
}

boolean mouseCheck(int x, int y){
  return(mouseX > x && mouseX <= x + gridSize && mouseY > y + 50&& mouseY <= y + gridSize + 50);
}
**/