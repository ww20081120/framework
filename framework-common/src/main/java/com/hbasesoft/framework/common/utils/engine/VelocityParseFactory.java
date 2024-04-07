/**
 * 
 */
package com.hbasesoft.framework.common.utils.engine;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ServiceLoader;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.security.DataUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月28日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VelocityParseFactory {

    /** 最大名称长度 */
    private static final int MAX_NAME_LENGTH = 16;

    /** v length */
    private static final int V_LENGTH = "velocity.".length();

    /**
     * properties
     */
    private static Properties properties;

    /**
     * logger
     */
    private static Logger logger = new Logger(VelocityParseFactory.class);

    static {

        try {
            properties = new Properties();
            properties.setProperty("runtime.log.error.stacktrace", "false");
            properties.setProperty("runtime.log.warn.stacktrace", "false");
            properties.setProperty("runtime.log.info.stacktrace", "false");
            properties.setProperty("runtime.log.logsystem.class",
                "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            properties.setProperty("runtime.log.logsystem.log4j.category", "velocity_log");
            properties.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
            properties.setProperty("string.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
            properties.setProperty("input.encoding", GlobalConstants.DEFAULT_CHARSET);
            properties.setProperty("output.encoding", GlobalConstants.DEFAULT_CHARSET);

            ServiceLoader<UserDirective> loader = ServiceLoader.load(UserDirective.class);
            if (loader != null) {
                StringBuilder sb = new StringBuilder();
                loader.forEach(directive -> {
                    sb.append(directive.getClass().getName()).append(',');
                });
                if (sb.length() > 0) {
                    properties.setProperty("userdirective", sb.substring(0, sb.length() - 1));
                }
            }

            for (Entry<String, String> entry : PropertyHolder.getProperties().entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("velocity.")) {
                    properties.setProperty(key.substring(V_LENGTH), entry.getValue());
                }
            }

            Velocity.init(properties);
        }
        catch (Exception e) {
            logger.error("初始化Velocity模板失败", e);
        }
    }

    /**
     * templateName
     * 
     * @param templateName templateName
     * @param body body
     * @param params params
     * @return String
     * @throws UtilException UtilException
     */
    @Deprecated
    public static String parse(final String templateName, final String body, final Map<String, ?> params) {
        return parse(null, body, params);
    }

    /**
     * templateName
     * 
     * @param body body
     * @param params params
     * @return String
     * @throws UtilException UtilException
     */
    public static String parse(final String body, final Map<String, ?> params) {
        if (StringUtils.isEmpty(body)) {
            return GlobalConstants.BLANK;
        }

        String templateName = body.length() > MAX_NAME_LENGTH ? DataUtil.md5(body) : body;

        VelocityContext context = new VelocityContext();
        if (MapUtils.isNotEmpty(params)) {
            for (Entry<String, ?> entry : params.entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
        }
        StringResourceRepository repository = StringResourceLoader.getRepository();
        repository.putStringResource(templateName, body);

        StringBuilder sb = new StringBuilder();
        try (Writer writer = new BufferedWriter(new StringBuilderWriter(sb))) {
            Template template = Velocity.getTemplate(templateName, GlobalConstants.DEFAULT_CHARSET);
            template.merge(context, writer);
            writer.flush();
            return sb.toString();
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.PARSE_TEPLATE_ERROR, e);
        }
    }
}
