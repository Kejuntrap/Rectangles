import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

class Tateyama3015{
	static int tate,yoko,hint;
	static ArrayList<Fourple> ar_nums;
	static Vertex[] pos;		// hidariue no zahyo to tate,yoko no size wo kiroku
	static int[] nums;
	static long  s=0,g=0;
	// x y is 0-indexed;
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		tate = sc.nextInt();
		yoko = sc.nextInt();
		hint = sc.nextInt();
		int[] area = new int[tate * yoko];		//kotae wo dasu
		nums = new int[tate * yoko];		//hint no hairetu
		Arrays.fill(area, 0);
		Arrays.fill(nums, 0);
		pos =new Vertex[hint];
		for(int i=0; i<hint; i++) {
			pos[i]=new Vertex();
		}
		ar_nums=new ArrayList<Fourple>();		//3 tu no tuple nanode Threple ( Three + Tuple ) Oh f***in naming sence, isn't it?
		int[] x=new int[hint];
		int[] y=new int[hint];
		int[] masu=new int[hint];

		for(int i=0; i<hint; i++) {
			x[i]=sc.nextInt();
			y[i]=sc.nextInt();
			masu[i]=sc.nextInt();
			nums[y[i] * yoko + x[i] ] = masu[i];
		}
		for(int i=0; i<hint; i++) {
			ar_nums.add(new Fourple(x[i],y[i],masu[i],comb(x[i],y[i],masu[i])));
		}

		ar_nums.sort(Comparator.comparing(p -> (p.d)));		//edakari zone 5
		//g=System.nanoTime();
		s=System.nanoTime();
		//System.out.println("Elapsed time:"+(g-s)/1000000+"."+String.format("%06d", (g-s)%1000000) +"ms");

		possiblepos(ar_nums);

		for(int i=0; i<hint; i++) {
			output(ar_nums.get(i));
			pos[i].show();
			System.out.println("");
		}

		/*for(Fourple a : ar_nums) {
			output(a);		//debug for Threple
		}*/

