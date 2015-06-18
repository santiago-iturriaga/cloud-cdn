//lrunode.h

#ifndef LRUNODE_H
#define LRUNODE_H

#include <stdlib.h>


class LruNode {
 
 public:

  LruNode(unsigned int initId, unsigned int initSize) //constructor
   { id = initId;  size = initSize; right = up = down = NULL; }

 ~LruNode() {} //the destructor

 unsigned GetFileId() { return id; }
 unsigned GetFileSize() { return size; }
 LruNode* GetRight() { return right; }
 LruNode* GetUp() { return up; }
 LruNode* GetDown () { return down; }
 void SetRight(LruNode* initRight) { right = initRight; }
 void SetUp(LruNode* initUp) { up = initUp; }
 void SetDown(LruNode* initDown) { down = initDown; }

 
 private:
  unsigned id; 
  unsigned size;
  LruNode *right;
  LruNode *up;
  LruNode *down;
};

#endif
