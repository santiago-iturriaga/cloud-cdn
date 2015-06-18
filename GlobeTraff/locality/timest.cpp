
/* Generate a random floating point number uniformly distributed in [0,1] */
double Uniform01()
  {
    double randnum;
    /* get a random positive integer from random() */
    //randnum = (double) 1.0 * random();
    randnum = (double) 1.0 * rand();
    /* divide by max int to get something in the range 0.0 to 1.0  */
    randnum = randnum / (1.0 * RAND_MAX); // MAX_INT);
    return( randnum );
  }


/* Generate a random floating point number from an exponential    */
/* distribution with mean mu.                                     */

double Exponential(double mu)
 {
    double randnum, ans;
	srand((unsigned)time(0));

    randnum = Uniform01();
    ans = -(mu) * log(randnum);
    return( ans );
  }

double
current_time(double time)
  {
    double now;

//    srand(12357);
    now = time + Exponential(1.0/ARRIVAL_RATE);
    return(now);
  }


