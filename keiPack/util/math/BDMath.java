package keiPack.util.math;

import java.math.BigDecimal;

/**
 * KeiBDMathは、BigDecimal型の数を使った計算(階乗、平方根など)をまとめたクラスです。<br>
 *
 * @author Keita
 * @version 1.2
 */
public class BDMath{

	/**
	 * -1 です。
	 */
	public static final BigDecimal MINUS = new BigDecimal("-1");

	public static final BigDecimal TWO = new BigDecimal("2");

	/**
	 * 円周率を小数第501位で四捨五入した値<br>
	 * http://www.geocities.jp/f9305710/PAI1000000.html より
	 */
	public static final BigDecimal PIE = new BigDecimal("3.14159265358979323846264338327950288419716939937510582097494459230781640628620899862803482534211706798214808651328230664709384460955058223172535940812848111745028410270193852110555964462294895493038196442881097566593344612847564823378678316527120190914564856692346034861045432664821339360726024914127372458700660631558817488152092096282925409171536436789259036001133053054882046652138414695194151160943305727036575959195309218611738193261179310511854807446237996274956735188575272489122793818301194913");

	/**
	 * ネイピア数を小数第501位で四捨五入した値<br>
	 * http://members2.jcom.home.ne.jp/seroron/kazunoizumi/e.htm より
	 */
	public static final BigDecimal E = new BigDecimal("2.71828182845904523536028747135266249775724709369995957496696762772407663035354759457138217852516642742746639193200305992181741359662904357290033429526059563073813232862794349076323382988075319525101901157383418793070215408914993488416750924476146066808226480016847741185374234544243710753907774499206955170276183860626133138458300075204493382656029760673711320070932870912744374704723069697720931014169283681902551510865746377211125238978442505695369677078544996996794686445490598793163688923009879313");


