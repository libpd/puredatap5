import com.noisepages.nettoyeur.*;

HelloLibrary hello;

void setup() {
  size(400,400);
  smooth();
  
  hello = new PdJackLibrary(this, 2, 2);
  
  PFont font = createFont("",40);
  textFont(font);
}

void draw() {
  background(0);
  fill(255);
  text(hello.sayHello(), 40, 200);
}