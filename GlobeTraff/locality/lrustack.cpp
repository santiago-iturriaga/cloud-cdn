// lrustack.h
#include "lrustack.h"
#include <iostream>
#include <math.h>

using namespace std;

//constructor
lrustack::lrustack(int depth,int mode) { 
	_depth = depth;
	_mode = mode;

	_docptr = new int[depth];
	_probptr = new float[depth];
	_cumprobptr = new float[depth];
}

//destructor
lrustack::~lrustack() { 
	delete [] _docptr;
	delete [] _probptr;
	delete [] _cumprobptr;
}


// stack initialization for independent model
// the stack depth is same as num of docs,and full of docs
// each level prob is binded with doc and num of reference 
void
lrustack::init(int M, int *doc1,int * freq1) {
		_counter = _depth;
		for (int i=0;i<_depth;i++) {
			_docptr[i] = doc1[i];
			_probptr[i] = (float)freq1[i] / (float)M;
		}
		// calculate the cum prob
		_cumprobptr[0] = _probptr[0];
		for (int i=1;i<_depth;i++) {
			_cumprobptr[i] = _cumprobptr[i-1]+_probptr[i];
		}
//		cout<<"Cumulative prob for "<<_depth<<" levels is "<<_cumprobptr[_depth-1]<<endl;
}

// stack initialization for static model
// the stack is empty initially
void
lrustack::init(float *stackprob) {
	_counter = 0;
	for (int i=0;i<_depth;i++) 
		_probptr[i] = stackprob[i];
	_cumprobptr[0] = _probptr[0];
	for (int i=1;i<_depth;i++)
		_cumprobptr[i] = _probptr[i]+_cumprobptr[i-1];
//		cout<<"Cumulative prob for "<<_depth<<" levels is "<<_cumprobptr[_depth-1]<<endl;
}

// stack initialization for dynamic model
// the stack is empty initially
void
lrustack::init(void) {
	_counter = 0;
}

// stack initialization for Carey's new model
// the stack is empty initially
void
lrustack::init(int *doc1, float *stackprob) {
	_counter = _depth;
	for (int i=0;i<_depth;i++) 
	  _docptr[i] = doc1[i];
	for (int i=0;i<_depth;i++) 
		_probptr[i] = stackprob[i];
	_cumprobptr[0] = _probptr[0];
	for (int i=1;i<_depth;i++)
		_cumprobptr[i] = _probptr[i]+_cumprobptr[i-1];
//		cout<<"Cumulative prob for "<<_depth<<" levels is "<<_cumprobptr[_depth-1]<<endl;
}

// Function to check if requested reference goes to the stack
// based on actual contents of stack,rather than the stack depth
// if yes, locate the reference level
bool 
lrustack::inStack(float randnum) {
	for (int i=0;i<_counter;i++) {
		if ( randnum <= _cumprobptr[i] ) {
			_reflevel = i; return true; }
	}
	return false;
}

// Function to add new doc into stack for static model
// if stack overflow, return docName of victim
// else return -1
int
lrustack::add(int doc) {
	//if stack not overflow
	if (_counter < _depth) {
		for (int i=_counter-1;i>=0;i--) 
			_docptr[i+1] = _docptr[i];
		_docptr[0] = doc;
		_counter++;
		return -1;
	}
	else {
		int victim = _docptr[_depth-1];
		for (int i=_depth-1;i>0;i--)
			_docptr[i] = _docptr[i-1];
		_docptr[0] = doc;
		_counter = _depth;
		return victim;
	}

}

// Function to add new doc with assoicted stack prob, for dynamic model
// if stack overflow, return docName of victim
// else return -1
int
lrustack::add(int doc, float stackprob) {
	//if stack not overflow
	if (_counter < _depth) {
		for (int i=_counter-1;i>=0;i--) {
			_docptr[i+1] = _docptr[i];
			_probptr[i+1] = _probptr[i];
		}
		_docptr[0] = doc;
		_probptr[0] = stackprob;
		_counter++;

		//update the cummprob
		_cumprobptr[0] = _probptr[0];
		for (int i=1;i<_counter;i++)
			_cumprobptr[i]= _probptr[i]+_cumprobptr[i-1];

		return -1;
	}
	else {
		int victim = _docptr[_depth-1];
		for (int i=_depth-1;i>0;i--) {
			_docptr[i] = _docptr[i-1];
			_probptr[i] = _probptr[i-1];
		}
		_docptr[0] = doc;
		_probptr[0] = stackprob;
		_counter = _depth;

		//update the cummprob
		_cumprobptr[0] = _probptr[0];
		for (int i=1;i<_counter;i++)
			_cumprobptr[i]= _probptr[i]+_cumprobptr[i-1];

		return victim;
	}

}

// Function to update stack,
// for static,move the doc at _reflevel to first level only
// for dynamic model, move the doc and its prob to first level
void
lrustack::update(int reflevel) {
	// for static LRU stack models
	if (_mode==1 || _mode==3) {
		int temp = docAt(reflevel);

		for (int i=reflevel;i>0;i--)
		    _docptr[i] = _docptr[i-1];

		_docptr[0] = temp;
	}

	// for dynamic LRU stack models
	if (_mode==2 || _mode==4) {
		int temp = docAt(reflevel);
		float temp2 = _probptr[reflevel];

		for (int i=reflevel;i>0;i--)
		  {
		    _docptr[i] = _docptr[i-1];
		    _probptr[i] = _probptr[i-1];
		  }

		_docptr[0] = temp;
		_probptr[0] = temp2;

		_cumprobptr[0] = _probptr[0];
		for (int i=1;i<_counter;i++)
			_cumprobptr[i] = _probptr[i]+_cumprobptr[i-1];
	}
}



// Function to delete a document record in stack
// for static model,del doc only
// for independent  and dynamic models,del doc and its prob
void
lrustack::del(int doc) {
	int pos = posOf(doc);

	for (int i=pos;i<_counter-1;i++)
		_docptr[i] = _docptr[i+1];

	// for independent/dynamic model
	if (_mode==0 || _mode==2) {
		for (int i=pos;i<_counter-1;i++)
			_probptr[i] = _probptr[i+1];

		if (pos==0)
			_cumprobptr[0] = _probptr[0];
		for (int i=pos+1;i<_counter-1;i++) 
			_cumprobptr[i] = _probptr[i]+_cumprobptr[i-1];
	}
	
	_counter--;

}


// Function to get the doc name based on _reflevel
int
lrustack::docAt(int reflevel) {
	return _docptr[reflevel];
}

// Function to get the _reflevel of doc accessed
int
lrustack::reflevel(void) {
	return _reflevel;
}

// random choose a doc in stack in case that
// no availbile docs currently not in stack.
int
lrustack::docChosen(float randnum) {
	int level = (int) floor(randnum*_counter);
	_reflevel = level;
	return docAt(level);
}


// private function
int
lrustack::posOf(int doc) {
	for(int i=0;i<_counter;i++) 
		if (_docptr[i] == doc) return i;
	return -1;
}

// output function for testing
void
lrustack::output() {
  cout<<"Current LRU stack contents: "<<endl ;
	for (int i=0;i<_counter;i++) {
		cout<<_docptr[i]<<"\t"<<_probptr[i]<<"\t"<<_cumprobptr[i]<<endl;
		//cout<<_docptr[i]<<" ";
	}
}
