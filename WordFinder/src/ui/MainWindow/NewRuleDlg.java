package ui.MainWindow;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.swtdesigner.SWTResourceManager;

public class NewRuleDlg extends Dialog {

	private Combo combo_category, combo_cnt, combo_language, combo_fileType;
	private Text text_ruleName, text_words, text_snippet, text_unwantedWords, text_site, text_synonymWords;
	private Button check_isRelative;
	
	private String defaultCategory;
	
	public NewRuleDlg(Shell parentShell) {
		super(parentShell);
	}
	
	public NewRuleDlg(Shell parentShell, String defaultCategory) {
		super(parentShell);
		this.defaultCategory = defaultCategory;
	}

	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		Composite comp = new Composite(container, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Label label = new Label(comp, SWT.NONE);
		label.setText("输入规则名称：");
		label.setBounds(10, 30, 147, 14);

		text_ruleName = new Text(comp, SWT.BORDER);
		text_ruleName.setBounds(163, 27, 147, 17);

		final Label label_1 = new Label(comp, SWT.NONE);
		label_1.setText("选择类别：");
		label_1.setBounds(10, 66, 147, 14);

		final Label label_2 = new Label(comp, SWT.NONE);
		label_2.setText("包含全部字句：");
		label_2.setBounds(10, 104, 147, 14);
		
		final Label label_11 = new Label(comp, SWT.NONE);
		label_11.setText("关联词：");
		label_11.setBounds(10, 152, 93, 12);

		final Label label_3 = new Label(comp, SWT.NONE);
		label_3.setText("包含完整字句：");
		label_3.setBounds(10, 211, 147, 14);

		final Label label_4 = new Label(comp, SWT.NONE);
		label_4.setText("不包含的字句：");
		label_4.setBounds(10, 270, 147, 14);

		final Label label_5 = new Label(comp, SWT.NONE);
		label_5.setText("搜索条目：");
		label_5.setBounds(10, 323, 147, 14);

		final Label label_6 = new Label(comp, SWT.NONE);
		label_6.setText("语言：");
		label_6.setBounds(10, 354, 147, 14);

		final Label label_7 = new Label(comp, SWT.NONE);
		label_7.setText("网站：");
		label_7.setBounds(10, 388, 147, 14);

		final Label label_8 = new Label(comp, SWT.NONE);
		label_8.setText("文件格式：");
		label_8.setBounds(10, 422, 147, 14);

		combo_category = new Combo(comp, SWT.READ_ONLY);
		combo_category.setItems(MainWindow.cp.getAllCategorynamesAsArray());
		if(defaultCategory == null)
			combo_category.select(0);
		else
			combo_category.select(combo_category.indexOf(defaultCategory));
		combo_category.setBounds(163, 63, 147, 17);

		text_words = new Text(comp, SWT.BORDER);
		text_words.setBounds(163, 101, 312, 38);
		
		text_synonymWords = new Text(comp, SWT.BORDER);
		text_synonymWords.setBounds(163, 152, 312, 38);

		text_snippet = new Text(comp, SWT.BORDER);
		text_snippet.setBounds(163, 208, 312, 38);

		text_unwantedWords = new Text(comp, SWT.BORDER);
		text_unwantedWords.setBounds(163, 267, 312, 38);

		combo_cnt = new Combo(comp, SWT.READ_ONLY);
		combo_cnt.setItems(new String[] {"10", "20", "50", "100"});
		combo_cnt.select(0);
		combo_cnt.setBounds(163, 320, 147, 17);

		combo_language = new Combo(comp, SWT.READ_ONLY);
		combo_language.setItems(new String[] {"任何语言", "简体中文", "英文"});
		combo_language.select(0);
		combo_language.setBounds(163, 351, 147, 17);

		text_site = new Text(comp, SWT.BORDER);
		text_site.setBounds(163, 385, 312, 17);

		combo_fileType = new Combo(comp, SWT.READ_ONLY);
		combo_fileType.setItems(new String[] {"任何格式", "Adobe Acrobat PDF(.pdf)", "微软 Word(.doc)", "微软 Excel(.xls)", "微软 Powerpoint(.ppt)", "RTF 文件(.rtf)"});
		combo_fileType.select(0);
		combo_fileType.setBounds(163, 419, 147, 17);

		check_isRelative = new Button(comp, SWT.CHECK);
		check_isRelative.setText("使用关联词");
		check_isRelative.setBounds(10, 459, 93, 16);

		final Label label_9 = new Label(comp, SWT.NONE);
		label_9.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_9.setText("注意：使用关联词可能意味着为每项搜索条件加入更多的关键词，这样");
		label_9.setBounds(34, 481, 394, 12);

		final Label label_10 = new Label(comp, SWT.NONE);
		label_10.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_10.setText("可能会对搜索的性能带来一定影响，所以请谨慎使用。");
		label_10.setBounds(10, 498, 418, 12);
		
		return container;
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID,"取消", false);
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(514, 626);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("添加新规则");
		/*
		//设置窗口居中显示
		Rectangle bounds = newShell.getMonitor().getBounds();
		Rectangle rect = newShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width)/2;
		int y = bounds.y + (bounds.height - rect.height)/2;
		newShell.setLocation(x, y);
		*/
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if(IDialogConstants.CANCEL_ID == buttonId)
			close();
		else{
			String ruleName = text_ruleName.getText(),
		       categoryName = combo_category.getText(),
		       words = text_words.getText(),
		       synonymWords = text_synonymWords.getText(),
		       snippet = text_snippet.getText(),
		       unwantedWords = text_unwantedWords.getText(),
		       language = combo_language.getText(),
		       fileType = combo_fileType.getText().substring(combo_fileType.getText().indexOf(".")+1).replace(")", ""),
		       site = text_site.getText(),
		       searchCount = combo_cnt.getText();
			boolean isRelative = check_isRelative.getSelection();
			//规则名称hash值<0时前缀为rulev，否则为rule
			int hashCode = ruleName.hashCode();
			String ruleMapping;
			if (hashCode < 0)
				ruleMapping = "rulev" + Math.abs(hashCode);
			else
				ruleMapping = "rule" + hashCode;
			if(words.equals("") && snippet.equals("") && unwantedWords.equals("")){
				MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
				box.setText("新建规则失败");
				box.setMessage("构成规则条件不足，不能新建规则。");
				box.open();
			}
			else if(!words.equals("") && words.equals(unwantedWords)){
				MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
				box.setText("新建规则失败");
				box.setMessage("错误的规则条件，”包含的全部字句“不能全部出现在”不包含的字句“中。");
				box.open();
			}
			else if(MainWindow.rp.isRuleNameExisted(ruleName)){
				MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
				box.setText("提示");
				box.setMessage("规则”" + ruleName + "“已存在！不能重复添加规则。");
				box.open();
			}
			else{
				MainWindow.rp.addNewRule(ruleName, words, snippet,
						unwantedWords, language, fileType, site,
						searchCount, ruleMapping, categoryName, 
						isRelative, synonymWords);
				
				close();
			}
		}
	}
}
