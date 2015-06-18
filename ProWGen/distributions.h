
#include <math.h>
#include <algorithm>
#include <stdio.h>
#include <iostream>
#include <time.h>

#ifndef DISTR_H
#define DISTR_H


#define ARRIVAL_RATE 1.0         /* Connections per second */
#define MAX_INT 2147483647       /* Maximum positive integer 2^31 - 1 */


#define ARRIVAL_RATE 1.0         /* Connections per second */
#define MAX_INT 2147483647       /* Maximum positive integer 2^31 - 1 */


class Distributions {
 public:
   
    Distributions(); 
    ~Distributions();

	double Uniform01();
	double Exponential(double mu);
	int UniformInt(int range);
	double exponentialDecayArrivalRate(float lamda, float tau, float tracesSeeding);
	float Weibull(int rank, float weibullK, float weibullL);
	float ParetoCDF(float alpha);
	double Lognormal(float mean, float std);
	double StandardNormal();
	double Normal(float mean, float std);
	double non_standard_value(double value, float mean, float std);
	double standard_normal_quantile(double p);
	double inverse_erf(double x);
	double sgn(double x);
};

#endif //DISTR_H
