/* rnd 

   reads number pairs from stdin. 
   Rounds the numbers to 2 decimal places and 
   prints the pair on stdout. 


   Al Fedoruk 
   CMPT855 
   April 2, 2001
*/

#include <stdio.h>

main () {

   int i = 1;
   float x;  
   float y;

   while (! feof(stdin)) {
      fscanf(stdin, "%f %f", &x, &y); 
      fprintf(stdout, "%6.2f %6.2f\n", x, y);      
   }
}

