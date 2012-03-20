package ui.MainWindow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import com.swtdesigner.SWTResourceManager;

import systemConfig.SystemParameter;

public class SystemConfigComposite extends Composite {

	private Text text_drivername;
	private Combo combo_urlMaxLength;
	private Combo combo_contentMaxLength;
	private Combo combo_titleMaxLength;
	private Text text_port;
	private Text text_password;
	private Text text_username;
	private Spinner spinner_titleSimilarity;
	
	public SystemConfigComposite(Composite parent) {
		super(parent, SWT.BORDER);
		createContent();
		loadSystemConfig();

		final Label label = new Label(this, SWT.NONE);
		label.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		label.setText("参数设置完成后，请点击“保存”按钮，使当前设置生效。");
		label.setBounds(34, 23, 460, 33);
	}

	private void createContent() {
		final Group group_DBConfig = new Group(this, SWT.NONE);
		group_DBConfig.setText("连接数据库参数");
		group_DBConfig.setBounds(72, 62, 422, 170);

		final Label label = new Label(group_DBConfig, SWT.NONE);
		label.setText("用户名：");
		label.setBounds(45, 39, 92, 14);

		final Label label_1 = new Label(group_DBConfig, SWT.NONE);
		label_1.setText("密码：");
		label_1.setBounds(45, 70, 92, 14);

		final Label label_2 = new Label(group_DBConfig, SWT.NONE);
		label_2.setText("端口号：");
		label_2.setBounds(45, 100, 92, 14);
		
		final Label label_8 = new Label(group_DBConfig, SWT.NONE);
		label_8.setText("驱动名称：");
		label_8.setBounds(44, 131, 93, 14);

		text_username = new Text(group_DBConfig, SWT.BORDER);
		text_username.setBounds(143, 36, 179, 25);

		text_password = new Text(group_DBConfig, SWT.BORDER);
		text_password.setBounds(143, 67, 179, 25);

		text_port = new Text(group_DBConfig, SWT.BORDER);
		text_port.setBounds(143, 97, 179, 25);

		text_drivername = new Text(group_DBConfig, SWT.BORDER);
		text_drivername.setBounds(143, 128, 179, 25);

		final Group group_outputConfig = new Group(this, SWT.NONE);
		group_outputConfig.setText("输出参数");
		group_outputConfig.setBounds(72, 360, 422, 167);

		final Label label_4 = new Label(group_outputConfig, SWT.NONE);
		label_4.setText("标题最大长度：");
		label_4.setBounds(22, 33, 114, 18);

		final Label label_5 = new Label(group_outputConfig, SWT.NONE);
		label_5.setText("内容最大长度：");
		label_5.setBounds(22, 64, 103, 18);

		final Label label_6 = new Label(group_outputConfig, SWT.NONE);
		label_6.setText("链接最大长度：");
		label_6.setBounds(22, 95, 114, 18);

		combo_titleMaxLength = new Combo(group_outputConfig, SWT.NONE);
		combo_titleMaxLength.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
		combo_titleMaxLength.select(1);
		combo_titleMaxLength.setItems(new String[] {"20", "30", "40", "50"});
		combo_titleMaxLength.setBounds(142, 27, 103, 20);

		combo_contentMaxLength = new Combo(group_outputConfig, SWT.NONE);
		combo_contentMaxLength.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
		combo_contentMaxLength.select(1);
		combo_contentMaxLength.setItems(new String[] {"60", "90", "120"});
		combo_contentMaxLength.setBounds(142, 58, 103, 20);

		combo_urlMaxLength = new Combo(group_outputConfig, SWT.NONE);
		combo_urlMaxLength.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
		combo_urlMaxLength.select(1);
		combo_urlMaxLength.setItems(new String[] {"30", "40", "50"});
		combo_urlMaxLength.setBounds(142, 89, 103, 20);

		final Label label_10 = new Label(group_outputConfig, SWT.NONE);
		label_10.setText("    这三项参数均是用于设置搜索结果在显示框中显示的内容长度。");
		label_10.setBounds(22, 139, 390, 18);
		
		final Group group_orderConfig = new Group(this, SWT.NONE);
		group_orderConfig.setText("排序参数");
		group_orderConfig.setBounds(72, 238, 422, 116);

		final Label label_7 = new Label(group_orderConfig, SWT.NONE);
		label_7.setText("标题相似度：");
		label_7.setBounds(25, 34, 108, 12);

		spinner_titleSimilarity = new Spinner(group_orderConfig, SWT.BORDER);
		spinner_titleSimilarity.setFont(SWTResourceManager.getFont("", 12, SWT.BOLD));
		spinner_titleSimilarity.setMaximum(9);
		spinner_titleSimilarity.setMinimum(1);
		spinner_titleSimilarity.setBounds(160, 27, 64, 22);

		final Label label_3 = new Label(group_orderConfig, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("", 12, SWT.BOLD));
		label_3.setText("0.");
		label_3.setBounds(139, 30, 31, 19);

		final Label label_9 = new Label(group_orderConfig, SWT.WRAP);
		label_9.setText("    标题相似度是指在将搜索结果按照标题排序时，输入标题与搜索结果标题的相近程度。");
		label_9.setBounds(25, 73, 387, 33);
		
		final Button button_save = new Button(this, SWT.NONE);
		button_save.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				doStore();
			}
		});
		button_save.setText("保存");
		button_save.setBounds(410, 533, 84, 33);
	}

	private void loadSystemConfig() {
		String para = "";

		para = SystemParameter.parameters.getProperty("database.user");
		if(para != null && !para.trim().equals(""))text_username.setText(para);
		para = SystemParameter.parameters.getProperty("database.password");
		if(para != null && !para.trim().equals(""))text_password.setText(para);
		para = SystemParameter.parameters.getProperty("database.connectionurl");
		if(para != null && !para.trim().equals(""))text_port.setText(para.substring(23, 27));
		para = SystemParameter.parameters.getProperty("database.drivername");
		if(para != null && !para.trim().equals(""))text_drivername.setText(para);
		para = SystemParameter.parameters.getProperty("order.titlesimilarity");
		if(para != null && !para.trim().equals(""))spinner_titleSimilarity.setSelection((int)(Float.valueOf(para)*10));
		para = SystemParameter.parameters.getProperty("output.titleLength");
		if(para != null && !para.trim().equals(""))combo_titleMaxLength.setText(para);
		para = SystemParameter.parameters.getProperty("output.contentLength");
		if(para != null && !para.trim().equals(""))combo_contentMaxLength.setText(para);
		para = SystemParameter.parameters.getProperty("output.urlLength");
		if(para != null && !para.trim().equals(""))combo_urlMaxLength.setText(para);
	}
	
	/**
	 * 保存配置信息
	 */
	private void doStore() {
		BufferedWriter bw = null;
		Set<String> paraNames = null;
		String para = "";
		
		para = text_username.getText();
		if(para != null && !para.trim().equals(""))
			SystemParameter.parameters.setProperty("database.user", para);
		para = text_password.getText();
		if(para != null && !para.trim().equals(""))
			SystemParameter.parameters.setProperty("database.password", para);
		para = text_port.getText();
		if(para != null && !para.trim().equals("")){
			para = "jdbc:mysql://localhost:" + para + "/";
			SystemParameter.parameters.setProperty("database.connectionurl", para);
		}
		para = text_drivername.getText();
		if(para != null && !para.trim().equals(""))
			SystemParameter.parameters.setProperty("database.drivername", para);
		para = spinner_titleSimilarity.getText();
		if(para != null && !para.trim().equals(""))
			SystemParameter.parameters.setProperty("order.titlesimilarity", String.valueOf(Float.valueOf(para)/10));
		para = combo_titleMaxLength.getText();
		if(para != null && !para.trim().equals(""))
			SystemParameter.parameters.setProperty("output.titleLength", para);
		para = combo_contentMaxLength.getText();
		if(para != null && !para.trim().equals(""))
			SystemParameter.parameters.setProperty("output.contentLength", para);
		para = combo_urlMaxLength.getText();
		if(para != null && !para.trim().equals(""))
			SystemParameter.parameters.setProperty("output.urlLength", para);
		
		paraNames = SystemParameter.parameters.stringPropertyNames();
		try{
			bw = new BufferedWriter(new FileWriter(new File("config.ini")));
			for(String paraName : paraNames){
				bw.write(paraName + "=" + SystemParameter.parameters.getProperty(paraName));
				bw.newLine();
			}
			bw.flush();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		
		MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION);
		box.setText("保存成功");
		box.setMessage("系统配置信息修改成功！");
		box.open();
		loadSystemConfig();
	}
}
