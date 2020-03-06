package keiPack.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 単純な分数計算用クラス
 * 内部データは既約分数として保持されます。
 * また負の符号は分子に保持されます。
 */
public class SimpleBDFraction {
	/**分子*/
	private BigDecimal numerator;

	/**分母(正の値)*/
	private BigDecimal dominator;


	/**コンストラクタでの例外用*/
	private final String ERRMSG_DOMIN_EQUAL_ZERO = "分母に0を指定することはできません";


	/**intをSimpleBDFractionに変換
	 * @param val SimpleBDFractionに変換する値
	 */
	public SimpleBDFraction(int val) {
		this.numerator = new BigDecimal(val);	this.dominator = BigDecimal.ONE;
	}

	/**longをSimpleBDFractionに変換
	 * @param val SimpleBDFractionに変換する値
	 */
	public SimpleBDFraction(long val) {
		this.numerator = new BigDecimal(val);	this.dominator = BigDecimal.ONE;
	}

	/**BigDecimalをSimpleBDFraction変換
	 * @param val SimpleBDFractionに変換する値
	 */
	public SimpleBDFraction(BigDecimal val) {
		val = val.stripTrailingZeros();
		if (val.scale() > 0){	//numが小数のとき	(注)0は来ないよ！
			BigDecimal nume = new BigDecimal(val.unscaledValue());
			BigDecimal domi = nume.divide(val);		//10の自然数乗

			//約分
			BigDecimal gcm = null;	//エラー避け
			try {
				if (nume.compareTo(BigDecimal.ZERO) > 0){	//分子が正の値のとき
					gcm = BDMath.gcm(nume, domi);
				}else {										//分子が負の値のとき
					gcm = BDMath.gcm(nume, domi);
				}
			} catch (KeiMathException e) {e.printStackTrace();}	//来ない
			nume = nume.divide(gcm);
			domi = domi.divide(gcm);


			this.numerator = nume;
			this.dominator = domi;

		}else {	//numが整数のとき
			this.numerator = val;	this.dominator = BigDecimal.ONE;
		}

	}

	/**int型で分母(0以外)と分子を入力して初期化
	 * @param numerator 分子
	 * @param dominator 分母
	 * @exception KeiMathException dominator==0のとき
	 */
	public SimpleBDFraction(int numerator, int dominator) throws KeiMathException{
		if (dominator == 0) {
			throw new KeiMathException(2, ERRMSG_DOMIN_EQUAL_ZERO);
		}

		if (dominator == 0){
			this.numerator = BigDecimal.ZERO;
			this.dominator = BigDecimal.ONE;
		}else {
			if (dominator < 0){
				numerator = numerator * -1;
				dominator = dominator * -1;
			}
			int gcm;
			if (numerator > 0){	//分子が正の値のとき
				gcm = (int) gcm(numerator, dominator);
			}else {
				gcm = (int) gcm(numerator * -1, dominator);
			}
			this.numerator = new BigDecimal(numerator / gcm);
			this.dominator = new BigDecimal(dominator / gcm);

		}

	}

	/**long型で分母(0以外)と分子を入力して初期化
	 * @param numerator 分子
	 * @param dominator 分母
	 * @exception KeiMathException dominator==0のとき
	 */
	public SimpleBDFraction(long numerator, long dominator) throws KeiMathException{
		if (dominator == 0){
			throw new KeiMathException(2, ERRMSG_DOMIN_EQUAL_ZERO);
		}

		if (dominator == 0){
			this.numerator = BigDecimal.ZERO;
			this.dominator = BigDecimal.ONE;
		}else {
			if (dominator < 0){
				numerator = numerator * -1;
				dominator = dominator * -1;
			}
			long gcm;
			if (numerator > 0){	//分子が正の値のとき
				gcm = gcm(numerator, dominator);
			}else {
				gcm = gcm(numerator * -1, dominator);
			}
			this.numerator = new BigDecimal(numerator / gcm);
			this.dominator = new BigDecimal(dominator / gcm);

		}
	}

	/**BigDecimal型で分母(0以外の整数)と分子(整数)を入力して初期化
	 * @param numerator 分子
	 * @param dominator 分母
	 * @exception KeiMathException numerator及びdominatorが整数でないとき、dominatorが0のとき
	 */
	public SimpleBDFraction(BigDecimal numerator, BigDecimal dominator) throws KeiMathException {
		//例外のスロー
		numerator = numerator.stripTrailingZeros();
		dominator = dominator.stripTrailingZeros();
		if (numerator.scale() > 0 || dominator.scale() > 0){
			throw new KeiMathException(2, "分母及び分数は整数を指定してください");
		}else if (dominator.compareTo(BigDecimal.ZERO) == 0){
			throw new KeiMathException(2, ERRMSG_DOMIN_EQUAL_ZERO);
		}
		/*ここまで例外スローの処理*/

		if (numerator.compareTo(BigDecimal.ZERO) == 0){	//分子が0のとき
			this.numerator = BigDecimal.ZERO;
			this.dominator = BigDecimal.ONE;
		}else {
			if (dominator.compareTo(BigDecimal.ZERO) <0){	//分母が負の場合、正にする
				numerator = numerator.multiply(BDMath.MINUS);
				dominator = dominator.multiply(BDMath.MINUS);
			}

			//約分
			BigDecimal gcm;
			if (numerator.compareTo(BigDecimal.ZERO) > 0){	//分子が正の値のとき
				gcm = BDMath.gcm(numerator, dominator);
			}else {	//分子が負の値のとき
				gcm = BDMath.gcm(numerator.multiply(BDMath.MINUS), dominator);
			}
			this.numerator = numerator.divide(gcm);
			this.dominator = dominator.divide(gcm);
		}

	}



