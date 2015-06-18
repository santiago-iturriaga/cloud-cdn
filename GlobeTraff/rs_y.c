#include <stdio.h>

int main() { 
	float current, biggest;
        unsigned int  dummy; 
	biggest = 0; 

	while (scanf("%u %f\n", &dummy, &current) > 0) { 
		if (current > biggest) { 
			biggest = current; 
		}
	}
	printf("%f\n", biggest + 5); 
	return(0); 
}