	/**
	 * 10^1000以下の正の数の平方根を求めるメソッドです。
	 * @param radicand 平方根を求めたい値。
	 * @param maxDigit 小数点以下何桁まで求めるか。<b>あまり大きくし過ぎると計算が遅くなるので注意。</b>
	 * @return BigDecimal 答えの値を返します。
	 * @throws KeiMathException getCode()でコードを取得できます。<br>
	 * このメソッドではradicandが負の数の時(code:2)、<br>
	 * radicandが10の1000乗以上、またはmaxDigitが3000以下(code:3)の時<br>
	 * にスロー。<br>
	 */
	final public static BigDecimal sqrt(BigDecimal radicand, int maxDigit) throws KeiMathException{
		final BigDecimal MAX_VALUE = new BigDecimal("1E+1000");	//【重要】これを変えたらgradeの初期値とこのメソッドのjavadocも変える。
		final int MAX_M_DIGIT = 3000;
		if (radicand.compareTo(BigDecimal.ZERO) < 0){
			throw new KeiMathException(2, "負の値なので平方根を求められません");
		}else if (radicand.compareTo(MAX_VALUE) > 0){
			throw new KeiMathException(3, MAX_VALUE + "以下の値を入力してください");
		}else if (maxDigit > MAX_M_DIGIT){
			throw new KeiMathException(3, "最大小数桁数は" + MAX_M_DIGIT + "以下の値を指定してください。");
		}
		BigDecimal ans = new BigDecimal(0);
		final BigDecimal THREE = new BigDecimal(3);
		final BigDecimal FIVE = new BigDecimal(5);
		BigDecimal grade = new BigDecimal("1E+499");	//MAX_VALUEの値によって初期値を変える。
		for (int digitNum = 1;	//maxDigitとの比較に使う。
				digitNum <= maxDigit + 500;
				digitNum++) {

			if (radicand.compareTo(grade.multiply(grade)) >= 0){
				/*
				 * その桁の数字の答え(pとする)が5以上のときとそうでない時にまず分岐。
				 * ５以上の時は8で同様に分岐させ、そうでないときは3で分岐させる。
				 * ans*ansを求める回数が7割くらいですむ。
				*/
				ans = ans.add(grade.multiply(FIVE));	//その桁の数字を5にする
				if (radicand.compareTo(ans.multiply(ans)) < 0){	//(その桁の数字の答えは0以上5未満)
					ans = ans.subtract(grade.multiply(TWO));	//その桁の数字を3にする
					if (radicand.compareTo(ans.multiply(ans)) < 0){	//(その桁の数字の答えは0以上3未満)
						ans = ans.subtract(grade);	//その桁の数字を2に
						if (radicand.compareTo(ans.multiply(ans)) < 0){
							ans = ans.subtract(grade);	//その桁の数字を1に
							if (radicand.compareTo(ans.multiply(ans)) < 0){
								ans = ans.subtract(grade);	//その桁の数字は0
							}
						}
					}else {		//radicand >= (ans * ans)	(その桁の数字の答えは3か4)
						ans = ans.add(grade);	//その桁の数字を4に
						if (radicand.compareTo(ans.multiply(ans)) < 0){
							ans = ans.subtract(grade);	//その桁の数字は3
						}
					}
				}else {		//radicand >= (ans * ans)	(その桁の数字の答えは5以上9以下)
					ans = ans.add(grade.multiply(THREE));	//その桁の数字を8にする
					if (radicand.compareTo(ans.multiply(ans)) < 0){	//(その桁の数字の答えは5以上8未満)
						ans = ans.subtract(grade);	//その桁の数字を7に
						if (radicand.compareTo(ans.multiply(ans)) < 0){
							ans = ans.subtract(grade);	//その桁の数字を6に
							if (radicand.compareTo(ans.multiply(ans)) < 0){
								ans = ans.subtract(grade);	//その桁の数字は5
							}
						}
					}else {		//radicand >= (ans * ans)	(その桁の数字の答えは8か9)
						ans = ans.add(grade);	//その桁の数字を9に
						if (radicand.compareTo(ans.multiply(ans)) < 0){
							ans = ans.subtract(grade);	//その桁の数字は8
						}
					}
				}
			}
			if (radicand.compareTo(ans.multiply(ans)) == 0){
				break;
			}
			grade = grade.divide(BigDecimal.TEN);
		}

		return ans;
	}

	/**
	 * 0以上50000以下の整数の階乗を求めるメソッドです。
	 * @param val 階乗を求めたい値
	 * @return BigDecimal 答えの値を返します。
	 * @throws KeiMathException getCode()でコードを取得できます。<br>
	 * このメソッドではvalが負の数の時(code:2)、<br>
	 * valが50000を超えたとき(code:3)、<br>
	 * valが小数値の時(code:4)<br>
	 * にスロー。
	 */
	final public static BigDecimal factorialWhole(BigDecimal val) throws KeiMathException{
		final BigDecimal MAX_VALUE = new BigDecimal("50000");
		val = val.stripTrailingZeros();
		if (val.compareTo(BigDecimal.ZERO) < 0){
			throw new KeiMathException(2, "負の数なので求められません");
		}else if (val.compareTo(MAX_VALUE) > 0){
			throw new KeiMathException(3, MAX_VALUE + "以下の整数値を入力してください");
		}else if (val.scale() > 0){
			throw new KeiMathException(4, "整数値を入力してください");
		}

		BigDecimal ans = new BigDecimal(1);
		BigDecimal i;
		for (i = new BigDecimal(1); i.compareTo(val) <= 0; i = i.add(BigDecimal.ONE)){
			ans = ans.multiply(i);
		}
		return ans;
	}

