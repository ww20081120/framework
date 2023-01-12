/**
 * 
 */
package com.hbasesoft.framework.common.utils.engine;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
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

        // dateTool = new DateTool();
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
    public static String parse(final String templateName, final String body, final Map<String, ?> params)
        throws UtilException {
        VelocityContext context = new VelocityContext();
        // context.put("dateTool", dateTool);
        if (MapUtils.isNotEmpty(params)) {
            for (Entry<String, ?> entry : params.entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
        }

        StringResourceRepository repository = StringResourceLoader.getRepository();
        repository.putStringResource(templateName, body);

        ByteArrayOutputStream out = null;
        try {
            Template template = Velocity.getTemplate(templateName, GlobalConstants.DEFAULT_CHARSET);
            out = new ByteArrayOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, GlobalConstants.DEFAULT_CHARSET));
            template.merge(context, writer);
            writer.flush();
            return new String(out.toByteArray(), GlobalConstants.DEFAULT_CHARSET);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.PARSE_TEPLATE_ERROR, e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    throw new UtilException(ErrorCodeDef.PARSE_TEPLATE_ERROR, e);
                }
            }
        }
    }
}
