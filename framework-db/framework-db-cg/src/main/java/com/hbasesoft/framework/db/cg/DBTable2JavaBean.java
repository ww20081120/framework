package com.hbasesoft.framework.db.cg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.security.DataUtil;

/**
 * <Description> 此类用来将mysql的表直接生成Bean<br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年11月5日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
public class DBTable2JavaBean extends JFrame {

    /** DEFAULT_LENGTH */
    private static final int DEFAULT_LENGTH = 100;

    /** DEFAULT_LENGTH */
    private static final int DEFAULT_HIGHT = 30;

    /** START_LENGTH */
    private static final int START_LENGTH = 840;

    /** NUM */
    private static final int NUM_3 = 3;

    /** NUM */
    private static final int NUM_4 = 4;

    /** NUM */
    private static final int NUM_5 = 5;

    /** NUM */
    private static final int NUM_6 = 6;

    /** NUM */
    private static final int NUM_13 = 13;

    /** NUM */
    private static final int NUM_15 = 15;

    /** NUM */
    private static final int NUM_20 = 20;

    /** NUM */
    private static final int NUM_23 = 23;

    /** NUM */
    private static final int NUM_40 = 40;

    /** NUM */
    private static final int NUM_80 = 80;

    /** NUM */
    private static final int NUM_93 = 93;

    /** NUM */
    private static final int NUM_120 = 120;

    /** NUM */
    private static final int NUM_145 = 145;

    /** NUM */
    private static final int NUM_150 = 150;

    /** NUM */
    private static final int NUM_450 = 450;

    /** NUM */
    private static final int NUM_580 = 580;

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * TITLE
     */
    private static final String TITLE = "根据数据库生成javabean小工具";

    /**
     * filedNames
     */
    private String[] filedNames = new String[] {
        "表名", "包名", "输出目录", "数据库地址", "用户名", "密码", "加密后的密码"
    };

    /**
     * textFields
     */
    private JTextField[] textFields;

    /**
     * tips
     */
    private JLabel[] tips;

    /**
     * dataSource
     */
    private DruidDataSource dataSource;

    /**
     * DBTable2JavaBean
     */
    public DBTable2JavaBean() {

        textFields = new JTextField[filedNames.length];
        tips = new JLabel[filedNames.length];

        setResizable(false);
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(DEFAULT_LENGTH, DEFAULT_LENGTH, START_LENGTH, DEFAULT_LENGTH + DEFAULT_HIGHT * filedNames.length);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JLabel label = null;
        JTextField textField = null;
        for (int i = 0; i < filedNames.length; i++) {
            label = new JLabel(filedNames[i]);
            label.setBounds(NUM_40, NUM_13 + (i * DEFAULT_HIGHT), NUM_80, NUM_15);
            panel.add(label);

            if (i == NUM_5) {
                textField = new JPasswordField();
            }
            else {
                textField = new JTextField();
            }

            textField.setBounds(NUM_120, NUM_13 + (i * DEFAULT_HIGHT), NUM_450, NUM_20);
            textFields[i] = textField;
            panel.add(textField);

            label = new JLabel("");
            label.setBounds(NUM_580, NUM_13 + (i * DEFAULT_HIGHT), NUM_150, NUM_15);
            label.setForeground(Color.RED);
            tips[i] = label;
            panel.add(label);
        }

        setDefaultValue();

        JButton button = new JButton("执行");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                try {
                    export();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        button.setBounds(NUM_145, NUM_20 + DEFAULT_HIGHT * filedNames.length, NUM_93, NUM_23);
        panel.add(button);

        textFields[NUM_6].setEditable(false);
        textFields[NUM_5].addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(final KeyEvent e) {
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                encryptPwd();
            }

            @Override
            public void keyPressed(final KeyEvent e) {
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }

    private void encryptPwd() {
        textFields[NUM_6].setText("ENC(" + DataUtil.encrypt(textFields[NUM_6].getText()) + ")");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     *         <br>
     */
    private void setDefaultValue() {
        Map<String, ?> configParam = getConfigParam();
        int i = 0;
        tips[i++].setText((String) configParam.get("tableNameText"));
        textFields[i++].setText((String) configParam.get("package"));
        textFields[i++].setText((String) configParam.get("dirstr"));
        textFields[i++].setText((String) configParam.get("jdbcUrl"));
        textFields[i++].setText((String) configParam.get("username"));
        textFields[i].setText((String) configParam.get("password"));
        encryptPwd();
    }

    private void saveConfig() {
        Map<String, String> paramMap = new HashMap<String, String>();
        int i = 0;
        paramMap.put("tableNameText", tips[i++].getText());
        paramMap.put("package", textFields[i++].getText());
        paramMap.put("dirstr", textFields[i++].getText());
        paramMap.put("jdbcUrl", textFields[i++].getText());
        paramMap.put("username", textFields[i++].getText());
        paramMap.put("password", textFields[i++].getText());
        String content = JSONObject.toJSONString(paramMap);
        try {
            IOUtil.writeFile(content,
                new File(System.getProperties().getProperty("user.home") + "/frameworkCache/config.cfg"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, ?> getConfigParam() {
        File file = new File(System.getProperties().getProperty("user.home"), "frameworkCache");
        if (!file.exists()) {
            file.mkdirs();
        }

        File f = new File(file, "config.cfg");
        if (f.exists()) {
            try {
                String content = IOUtil.readFile(f);
                if (StringUtils.isNotEmpty(content)) {
                    return JSONObject.parseObject(content);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("tableNameText", "表名不填则导出所有的表");
        paramMap.put("package", "com.hbasesoft.framework.bootstrap.bean");
        String classPath = this.getClass().getClassLoader().getResource("").getPath();
        paramMap.put("dirstr", StringUtils.replace(StringUtils.replace(classPath, "/target/classes", "/src/main/java"),
            "/target/test-classes", "/src/main/java"));
        paramMap.put("jdbcUrl", "jdbc:mysql://ranyinfo.com:3306/web?useUnicode=true&characterEncoding=UTF-8");
        paramMap.put("username", "root");
        paramMap.put("password", "ranyinfo.com");
        return paramMap;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws Exception <br>
     */
    public void export() throws Exception {

        saveConfig();
        String tablename = textFields[0].getText();
        String packname = textFields[1].getText();
        String dirstr = textFields[2].getText(); // 空表示当前目录

        DruidDataSource dbs = new DruidDataSource();
        dbs.setUrl(textFields[NUM_3].getText());
        dbs.setUsername(textFields[NUM_4].getText());
        dbs.setPassword(textFields[NUM_5].getText());
        dbs.setValidationQuery("select 1");
        dbs.init();
        dataSource = dbs;

        if (StringUtils.isEmpty(dirstr)) {
            tips[2].setText("给你导到根目录去了");
            dirstr = GlobalConstants.BLANK;
        }
        else {
            tips[2].setText(GlobalConstants.BLANK);
        }

        if (StringUtils.isEmpty(packname)) {
            tips[1].setText("w靠，你居然不写包名");
            packname = GlobalConstants.BLANK;
        }
        else {
            tips[1].setText(GlobalConstants.BLANK);
        }

        try {
            if (StringUtils.isEmpty(tablename)) {
                parseAllTable(packname, dirstr);
            }
            else {
                export(packname, dirstr, tablename);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (dataSource != null) {
                dataSource.close();
            }
        }

    }

    /**
     * Description: 开始处理生成所有表 如果不传入表名，表示将数据库中所有表生成bean; 可以指定表名生成bean;<br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param packname <br>
     * @param outputdir <br>
     * @throws Exception <br>
     */
    public void parseAllTable(final String packname, final String outputdir) throws Exception {
        ResultSet rs = null;
        try {
            DruidPooledConnection conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(conn.getCatalog(), null, null, new String[] {
                "TABLE"
            });
            while (rs.next()) {
                export(packname, outputdir, rs.getString("TABLE_NAME"));
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void export(final String packname, final String dirstr, final String tablename) {

        File dir = new File(dirstr + "/" + StringUtils.replace(packname, ".", "/"));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String outputdir = dir.getAbsolutePath(); // bean的生成目录

        TableInfo info = TableParseUtil.parse(tablename, dataSource);
        DbGeneratorUtil dbGeneratorUtil = new DbGeneratorUtil(packname, outputdir);
        dbGeneratorUtil.generateDao(info);
        dbGeneratorUtil.generateEntity(info);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param args <br>
     */
    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        DBTable2JavaBean frame = new DBTable2JavaBean();
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
