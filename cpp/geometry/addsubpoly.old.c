#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define MAXPOINTS 100
#define MAXVERTICES (MAXPOINTS*MAXPOINTS)
#define MAXEDGES 4

#define eps 0.000001

struct TVertex
{
	double x,y;
	int n;
	int e[MAXEDGES];
} v[MAXVERTICES];

int noVert;

struct TPoint
{
	double x,y;
};

struct TPoly
{
	int n;
	struct TPoint p[MAXPOINTS];
};

struct TLine {
  struct TPoint f, t;
};

struct TList
{
	int v;
	double dist;
};

int dEq(double a, double b)
{
	return fabs(a-b)<eps;
}

double det(struct TPoint *a, struct TPoint *b)
{
  return (a->x*b->y - a->y*b->x);
}

void sub(struct TPoint *a, struct TPoint *b, struct TPoint *c)
{
  c->x = a->x - b->x;
  c->y = a->y - b->y;
}

double cross(struct TPoint *v1, struct TPoint *v2, struct TPoint *v3)
{
	return (v2->x-v1->x)*(v3->y-v1->y)-(v3->x-v1->x)*(v2->y-v1->y);
}

double vectdist(struct TPoint *org, struct TPoint *dest, struct TPoint *v)
{
	if (!dEq(v->x,0))
  	return (dest->x-org->x)/v->x;
	return (dest->y-org->y)/v->y;
}

int intersect(struct TLine *a, struct TLine *b, struct TPoint *res, int *para) {
  struct TPoint vw0, v1, w1,c;
  double  fa, fb, d0,af,at;

  if (para)
  	*para = 0;

  sub(&(a->t), &(a->f), &v1);
  sub(&(b->f), &(b->t), &w1);
  sub(&(b->f), &(a->f), &vw0);

  d0 = det(&v1, &w1);
 	if (!dEq(d0,0)) {
    fa = det(&vw0, &w1)/d0;
    fb = det(&v1, &vw0)/d0;
    //printf("%lf %lf\n",fa,fb);
    if ((fabs(fa-0.5)<=0.5) && (fabs(fb-0.5)<0.5)) {
    	if (res) {
      	res->x = fa * v1.x + a->f.x;
      	res->y = fa * v1.y + a->f.y;
      }
      return 1;
    }
  }
  else if (para) {
  	sub(&(b->t), &(b->f), &w1);
  	af=vectdist(&(b->f),&(a->f),&w1);
  	c.x=b->f.x+af*w1.x;
  	c.y=b->f.y+af*w1.y;
  	if (dEq(c.x,a->f.x) && dEq(c.y,a->f.y)) {
  		at=vectdist(&(b->f),&(a->t),&w1);
  		/* Check if lines actually overlap */
  		if (((at<=eps) && (af<=eps)) || ((at>=1-eps) && (af>=1-eps))) {
  			*para=0;
  			return 0;
  		}
  		/*
  		printf("af: %0.0lf,%0.0lf\n",a->f.x,a->f.y);
  		printf("at: %0.0lf,%0.0lf\n",a->t.x,a->t.y);
  		printf("bf: %0.0lf,%0.0lf\n",b->f.x,b->f.y);
  		printf("bt: %0.0lf,%0.0lf\n",b->t.x,b->t.y);
  		printf("afd=%0.2lf, atd=%0.2lf\n",af,at);  		
  		*/
  		*para=7;
  		if ((af>0) && (af<1)) /* a.f is between b.f and b.t */
  			*para=(at<0)?2:((at>1)?1:5);
  		if ((at>0) && (at<1)) /* a.t is between b.f and b.t */
  			*para=(af<0)?4:((af>1)?3:5);
  		if ((*para==5) && (af>at)) *para=6;
    }
  }

  return 0;
}

int vlookup(double x, double y)
{
	int i;
	for(i=0;i<noVert;i++)
		if (dEq(v[i].x,x) && dEq(v[i].y,y))
			return i;
	v[noVert].x=x;
	v[noVert].y=y;
	v[noVert].n=0;
	return noVert++;
}

int listsort(const void *v1, const void *v2)
{
	struct TList *e1=(struct TList*)v1,*e2=(struct TList*)v2;
	if (e1->dist<e2->dist) return -1;
	if (e1->dist>e2->dist) return 1;
	return 0;
}

void addedge(int v1, int v2)
{
	if (v1!=v2)
		v[v1].e[v[v1].n++]=v2;
}

void readpoly(struct TPoly *p)
{
	int i;
	scanf("%d",&p->n);
	for(i=0;i<p->n;i++)
		scanf("%lf %lf",&p->p[i].x,&p->p[i].y);
}

void addlist(struct TList *cur, int v1, int v2)
{
	double dx,dy;
	dx=v[v1].x-v[v2].x;
	dy=v[v1].y-v[v2].y;
	cur->dist=dx*dx+dy*dy;
	cur->v=v2;
}

