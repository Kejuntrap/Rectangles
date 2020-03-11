import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

class Otensho2922 {
	static int tate, yoko, hint; //盤面の縦横
	static ArrayList<F> ar_nums; //盤面にある数字とその位置とおける通り
	static Vertex[] pos; // 置ける場所の左上の座標。　矩形の縦と横のサイズ
	static int[] nums; //盤面の数字（確認用）
	static long s = 0, g = 0;
	static int flags = 0;
	static HashMap<Integer,ArrayList<Integer>> yakusu;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		tate = sc.nextInt();
		yoko = sc.nextInt();
		hint = sc.nextInt();
		int[] area = new int[tate * yoko]; //答え出力用配列
		nums = new int[tate * yoko]; //盤面の数字を保管
		Arrays.fill(area, 0);
		Arrays.fill(nums, 0);
		pos = new Vertex[hint];
		yakusu = new HashMap<Integer,ArrayList<Integer>>();
		for (int i = 0; i < hint; i++) {
			pos[i] = new Vertex();
		}
		ar_nums = new ArrayList<F>();
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
		for(int i=0; i<hint; i++) {
			int tori = comb(x[i], y[i], masu[i],area);
			ar_nums.add(new F(new int[] { x[i], y[i], masu[i], tori}));
		}
		s = System.nanoTime();
		possiblepos(ar_nums,area);
		// /* 決め打ちBEGIN
		area = kimeuchi(area);
		 // 決め打ちEND*/
		//outputB(area);
		//System.out.println(flags);
		ar_nums.sort(Comparator.comparing(p -> (p.a[3]))); //枝刈りポイント（おける通りの少ないものからおいていくためのソート　規模が小さい場合ここがボトルネックになるが、規模が大きい場合絶大な力を発揮する）
		possiblepos(ar_nums,area);
		dfs(flags, area);
	}
	static int[] kimeuchi(int[] kimeuchiarea) {
		for(int ii=0; ii<hint; ii++) {
			int kimeuchicnt = 0;
			int cnt=0;
			for(Vertex v: pos) {
				if(v.size() == 1) {
					kimeuchicnt++;
					int recttate = v.get(0,2);
					int rectyoko = v.get(0,3);
					int siteny = v.get(0,1);
					int sitenx = v.get(0,0);
					if(kimeuchiarea[siteny * yoko + sitenx] == 0) {
						for(int i=siteny; i<siteny + recttate ; i++) {
							for(int j=sitenx; j < sitenx + rectyoko ; j++) {
								kimeuchiarea[ i * yoko + j] = flags + 1;
							}
						}
						flags++;
						kimeuchicnt++;
						cnt++;
					}
				}
				cnt++;
			}
			for(int i=0; i<hint; i++) {
				F modifyf = ar_nums.get(i);
				modifyf.set(3,comb(modifyf.get(0) , modifyf.get(1) , modifyf.get(2) , kimeuchiarea) );
				ar_nums.set(i,modifyf);		//決め打ちしたのですでに置いたブロックによっておける通り数が変化するので更新。
			}
			pos = new Vertex[hint];
			for(int i=0; i<hint; i++) {
				pos[i] = new Vertex();
			}
			possiblepos(ar_nums,kimeuchiarea);
		}
		return kimeuchiarea;
	}
	static void possiblepos(ArrayList<F> a , int[] kakunin_area) {
		for(int i=0; i<a.size(); i++) {
			F t = a.get(i);
			ArrayList<Integer> tmpyakusu = yakusu.get(t.get(2));
			for(int tmptate : tmpyakusu) {
				int tmpyoko = t.get(2)/tmptate;
				int x = t.get(0);
				int y = t.get(1);
				if(tmptate <=tate && tmpyoko <= yoko) {
					for(int k=0; k<tmptate; k++) {
						for(int l=0; l<tmpyoko; l++) {
							if(x - l >=0 && x + (tmpyoko - 1 - l) < yoko) {
								if( y - k >= 0 && y + (tmptate - 1- k) < tate) {		//領域内
									if(noterritory(x-l, y-k , x , y ,tmptate , tmpyoko,kakunin_area)) {
										pos[i].add(new F(x-l, y-k,tmptate,tmpyoko));  //始点の座標と、たてよこ
									}
								}
							}
						}
					}
				}
			}
		}
	}

	static boolean noterritory(int sitenx, int siteny, int mojix, int mojiy, int tat, int yok ,int[] kakunin_area) { // 左上のX,Y座標、長方形のたて、長方形のよこ、答えの盤面
		for (int i = siteny; i < siteny + tat; i++) {
			for (int j = sitenx; j < sitenx + yok; j++) {
				if (!(i == mojiy && j == mojix)) { //自分自身を表す盤面の数字は除外
					if (nums[i * yoko + j] != 0 || kakunin_area[i * yoko + j]!=0 ) { //盤面に自分自身以外の他の数字が書いてあったらそこにはおけないのでfalse
						return false;
					}
				}
			}
		}
		return true;
	}

	public static int comb(int x, int y, int masu , int[] a) { //おける組み合わせの数)noterritoryとかなり同じなので説明は省略)
		ArrayList<Integer> tmpyakusu = yakusu.get(masu); //面積を取得。縦と横の可能性のある長さを計算 //ここ何回もやってるから効率化できたら高速化できそう
		int ret = 0;
		for (int tmptate : tmpyakusu) {
			int tmpyoko = masu / tmptate;
			if (tmptate <= tate && tmpyoko <= yoko) {
				for (int k = 0; k < tmptate; k++) {
					for (int l = 0; l < tmpyoko; l++) {
						if (x - l >= 0 && x + (tmpyoko - 1 - l) < yoko) {
							if (y - k >= 0 && y + (tmptate - 1 - k) < tate) {
								if (noterritory(x - l, y - k, x, y, tmptate, tmpyoko, a)) {
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

	static void dfs(int flags, int[] area ) { //DFS flagsはいくつヒントを使ったかを記録（flags == hint で答えの盤面完成）
		//System.out.println(flags);
		if (flags >= hint) { //完成したら出力。（出力したら即終了で1つ）
			g = System.nanoTime();
			outputB(area);
			System.out.println("Elapsed time:" + (g - s) / 1000000 + "." + String.format("%06d", (g - s) % 1000000) + "ms");
			//System.out.println(flags);
			//System.exit(0);
		} else {
			for (int j = 0; j < pos[flags].size(); j++) {
				F tmp = pos[flags ].get(j);
				int tmpx = tmp.get(0);
				int tmpy = tmp.get(1);
				int tmptate = tmp.get(2);
				int tmpyoko = tmp.get(3);
				boolean canfill = true; //埋める候補のところで、すでに他の領域が占領してないかを確認
				for (int k = 0; k < tmptate; k++) {
					for (int l = 0; l < tmpyoko; l++) {
						if (area[(tmpy + k) * yoko + (tmpx + l)] != 0) { //すでに埋まってたらNG
							canfill = false;
						}
					}
				}
				if (canfill) { //候補の盤面を作成
					int[] areac = new int[tate * yoko];
					for (int k = 0; k < tate; k++) {
						for (int l = 0; l < yoko; l++) {
							areac[yoko * k + l] = area[yoko * k + l];
						}
					}
					for (int k = 0; k < tmptate; k++) {
						for (int l = 0; l < tmpyoko; l++) {
							areac[(tmpy + k) * yoko + (tmpx + l)] = flags + 1;
						}
					}
					if (chk(flags + 1, areac)) {		//おけないものが存在するか？
						dfs(flags + 1, areac);
					}
				}
			}
		}
	}

	static boolean chk(int flags, int[] area) {
		for (int i = flags; i < ar_nums.size(); i++) {
			F tmp = ar_nums.get(i);
			int x = tmp.get(0); // ヒントのX
			int y = tmp.get(1); //ヒントのY
			int S = tmp.get(2); //面積
			ArrayList<Integer> tmpyakusu = yakusu.get(S); //面積を取得。縦と横の可能性のある長さを計算
			boolean kouho = false;
			for (int recttate : tmpyakusu) {
				if (kouho) {
					break;
				}
				else if(tmp.get(3) == 0 ) {
					continue;
				}
				else{
					int rectyoko = S / recttate;
					if (rectyoko <= yoko && recttate <= tate) {
						for (int k = 0; k < recttate; k++) {
							for (int l = 0; l < rectyoko; l++) {
								if (x - l >= 0 && x + (rectyoko - 1 - l) < yoko) {
									if (y - k >= 0 && y + (recttate - 1 - k) < tate) {
										boolean unfill = true;
										for (int m = x - l; m < x - l + rectyoko; m++) {
											for (int n = y - k; n < y - k + recttate; n++) {
												if (area[n * yoko + m] != 0 || (nums[n * yoko + m] != 0 && n * yoko + m != y * yoko + x)) { //edakari zone 2
													unfill = false;
												}
											}
										}
										if (unfill) {
											kouho = true;
										}
									}
								}
								if (kouho) { //枝刈りポイント　条件を満たしたら即ループを抜ける
									break;
								}
							}
							if (kouho) { //枝刈りポイント　条件を満たしたら即ループを抜ける
								break;
							}
						}
					}
				}
			}
			if (!kouho) {
				return false;
			}
		}
		return true;
	}

	static void outputB(int[] a) { //盤面の出力
		for (int i = 0; i < tate; i++) {
			for (int j = 0; j < yoko - 1; j++) {
				System.out.print(String.format("%03d", (a[i * yoko + j] )) + " ");
			}
			System.out.println(String.format("%03d", (a[i * yoko + yoko - 1] )) + " ");
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
}