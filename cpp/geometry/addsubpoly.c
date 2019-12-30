#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <assert.h>

#define MAXPOINTS 100
#define MAXVERTICES (MAXPOINTS*MAXPOINTS)

#define eps 0.000001

int noVert;

struct TEdge {
	int v;
	struct TEdge *next;
};

struct TVertex {
	double x,y;
	struct TEdge *e;
	int step;
} v[MAXVERTICES];

struct TPoint {
	double x,y;
};

struct TPoly {
	int n;
	struct TPoint p[MAXPOINTS];
};

struct TLine {
  struct TPoint f,t;
};

struct TList {
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

double vectdist(struct TPoint *org, struct TPoint *dest, struct TPoint *v)
{
	if (!dEq(v->x,0))
  	return (dest->x-org->x)/v->x;
	return (dest->y-org->y)/v->y;
}

double getangle(struct TPoint *a, struct TPoint *b)
{
	double alen,blen,tsin,tcos;
	alen=sqrt(a->x*a->x+a->y*a->y);
	blen=sqrt(b->x*b->x+b->y*b->y);
	tcos=(a->x*b->x+a->y*b->y)/(alen*blen);
	tsin=(a->x*b->y-a->y*b->x)/(alen*blen);
	if (tsin>eps)
		return tcos-1;
	return -tcos+1;
}

int intersect(struct TLine *a, struct TLine *b, struct TPoint *res, int *para) {
  struct TPoint vw0,v1,w1,c;
  double  fa,fb,d0,af,at;

  if (para) *para=0;

  sub(&(a->t),&(a->f),&v1);
  sub(&(b->f),&(b->t),&w1);
  sub(&(b->f),&(a->f),&vw0);

  d0 = det(&v1,&w1);
 	if (!dEq(d0,0)) {
    fa = det(&vw0,&w1)/d0;
    fb = det(&v1,&vw0)/d0;
    if ((fabs(fa-0.5)<=0.5) && (fabs(fb-0.5)<0.5)) {
    	if (res) {
      	res->x=fa*v1.x+a->f.x;
      	res->y=fa*v1.y+a->f.y;
      }
      return 1;
    }
  }
  else if (para) {
  	sub(&(b->t),&(b->f),&w1);
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
	v[noVert].e=0;
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
	struct TEdge *e;
	if (v1!=v2) {
		e=malloc(sizeof(struct TEdge));
		e->next=v[v1].e;
		e->v=v2;
		v[v1].e=e;
	}
}

void addlist(struct TList *cur, int v1, int v2)
{
	double dx,dy;
	dx=v[v1].x-v[v2].x;
	dy=v[v1].y-v[v2].y;
	cur->dist=dx*dx+dy*dy;
	cur->v=v2;
}

void graphput(struct TPoly *p, int dir)
{
	int i,j,k,v1,v2,v3,v4,par,listlen;
	struct TLine l1,l2;
	struct TPoint res;
	struct TEdge *e;
	struct TList list[MAXPOINTS+2];

	for(i=0;i<p->n;i++) {
		l1.f=p->p[i];
		l1.t=p->p[(i+dir+p->n)%p->n];
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
			while (e) {
				l2.t.x=v[e->v].x;
				l2.t.y=v[e->v].y;
				if (intersect(&l1,&l2,&res,&par)) {
					v3=vlookup(res.x,res.y);
					addedge(v3,e->v); /* Split edge in existing graph */
					e->v=v3;
					addlist(list+listlen++,v1,v3);
				} else if (par) {
					switch (par) {
						case 1 : v3=v1; v4=e->v; break;
						case 2 : v3=v1; v4=j; break;
						case 3 : v3=v2; v4=e->v; break;
						case 4 : v3=v2; v4=j; break;
						case 5 :
							addedge(v1,v2);
							addedge(v2,e->v);
							e->v=v1;
							break;
						case 6 :
							addedge(v2,v1);
							addedge(v1,e->v);
							e->v=v2;
						case 7 :
							addlist(list+listlen++,v1,j);
							addlist(list+listlen++,v1,e->v);
							break;
					}
					if (par<5) {
						addedge(v3,e->v);
						addlist(list+listlen++,v1,v4);
						e->v=v3;
					}
				}
				e=e->next;
			}
		}
		qsort(list,listlen,sizeof(struct TList),listsort);
		for(j=0;j<listlen-1;j++) {
			v1=list[j].v;
			v2=list[j+1].v;
			addedge(v1,v2);
		}
	}
}

void polyadd(struct TPoly *p)
{
	int i,j,next,cur,step,maxstep;
	double angle,best;
	struct TPoint last,w;
	struct TEdge *e,*f,**g;
	
	graphput(p,1);
	/* Remove edges that is not part of the resulting polygon */
	for(i=0;i<noVert;i++)
		v[i].step=0;
	cur=-1;
	for(i=0;i<noVert;i++)
		if (v[i].e && ((cur<0) || (v[i].x<v[cur].x)))
			cur=i;
	last.x=-1;
	last.y=0;
	step=1;
	/* Walk along the edge counter-clockwise */
	do {
		v[cur].step=step++;
		//printf("%d ",cur);
		e=v[cur].e;
		best=3;
		while (e) {
			if (e->v!=cur) {
				w.x=v[e->v].x-v[cur].x;
				w.y=v[e->v].y-v[cur].y;
				angle=getangle(&last,&w);
				if (angle<best) {
					best=angle;
					next=e->v;
				}
			}
			e=e->next;
		}
		assert(best<3);
		last.x=v[next].x-v[cur].x;
		last.y=v[next].y-v[cur].y;
		cur=next;		
	} while (!v[cur].step);	
	//printf("\n");
	maxstep=step;
	step=v[cur].step;
	for(i=0;i<noVert;i++) {
		g=&v[i].e;
		e=v[i].e;
		j=v[i].step+1;
		if (j==maxstep) j=step;
		while (e) {
			//printf("%d->%d %d %d\n",i,e->v,v[i].step,v[e->v].step);			
			if ((v[i].step>=step) && (v[e->v].step==j)) {
				*g=e;
				g=&e->next;
				e=e->next;
				j=-1;
			} else {
				//printf("Removing edge %d->%d\n",i,e->v);
				f=e->next;
				free(e);
				e=f;
			}			
		}
		*g=0;
	}
}

void polysub(struct TPoly *p)
{
	graphput(p,-1);
}

void polyget(struct TPoly *p)
{
	int i,start,cur;
	p->n=0;
	for(i=0;i<noVert;i++)
		if (v[i].e) break;
	if (i==noVert)
		return;
	cur=0;
	start=i;
	do {
		p->p[cur].x=v[i].x;
		p->p[cur++].y=v[i].y;
		i=v[i].e->v;
	} while (i!=start);	
	p->n=cur;
}

void graphshow(void)
{
	int i;
	struct TEdge *e;
	for(i=0;i<noVert;i++)
		printf("Vertex %d at %0.1lf,%0.1lf\n",i,v[i].x,v[i].y);
	for(i=0;i<noVert;i++) {
		printf("%d:",i);
		e=v[i].e;
		while (e) {
			printf(" %d",e->v);
			e=e->next;
		}
		printf("\n");
	}
}

void readpoly(struct TPoly *p)
{
	int i;
	scanf("%d",&p->n);
	for(i=0;i<p->n;i++)
		scanf("%lf %lf",&p->p[i].x,&p->p[i].y);
}

void writepoly(struct TPoly *p)
{
	int i;
	printf("%d\n",p->n);
	for(i=0;i<p->n;i++)
		printf("%0.1lf %0.1lf\n",p->p[i].x,p->p[i].y);
}

int main(void)
{
	struct TPoly p1,p2,p3;
	struct TPoint a,b,c;

	readpoly(&p1);
	readpoly(&p2);
	
	noVert=0;

	polyadd(&p1);
	polyadd(&p2);

	//graphshow();
	
	polyget(&p3);
	writepoly(&p3);

	return 0;
}
