#include <stdio.h>

#define MAX_SIZE 1000000
long int sizes[MAX_SIZE]; 
int counts[MAX_SIZE]; 

long int check_for_size(long int); 

int main() { 

	long int fileid, size; 
	float timest; 
	long int count = 0; 
	long int temp; 

	for (temp = 0; temp < MAX_SIZE; temp++) { 
		sizes[temp] = -1; 
		counts[temp] = -1; 
	}

	while(scanf("%f %d %d\n", &timest, &fileid, &size) > 0) { 
		temp = check_for_size(size); 
		if (temp == -1) {  //first time 
			sizes[count] = size; 
			counts[count] = 1; 	
			count++; 
		} else { 
			counts[temp] += 1; 
		}
	} 
	
	for (temp = 0; temp < MAX_SIZE; temp++) { 
		if (sizes[temp] == -1) {
			return(0); 
		}
		printf("%d  - %d\n", sizes[temp], counts[temp]); 
	}
	return(0); 
}

long int check_for_size(long int size) { 
	long int i; 

	for (i = 0; i < MAX_SIZE; i++) { 
		if (sizes[i] == -1) { 
			return(-1); 
		} 
		if (sizes[i] == size) { 
			return(i); 
		}
	}
	printf("PANIC\n"); 
}
