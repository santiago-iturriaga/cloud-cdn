#include <stdio.h>

main()
  {
    float num1; 
    unsigned int num2, num3;
    while( scanf("%f %u %u\n", &num1, &num2, &num3) > 0 )
      {
	printf("%f %u\n", num1, num3);
      }
  return(0);
  }
