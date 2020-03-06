package keiPack.util.math;

import java.math.BigDecimal;

/**
 * 式どおりの四則演算に関する型、メソッドを集めたものです。<br>
 * 数はBigDecimal型を使います。<br>
 * 用途は、文字列で書かれた式を読み取って計算したり電卓を作成したりするなど。<br>
 * 3＋6×5＝33のように、×、÷を先に計算することができます。<br>
 * <br>
 * <br>
 * 3＋6＋9×5の場合、<br>
 * <br>
 * FourArithOperation calc = new FourArithOperation();<br>
 * calc.setNum(new BigDecimal(3));<br>
 * calc.add();<br>
 * calc.setNum(new BigDecimal(6));<br>
 * calc.add();<br>
 * calc.setNum(new BigDecimal(9));<br>
 * calc.multiply();<br>
 * calc.setNum(new BigDecimal(5));<br>
 * BigDecimal ans = calc.getAnswer();	//ans は 54<br>
 * <br>
 * といった感じです。<br>
 * <br>
 * <br>
 * numbersは数たち、<br>
 * operatorは四則演算の演算子、<br>
 * inputToは計算をするときに代入先であるべき変数を表しています。<br>
 * そのほかの変数はあまり気にしないでください。<br>
 * @author Keita
 *
 */
public class FourArithOperation {

	/**
	 * Inp型の定数は、四則演算の時に渡す3つの数（説明のためnum[]とする）のうちどれに入力すべきかを表します。<br>
	 * num[0]は操作しないことを推奨。
	 */
	public enum Inp{
		/**
		 * num[1]に入力すべきであることを示します。
		 */
		NUM1,
		/**
		 * num[2]に入力すべきであることを示します。
		 */
		NUM2
	}

	/**
	 * Operator型の定数は四則演算の記号を表します。
	 */
	public enum Operator{
		/**
		 * 演算子なしを示す。
		 */
		NON,

		/**
		 * 演算子が＋であることを示す。
		 */
		ADD,

		/**
		 * 演算子が－であることを示す。
		 */
		SUBTRACT,

		/**
		 * 演算子が×であることを示す。
		 */
		MULTIPLY,

		/**
		 * 演算子が÷であることを示す。
		 */
		DIVIDE
	}

	private BigDecimal[] numbers = new BigDecimal[3];
	private Operator operator;
	private Operator operatorBefore;
	private boolean num1Minus;
	private Inp inputTo;

	/**
	 * インスタンスを初期値で作成します。
	 */
	public FourArithOperation(){
		final BigDecimal[] FIRST = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
		this.numbers = FIRST;
		this.operator = Operator.NON;
		this.operatorBefore = Operator.NON;
		this.num1Minus = false;
		this.inputTo = Inp.NUM1;
	}

	/**
	 * inputToで示されている入力先の変数に指定した数値を入力するものです。
	 * @param number 入力したい数値です。
	 */
	public void inputNum(BigDecimal number){
		switch(this.inputTo){
		case NUM1:
			if (num1Minus){
				number = number.multiply(BDMath.MINUS);
			}
			this.numbers[1] = number;
			break;
		case NUM2:
			this.numbers[2] = number;
			break;
		}
	}

	/**
	 * inputToで示されている入力先の変数の値を返します。
	 * @return BigDecimal型の数値です
	 */
	public BigDecimal getNum(){
		BigDecimal numToReturn;
		switch(this.inputTo){
		case NUM1:
			if (!num1Minus){
				numToReturn = this.numbers[1];
			}else {
				numToReturn = this.numbers[1].multiply(BDMath.MINUS);
			}
			break;
		default:	//case NUM2:
			numToReturn = this.numbers[2];
			break;
		}
		return numToReturn;
	}

	/**
	 * numbers(メインの数3つ)の配列を返します。
	 */
	public BigDecimal[] getNums(){
		return numbers;
	}

	/**
	 * 現在の演算子を返します。
	 */
 	public Operator getNowOperator(){
		return operator;
	}

	/**
	 * ひとつ前の段階の演算子を返します。
	 */
	public Operator getBeforeOperator(){
		return operatorBefore;
	}

