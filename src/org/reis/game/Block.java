package org.reis.game;

import java.util.Random;

public class Block {
    // bit coordinate cube vertices and faces
    public static final int[][] vertices = {
        {0,1,0},/*_ _*/{1,1,0},
/*      |\             |\
        | \            | \
        |*/{0,1,1},/*_ _*/{1,1,1},
/*      |  |           |  |
        |  |           |  |
*/      {0,0,0},/*_ _*/{1,0,0},
/*       \ |            \ |
          \|_ _ _ _ _ _ _\|
*/         {0,0,1},       {1,0,1}
    };
/*

   3  2
    \ |
     \|
6 - - 0 - - 1
      |\
      | \
      5  4

 000 001 010 011 100 101 110
 zyx 00+ 0+0 -00 +00 0-0 00-

*/
    public static final int[][] faces = {
        {3,7,5,1}, {0,2,3,1}, {1,5,4,0},
        {2,6,7,3}, {5,7,6,4}, {0,4,6,2}
    };

    public int id = new Random().nextInt(16);
}
