#include <stdlib.h>
#include <stdio.h>

int main(int argc, char *argv[]) { 

	unsigned int *sizes; 
	unsigned int *pops; 
        unsigned int lines; 

	if (argc < 2) { 
		printf("Need the size of the input file\n"); 
		exit(-1); 
	}

	lines = atoi(argv[1]); 
	printf("%u\n", lines); 
	
	sizes = malloc(lines * sizeof(unsigned int)); 
	pops = malloc(lines * sizeof(unsigned int)); 



	free(sizes);
	free(pops);	
	return(0);
}	
