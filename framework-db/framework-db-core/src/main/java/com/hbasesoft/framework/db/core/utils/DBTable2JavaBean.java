package com.hbasesoft.framework.db.core.utils;

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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.date.DateConstants;
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
    private DataSource dataSource;

    /**
     * template
     */
    private String daoTemplate;

    private String entityTemplate;

    /**
     * dateFormat
     */
    private DateFormat dateFormat;

    /**
     * DBTable2JavaBean
     */
    public DBTable2JavaBean() {
        dateFormat = new SimpleDateFormat(DateConstants.DATE_FORMAT_11);

        textFields = new JTextField[filedNames.length];
        tips = new JLabel[filedNames.length];

        setResizable(false);
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 840, 100 + 30 * filedNames.length);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JLabel label = null;
        JTextField textField = null;
        for (int i = 0; i < filedNames.length; i++) {
            label = new JLabel(filedNames[i]);
            label.setBounds(40, 13 + (i * 30), 80, 15);
            panel.add(label);

            if (i == 5) {
                textField = new JPasswordField();
            }
            else {
                textField = new JTextField();
            }

            textField.setBounds(120, 13 + (i * 30), 450, 20);
            textFields[i] = textField;
            panel.add(textField);

            label = new JLabel("");
            label.setBounds(580, 13 + (i * 30), 150, 15);
            label.setForeground(Color.RED);
            tips[i] = label;
            panel.add(label);
        }

        setDefaultValue();

        JButton button = new JButton("执行");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    export();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        button.setBounds(145, 20 + 30 * filedNames.length, 93, 23);
        panel.add(button);

        textFields[6].setEditable(false);
        textFields[5].addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                encryptPwd();
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }

    private void encryptPwd() {
        textFields[6].setText("ENC(" + DataUtil.encrypt(textFields[6].getText()) + ")");
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

        tips[0].setText((String) configParam.get("tableNameText"));
        textFields[1].setText((String) configParam.get("package"));
        textFields[2].setText((String) configParam.get("dirstr"));
        textFields[3].setText((String) configParam.get("jdbcUrl"));
        textFields[4].setText((String) configParam.get("username"));
        textFields[5].setText((String) configParam.get("password"));
        encryptPwd();
    }

    private void saveConfig() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("tableNameText", tips[0].getText());
        paramMap.put("package", textFields[1].getText());
        paramMap.put("dirstr", textFields[2].getText());
        paramMap.put("jdbcUrl", textFields[3].getText());
        paramMap.put("username", textFields[4].getText());
        paramMap.put("password", textFields[5].getText());
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

        if (dataSource == null) {
            DruidDataSource dbs = new DruidDataSource();
            dbs.setUrl(textFields[3].getText());
            dbs.setUsername(textFields[4].getText());
            dbs.setPassword(textFields[5].getText());
            dbs.setValidationQuery("select 1");
            dbs.init();
            dataSource = dbs;
        }

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

        export(dirstr, packname, tablename);

    }

    private void export(String dirstr, String packname, String tablename) throws SQLException {

        File dir = new File(dirstr + "/" + StringUtils.replace(packname, ".", "/"));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String outputdir = dir.getAbsolutePath(); // bean的生成目录

        Connection conn = null;

        try {
            entityTemplate = IOUtil.readString(this.getClass().getClassLoader()
                .getResourceAsStream("com/hbasesoft/framework/db/core/utils/entityTemplate.vm"));
            daoTemplate = IOUtil.readString(this.getClass().getClassLoader()
                .getResourceAsStream("com/hbasesoft/framework/db/core/utils/daoTemplate.vm"));

            conn = dataSource.getConnection();
            if (StringUtils.isEmpty(tablename)) {
                parseAllTable(conn, packname, outputdir);
            }
            else {
                parseTableByShowCreate(conn, tablename, packname, outputdir);
            }

        }
        catch (Exception e) {
            dataSource = null;
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Description: 开始处理生成所有表 如果不传入表名，表示将数据库中所有表生成bean; 可以指定表名生成bean;<br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param conn <br>
     * @param packname <br>
     * @param outputdir <br>
     * @throws Exception <br>
     */
    public void parseAllTable(Connection conn, String packname, String outputdir) throws Exception {
        ResultSet rs = null;
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(conn.getCatalog(), null, null, new String[] {
                "TABLE"
            });

            while (rs.next()) {
                parseTableByShowCreate(conn, rs.getString("TABLE_NAME"), packname, outputdir);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * Description:通过 mysql的 show create table TABLE_NAME逆向生成Bean; <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param conn <br>
     * @param tablename <br>
     * @param packname <br>
     * @param outputdir <br>
     * @throws Exception <br>
     */
    public void parseTableByShowCreate(Connection conn, String tablename, String packname, String outputdir)
        throws Exception {
        String className = BeanUtil
            .toCapitalizeCamelCase(tablename.toUpperCase().startsWith("T_") ? tablename.substring(2) : tablename);
        tablename = StringUtils.upperCase(tablename);

        File dir = new File(outputdir + "/entity/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File entityFile = new File(dir, className + "Entity.java");
        if (entityFile.exists()) {
            System.out.println("文件已经存在，请删除后在生成。" + entityFile.getAbsoluteFile());
            return;
        }

        dir = new File(outputdir + "/dao/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File daoFile = new File(dir, className + "Dao.java");
        if (daoFile.exists()) {
            System.out.println("文件已经存在，请删除后在生成。" + daoFile.getAbsoluteFile());
            return;
        }

        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select * from " + tablename + " where 1 = 2");
            ResultSetMetaData metaData = ps.executeQuery().getMetaData();

            StringBuilder fields = new StringBuilder();
            StringBuilder methods = new StringBuilder();

            String pkColum = null;
            ResultSet rs = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), null, tablename);
            int index = 0;
            while (rs.next()) {
                if (index++ > 0) {
                    pkColum = null;
                    break;
                }
                pkColum = rs.getString(4);
            }

            for (int i = 0, size = metaData.getColumnCount(); i < size; i++) {
                String cmt = metaData.getColumnLabel(i + 1);
                String field = BeanUtil.toCamelCase(cmt);
                String type = typeTrans(metaData.getColumnTypeName(i + 1));
                fields.append('\n').append(getFieldStr(field, type, cmt, pkColum));
                methods.append('\n').append(getMethodStr(field, type));
            }

            IOUtil.writeFile(StringUtils.replaceEach(entityTemplate, new String[] {
                "${PACKAGE}", "${CLASSNAME}", "${CODE}", "${TABLENAME}", "${DATE}", "${ENTITY}"
            }, new String[] {
                packname, className, fields.append(methods).toString(), tablename, dateFormat.format(new Date()),
                StringUtils.isEmpty(pkColum) ? GlobalConstants.BLANK : "@Entity(name = \"" + tablename + "\")"
            }), entityFile);
            System.out.println("生成文件成功。" + entityFile.getAbsoluteFile());

            IOUtil.writeFile(StringUtils.replaceEach(daoTemplate, new String[] {
                "${PACKAGE}", "${CLASSNAME}", "${TABLENAME}", "${DATE}"
            }, new String[] {
                packname, className, tablename, dateFormat.format(new Date())
            }), daoFile);
            System.out.println("生成文件成功。" + daoFile.getAbsoluteFile());

        }
        finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param field <br>
     * @param type <br>
     * @return <br>
     */
    private String getMethodStr(String field, String type) {
        StringBuilder get = new StringBuilder('\n');
        get.append("    ").append("public ").append(type).append(" ");
        if ("boolean".equals(type)) {
            get.append(field);
        }
        else {
            get.append("get");
            get.append(upperFirestChar(field));
        }
        get.append("() {").append("\r\n        return this.").append(field).append(";\r\n    }\r\n");

        StringBuilder set = new StringBuilder("\n");
        set.append("    public void ");
        if ("boolean".equals(type)) {
            set.append(field);
        }
        else {
            set.append("set");
            set.append(upperFirestChar(field));
        }
        set.append("(").append(type).append(" ").append(field).append(") {\r\n        this.").append(field)
            .append(" = ").append(field).append(";\r\n    }\r\n");
        get.append(set);
        return get.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param src <br>
     * @return <br>
     */
    private String upperFirestChar(String src) {
        return src.substring(0, 1).toUpperCase().concat(src.substring(1));
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param field <br>
     * @param type <br>
     * @param cmt <br>
     * @param pkColum <br>
     * @return <br>
     */
    private String getFieldStr(String field, String type, String cmt, String pkColum) {
        StringBuilder sb = new StringBuilder('\n');
        sb.append("    ").append("/** ").append(cmt).append(" */").append('\n');
        if (cmt.equals(pkColum)) {
            sb.append("    ").append("@Id").append('\n');
            sb.append("    ").append("@GeneratedValue(generator = \"paymentableGenerator\")").append('\n');
            sb.append("    ").append("@GenericGenerator(name = \"paymentableGenerator\", strategy = \"uuid\")")
                .append('\n');
        }
        sb.append("    ").append("@Column(name = \"").append(cmt).append("\")").append('\n');
        sb.append("    ").append("private ").append(type).append(" ").append(field).append(';').append('\n');
        return sb.toString();
    }

    /**
     * Description: mysql的类型转换到java 类型参考文章 http://hi.baidu.com/wwtvanessa/blog/item/9fe555945a07bd16d31b70cd.html<br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param type <br>
     * @return <br>
     */
    private String typeTrans(String type) {
        if (type.contains("INT")) {
            return "Integer";
        }
        else if ("LONG".equals(type) || "NUMBER".equals(type) || "BIGINT".equals(type)) {
            return "Long";
        }
        else if ("FLOAT".equals(type) || "DOUBLE".equals(type)) {
            return "Double";
        }
        else if ("DATE".equals(type) || "TIME".equals(type) || "DATETIME".equals(type) || "TIMESTAMP".equals(type)) {
            return "java.util.Date";
        }
        else if (type.contains("BINARY") || type.contains("BLOB")) {
            return "byte[]";
        }
        else {
            return "String";
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param args <br>
     */
    public static void main(String[] args) {
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
