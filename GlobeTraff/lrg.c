/* Average 

   calculates the mean file size

   Al Fedoruk
   CMPT855
   April 7, 2001
*/

#include <stdio.h>

main () {

   int i = 0;
   int id = 0;
   long int size = 0;
   long int large = 0; 

   while ( fscanf(stdin, "%d %d", &id, &size) != EOF){ 
      if (size > large)  large = size ;
   };

   printf("largest: %d \n", large );
}

