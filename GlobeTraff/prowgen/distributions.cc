
#include "distributions.h"

using namespace std;

const double PI = 3.141592;

Distributions::Distributions()
{
}

Distributions::~Distributions()
{
}

/* 
 * Generate a random floating point number uniformly distributed in [0,1] 
 * 
 */
double Distributions::Uniform01()
{
	double randnum;

	/* get a random positive integer from random() */
	randnum = (double) 1.0 * rand(); //random

	/* divide by max int to get something in the range 0.0 to 1.0  */
	randnum = randnum / (1.0 * RAND_MAX); //MAX_INT

	return( randnum );
}


/* 
 * Generate a random floating point number from an exponential
 * distribution with mean mu.                                     
 */
double Distributions::Exponential(double mu)
{
    double randnum, ans;

    randnum = Uniform01();
    ans = -(mu) * log(randnum);
    return( ans );
  }

/* 
 * Generate a random integer number uniformly distributed in [0,range) 
 */
int Distributions::UniformInt(int range)
{	
    int randnum;
	
    randnum = rand() % range;
    return( randnum );
}


/*
 * Taking uniformly distributed in [0, Tlife) random "time" values
 * Based on this value we generate the expected arrival rate
 * according to the exponential decay rule.
 *  See JSAC'07 paper by Guo et al. for more information.  
 */
double Distributions::exponentialDecayArrivalRate(float lamda, float tau, float tracesSeeding)
{

	return lamda*exp( -UniformInt(tau*log(lamda*tracesSeeding)) / tau );;
}

/*
 * Weibull distribution
 */
float Distributions::Weibull(int rank, float weibullK, float weibullL)
{
	return ((weibullK / weibullL) * pow((rank/weibullL),(weibullK-1))*exp(-pow((rank/weibullL), (weibullK))) );
}


/*
 * Returns a random value for the "life span" of a request 
 * following the Pareto distribution
 * as described by W. Tang et al. (Computer Networks 51 (2007) 336-356)
 */
float Distributions::ParetoCDF(float alpha)
{
	return 1 / pow(1- Uniform01(), 1/alpha);
}

/*
 * Returns a random value for the "life span" of a request 
 * following the Lognormal distribution
 * as described by W. Tang et al. (Computer Networks 51 (2007) 336-356)
 */
double Distributions::Lognormal(float mean, float std)
{
	float lognormMeanSqr = pow(mean, 2);
    float lognormVariance= pow(std, 2);

    float paramMean = log( lognormMeanSqr / sqrt(lognormVariance + lognormMeanSqr));
    float paramStd  = sqrt(log((lognormVariance + lognormMeanSqr) / lognormMeanSqr));
    
    return exp(paramMean + paramStd * StandardNormal());
}

/*
 * Marsaglia polar method for generating a random number following
 * the standard normal distribution
 */
double Distributions::StandardNormal()
{
    double u1, u2, v1, v2, w, y, x1, x2; //params of the normal variate
    double normalVariate; //the lognormal variate
	unsigned short seed[3] = {5,7,9}; //passed to the random number function

	w = 2.0;
	while (w > 1)
	{
	 u1 = Uniform01();
	 u2 = Uniform01();
	 v1 = 2*u1 - 1;
	 v2 = 2*u2 - 1;
	 w = v1*v1 + v2*v2;
	}

	y  = pow((-2*log(w)/w), 0.5);
	x1 = v1*y;  //x1 & x2 are IID N(0,1) random variables
	x2 = v2*y;

	normalVariate = x1;
	  
	return normalVariate;
}

/* 
 * Returns a random number following the normal distribution
 * with the given parameters.
 */
double Distributions::Normal(float mean, float std)
{
	return mean+std*StandardNormal();
}

double Distributions::non_standard_value(double value, float mean, float std)
{
	return value*std + mean;
}

double Distributions::standard_normal_quantile(double p)
{
	return sqrt(2)*inverse_erf(2*p-1);
}

/*
 * http://en.wikipedia.org/wiki/Error_function#Inverse_function
 */ 
double Distributions::inverse_erf(double x)
{
	float a = 0.140012;
	return sgn(x)*sqrt( sqrt(  pow(((2/PI*a) + log(1-pow(x,2))/2),2) - ( log(1-pow(x,2)) /a) ) - ( ((2/PI*a) + log(1-pow(x,2))/2) ) );
}

double Distributions::sgn(double x)
{
	if (x > 0) return 1;
	if (x < 0) return -1;
	return 0;
}
	
