/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisionrequired;

import java.awt.Point;

/**
 *
 * @author Jul
 */
public class Platform extends Collisionable
{
  public int dirFlag;
  public final static int HORIZONTAL=1;
  public final static int VERTICAL=2;
  public final static int DIAGONALRISING=3;
  public final static int DIAGONALFALLING=4;
  public Platform(int flg, Point or, int length, int height)
  {
    dirFlag = flg;
    x = or.x;
    y = or.y;
    this.length = length;
    this.height = height;
  }
}