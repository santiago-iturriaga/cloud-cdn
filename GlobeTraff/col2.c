#include <stdio.h>

main()
  {
    float num1; 
    unsigned int num2, num3;
    while( scanf("%f %u %u\n", &num1, &num2, &num3) > 0 )
      {
	printf("%u\n", num2);
      }
  return(0);
  }
