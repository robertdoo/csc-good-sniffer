package ui.MainWindow;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import systemConfig.SystemParameter;

public class SystemInfoComposite extends Composite {

	private Label label_ipAddress, label_hostName, label_os,
	              label_JDKVersion, label_systemDir, label_systemVersion,
	              label_titleSimi, label_titleLength, label_contentLength, label_urlLength;
	private Label label_username, label_password, label_port;
	
	public SystemInfoComposite(Composite parent) {
		super(parent, SWT.BORDER);
		createContent();
		loadSystemInfo();
	}

	private void createContent() {
		this.setLayout(new GridLayout());
		
		Composite comp = new Composite(this, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final Group group_serverInfo = new Group(comp, SWT.NONE);
		group_serverInfo.setText("��������Ϣ");
		group_serverInfo.setBounds(10, 10, 490, 264);
		
		final Label label = new Label(group_serverInfo, SWT.NONE);
		label.setText("IP��ַ��");
		label.setBounds(48, 51, 93, 14);

		final Label label_1 = new Label(group_serverInfo, SWT.NONE);
		label_1.setText("�������ƣ�");
		label_1.setBounds(48, 83, 93, 14);

		final Label label_2 = new Label(group_serverInfo, SWT.NONE);
		label_2.setText("����ϵͳ��");
		label_2.setBounds(48, 116, 93, 14);

		final Label label_3 = new Label(group_serverInfo, SWT.NONE);
		label_3.setText(" JDK�汾��");
		label_3.setBounds(48, 149, 93, 14);

		final Label label_4 = new Label(group_serverInfo, SWT.NONE);
		label_4.setText("ϵͳĿ¼��");
		label_4.setBounds(48, 183, 93, 14);

		final Label label_5 = new Label(group_serverInfo, SWT.NONE);
		label_5.setText("ϵͳ�汾��");
		label_5.setBounds(48, 211, 93, 14);

		label_ipAddress = new Label(group_serverInfo, SWT.NONE);
		label_ipAddress.setBounds(147, 51, 174, 14);

		label_hostName = new Label(group_serverInfo, SWT.NONE);
		label_hostName.setBounds(147, 83, 174, 14);

		label_os = new Label(group_serverInfo, SWT.NONE);
		label_os.setBounds(147, 116, 174, 14);

		label_JDKVersion = new Label(group_serverInfo, SWT.NONE);
		label_JDKVersion.setBounds(147, 149, 174, 14);

		label_systemDir = new Label(group_serverInfo, SWT.NONE);
		label_systemDir.setBounds(147, 183, 312, 14);

		label_systemVersion = new Label(group_serverInfo, SWT.NONE);
		label_systemVersion.setBounds(147, 211, 174, 14);

		final Group group_mySQLInfo = new Group(comp, SWT.NONE);
		group_mySQLInfo.setText("�������ݿ���Ϣ");
		group_mySQLInfo.setBounds(10, 302, 490, 121);

		final Label label_6 = new Label(group_mySQLInfo, SWT.NONE);
		label_6.setText("�û�����");
		label_6.setBounds(45, 33, 99, 14);

		final Label label_7 = new Label(group_mySQLInfo, SWT.NONE);
		label_7.setText("���룺");
		label_7.setBounds(45, 59, 99, 14);

		final Label label_8 = new Label(group_mySQLInfo, SWT.NONE);
		label_8.setText("���Ӷ˿ںţ�");
		label_8.setBounds(45, 85, 99, 14);

		label_username = new Label(group_mySQLInfo, SWT.NONE);
		label_username.setBounds(150, 33, 166, 14);

		label_password = new Label(group_mySQLInfo, SWT.NONE);
		label_password.setBounds(150, 59, 166, 14);

		label_port = new Label(group_mySQLInfo, SWT.NONE);
		label_port.setBounds(150, 85, 166, 14);

		final Group group_configInfo = new Group(comp, SWT.NONE);
		group_configInfo.setText("ϵͳ������Ϣ");
		group_configInfo.setBounds(506, 10, 303, 264);

		final Label label_9 = new Label(group_configInfo, SWT.NONE);
		label_9.setText("�������ƶȣ�");
		label_9.setBounds(45, 43, 91, 18);

		final Label label_10 = new Label(group_configInfo, SWT.NONE);
		label_10.setText("������󳤶ȣ�");
		label_10.setBounds(45, 138, 91, 18);

		final Label label_11 = new Label(group_configInfo, SWT.NONE);
		label_11.setText("������󳤶ȣ�");
		label_11.setBounds(45, 175, 91, 18);

		final Label label_12 = new Label(group_configInfo, SWT.NONE);
		label_12.setText("������󳤶ȣ�");
		label_12.setBounds(45, 212, 91, 23);

		label_titleSimi = new Label(group_configInfo, SWT.NONE);
		label_titleSimi.setBounds(169, 43, 91, 18);

		label_titleLength = new Label(group_configInfo, SWT.NONE);
		label_titleLength.setBounds(169, 138, 91, 18);

		label_contentLength = new Label(group_configInfo, SWT.NONE);
		label_contentLength.setBounds(169, 175, 91, 18);

		label_urlLength = new Label(group_configInfo, SWT.NONE);
		label_urlLength.setBounds(169, 212, 91, 23);

		final Label label_13 = new Label(group_configInfo, SWT.HORIZONTAL | SWT.SEPARATOR);
		label_13.setText("Label");
		label_13.setBounds(23, 86, 270, 12);
	}

	private void loadSystemInfo() {
		try {
			InetAddress netAddress =  InetAddress.getLocalHost();
			label_ipAddress.setText(netAddress.getHostAddress());
			label_hostName.setText(netAddress.getHostName());
			label_os.setText(System.getProperties().getProperty("os.name"));
			label_JDKVersion.setText(System.getProperties().getProperty("java.version"));
			label_systemDir.setText(System.getProperties().getProperty("user.dir"));
			label_systemVersion.setText("���ð�");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		String para = "";

		para = SystemParameter.parameters.getProperty("database.user");
		if(para != null)label_username.setText(para);
		para = SystemParameter.parameters.getProperty("database.password");
		if(para != null)label_password.setText(para);
		para = SystemParameter.parameters.getProperty("database.connectionurl");
		if(para != null)label_port.setText(para.substring(23, 27));
		
		para = SystemParameter.parameters.getProperty("order.titlesimilarity");
		if(para != null)label_titleSimi.setText(para);
		para = SystemParameter.parameters.getProperty("output.titleLength");
		if(para != null)label_titleLength.setText(para);
		para = SystemParameter.parameters.getProperty("output.contentLength");
		if(para != null)label_contentLength.setText(para);
		para = SystemParameter.parameters.getProperty("output.urlLength");
		if(para != null)label_urlLength.setText(para);
	}
}
