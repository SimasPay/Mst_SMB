/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

<!-- Paste this code into an external JavaScript file  -->

/* This script and many more are available free online at
The JavaScript Source :: http://javascript.internet.com
Created by: Steve DeGroof :: http://degroof.blogspot.com */

// a slider control class
// Limitation: slider controls must be positioned at absolute coordinates within the document.

// document-scope mouse button indicator
var SliderControlIsMouseDown = false;

// notifies slider object that mouse button has been pressed
function SliderControlMouseDown(e) {
  // stupid kludge because "this" is overwritten on events
  this.parent.isMouseDown=true;
  SliderControlIsMouseDown=true;
  // set the thumb to the current position
  this.parent.setThumb();
}

// notifies slider object that mouse button has been released
function SliderControlMouseUp(e) {
  // stupid kludge because "this" is overwritten on events
  this.parent.isMouseDown=false;
  SliderControlIsMouseDown=false;
}

// notifies slider object that mouse moved
function SliderControlMouseMove(e) {
  // stupid kludge because "this" is overwritten on events
  var parent = this.parent;
  var tempX = 0;
  var tempY = 0;
  if(navigator.appName == "Netscape"){
    tempX = e.pageX
    tempY = e.pageY
  } else {
    tempX = window.event.clientX + window.document.body.scrollLeft
    tempY = window.event.clientY + window.document.body.scrollTop
  }
  parent.mouseX=tempX-parent.x;
  parent.mouseY=tempY-parent.y;
  parent.setThumb();
}

// check mouse button and position, then update as needed
function SliderControlSetThumb() {
  if (  // if the mouse is outside the extents of the control or the mouse button is up...
    this.mouseX>this.width  ||
    this.mouseX<0  ||
    this.mouseY>this.height  ||
    this.mouseY<0 ||
    SliderControlIsMouseDown==false) {
      this.isMouseDown=false;
  }
  if (this.isMouseDown) {
    this.UpdateThumbPosition(true);
  }
}

// notifies control that mouse button was released anywhere on the document
function SliderControlDocMouseUp(e) {
  SliderControlIsMouseDown = false;
}

// resize the control
function SliderControlSetSize(w,h) {
  this.width=w;
  this.height=h;
  this.slider.style.width=this.width;
  this.slider.style.height=this.height;
  this.UpdateThumbPosition(true);
}

// set control position
function SliderControlSetPosition(x,y) {
  this.x=x;
  this.y=y;
  this.slider.style.left=this.x;
  this.slider.style.top=this.y;
  this.UpdateThumbPosition(true);
}

// set tracking
// true, false = horizontal slider
// false, true = vertical slider
// true, true  = x/y control
function SliderControlSetTracking(x,y) {
  this.trackX=x;
  this.trackY=y;
  this.UpdateThumbPosition(true);
}

// callback on value change
function SliderControlNotify(x,y) {
  // dummy callback, do nothing
}

// update the slider control position and optionally notify a listener
function SliderControlUpdateThumbPosition(notify) {
  // check horizontal vs vertical vs xy
  if (this.trackX) {
    this.thumbWidth=this.thumbSize;
  } else {
    this.thumbWidth=this.width;
  }
  if (this.trackY) {
    this.thumbHeight=this.thumbSize;
  } else {
    this.thumbHeight=this.height;
  }
  this.thumb.style.width=this.thumbWidth;
  this.thumb.style.height=this.thumbHeight;

  // limit thumb movement
  if (this.mouseX>this.width-this.thumbWidth/2) {
    this.mouseX=this.width-this.thumbWidth/2;
  }
  if (this.mouseY>this.height-this.thumbHeight/2) {
    this.mouseY=this.height-this.thumbHeight/2;
  }
  if (this.mouseX<this.thumbWidth/2) {
    this.mouseX=this.thumbWidth/2;
  }
  if (this.mouseY<this.thumbHeight/2) {
    this.mouseY=this.thumbHeight/2;
  }
  if (!this.trackX) {
    this.mouseX=this.width/2;
  }
  if (!this.trackY) {
    this.mouseY=this.height/2;
  }

  // update x and y values (0..1)
  if (this.trackX) {
    this.xValue=(this.mouseX-this.thumbWidth/2)/(this.width-this.thumbWidth);
  } else {
    this.xValue=0;
  }
  if (this.trackY) {
    this.yValue=(this.mouseY-this.thumbHeight/2)/(this.height-this.thumbHeight);
  } else {
    this.yValue=0;
  }
  this.thumb.style.left=this.x+this.mouseX-this.thumbWidth/2;
  this.thumb.style.top=this.y+this.mouseY-this.thumbHeight/2;

  // notify listener
  if (notify) {
    if (this.trackY && this.trackX) {
      this.notify(this.xValue,this.yValue);
    }
    if (this.trackY && !this.trackX) {
      this.notify(this.yValue,this.yValue);
    }
    if (!this.trackY && this.trackX) {
      this.notify(this.xValue,this.xValue);
    }
  }
}