	/**
	 * <strong>指数が整数であるときのみ</strong>累乗数を求めるメソッドです。
	 * @param base -(10^50)以上10^50以下の底の値です。
	 * @param exponent -10000以上10^10以下の指数の値です。
	 * @param maxDigit 小数以下何桁目まで求めるかです。exponentが0以上のときは使いません。<b>あまり大きくし過ぎると計算に時間がかかります</b>
	 * @return BigDecimal 答えの値です。
	 * @throws KeiMathException getCode()でコードを取得できます。<br>
	 * このメソッドでは、baseやexponentの値が大き(小さ)いとき(code:3)とexponentが小数の時(code:4)にスローされます。
	 */
	final public static BigDecimal powWhole(BigDecimal base, BigDecimal exponent, int maxDigit) throws KeiMathException{
		final BigDecimal MAX_BASE_VAL = new BigDecimal("1E+50");
		final BigDecimal MIN_BASE_VAL = MAX_BASE_VAL.multiply(MINUS);
		final BigDecimal MAX_EXPONENT_VAL = new BigDecimal("999999999");	//BigDecimal型powメソッドの都合上
		final BigDecimal MIN_EXPONENT_VAL = new BigDecimal("-10000");
		exponent = exponent.stripTrailingZeros();
		if (base.compareTo(MAX_BASE_VAL) > 0 || base.compareTo(MIN_BASE_VAL) < 0){
			throw new KeiMathException(3, "底は" + MIN_BASE_VAL + "以上" + MAX_BASE_VAL + "以下の値を指定してください");
		}else if (exponent.compareTo(MAX_EXPONENT_VAL) > 0 || exponent.compareTo(MIN_EXPONENT_VAL) < 0){
			throw new KeiMathException(3, "指数は" + MIN_EXPONENT_VAL + "以上" + MAX_EXPONENT_VAL + "以下の値を指定してください");
		}else if (exponent.scale() > 0){
			throw new KeiMathException(4, "指数は整数値を指定してください");
		}

		BigDecimal ans;
		int expoSignum = exponent.signum();
		if (expoSignum == -1){
			exponent = exponent.multiply(MINUS);
		}
		ans = base.pow(exponent.intValueExact());
		if (expoSignum == -1){
			ans = BigDecimal.ONE.divide(ans, maxDigit, BigDecimal.ROUND_HALF_UP);
		}
		ans = ans.stripTrailingZeros();
		return ans;
	}