	public boolean getNum1Minus(){
		return num1Minus;
	}

	/**
	 * inputTo(数3つのうちどこに入力すべきか)を返します。
	 */
	public Inp getInputTo(){
		return inputTo;
	}

	/**
	 * 全てのインスタンスを初期値に戻します。
	 * 主に電卓のACボタン用です。
	 */
	public void resetAll(){
		final BigDecimal[] DEFAULT_NUMBERS = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
		this.numbers = DEFAULT_NUMBERS;
		this.operator = Operator.NON;
		this.operatorBefore = Operator.NON;
		this.num1Minus = false;
		this.inputTo = Inp.NUM1;
	}

	/**
	 * inputToで指定された入力先の値をリセットします。
	 * 主に電卓のCボタン用です。
	 * setNumberInputTo(BigDecimal.ZERO)とすることは同じです。
	 */
	public void clear(){
		switch(this.inputTo){
		case NUM1:
			this.numbers[1] = BigDecimal.ZERO;
			break;
		case NUM2:
			this.numbers[2] = BigDecimal.ZERO;
			break;
		}
	}

	/**
	 * '+'入力用メソッドです。
	 * @throws KeiMathException getCode()でコードを取得できます。ここでは、0で割ろうとしたとき(code==1)に出ます。
	 */
	public void add() throws KeiMathException{
		switch (this.operator){
		case ADD:
			this.numbers[1] = this.numbers[1].add(this.numbers[2]);
			this.numbers[2] = BigDecimal.ZERO;
			//this.operator = Operator.ADD;
			this.inputTo = Inp.NUM2;
			break;
		case SUBTRACT:
			this.numbers[1] = this.numbers[1].subtract(this.numbers[2]);
			this.numbers[2] = BigDecimal.ZERO;
			this.operator = Operator.ADD;
			this.inputTo = Inp.NUM2;
			break;
		case MULTIPLY:
			if (this.operatorBefore == Operator.NON || this.operatorBefore == Operator.ADD){
				this.numbers[0] = this.numbers[0].add(this.numbers[1].multiply(this.numbers[2]));
			}else if (this.operatorBefore == Operator.SUBTRACT){
				this.numbers[0] = this.numbers[0].add(this.numbers[1].multiply(this.numbers[2]).multiply(BDMath.MINUS));
			}
			this.numbers[1] = BigDecimal.ZERO;
			this.numbers[2] = BigDecimal.ZERO;
			this.operator = Operator.NON;
			this.inputTo = Inp.NUM1;
			break;
		case DIVIDE:
			if (this.numbers[2].compareTo(BigDecimal.ZERO) == 0){
				throw new KeiMathException(1, "0で割ることはできません");
			}

			if (this.operatorBefore == Operator.NON || this.operatorBefore == Operator.ADD){
				this.numbers[0] = this.numbers[0].add(this.numbers[1].divide(this.numbers[2], 200, BigDecimal.ROUND_HALF_UP));
				this.numbers[0] = this.numbers[0].stripTrailingZeros();
				if (this.numbers[0].scale() < 0){
					this.numbers[0] = this.numbers[0].setScale(0);
				}
			}else if (this.operatorBefore == Operator.SUBTRACT){
				this.numbers[0] = this.numbers[0].add(this.numbers[1].divide(this.numbers[2], 200, BigDecimal.ROUND_HALF_UP).multiply(BDMath.MINUS));
				this.numbers[0] = this.numbers[0].stripTrailingZeros();
				if (this.numbers[0].scale() < 0){
					this.numbers[0] = this.numbers[0].setScale(0);
				}
			}
			this.numbers[1] = BigDecimal.ZERO;
			this.numbers[2] = BigDecimal.ZERO;
			this.operator = Operator.NON;
			this.inputTo = Inp.NUM1;
			break;
		default:		//case NON:
			this.operator = Operator.ADD;
			this.inputTo = Inp.NUM2;
			break;
		}
		this.num1Minus = false;
	}

