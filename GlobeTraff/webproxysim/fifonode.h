//fifonode.h

#ifndef FIFONODE_H
#define FIFONODE_H

#include <stdlib.h>


class FifoNode {
 
 public:

  FifoNode(unsigned int initId, unsigned int initSize) //constructor
   { id = initId;  size = initSize; right = up = down = NULL; }

 ~FifoNode() {} //the destructor

 unsigned GetFileId() { return id; }
 unsigned GetFileSize() { return size; }
 FifoNode* GetRight() { return right; }
 FifoNode* GetUp() { return up; }
 FifoNode* GetDown () { return down; }
 void SetRight(FifoNode* initRight) { right = initRight; }
 void SetUp(FifoNode* initUp) { up = initUp; }
 void SetDown(FifoNode* initDown) { down = initDown; }

 
 private:
  unsigned id; 
  unsigned size;
  FifoNode *right;
  FifoNode *up;
  FifoNode *down;
};

#endif
