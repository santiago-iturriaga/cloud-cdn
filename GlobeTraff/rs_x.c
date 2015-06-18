#include <stdio.h>

int main() { 
	unsigned int current, biggest;
        float dummy; 
	biggest = 0; 

	while (scanf("%u %f\n", &current, &dummy) > 0) { 
		if (current > biggest) { 
			biggest = current; 
		}
	}
	printf("%u\n", biggest + 5); 
	return(0); 
}

