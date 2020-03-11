import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

class Momisawa2755{
	static int tate, yoko, hint; //盤面の縦横
	static int[] nums;		//盤面の保存
	static long s = 0, g = 0;
	static int[] area;
	static int[] area_seki;
	static HashMap<Integer,ArrayList<Integer>> yakusu;
	static int[] x,y,masu;
	static int filled = 0;
	static HashMap<T,V> onereach,manyreach;
	static F[] answer;
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		tate = sc.nextInt();
		yoko = sc.nextInt();
		hint = sc.nextInt();
		answer =new F[hint];
		area = new int[yoko * tate];
		nums = new int[yoko * tate];
		area_seki = new int[hint];
		Arrays.fill(area, 0);
		Arrays.fill(nums, 0);
		Arrays.fill(area_seki, 0);
		yakusu = new HashMap<Integer,ArrayList<Integer>>();
		boolean[] placed = new boolean[hint];
		int[] x = new int[hint];
		int[] y = new int[hint];
		int[] masu = new int[hint];
		for (int i = 0; i < hint; i++) {
			x[i] = sc.nextInt();
			y[i] = sc.nextInt();
			masu[i] = sc.nextInt();
			nums[y[i] * yoko + x[i]] = masu[i];
			if(!yakusu.containsKey(masu[i])) {
				yakusu.put(masu[i], rekkyo(masu[i]));
			}
		}
		sc.close();
		s = System.nanoTime();
		int notime = 0;
		int oldf = 0;
		while(filled < yoko * tate) {
			if(notime >= 1) {		//候補がないとき、唯一到達できるクエリを探す。
				onereach = new HashMap<T,V>();
				manyreach = new HashMap<T,V>();
				for(int i=0; i<hint; i++) {
					if(!placed[i]) {
						wa(x[i],y[i],masu[i],i);
					}
				}
				for(T t:onereach.keySet()) {
					V v=onereach.get(t);		//情報を取得
					//(x,y) => (index , siteny , sitenx , tate, yoko) HashMapの対応
					int index = v.get(0);
					int siteny = v.get(1);
					int sitenx = v.get(2);
					int tmptate = v.get(3);
					int tmpyoko = v.get(4);
					for(int i=siteny; i<siteny+tmptate; i++) {
						for(int j=sitenx; j<sitenx+tmpyoko; j++) {
							area[i*yoko + j] = index + 1;
						}
					}
					filled += -area_seki[index] + masu[index];
					area_seki[index] = masu[index];
					placed[index] = true;
					answer[index] = new F(sitenx ,siteny, tmpyoko , tmptate);
				}
				if(onereach.size()>0) {
					notime = 0;
				}
			}
			else {
				for(int i=0; i<hint; i++) {
					if(!placed[i]) {		//全て置き終わってないとき
						F sekisyugo = seki(x[i],y[i], masu[i],i);
						if(sekisyugo.get(0) != -1) {		//積集合があるとき
							int sx =sekisyugo.get(0);
							int sy =sekisyugo.get(1);
							int gx =sekisyugo.get(2);
							int gy =sekisyugo.get(3);
							int sekiS = (gy - sy) * (gx - sx);
							if(area_seki[i] < sekiS) {
								filled += sekiS - area_seki[i] ;
								area_seki[i] = sekiS;
								if(sekiS == masu[i]) {
									placed[i] = true;
								}
								answer[i] = new F(sx,sy, (gx-sx) , (gy - sy));
								for(int j = sy; j< gy; j++) {
									for(int k = sx; k < gx; k++) {
										area[j * yoko + k] = i + 1;
									}
								}
							}
						}
					}
				}
			}
			if( filled - oldf == 0) {
				notime++;
			}
			else {
				notime = 0;
				oldf = filled;
			}
		}
		g = System.nanoTime();
		outputB(area); //outputBB(area);
		System.out.println("Elapsed time:" + (g - s) / 1000000 + "." + String.format("%06d", (g - s) % 1000000) + "ms");

