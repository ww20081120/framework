/**
 * 
 */
package com.fccfc.framework.web.mapping;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.web.service.MappingService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月17日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.mapping <br>
 */
public class DatabaseHandlerMethodMapping extends RequestMappingHandlerMapping {

    private static Logger logger = new Logger(DatabaseHandlerMethodMapping.class);

    @Resource
    private MappingService mappingService;

    /*
     * (non-Javadoc)
     * @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping# isHandler(java.lang.Class)
     */
    @Override
    protected boolean isHandler(Class<?> beanType) {
        List<String> resourceCache;

        try {
            resourceCache = mappingService.selectAllUrlResource();
        }
        catch (ServiceException e) {
            logger.error("获取URL资源失败", e);
            throw new RuntimeException("获取URL资源失败", e);
        }

        boolean isHandler = resourceCache.contains(beanType);
        if (!isHandler) {
            Class<?> superClazz = beanType.getSuperclass();
            if (superClazz != null) {
                isHandler = resourceCache.contains(superClazz.getName());
            }
        }

        if (isHandler) {
            logger.info("================>{0} is Handler", beanType.getName());
        }

        return isHandler;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#
     * getMappingForMethod(java.lang.reflect.Method , java.lang.Class)
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {

        RequestMappingInfo info = null;
        String url;

        try {
            url = mappingService.getMethodUrl(handlerType.getName(), method.getName());
        }
        catch (ServiceException e) {
            logger.error("获取URL资源项失败", e);
            throw new RuntimeException("获取URL资源项失败", e);
        }

        if (CommonUtil.isNotEmpty(url)) {
            RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
            RequestCondition<?> methodCondition = getCustomMethodCondition(method);
            info = createRequestMappingInfo(new String[] {
                url
            }, methodAnnotation, methodCondition);
            RequestMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
            if (typeAnnotation != null) {
                RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);
                info = createRequestMappingInfo(new String[] {
                    GlobalConstants.BLANK
                }, typeAnnotation, typeCondition).combine(info);

            }
            logger.info("success set class[{0}].<[{1}]> url[{2}] resource [{3}]", handlerType.getName(),
                method.getName(), url, info);
        }

        return info;
    }

    protected RequestMappingInfo createRequestMappingInfo(String[] value, RequestMapping annotation,
        RequestCondition<?> customCondition) {
        String[] patterns = resolveEmbeddedValuesInPatterns(value);
        return annotation == null ? new RequestMappingInfo(new PatternsRequestCondition(patterns, getUrlPathHelper(),
            getPathMatcher(), useSuffixPatternMatch(), useTrailingSlashMatch(), getFileExtensions()), null, null, null,
            null, null, customCondition)

        : new RequestMappingInfo(annotation.name(), new PatternsRequestCondition(patterns, getUrlPathHelper(),
            getPathMatcher(), useSuffixPatternMatch(), useTrailingSlashMatch(), getFileExtensions()),
            new RequestMethodsRequestCondition(annotation.method()), new ParamsRequestCondition(annotation.params()),
            new HeadersRequestCondition(annotation.headers()), new ConsumesRequestCondition(annotation.consumes(),
                annotation.headers()), new ProducesRequestCondition(annotation.produces(), annotation.headers(),
                getContentNegotiationManager()), customCondition);
    }
}
