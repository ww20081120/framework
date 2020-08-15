/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.beust.jcommander.JCommander;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.shell.core.util.AopTargetUtils;
import com.hbasesoft.framework.shell.core.util.GitUtil;
import com.hbasesoft.framework.shell.core.vo.AbstractOption;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年8月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.shell.core <br>
 */
public class Shell {

    private static boolean login = PropertyHolder.getBooleanProperty("security.login", true);

    private GitUtil gitUtil;

    public final boolean remote;

    public final PrintStream out;

    public final InputStream in;

    private Scanner scanner;

    private boolean exit;

    public Shell(InputStream in, PrintStream out, boolean remote) {
        this.out = out;
        this.in = in;
        this.exit = false;
        this.remote = remote;
    }

    public Shell(InputStream in, PrintStream out) {
        this(in, out, false);
    }

    @SuppressWarnings("rawtypes")
    public void run(String[] args) throws Exception {

        // 加载所有命令
        Map<String, CommandHandler> commandHolder = ContextHolder.getContext().getBeansOfType(CommandHandler.class);

        Map<String, Class<? extends AbstractOption>> paramsHolder = new HashMap<>();

        for (Entry<String, CommandHandler> entry : commandHolder.entrySet()) {
            paramsHolder.put(entry.getKey(), getCommandOptions(AopTargetUtils.getTarget(entry.getValue()).getClass()));
        }

        if (CommonUtil.isEmpty(args)) {
            try {
                scanner = new Scanner(in);

                if (login) {
                    login(scanner);
                }

               // out.print(">>> ");

                while (!exit && scanner.hasNextLine()) {
                    try {
                        String command = StringUtils.trim(scanner.nextLine());
                        if (StringUtils.isNotEmpty(command)) {
                            if (login) {
                                gitUtil.addLog(command);
                            }

                            String[] cmds = StringUtils.split(command);
                            execute(commandHolder, paramsHolder, cmds);
                        }
                    }
                    catch (Exception e) {
                        out.println(e.getMessage());
                    }
                    out.print(">>> ");
                }
            }
            finally {
                IOUtils.closeQuietly(scanner);
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
        else {
            out.println(DateUtil.getCurrentTimestamp() + ":>>> " + StringUtils.join(args, " "));
            if (login) {
                String c = args[0];
                int index = c.indexOf("@");
                Assert.isTrue(index > 3, "用户名密码不存在, 格式 jar -jar vcc-tools.jar 用户名:密码@命令 -命令参数 ");
                String up = c.substring(0, index);
                String[] ups = StringUtils.split(up, ":");
                Assert.isTrue(ups.length == 2, "用户名密码不存在, 格式 jar -jar vcc-tools.jar 用户名:密码@命令 -命令参数 ");
                login(ups[0], ups[1]);
                args[0] = c.substring(index + 1);
            }

            gitUtil.addSyncLog(StringUtils.join(args, " "));

            execute(commandHolder, paramsHolder, args);
            out.println(DateUtil.getCurrentTimestamp() + ":任务执行结束。");
        }
    }

    public void exit() {
        this.exit = true;
        IOUtils.closeQuietly(scanner);
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
        if (!remote) {
            System.exit(0);
        }
    }

    private void login(Scanner scanner) {

        String remoteLocation = PropertyHolder.getProperty("project.server.logserver");
        Assert.notEmpty(remoteLocation, "远程服务器地址未配置，请检查project.server.logserver配置项");

        File file = new File(
            PropertyHolder.getProperty("log.dir", System.getProperty("user.home") + "/log/hbasesoft/vcc-tools"));
        if (file.exists()) {
            deleteDir(file);
        }

        boolean flag = true;
        for (int i = 0; i < 3; i++) {
            out.println("请输入用户名");
            String username = scanner.next();
            out.println("请输入密码");
            String password = scanner.next();

            try {
                GitUtil util = new GitUtil(file.getAbsolutePath());
                util.login(username, password, remoteLocation);
                out.println("登录成功！");
                gitUtil = util;
                flag = false;
                break;
            }
            catch (Exception e) {
                out.println("登录失败!" + e.getMessage());
                if (file.exists()) {
                    deleteDir(file);
                }
            }
        }
        if (flag) {
            exit();
        }
    }

    private void login(String u, String p) {

        String remoteLocation = PropertyHolder.getProperty("project.server.logserver");
        Assert.notEmpty(remoteLocation, "远程服务器地址未配置，请检查project.server.logserver配置项");

        File file = new File(
            PropertyHolder.getProperty("log.dir", System.getProperty("user.home") + "/log/hbasesoft/vcc-tools"));
        if (file.exists()) {
            deleteDir(file);
        }

        try {
            GitUtil util = new GitUtil(file.getAbsolutePath());
            util.login(u, p, remoteLocation);
            out.println("登录成功！");
            gitUtil = util;
        }
        catch (Exception e) {
            out.println("登录失败!" + e.getMessage());
            if (file.exists()) {
                deleteDir(file);
            }
            exit();
        }

    }

    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    private void execute(Map<String, CommandHandler> commandHolder,
        Map<String, Class<? extends AbstractOption>> paramsHolder, String[] cmds) throws Exception {
        CommandHandler cmd = commandHolder.get(cmds[0]);
        if (cmd != null) {
            AbstractOption option = null;
            JCommander jCommander = null;
            Class<? extends AbstractOption> optionClass = paramsHolder.get(cmds[0]);
            if (optionClass != null) {
                option = optionClass.newInstance();

                if (cmds.length > 1) {
                    String[] newCmds = Arrays.copyOfRange(cmds, 1, cmds.length);
                    for (int i = 0; i < newCmds.length; i++) {
                        newCmds[i] = StringUtils.replace(newCmds[i], "\\s", " ");
                    }
                    jCommander = new JCommander(option);
                    jCommander.setProgramName(cmd.toString());
                    jCommander.parse(newCmds);
                }
            }
            if (option.isHelp()) {
                jCommander.usage();
            }
            else {
                cmd.execute(jCommander, option, this);
            }
        }
        else {
            try {
                Process process = Runtime.getRuntime().exec(StringUtils.join(cmds, " "));
                IOUtils.copy(process.getInputStream(), out);
            }
            catch (IOException e) {
                out.println("未找到" + cmds[0] + "命令");
            }
            catch (Exception e) {
                out.println(e.getMessage());
            }
        }
    }

    private void deleteDir(File dir) {
        try {
            Runtime.getRuntime().exec("rm -rf " + dir.getAbsolutePath());
        }
        catch (Exception e) {
            out.println("删除目录失败" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends AbstractOption> getCommandOptions(Class<?> clazz) {
        Type[] interfacesTypes = clazz.getGenericInterfaces();
        for (Type t : interfacesTypes) {
            Type[] genericType2 = ((ParameterizedType) t).getActualTypeArguments();
            if (CommonUtil.isNotEmpty(genericType2)) {
                return (Class<? extends AbstractOption>) genericType2[0];
            }
        }
        return null;
    }

}
