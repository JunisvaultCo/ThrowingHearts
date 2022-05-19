/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidescrollingshooter;

/**
 *
 * @author Jul
 */
public class Util
{//slightly modified, returns the position of the closest to the actual one,
 //unlike the java API one which returns -1 if it didn't find it, or the position
 //it is at.
  static int binarySearch(double[] array,double searched, int begin, int end)
  {
    int lower, higher;
    lower = begin;
    higher = end;
    while(lower != higher && lower != higher-1)
    {
      int middle = (lower+higher)/2;
      if( array[middle] < searched)
        lower = middle;
      else if(array[middle]== searched)
        return middle;
      else
        higher= middle;
    }
    return lower;
  }
}