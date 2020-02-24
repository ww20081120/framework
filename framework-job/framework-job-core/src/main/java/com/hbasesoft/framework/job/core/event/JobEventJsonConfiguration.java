package com.hbasesoft.framework.job.core.event;

import java.io.Serializable;

import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.JobEventListener;
import com.dangdang.ddframe.job.event.JobEventListenerConfigurationException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <Description> 作业Json事件配置.<br>
 * 
 * @author 大刘杰<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.core.event <br>
 */
@RequiredArgsConstructor
@Getter
public final class JobEventJsonConfiguration extends JobEventJsonIdentity
    implements JobEventConfiguration, Serializable {

    private static final long serialVersionUID = 3344410699286435226L;

    // private final transient DataSource dataSource; TODO 定义输出源头

    @Override
    public JobEventListener createJobEventListener() throws JobEventListenerConfigurationException {
        try {
            // return new JobEventJsonListener(dataSource);
            return new JobEventJsonListener();
        }
        catch (final Exception ex) {
            throw new JobEventListenerConfigurationException(ex);
        }
    }
}
