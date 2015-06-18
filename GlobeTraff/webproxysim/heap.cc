//heap.cc

#include "heap.h"
#include <stdio.h>
#include <stdlib.h>


Heap::Heap(unsigned int initSize)
{
    size = initSize;
    noofElements = 0;
    if ((data = new GDNode *[size]) == NULL)
    {
	printf("Not enough memory to allocate heap\n");
	exit(1);
    }
}
					     

 
Heap::~Heap()
{
    delete [] data;
}


void 
Heap::Swap(GDNode **one, GDNode **two)
{
    GDNode *temp = *one;
    *one = *two;
    *two = temp;  
}



void 
Heap::AlterHeap(unsigned i, float v)
{
    float x = data[i]->GetHValue();
    data[i]->SetHValue(v);
    if ( v >= x )
	SiftDown(i);
    else
	Percolate(i);
}


void 
Heap::SiftDown(unsigned i)
{
    unsigned j, k = i;
    do
    {
	j = k;
	if ((2*j <= noofElements) && (data[2*j]->GetHValue() <= data[k]->GetHValue()))
	    k = 2*j;
	if ((2*j < noofElements) && (data[2*j +1]->GetHValue() <= data[k]->GetHValue()))
	    k = 2*j + 1;
	Swap(&data[j], &data[k]);
	data[j]->SetHeapLoc(j); data[k]->SetHeapLoc(k); 
       }while (j != k);
}
 


void 
Heap::Percolate(unsigned i)
{
    unsigned j, k = i;
    do
    {
	j = k;
	if ((j > 1) && (data[j/2]->GetHValue() >= data[k]->GetHValue()))
	    k = j/2;
	Swap(&data[j], &data[k]);
	data[j]->SetHeapLoc(j); data[k]->SetHeapLoc(k); 
    }while (j != k);
}



GDNode* 
Heap::FindMin()
{
    return data[1];
}



void 
Heap::DeleteMin()
{
    data[1] = data[noofElements];
    --noofElements;
    SiftDown(1);
}


void 
Heap::InsertNode(GDNode *v)
{
    if ((noofElements +1) == size)
    {
	printf("Not enough space to add new element on the heap.. Make it larger\n");
	exit(1);
    }
    noofElements++;
    data[noofElements] = v;
    v->SetHeapLoc(noofElements);
    Percolate(noofElements);
}
    
