/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisionrequired;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import sidescrollingshooter.Projectile;
import sidescrollingshooter.SideScrollingShooter;
import throwinghearts.NPC;

/**
 *
 * @author Jul
 */
public class Sprite extends Collisionable
{
  public BufferedImage bi;
  public BufferedImage flipped;
  public BufferedImage current;
  public boolean isFlipped;
  
  public String nameFlipped;
  public String nameBI;
  
  public String movementX;
  public double startY;
  public double speedY;
  public double speedY0;
  public double speedX0;
  public static final int ACCELERATION = 200;
  public long start;
  public boolean isOnPlat;
  public double firstX;
  public double lastX;
  public boolean isDoubleJump;
  public int speedJump;
    
      
  public Sprite(BufferedImage bi,BufferedImage flipped,int x,int y,int h,int l,
          String image, String imageFlipped, int speedX)
  {
    this.bi = bi;
    this.flipped = flipped;
    this.x=x;
    this.y=y;
    firstX=x;
    lastX=x+l;
    nameFlipped=imageFlipped;
    nameBI= image;
    this.speedX0= speedX;
    
    startY=y;
    oldY=y;
    oldX=x;
    speedY=0;
    speedY0=-150;
    start=0;
    isDoubleJump=false;
    speedJump = -200;//TODO: different jumps?
    height=h;
    length=l;
    setFlipped(false);
  }
  public void setFlipped(boolean flip)
  {
    isFlipped= flip;
    current= (flip)?flipped:bi;
  }
  public void yMovement()
  {
    //if it is staying on a platform just don't move
    if(isOnPlat ) return;
    //update y position according to law of Physics (gravitational attraction)
    double airTime = (System.nanoTime() -start)/1000000000.0;
    speedY = speedY0 + Sprite.ACCELERATION*airTime;
    oldY = y;
    y = startY+speedY0 * airTime + Sprite.ACCELERATION*airTime*airTime/2;
  }
  public Collisionable checkCollisions(ArrayList<Collisionable> col, SideScrollingShooter SSS)
  {
    //Checking different collisions
    isOnPlat= false;
    for(int j = 0; j< fathers.size();j++)
      for(int i = 0; i< fathers.get(j).objects.size();i++)
      {
        Collisionable co;
        co =fathers.get(j).objects.get(i);
        //a Sprite cannot collide with itself, so let's check if it is itself first
        if (this == co) continue;
        if (co instanceof Projectile && ((Projectile)co).active )
          if( this != ((Projectile)co).shooter)
          {
            ((Projectile)co).collideWith(this, SSS);
            continue;
          }
          else continue;
        //Verifying lateral collisions (from left)
        if(co.x <= x + length && co.x>=oldX+ length && (co.y < y+height && co.y + co.height> y) )
        {
          x=co.x - length;
          oldX= x;
        } // from right
        if ( co.x+co.length >=x && co.x+co.length<=oldX && (co.y < y+height && co.y+co.height> y) )
        {
          x=co.x+co.length;
          oldX= x;
        }
        //Verifying upper collisions
        if( co.x < x+length && x < co.x+co.length)
        {
          if (co.y <= y+height && oldY + height <=co.y)
          {
            y= co.y-height;
            oldY = y;
            //stop the downward movement
            speedY0= 0;
            start = System.nanoTime();
            speedY = 0;
            startY = y;
            isDoubleJump = false;
            isOnPlat=true;
            firstX= co.x;
            lastX = co.x+co.length;
          }
          //Did it hit a horizontal platform from below?
          if (co.y+co.height>= y+1 && co.y+co.height <= oldY+1)
          {
            y =oldY;
            startY = y + 1;
            start = System.nanoTime();
            //then according to physics move in the other direction
            speedY0 = - speedY;
          }
        }
      }
    return null;
  }
}