/* Compute interarrival time from an arrival time trace */

#include <stdio.h>

int
main()
  {
    float newtime, oldtime;
    unsigned int dummy1, dummy2; 
    unsigned int packet = 0; 

    scanf("%f %u %u\n", &oldtime, &dummy1, &dummy2);

    while( scanf("%f %u %u\n", &newtime, &dummy1, &dummy2) > 0 )
      {
	/* compute elapsed time from last arrival */
	printf("%f\n", (newtime - oldtime));
	
	/* record the new time */
        packet++;
	oldtime = newtime;
      }
    return(0); 
  }

