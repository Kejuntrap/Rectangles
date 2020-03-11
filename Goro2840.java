import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

//Goro2840はブロック単位の決め打ちを行った。ブロック単位の決め打ちで一意に定まらないと解けない
class Goro2840{
	static int tate, yoko, hint; //盤面の縦横
	static int[] nums;		//盤面の保存
	static ArrayList<F> ar_nums;		//ヒントの場所と場合の数
	static long s = 0, g = 0;
	static int flags = 0;
	static int[] area;
	static HashMap<Integer,ArrayList<Integer>> yakusu;
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		tate = sc.nextInt();
		yoko = sc.nextInt();
		hint = sc.nextInt();
		area = new int[yoko * tate];
		nums = new int[yoko * tate];
		Arrays.fill(area, 0);
		Arrays.fill(nums, 0);
		yakusu = new HashMap<Integer,ArrayList<Integer>>();
		ar_nums = new ArrayList<F>();
		boolean[] placed = new boolean[hint];

		int[] x = new int[hint];
		int[] y = new int[hint];
		int[] masu = new int[hint];
		for (int i = 0; i < hint; i++) {
			x[i] = sc.nextInt();
			y[i] = sc.nextInt();
			masu[i] = sc.nextInt();
			nums[y[i] * yoko + x[i]] = masu[i];
		}
		for(int i=0; i< hint; i++) {
			//pl(nums[y[i] * yoko + x[i]]);
			if(!yakusu.containsKey(masu[i])) {
				yakusu.put(masu[i], rekkyo(masu[i]));
			}
			ar_nums.add(new F(new int[] { x[i], y[i], masu[i], comb( x[i], y[i], masu[i], area)}));
		}
		sc.close();
		int oldflags = 0;
		int endcnt = 0;
		s = System.nanoTime();
		while(flags < hint) {
			for(int i=0; i<ar_nums.size(); i++) {
				F f =ar_nums.get(i);
				int tori = comb(f.get(0), f.get(1), f.get(2) , area);
				f.set(3,tori);
				ar_nums.set(i,f);
				//System.out.println(i+" "+tori);
			}
			for(int i=0; i<hint; i++) {
				if(!placed[i]) {
					int tori = ar_nums.get(i).get(3);
					if(tori == 1) {		//決め打ち可能なら
						F f = place(ar_nums.get(i) , area);		// fに入った値によって変える.
						if(f.get(0) != -1) {		//not nullならば
							for(int j = f.get(1); j < f.get(1) + f.get(2) ; j++) {
								for(int k = f.get(0); k < f.get(0) + f.get(3) ; k++) {
									area[ j * yoko + k] = flags + 1;
								}
							}
							flags++;
							placed[i] = true;
						}
					}
				}
			}
			if(flags - oldflags == 0) {
				endcnt++;
				if(endcnt > 1) {
					System.out.println("not determined. flag(s):"+flags);
					break;
				}
			}
			else {
				endcnt=0;
				oldflags = flags;
			}
		}
		g = System.nanoTime();
		outputB(area);
		System.out.println("Elapsed time:" + (g - s) / 1000000 + "." + String.format("%06d", (g - s) % 1000000) + "ms");
	}
	static void outputB(int[] a) { //盤面の出力
		for (int i = 0; i < tate; i++) {
			for (int j = 0; j < yoko - 1; j++) {
				System.out.print(String.format("%03d", (a[i * yoko + j] )) + " ");
			}
			System.out.println(String.format("%03d", (a[i * yoko + yoko - 1] )) + " ");
		}
	}
	static int comb(int x,int y,int S , int[] a) {		//a[] は答えの盤面
		ArrayList<Integer> tates= yakusu.get(S);
		int ret=0;
		for(int tmptate : tates) {
			int tmpyoko = S/ tmptate;
			for(int i = y - tmptate + 1; i <= y; i++) {
				for(int j = x - tmpyoko + 1; j <= x; j++) {		//左上端の座標
					if( (0<= i && i + tmptate <= tate) && (0 <= j && j+tmpyoko <= yoko)) {
						boolean verify =true;
						for(int k =0; k < tmptate; k++) {
							for(int l=0; l < tmpyoko; l++) {
								if(a[(i + k) * yoko + (j + l)] > 0) {
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
							//pl(j+" "+i+" "+tmpyoko+" "+tmptate);
							ret++;
						}
					}
					else {
						//範囲外
					}
				}
			}
		}
		return ret;
	}
	public static F place(F f, int[] a) {
		int x = f.get(0);
		int y = f.get(1);
		int S = f.get(2);
		ArrayList<Integer> tates= yakusu.get(S);
		F ret = new F(-1,-1,-1,-1);
		for(int tmptate : tates) {
			int tmpyoko = S/ tmptate;
			for(int i = y - tmptate + 1; i <= y; i++) {
				for(int j = x - tmpyoko + 1; j <= x; j++) {		//左上端の座標
					if( (0<= i && i + tmptate <= tate) && (0 <= j && j+tmpyoko <= yoko)) {
						boolean verify =true;
						for(int k =0; k < tmptate; k++) {
							for(int l=0; l < tmpyoko; l++) {
								if(a[(i + k) * yoko + (j + l)] > 0) {
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
							return new F(j , i , tmptate , tmpyoko);
						}
					}
					else {
						//範囲外
					}
				}
			}
		}
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
	public static void output(F t) {
		System.out.println("{" + t.a[0] + "," + t.a[1] + "," + t.a[2] + "," + t.a[3] + "}");
	}
	public static class Vertex {
		ArrayList<F> ary = new ArrayList<F>();
		public void add(F t) {
			ary.add(t);
		}
		public void add(int[] a) {
			F p = new F(new int[] { a[0], a[1], a[2], a[3] });
			ary.add(p);
		}
		public void show() { //output list
			for (int i = 0; i < ary.size(); i++) {
				output(ary.get(i));
			}
		}
		public int get(int pos, int elem) {
			return (ary.get(pos)).get(elem);
		}
		public F get(int pos) {
			return ary.get(pos);
		}
		public int size() {
			return ary.size();
		}
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
}