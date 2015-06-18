/* availdocs.h
 * this class is used to store availble doc with num of references,
 * which is currently not in LRU stack.
 * it exchanges info with LRU Stack via common docName.
 */

#ifndef AVAILDOCS_H
#define AVAILDOCS_H

class availdocs{
public:
	availdocs(int N,int M); //constructor
	~availdocs(); //destructor

	//initialization
	void init(int * docNames,int *docFreqs); 

	//decrease number of references of doc accessed
	void decreaseCountOf(int doc);

	//get rest num of references desired
	int quotaOf(int docName); 

	//delete doc from consideration
	void del(int docName);  

	// mark doc as currently in stack, or not due to stack overflow victim
	void mark(int docName); 
	
    //choose a doc within availible docs(not in stack) randomly
	int docChosen(float randnum);

    //choose a doc within availible docs(not in stack) not so randomly
	int docChosen2(float randnum, int refstogo);

	//associate stack prob with doc, for dynamic model only
	void bind(float * stackDepthProb);
	
	// stack prob associated with doc, for dynamic model only
	float prob(int doc);

	void output(void); //for testing
		   

private:
	//avilible docs structure
	int *_docptr; // pointer to array of doc
	int *_freqptr; //pointer to aray of number of references desired for doc
	bool *_currentInStack; //mark if the doc is currently in stack or not
	float *_stackprob; //associated stack prob for dynamic model

	// attributes
	int _n;	//total avail docs, including those are stacked
	int _m; //total num of references
	int _counter; //counting avail docs,excluding those recorded in stack

	// private function
	int posOf(int doc);
   
};

#endif //AVAILDOCS_H
