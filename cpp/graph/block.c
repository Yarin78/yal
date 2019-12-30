#include <stdio.h>
#include <stdlib.h>

struct TEdge
{
  int v;               /* Vertex */
 	int explored;        /* 1 if edge explored */
  struct TEdge *opp,*next;
};

struct TBlock
{
	int n;               /* Number of vertices in this block */
	int *vlist;          /* Set of vertices in this block */	
};

struct TGraph
{
  int n;                 /* Number of vertices in G */
  struct TEdge **e;      /* List of edges for each vertex */
 	int *type;             /* 0=block, 1=cutvertex */
 	struct TBlock **block; /* Pointer to block structure, if type=0, or cut-vertex map */
};


/* Variables used by findblock */

int label,root,*back,*oback,*vmap;
int sp,*stack;
struct TGraph *gg,bg;

void addedge(struct TGraph *g, int a, int b)
{
	struct TEdge *ea,*eb;
	ea=malloc(sizeof(struct TEdge));
  eb=malloc(sizeof(struct TEdge));
  ea->v=b;
  eb->v=a;
  ea->explored=0;
  eb->explored=0;
 	ea->opp=eb;
 	eb->opp=ea;
  ea->next=g->e[a];
  eb->next=g->e[b];
  g->e[a]=ea;
  g->e[b]=eb;
}

/* Block found */

void getblock(int v, int w, int last)
{
  int i,j=0,vblock,vcut=0;
  struct TBlock *bptr;
  
	bptr=malloc(sizeof(struct TBlock));  
	bg.type[bg.n]=0;
	bg.block[bg.n]=bptr;
	vblock=bg.n++; /*  Vertex in block-cutpoint graph representing this block */	
  if ((vmap[v]>=0) && (bg.type[vmap[v]]==1))
  	vcut=vmap[v]; /* Old cut-vertex */
  else if (!last) {
  	bg.type[bg.n]=1;
  	bg.block[bg.n]=(struct TBlock*)v;
  	vmap[v]=vcut=bg.n++;  /* New cut-vertex; */
  } else if (vmap[v]<0) {
  	bg.type[bg.n]=1;
  	vmap[v]=vblock;
  }
	if (vcut)
		addedge(&bg,vblock,vcut);	

	i=sp;		
	while (stack[--i]!=w);
	bptr->n=sp-i+1;
	bptr->vlist=calloc(sp-i+1,sizeof(int));
	bptr->vlist[j++]=v;
	
	do {
		i=stack[--sp];
  	if ((vmap[i]>=0) && (bg.type[vmap[i]]==1))
  		addedge(&bg,vmap[i],vblock);
  	else
  		vmap[i]=vblock;
 		bptr->vlist[j++]=i;
  } while (i!=w);
}

void dfs(int v)
{
  int w;
  struct TEdge *e;

  e=gg->e[v];
  oback[v]=back[v]=++label;
  stack[sp++]=v;
  while (e) {
  	if (!e->explored) {
  		w=e->v;  		
  		e->explored=e->opp->explored=1;
      if (back[w]) {
      	if (oback[w]<back[v])
         	back[v]=oback[w];
     	} else {
     	  dfs(w);
        if (back[w]<back[v])
         	back[v]=back[w];
       	while (e && e->explored)
       		e=e->next;
       	if (back[w]>=oback[v])
          getblock(v,w,(e==NULL) && (v==root));
     	}
  	} else
  		e=e->next;  	
  }
}

/* Algorithm to compute the block-cutpoint graph

   Input:  A graph G
           Array to store vertex mapping (optional)
   Output: An block-cutpoint graph H.
           A mapping of vertices in G to blocks/cutvertices in H (optional)
*/

struct TGraph findblocks(struct TGraph *g, int **map)
{
  int i;
  gg=g; 
  back=calloc(gg->n,sizeof(int));
  oback=calloc(gg->n,sizeof(int));
  stack=calloc(gg->n,sizeof(int));
  bg.e=calloc(gg->n*2,sizeof(struct TEdge*));
  bg.type=calloc(gg->n*2,sizeof(int));
  bg.block=calloc(gg->n*2,sizeof(struct TBlock*));
  vmap=malloc(gg->n*sizeof(int));
  for(i=0;i<gg->n;i++)
  	vmap[i]=-1;
  sp=label=0;

  for(root=0;root<g->n;root++)
    if (vmap[root]<0)
      dfs(root);
  
  free(back);
  free(oback);
  free(stack);
  if (!map)
  	free(vmap);
  else
  	*map=vmap;
  return bg;
}

int main(void)
{
  int i,j,m,a,b;
  int *map;
  struct TGraph g,h;
  struct TEdge *e;
  struct TBlock *bptr;

	/* Read graph G */
  scanf("%d %d",&g.n,&m); /* No of vertices, no of edges */
  g.e=calloc(g.n,sizeof(struct TEdge*));
  for(i=0;i<m;i++) {
    scanf("%d %d",&a,&b);
    addedge(&g,a,b);    
  }

	/* Find block-cutpoint graph */
  h=findblocks(&g,&map);
  
  /* Print info about the block-cutpoint graph */
  printf("Mappings from G to block-cutpoint graph:\n");
  for(i=0;i<g.n;i++)
  	if (h.type[map[i]]==1)
  		printf("%2d [cut-vertex] -> %2d\n",i,map[i]);
  	else
  		printf("%2d [block]      -> %2d\n",i,map[i]);
	printf("\n");
	
	printf("Connections in block-cutpoint graph:\n");
  for(i=0;i<h.n;i++) {  	
    printf("%2d %s  :",i,h.type[i]?"[cut-vertex]":"[block]     ");
    e=h.e[i];
    while (e) {
      printf(" %d",e->v);
      e=e->next;
    }
    printf("\n");
  }
  printf("\n");

	printf("Mappings from block-cutpoint graph to G:\n");
	for(i=0;i<h.n;i++) {
		printf("%2d%c:",i,h.type[i]?'C':'B');
		if (!h.type[i]) {
			bptr=h.block[i];
			for(j=0;j<bptr->n;j++)
				printf(" %d",bptr->vlist[j]);
			printf("\n");
		} else
			printf(" %d\n",(int)h.block[i]);
	}
  return 0;
}

/* The following input:

11 13
0 1
1 8
0 8
8 6
9 3
3 10
10 7
3 7
9 7
3 4
4 5
5 2
2 4

should yield

Mappings from G to block-cutpoint graph:
 0 [block]      ->  2
 1 [block]      ->  2
 2 [block]      ->  7
 3 [cut-vertex] ->  4
 4 [cut-vertex] ->  6
 5 [block]      ->  7
 6 [block]      ->  0
 7 [block]      ->  3
 8 [cut-vertex] ->  1
 9 [block]      ->  3
10 [block]      ->  3

Connections in block-cutpoint graph:
 0 [block]       : 1
 1 [cut-vertex]  : 2 0
 2 [block]       : 1
 3 [block]       : 4
 4 [cut-vertex]  : 5 3
 5 [block]       : 4 6
 6 [cut-vertex]  : 7 5
 7 [block]       : 6

Mappings from block-cutpoint graph to G:
 0B: 8 6
 1C: 8
 2B: 0 1 8
 3B: 3 10 9 7
 4C: 3
 5B: 4 3
 6C: 4
 7B: 2 5 4
*/
