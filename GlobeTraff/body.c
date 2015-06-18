/* 
   body  

   Takes a file of x, y pairs 
   and removes all pairs with x < arg1 and x > arg2, 
   that is remove the head and tail. 

   Al Fedoruk 
   CMPT855 
   April 2, 2001

*/

#include <stdio.h>

main ( int argc, char *argv[]) {

   int H = atoi(argv[1]);
   int T = atoi(argv[2]);
   int x = 0;
   int y = 0;

   while (! feof(stdin)) {
      fscanf(stdin, "%d %d", &x, &y); 
      
      if ( (x >= H) && (x <= T) ) {
         fprintf(stdout, "%d %d\n", x, y);      
      }
   }
}

