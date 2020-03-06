package keiPack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import keiPack.util.math.BDMath;
import keiPack.util.math.FourArithOperation;
import keiPack.util.math.FourArithOperation.Operator;
import keiPack.util.math.KeiMathException;

/**
 * ある程度高機能（自称）な電卓です。
 * クソコードですがご了承ください。
 * @author Keita
 * @version 3.0
 */
@SuppressWarnings("serial")
final class Calculator extends JFrame implements ActionListener, WindowListener, KeyListener{

	private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

	public static void main(String args[]){
		Calculator cal = new Calculator("電卓");
		if (OS_NAME.startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");	//メニューバーをデスクトップの上に
		}
		cal.setVisible(true);
	}

	/**式の表示用文字列*/
	String formula = "";
	/**括弧内の式表示用文字列*/
	String inParenthesis = "";
	FourArithOperation main = new FourArithOperation();
	FourArithOperation memory = new FourArithOperation();
	/**答え*/
	BigDecimal finalAnswer;
	String valueStr = "  " + main.getNum();
	/**括弧内の計算中なのかどうか*/
	boolean parenthesisEnabled = false;
	/**答えが表示されている状態なのかどうか*/
	boolean isAnswerShown = false;
	/**どの位に入力するか。（digit==1の時は一の位に入力）*/
	BigDecimal digit = new BigDecimal(1);
	int digitNumber = 0;
	/**数字入力が有効か無効か*/
	boolean entNumEnabled = true;
	final int MAX_NUM_DECIMAL_PLACE = 150;

	JLabel labelShowingFormula = new JLabel("  " + formula);
	JLabel labelShowingValue = new JLabel(valueStr);
	JPopupMenu popupOnFormula = new JPopupMenu();
	JPopupMenu popupOnValue = new JPopupMenu();
	JButton btn_point = new JButton();	//.(小数点)
	JButton btn_Clear = new JButton();	//C/AC(setTextとsetActionCommandで切り替え)
	JButton btn_equal = new JButton();	//＝
	JButton btn_plus = new JButton();	//＋
	JButton btn_minus = new JButton();	//－
	JButton btn_times = new JButton();	//×
	JButton btn_divide = new JButton();	//÷
	JButton btn_sParen = new JButton();	//(
	JButton btn_cParen = new JButton();	//)
	JButton btn0 = new JButton();		//0
	JButton btn00 = new JButton();		//00
	JButton btn1 = new JButton();		//1
	JButton btn2 = new JButton();		//2
	JButton btn3 = new JButton();		//3
	JButton btn4 = new JButton();		//4
	JButton btn5 = new JButton();		//5
	JButton btn6 = new JButton();		//6
	JButton btn7 = new JButton();		//7
	JButton btn8 = new JButton();		//8
	JButton btn9 = new JButton();		//9
	JButton btn_delete = new JButton();	//←(一桁削除)
	JButton btn_pow = new JButton();		//累乗
	JButton btn_factorial = new JButton();	//階乗
	JButton btn_root = new JButton();	//平方根
	JButton btn_radicalRoot = new JButton();	//累乗根
	JButton btn_plusMinus = new JButton();	//+/-(符号入れ替え)
	JButton btn_circleRatio = new JButton();	//π
	JButton btn_constant = new JButton();	//ユーザー定数
	JButton btn_function = new JButton();	//ユーザー関数
	JButton btn_Napier = new JButton();	//ネイピア数

	Calculator(String title){
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 15);

		setTitle(title);
		setSize(570,470);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setFocusable(true);
		addKeyListener(this);
		URL url = this.getClass().getResource("data/img/icon.png");
		if (url != null){
			ImageIcon icon = new ImageIcon(url, "icon");
			setIconImage(icon.getImage());
		}

		//メニューの作成
		JMenuBar menuBar = new JMenuBar();
		JMenu menu_File = new JMenu("ファイル");
		JMenuItem menuEditConsts = new JMenuItem("ユーザー定数の編集");
		menuEditConsts.addActionListener(this);
		menuEditConsts.setActionCommand("editConstsMenu");
		menu_File.add(menuEditConsts);
		menuBar.add(menu_File);
		this.setJMenuBar(menuBar);

