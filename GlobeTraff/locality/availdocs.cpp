#include "availdocs.h"
#include <math.h>
#include <iostream>

using namespace std;

//constructor
availdocs::availdocs(int N, int M) { 
	_n = N;
	_m = M;
	_counter = N;
	_docptr = new int[N];
	_freqptr = new int[N];
	_currentInStack = new bool[N];
	_stackprob = new float[N];
}

//destructor
availdocs::~availdocs() { 
	delete [] _docptr;
	delete [] _freqptr;
	delete [] _currentInStack;
	delete [] _stackprob;
}

//initialization
void
availdocs::init(int * docNames,int *docFreqs) {
	for (int i=0;i<_n;i++) {
		_docptr[i] = docNames[i];
		_freqptr[i]= docFreqs[i];
		_currentInStack[i] = false;
	}
}

//Function to decrease number of references of doc accessed
void
availdocs::decreaseCountOf(int docName) {
	int pos = posOf(docName);
	_freqptr[pos]--;
}

//Function to get rest num of referenced required for specific doc
int
availdocs::quotaOf(int docName) {
	int pos = posOf(docName);
	return _freqptr[pos];
}

//Function to delete doc from consideration
void
availdocs::del(int docName) {
	int pos = posOf(docName);
	//if the doc is not in stack
	if (_currentInStack[pos] == false) _counter--;
	for(int i=pos;i<_n-1;i++) {
		_docptr[i] = _docptr[i+1];
		_freqptr[i] = _freqptr[i+1];
		_currentInStack[i] = _currentInStack[i+1];
	}
	//total avail docs decrease
	_n--;
}

// Function to mark the doc currently in stack or not if stack overflow
void
availdocs::mark(int docName) {
	int pos = posOf(docName);
	
	//mark doc,which is removed from avail docs into stack
	if ( _currentInStack[pos] == false ) {
		_currentInStack[pos] =true;
		_counter--; }
	//mark the doc which is victim of stack overflow 
	else {
		_currentInStack[pos] =false;
		_counter++; }
}

//Function to pick doc randomly from availible docs currently not in stack
//if no avail doc to pick,return -1
int
availdocs::docChosen(float randnum) {
	int idx = (int) floor(randnum*_counter);
	int id=-1;
	for (int i=0;i<_n;i++) {
		if (_currentInStack[i] == false) id++;
		if (id==idx) return _docptr[i];
	}
	return -1;
}

//Function to pick doc randomly from availible docs currently not in stack
//if no avail doc to pick,return -1
int
availdocs::docChosen2(float randnum, int refstogo) {
	int idx = (int) floor(randnum*refstogo);
	int id=-1;
	for (int i=0;i<_n;i++) {
		if (_currentInStack[i] == false)
		  id += _freqptr[i];
		if (id>=idx) return _docptr[i];
	}
	return -1;
}

// private function
int
availdocs::posOf(int docName) {
	for (int i=0;i<_n;i++) {
		if (_docptr[i] == docName)
			return i;
	}
	return -1;
}

//associate stack prob with doc for dynamic model
void
availdocs::bind(float * stackDepthProb) {
	for (int i=0;i<_n;i++) {
		_stackprob[i] = stackDepthProb[i];
	}
}

// stack prob associated with doc, used for dynamic model
float
availdocs::prob(int doc) {
	int pos = posOf(doc);
	return _stackprob[pos];
}


//for testing
void
availdocs::output(void) {
	for (int i=0;i<_n;i++) {
		if (_currentInStack[i]==false) 
			cout<<_docptr[i];
	}
	cout<<" ";
}
