class PopupText{
  boolean state;
  String showText;
  int fontSize;
  int startFrame;
  int showTime;
  int moveMode;
  float alpha;
  float speed;
  float startX;
  float startY;
  float destY;
  float x;
  float y;
  int textColor;
  
  void show(){
    pushStyle();
    textAlign(CENTER, CENTER);
    alpha = 255*(1 - 0.2*(y - destY)/30 - max(0,(realFrameCount - (startFrame + 0.5*showTime))/showTime)*2);
    switch(textColor){
      case COLOR_WHITE:
        fill(255,alpha);
        break;
        
      case COLOR_RED:
        fill(255,0,0,alpha);
        break;
      
      case COLOR_GREEN:
        fill(0,0,255,alpha);
        break;
        
      case COLOR_RAINBOW:
        colorMode(HSB, 360, 100, 100);
        fill(frameCount*20%360,100,100,alpha);
        break;
    }
    textFont(font[4]);
    switch(moveMode){
      case TEXT_MOVE:
        textSize(fontSize*(0.8 + 0.2*(1 - (y - destY)/30)));
        text(showText, x, y);
        break;
        
      case TEXT_NOMOVE:
        textSize(fontSize);
        text(showText, x, destY);
        break;
    }
    if(y > destY){
      y -= speed;
    }
    if(realFrameCount - startFrame > showTime){
      init();
    }
    popStyle();
  }
  
  void init(){
    state = false;
  }
  
  
  PopupText(String showText, float startX, float startY, int textColor, int fontSize, int moveMode){
    startFrame = realFrameCount;
    state = true;
    this.moveMode = moveMode;
    this.showText = showText;
    this.startX = startX + random(-3,3);
    this.startY = startY + random(-2,2);
    this.textColor = textColor;
    this.fontSize = fontSize;
    x = this.startX;
    y = this.startY;
    showTime = 36;
    speed = 6;
    destY = startY - speed*5;
  }
  
  PopupText(){
    state = false;
  }
}