	/**
	 * <strong>指数が整数であるときのみ</strong>累乗根を求めるメソッドです。
	 * @param radicand 被開平数の値(10^50以下の正の数)
	 * @param index 指数の値(-20以上100以下の整数(0を除く))
	 * @param maxDigit 小数以下何桁目まで求めるかです。負の数の場合10^(-maxDigit)まで求めます。<b>あまり大きすぎると計算に時間がかかります</b>
	 * @return BigDecimal 答えの値です。
	 * @throws KeiMathException getCode()でコードを取得できます。<br>
	 * このメソッドでは、radicandが負の数またはindexの値が0のとき(code:2)、<br>
	 * radicandが10^50を超える、またはindexが0以上100以下<b>でない</b>、またはmaxDigitが300以下の場合(code:3)、<br>
	 * indexが整数でない場合(code:4)
	 * にスロー。
	 */
	final public static BigDecimal radicalRootWhole(BigDecimal radicand, BigDecimal index, int maxDigit) throws KeiMathException{
		final BigDecimal MAX_RADICAND_VAL = new BigDecimal("1E+50");	//【重要】これを変えたらgradeの初期値とこのメソッドのjavadocも変える。
		final BigDecimal MAX_INDEX_VAL = new BigDecimal("100");		//【重要】これを変えたらこのメソッドのjavadocも変える。
		final BigDecimal MIN_INDEX_VAL = new BigDecimal("-20");		//【重要】これを変えたらこのメソッドのjavadocも変える。
		final int MAX_M_DIGIT = 300;	//maxDigitの最大値制限
		index = index.stripTrailingZeros();
		if (index.scale() > 0){
			throw new KeiMathException(4, "指数は整数値を指定してください");
		}else if ((radicand.compareTo(BigDecimal.ZERO) < 0 && index.divide(TWO).scale() <= 0) || index.compareTo(BigDecimal.ZERO) == 0){
			throw new KeiMathException(2, "値が不正です");
		}else if (radicand.compareTo(MAX_RADICAND_VAL) > 0){
			throw new KeiMathException(3, "被開平数は" + MAX_RADICAND_VAL + "以下の値を指定してください");
		}else if (index.compareTo(MAX_INDEX_VAL) > 0 || index.compareTo(MIN_INDEX_VAL) < 0){
			throw new KeiMathException(3, "指数は" + MIN_INDEX_VAL + "以上" + MAX_INDEX_VAL + "以下の整数値(0を除く)を指定してください");
		}else if(maxDigit > MAX_M_DIGIT){
			throw new KeiMathException(3, "最大の小数桁数は" + MAX_M_DIGIT + "以下の値を入力してください");
		}

		boolean multMinus = false;
		if (radicand.compareTo(BigDecimal.ZERO) < 0){	//indexが偶数の場合例外をスローしているので来ない
			radicand = radicand.multiply(MINUS);
			multMinus = true;
		}

		BigDecimal ans = new BigDecimal(0);
		final BigDecimal THREE = new BigDecimal(3);
		final BigDecimal FIVE = new BigDecimal(5);
		BigDecimal grade;
		int a;
		if (index.compareTo(BigDecimal.ZERO) > 0){
			grade = new BigDecimal("1E+" + new BigDecimal(50).divide(index, 0, BigDecimal.ROUND_UP));	//MAX_RADICAND_VALの値によって初期値を変える。
			a = new BigDecimal(50).divide(index, 0, BigDecimal.ROUND_UP).intValue();	//下のfor文の条件式で使用
		}else {
			grade = new BigDecimal("1E+" + new BigDecimal(50).divide(index, 0, BigDecimal.ROUND_UP).multiply(MINUS));	//MAX_RADICAND_VALの値によって初期値を変える。
			a = new BigDecimal(50).divide(index, 0, BigDecimal.ROUND_UP).multiply(MINUS).intValue();	//下のfor文の条件式で使用
		}
		BigDecimal indexPowOfAns = BigDecimal.ZERO;		//計算量を少しだけ減らすためのもの

		for(int digitNum = 1;		//maxDigitとの比較に使う。
				digitNum <= maxDigit + a + 1;
				digitNum++){
			/*
			 * その桁の数字の答え(pとする)が5以上のときとそうでない時にまず分岐。
			 * ５以上の時は8で同様に分岐させ、そうでないときは3で分岐させる。
			 * ans*ansを求める回数が7割くらいですむ。
			*/
			if (index.compareTo(BigDecimal.ZERO) > 0
					&& radicand.compareTo(powWhole(grade, index, maxDigit+10)) >= 0){	//比較しているのはgrade^indexなので注意

				ans = ans.add(grade.multiply(FIVE));	//その桁の数字を5にする
				if (radicand.compareTo(powWhole(ans, index, maxDigit+10)) < 0){	//(その桁の数字の答えは0以上5未満)
					ans = ans.subtract(grade.multiply(TWO));	//その桁の数字を3にする
					if (radicand.compareTo(powWhole(ans, index, maxDigit+10)) < 0){	//(その桁の数字の答えは0以上3未満)
						ans = ans.subtract(grade);	//その桁の数字を2に
						indexPowOfAns = powWhole(ans, index, maxDigit+10);
						if (radicand.compareTo(indexPowOfAns) < 0){
							ans = ans.subtract(grade);	//その桁の数字を1に
							indexPowOfAns = powWhole(ans, index, maxDigit+10);
							if (radicand.compareTo(indexPowOfAns) < 0){
								ans = ans.subtract(grade);	//その桁の数字は0
								indexPowOfAns = powWhole(ans, index, maxDigit+10);
							}
						}
					}else {		//radicand >= ans^index	(その桁の数字の答えは3か4)
						ans = ans.add(grade);	//その桁の数字を4に
						indexPowOfAns = powWhole(ans, index, maxDigit+10);
						if (radicand.compareTo(indexPowOfAns) < 0){
							ans = ans.subtract(grade);	//その桁の数字は3
							indexPowOfAns = powWhole(ans, index, maxDigit+10);
						}
					}
				}else {		//radicand >= ans^index	(その桁の数字の答えは5以上9以下)
					ans = ans.add(grade.multiply(THREE));	//その桁の数字を8にする
					if (radicand.compareTo(powWhole(ans, index, maxDigit+10)) < 0){	//(その桁の数字の答えは5以上8未満)
						ans = ans.subtract(grade);	//その桁の数字を7に
						indexPowOfAns = powWhole(ans, index, maxDigit+10);
						if (radicand.compareTo(indexPowOfAns) < 0){
							ans = ans.subtract(grade);	//その桁の数字を6に
							indexPowOfAns = powWhole(ans, index, maxDigit+10);
							if (radicand.compareTo(indexPowOfAns) < 0){
								ans = ans.subtract(grade);	//その桁の数字は5
								indexPowOfAns = powWhole(ans, index, maxDigit+10);
							}
						}
					}else {		//radicand >= ans^index	(その桁の数字の答えは8か9)
						ans = ans.add(grade);	//その桁の数字を9に
						indexPowOfAns = powWhole(ans, index, maxDigit+10);
						if (radicand.compareTo(indexPowOfAns) < 0){
							ans = ans.subtract(grade);	//その桁の数字は8
							indexPowOfAns = powWhole(ans, index, maxDigit+10);
						}
					}
				}

			}else if (index.compareTo(BigDecimal.ZERO) < 0
					&& radicand.compareTo(powWhole(grade, index, maxDigit+10)) <= 0){	//比較しているのはgrade^indexなので注意

				ans = ans.add(grade.multiply(FIVE));	//その桁の数字を5にする
				if (radicand.compareTo(powWhole(ans, index, maxDigit+10)) > 0){	//(その桁の数字の答えは0以上5未満)
					ans = ans.subtract(grade.multiply(TWO));	//その桁の数字を3にする
					if (radicand.compareTo(powWhole(ans, index, maxDigit+10)) > 0){	//(その桁の数字の答えは0以上3未満)
						ans = ans.subtract(grade);	//その桁の数字を2に
						indexPowOfAns = powWhole(ans, index, maxDigit+10);
						if (radicand.compareTo(indexPowOfAns) > 0){
							ans = ans.subtract(grade);	//その桁の数字を1に
							indexPowOfAns = powWhole(ans, index, maxDigit+10);
							if (radicand.compareTo(indexPowOfAns) > 0){
								ans = ans.subtract(grade);	//その桁の数字は0
								indexPowOfAns = powWhole(ans, index, maxDigit+10);
							}
						}
					}else {		//radicand <= ans^index	(その桁の数字の答えは3か4)
						ans = ans.add(grade);	//その桁の数字を4に
						indexPowOfAns = powWhole(ans, index, maxDigit+10);
						if (radicand.compareTo(indexPowOfAns) > 0){
							ans = ans.subtract(grade);	//その桁の数字は3
							indexPowOfAns = powWhole(ans, index, maxDigit+10);
						}
					}
				}else {		//radicand <= ans^index	(その桁の数字の答えは5以上9以下)
					ans = ans.add(grade.multiply(THREE));	//その桁の数字を8にする
					if (radicand.compareTo(powWhole(ans, index, maxDigit+10)) > 0){	//(その桁の数字の答えは5以上8未満)
						ans = ans.subtract(grade);	//その桁の数字を7に
						indexPowOfAns = powWhole(ans, index, maxDigit+10);
						if (radicand.compareTo(indexPowOfAns) > 0){
							ans = ans.subtract(grade);	//その桁の数字を6に
							indexPowOfAns = powWhole(ans, index, maxDigit+10);
							if (radicand.compareTo(indexPowOfAns) > 0){
								ans = ans.subtract(grade);	//その桁の数字は5
								indexPowOfAns = powWhole(ans, index, maxDigit+10);
							}
						}
					}else {		//radicand <= ans^index	(その桁の数字の答えは8か9)
						ans = ans.add(grade);	//その桁の数字を9に
						indexPowOfAns = powWhole(ans, index, maxDigit+10);
						if (radicand.compareTo(indexPowOfAns) > 0){
							ans = ans.subtract(grade);	//その桁の数字は8
							indexPowOfAns = powWhole(ans, index, maxDigit+10);
						}
					}
				}

			}
			if (radicand.compareTo(indexPowOfAns) == 0){
				break;
			}
			grade = grade.divide(BigDecimal.TEN);
		}
		if (multMinus){
			ans = ans.multiply(MINUS);
		}
		return ans;
	}

