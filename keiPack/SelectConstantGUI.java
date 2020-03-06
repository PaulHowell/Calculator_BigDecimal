package keiPack;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
class SelectConstantGUI extends JDialog implements ActionListener {

	private static boolean closeType = false;
	private static BigDecimal selectedValue = new BigDecimal(0);
	private static String selectedName = "";

	private UserConstant constants;
	private ArrayList<String> list4Display = new ArrayList<String>();

	JTextField searchBox = new JTextField(15);
	JList<String> mainList = new JList<String>();

	public SelectConstantGUI(JFrame owner) {
		super(owner, true);
		setTitle("ユーザー定数の選択");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


		//コンポーネントの配置
		JPanel searchBoxPnl = new JPanel();
		searchBoxPnl.setLayout(new FlowLayout());
		searchBoxPnl.add(searchBox);
		JButton searchBtn = new JButton("検索");
		searchBtn.addActionListener(this);
		searchBtn.setActionCommand("search");
		searchBoxPnl.add(searchBtn);
		this.getContentPane().add(searchBoxPnl, BorderLayout.NORTH);

		JPanel mainPnl = new JPanel();
		mainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp = new JScrollPane(mainList);
		mainPnl.add(sp);
		this.getContentPane().add(mainPnl, BorderLayout.CENTER);

		JPanel windowCtrlBtnPnl = new JPanel();
		windowCtrlBtnPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton OKBtn = new JButton("OK");
		OKBtn.addActionListener(this);
		OKBtn.setActionCommand("OK");
		windowCtrlBtnPnl.add(OKBtn);
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(this);
		cancelBtn.setActionCommand("cancel");
		windowCtrlBtnPnl.add(cancelBtn);
		this.getContentPane().add(windowCtrlBtnPnl, BorderLayout.SOUTH);


		//constantsの初期化とmainListへの表示
		try {
			constants = new UserConstant();
			setMainList(constants.getDatas());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "エラーが発生しました", "エラー", JOptionPane.ERROR_MESSAGE);
			cancelBtn.doClick();
		}

	}

	/**
	 * @return boolean trueなら情報あり, falseなら異常終了または情報なし
	 */
	public static boolean getCloseType() {
		return closeType;
	}

	public static BigDecimal getSelectedValue() {
		return selectedValue;
	}

	public static String getSelectedName() {
		return selectedName;
	}

	public void setMainList(HashMap<String, BigDecimal> data) {
		list4Display.clear();
		ArrayList<String> keys = new ArrayList<String>(data.keySet());
		if (!keys.isEmpty()){
			for (int i = 0; i < keys.size(); i++) {
				list4Display.add(keys.get(i) + " - " + data.get(keys.get(i)));
			}
			mainList.clearSelection();
			Object[] arr = list4Display.toArray();
			mainList.setListData(Arrays.copyOf(arr, arr.length, String[].class));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Window w = SwingUtilities.getWindowAncestor((Component)e.getSource());
		switch (e.getActionCommand()) {
		case "search":
			setMainList(constants.searchNames(searchBox.getText()));
			break;
		case "OK":
			String str = mainList.getSelectedValue();
			if (str != null) {
				try {
				selectedValue = new BigDecimal(str.substring(str.lastIndexOf(' ')+1));
				selectedName = str.substring(0, str.lastIndexOf(' ')-2);	//定数名に' 'が入っている場合も考慮
				closeType = true;
				w.dispose();
				}catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(this, exc, "エラー", JOptionPane.ERROR_MESSAGE);
				}
			}else {
				JOptionPane.showMessageDialog(this, "値を選択してください", "エラー", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case "cancel":
			closeType = false;
			w.dispose();
			break;
		}
	}


}