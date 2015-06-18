#include <stdio.h>

main()
  {
    float num1; 
    unsigned int num2;
    while( scanf("%f %u\n", &num1, &num2) > 0 )
      {
	printf("%f\n", num1);
      }
  return(0);
  }
