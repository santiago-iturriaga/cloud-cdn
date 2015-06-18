/* lrustack.h
 * this is LRU Stack,stacking doc with stack prob,
 * which is currently not in availible docs object.
 * it exchanges info with avail docs object via common docName.
 */

#ifndef LRUSTACK_H
#define LRUSTACK_H

class lrustack{
public:
     
    lrustack(int depth,int mode); //constructor
	~lrustack();			//destructor

	// initialization of stack
	void init(int M, int *docName,int * freqName); //independent model
	void init(float *stackprob); //static model
	void init(void); //dynamic model
	void init(int *docName, float *stackprob); //Carey's new model

	// check if requested reference goes to the stack
	bool inStack(float randnum);

	// add doc into stack
	int add(int doc);
    int add(int doc, float stackprob); // used for dynamic model only

	// update stack if some level has been referenced
	void update(int reflevel);

	// get the _reflevel
	int reflevel(void);

	// get the document based on _reflevel
	int docAt(int reflevel);

    // delete a document record
	void del(int doc);

    // choose a doc in stack in case that
	// ava doc is empty
	int docChosen(float randnum);


	// output the stack for testing
	void output();
	

private:
	//lru stack structure
	int *_docptr; // pointer to array of doc
	float *_probptr; //ponter to array of stack prob
	float *_cumprobptr;//pointer to array of cum prob

	// attributes
	int _depth;   //capacity of stack
	int _counter; //real depth of stack
	int _mode; //0 is independent, 1 is static, 2 is dynamic
	int _reflevel; //stack level which is referenced

	// private function
	int posOf(int doc);
   
};

#endif //LRUSTACK_H