void graphadd(struct TPoly *p)
{
	int i,j,k,v1,v2,v3,v4,par,*e,listlen,abort=0;
	struct TLine l1,l2;
	struct TPoint res;
	struct TList list[MAXPOINTS+2];

	for(i=0;i<p->n;i++) {
		l1.f=p->p[i];
		l1.t=p->p[(i+1)%p->n];
		v1=vlookup(l1.f.x,l1.f.y);
		v2=vlookup(l1.t.x,l1.t.y);
		listlen=0;
		addlist(list+listlen++,v1,v1);
		addlist(list+listlen++,v1,v2);
		/* Loop through all edges already in graph */
		for(j=0;j<noVert;j++) {
			l2.f.x=v[j].x;
			l2.f.y=v[j].y;
			e=v[j].e;
			for(k=0;k<v[j].n;k++,e++) {
				l2.t.x=v[*e].x;
				l2.t.y=v[*e].y;
				if (intersect(&l1,&l2,&res,&par)) {
					//printf("(%0.0lf,%0.0lf)-(%0.0lf,%0.0lf)  (%0.0lf,%0.0lf)-(%0.0lf,%0.0lf)\n",
						//l1.f.x,l1.f.y,l1.t.x,l1.t.y, l2.f.x,l2.f.y,l2.t.x,l2.t.y);
					printf("Intersection at %0.0lf,%0.0lf\n",res.x,res.y);
					v3=vlookup(res.x,res.y);
					addedge(v3,*e); /* Split edge in existing graph */
					*e=v3;
					addlist(list+listlen++,v1,v3);
					printf("Splitting edge %0.0lf,%0.0lf-%0.0lf,%0.0lf at %0.0lf,%0.0lf\n",
						l2.f.x,l2.f.y,l2.t.x,l2.t.y,res.x,res.y);
				} else if (par) {
					// 1: l2.f - l1.f - l2.t - l1.t : split l2 at l1.f, add l2.t to list
					// 2: l1.t - l2.f - l1.f - l2.t : split l2 at l1.f, add l2.f to list
					// 3: l2.f - l1.t - l2.t - l1.f : split l2 at l1.t, add l2.t to list
					// 4: l1.f - l2.f - l1.t - l2.t : split l2 at l1.t, add l2.f to list
					// 5: l2.f - l1.f - l1.t - l2.t
					// 6: l2.f - l1.t - l1.f - l2.t
					// 7: l1.f - l2.f - l2.t - l1.t
					printf("Parallell lines! (%d)\n",par);
					if (par<5) {
						// v1=vlookup(l1.f.x,l2.f.y);
						// v2=vlookup(l1.t.x,l2.t.y);
						// j=vlookup(l2.f.x,l2.f.y);
						// *e=vlookup(l2.t.x,l2.t.y);
						printf("*%d*\n",par);
						switch (par) {
							case 1 :
								addedge(v1,*e);
								addlist(list+listlen++,v1,*e);
								*e=v1;
								break;
							case 2 :
								addedge(v1,*e);
								addlist(list+listlen++,v1,j);
								*e=v1;
								break;
							case 3 :
								addedge(v2,*e);
								addlist(list+listlen++,v1,*e);
								*e=v2;
								//printf("Splitting edge %0.0lf,%0.0lf-%0.0lf,%0.0lf at %0.0lf,%0.0lf\n",
									//l2.f.x,l2.f.y,l2.t.x,l2.t.y,l1.t.x,l1.t.y);
								break;
							case 4 :
								addedge(v2,*e);
								addlist(list+listlen++,v1,j);
								*e=v2;
								//printf("Splitting edge %0.0lf,%0.0lf-%0.0lf,%0.0lf at %0.0lf,%0.0lf\n",
									//l2.f.x,l2.f.y,l2.t.x,l2.t.y,l1.t.x,l1.t.y);
								break;							
						}
					} else {
						if (par==7) {
							addlist(list+listlen++,v1,j);
							addlist(list+listlen++,v1,*e);
						} else if (par==5) {
							addedge(v1,v2);
							addedge(v2,*e);
							*e=v1;					
						} else if (par==6) {							
							addedge(v2,v1);
							addedge(v1,*e);
							*e=v2;
							//printf("(%0.0lf,%0.0lf)-(%0.0lf,%0.0lf)  (%0.0lf,%0.0lf)-(%0.0lf,%0.0lf)\n",
//								l1.f.x,l1.f.y,l1.t.x,l1.t.y, l2.f.x,l2.f.y,l2.t.x,l2.t.y);
	//						exit(-1);
						}
					}
					//abort=1;
				}
			}
		}
		qsort(list,listlen,sizeof(struct TList),listsort);
		for(j=0;j<listlen-1;j++) {
			v1=list[j].v;
			v2=list[j+1].v;
			addedge(v1,v2);
			printf("Adding edge %0.0lf,%0.0lf-%0.0lf,%0.0lf\n",
				v[v1].x,v[v1].y,v[v2].x,v[v2].y);
		}
		//if (abort) return;
	}
}

void graphshow(void)
{
	int i,j;
	for(i=0;i<noVert;i++)
		printf("Vertex %d at %0.1lf,%0.1lf\n",i,v[i].x,v[i].y);
	for(i=0;i<noVert;i++) {
		printf("%d:",i);
		for(j=0;j<v[i].n;j++)
			printf(" %d",v[i].e[j]);
		printf("\n");
	}
}

int addpoly(struct TPoly *p1, struct TPoly *p2, struct TPoly *psum)
{
/*
	struct TPoint a,b,c;
	a.x=2; a.y=2;
	b.x=-2; b.y=0;
	c.x=-2; c.y=1;
	printf("%0.0lf\n",cross(&a,&b,&c));
	c.x=1; c.y=1;
	printf("%0.0lf\n",cross(&a,&b,&c));
*/

	noVert=0;
	graphadd(p1);
	graphadd(p2);

	graphshow();

}

int main(void)
{
	struct TPoly p1,p2,psum;

	readpoly(&p1);
	readpoly(&p2);

	addpoly(&p1,&p2,&psum);

	return 0;
}
