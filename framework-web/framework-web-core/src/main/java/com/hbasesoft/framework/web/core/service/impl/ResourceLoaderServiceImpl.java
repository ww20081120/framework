/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.cache.core.CacheException;
import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.web.core.WebConstant;
import com.hbasesoft.framework.web.core.annotation.Function;
import com.hbasesoft.framework.web.core.bean.Module;
import com.hbasesoft.framework.web.core.bean.UrlResource;
import com.hbasesoft.framework.web.core.init.StartupServlet;
import com.hbasesoft.framework.web.core.service.ResourceLoaderService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年1月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.core.service.impl <br>
 */
public class ResourceLoaderServiceImpl implements ResourceLoaderService {

    private static Logger logger = new Logger(ResourceLoaderServiceImpl.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param module
     * @throws ServiceException <br>
     */
    @Override
    public void loadUrlResouce(Module module) throws ServiceException {

        List<String> packages = module.getControllerPath();
        try {
            if (CommonUtil.isNotEmpty(packages)) {
                for (String pack : packages) {
                    Set<Class<?>> clazzSet = BeanUtil.getClasses(pack);
                    if (CommonUtil.isNotEmpty(clazzSet)) {
                        for (Class<?> clazz : clazzSet) {
                            if (clazz.isAnnotationPresent(Controller.class)) {
                                String[] baseUrl = new String[] {
                                    GlobalConstants.BLANK
                                };
                                RequestMapping requestMapping = AnnotationUtils.findAnnotation(clazz,
                                    RequestMapping.class);
                                if (requestMapping != null) {
                                    baseUrl = requestMapping.value();
                                }

                                Method[] methods = clazz.getDeclaredMethods();
                                for (Method method : methods) {
                                    RequestMapping methodRequestMapping = null;
                                    if ((method.getModifiers() & Method.PUBLIC) == Method.PUBLIC
                                        && (methodRequestMapping = AnnotationUtils.findAnnotation(method,
                                            RequestMapping.class)) != null) {
                                        Function fun = AnnotationUtils.findAnnotation(method, Function.class);
                                        resourceCache(module.getModuleCode(),
                                            fun == null ? WebConstant.ALL_IN_ONE_PERMISSION : fun.value(), baseUrl,
                                            methodRequestMapping.value(), methodRequestMapping.method(),
                                            fun == null ? null : fun.event());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (UtilException e) {
            throw new ServiceException(e);
        }
        catch (CacheException e) {
            throw new ServiceException(e);
        }
    }

    private void copyResource(String resourcePath) throws ServiceException {
        File distDir = new File(StartupServlet.getContext().getRealPath("/"));
        if (!distDir.exists()) {
            distDir.mkdirs();
        }
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(resourcePath);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    copyFile(new File(url.getFile()), distDir);
                }
                else if ("jar".equals(protocol)) {
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (StringUtils.startsWith(entry.getName(), resourcePath)) {
                            copyJarFile(resourcePath, entry, distDir);
                        }
                    }

                }
            }

        }
        catch (UtilException e) {
            throw new ServiceException(ErrorCodeDef.FILE_NOT_FIND_20013, "资源文件读取失败{0}", resourcePath, e);
        }
        catch (IOException e) {
            throw new ServiceException(ErrorCodeDef.FILE_NOT_FIND_20013, "资源文件读取失败{0}", resourcePath, e);
        }

    }

    private void copyJarFile(String resourcePath, JarEntry jarEntry, File distPath) throws UtilException {
        String fileName = jarEntry.getName().substring(resourcePath.length());
        File objFile = new File(distPath, fileName);

        if (jarEntry.isDirectory()) {
            if (!objFile.exists()) {
                objFile.mkdirs();
            }
        }
        else {
            if (objFile.exists()) {
                return;
            }

            IOUtil.copyFileFromInputStream(objFile.getAbsolutePath(),
                Thread.currentThread().getContextClassLoader().getResourceAsStream(jarEntry.getName()));
            logger.info("复制文件[{0}]成功", objFile.getAbsolutePath());
        }
    }

    private void copyFile(File srcPath, File distPath) throws UtilException {
        if (srcPath.isDirectory()) {
            if (!distPath.exists()) {
                distPath.mkdirs();
            }

            File[] list = srcPath.listFiles();
            for (File s : list) {
                copyFile(s, new File(distPath, s.getName()));
            }
        }
        else {
            if (distPath.exists()) {
                return;
            }
            IOUtil.copyFile(srcPath, distPath);
        }
    }

    private void resourceCache(String moduleCode, String permissionCode, String[] baseUrls, String[] urls,
        RequestMethod[] methods, String[] eventCodes) throws CacheException {
        Set<String> urlSet = new HashSet<String>();
        for (String baseUrl : baseUrls) {
            for (String url : urls) {
                urlSet.add(StringUtils.replace(StringUtils.join(new String[] {
                    baseUrl, url
                }, GlobalConstants.PATH_SPLITOR), "//", GlobalConstants.PATH_SPLITOR));
            }
        }

        String cacheKey = moduleCode + GlobalConstants.PERIOD + permissionCode;

        for (String url : urlSet) {
            UrlResource urlResource = new UrlResource();
            urlResource.setEvents(eventCodes);
            urlResource.setMethods(methods);
            urlResource.setUrl(url);
            urlResource.setFunctionCode(cacheKey);
            CacheHelper.getCache().putValue(CacheConstant.URL_RESOURCE, cacheKey, urlResource);
            logger.info("     设置UrlResource [{0}] 成功", urlResource);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param module
     * @throws ServiceException <br>
     */
    @Override
    public void loadStaticResource(Module module) throws ServiceException {
        List<String> resources = module.getResources();
        if (CommonUtil.isNotEmpty(resources)) {
            for (String resource : resources) {
                copyResource(resource);
            }
        }
    }
}
