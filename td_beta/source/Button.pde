class Button{
  int w;
  int h;
  int x;
  int y;
  PFont fontType;
  int showState;
  String word;
  
  void show(){
    pushStyle();
    textAlign(CENTER, CENTER);
    if(showState == ENABLED){
      fill(#F7CF2A);
    }else if(showState == UNCLICKABLE){
      fill(130);
    }else if(mouseCheck(x,y,w,h)){
      fill(0,255,0);
    }else{
      fill(255);
    }
    strokeWeight(5);
    stroke(0);
    rect(x,y,w,h);
    fill(0);
    noStroke();
    textFont(fontType);
    text(word,x+w/2,y+h/2);
    popStyle();
  }
  
  
  Button(int x, int y, int w, int h, String word){
    this.w = w;
    this.h = h;
    this.x = x;
    this.y = y;
    this.word = word;
    this.fontType = font[2];
    showState = 0;
  }
  
  Button(int x, int y, int w, int h, String word, PFont font){
    this.w = w;
    this.h = h;
    this.x = x;
    this.y = y;
    this.word = word;
    this.fontType = font;
    showState = 0;
  }
  
  Button(int x, int y, int w, int h, String word, PFont font, int clickstate){
    this.w = w;
    this.h = h;
    this.x = x;
    this.y = y;
    this.word = word;
    this.fontType = font;
    showState = clickstate;
  }
  
  Button(int x, int y, int w, int h, String word, int clickstate){
    this.w = w;
    this.h = h;
    this.x = x;
    this.y = y;
    this.word = word;
    this.fontType = font[2];
    showState = clickstate;
  }
  
}