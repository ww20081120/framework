/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.jdbc;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.CounterGroup;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.BatchSizeSupported;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.AbstractSink;

import com.google.common.base.Preconditions;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.log.flume.core.EventSerializer;
import com.hbasesoft.framework.log.flume.jdbc.client.JdbcClient;
import com.hbasesoft.framework.log.flume.jdbc.client.JdbcClientFactory;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 18, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.jdbc <br>
 */
public class JdbcSink extends AbstractSink implements Configurable, BatchSizeSupported {

    /** */
    private static final int DEFAULT_BATCH_SIZE = 100;

    /** */
    private int batchSize = DEFAULT_BATCH_SIZE;

    /** */
    private boolean rollback = true;

    /** */
    private String datasourceCode = JdbcSinkConstants.DEFAULT_DATASOURCE_CODE;

    /** */
    private String tableName = JdbcSinkConstants.DEFAULT_TABLE_NAME;

    /** */
    private Context clientContext = null;

    /** */
    private EventSerializer eventSerializer;

    /** */
    private SinkCounter sinkCounter;

    /** */
    private JdbcClient client = null;

    /** */
    private final CounterGroup counterGroup = new CounterGroup();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return
     * @throws EventDeliveryException <br>
     */
    @Override
    public Status process() throws EventDeliveryException {
        LoggerUtil.debug("processing...");
        Status status = Status.READY;
        Channel channel = getChannel();
        Transaction txn = channel.getTransaction();
        try {
            txn.begin();
            int count;
            for (count = 0; count < batchSize; ++count) {
                Event event = channel.take();
                if (event == null) {
                    break;
                }
                client.addEvent(event);
            }

            if (count <= 0) {
                sinkCounter.incrementBatchEmptyCount();
                counterGroup.incrementAndGet("channel.underflow");
                status = Status.BACKOFF;
            }
            else {
                if (count < batchSize) {
                    sinkCounter.incrementBatchUnderflowCount();
                    status = Status.BACKOFF;
                }
                else {
                    sinkCounter.incrementBatchCompleteCount();
                }
                sinkCounter.addToEventDrainAttemptCount(count);
                client.execute();
            }
            txn.commit();
            sinkCounter.addToEventDrainSuccessCount(count);
            counterGroup.incrementAndGet("transaction.success");
        }
        catch (Throwable e) {
            if (rollback) {
                try {
                    txn.rollback();
                    counterGroup.incrementAndGet("transaction.rollback");
                }
                catch (Exception ex2) {
                    LoggerUtil.error("Exception in rollback. Rollback might not have been successful.", ex2);
                }

                if (e instanceof Error || e instanceof RuntimeException) {
                    LoggerUtil.error("Failed to commit transaction. Transaction rolled back.", e);
                    throw new ServiceException(e);
                }
                else {
                    LoggerUtil.error("Failed to commit transaction. Transaction rolled back.", e);
                    throw new EventDeliveryException("Failed to commit transaction. Transaction rolled back.", e);
                }
            }
            else {
                txn.commit();
            }
        }
        finally {
            txn.close();
        }
        return status;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public long getBatchSize() {
        return batchSize;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void configure(final Context context) {

        if (StringUtils.isNotBlank(context.getString(JdbcSinkConstants.TABLE_NAME))) {
            this.tableName = context.getString(JdbcSinkConstants.TABLE_NAME);
        }

        if (StringUtils.isNotBlank(context.getString(JdbcSinkConstants.DATASOURCE_CODE))) {
            this.datasourceCode = context.getString(JdbcSinkConstants.DATASOURCE_CODE);
        }

        if (StringUtils.isNotBlank(context.getString(JdbcSinkConstants.BATCH_SIZE))) {
            this.batchSize = Integer.parseInt(context.getString(JdbcSinkConstants.BATCH_SIZE));
        }

        if (StringUtils.isNotBlank(context.getString(JdbcSinkConstants.ROLLBACK))) {
            this.rollback = !"false".equalsIgnoreCase(context.getString(JdbcSinkConstants.ROLLBACK));
        }

        clientContext = new Context();
        clientContext.putAll(context.getSubProperties(JdbcSinkConstants.CLIENT_PREFIX));

        String serializerClazz = JdbcSinkConstants.DEFAULT_SERIALIZER_CLASS;
        if (StringUtils.isNotBlank(context.getString(JdbcSinkConstants.SERIALIZER))) {
            serializerClazz = context.getString(JdbcSinkConstants.SERIALIZER);
        }

        Context serializerContext = new Context();
        serializerContext.putAll(context.getSubProperties(JdbcSinkConstants.SERIALIZER_PREFIX));

        try {
            @SuppressWarnings("unchecked")
            Class<? extends Configurable> clazz = (Class<? extends Configurable>) Class.forName(serializerClazz);
            Configurable serializer = clazz.newInstance();

            if (serializer instanceof EventSerializer) {
                eventSerializer = (EventSerializer) serializer;
                eventSerializer.configure(serializerContext);
            }
            else {
                throw new IllegalArgumentException(serializerClazz + " is not an ElasticSearchEventSerializer");
            }
        }
        catch (Exception e) {
            LoggerUtil.error("Could not instantiate event serializer.", e);
            throw new InitializationException(e);
        }

        if (sinkCounter == null) {
            sinkCounter = new SinkCounter(getName());
        }

        Preconditions.checkState(batchSize >= 1, JdbcSinkConstants.BATCH_SIZE + " must be greater than 0");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public synchronized void start() {
        JdbcClientFactory clientFactory = new JdbcClientFactory();

        LoggerUtil.info("Jdbc sink {} started");
        sinkCounter.start();

        try {
            client = clientFactory.getClient(tableName, datasourceCode, eventSerializer);
            client.configure(clientContext);
            sinkCounter.incrementConnectionCreatedCount();
        }
        catch (Exception e) {
            LoggerUtil.error(e);
            sinkCounter.incrementConnectionFailedCount();
            if (client != null) {
                client.close();
                sinkCounter.incrementConnectionClosedCount();
            }
        }

        super.start();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public synchronized void stop() {
        LoggerUtil.info("ElasticSearch sink {} stopping");
        if (client != null) {
            client.close();
        }
        sinkCounter.incrementConnectionClosedCount();
        sinkCounter.stop();
        super.stop();
    }

}