		//パネルの作成
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(5, 6));

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));

		//ラベルの設定（作成は上（メソッド外））
		labelShowingFormula.setAlignmentX(Component.LEFT_ALIGNMENT);	//ラベルの位置を左に設定
		labelShowingFormula.setHorizontalAlignment(JLabel.LEFT);		//ラベル内の文字列の位置を左に設定
		labelShowingFormula.addMouseListener(new myMouseListener());
		labelShowingFormula.setFont(new Font(Font.SERIF, Font.PLAIN, 20));	//フォントを設定
		labelShowingFormula.setOpaque(true);		//ラベルの背景を非透明に設定
		labelShowingFormula.setBackground(new Color(200, 200, 200));	//ラベルの背景色を設定

		labelShowingValue.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelShowingValue.setHorizontalAlignment(JLabel.RIGHT);
		labelShowingValue.addMouseListener(new myMouseListener());
		labelShowingValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		labelShowingValue.setOpaque(true);
		labelShowingValue.setBackground(new Color(200, 200, 200));

		//ポップアップメニューの設定

		JMenuItem copyFormulaMenuItem = new JMenuItem("式のコピー");
		copyFormulaMenuItem.addActionListener(this);
		copyFormulaMenuItem.setActionCommand("copyFormulaMenu");
		JMenuItem pasteMenuItemOnFormula = new JMenuItem("貼り付け");
		pasteMenuItemOnFormula.addActionListener(this);
		pasteMenuItemOnFormula.setActionCommand("pasteMenu");
		popupOnFormula.add(copyFormulaMenuItem);
		popupOnFormula.addSeparator();
		popupOnFormula.add(pasteMenuItemOnFormula);

		JMenuItem copyValueMenuItem = new JMenuItem("数値のコピー");
		copyValueMenuItem.addActionListener(this);
		copyValueMenuItem.setActionCommand("copyValueMenu");
		JMenuItem pasteMenuItemOnValue = new JMenuItem("貼り付け");
		pasteMenuItemOnValue.addActionListener(this);
		pasteMenuItemOnValue.setActionCommand("pasteMenu");
		popupOnValue.add(copyValueMenuItem);
		popupOnValue.addSeparator();
		popupOnValue.add(pasteMenuItemOnValue);


		//ボタンの作成と設定

		btn0.setText("0");
		btn0.addActionListener(e -> {
			enterNumber(new BigDecimal(0));
			afterEvent();
		});
		btn0.setFont(font);
		btn0.setOpaque(true);

		btn00.setText("00");
		btn00.addActionListener(e -> {
			enterNumber(new BigDecimal(0));
			enterNumber(new BigDecimal(0));
			afterEvent();
		});
		btn00.setFont(font);
		btn00.setOpaque(true);

		btn1.setText("1");
		btn1.addActionListener(e -> {
			enterNumber(new BigDecimal(1));
			afterEvent();
		});
		btn1.setFont(font);
		btn1.setOpaque(true);

		btn2.setText("2");
		btn2.addActionListener(e -> {
			enterNumber(new BigDecimal(2));
			afterEvent();
		});
		btn2.setFont(font);
		btn2.setOpaque(true);

		btn3.setText("3");
		btn3.addActionListener(e -> {
			enterNumber(new BigDecimal(3));
			afterEvent();
		});
		btn3.setFont(font);
		btn3.setOpaque(true);

		btn4.setText("4");
		btn4.addActionListener(e -> {
			enterNumber(new BigDecimal(4));
			afterEvent();
		});
		btn4.setFont(font);
		btn4.setOpaque(true);

		btn5.setText("5");
		btn5.addActionListener(e -> {
			enterNumber(new BigDecimal(5));
			afterEvent();
		});
		btn5.setFont(font);
		btn5.setOpaque(true);

		btn6.setText("6");
		btn6.addActionListener(e -> {
			enterNumber(new BigDecimal(6));
			afterEvent();
		});
		btn6.setFont(font);
		btn6.setOpaque(true);

		btn7.setText("7");
		btn7.addActionListener(e -> {
			enterNumber(new BigDecimal(7));
			afterEvent();
		});
		btn7.setFont(font);
		btn7.setOpaque(true);

		btn8.setText("8");
		btn8.addActionListener(e -> {
			enterNumber(new BigDecimal(8));
			afterEvent();
		});
		btn8.setFont(font);
		btn8.setOpaque(true);

		btn9.setText("9");
		btn9.addActionListener(e -> {
			enterNumber(new BigDecimal(9));
			afterEvent();
		});
		btn9.setFont(font);
		btn9.setOpaque(true);

		btn_Clear.setText("AC");
		btn_Clear.addActionListener(this);
		btn_Clear.setActionCommand("Button_AClear");
		btn_Clear.setFont(font);
		btn_Clear.setOpaque(true);

		btn_point.setText(".");
		btn_point.addActionListener(this);
		btn_point.setActionCommand("Button_point");
		btn_point.setFont(font);
		btn_point.setOpaque(true);

		btn_plus.setText("＋");
		btn_plus.addActionListener(this);
		btn_plus.setActionCommand("Button_plus");
		btn_plus.setFont(font);
		btn_plus.setOpaque(true);

		btn_minus.setText("－");
		btn_minus.addActionListener(this);
		btn_minus.setActionCommand("Button_minus");
		btn_minus.setFont(font);
		btn_minus.setOpaque(true);

		btn_times.setText("×");
		btn_times.addActionListener(this);
		btn_times.setActionCommand("Button_times");
		btn_times.setFont(font);
		btn_times.setOpaque(true);

		btn_divide.setText("÷");
		btn_divide.addActionListener(this);
		btn_divide.setActionCommand("Button_divide");
		btn_divide.setFont(font);
		btn_divide.setOpaque(true);

		btn_equal.setText("＝");
		btn_equal.addActionListener(this);
		btn_equal.setActionCommand("Button_equal");
		btn_equal.setFont(font);
		btn_equal.setOpaque(true);

		btn_plusMinus.setText("+/-");
		btn_plusMinus.addActionListener(this);
		btn_plusMinus.setActionCommand("Button_plusMinus");
		btn_plusMinus.setFont(font);
		btn_plusMinus.setOpaque(true);

		btn_delete.setText("←");
		btn_delete.addActionListener(e -> delete());
		btn_delete.setFont(font);
		btn_delete.setOpaque(true);

		btn_pow.setText("n^m");
		btn_pow.addActionListener(this);
		btn_pow.setActionCommand("Button_pow");
		btn_pow.setFont(font);
		btn_pow.setOpaque(true);

		btn_circleRatio.setText("π");
		btn_circleRatio.addActionListener(e -> {
			//piは円周率を小数第(MAX_NUM_DICIMAL_PLA)位まで入力
			BigDecimal pie = BDMath.PIE.setScale(MAX_NUM_DECIMAL_PLACE, BigDecimal.ROUND_HALF_UP);
			enterConstant(pie, "π");
			afterEvent();
		});
		btn_circleRatio.setFont(font);
		btn_circleRatio.setOpaque(true);

		btn_Napier.setText("e");
		btn_Napier.addActionListener(e -> {
			//napierはネイピア数を小数第(MAX_NUM_DICIMAL_PLA)位まで入力
			BigDecimal napier = BDMath.E.setScale(MAX_NUM_DECIMAL_PLACE, BigDecimal.ROUND_HALF_UP);
			enterConstant(napier, "e");
			afterEvent();
		});
		btn_Napier.setFont(font);
		btn_Napier.setOpaque(true);

		btn_factorial.setText("n!");
		btn_factorial.addActionListener(this);
		btn_factorial.setActionCommand("Button_factorial");
		btn_factorial.setFont(font);
		btn_factorial.setOpaque(true);

		btn_root.setText("√");
		btn_root.addActionListener(this);
		btn_root.setActionCommand("Button_root");
		btn_root.setFont(font);
		btn_root.setOpaque(true);

		btn_radicalRoot.setText("m√n");
		btn_radicalRoot.addActionListener(this);
		btn_radicalRoot.setActionCommand("Button_radicalRoot");
		btn_radicalRoot.setFont(font);
		btn_radicalRoot.setOpaque(true);

		btn_sParen.setText("(");
		btn_sParen.addActionListener(e -> startParenthesis());
		btn_sParen.setFont(font);
		btn_sParen.setOpaque(true);

		btn_cParen.setText(")");
		btn_cParen.addActionListener(e -> {
			if (parenthesisEnabled){
				try {
					closeParenthesis();
					afterEvent();
				} catch (KeiMathException excep) {
					JOptionPane.showMessageDialog(this, excep.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btn_cParen.setFont(font);
		btn_cParen.setEnabled(false);
		btn_cParen.setOpaque(true);

		btn_constant.setText("Const.");
		btn_constant.addActionListener(this);
		btn_constant.setActionCommand("constant");
		btn_constant.setFont(font);
		btn_constant.setBackground(new Color(255,215, 0));
		btn_constant.setOpaque(true);
		btn_constant.setBorderPainted(false);

		btn_function.setText("Func.");
		btn_function.addActionListener(this);
		btn_function.setActionCommand("function");
		btn_function.setFont(font);
		btn_function.setBackground(new Color(192, 192, 192));
		btn_function.setOpaque(true);
		btn_function.setBorderPainted(false);

		//パネルp1への配置
		p1.add(btn_sParen);
		p1.add(btn_cParen);
		p1.add(btn_delete);
		p1.add(btn_Clear);
		p1.add(btn_plusMinus);
		p1.add(btn_divide);
		p1.add(btn_circleRatio);
		p1.add(btn_root);
		p1.add(btn7);
		p1.add(btn8);
		p1.add(btn9);
		p1.add(btn_times);
		p1.add(btn_Napier);
		p1.add(btn_factorial);
		p1.add(btn4);
		p1.add(btn5);
		p1.add(btn6);
		p1.add(btn_minus);
		p1.add(btn_constant);
		p1.add(btn_pow);
		p1.add(btn1);
		p1.add(btn2);
		p1.add(btn3);
		p1.add(btn_plus);
		p1.add(btn_function);
		p1.add(btn_radicalRoot);
		p1.add(btn0);
		p1.add(btn00);
		p1.add(btn_point);
		p1.add(btn_equal);

		//パネルp2への配置
		p2.add(labelShowingFormula);
		p2.add(labelShowingValue);

		//パネルの配置
		getContentPane().add(p1, BorderLayout.CENTER);
		getContentPane().add(p2, BorderLayout.NORTH);

	}

	//ActionEventの処理
	@Override
	final public void actionPerformed(ActionEvent e){
		String cmd = e.getActionCommand();
		if (cmd.equals("Button_point")){				//.
			if (digitNumber == 0){
				final BigDecimal pOne = new BigDecimal("0.1");
				digit = pOne;
				digitNumber = 1;
				if (parenthesisEnabled){
					if (inParenthesis.endsWith(" ") || inParenthesis.endsWith("(")){
						inParenthesis = inParenthesis.concat("0");
					}
					inParenthesis = inParenthesis.concat(".");
				}else {
					if (formula.endsWith(" ") || formula.equals("")){
						formula = formula.concat("0");
					}
					formula = formula.concat(".");
				}
			}
		}else if (cmd.equals("Button_plus")){			//＋
			if (parenthesisEnabled){
				switch(memory.getNowOperator()){
				case NON:
					if (inParenthesis.endsWith(" ") || inParenthesis.endsWith("(")){
						inParenthesis = inParenthesis.concat("0");
					}
					break;
				case ADD:
					if (inParenthesis.endsWith(" ")){
						inParenthesis = inParenthesis.concat("0");
					}
					break;
				case SUBTRACT:
					if (inParenthesis.endsWith(" ")){
						inParenthesis = inParenthesis.concat("0");
					}
					break;
				case MULTIPLY:
					if (inParenthesis.endsWith(" ")){
						btn1.doClick();
					}
					break;
				case DIVIDE:
					if (inParenthesis.endsWith(" ")){
						btn1.doClick();
					}
					break;
				}
				try {
					memory.add();
					entNumEnabled = true;
					digit = BigDecimal.ONE;
					digitNumber = 0;
					inParenthesis = inParenthesis + " ＋ ";
				} catch (KeiMathException excep) {
					JOptionPane.showMessageDialog(this, excep.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
				}
			}else {	//!parenthesisEnabledのとき
				if (isAnswerShown){
					main.resetAll();
					digit = BigDecimal.ONE;
					digitNumber = 0;
					main.inputNum(finalAnswer);
					finalAnswer = null;
					isAnswerShown = false;
					formula = "(" + formula + ")";
				}
				switch(main.getNowOperator()){
				case NON:
					if (formula.endsWith(" ") || formula.equals("")){
						formula = formula.concat("0");
					}
					break;
				case ADD:
					if (formula.endsWith(" ")){
						formula = formula.concat("0");
					}
					break;
				case SUBTRACT:
					if (formula.endsWith(" ")){
						formula = formula.concat("0");
					}
					break;
				case MULTIPLY:
					if (formula.endsWith(" ")){
						btn1.doClick();
					}
					break;
				case DIVIDE:
					if (formula.endsWith(" ")){
						btn1.doClick();
					}
					break;
				}
				try {
					main.add();
					entNumEnabled = true;
					digit = BigDecimal.ONE;
					digitNumber = 0;
					formula = formula + " ＋ ";
				} catch (KeiMathException excep) {
					JOptionPane.showMessageDialog(this, excep.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if (cmd.equals("Button_minus")){			//－
			if (parenthesisEnabled){
				switch (memory.getNowOperator()){
				case NON:
					if (inParenthesis.endsWith(" ") || inParenthesis.endsWith("(")){
						inParenthesis = inParenthesis.concat("0");
					}
					break;
				case ADD:
					if (inParenthesis.endsWith(" ")){
						inParenthesis = inParenthesis.concat("0");
					}
					break;
				case SUBTRACT:
					if (inParenthesis.endsWith(" ")){
						inParenthesis = inParenthesis.concat("0");
					}
					break;
				case MULTIPLY:
					if (inParenthesis.endsWith(" ")){
						btn1.doClick();
					}
					break;
				case DIVIDE:
					if (inParenthesis.endsWith(" ")){
						btn1.doClick();
					}
					break;
				}
				try {
					memory.subtract();
					entNumEnabled = true;
					digit = BigDecimal.ONE;
					digitNumber = 0;
					inParenthesis = inParenthesis + " － ";
				} catch (KeiMathException excep) {
					JOptionPane.showMessageDialog(this, excep.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
				}
			}else {	//!parenthesisEnabledのとき
				if (isAnswerShown){
					main.resetAll();
					digit = BigDecimal.ONE;
					digitNumber = 0;
					main.inputNum(finalAnswer);
					finalAnswer = null;
					isAnswerShown = false;
					formula = "(" + formula + ")";
				}
				switch (main.getNowOperator()){
				case NON:
					if (formula.endsWith(" ") || formula.equals("")){
						formula = formula.concat("0");
					}
					break;
				case ADD:
					if (formula.endsWith(" ")){
						formula = formula.concat("0");
					}
					break;
				case SUBTRACT:
					if (formula.endsWith(" ")){
						formula = formula.concat("0");
					}
					break;
				case MULTIPLY:
					if (formula.endsWith(" ")){
						btn1.doClick();
					}
					break;
				case DIVIDE:
					if (formula.endsWith(" ")){
						btn1.doClick();
					}
					break;
				}
				try {
					main.subtract();
					entNumEnabled = true;
					digit = BigDecimal.ONE;
					digitNumber = 0;
					formula = formula + " － ";
				} catch (KeiMathException excep) {
					JOptionPane.showMessageDialog(this, excep.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if (cmd.equals("Button_times")){			//×
			if (parenthesisEnabled){
				switch (memory.getNowOperator()){
				case NON:
					if (inParenthesis.endsWith(" ") || inParenthesis.endsWith("(")){
						btn1.doClick();
					}
					break;
				case ADD:
					if (inParenthesis.endsWith(" ")){
						inParenthesis = inParenthesis.concat("0");
					}
					break;
				case SUBTRACT:
					if (inParenthesis.endsWith(" ")){
						inParenthesis = inParenthesis.concat("0");
					}
					break;
				case MULTIPLY:
					if (inParenthesis.endsWith(" ")){
						btn1.doClick();
					}
					break;
				case DIVIDE:
					if (inParenthesis.endsWith(" ")){
						btn1.doClick();
					}
					break;
				}
				try {
					memory.multiply();
					entNumEnabled = true;
					digit = BigDecimal.ONE;
					digitNumber = 0;
					inParenthesis = inParenthesis + " × ";
				} catch (KeiMathException excep) {
					JOptionPane.showMessageDialog(this, excep.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
				}
			}else {	//!parenthesisEnabledのとき
				if (isAnswerShown){
					main.resetAll();
					digit = BigDecimal.ONE;
					digitNumber = 0;
					main.inputNum(finalAnswer);
					finalAnswer = null;
					isAnswerShown = false;
					formula = "(" + formula + ")";
				}
				switch (main.getNowOperator()){
				case NON:
					if (formula.endsWith(" ") || formula.equals("")){
						btn1.doClick();
					}
					break;
				case ADD:
					if (formula.endsWith(" ")){
						formula = formula.concat("0");
					}
					break;
				case SUBTRACT:
					if (formula.endsWith(" ")){
						formula = formula.concat("0");
					}
					break;
				case MULTIPLY:
					if (formula.endsWith(" ")){
						btn1.doClick();
					}
					break;
				case DIVIDE:
					if (formula.endsWith(" ")){
						btn1.doClick();
					}
					break;
				}
				try {
					main.multiply();
					entNumEnabled = true;
					digit = BigDecimal.ONE;
					digitNumber = 0;
					formula = formula + " × ";
				} catch (KeiMathException excep) {
					JOptionPane.showMessageDialog(this, excep.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if (cmd.equals("Button_divide")){			//÷
			if (parenthesisEnabled){
				switch(memory.getNowOperator()){
				case NON:
					if (inParenthesis.endsWith(" ") || inParenthesis.endsWith("(")){
						btn1.doClick();
					}
					break;
				case ADD:
					if (inParenthesis.endsWith(" ")){
						inParenthesis = inParenthesis.concat("0");
					}
					break;
				case SUBTRACT:
					if (inParenthesis.endsWith(" ")){
						inParenthesis = inParenthesis.concat("0");
					}
					break;
				case MULTIPLY:
					if (inParenthesis.endsWith(" ")){
						btn1.doClick();
					}
					break;
				case DIVIDE:
					if (inParenthesis.endsWith(" ")){
						btn1.doClick();
					}
					break;
				}
				try {
					memory.divide();
					entNumEnabled = true;
					digit = BigDecimal.ONE;
					digitNumber = 0;
					inParenthesis = inParenthesis + " ÷ ";
				} catch (KeiMathException excep) {
					JOptionPane.showMessageDialog(this, excep.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
				}
			}else {	//!parenthesisEnabledのとき
				if (isAnswerShown){
					main.resetAll();
					digit = BigDecimal.ONE;
					digitNumber = 0;
					main.inputNum(finalAnswer);
					finalAnswer = null;
					isAnswerShown = false;
					formula = "(" + formula + ")";
				}
				switch(main.getNowOperator()){
				case NON:
					if (formula.endsWith(" ") || formula.equals("")){
						btn1.doClick();
					}
					break;
				case ADD:
					if (formula.endsWith(" ")){
						formula = formula.concat("0");
					}
					break;
				case SUBTRACT:
					if (formula.endsWith(" ")){
						formula = formula.concat("0");
					}
					break;
				case MULTIPLY:
					if (formula.endsWith(" ")){
						btn1.doClick();
					}
					break;
				case DIVIDE:
					if (formula.endsWith(" ")){
						btn1.doClick();
					}
					break;
				}
				try {
					main.divide();
					entNumEnabled = true;
					digit = BigDecimal.ONE;
					digitNumber = 0;
					formula = formula + " ÷ ";
				} catch (KeiMathException excep) {
					JOptionPane.showMessageDialog(this, excep.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if (cmd.equals("Button_equal")){			//＝
			switch(main.getNowOperator()){
			case NON:
				if (formula.endsWith(" ") || formula.equals("")){
					formula = formula.concat("0");
				}
				break;
			case ADD:
				if (formula.endsWith(" ")){
					formula = formula.concat("0");
				}
				break;
			case SUBTRACT:
				if (formula.endsWith(" ")){
					formula = formula.concat("0");
				}
				break;
			case MULTIPLY:
				if (formula.endsWith(" ")){
					btn1.doClick();
				}
				break;
			case DIVIDE:
				if (formula.endsWith(" ")){
					btn1.doClick();
				}
				break;
			}
			try {
				finalAnswer = main.getAnswer();
				isAnswerShown = true;
			} catch (KeiMathException excep) {
				JOptionPane.showMessageDialog(this, "0で割ることはできません", "エラー", JOptionPane.ERROR_MESSAGE);
			}
		}else if (cmd.equals("Button_AClear")){		//AC
			entNumEnabled = true;
			main.resetAll();
			digit = BigDecimal.ONE;
			digitNumber = 0;
			formula = "";
			finalAnswer = null;
			isAnswerShown = false;
			System.gc();
		}else if (cmd.equals("Button_Clear")){		//C
			entNumEnabled = true;
			if (parenthesisEnabled){
				memory.resetAll();
				parenthesisEnabled = false;
				inParenthesis = "";
				btn_equal.setEnabled(true);
				btn_sParen.setEnabled(true);
				btn_cParen.setEnabled(false);
			}
			main.clear();
			digit = BigDecimal.ONE;
			digitNumber = 0;

			boolean isSpaceFound = false;
			while (!isSpaceFound){
				int p = formula.lastIndexOf(' ');
				int q = formula.lastIndexOf(')');
				int r = Math.max(p, q);
				if (r == -1){
					formula = "";
					break;
				}else if (r == p){
					formula = formula.substring(0, r + 1);
					isSpaceFound = true;
				}else {
					int numOfParen = 1;
					int i = r - 1;
					while (numOfParen != 0){
						char ch = formula.charAt(i);
						if (ch == ')'){
							numOfParen++;
						}else if (ch == '('){
							numOfParen--;
						}
						i--;
					}
					i++;
					formula = formula.substring(0, i + 1);
				}
			}
			formula = formula + main.getNum();

		}else if (cmd.equals("Button_plusMinus")){	//＋⇔－
			if (parenthesisEnabled){
				memory.inputNum(memory.getNum().multiply(BDMath.MINUS));

				if(inParenthesis.endsWith(" ") || inParenthesis.endsWith("(")){
					inParenthesis = inParenthesis.concat("0");
				}
				int p = Math.max(inParenthesis.lastIndexOf(' ') + 1, inParenthesis.lastIndexOf('(') + 1);
				String str1 = inParenthesis.substring(0, p);
				String str2 = inParenthesis.substring(p);
				if (str2.equals("0")){
					inParenthesis = str1.concat(str2);
				}else if (str2.startsWith("-")){
					inParenthesis = str1.concat(str2.substring(1));
				}else {
					inParenthesis = str1.concat("-").concat(str2);
				}
			}else {
				main.inputNum(main.getNum().multiply(BDMath.MINUS));

				if (formula.endsWith(" ") || formula.equals("")){
					formula = formula.concat("0");
				}

				boolean isSpaceFound = false;
				String str1 = "";
				String str2 = "";
				while (!isSpaceFound){
					int p = formula.lastIndexOf(' ');
					int q = formula.lastIndexOf(')');
					int r = Math.max(p, q);
					if (r == -1){
						str1 = "";
						str2 = formula + str2;
						break;
					}else if (r == p){
						str1 = formula.substring(0, r + 1);
						str2 = formula.substring(r + 1).concat(str2);
						isSpaceFound = true;
					}else {
						int numOfParen = 1;
						int i = r - 1;
						while (numOfParen != 0){
							char ch = formula.charAt(i);
							if (ch == ')'){
								numOfParen++;
							}else if (ch == '('){
								numOfParen--;
							}
							i--;
						}
						i++;
						str2 = formula.substring(i + 1);
						formula = formula.substring(0, i + 1);
					}
				}
				if (str2.equals("0")){
					formula = str1.concat(str2);
				}else if (str2.startsWith("-")){
					formula = str1.concat(str2.substring(1));
				}else {
					formula = str1.concat("-").concat(str2);
				}

			}
		}else if (cmd.equals("Button_factorial")){	//TODO 要改良 階乗
			try {
				if (parenthesisEnabled){
					memory.inputNum(evaluateFactorial(memory.getNum()) );
				}else {
					main.inputNum(evaluateFactorial(main.getNum()) );
				}
			}catch(KeiMathException excep){
				int code = excep.getCode();
				switch (code){
				case 2:
					JOptionPane.showMessageDialog(this, excep.getMessage(), "値が不正です", JOptionPane.ERROR_MESSAGE);
					break;
				case 3:
					JOptionPane.showMessageDialog(this, "0から" + excep.getMessage(), "数が大きすぎます", JOptionPane.ERROR_MESSAGE);
					break;
				case 4:
					JOptionPane.showMessageDialog(this, "小数の階乗の求め方を知りません。\n別の計算機に聞いてください", "制作者の知識が足りません", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
			}
		}else if (cmd.equals("Button_root")){	//TODO 要改良 平方根
			try {
				if (parenthesisEnabled){
					memory.inputNum(evaluateRoot(memory.getNum()) );
				}else {
					main.inputNum(evaluateRoot(main.getNum()) );
				}
			} catch (KeiMathException excep) {
				int code = excep.getCode();
				switch (code) {
				case 2:
					JOptionPane.showMessageDialog(this, excep.getMessage(), "値が不正です", JOptionPane.ERROR_MESSAGE);
					break;
				case 3:
					JOptionPane.showMessageDialog(this, excep.getMessage(), "数が大きすぎます", JOptionPane.ERROR_MESSAGE);
					break;
				}
			}
		}else if (cmd.equals("Button_pow")){		//累乗
			JTextField baseTF = new JTextField("0", 20);
			JTextField exponentTF = new JTextField("0", 10);
			JButton baseReset = new JButton("リセット");
			JButton exponentReset = new JButton("リセット");

			JPanel pnl = new JPanel();
			pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
			JPanel basePnl = new JPanel();
			basePnl.setLayout(new FlowLayout());
			basePnl.add(new JLabel("底(n(←これ)のm乗)"));
			basePnl.add(baseTF);
			baseReset.addActionListener(evnt -> baseTF.setText("0"));
			basePnl.add(baseReset);
			pnl.add(basePnl);
			JPanel exponentPnl = new JPanel();
			exponentPnl.setLayout(new FlowLayout());
			exponentPnl.add(new Label("指数(nのm(←これ)乗)"));
			exponentPnl.add(exponentTF);
			exponentReset.addActionListener(evnt -> exponentTF.setText("0"));
			exponentPnl.add(exponentReset);
			pnl.add(exponentPnl);

			while (true) {
				int option = JOptionPane.showConfirmDialog(this, pnl, "値の入力<累乗>", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					try {
						BigDecimal base = new BigDecimal(baseTF.getText());
						BigDecimal exponent = new BigDecimal(exponentTF.getText());
						BigDecimal ans = BDMath.pow(base, exponent, MAX_NUM_DECIMAL_PLACE);

						int option2 = JOptionPane.showConfirmDialog(pnl,
								base + "の" + exponent + "乗を求めますか？",
								"確認", JOptionPane.OK_CANCEL_OPTION);
						if (option2 == JOptionPane.OK_OPTION) {
							String txt;	//式に表示する用
							if (base.compareTo(BigDecimal.ZERO) >= 0){
								txt = base + "^" + exponent;
							}else {
								txt = "(" + base + ")^" + exponent;
							}
							enterConstant(ans, txt);
							break;
						}
					} catch (NumberFormatException exc) {
						JOptionPane.showMessageDialog(this, "値が不正です\n", "", JOptionPane.ERROR_MESSAGE);
					} catch (KeiMathException exc) {
						JOptionPane.showMessageDialog(this, exc.getMessage(), "", JOptionPane.ERROR_MESSAGE);
					}
				}else {
					break;
				}
			}
		}else if (cmd.equals("Button_radicalRoot")){	//累乗根
			JTextField radicandTF = new JTextField("0", 20);	//被開平数の入力用
			JTextField indexTF = new JTextField("0", 10);	//指数の入力用
			JButton radicandReset = new JButton("リセット");
			JButton indexReset = new JButton("リセット");

			JPanel pnl = new JPanel();
			pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
			JPanel radicandPnl = new JPanel();
			radicandPnl.setLayout(new FlowLayout());
			radicandPnl.add(new JLabel("被開平数(n(←これ)のm乗根)"));
			radicandPnl.add(radicandTF);
			radicandReset.addActionListener(evnt -> radicandTF.setText("0"));
			radicandPnl.add(radicandReset);
			pnl.add(radicandPnl);
			JPanel indexPnl = new JPanel();
			indexPnl.setLayout(new FlowLayout());
			indexPnl.add(new JLabel("指数(nのm(←これ)乗根)"));
			indexPnl.add(indexTF);
			indexReset.addActionListener(evnt -> indexTF.setText("0"));
			indexPnl.add(indexReset);
			pnl.add(indexPnl);

			while (true) {
				int option = JOptionPane.showConfirmDialog(this, pnl, "値の入力<累乗根>", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					try  {
						BigDecimal radicand = new BigDecimal(radicandTF.getText());
						BigDecimal index = new BigDecimal(indexTF.getText());
						BigDecimal ans = BDMath.radicalRootWhole(radicand, index, MAX_NUM_DECIMAL_PLACE);

						int option2 = JOptionPane.showConfirmDialog(pnl,
								radicand + "の" + index + "乗根を求めます。\nよろしいですか",
								"確認", JOptionPane.OK_CANCEL_OPTION);
						if (option2 == JOptionPane.OK_OPTION) {
							String txt;	//式に表示する用
							if (index.compareTo(BigDecimal.ZERO) >= 0){
								txt = index + "thRootOf" + radicand;
							}else{
								txt = "(" + index + ")thRootOf" + radicand;
							}
							enterConstant(ans, txt);
							break;
						}

					}catch (NumberFormatException exc) {
						JOptionPane.showMessageDialog(this, "値が不正です\n", "", JOptionPane.ERROR_MESSAGE);
					} catch (KeiMathException exc) {
						JOptionPane.showMessageDialog(this, exc.getMessage() + "\n" + exc, "", JOptionPane.ERROR_MESSAGE);
					}
				}else {
					break;
				}
			}
		}else if (cmd.equals("constant")) {	//ユーザー定数
			Window constGUI = new SelectConstantGUI(this);
			constGUI.setLocationRelativeTo(this);
			constGUI.addWindowListener(this);
			constGUI.setVisible(true);
		}else if (cmd.equals("function")){	//ユーザー関数
			//TODO 処理の記入
		}else if (cmd.equals("editConstsMenu")) {	//ユーザー定数編集
			try {
				Window editConstsGUI = new EditConstantsGUI(this);
				editConstsGUI.setLocationRelativeTo(this);
				editConstsGUI.setVisible(true);
			} catch (IOException exc) {
				JOptionPane.showMessageDialog(this, "エラーが発生しました。\n" + exc, "エラー", JOptionPane.ERROR_MESSAGE);
				exc.printStackTrace();
			}
		}
		else if(cmd.equals("copyFormulaMenu")){
			Clipboard clpb = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection selection;
			if (parenthesisEnabled){
				selection = new StringSelection(formula + inParenthesis);
			}else {
				selection = new StringSelection(formula);
			}
			clpb.setContents(selection, null);
		}else if (cmd.equals("copyValueMenu")){
			Clipboard clpb = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection selection;
			if (isAnswerShown){
				selection = new StringSelection("" + finalAnswer);
			}else if (parenthesisEnabled){
				selection = new StringSelection("" + memory.getNum());
			}else {
				selection = new StringSelection("" + main.getNum());
			}
			clpb.setContents(selection, null);
		}else if (cmd.equals("pasteMenu")){
			Clipboard clpb = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable object = clpb.getContents(null);
			String str = "";
			try{
				str = (String)object.getTransferData(DataFlavor.stringFlavor);
				str = str.trim();
				BigDecimal bd = new BigDecimal(str);
				if (parenthesisEnabled){
					memory.inputNum(bd);
					if (bd.scale() <= 0){
						digitNumber = 0;
					}else {
						digitNumber = memory.getNum().scale() + 1;
					}

					int p = Math.max(inParenthesis.lastIndexOf(' ') + 1, inParenthesis.lastIndexOf('(') + 1);
					inParenthesis = inParenthesis.substring(0, p);
					inParenthesis = inParenthesis + memory.getNum();
				}else {
					if (isAnswerShown){
						btn_Clear.doClick();	//必ずAC
					}
					main.inputNum(bd);
					if (bd.scale() == 0){
						digitNumber = 0;
					}else {
						digitNumber = memory.getNum().scale() + 1;
					}
					digit = BigDecimal.ONE;
					if (digitNumber <= 0){
						digitNumber = 0;
					}else {
						int i = 0;
						while (i < digitNumber){
							digit = digit.divide(BigDecimal.TEN);
							i++;
						}
					}

					boolean isSpaceFound = false;
					while (!isSpaceFound){
						int p = formula.lastIndexOf(' ');
						int q = formula.lastIndexOf(')');
						int r = Math.max(p, q);
						if (r == -1){
							formula = "";
							break;
						}else if (r == p){
							formula = formula.substring(0, r + 1);
							isSpaceFound = true;
						}else {
							int numOfParen = 1;
							int i = r - 1;
							while (numOfParen != 0){
								char ch = formula.charAt(i);
								if (ch == ')'){
									numOfParen++;
								}else if (ch == '('){
									numOfParen--;
								}
								i--;
							}
							i++;
							formula = formula.substring(0, i + 1);
						}
					}
					formula = formula + main.getNum();
				}
			}catch(UnsupportedFlavorException err){
				JOptionPane.showMessageDialog(this, "貼り付けできませんでした", "エラー", JOptionPane.ERROR_MESSAGE);
			}catch(IOException err){
				JOptionPane.showMessageDialog(this, "貼り付けできませんでした", "エラー", JOptionPane.ERROR_MESSAGE);
			}catch(NumberFormatException err){
				JOptionPane.showMessageDialog(this, "文字列を処理できません", "エラー", JOptionPane.ERROR_MESSAGE);
			}
		}

		afterEvent();
	}

	//KeyEventの処理
	@Override
	final public void keyPressed(KeyEvent e){
		int keyCode;
		keyCode = e.getKeyCode();
		if (entNumEnabled){
			switch (keyCode){
			case KeyEvent.VK_0:	// 0キーが押された場合
				enterNumber(new BigDecimal(0));
				break;
			case KeyEvent.VK_1:	// 1キーが押された場合
				enterNumber(new BigDecimal(1));
				break;
			case KeyEvent.VK_2:	// 2キーが押された場合
				enterNumber(new BigDecimal(2));
				break;
			case KeyEvent.VK_3:	// 3キーが押された場合
				enterNumber(new BigDecimal(3));
				break;
			case KeyEvent.VK_4:	// 4キーが押された場合
				enterNumber(new BigDecimal(4));
				break;
			case KeyEvent.VK_5:	// 5キーが押された場合
				enterNumber(new BigDecimal(5));
				break;
			case KeyEvent.VK_6:	// 6キーが押された場合
				enterNumber(new BigDecimal(6));
				break;
			case KeyEvent.VK_7:	// 7キーが押された場合
				enterNumber(new BigDecimal(7));
				break;
			case KeyEvent.VK_8:	// 8キーが押された場合
				enterNumber(new BigDecimal(8));
				break;
			case KeyEvent.VK_9:	// 9キーが押された場合
				enterNumber(new BigDecimal(9));
				break;
			case KeyEvent.VK_BACK_SPACE:	//BackSpaceキーが押された場合
				delete();
				break;

			//以下同様にテンキーで指定
			case KeyEvent.VK_NUMPAD0:	// 0キーが押された場合
				enterNumber(new BigDecimal(0));
				break;
			case KeyEvent.VK_NUMPAD1:	// 1キーが押された場合
				enterNumber(new BigDecimal(1));
				break;
			case KeyEvent.VK_NUMPAD2:	// 2キーが押された場合
				enterNumber(new BigDecimal(2));
				break;
			case KeyEvent.VK_NUMPAD3:	// 3キーが押された場合
				enterNumber(new BigDecimal(3));
				break;
			case KeyEvent.VK_NUMPAD4:	// 4キーが押された場合
				enterNumber(new BigDecimal(4));
				break;
			case KeyEvent.VK_NUMPAD5:	// 5キーが押された場合
				enterNumber(new BigDecimal(5));
				break;
			case KeyEvent.VK_NUMPAD6:	// 6キーが押された場合
				enterNumber(new BigDecimal(6));
				break;
			case KeyEvent.VK_NUMPAD7:	// 7キーが押された場合
				enterNumber(new BigDecimal(7));
				break;
			case KeyEvent.VK_NUMPAD8:	// 8キーが押された場合
				enterNumber(new BigDecimal(8));
				break;
			case KeyEvent.VK_NUMPAD9:	// 9キーが押された場合
				enterNumber(new BigDecimal(9));
				break;
			}
		}

		afterEvent();
	}

	@Override
	final public void keyReleased(KeyEvent e) {
		int keyCode;
		keyCode = e.getKeyCode();

		switch (keyCode){
		case KeyEvent.VK_DECIMAL:	// .キーが押された場合
			btn_point.doClick();
			break;
		case KeyEvent.VK_ADD:	// +キーが押された場合
			btn_plus.doClick();
			break;
		case KeyEvent.VK_SUBTRACT:	// -キーが押された場合
			btn_minus.doClick();
			break;
		case KeyEvent.VK_MULTIPLY:	// *キーが押された場合
			btn_times.doClick();
			break;
		case KeyEvent.VK_DIVIDE:	// /キーが押された場合
			btn_divide.doClick();
			break;
		case KeyEvent.VK_ENTER:	// Enterキーが押された場合 テンキーでも同じKeyCode
			btn_equal.doClick();
			break;
		case KeyEvent.VK_DELETE:	// Deleteキーが押された場合
			btn_Clear.doClick();
			break;
		}

		//全部ボタンを押すだけなのでafterEvent();は省略
	}

	//MouseEventの処理
	final public class myMouseListener extends MouseAdapter{
		@Override
		final public void mouseReleased(MouseEvent e){
			showPopup(e);
		}
		@Override
		final public void mousePressed(MouseEvent e){
			showPopup(e);
		}
	}

	//WindowEventの処理
	@Override
	final public void windowClosed(WindowEvent e){
		JDialog source = (JDialog)e.getWindow();
		String srcName = source.getTitle();
		if (srcName == "ユーザー定数の選択") {
			setVisible(true);
			if (SelectConstantGUI.getCloseType()) {
				enterConstant(SelectConstantGUI.getSelectedValue(), SelectConstantGUI.getSelectedName());
			}
		}

		afterEvent();
	}

	//コピペメニュー表示
	final void showPopup(MouseEvent e){
		if (e.isPopupTrigger()){
			if (e.getComponent() == labelShowingFormula){
				popupOnFormula.show(e.getComponent(), e.getX(), e.getY());
			}else if (e.getComponent() == labelShowingValue){
				popupOnValue.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	//数字入力時の処理
	final void enterNumber(BigDecimal n){
		if (parenthesisEnabled){
			BigDecimal mainNumber = memory.getNum();
			if (mainNumber.compareTo(BigDecimal.ZERO) < 0){
				n = n.multiply(BDMath.MINUS);
			}

			if (digitNumber == 0){
				mainNumber = mainNumber.multiply(BigDecimal.TEN);
				mainNumber = mainNumber.add(n);
			}else {
				mainNumber = mainNumber.add(n.multiply(digit));
				digitNumber++;
				digit = digit.divide(BigDecimal.TEN);
			}
			memory.inputNum(mainNumber);

			int p = Math.max(inParenthesis.lastIndexOf(' ') + 1, inParenthesis.lastIndexOf('(') + 1);
			inParenthesis = inParenthesis.substring(0, p);
			inParenthesis = inParenthesis + memory.getNum();
		}else{
			if (isAnswerShown){
				btn_Clear.doClick();	//必ずAC
			}
			BigDecimal mainNumber = main.getNum();
			if (mainNumber.compareTo(BigDecimal.ZERO) < 0){
				n = n.multiply(BDMath.MINUS);
			}

			if (digitNumber == 0){
				mainNumber = mainNumber.multiply(BigDecimal.TEN);
				mainNumber = mainNumber.add(n);
			}else {
				mainNumber = mainNumber.add(n.multiply(digit));
				digitNumber++;
				digit = digit.divide(BigDecimal.TEN);
			}
			main.inputNum(mainNumber);

			int p = formula.lastIndexOf(' ') + 1;
			formula = formula.substring(0, p);
			formula = formula + main.getNum();
		}
	}

	//一桁削除の処理
	final void delete(){
		if (!isAnswerShown){
			if (digitNumber == 0 || digitNumber == 1){
				digit = BigDecimal.ONE;
				digitNumber = 0;
				if (parenthesisEnabled){
					memory.inputNum(memory.getNum().divide(BigDecimal.TEN, 0, BigDecimal.ROUND_DOWN));
				}else {
					main.inputNum(main.getNum().divide(BigDecimal.TEN, 0, BigDecimal.ROUND_DOWN));
				}
			}else {
				digit = digit.multiply(BigDecimal.TEN);
				digitNumber--;
				if (parenthesisEnabled){
					memory.inputNum(memory.getNum().setScale(digitNumber - 1, BigDecimal.ROUND_DOWN));
				}else {
					main.inputNum(main.getNum().setScale(digitNumber - 1, BigDecimal.ROUND_DOWN));
				}
			}

			if (parenthesisEnabled){
				int p = Math.max(inParenthesis.lastIndexOf(' ') + 1, inParenthesis.lastIndexOf('(') + 1);
				inParenthesis = inParenthesis.substring(0, p);
				inParenthesis = inParenthesis + memory.getNum();
			}else {
				int p = formula.lastIndexOf(' ');
				if (p == -1){
					formula = "";
				}else {
					formula = formula.substring(0, p + 1);
				}
				formula = formula + main.getNum();
			}
		}
	}

	//括弧開始の処理
	final void startParenthesis(){
		if (!isAnswerShown){
			btn_Clear.setText("C");
			btn_Clear.setActionCommand("Button_Clear");
			btn_Clear.doClick();
			digit = BigDecimal.ONE;
			digitNumber = 0;
			memory.resetAll();
			parenthesisEnabled = true;
			inParenthesis = "(";
			btn_equal.setEnabled(false);
			btn_sParen.setEnabled(false);
			btn_cParen.setEnabled(true);

			boolean isSpaceFound = false;
			while (!isSpaceFound){
				int p = formula.lastIndexOf(' ');
				int q = formula.lastIndexOf(')');
				int r = Math.max(p, q);
				if (r == -1){
					formula = "";
					break;
				}else if (r == p){
					formula = formula.substring(0, r + 1);
					isSpaceFound = true;
				}else {
					int numOfParen = 1;
					int i = r - 1;
					while (numOfParen != 0){
						char ch = formula.charAt(i);
						if (ch == ')'){
							numOfParen++;
						}else if (ch == '('){
							numOfParen--;
						}
						i--;
					}
					i++;
					formula = formula.substring(0, i + 1);
				}
			}
		}
	}

	//括弧終了の処理
	final void closeParenthesis() throws KeiMathException{
		if (memory.getNowOperator() == Operator.MULTIPLY || memory.getNowOperator() == Operator.DIVIDE){
			if (inParenthesis.endsWith(" ")){
				btn1.doClick();
			}
		}

		main.inputNum(memory.getAnswer());
		if (main.getNum().scale() == 0){
			digitNumber = 0;
			digit = BigDecimal.ONE;
		}else {
			digitNumber = main.getNum().scale() + 1;
			int i = 0;
			digit = BigDecimal.ONE;
			while (i < digitNumber){
				digit = digit.divide(BigDecimal.TEN);
				i++;
			}
		}

		formula = formula.concat(inParenthesis).concat(")");
		memory.resetAll();
		entNumEnabled = false;
		parenthesisEnabled = false;
		inParenthesis = "";
		btn_equal.setEnabled(true);
		btn_sParen.setEnabled(true);
		btn_cParen.setEnabled(false);
	}

	//階乗の処理
	//TODO 要改良 階乗処理
	final BigDecimal evaluateFactorial(BigDecimal a) throws KeiMathException{
		if (isAnswerShown){
			main.resetAll();
			main.inputNum(finalAnswer);
			finalAnswer = null;
			isAnswerShown = false;
			if (main.getNum().scale() == 0){
				digitNumber = 0;
			}else {
				digitNumber = main.getNum().scale() + 1;
			}
			digit = BigDecimal.ONE;
			if (digitNumber <= 0){
				digitNumber = 0;
			}else {
				int i = 0;
				while (i < digitNumber){
					digit = digit.divide(BigDecimal.TEN);
					i++;
				}
			}
			formula = "(" + formula + ")";
			a = main.getNum();
			entNumEnabled = false;
		}
		BigDecimal factorialOfA = BDMath.factorialWhole(a);
		entNumEnabled = false;

		if (parenthesisEnabled){
			if (inParenthesis.endsWith(" ") || inParenthesis.endsWith("(")){
				inParenthesis = inParenthesis.concat("0");
			}
			int p = Math.max(inParenthesis.lastIndexOf(' ') + 1, inParenthesis.lastIndexOf('(') + 1);
			String str1 = inParenthesis.substring(0, p);
			String str2 = inParenthesis.substring(p);
			inParenthesis = str1 + "(" + str2 + ")!";
		}else {
			if (formula.endsWith(" ") || formula.equals("")){
				formula = formula.concat("0");
			}
			boolean isSpaceFound = false;
			String str1 = "";
			String str2 = "";
			while (!isSpaceFound){
				int p = formula.lastIndexOf(' ');
				int q = formula.lastIndexOf(')');
				int r = Math.max(p, q);
				if (r == -1){
					str1 = "";
					str2 = formula + str2;
					break;
				}else if (r == p){
					str1 = formula.substring(0, r + 1);
					str2 = formula.substring(r + 1).concat(str2);
					isSpaceFound = true;
				}else {
					int numOfParen = 1;
					int i = r - 1;
					while (numOfParen != 0){
						char ch = formula.charAt(i);
						if (ch == ')'){
							numOfParen++;
						}else if (ch == '('){
							numOfParen--;
						}
						i--;
					}
					i++;
					str2 = formula.substring(i + 1);
					formula = formula.substring(0, i + 1);
				}
			}
			if (str2.startsWith("(") && str2.endsWith(")")){
				formula = str1 + str2 + "!";
			}else {
				formula = str1 + "(" + str2 + ")!";
			}
		}
		//扱うのは整数のみなのでdigit,digitNumberはいじる必要なし。
		return factorialOfA;
	}

	//平方根を求める処理
	//TODO 要改良 平方根処理
	final BigDecimal evaluateRoot(BigDecimal a) throws KeiMathException{
		if (isAnswerShown){	//=が押された後の状態
			main.resetAll();
			main.inputNum(finalAnswer);
			finalAnswer = null;
			isAnswerShown = false;
			if (main.getNum().scale() == 0){
				digitNumber = 0;
			}else {
				digitNumber = main.getNum().scale() + 1;
			}
			digit = BigDecimal.ONE;
			if (digitNumber <= 0){
				digitNumber = 0;
			}else {
				int i = 0;
				while (i < digitNumber){
					digit = digit.divide(BigDecimal.TEN);
					i++;
				}
			}
			formula = "(" + formula + ")";
			a = main.getNum();
			entNumEnabled = false;
		}
		BigDecimal ans = BDMath.sqrt(a, MAX_NUM_DECIMAL_PLACE);
		entNumEnabled = false;

		if (parenthesisEnabled){
			if (inParenthesis.endsWith(" ") || inParenthesis.endsWith("(")){
				inParenthesis = inParenthesis.concat("0");
			}
			int p = inParenthesis.lastIndexOf(' ') + 1;
			int q = inParenthesis.lastIndexOf('(') + 1;
			int r = Math.max(p, q);
			String str1 = inParenthesis.substring(0, r);
			String str2 = inParenthesis.substring(r);
			inParenthesis = str1 + "√(" + str2 + ")";
		}else {
			if (formula.endsWith(" ") || formula.equals("")){
				formula = formula.concat("0");
			}
			boolean isSpaceFound = false;
			String str1 = "";
			String str2 = "";
			while (!isSpaceFound){
				int p = formula.lastIndexOf(' ');
				int q = formula.lastIndexOf(')');
				int r = Math.max(p, q);
				if (r == -1){
					str1 = "";
					str2 = formula + str2;
					break;
				}else if (r == p){
					str1 = formula.substring(0, r + 1);
					str2 = formula.substring(r + 1).concat(str2);
					isSpaceFound = true;
				}else {
					int numOfParen = 1;
					int i = r - 1;
					while (numOfParen != 0){
						char ch = formula.charAt(i);
						if (ch == ')'){
							numOfParen++;
						}else if (ch == '('){
							numOfParen--;
						}
						i--;
					}
					i++;
					str2 = formula.substring(i + 1);
					formula = formula.substring(0, i + 1);
				}
			}
			if (str2.startsWith("(") && str2.endsWith(")")){
				formula = str1 + "√" + str2 + "";
			}else {
				formula = str1 + "√(" + str2 + ")";
			}
		}
		return ans;
	}

	//πなどの定数の入力
	final void enterConstant(BigDecimal value, String name) {
		if (parenthesisEnabled){
			memory.inputNum(value);

			int p = Math.max(inParenthesis.lastIndexOf(' ') + 1, inParenthesis.lastIndexOf('(') + 1);
			inParenthesis = inParenthesis.substring(0, p);
			inParenthesis = inParenthesis + name;
		}else {
			if (isAnswerShown){
				btn_Clear.doClick();	//必ずAC
			}
			main.inputNum(value);

			boolean isSpaceFound = false;
			while (!isSpaceFound){
				int p = formula.lastIndexOf(' ');
				int q = formula.lastIndexOf(')');
				int r = Math.max(p, q);
				if (r == -1){
					formula = "";
					break;
				}else if (r == p){
					formula = formula.substring(0, r + 1);
					isSpaceFound = true;
				}else {
					int numOfParen = 1;
					int i = r - 1;
					while (numOfParen != 0){
						char ch = formula.charAt(i);
						if (ch == ')'){
							numOfParen++;
						}else if (ch == '('){
							numOfParen--;
						}
						i--;
					}
					i++;
					formula = formula.substring(0, i + 1);
				}
			}
			formula = formula + name;
		}
		entNumEnabled = false;
	}

	//関数入力の文字列処理
	final void enterFactorialFormula(String pre, String next) {
		//TODO 処理の記入
	}

	//Event処理後の共通処理(GUI更新等, MouseEventを除く)
	final void afterEvent() {

		if (isAnswerShown){
			btn_Clear.setText("AC");
			btn_Clear.setActionCommand("Button_AClear");
		}else if (parenthesisEnabled || main.getNum().compareTo(BigDecimal.ZERO) != 0){
			btn_Clear.setText("C");
			btn_Clear.setActionCommand("Button_Clear");
		}else {
			btn_Clear.setText("AC");
			btn_Clear.setActionCommand("Button_AClear");
		}

		if (isAnswerShown){
			valueStr = "  " + finalAnswer;
		}else if (parenthesisEnabled){
			valueStr = "  " + memory.getNum();
		}else {
			valueStr = "  " + main.getNum();
		}
		if (valueStr.length() >= 8){
			valueStr = valueStr.substring(2);
		}
		labelShowingValue.setText(valueStr);

		if (parenthesisEnabled){
			if (formula.length() >= 10){
				labelShowingFormula.setText(formula + inParenthesis);
			}else {
				labelShowingFormula.setText("  " + formula + inParenthesis);
			}
		}else {
			if (formula.length() >= 10){
				labelShowingFormula.setText(formula);
			}else {
				labelShowingFormula.setText("  " + formula);
			}
		}

		btn0.setEnabled(entNumEnabled);
		btn00.setEnabled(entNumEnabled);
		btn1.setEnabled(entNumEnabled);
		btn2.setEnabled(entNumEnabled);
		btn3.setEnabled(entNumEnabled);
		btn4.setEnabled(entNumEnabled);
		btn5.setEnabled(entNumEnabled);
		btn6.setEnabled(entNumEnabled);
		btn7.setEnabled(entNumEnabled);
		btn8.setEnabled(entNumEnabled);
		btn9.setEnabled(entNumEnabled);
		btn_point.setEnabled(entNumEnabled);
		btn_delete.setEnabled(entNumEnabled);

		requestFocusInWindow();
	}


	//使わないやつ
	@Override
	final public void windowActivated(WindowEvent arg0) {
	}

	@Override
	final public void windowClosing(WindowEvent e) {
	}

	@Override
	final public void windowDeactivated(WindowEvent e) {
	}

	@Override
	final public void windowDeiconified(WindowEvent e) {
	}

	@Override
	final public void windowIconified(WindowEvent e) {
	}

	@Override
	final public void windowOpened(WindowEvent e) {
	}

	@Override
	final public void keyTyped(KeyEvent e) {
	}
}