// set slider colors
function SliderControlSetColors(color,thumbColor) {
  this.color=color;
  this.thumbColor=thumbColor;
  this.slider.style.backgroundColor=this.color;
  this.slider.style.border='solid 1px blue';
  this.thumb.style.backgroundColor=this.thumbColor;
}

// set x and y values and optionally notify listener
function SliderControlSetValue(x,y,notify) {
  this.xValue=x;
  this.yValue=y;
  if(x == undefined || isNaN(x)) {
    x=0.5;
  }
  if (x>1) {
    x=1;
  }
  if (x<0) {
    x=0;
  }
  if(y == undefined || isNaN(y)) {
    y=x;
  }
  if (this.trackX) {
    this.xValue=x;
    this.mouseX=(this.xValue*(this.width-this.thumbWidth))+this.thumbWidth/2;
  }
  if (this.trackY) {
    this.yValue=y;
    this.mouseY=(this.yValue*(this.height-this.thumbHeight))+this.thumbHeight/2;
  }
  if(notify == undefined) {
    notify = true;
  }
  this.UpdateThumbPosition(notify);
}

// set callback function
// function should have 2 parameters
// e.g. sliderMoved(x,y)
function SliderControlSetNotifier(func) {
  if(typeof func == 'function') {
    this.notify=func;
  } else {
    alert('Notifier must be a function with 2 parameters');
  }
}

// set the thumb size
function SliderControlSetThumbSize(sz) {
  this.thumbSize=sz;
  this.UpdateThumbPosition(true);
}

// constructor
function SliderControl() {
  // set a bunch of default values
  this.thumbWidth=10;
  this.thumbHeight=3;
  this.thumbSize=3;
  this.xValue=0;
  this.yValue=0;
  this.x=0;
  this.y=0;
  this.mouseX=0;
  this.mouseY=0;
  this.width=10;
  this.height=10;
  this.color='#FFFFFF';
  this.thumbColor='#000000';
  this.trackX=true;
  this.trackY=true;
  this.isMouseDown=false;
  // set up main slider body
  this.slider = document.createElement('div');
  this.slider.style.zIndex=5;
  this.slider.style.width=this.width;
  this.slider.style.height=this.height;
  this.slider.style.left=this.x;
  this.slider.style.top=this.y;
  this.slider.style.backgroundColor=this.color;
  this.slider.style.visibility='visible';
  this.slider.style.position='absolute';
  this.slider.style.fontSize='0';
  this.slider.parent=this;
  document.body.appendChild(this.slider);
  // set up methods
  this.mouseDown=SliderControlMouseDown;
  this.setValue=SliderControlSetValue;
  this.setThumb=SliderControlSetThumb;
  this.setThumbSize=SliderControlSetThumbSize;
  this.setSize=SliderControlSetSize;
  this.setPosition=SliderControlSetPosition;
  this.setTracking=SliderControlSetTracking;
  this.setColors=SliderControlSetColors;
  this.setNotifier=SliderControlSetNotifier;
  this.UpdateThumbPosition=SliderControlUpdateThumbPosition;
  this.mouseUp=SliderControlMouseUp;
  this.mouseMove=SliderControlMouseMove;
  this.notify=SliderControlNotify;
  this.slider.onmousedown=this.mouseDown;
  this.slider.onmouseup=this.mouseUp;
  this.slider.onmousemove=this.mouseMove;
  // set up 'thumb' element
  this.thumb = document.createElement('div');
  this.thumb.style.zIndex=6;
  this.thumb.style.width=this.thumbWidth;
  this.thumb.style.height=this.thumbHeight;
  this.thumb.style.left=this.x+this.mouseX;
  this.thumb.style.top=this.y+this.mouseY;
  this.thumb.style.backgroundColor=this.thumbColor;
  this.thumb.style.visibility='visible';
  this.thumb.style.position='absolute';
  this.thumb.style.fontSize='0';
  document.body.appendChild(this.thumb);
  this.thumb.onmousedown=this.mouseDown;
  this.thumb.onmouseup=this.mouseUp;
  this.thumb.onmousemove=this.mouseMove;
  this.thumb.parent=this;
  document.onmouseup=SliderControlDocMouseUp;
}

// end of SliderControl class


//color controls

var slLoanAmt;

function init() {

  slLoanAmt=new SliderControl();

  slLoanAmt.setNotifier(calculateRgb);
  slLoanAmt.setSize(200,15);
  slLoanAmt.setPosition(75,10);
  slLoanAmt.setTracking(true, false);
  slLoanAmt.setValue(5);
  slLoanAmt.setColors('lightblue','blue');
}


//listens for any rgb slider change
//calculates html color and hsl slider values
function calculateRgb(x,y) {
 
}