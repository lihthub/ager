package ager.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import ager.common.AgeFont;
import ager.common.Constants;
import ager.util.CalendarUtils;
import ager.util.ChineseCalendar;
import ager.util.SystemUtils;
import ager.util.Utils;

/**
 * 年龄助手主界面
 * 
 * @author 李海涛
 * @version 1.0
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// 菜单栏图标大小
	private static final int MENUBAR_ICON_WITH = 16;
	private static final int MENUBAR_ICON_HEIGHT = 16;
	
	private JPanel contentPanel; // 主面板
	private JTable table; // 表格
	private DefaultTableModel tableModel; // 表格数据
	private JLabel queryBtn; // 查询按钮
	private ImageIcon queryIcon; // 查询按钮图标
	private ImageIcon queryIconGo; // 鼠标移到按钮时的图标
	private ButtonGroup bg; // 农历生日 公历生日 按钮集合
	private JComboBox<String> glNianCB; // 公历年下拉框
	private JComboBox<String> nlNianCB; // 农历年下拉框
	private JComboBox<String> glYueCB; // 公历月下拉框
	private JComboBox<String> nlYueCB; // 农历月下拉框
	private JComboBox<String> glRiCB; // 公历日下拉框
	private JComboBox<String> nlRiCB; // 农历日下拉框
	
	private int age; // 周岁
	private int nominalAge; // 虚岁
	private String animalsYear = ""; // 生肖
	private String starSigns = ""; // 星座
	private String glBirthday = ""; // 公历生日
	private String nlBirthday = ""; // 农历生日
	private String glFestival = ""; // 公历节日
	private String nlFestival = ""; // 农历节日
	private long totalDays; // 生活天数
	private long toBirthDays; // 距下次公历生日天数
	private String nextBirthWeek = ""; // 下次生日星期几
	
	private String[] yueArr = { "正", "二", "三", "四", "五", "六", "七", "八", "九", "十",
			"冬", "腊", "闰正", "闰二", "闰三", "闰四", "闰五", "闰六", "闰七", "闰八", "闰九",
			"闰十", "闰冬", "闰腊" };
	private String[] riArr = { "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八",
			"初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八",
			"十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八",
			"廿九", "三十" };

	/**
	 * 主面板
	 */
	public MainFrame() {
		try {
			// 设置界面风格为系统默认风格
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int screenWidth = kit.getScreenSize().width; // 屏幕宽度
		int screenHeight = kit.getScreenSize().height; // 屏幕高度
		int frameWidth = 450; // 主窗口宽度
		int frameHeight = 420; // 主窗口高度
		setBounds(screenWidth / 2 - frameWidth / 2, screenHeight / 2 - frameHeight / 2, frameWidth, frameHeight);
		Date today = new Date();
		String glDateStr = CalendarUtils.dateToStr(today); // 今天公历日期
		String nlDateStr = new ChineseCalendar(today).getChineseMouthDay(); // 今天农历日期
		setTitle("年龄助手 - " + glDateStr + "（农历" + nlDateStr + "）");
		ImageIcon icon = new ImageIcon(Constants.ICON_URL);
		setIconImage(icon.getImage());
		
		contentPanel = new JPanel();
//		contentPanel.setBackground(new Color(102, 153, 255));
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.setToolTipText("");
		contentPanel.setLayout(null);
		
		createMenuBar(frameWidth);
		createSearchArea();
		createResultArea();
		
		JLabel lblText = new JLabel();
		lblText.setForeground(Color.WHITE);
		lblText.setFont(new AgeFont().getBoldFont());
		lblText.setText("© 2015-" + CalendarUtils.getThisYear() + " 李海涛");
		lblText.setBounds(160, 356, 150, 21);
		lblText.setAlignmentX(CENTER_ALIGNMENT);
		contentPanel.add(lblText);
		
		setContentPane(contentPanel);
		
		// 设置背景图片
		Image img = new ImageIcon(Constants.BACKGROUND_IMAGE_URL).getImage();
		BackgroundPanel bgPanel = new BackgroundPanel(img);
		bgPanel.setBounds(0, 0, frameWidth, frameHeight);
		add(bgPanel);
	}
	
	/**
	 * 菜单栏
	 */
	private void createMenuBar(int frameWidth) {
		Font plainFont = new AgeFont();
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(plainFont);
		menuBar.setBounds(0, 0, frameWidth, 21);
		contentPanel.add(menuBar);
		
		JMenu ager = new JMenu("Ager");
		ager.setFont(plainFont);
		menuBar.add(ager);
		
		JMenuItem close = new JMenuItem("关闭");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		close.setFont(plainFont);
		ager.add(close);
		
		JMenu tools = new JMenu("工具");
		tools.setFont(plainFont);
		menuBar.add(tools);
		
		JMenuItem export = new JMenuItem("导出");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		ImageIcon exportIcon = new ImageIcon(Constants.ICON_EXPORT_URL);
		exportIcon.setImage(exportIcon.getImage().getScaledInstance(MENUBAR_ICON_WITH, MENUBAR_ICON_HEIGHT, Image.SCALE_DEFAULT));
		export.setIcon(exportIcon);
		export.setFont(plainFont);
		tools.add(export);
		
		JMenuItem options = new JMenuItem("选项");
		options.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toOptions();
			}
		});
		ImageIcon optionsIcon = new ImageIcon(Constants.ICON_OPTIONS_URL);
		optionsIcon.setImage(optionsIcon.getImage().getScaledInstance(MENUBAR_ICON_WITH, MENUBAR_ICON_HEIGHT, Image.SCALE_DEFAULT));
		options.setIcon(optionsIcon);
		options.setFont(plainFont);
		tools.add(options);
		
		JMenu help = new JMenu("帮助");
		help.setFont(plainFont);
		menuBar.add(help);
		
		JMenuItem knowledge = new JMenuItem("常识");
		knowledge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toKnowledge();
			}
		});
		ImageIcon knowledgeIcon = new ImageIcon(Constants.ICON_KNOWLEDGE_URL);
		knowledgeIcon.setImage(knowledgeIcon.getImage().getScaledInstance(MENUBAR_ICON_WITH, MENUBAR_ICON_HEIGHT, Image.SCALE_DEFAULT));
		knowledge.setIcon(knowledgeIcon);
		knowledge.setFont(plainFont);
		help.add(knowledge);
		
		JMenuItem about = new JMenuItem("关于Ager");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toAbout();
			}
		});
		ImageIcon aboutIcon = new ImageIcon(Constants.ICON_URL);
		aboutIcon.setImage(aboutIcon.getImage().getScaledInstance(MENUBAR_ICON_WITH, MENUBAR_ICON_HEIGHT, Image.SCALE_DEFAULT));
		about.setIcon(aboutIcon);
		about.setFont(plainFont);
		help.add(about);
	}
	
	/**
	 * 搜索区
	 * @param boldFont2 
	 * @param plainFont2 
	 */
	private void createSearchArea() {
		Font plainFont = new AgeFont();
		Font boldFont = new AgeFont().getBoldFont();
		String[] years = new String[199];
		String[] months = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
		String[] days = new String[31];
		for (int i = 0, year = 1901; year <= 2099; i++, year++) {
			years[i] = year + "";
		}
		for (int i = 0; i < 31; i++) {
			days[i] = i + 1 + "";
		}
		
		JRadioButton glRadio = new JRadioButton("公历生日");
		glRadio.setActionCommand("gl");
		glRadio.setFocusPainted(false); // 设置无焦点边框
		glRadio.setSelected(true);
		glRadio.setFont(boldFont);
		glRadio.setContentAreaFilled(false);
		glRadio.setBounds(47, 50, 80, 23);
		contentPanel.add(glRadio);
		
		JRadioButton nlRadio = new JRadioButton("农历生日");
		nlRadio.setActionCommand("nl");
		nlRadio.setFocusPainted(false); // 设置无焦点边框
		nlRadio.setFont(boldFont);
		nlRadio.setContentAreaFilled(false);
		nlRadio.setBounds(47, 78, 80, 23);
		contentPanel.add(nlRadio);
		
		bg = new ButtonGroup();
		bg.add(glRadio);
		bg.add(nlRadio);
		
		// 公历年份下拉列表
		glNianCB = new JComboBox<String>();
		glNianCB.setMaximumRowCount(15);
		glNianCB.setFont(plainFont);
		glNianCB.setModel(new DefaultComboBoxModel<String>(years));
		if (SystemUtils.isMacOS()) {
			glNianCB.setBounds(124, 51, 85, 21);
		} else {
			glNianCB.setBounds(132, 51, 54, 21);
		}
		contentPanel.add(glNianCB);
		
		// 农历年份下拉列表
		nlNianCB = new JComboBox<String>();
		nlNianCB.setMaximumRowCount(15);
		nlNianCB.setFont(plainFont);
		nlNianCB.setModel(new DefaultComboBoxModel<String>(years));
		if (SystemUtils.isMacOS()) {
			nlNianCB.setBounds(124, 79, 85, 21);
		} else {
			nlNianCB.setBounds(132, 79, 54, 21);
		}
		contentPanel.add(nlNianCB);
		
		// 公历月份下拉列表
		glYueCB = new JComboBox<String>();
		glYueCB.setFont(plainFont);
		glYueCB.setModel(new DefaultComboBoxModel<String>(months));
		if (SystemUtils.isMacOS()) {
			glYueCB.setBounds(205, 51, 66, 21);
		} else {
			glYueCB.setBounds(207, 51, 40, 21);
		}
		contentPanel.add(glYueCB);
		
		// 农历月份下拉列表
		nlYueCB = new JComboBox<String>();
		nlYueCB.setFont(plainFont);
		nlYueCB.setModel(new DefaultComboBoxModel<String>(yueArr));
		if (SystemUtils.isMacOS()) {
			nlYueCB.setBounds(200, 79, 75, 21);
		} else {
			nlYueCB.setBounds(207, 79, 52, 21);
		}
		contentPanel.add(nlYueCB);
		
		// 公历日下拉列表
		glRiCB = new JComboBox<String>();
		glRiCB.setFont(plainFont);
		glRiCB.setModel(new DefaultComboBoxModel<String>(days));
		if (SystemUtils.isMacOS()) {
			glRiCB.setBounds(266, 51, 66, 21);
		} else {
			glRiCB.setBounds(269, 51, 40, 21);
		}
		contentPanel.add(glRiCB);
		
		// 农历日下拉列表
		nlRiCB = new JComboBox<String>();
		nlRiCB.setFont(plainFont);
		nlRiCB.setModel(new DefaultComboBoxModel<String>(riArr));
		if (SystemUtils.isMacOS()) {
			nlRiCB.setBounds(266, 79, 75, 21);
		} else {
			nlRiCB.setBounds(279, 79, 52, 21);
		}
		contentPanel.add(nlRiCB);
		
		// 年月日标签
		if (!SystemUtils.isMacOS()) {
			JLabel glNianLabel = new JLabel();
			glNianLabel.setFont(plainFont);
			glNianLabel.setText("年");
			glNianLabel.setBounds(190, 51, 18, 21);
			contentPanel.add(glNianLabel);
			
			JLabel nlNianLabel = new JLabel();
			nlNianLabel.setText("年");
			nlNianLabel.setFont(plainFont);
			nlNianLabel.setBounds(190, 79, 18, 21);
			contentPanel.add(nlNianLabel);
		
			JLabel glYueLabel = new JLabel();
			glYueLabel.setFont(plainFont);
			glYueLabel.setText("月");
			glYueLabel.setBounds(251, 51, 18, 21);
			contentPanel.add(glYueLabel);
			
			JLabel nlYueLabel = new JLabel();
			nlYueLabel.setText("月");
			nlYueLabel.setFont(plainFont);
			nlYueLabel.setBounds(263, 79, 18, 21);
			contentPanel.add(nlYueLabel);
		
			JLabel riLabel = new JLabel();
			riLabel.setFont(plainFont);
			riLabel.setText("日");
			riLabel.setBounds(313, 51, 18, 21);
			contentPanel.add(riLabel);
		}
		
		// 查询按钮
		queryBtn = new JLabel();
		queryIcon = new ImageIcon(Constants.ICON_QUERYBTN_1_URL);
		queryIconGo = new ImageIcon(Constants.ICON_QUERYBTN_2_URL);
		queryBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				query();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				queryBtn.setIcon(queryIconGo);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				queryBtn.setIcon(queryIcon);
			}
		});
		queryBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 设置鼠标为手形
		queryBtn.setIcon(queryIcon);
		queryBtn.setBounds(345, 50, 50, 50);
		contentPanel.add(queryBtn);
	}
	
	/**
	 * 结果显示区
	 */
	private void createResultArea() {
		Font plainFont = new AgeFont();
		Font boldFont = new AgeFont().getBoldFont();
		table = new JTable();
		// 表格背景设为透明
		table.setOpaque(false);
		table.setGridColor(Color.WHITE);
		table.setFont(boldFont);
		table.setFillsViewportHeight(true);
		table.setEnabled(false);
		tableModel = new DefaultTableModel(
			new Object[][] {
				{ "周岁", "" },
				{ "虚岁", "" },
				{ "生肖", "" },
				{ "星座", "" },
				{ "公历生日", "" },
				{ "农历生日", "" },
				{ "生活天数", "" },
				{ "下次公历生日", "" },
			},
			new String[] { "name", "description" }
		);
		table.setModel(tableModel);
		DefaultTableCellRenderer nameRender = new DefaultTableCellRenderer();
		nameRender.setHorizontalAlignment(SwingConstants.CENTER); // 设置字体居中
		nameRender.setOpaque(false);
	    MyTableCellRenderer desRender = new MyTableCellRenderer();
	    desRender.setFont(plainFont);
	    desRender.setOpaque(false);
	    table.getColumnModel().getColumn(0).setCellRenderer(nameRender);
	    table.getColumnModel().getColumn(1).setCellRenderer(desRender);
		table.getColumnModel().getColumn(0).setPreferredWidth(82);
		table.getColumnModel().getColumn(1).setPreferredWidth(308);
		table.setRowHeight(28);
		table.setBounds(30, 120, 390, 224);
		contentPanel.add(table);
	}

	/**
	 * 查询
	 */
	private void query() {
		int year = 1901;
		int month = 1;
		int day = 1;
		if ("gl".equals(bg.getSelection().getActionCommand())) {
			// 根据公历生日查询
			year = Integer.parseInt(glNianCB.getSelectedItem().toString()); // 输入年 公历
			month = Integer.parseInt(glYueCB.getSelectedItem().toString()); // 输入月 公历
			day = Integer.parseInt(glRiCB.getSelectedItem().toString()); // 输入日 公历
			int lastDay = 0;
			
			if (month == 2) { // 如果是2月
				if (CalendarUtils.isLeapYear(year)) { // 是否闰年
					lastDay = 29;
				} else {
					lastDay = 28;
				}
				if (day > lastDay) {
					// getRootPane() 表示在根面板中间位置弹出对话框
					JOptionPane.showMessageDialog(getRootPane(),
							year + "年2月共有" + lastDay + "天！请重新选择！", "",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else if (month == 4 || month == 6 || month == 9 || month == 11) {
				// 4 6 9 11 共30天
				lastDay = 30;
				if (day > lastDay) {
					JOptionPane.showMessageDialog(getRootPane(),
							month + "月共有30天！请重新选择！", "",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		} else {
			// 根据农历生日查询
			int yue = 1;
			int ri = 1;
			boolean isLeapMonth = false;
			int nlYear = Integer.parseInt(nlNianCB.getSelectedItem().toString()); // 输入年 农历
			String nlYue = nlYueCB.getSelectedItem().toString(); // 输入月 农历
			String nlRi = nlRiCB.getSelectedItem().toString(); // 输入日 农历
			for (int i = 0; i < yueArr.length; i++) {
				if (nlYue.equals(yueArr[i])) {
					if (i > 11) {
						yue = i - 11;
						isLeapMonth = true;
					} else {
						yue = i + 1;
						isLeapMonth = false;
					}
					break;
				}
			}
			for (int i = 0; i < riArr.length; i++) {
				if (nlRi.equals(riArr[i])) {
					ri = i + 1;
					break;
				}
			}
			int[] glDateArr = ChineseCalendar.lunarToSolar(nlYear, yue, ri, isLeapMonth); // 农历转公历
			year = glDateArr[0]; // 公历年 
			month = glDateArr[1]; // 公历月
			day = glDateArr[2]; // 公历日
		}
			
		// 是否大于今天
		if (CalendarUtils.getToDays(year, month, day) > 0) {
			JOptionPane.showMessageDialog(getRootPane(),
					"你选择的这一天还没到来！请重新选择！", "",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String dateStr = year + "-" + month + "-" + day; // 输入日期 yyyy-MM-dd
		Utils utils = new Utils(dateStr);
		age = utils.getAge();
		nominalAge = utils.getNominalAge();
		animalsYear = utils.getAnimalsYear();
		starSigns = utils.getStarSigns();
		glBirthday = utils.getGLBirthday();
		nlBirthday = utils.getNLBirthday();
		glFestival = utils.getGLFestival();
		nlFestival = utils.getNLFestival();
		totalDays = utils.getDays();
		toBirthDays = utils.getToGLBirthDays();
		nextBirthWeek = utils.getNextGLBirthWeek();
		
		// 给结果显示表格中设值
		tableModel.setValueAt(age + "岁", 0, 1); // 周岁
		tableModel.setValueAt(nominalAge + "岁", 1, 1); // 虚岁
		tableModel.setValueAt(animalsYear, 2, 1); // 生肖
		tableModel.setValueAt(starSigns, 3, 1); // 星座
		tableModel.setValueAt(glBirthday + " " + glFestival, 4, 1); // 公历生日 + 节日
		tableModel.setValueAt(nlBirthday + " " + nlFestival, 5, 1); // 农历生日 + 节日
		tableModel.setValueAt("你在这个世界上生活了：" + totalDays + "天", 6, 1); // 生活天数
		tableModel.setValueAt("距下次生日还有：" + toBirthDays + "天，那天是星期" + nextBirthWeek, 7, 1); // 下次公历生日
		tableModel.fireTableDataChanged();
	}
	
	/**
	 * 选项
	 */
	private void toOptions() {
		try {
			OptionsDialog dialog = new OptionsDialog();
			int dialogWidth = dialog.getWidth();
			int dialogHeight = dialog.getHeight();
			int dialogX = getX() + (getWidth() / 2 - dialogWidth / 2);
			int dialogY = getY() + (getHeight() / 2 - dialogHeight / 2);
			dialog.setLocation(dialogX, dialogY); // 设置窗口的位置在主窗口的中间显示
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导出
	 */
	private void export() {
		@SuppressWarnings("deprecation")
		String text = "\r\n★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆"
					+ "\r\n                       " + new Date().toLocaleString()
					+ "\r\n周岁：" + age + "岁"
					+ "\r\n虚岁：" + nominalAge + "岁"
					+ "\r\n生肖：" + animalsYear
					+ "\r\n星座：" + starSigns
					+ "\r\n公历生日：" + glBirthday + " " + glFestival
					+ "\r\n农历生日：" + nlBirthday + " " + nlFestival
					+ "\r\n你在这个世界上生活了：" + totalDays + "天"
					+ "\r\n距下次生日还有：" + toBirthDays + "天，那天是星期" + nextBirthWeek
					+ "\r\n★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆";
		FileSystemView fsv = FileSystemView.getFileSystemView(); // 得到系统视图对象
	    File root = fsv.getHomeDirectory(); // 获取桌面的路径
		JFileChooser fileChooser = new JFileChooser(root.getPath());
		fileChooser.setSelectedFile(new File("/年龄助手.txt")); // 默认文件名
		int option = fileChooser.showSaveDialog(getRootPane());
        if(option == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				char[] arry = text.toCharArray();
				writer.write(arry);
				JOptionPane.showMessageDialog(getRootPane(), "已保存至 " + file.toString(), "成功", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.flush();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
        }
	}
	
	/**
	 * 常识
	 */
	private void toKnowledge() {
		try {
			KnowledgeDialog dialog = new KnowledgeDialog();
			int dialogWidth = dialog.getWidth();
			int dialogHeight = dialog.getHeight();
			int dialogX = getX() + (getWidth() / 2 - dialogWidth / 2);
			int dialogY = getY() + (getHeight() / 2 - dialogHeight / 2);
			dialog.setLocation(dialogX, dialogY); // 设置窗口的位置在主窗口的中间显示
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关于年龄助手
	 */
	private void toAbout() {
		try {
			AboutDialog dialog = new AboutDialog();
			int dialogWidth = dialog.getWidth();
			int dialogHeight = dialog.getHeight();
			int dialogX = getX() + (getWidth() / 2 - dialogWidth / 2);
			int dialogY = getY() + (getHeight() / 2 - dialogHeight / 2);
			dialog.setLocation(dialogX, dialogY); // 设置窗口的位置在主窗口的中间显示
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

/**
 * 单元格渲染器
 * 
 * @author 李海涛
 * @version 1.0
 */
class MyTableCellRenderer extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = -7204578599204168752L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setHorizontalAlignment(SwingConstants.CENTER);
		setValue(value);
		return this;
	}
	
	protected void setValue(Object value) {
        setText((value == null) ? "" : value.toString());
    }
	
}