	/**
	 * '-'入力用メソッドです。
	 * @throws KeiMathException getCode()でコードを取得できます。このメソッドでは、0で割ろうとしたとき(code==1)に出ます。
	 */
	public void subtract() throws KeiMathException{
		switch(this.operator){
		case ADD:
			this.numbers[1] = this.numbers[1].add(this.numbers[2]);
			this.numbers[2] = BigDecimal.ZERO;
			this.num1Minus = false;
			this.operator = Operator.SUBTRACT;
			this.inputTo = Inp.NUM2;
			break;
		case SUBTRACT:
			this.numbers[1] = this.numbers[1].subtract(this.numbers[2]);
			this.numbers[2] = BigDecimal.ZERO;
			this.num1Minus = false;
			//this.operator = Operator.SUBTRACT;
			this.inputTo = Inp.NUM2;
			break;
		case MULTIPLY:
			if (this.operatorBefore == Operator.NON || this.operatorBefore == Operator.ADD){
				this.numbers[0] = this.numbers[0].add(this.numbers[1].multiply(this.numbers[2]));
			}else if (this.operatorBefore == Operator.SUBTRACT){
				this.numbers[0] = this.numbers[0].add(this.numbers[1].multiply(this.numbers[2]).multiply(BDMath.MINUS));
			}
			this.numbers[1] = BigDecimal.ZERO;
			this.numbers[2] = BigDecimal.ZERO;
			this.num1Minus = true;
			this.operator = Operator.NON;
			this.inputTo = Inp.NUM1;
			break;
		case DIVIDE:
			if (this.numbers[2].compareTo(BigDecimal.ZERO) == 0){
				throw new KeiMathException(1, "0で割ることはできません");
			}

			if (this.operatorBefore == Operator.NON || this.operatorBefore == Operator.ADD){
				this.numbers[0] = this.numbers[0].add(this.numbers[1].divide(this.numbers[2], 200, BigDecimal.ROUND_HALF_UP));
				this.numbers[0] = this.numbers[0].stripTrailingZeros();
				if (this.numbers[0].scale() < 0){
					this.numbers[0] = this.numbers[0].setScale(0);
				}
			}else if (this.operatorBefore == Operator.SUBTRACT){
				this.numbers[0] = this.numbers[0].add(this.numbers[1].divide(this.numbers[2], 200, BigDecimal.ROUND_HALF_UP).multiply(BDMath.MINUS));
				this.numbers[0] = this.numbers[0].stripTrailingZeros();
				if (this.numbers[0].scale() < 0){
					this.numbers[0] = this.numbers[0].setScale(0);
				}
			}
			this.numbers[1] = BigDecimal.ZERO;
			this.numbers[2] = BigDecimal.ZERO;
			this.num1Minus = true;
			this.operator = Operator.NON;
			this.inputTo = Inp.NUM1;
			break;
		default:	//case NON:
			this.operator = Operator.SUBTRACT;
			this.inputTo = Inp.NUM2;
			break;
		}
	}

	/**
	 * '×'入力用メソッドです。
	 * @throws KeiMathException getCode()でコードを取得できます。このメソッドでは、0で割ろうとしたとき(code==1)に出ます。
	 */
	public void multiply() throws KeiMathException{
		switch (this.operator){
		case ADD:
			this.numbers[0] = this.numbers[0].add(this.numbers[1]);
			this.numbers[1] = this.numbers[2];
			this.numbers[2] = BigDecimal.ZERO;
			this.operatorBefore = this.operator;	//つまりは opeBef = Operator.ADD;
			break;
		case SUBTRACT:
			this.numbers[0] = this.numbers[0].add(this.numbers[1]);
			this.numbers[1] = this.numbers[2].multiply(BDMath.MINUS);
			this.numbers[2] = BigDecimal.ZERO;
			this.operatorBefore = this.operator;	//つまりは opeBef = Operator.SUBTRACT;
			break;
		case MULTIPLY:
			this.numbers[1] = this.numbers[1].multiply(this.numbers[2]);
			this.numbers[2] = BigDecimal.ZERO;
			this.operatorBefore = Operator.NON;
			break;
		case DIVIDE:
			if (this.numbers[2].compareTo(BigDecimal.ZERO) == 0){
				throw new KeiMathException(1, "0で割ることはできません");
			}

			this.numbers[1] = this.numbers[1].divide(this.numbers[2], 200, BigDecimal.ROUND_HALF_UP);
			this.numbers[1] = this.numbers[1].stripTrailingZeros();
			if (this.numbers[1].scale() < 0){
				this.numbers[1] = this.numbers[1].setScale(0);
			}
			this.numbers[2] = BigDecimal.ZERO;
			this.operatorBefore = Operator.NON;
			break;
		case NON:
			this.operatorBefore = Operator.NON;
			break;
		}
		this.operator = Operator.MULTIPLY;
		this.inputTo = Inp.NUM2;
		this.num1Minus = false;
	}

