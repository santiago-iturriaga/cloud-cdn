/* avgpop 

   reads from stdin, averages the popularities. that is, 
   if N documents n, n+1, n+2 all have same 
   popularity, replace with 1 row.  

   Al Fedoruk
   April 2, 2001

*/

#include <stdio.h>

main () {

   int i = 1;
   int rank = 0;
   int popularity = 0;
   int prevpop = 0;

   while ( fscanf(stdin, "%d %d", &rank, &popularity) != EOF) { 
      if (popularity != prevpop) 
         fprintf(stdout, "%d %d\n", i++, popularity);      
      prevpop = popularity; 
   };
}

