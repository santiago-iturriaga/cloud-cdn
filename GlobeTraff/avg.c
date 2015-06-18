/* Average 

   calculates the mean file size

   Al Fedoruk
   CMPT855
   April 7, 2001
*/

#include <stdio.h>
#include <math.h>

main () {

   int i = 0;
   int id = 0;
   long double size = 0;
   long double sum = 0.0;
   long double sqrsum = 0.0; 
   long double stdev = 0.0;

   while ( fscanf(stdin, "%d %g", &id, &size) != EOF){ 
      i++; 
      sum = sum + size;
      sqrsum = sqrsum + ( size * size);
   };

   stdev = ((i * sqrsum) - ( sum * sum)) / ( i * ( i - 1));

   printf("Number of files: %d Mean Size: %Lg \n", i, sum/i );
   printf("Stddev: %Lg \n", stdev );

}

