#include <stdio.h>

int main() { 

	float timest; 
	unsigned int fileid, size; 

	while (scanf("%f %u %u\n", &timest, &fileid, &size) > 0) { 
		printf("%u %u\n", fileid, size); 
	}

	return(0);
}
