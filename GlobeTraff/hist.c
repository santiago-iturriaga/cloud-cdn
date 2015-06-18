#include <stdio.h>
#include <unistd.h>


int main(int argc, char **argv) { 

	char *fname = NULL; 
	FILE *fp;
	long int size, count; 
	int i = 0; 
	long int all_count = 0; 
	long int bs = -1; 
	long int big_count =  0; 
	float cnt = 0; 


	fname = argv[1]; 


	if ( (fp = fopen(fname, "r")) == NULL) { 
		printf("Error opening %s\n", fname); 
		exit(-1);
	}
	
	while (fscanf(fp, "%d %d\n", &size, &count) > 0) { 
		all_count += count; 
	}
		

	rewind(fp); 

	while (fscanf(fp, "%d %d\n", &size, &count) > 0) { 
		if (i < 20) { 
			cnt = (float) (100 * count) / all_count; 
			printf("%d %f\n", size, cnt);
			i++; 
		} else { 
			if (bs < 0) {
				bs = size; 
			}
			big_count += count; 
		} 
	} 
	
	cnt = (float) (100 * big_count) / all_count; 
	printf("%d+ %f\n", bs, cnt); 
	
	fclose(fp); 
	exit(0); 
}
		
