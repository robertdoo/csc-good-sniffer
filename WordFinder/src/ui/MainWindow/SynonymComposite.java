package ui.MainWindow;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class SynonymComposite extends Composite {

	private Text text_search, text_add, text_modify, text_id;
	private List wordsList;
	private Group addGroup, modifyGroup;
	
	public SynonymComposite(Composite parent) {
		super(parent, SWT.BORDER);
		createContent();
	}

	private void createContent() {
		this.setLayout(new GridLayout());
		
		SashForm sash = new SashForm(this, SWT.NONE);
		sash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite leftComp = new Composite(sash, SWT.NONE);
		leftComp.setLayout(new GridLayout());
		
		Composite lefttopComp = new Composite(leftComp, SWT.NONE);
		lefttopComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		text_search = new Text(lefttopComp, SWT.BORDER);
		text_search.setBounds(10, 10, 223, 25);

		final Button button = new Button(lefttopComp, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onSearch();
			}
		});
		button.setText("����");
		button.setImage(SWTResourceManager.getImage("img/traceback(1).png"));
		button.setBounds(257, 8, 79, 22);
		
		wordsList = new List(leftComp, SWT.BORDER);
		wordsList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(wordsList.getSelectionCount() == 0)return;
				text_id.setText(MainWindow.swp.getIdByWords(wordsList.getSelection()[0]));
				text_modify.setText(wordsList.getSelection()[0]);
				modifyGroup.setEnabled(true);
			}
		});
		wordsList.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite leftbottomComp = new Composite(leftComp, SWT.NONE);
		leftbottomComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button button_3 = new Button(leftbottomComp, SWT.NONE);
		button_3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onDelete();
			}
		});
		button_3.setText("ɾ��");
		button_3.setBounds(0, 10, 70, 22);
		
		Composite rightComp = new Composite(sash, SWT.NONE);
		rightComp.setLayout(new GridLayout());
		
		addGroup = new Group(rightComp, SWT.NONE);
		addGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		addGroup.setText("���");

		final Label label = new Label(addGroup, SWT.NONE);
		label.setText("�������飺");
		label.setBounds(10, 36, 76, 12);

		text_add = new Text(addGroup, SWT.WRAP | SWT.BORDER);
		text_add.setBounds(92, 33, 245, 151);

		final Button button_1 = new Button(addGroup, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onAdd();
			}
		});
		button_1.setText("ȷ��");
		button_1.setBounds(92, 190, 70, 22);
		
		modifyGroup = new Group(rightComp, SWT.NONE);
		modifyGroup.setEnabled(false);
		modifyGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		modifyGroup.setText("�޸�");

		text_modify = new Text(modifyGroup, SWT.WRAP | SWT.BORDER);
		text_modify.setBounds(96, 67, 241, 127);

		final Label label_1 = new Label(modifyGroup, SWT.NONE);
		label_1.setText("�������飺");
		label_1.setBounds(10, 70, 80, 12);

		final Button button_2 = new Button(modifyGroup, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				onSave();
			}
		});
		button_2.setText("����");
		button_2.setBounds(96, 200, 74, 22);

		final Label label_2 = new Label(modifyGroup, SWT.NONE);
		label_2.setText("��ţ�");
		label_2.setBounds(10, 29, 80, 12);

		text_id = new Text(modifyGroup, SWT.BORDER);
		text_id.setEditable(false);
		text_id.setBounds(96, 26, 80, 25);
		
		sash.setWeights(new int[]{50, 50});
	}
	
	/**
	 * �������д���
	 */
	public void loadWords() {
		wordsList.setItems(MainWindow.swp.getAllWordsAsString());
	}
	
	/**
	 * ������Ұ�ťʱ�Ķ���
	 */
	private void onSearch() {
		wordsList.removeAll();
		if(text_search.getText().trim().equals(""))
			wordsList.setItems(MainWindow.swp.getAllWordsAsString());
		else
			wordsList.setItems(MainWindow.swp.getWordsByWord(text_search.getText().trim()));
	}
	
	/**
	 * ���ɾ����ť�Ĳ���
	 */
	private void onDelete() {
		if(wordsList.getSelectionCount() == 0)return;
		if(MessageDialog.openConfirm(getShell(),
				"ɾ��ȷ��",
				"ȷ��ɾ���������顰" + wordsList.getSelection()[0] + "����")){
			MainWindow.swp.deleteWords(text_id.getText().trim());
			wordsList.remove(wordsList.getSelectionIndex());
			text_id.setText("");
			text_modify.setText("");
			modifyGroup.setEnabled(false);
		}
	}
	
	/**
	 * �����Ӱ�ťʱ�Ĳ���
	 */
	private void onAdd() {
		if(text_add.getText().trim().equals("")){
			MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("��������������顣");
			box.open();
		}
		else{
			MainWindow.swp.addNewWords(text_add.getText().trim());
			MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("��ӳɹ���");
			box.open();
			wordsList.add(text_add.getText().trim());
			text_add.setText("");
		}
	}
	
	/**
	 * ������水ťʱ�Ĳ���
	 */
	private void onSave() {
		if(text_modify.getText().trim().equals("")){
			if(MessageDialog.openConfirm(getShell(),
					"ע��",
					"����Ϊ�գ��Ƿ�ɾ�������¼��")){
				MainWindow.swp.deleteWords(text_id.getText().trim());
				wordsList.remove(wordsList.getSelectionIndex());
				text_id.setText("");
				text_modify.setText("");
				modifyGroup.setEnabled(false);
			}
			else
				text_modify.setText(wordsList.getSelection()[0]);
		}
		else{
			MainWindow.swp.updateWords(text_id.getText().trim(),
					text_modify.getText().trim());
			MessageBox box = new MessageBox(getShell(), SWT.ICON_WARNING);
			box.setText("��ʾ");
			box.setMessage("�޸ĳɹ���");
			box.open();
			wordsList.setItem(wordsList.getSelectionIndex(),
					text_modify.getText().trim());
		}
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
