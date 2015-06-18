#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "hash.h"

int main(int argc, char **argv) {
   long long int i;
   struct htab *curhash;
   char op[MAXCOMMAND];
   char *arr[1000000]; 
 
  
   for (i = 0; i < 1000000; i++) { 
	arr[i] = NULL; 
   }

   i = 0;  
   while (gettoken(op) > 0) {
	curhash = findhash(op); 

	if (curhash == NULL) { 
		curhash = addhash(op, 1); 
		arr[i] = malloc(MAXCOMMAND * sizeof(char)); 
		strcpy(arr[i], op); 
		i++;
	} else { 
		curhash->data += 1; 
	} 

    }
    for (i = 0; i < 1000000; i++) { 
	if (arr[i] != NULL) { 	
		curhash = findhash(arr[i]); 
		printf("%d %s\n", curhash->data, curhash->key); 
	}
    }
    return(0);
}
