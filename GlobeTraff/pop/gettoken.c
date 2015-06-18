#include <ctype.h>
#include <stdio.h>
#include "hash.h"

int gettoken(char *s) {
   int i, c;
   
   i = c = 0;

   while ((c = getch()) == ' ' || c == '\t' || c == '\n');
   
   s[0] = c;
   
   i=0;

   while ((isalnum((s[++i] = c = getch()))) && (i < MAXCOMMAND - 1)); 

   s[i] = '\0';
   
   if (c != EOF) {
      ungetch(c);
   }
   else {
      return 0;
   }
   
   return i;
}