	/**足す
	 * @param addend 足す数
	 */
	public final SimpleBDFraction add(SimpleBDFraction addend) {
		BigDecimal ansNume;	//答えの分子
		BigDecimal ansDomin;	//答えの分母
		ansDomin = this.dominator.multiply(addend.dominator);
		ansNume = (this.numerator.multiply(addend.dominator)).add(addend.numerator.multiply(this.dominator));
		SimpleBDFraction result = null;	//エラー回避用にnullで初期化
		try {
			result = new SimpleBDFraction(ansNume, ansDomin);
		} catch (KeiMathException e) {	e.printStackTrace();}	//来ない
		return result;
	}

	/**引く
	 * @param subtrahend 引く数
	 */
	public final SimpleBDFraction subtract(SimpleBDFraction subtrahend) {
		BigDecimal ansNume;	//答えの分子
		BigDecimal ansDomin;	//答えの分母
		ansDomin = this.dominator.multiply(subtrahend.dominator);
		ansNume = (this.numerator.multiply(subtrahend.dominator)).subtract(subtrahend.numerator.multiply(this.dominator));
		SimpleBDFraction result = null;	//エラー回避用にnullで初期化
		try {
			result = new SimpleBDFraction(ansNume, ansDomin);
		} catch (KeiMathException e) {	e.printStackTrace();}	//来ない
		return result;
	}

	/**掛ける
	 * @param multiplicand 掛ける数
	 */
	public final SimpleBDFraction multiply(SimpleBDFraction multiplicand){
		SimpleBDFraction result = null;	//エラー回避用にnullで初期化
		try {
			result = new SimpleBDFraction(this.numerator.multiply(multiplicand.numerator), this.dominator.multiply(multiplicand.dominator));
		} catch (KeiMathException e) {	e.printStackTrace();}	//来ない
		return result;
	}

	/**割る
	 * @param divisor 割る数
	 */
	public final SimpleBDFraction divide(SimpleBDFraction divisor) {
		SimpleBDFraction result = null;	//エラー回避用にnullで初期化
		try {
			result = new SimpleBDFraction(this.numerator.multiply(divisor.dominator), this.dominator.multiply(divisor.numerator));
		} catch (KeiMathException e) {	e.printStackTrace();}	//来ない
		return result;
	}

	/**値の比較。valより小さい場合-1, valと同値の場合0, valより大きい場合1*/
	public final int compareTo(SimpleBDFraction val) {
		return (this.numerator.divide(dominator, 50, BigDecimal.ROUND_HALF_UP)).compareTo(val.numerator.divide(val.dominator, 50, BigDecimal.ROUND_HALF_UP));
	}

	public final boolean equals(SimpleBDFraction val) {
		return (this.numerator.compareTo(val.numerator) == 0 && this.dominator.compareTo(val.dominator) == 0);
	}

	/**正負の符号を入れ替えた数を返す*/
	public final SimpleBDFraction minus(){
		SimpleBDFraction result = null;
		try {
			result = new SimpleBDFraction(this.numerator.multiply(BDMath.MINUS), this.dominator);
		} catch (KeiMathException e) {	e.printStackTrace();}	//来ない
		return result;
	}

	/**"[分子]/[分母]"または"[整数]"の形の文字列を返します*/
	public String toString(){
		if (dominator.compareTo(BigDecimal.ONE) == 0){
			return numerator.toString();
		}else {
			return numerator.toString() + "/" + dominator.toString();
		}
	}

	/**
	 * 分子の値を返します
	*/
	public final BigDecimal getNumerator() {
		return this.numerator;
	}

	/**
	 * 分母の値を返します
	*/
	public final BigDecimal getDominator() {
		return this.dominator;
	}

	/**
	 * 分数の値を整数または小数(丸めは四捨五入)で返します
	*/
	public final BigDecimal toBigDecimal() {
		return this.numerator.divide(dominator, RoundingMode.HALF_UP);
	}


	/**最大公約数を返す。a, bが自然数でないときの例外処理はないので注意*/
	private static long gcm(long a, long b) {
		if (a < b){
			long c = a;		a = b;	b = c;
		}

		long mod;
		while (b != 0) {
			mod = a % b;	a = b;	b = mod;
		}
		return a;
	}

}