	final public static BigDecimal pow(BigDecimal base, BigDecimal exponent, int maxDigit) throws KeiMathException{
		BigDecimal ans;	//答えの値
		if (exponent.scale() < 0){
			ans =powWhole(base, exponent, maxDigit);
		}else {
			int expoSignum = exponent.signum();	//exponentの正負。正->1, 負->-1 (0にはならない)
			if (expoSignum == -1){
				exponent = exponent.multiply(MINUS);
			}

			BigDecimal expoWhole = exponent.setScale(0, BigDecimal.ROUND_DOWN);	//exponentの整数部分
			ans = powWhole(base, expoWhole, maxDigit);

			BigDecimal expoDecimal = exponent.subtract(expoWhole);	//exponentの小数部分。勿論0以上1未満
			while (expoDecimal.compareTo(BigDecimal.ZERO) != 0){
				expoDecimal = expoDecimal.movePointRight(1);	//小数部分を10倍。
				BigDecimal numeral = expoDecimal.setScale(0, BigDecimal.ROUND_DOWN);	//その桁の数字を取得
				expoDecimal = expoDecimal.subtract(numeral);	//その桁の数を引いて0以上1未満の小数に。

				base = radicalRootWhole(base, BigDecimal.TEN, maxDigit+3);
				ans = ans.multiply(powWhole(base, numeral, 0));	//powWholeの最大桁数を0にしているのはそれが関係ないため
				ans = ans.setScale(maxDigit+3, BigDecimal.ROUND_HALF_EVEN);	//何度も同じ計算を繰り返すのでROUND_HALF_EVENに。
			}
			ans = ans.setScale(maxDigit, BigDecimal.ROUND_HALF_UP);

			if(expoSignum == -1){
				ans = BigDecimal.ONE.divide(ans, maxDigit, BigDecimal.ROUND_HALF_UP);
			}
		}

		return ans;
	}

	/**
	 * 最大公約数を返す
	 */
	final public static BigDecimal gcm(BigDecimal a, BigDecimal b) throws KeiMathException {
		a = a.stripTrailingZeros();
		b = b.stripTrailingZeros();
		if (a.scale() > 0 || b.scale() > 0 || a.compareTo(BigDecimal.ONE) < 0 || b.compareTo(BigDecimal.ONE) < 0){
			throw new KeiMathException(2, "値は自然数を指定してください");
		}

		if (a.compareTo(b) < 0){	//a>=bにする(a<bのときaとbを入れ替える)
			BigDecimal z = a;	a = b;	b = z;
		}

		BigDecimal mod;
		while (b.compareTo(BigDecimal.ZERO) != 0){	//ユークリッドの互除法
			mod = a.remainder(b);
			a = b;	b = mod;
		}
		return a;
	}

	/**
	 * 最小公倍数を返す
	*/
	final public static BigDecimal lcm(BigDecimal a, BigDecimal b) throws KeiMathException{
		return a.multiply(b).divide(gcm(a, b));
	}

}