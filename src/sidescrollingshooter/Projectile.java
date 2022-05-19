/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidescrollingshooter;

import throwinghearts.NPC;
import collisionrequired.Sprite;
import collisionrequired.Collisionable;

/**
 *
 * @author Jul
 */

public class Projectile extends Sprite
{
  public int degrees;//projectiles rotate mid-air for visual effect
  public double speedX =0;
  public final ShooterHolder shooter;
  public final boolean isSticky;
  public final ProjectilePlan plan;
  public boolean active=true;
  public Projectile( int x, int y,int h,int l,ShooterHolder sh,ProjectilePlan pl,
              boolean isSticky)
  {
    //it doesn't use the notion of flipped, and for images it goes
    //to its ProjectilePlan
    super(null,null,x,y,h,l,null,null,0);
    degrees = 1;
    shooter = sh;
    this.isSticky=isSticky;
    plan = pl;
  }
  public boolean collide(SideScrollingShooter SSS)
  {
    Collisionable co = null;
    boolean ok = false;
    if(this.fathers==null) return false;
    for(int j = 0; j<this.fathers.size();j++)
      for(int i = 0; i<fathers.get(j).objects.size();i++)
      {
        co = fathers.get(j).objects.get(i);
        ok = collideWith(co,SSS);
        if(ok) break;
      }
    if(!ok) return false;
    //do what is necessary to what it hit
    if( co == SSS.player && shooter != SSS.player )
      SSS.receiveEvent(SideScrollingShooter.PLAYERHIT);
    if( co instanceof NPC)
    {
      NPC npc = (NPC)co;
      SSS.receiveEvent(SideScrollingShooter.MONSTERHIT, npc, shooter);
    }
    //apply effects to projectile
  //  y=oldY;
  //  x=oldX;
    SSS.projectiles.remove(this);
    active=false;
    if(isSticky && ! (co instanceof NPC) )
      SSS.collis.add(this);
    else
      SSS.root.remove(this);
    return true;
  }
  
  public boolean collideWith(Collisionable co, SideScrollingShooter SSS)
  {
    double endX = x + length;
    double endY = y + height;
    double coendY = co.y+co.height;
    double coendX = co.x+co.length;
    //a Sprite cannot collide with itself, so let's check if it is itself first
    if (this == co)
      return false;
    if (shooter == co)return false;
    boolean yBetweenCoYBorders   =co.y<=y      && y      <=coendY;
    boolean coYBetweenYBorders   =y   <=co.y   && co.y   <=endY;
    boolean endYBetweenCoYBorders=co.y<=endY   && endY   <=coendY;
    boolean coendYBetweenYBorders=y   <=coendY && coendY <=endY;
    
    boolean xBetweenCoXBorders   =co.x<=x      && x      <=coendX;
    boolean coXBetweenXBorders   =x   <=co.x   && co.x   <=endX;
    boolean endXBetweenCoXBorders=co.x<=endX   && endX   <=coendX;
    boolean coendXBetweenXBorders=x   <=coendX && coendX <=endX;
    //Verifying collisions
    if( (yBetweenCoYBorders || coYBetweenYBorders
        || endYBetweenCoYBorders || coendYBetweenYBorders
        || y>= co.y && endY<= coendY || co.y>= y && coendY<= endY)
    //    || co.y>= y && coendY>= endY || y>= co.y && endY>= coendY)
      && (xBetweenCoXBorders || coXBetweenXBorders
        || endXBetweenCoXBorders || coendXBetweenXBorders
        || x>= co.x && endX<= coendX || co.x>= x && coendX<= endX)
       // || co.x>= x && coendX>= endX || x>= co.x && endX>= coendX)
     /* ||(
          (x>= co.x && endX<= coendX /*&& oldX >= co.x && oldX + length <= coendX)
        &&(
            (endY <=co.y && coendY <= oldY )
          ||(oldY + height <= co.y && coendY <= y)
          )
        )*/
      )
    {
      //if what it is hitting is a moving projectile then let the implementation
      //decide whether to count it
      if( SSS.projectiles.contains(co))
      {
        boolean ok =SSS.receiveEvent(SideScrollingShooter.PROJECTILEHIT,co,this);
        return ok;
      }
      else
        return true;
    }
    return false;
  }
  
  public void updateRadians()
  {
    current =plan.degreeImages[degrees++];
    if(degrees==360)degrees=0;
  }
}