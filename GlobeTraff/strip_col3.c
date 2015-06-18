#include <stdio.h>

main()
  {
    unsigned int num2, num3;
    float num1; 
    while( scanf("%f %u %u\n", &num1, &num2, &num3) > 0 )
      {
	printf("%f %u\n", num1, num2);
      }
  return(0);
  }
