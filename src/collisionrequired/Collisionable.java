/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisionrequired;

import java.util.ArrayList;

/**
 *
 * @author Jul
 */
public class Collisionable
{
  public double x;
  public double y;
  public double oldY;
  public double oldX;
  public int height;
  public int length;
  //for quadtree collision detecting
  public ArrayList<Quadtree> fathers;//where it is contained
  //
  
}