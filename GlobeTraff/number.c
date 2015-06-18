/* number 

   reads number pairs from stdin strips out all extraneous blanks, tabs etc 
   and reprints the pairs, numbered. 

   Al Fedoruk
   CMPT855
   April 2, 2001
*/

#include <stdio.h>

main () {

   int i = 1;
   int pop = 0;
   int size = 0;

   while (! feof(stdin)) {
      fscanf(stdin, "%d %d", &pop, &size); 
      fprintf(stdout, "%d %d\n", i++, pop);      
   };
}