		for(int i=0; i<hint; i++) {
			output(answer[i]);
		}
	}
	static void wa(int x,int y,int S , int index) {		//和集合を返す
		ArrayList<Integer> tates= yakusu.get(S);
		for(int tmptate : tates) {
			int tmpyoko = S/ tmptate;
			for(int i = y - tmptate + 1; i <= y; i++) {
				for(int j = x - tmpyoko + 1; j <= x; j++) {		//左上端の座標
					if( (0<= i && i + tmptate <= tate) && (0 <= j && j+tmpyoko <= yoko)) {
						boolean verify =true;
						for(int k =0; k < tmptate; k++) {
							for(int l=0; l < tmpyoko; l++) {
								if(!(area[(i + k) * yoko + (j + l)] ==0 || area[(i+k) * yoko + (j + l) ]== index + 1)) {
									verify = false;
								}
								if( !((i+k) == y && (j+l) == x)) {		//文字のところではない
									if(nums[ (i+k) * yoko + (j+l)] > 0) {
										verify = false;
									}
								}
							}
						}
						if(verify) {
							T t = new T(j,i);		//始点のx,y
							if(manyreach.containsKey(t)) {
								//複数クエリが到達しているのでNG
							}
							else if(onereach.containsKey(t)) {
								V v = onereach.get(t);
								manyreach.put(t,v);
								onereach.remove(t);//到達可能なクエリが唯一つに定まらないのでNG
								//if(v.get(0) == index) {	//indexが同じ時、}
							}
							else {
								V v=new V(index , i , j, tmptate , tmpyoko);
								onereach.put(t, v);
							}
							//(x,y) => (index , siteny , sitenx , tate, yoko) HashMapの対応
						}
					}
					else {
					}
				}
			}
		}
	}

	static F seki(int x,int y,int S , int index) {		//積集合を返す
		ArrayList<Integer> tates= yakusu.get(S);
		F ret = new F(-1,-1,tate,yoko);		//積集合の始点x,y終点x,y		//なにもない場合は積集合のindexがおかしいのでわかる
		for(int tmptate : tates) {
			int tmpyoko = S/ tmptate;
			for(int i = y - tmptate + 1; i <= y; i++) {
				for(int j = x - tmpyoko + 1; j <= x; j++) {		//左上端の座標
					if( (0<= i && i + tmptate <= tate) && (0 <= j && j+tmpyoko <= yoko)) {
						boolean verify =true;
						for(int k =0; k < tmptate; k++) {
							for(int l=0; l < tmpyoko; l++) {
								if(!(area[(i + k) * yoko + (j + l)] ==0 || area[(i+k) * yoko + (j + l) ]== index + 1)) {
									verify = false;
								}
								if( !((i+k) == y && (j+l) == x)) {		//文字のところではない
									if(nums[ (i+k) * yoko + (j+l)] > 0) {
										verify = false;
									}
								}
							}
						}
						if(verify) {
							ret = intersection(ret , new F(j,i,(j+tmpyoko),(i+tmptate)));		//始点x,y,終点x,y
						}
					}
					else {
					}
				}
			}
		}
		return ret;
	}
	static F intersection(F f1,F f2) {		//積集合の範囲を返す関数
		F ret = new F(Math.max(f1.get(0), f2.get(0)) , Math.max(f1.get(1), f2.get(1)) , Math.min(f1.get(2), f2.get(2)) , Math.min(f1.get(3), f2.get(3)));
		return ret;
	}
	static class F {
		private int[] a;
		F(int[] x) {
			a = new int[4];
			for (int i = 0; i < 4; i++)
				a[i] = x[i];
		}
		F(F tmp) {
			for (int i = 0; i < 4; i++)
				a[i] = tmp.a[i];
		}
		public F(int e, int b, int c, int d) {
			a=new int[4];
			a[0] = e;
			a[1] = b;
			a[2] = c;
			a[3] = d;
		}
		int get(int elem) {
			return a[elem];
		}
		void set(int elem,int num) {
			a[elem] = num;
		}
	}
	static class T {		//座標
		private int[] a;
		T(int[] x) {
			a = new int[2];
			for (int i = 0; i < 2; i++)
				a[i] = x[i];
		}
		T(T tmp) {
			for (int i = 0; i < 2; i++)
				a[i] = tmp.a[i];
		}
		public T(int e, int b) {
			a=new int[2];
			a[0] = e;
			a[1] = b;
		}
		int get(int elem) {
			return a[elem];
		}
		void set(int elem,int num) {
			a[elem] = num;
		}
	}
	static class V {		//座標
		private int[] a;
		V(int[] x) {
			a = new int[5];
			for (int i = 0; i < 5; i++)
				a[i] = x[i];
		}
		V(V tmp) {
			for (int i = 0; i < 5; i++)
				a[i] = tmp.a[i];
		}
		public V(int p, int q,int r,int s,int t) {
			a=new int[5];
			a[0] = p;
			a[1] = q;
			a[2] = r;
			a[3] = s;
			a[4] = t;
		}
		int get(int elem) {
			return a[elem];
		}
		void set(int elem,int num) {
			a[elem] = num;
		}
	}
	public static void output(F t) {
		System.out.println("{" + t.a[0] + "," + t.a[1] + "," + t.a[2] + "," + t.a[3] + "}");
	}
	public static void output(V t) {
		System.out.println("{" + t.a[0] + "," + t.a[1] + "," + t.a[2] + "," + t.a[3] +"," +t.a[4] +"}");
	}
	public static void output(T t) {
		System.out.println("{" + t.a[0] + "," + t.a[1] + "}");
	}
	static ArrayList<Integer> rekkyo(int S) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ret.add(1);
		for (int i = 2; i <= S; i++) {
			if (S % i == 0) {
				ret.add(i);
			}
		}
		return ret;
	}
	public static void pl(Object o) {
		System.out.println(o);
	}
	static void outputB(int[] a) { //盤面の出力
		for (int i = 0; i < tate; i++) {
			for (int j = 0; j < yoko - 1; j++) {
				System.out.print(String.format("%03d", (a[i * yoko + j] )) + " ");
			}
			System.out.println(String.format("%03d", (a[i * yoko + yoko - 1] )) + " ");
		}
	}
	static void outputBB(int[] a) { //盤面の出力
		for (int i = 0; i < tate; i++) {
			for (int j = 0; j < yoko - 1; j++) {
				if(a[i * yoko + j] == -1) {
					System.out.print("■");
				}
				else if(a[i * yoko + j] == 0) {
					System.out.print(" ");
				}
				else {
					System.out.print(a[i*yoko + j]);
				}
			}
			if(a[i * yoko + yoko -1] == -1) {
				System.out.println("■");
			}
			else if(a[i * yoko + yoko -1] == 0) {
				System.out.println(" ");
			}
			else {
				System.out.println(a[i*yoko + yoko -1]);
			}
		}
	}
}