//gdnode.h

#ifndef GDNODE_H
#define GDNODE_H

#include <stdlib.h>

class GDNode {
 
 public:

  GDNode(unsigned int initId, unsigned int initSize) //constructor
   { id = initId;  size = initSize; right = NULL; }

 ~GDNode() {} //the destructor

 unsigned GetFileId() { return id; }
 unsigned GetFileSize() { return size; }
 GDNode* GetRight() { return right; }
 void SetRight(GDNode* initRight) { right = initRight; }
 void SetHValue(float initHValue) { HValue = initHValue; }
 float GetHValue() { return HValue; }
 unsigned GetHeapLoc() { return loc; }
 void SetHeapLoc(unsigned initLoc) { loc = initLoc; }

 private:
  unsigned id; 
  unsigned size;
  unsigned loc;
  float HValue;
  GDNode *right;
};

#endif