		sc.close();
		dfs(0 , area);
	}

	static void possiblepos(ArrayList<Fourple> a) {

		for(int i=0; i<a.size(); i++) {
			Fourple t = a.get(i);
			ArrayList<Integer> yakusu = rekkyo(t.get(2));		//kukei wake (kukei no tate yoko ha S no yakusu de aru kara)
			for(int tmptate : yakusu) {
				int tmpyoko = t.get(2)/tmptate;
				int x = t.get(0);
				int y = t.get(1);
				if(tmptate <=tate && tmpyoko <= yoko) {
					for(int k=0; k<tmptate; k++) {
						for(int l=0; l<tmpyoko; l++) {
							if(x - l >=0 && x + (tmpyoko - 1 - l) < yoko) {
								if( y - k >= 0 && y + (tmptate - 1- k) < tate) {		//ryouiki nai
									//System.out.println(x+" "+y+" "+(x-l)+" "+(y-k) );
									if(noterritory(x-l, y-k , x , y ,tmptate , tmpyoko)) {		//hoka no suji ga nai ka
										pos[i].add(x-l, y-k,tmptate,tmpyoko);  //siten
									}
								}
							}
						}
					}
				}
			}
		}
	}
	static boolean noterritory(int sitenx,int siteny, int mojix,int mojiy ,int tat,int yok) {		// sikaku no sitenX , sitenY , hint no X , hint no Y, sikaku no tate no ookisa, sikaku no yoko no ookisa
		for(int i=siteny; i<siteny+tat; i++) {
			for(int j=sitenx; j<sitenx+yok; j++) {
				if(!(i==mojiy && j == mojix)) {
					if(nums[i*yoko+j]!=0) {
						return false;
					}
				}
			}
		}
		return true;
	}
	static long maxfill(int pops) {
		long ret=0L;
		for(int i=0; i<pops; i++) {
			ret*=2L;
			ret++;
		}
		return ret;
	}
	public static int comb(int x,int y , int masu) {		//okeru baai no kazu
		ArrayList<Integer> yakusu = rekkyo(masu);		//kukei wake (kukei no tate yoko ha S no yakusu de aru kara)
		int ret=0;
		for(int tmptate : yakusu) {
			int tmpyoko = masu/tmptate;
			if(tmptate <=tate && tmpyoko <= yoko) {
				for(int k=0; k<tmptate; k++) {
					for(int l=0; l<tmpyoko; l++) {
						if(x - l >=0 && x + (tmpyoko - 1 - l) < yoko) {
							if( y - k >= 0 && y + (tmptate - 1- k) < tate) {		//ryouiki nai
								if(noterritory(x-l, y-k , x , y ,tmptate , tmpyoko)) {		//hoka no suji ga nai ka
									ret++;
								}
							}
						}
					}
				}
			}
		}
		return ret;
	}
	static void dfs(int flags, int[] area) {		//doko ga umatteruka , area wake
		if(flags == hint) {
			g=System.nanoTime();
			System.out.println("Elapsed time:"+(g-s)/1000000+"."+String.format("%06d", (g-s)%1000000) +"ms");
			outputB(area);
			System.exit(0);
		}
		else {
			//System.out.println(flags);
			//outputB(area);
			for(int j=0; j<pos[flags].size(); j++) {
				Fourple tmp = pos[flags].get(j);
				int tmpx = tmp.get(0);
				int tmpy= tmp.get(1);
				int tmptate = tmp.get(2);
				int tmpyoko = tmp.get(3);
				//int x = ar_nums.get(flags).get(0);
				//int y = ar_nums.get(flags).get(1);
				boolean canfill =true;
				for(int k = 0; k<tmptate; k++) {
					for(int l=0; l<tmpyoko; l++) {
						if(area[(tmpy+k)*yoko + (tmpx+l)]!=0) {
							canfill=false;		//noterritory(tmpx+l,tmpy+k,x,y,tmptate,tmpyoko)
						}
					}
				}
				if(canfill) {
					int[] areac =new int[tate * yoko];
					for(int k=0; k<tate; k++) {
						for(int l=0; l<yoko; l++) {
							areac[yoko * k + l]  = area[yoko * k + l];
						}
					}
					for(int k = 0; k<tmptate; k++) {
						for(int l=0; l<tmpyoko; l++) {
							areac[(tmpy+k)*yoko + (tmpx+l)] = (flags+1);
						}
					}
					if(chk(flags+1,areac)) {
						dfs(flags+1 , areac);
					}
					//if you want , you do chk()
				}
			}
		}
	}

	static boolean chk(int flags , int[] area) {
		for(int i=flags; i<hint; i++) {
			Fourple tmp = ar_nums.get(i);
			int x = tmp.get(0);		// hint no aru x zahyo
			int y = tmp.get(1);		//hint no aru y zahyo
			int S = tmp.get(2);		//menseki
			ArrayList<Integer> yakusu = rekkyo(S);		//kukei wake (kukei no tate yoko ha S no yakusu de aru kara)
			boolean kouho =false;
			for(int recttate : yakusu) {
				if(kouho) {
					break;
				}
				else {
					int rectyoko = S / recttate;
					if(rectyoko <= yoko && recttate <= tate ) {		//edakari zone 4
						for(int k=0; k<recttate; k++) {
							for(int l=0; l<rectyoko; l++) {
								if(x - l >=0 && x + (rectyoko - 1 - l) < yoko) {
									if( y - k >= 0 && y + (recttate - 1- k) < tate) {		//ryouiki nai
										boolean unfill=true;
										for(int m= x - l; m< x - l + rectyoko; m++) {
											for(int n = y - k; n < y - k + recttate; n++) {
												if(area[n * yoko + m] != 0  || (nums[n * yoko + m] != 0 && n*yoko+m != y*yoko+x) ) {		//edakari zone 2
													unfill = false;
												}
											}
										}
										if(unfill) {
											kouho = true;
										}
									}
								}
								if(kouho) {		//edakari zone 3
									break;
								}
							}
							if(kouho) {			//edakari zone 3
								break;
							}
						}
					}
				}
			}
			if(!kouho) {
				return false;
			}
		}
		return true;
	}

	static void outputB(int[] a) {
		for(int i=0; i<tate; i++) {
			for(int j=0; j<yoko-1; j++) {
				System.out.print(String.format("%02d", (a[i * yoko + j] )) + " ");
			}
			System.out.println(String.format("%02d", (a[i * yoko + yoko - 1] )) + " ");
		}
	}
	static int pops(long a) {
		long ret=0;
		while(a>0) {
			ret += a%2L;
			a/=2L;
		}
		return ((int) ret);
	}
	static ArrayList<Integer> rekkyo(int S){
		ArrayList<Integer> ret=new ArrayList<Integer>();
		ret.add(1);
		for(int i=2; i<=S; i++) {
			if(S % i ==0) {
				ret.add(i);
			}
		}
		return ret;
	}
	static class Threple{
		private int a,b,c;
		Threple(int x,int y,int z){
			a = x;
			b = y;
			c = z;
		}
		Threple(Threple tmp){
			a = tmp.a;
			b = tmp.b;
			c = tmp.c;
		}
		int get(int elem){
			if(elem == 0) {
				return a;
			}
			else if(elem == 1) {
				return b;
			}
			else {
				return c;
			}
		}
	}

	static class Fourple{
		private int a,b,c,d;
		Fourple(int x,int y,int z,int w){
			a = x;
			b = y;
			c = z;
			d = w;
		}
		Fourple(Fourple tmp){
			a = tmp.a;
			b = tmp.b;
			c = tmp.c;
			d = tmp.d;
		}
		int get(int elem){
			if(elem == 0) {
				return a;
			}
			else if(elem == 1) {
				return b;
			}
			else if(elem == 2){
				return c;
			}
			else {
				return d;
			}
		}
	}

	public static void output(Fourple t) {
		System.out.println("{"+t.a+","+t.b+","+t.c+","+t.d+"}");
	}
	public static void output(Threple t) {
		System.out.println("{"+t.a+","+t.b+","+t.c+"}");
	}

	public static class Vertex{
		ArrayList <Fourple> ary=new ArrayList<Fourple>();
		public void add(Fourple t) {
			ary.add(t);
		}
		public void add(int a,int b,int c,int d) {
			Fourple p=new Fourple(a,b,c,d);
			ary.add(p);
		}
		public void show() { //output list
			for(int i=0; i<ary.size(); i++) {
				output(ary.get(i));
			}
		}
		public int get(int pos , int elem) {
			return (ary.get(pos)).get(elem);
		}
		public Fourple get(int pos) {
			return ary.get(pos);
		}
		public int size() {     //return the size
			return ary.size();
		}
    }
}


