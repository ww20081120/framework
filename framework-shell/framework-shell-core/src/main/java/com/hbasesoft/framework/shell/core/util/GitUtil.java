/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.hbasesoft.framework.common.utils.date.DateUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jul 30, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.tools.util <br>
 */
public class GitUtil {

    /** */
    private static final String REMOTE_NAME = "origin";

    /** */
    private static final String LOG_FILE_NAME = "logfile";

    /** */
    private Git git;

    /** */
    private CredentialsProvider credentialsProvider;

    /** */
    private String repo;

    /** */
    private LinkedBlockingDeque<String> logQueue = new LinkedBlockingDeque<>();

    /** */
    private static final int NUM5 = 5;

    /** */
    private static final int NUM1000 = 1000;

    /** */
    private Thread sendLogThread = new Thread() {
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    String msg = logQueue.poll(NUM5, TimeUnit.MILLISECONDS);
                    if (StringUtils.isNotEmpty(msg)) {
                        git.pull().setCredentialsProvider(credentialsProvider).call();
                        try (PrintWriter out = new PrintWriter(new FileWriter(new File(repo, LOG_FILE_NAME)))) {
                            out.append(DateUtil.getCurrentTimestamp());
                        }
                        git.add().addFilepattern(LOG_FILE_NAME).call();
                        git.commit().setMessage(msg).call();
                        git.push().setCredentialsProvider(credentialsProvider).call();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(NUM1000);
                    }
                    catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

            }

        }
    };

    /**
     * @Method GitUtil
     * @param repo
     * @return
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:20
    */
    public GitUtil(final String repo) throws IOException, IllegalStateException, GitAPIException {
        this.repo = repo;
        File gitDir = new File(repo);
        if (!gitDir.exists()) {
            gitDir.mkdirs();
        }

        if (!new File(gitDir, ".git").exists()) {
            git = Git.init().setDirectory(gitDir).setBare(false).call();
        }
        else {
            git = Git.open(gitDir);
        }

    }

    /**
     * @Method login
     * @param username
     * @param password
     * @param remote
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:20
    */
    public void login(final String username, final String password, final String remote)
        throws InvalidRemoteException, TransportException, GitAPIException, URISyntaxException {

        // 设置证书
        credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);

        Optional<RemoteConfig> remoteRef = git.remoteList().call().stream().filter(r -> REMOTE_NAME.equals(r.getName()))
            .findFirst();

        URIish uri = new URIish(remote);
        if (remoteRef.isPresent()) {
            remoteRef.get().addURI(uri);
        }
        else {
            git.remoteAdd().setName(REMOTE_NAME).setUri(uri).call();
            git.pull().setCredentialsProvider(credentialsProvider).call();
        }

        // 判断本地有没有分支
        List<Ref> branchList = git.branchList().call();
        String localName = "refs/heads/" + username;
        Optional<Ref> ref = branchList.stream().filter(r -> localName.equals(r.getName())).findFirst();
        if (!ref.isPresent()) {
            git.branchCreate().setName(username).call();
            git.checkout().setName(username).call();
            String remoteName = "remotes/origin/" + username;
            if (git.branchList().setListMode(ListMode.REMOTE).call().stream()
                .filter(r -> remoteName.equals(r.getName())).findFirst().isPresent()) {
                git.pull().setCredentialsProvider(credentialsProvider).call();
            }
            else {
                git.push().setCredentialsProvider(credentialsProvider).call();
            }
        }
        else {
            git.checkout().setName(username).call();
            git.pull().setCredentialsProvider(credentialsProvider).call();
        }

        sendLogThread.start();
    }

    /**
     * @Method addLog
     * @param log
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:21
    */
    public void addLog(final String log) {
        logQueue.offer(log);
    }

    /**
     * @Method addSyncLog
     * @param log
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:21
    */
    public void addSyncLog(final String log) throws WrongRepositoryStateException, InvalidConfigurationException,
        InvalidRemoteException, CanceledException, RefNotFoundException, RefNotAdvertisedException, NoHeadException,
        TransportException, GitAPIException, IOException {

        git.pull().setCredentialsProvider(credentialsProvider).call();
        try (PrintWriter out = new PrintWriter(new FileWriter(new File(repo, LOG_FILE_NAME)))) {
            out.append(DateUtil.getCurrentTimestamp());
        }
        git.add().addFilepattern(LOG_FILE_NAME).call();
        git.commit().setMessage(log).call();
        git.push().setCredentialsProvider(credentialsProvider).call();
    }

}
