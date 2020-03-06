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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

//@SuppressWarnings("serial")
class EditConstantsGUI extends JDialog implements ActionListener {

	private UserConstant constants;
	private ArrayList<String> list4Display = new ArrayList<String>();

	JTextField searchBox = new JTextField(15);
	JList<String> mainList = new JList<String>();

	public EditConstantsGUI(JFrame owner) throws IOException {
		super(owner, true);
		setTitle("ユーザー定数の編集");
		setSize(500, 350);
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
		mainPnl.setLayout(new BorderLayout());
		mainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp = new JScrollPane(mainList);
		mainPnl.add(sp, BorderLayout.CENTER);
		JPanel mainBtnPnl = new JPanel();
		mainBtnPnl.setLayout(new FlowLayout(FlowLayout.CENTER));
		mainPnl.add(mainBtnPnl, BorderLayout.SOUTH);
		JButton addBtn = new JButton("新規");
		addBtn.addActionListener(this);
		addBtn.setActionCommand("newConstant");
		mainBtnPnl.add(addBtn);
		JButton editBtn = new JButton("編集");
		editBtn.addActionListener(this);
		editBtn.setActionCommand("editConstant");
		mainBtnPnl.add(editBtn);
		JButton deleteBtn = new JButton("削除");
		deleteBtn.addActionListener(this);
		deleteBtn.setActionCommand("deleteConstant");
		mainBtnPnl.add(deleteBtn);
		this.getContentPane().add(mainPnl, BorderLayout.CENTER);

		JPanel ctrlBtnPnl = new JPanel();
		ctrlBtnPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton saveBtn = new JButton("保存");
		saveBtn.addActionListener(this);
		saveBtn.setActionCommand("save");
		ctrlBtnPnl.add(saveBtn);
		JButton saveAndCloseBtn = new JButton("保存して閉じる");
		saveAndCloseBtn.addActionListener(this);
		saveAndCloseBtn.setActionCommand("saveAndClose");
		ctrlBtnPnl.add(saveAndCloseBtn);
		JButton cancelBtn = new JButton("キャンセル");
		cancelBtn.addActionListener(this);
		cancelBtn.setActionCommand("cancel");
		ctrlBtnPnl.add(cancelBtn);
		this.getContentPane().add(ctrlBtnPnl, BorderLayout.SOUTH);


		//constantsの初期化とmainListへの表示
		constants = new UserConstant();
		setMainList(constants.getDatas());


	}

	public void setMainList(HashMap<String, BigDecimal> data) {
		list4Display.clear();
		ArrayList<String> keys = new ArrayList<String>(data.keySet());
		for (int i = 0; i < keys.size(); i++) {
			list4Display.add(keys.get(i) + " - " + data.get(keys.get(i)));
		}
		mainList.clearSelection();
		Object[] arr = list4Display.toArray();
		mainList.setListData(Arrays.copyOf(arr, arr.length, String[].class));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String selected = mainList.getSelectedValue();
		switch (e.getActionCommand()) {
		case "search":
			setMainList(constants.searchNames(searchBox.getText()));
			break;
		case "newConstant":

			JTextField newNameTxtFld = new JTextField(15);
			JTextField newValTxtFld = new JTextField(15);

			JPanel newConstPnl = new JPanel();
			newConstPnl.add(new JLabel("定数名"));
			newConstPnl.add(newNameTxtFld);
			newConstPnl.add(new JLabel(" 数値"));
			newConstPnl.add(newValTxtFld);

			while (true) {
				int result = JOptionPane.showConfirmDialog(this, newConstPnl, "新規定数の作成", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					try {
						String newName = newNameTxtFld.getText();
						if (checkConstantNameAllowed(newName)) {
							BigDecimal newVal = new BigDecimal(newValTxtFld.getText());
							constants.putNewData(newName, newVal);
							break;
						}else {
							JOptionPane.showMessageDialog(newConstPnl, "定数名に半角スペースを含むことはできません", "エラー", JOptionPane.ERROR_MESSAGE);
						}
					} catch (NumberFormatException exc) {
						JOptionPane.showMessageDialog(newConstPnl, "不正な数値です", "エラー", JOptionPane.ERROR_MESSAGE);
					}
				}else {
					break;
				}
			}
			setMainList(constants.getDatas());
			break;
		case "editConstant":
			if (selected != null) {
				String oldName = selected.substring(0, selected.lastIndexOf(' ')-2);
				JTextField currentNameTxtFld = new JTextField(oldName, 15);
				JTextField currentValTxtFld = new JTextField(selected.substring(selected.lastIndexOf(' ')+1), 15);

				JPanel currentConstPnl = new JPanel();
				currentConstPnl.add(new JLabel("定数名"));
				currentConstPnl.add(currentNameTxtFld);
				currentConstPnl.add(new JLabel("　数値"));
				currentConstPnl.add(currentValTxtFld);

				while (true) {
					int result = JOptionPane.showConfirmDialog(this, currentConstPnl, "新規定数の作成", JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION) {
						try {
							String newName = currentNameTxtFld.getText();
							if (checkConstantNameAllowed(newName)) {
								BigDecimal newVal = new BigDecimal(currentValTxtFld.getText());
								constants.editData(oldName, newName, newVal);
								break;
							}else {
								JOptionPane.showMessageDialog(null, "不正な数値です", "エラー", JOptionPane.ERROR_MESSAGE);
							}
						} catch (NumberFormatException exc) {
							JOptionPane.showMessageDialog(null, "不正な数値です", "エラー", JOptionPane.ERROR_MESSAGE);
						}
					}else {
						break;
					}
				}
			}
			setMainList(constants.getDatas());
			break;
		case "deleteConstant":
			if (selected != null) {
				String[] btnStr = {"OK", "戻る"};
				int option = JOptionPane.showOptionDialog(this,
						"削除します。よろしいですか。",
						"確認",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, btnStr, btnStr[0]);
				if (option == JOptionPane.YES_OPTION) {
					String selectedName = selected.substring(0, selected.lastIndexOf(' ')-2);	//定数名に' 'が入っている場合も考慮
					constants.deleteData(selectedName);
				}
			}
			setMainList(constants.getDatas());
			break;
		case "save":
			try {
				constants.saveDatas();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "エラーが発生しました", "エラー", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case "saveAndClose":
			try {
				constants.saveDatas();
				Window w = SwingUtilities.getWindowAncestor((Component)e.getSource());
				w.dispose();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "エラーが発生しました", "エラー", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case "cancel":
			String[] btnStr = {"OK", "戻る"};
			int option = JOptionPane.showOptionDialog(this,
					"保存していない場合、編集したデータは削除されます。\nよろしいですか。",
					"確認",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, btnStr, btnStr[0]);
			if (option == JOptionPane.YES_OPTION) {
				Window w = SwingUtilities.getWindowAncestor((Component)e.getSource());
				w.dispose();
			}
			break;
		}
	}

	private boolean checkConstantNameAllowed(String name) {
		if (name.indexOf(' ') != -1){
			return false;
		}
		return true;
	}

}