/*

example (intermidiate)
0 0 0 0 4 0 0
8 0 0 0 0 0 0
2 0 2 3 0 0 3
0 0 0 0 0 0 0
3 0 0 9 2 0 4
0 0 0 0 0 0 6
0 0 3 0 0 0 0

input example
7 7 12
4 0 4
0 1 8
0 2 2
2 2 2
3 2 3
6 2 3
0 4 3
3 4 9
4 4 2
6 4 4
6 5 6
2 6 3

1.780ms
1.769ms
1.841ms

https://www.nikoli.co.jp/ja/puzzles/shikaku/

example (easy)

0 0 4 0
0 3 0 3
0 0 6 0
0 0 0 0

input example

4 4 4
2 0 4
1 1 3
3 1 3
2 2 6

1.248ms
1.260ms
1.262ms


example (dificult)

  0 1 2 3 4 5 6 7 8 9
0 0 0 0 0 0 4 0 0 6 0
1 0 0 0 0 0 0 0 0 0 0
2 0 6 012 0 0 0 0 9 0
3 4 0 0 0 0 4 0 0 0 0
4 0 0 0 0 0 0 0 0 0 7
5 0 9 0 0 0 0 0 0 0 0
6 4 0 0 0 0 0 010 0 0
7 0 0 0 0 0 6 0 0 0 0
8 0 6 0 0 0 0 0 0 0 0
9 0 0 7 0 0 0 0 6 0 0

example input (H15)

10 10 15
5 0 4
8 0 6
1 2 6
3 2 12
8 2 9
0 3 4
5 3 4
9 4 7
1 5 9
0 6 4
7 6 10
5 7 6
1 8 6
2 9 7
7 9 6

4.655 ms
4.603 ms
4.795 ms


http://www.cross-plus-a.com/jp/puzzles.htm

10x10 input example (H14)

10 10 14
0 0 3
3 0 6
9 0 6
8 1 9
2 2 10
6 3 8
5 4 8
4 5 4
3 6 7
7 7 6
1 8 8
0 9 9
6 9 7
9 9 9

2.650 ms
2.507 ms
2.502 ms

http://pzv.jp/p.html?shikaku/10/10/3h6k6n9ias8n8n4n7s6i8n9k7h9



10x10 input example (H12)

10 10 12
1 0 12
8 0 8
0 1 6
9 1 8
3 3 16
6 3 4
3 6 8
6 6 16
0 8 3
9 8 9
1 9 6
8 9 4

3.163ms
3.353ms
3.154ms

https://4dvector.hatenablog.com/entry/20180527/1527423591

10x10 input example (H13)

10 10 13
6 6 25
3 0 7
2 1 3
5 1 3
0 2 9
8 2 15
2 3 16
6 3 6
3 6 3
4 6 1
1 7 6
4 7 2
2 9 4

original


10x10 input example (H13)

10 10 13
0 0 3
7 0 2
8 0 2
2 1 3
3 1 2
4 1 4
7 1 2
8 1 2
0 2 4
0 5 6
1 5 2
5 5 64
1 6 4

1.951ms
1.971ms
1.880ms


17x17 H68

17 17 68
2 0 4
7 0 4
9 0 6
14 0 5
0 1 3
4 1 6
8 1 4
12 1 2
16 1 4
2 2 9
7 2 4
9 2 6
14 2 2
0 3 4
4 3 2
12 3 6
16 3 10
2 4 4
6 4 2
10 4 3
14 4 4
5 5 6
8 5 3
11 5 3
4 6 8
12 6 2
0 7 2
2 7 3
7 7 4
9 7 4
14 7 3
16 7 2
1 8 8
5 8 6
11 8 3
15 8 3
0 9 3
2 9 6
7 9 2
9 9 2
14 9 2
16 9 4
4 10 3
12 10 6
5 11 4
8 11 4
11 11 4
2 12 3
6 12 4
10 12 6
14 12 8
0 13 2
4 13 4
12 13 4
16 13 3
2 14 3
7 14 4
9 14 3
14 14 6
0 15 6
4 15 8
8 15 5
12 15 12
16 15 2
2 16 5
7 16 2
9 16 2
14 16 3


139758.722ms


40x25 test

25 40 292
3 0 4
4 0 3
6 0 2
7 0 2
9 0 2
10 0 2
14 0 5
19 0 4
20 0 2
25 0 3
29 0 3
30 0 2
32 0 2
33 0 3
35 0 2
36 0 4
2 1 3
5 1 6
8 1 3
11 1 2
13 1 2
18 1 3
21 1 6
26 1 4
28 1 2
31 1 4
34 1 3
37 1 4
2 2 4
11 2 3
14 2 3
15 2 4
17 2 5
22 2 2
24 2 3
25 2 6
28 2 3
37 2 2
3 3 6
10 3 2
16 3 7
23 3 4
29 3 7
36 3 5
1 4 2
2 4 3
4 4 2
9 4 3
11 4 2
12 4 2
15 4 2
24 4 3
27 4 2
28 4 4
30 4 4
35 4 4
37 4 3
38 4 3
0 5 5
5 5 6
8 5 4
13 5 3
15 5 2
18 5 8
21 5 2
24 5 2
26 5 3
31 5 2
34 5 4
39 5 5
1 6 2
6 6 3
7 6 4
12 6 6
16 6 5
17 6 3
19 6 2
20 6 2
22 6 2
23 6 3
27 6 3
32 6 3
33 6 3
38 6 2
0 7 5
5 7 3
8 7 6
13 7 2
15 7 4
18 7 3
21 7 2
24 7 2
26 7 3
31 7 2
34 7 4
39 7 5
1 8 3
2 8 4
4 8 4
9 8 3
11 8 4
12 8 3
15 8 2
24 8 6
27 8 2
28 8 4
30 8 4
35 8 3
37 8 3
38 8 4
3 9 8
10 9 3
16 9 2
23 9 4
29 9 5
36 9 3
2 10 2
11 10 4
14 10 2
15 10 4
17 10 2
22 10 2
24 10 3
25 10 2
28 10 3
37 10 2
2 11 3
5 11 2
8 11 3
11 11 3
13 11 3
18 11 8
21 11 4
26 11 8
28 11 2
31 11 2
34 11 6
37 11 6
3 12 4
4 12 5
6 12 4
7 12 5
9 12 2
10 12 4
14 12 2
19 12 5
20 12 2
25 12 2
29 12 3
30 12 4
32 12 3
33 12 2
35 12 4
36 12 2
2 13 2
5 13 3
8 13 2
11 13 4
13 13 4
18 13 4
21 13 4
26 13 2
28 13 2
31 13 2
34 13 3
37 13 3
2 14 2
11 14 5
14 14 5
15 14 2
17 14 3
22 14 8
24 14 3
25 14 2
28 14 6
37 14 4
3 15 3
10 15 7
16 15 8
23 15 4
29 15 2
36 15 3
1 16 5
2 16 2
4 16 2
9 16 4
11 16 4
12 16 2
15 16 2
24 16 4
27 16 2
28 16 6
30 16 3
35 16 5
37 16 2
38 16 2
0 17 6
5 17 4
8 17 4
13 17 3
15 17 3
18 17 2
21 17 2
24 17 2
26 17 2
31 17 4
34 17 12
39 17 4
1 18 4
6 18 5
7 18 2
12 18 4
16 18 3
17 18 2
19 18 2
20 18 5
22 18 3
23 18 2
27 18 2
32 18 6
33 18 3
38 18 4
0 19 6
5 19 4
8 19 2
13 19 6
15 19 2
18 19 5
21 19 3
24 19 4
26 19 4
31 19 3
34 19 6
39 19 2
1 20 3
2 20 4
4 20 4
9 20 6
11 20 2
12 20 2
15 20 2
24 20 2
27 20 2
28 20 3
30 20 4
35 20 2
37 20 2
38 20 10
3 21 2
10 21 4
16 21 2
23 21 4
29 21 3
36 21 3
2 22 2
11 22 5
14 22 3
15 22 4
17 22 5
22 22 9
24 22 2
25 22 3
28 22 4
37 22 3
2 23 4
5 23 3
8 23 4
11 23 2
13 23 2
18 23 2
21 23 5
26 23 2
28 23 2
31 23 2
34 23 4
37 23 3
3 24 3
4 24 2
6 24 2
7 24 2
9 24 2
10 24 3
14 24 4
19 24 5
20 24 3
25 24 4
29 24 3
30 24 2
32 24 2
33 24 4
35 24 2
36 24 2



26x18 case

18 26 76
1 0 8
4 0 6
8 0 4
11 0 8
15 0 4
18 0 4
22 0 4
25 0 6
1 2 2
4 2 5
8 2 3
11 2 6
15 2 15
18 2 9
22 2 6
25 2 4
1 4 6
4 4 4
8 4 16
11 4 2
15 4 5
18 4 4
22 4 12
25 4 3
2 5 9
3 5 5
9 5 7
10 5 3
16 5 6
17 5 3
23 5 6
24 5 14
6 7 5
13 7 8
20 7 9
5 8 7
12 8 3
19 8 6
5 9 14
12 9 12
19 9 4
4 10 7
11 10 14
18 10 5
1 12 4
2 12 8
8 12 10
9 12 3
15 12 4
16 12 7
22 12 5
23 12 4
0 13 5
3 13 3
7 13 4
10 13 3
14 13 6
17 13 16
21 13 4
24 13 6
0 15 4
3 15 6
7 15 2
10 15 4
14 15 12
17 15 2
21 15 3
24 15 8
0 17 4
3 17 2
7 17 10
10 17 4
14 17 4
17 17 6
21 17 8
24 17 4


15x15 case

15 15 20
4 1 20
12 1 12
1 2 20
9 2 6
5 4 8
13 4 6
2 5 8
7 5 21
10 5 5
5 7 8
9 7 4
4 9 8
7 9 4
12 9 25
1 10 4
9 10 6
5 12 10
13 12 10
2 13 20
10 13 20

*/