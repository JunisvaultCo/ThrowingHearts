/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidescrollingshooter;

import java.awt.Point;
import java.awt.image.BufferedImage;
import collisionrequired.*;

/**
 *
 * @author Jul
 */
public class ShooterHolder extends Sprite
{
  public boolean hasShooter=false;
  public Shooter shooter;
  public Point lastAim;
  boolean hasAim = false;
  public Point target;
  public boolean hasTarget =false;
  public long rechargeTime;
  public long lastTrigger;  
  public boolean hasInfiniteAmmo;
  public int ammo;
  public BufferedImage degreeImages[];
  public ProjectilePlan plan;
  public boolean ok1;
  public boolean ok2;
  
  private int offsetx;
  private int offsety;
    
  public ShooterHolder(BufferedImage bi, BufferedImage flipped,int x,int y,
          int h, int l,String img, String imgF, int speed,long rt,boolean hasInfiniteAmmo,
          int ammo,ProjectilePlan plan,int offx, int offy)
  {
    super(bi,flipped,x,y,h,l,img,imgF,speed);
    rechargeTime = rt;
    this.hasInfiniteAmmo= hasInfiniteAmmo;
    this.ammo = ammo; 
    this.plan=plan;
    offsetx=offx;
    offsety=offy;
  }
     
