//randnode.h

#ifndef RANDNODE_H
#define RANDNODE_H

#include <stdlib.h>


class RandNode {
 
 public:

  RandNode(unsigned int initId, unsigned int initSize) //constructor
   { id = initId;  size = initSize; right = up = down = NULL; }

 ~RandNode() {} //the destructor

 unsigned GetFileId() { return id; }
 unsigned GetFileSize() { return size; }
 RandNode* GetRight() { return right; }
 RandNode* GetUp() { return up; }
 RandNode* GetDown () { return down; }
 void SetRight(RandNode* initRight) { right = initRight; }
 void SetUp(RandNode* initUp) { up = initUp; }
 void SetDown(RandNode* initDown) { down = initDown; }

 
 private:
  unsigned id; 
  unsigned size;
  RandNode *right;
  RandNode *up;
  RandNode *down;
};

#endif
