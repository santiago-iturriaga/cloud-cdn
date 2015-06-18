#define MAXCOMMAND 100
#define HASHELEMENTS 10007000
#define AAA 10007000

struct htab {
   struct htab *child;
   struct htab *parent;
   char key[MAXCOMMAND];
   long data;
};

unsigned int hash(char *);
struct htab *addhash(char *, long );
struct htab *findhash(char *);
int delhash(char *);
void hashprofile(int);

int gettoken(char *);

void ungetch(int);
int getch(void);
