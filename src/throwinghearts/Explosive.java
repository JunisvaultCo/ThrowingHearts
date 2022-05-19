/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package throwinghearts;

import collisionrequired.*;
import sidescrollingshooter.*;
import java.util.ArrayList;

/**
 *
 * @author Jul
 */
public class Explosive extends Projectile
{
  public int destroyRadius;
  public int explodeRadius;
  public Explosive(int x, int y,int h,int l,ShooterHolder sh,ProjectilePlan plan)
  {
    super(x,y,h,l,sh,plan,false);
  }
  //TODO: more
  public void explode(ArrayList<Collisionable> c)
  {
    c.remove(this);
  }
}