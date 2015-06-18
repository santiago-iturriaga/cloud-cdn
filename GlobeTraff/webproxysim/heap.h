//heap.h


#ifndef HEAP_H
#define HEAP_H

#include "gdnode.h"

class Heap {

 public:

 Heap(unsigned int initSize);
 
 ~Heap();

 void Init() { noofElements =0; }
 
 void Swap(GDNode **one , GDNode **two);

 void AlterHeap(unsigned i,  float v);

 void SiftDown(unsigned i);
 
 void Percolate(unsigned i);

 GDNode* FindMin();

 void DeleteMin();

 void InsertNode(GDNode *v);


 private:
 
 unsigned size;
 unsigned noofElements;
 GDNode ** data;

};

#endif