  public void setAimPoint(SideScrollingShooter s)
  {
  if(!hasAim) return;
    double origX;
    if(isFlipped)
      origX = x+offsetx;
    else 
      origX = x-offsetx;
    double origY = y+offsety;
    setFlipped( (lastAim.x-origX-s.addX)<0); 
    shooter.tan = (lastAim.y-origY-s.addY)/(lastAim.x-origX-s.addX);
    shooter.setCurrent(isFlipped,s);
  }
  public void setShooter( Shooter sh)
  {
    hasShooter = true;
    shooter = sh;  
  }
  public void setTarget(Point tr)
  {
    target = tr;
    hasTarget=true;
    setAim(tr);
  }
  public void setAim(Point a)
  {
    lastAim = a;
    hasAim=true;
  }
  //overloaded shoot(for monster and player)
  //player
  public void shoot(Point po,SideScrollingShooter s,boolean sticky)
  {
    if(!hasShooter) return;
    if(!hasInfiniteAmmo && ammo <= 0) return;
    try
    {
      int origX;
      int rl = shooter.realLength;
      if(!hasInfiniteAmmo)
        ammo--;
    
      if(!isFlipped)
        origX = (int)(this.x+offsetx);
      else
        origX = (int)(this.x+this.length-this.plan.length-offsetx);
      int origY = (int)(this.y+offsety);
      double dY = (po.getY()-origY-s.addY);
      double dX = (po.getX()-origX-s.addX);
      origY += rl*dY/Math.sqrt(dX*dX+dY*dY);
      origX += (int) rl*dX/ Math.sqrt(dX*dX+dY*dY);
      //if it's creating maps mode make them sticky, else make them disappear
      //(TODO: Change it)
      Projectile p=new Projectile(origX,origY,10,10,this,plan,sticky);
      s.projectiles.add(p);
      s.root.insert(p);
      p.speedY0= (5*60* dY)/Math.sqrt(dX*dX + dY *dY);
      p.start = System.nanoTime();
      p.speedX =(5* dX)/Math.sqrt(dX*dX + dY *dY);
    }
    catch (Exception e) {e.printStackTrace();}
  }
  //helper function for (monster) boolean shoot(s,proj,col)
  double chooseTg(double dif, Sprite s, double origY,int i)
  {
    double g = Sprite.ACCELERATION;
    double force = 300;
    double a = (dif*dif*g)/(force*force*2);
    double b = dif;
    double c = origY + (dif *dif * g)/(force*force *2);
    double delta1 = b*b - 4*a*(c-s.y); 
    double delta2 = b*b - 4*a*(c-s.y-s.height+10);
    double tg1 = (-b - Math.sqrt(delta1))/(2*a);
    double tg2 = (-b + Math.sqrt(delta1))/(2*a);
    double tg3 = (-b - Math.sqrt(delta2))/(2*a);
    double tg4 = (-b + Math.sqrt(delta2))/(2*a);
    // tg1<= tg<= tg2
    // tg <= tg3 || tg >= tg4
    // smaller tg is better
    double firstTg=1;
    if(i==1)ok1=true;
    else ok2=true;
    if (delta1 <0 && delta2 < 0)
    {
      if(i==1)
        ok1=false;
      else ok2=false;
    }
    else if ( delta1 < 0)
      firstTg = tg3;
    else if ( delta2 < 0)
    {
      if(i==1)
        ok1=false;
      else ok2=false;
    }
    else if(tg3 > tg1 && tg2 >  tg4)
    {
      if(i==1)
        ok1=false;
      else ok2=false;
    }
    else if(tg3 > tg1 && tg2 <= tg4)
    {
      if(Math.abs(tg2) < Math.abs(tg4))
        return tg2;
      return tg4;
    }
    else if(tg3 <= tg1 && tg2>tg4)
    {
      if(Math.abs(tg3) < Math.abs(tg1))
        return tg3;
      return tg1;
    }
    else if(tg2<=tg3)
    {
      if(Math.abs(tg3) < Math.abs(tg4))
        return tg3;
      return tg4;
    }
    else if(tg1>tg4)
    {
      if(Math.abs(tg3) < Math.abs(tg4))
        return tg3;
      return tg4;
    }
    else
    {
      firstTg = tg3;
      if(Math.abs(firstTg) > Math.abs(tg1))
        firstTg= tg1;
      if(Math.abs(firstTg) > Math.abs(tg2))
        firstTg= tg2;
      if(Math.abs(firstTg) > Math.abs(tg4))
        return tg4;
    }
    return firstTg;
  }
  //monster. Boolean returns whether it can hit
  public boolean shoot(Sprite s, SideScrollingShooter SSS)
  {
    if(!hasShooter) return false;
    if(!hasInfiniteAmmo && ammo <= 0) return false;
    int origX;
    int rl = shooter.realLength;
         
    if(!isFlipped)
      origX = (int)(this.x+5);
    else
      origX = (int)(this.x+this.length-this.plan.length-5);
    int origY = (int)(this.y+31);
    double dif = s.x - origX;
    double firstTg = chooseTg(dif,s,origY,1);
    dif = s.x+s.length - origX;
    double secondTg = chooseTg(dif,s,origY,2);
    double realTg;
    double realX;
    if(!ok1&&!ok2) return false;
    if (!ok1 && ok2)
    {
      realTg = secondTg;
      realX=s.x+s.length;
    }
    else if (ok1&&!ok2)
    {
      realTg = firstTg;
      realX=s.x;
    }
    else if( Math.abs(secondTg) < Math.abs(firstTg))
    {
      realTg = secondTg;
      realX=s.x+s.length;
    }
    else
    {
      realTg = firstTg;
      realX=s.x;
    }
    double angle = Math.atan(realTg);
    double sin = Math.sin(angle);
    double cos = Math.cos(angle);
    origY += rl*sin;
    origX += (int) rl*cos;
    Projectile p=new Projectile(origX,origY,10,10,this,plan,false);
    SSS.projectiles.add(p);
    SSS.root.insert(p);
    if(s.y < origY )
      p.speedY0= -300* Math.abs(sin);
    else
      p.speedY0 = 300*Math.abs(sin);
    p.start = System.nanoTime();
    if(s.x < origX)
      p.speedX =-5*Math.abs(cos);
    else
      p.speedX = 5*Math.abs(cos);

    if(!hasInfiniteAmmo)
      ammo--;
    return true;
  }
}
    