	/**
	 * '÷'入力用メソッドです。
	 * @throws KeiMathException getCode()でコードを取得できます。このメソッドでは、0で割ろうとしたとき(code==1)に出ます。
	 */
	public void divide() throws KeiMathException{
		switch (this.operator){
		case ADD:
			this.numbers[0] = this.numbers[0].add(this.numbers[1]);
			this.numbers[1] = this.numbers[2];
			this.numbers[2] = BigDecimal.ZERO;
			this.operatorBefore = this.operator;	//つまりは opeBef = Operator.ADD;
			break;
		case SUBTRACT:
			this.numbers[0] = this.numbers[0].add(this.numbers[1]);
			this.numbers[1] = this.numbers[2].multiply(BDMath.MINUS);
			this.numbers[2] = BigDecimal.ZERO;
			this.operatorBefore = this.operator;	//つまりは opeBef = Operator.SUBTRACT;
			break;
		case MULTIPLY:
			this.numbers[1] = this.numbers[1].multiply(this.numbers[2]);
			this.numbers[2] = BigDecimal.ZERO;
			this.operatorBefore = Operator.NON;
			break;
		case DIVIDE:
			if (this.numbers[2].compareTo(BigDecimal.ZERO) == 0){
				throw new KeiMathException(1, "0で割ることはできません");
			}

			this.numbers[1] = this.numbers[1].divide(this.numbers[2], 200, BigDecimal.ROUND_HALF_UP);
			this.numbers[1] = this.numbers[1].stripTrailingZeros();
			if (this.numbers[1].scale() < 0){
				this.numbers[1] = this.numbers[1].setScale(0);
			}
			this.numbers[2] = BigDecimal.ZERO;
			this.operatorBefore = Operator.NON;
			break;
		case NON:
			this.operatorBefore = Operator.NON;
			break;
		}
		this.operator = Operator.DIVIDE;
		this.inputTo = Inp.NUM2;
		this.num1Minus = false;
	}

	/**
	 * 答えを出すためのメソッドです。
	 * @return BigDecimal 答えの値を返します。
	 * @throws KeiMathException getCode()でコードを取得できます。このメソッドでは、0で割ろうとしたとき(code==1)に出ます。
	 */
	public BigDecimal getAnswer() throws KeiMathException{
		BigDecimal ans;
		switch (this.operator){
		case ADD:
			ans = this.numbers[0].add(this.numbers[1].add(this.numbers[2]));
			break;
		case SUBTRACT:
			ans = this.numbers[0].add(this.numbers[1].subtract(this.numbers[2]));
			break;
		case MULTIPLY:
			ans = this.numbers[0].add(this.numbers[1].multiply(this.numbers[2]));
			break;
		case DIVIDE:
			if (this.numbers[2].compareTo(BigDecimal.ZERO) == 0){
				throw new KeiMathException(1, "0で割ることはできません");
			}

			ans = this.numbers[0].add(this.numbers[1].divide(this.numbers[2], 200, BigDecimal.ROUND_HALF_UP));
			ans = ans.stripTrailingZeros();
			if (ans.scale() < 0){
				ans = ans.setScale(0);
			}
			break;
		default:	//case NON:
			ans = this.numbers[0].add(this.numbers[1]);
			break;
		}
		return ans;
	}

}
