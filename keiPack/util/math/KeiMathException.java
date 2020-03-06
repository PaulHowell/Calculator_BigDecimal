package keiPack.util.math;

/**
 * KeisBDMathクラス用に作られた例外クラスです。
 * @author Keita
 */
public class KeiMathException extends Exception {
	private static final long serialVersionUID = 1L;
	private int code;
	public KeiMathException(int code, String message){
		super(message);
		this.code = code;
	}

	public int getCode() {
        return code;
    }

}
