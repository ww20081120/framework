/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.elasticsearch;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.CounterGroup;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.BatchSizeSupported;
import org.apache.flume.conf.Configurable;
import org.apache.flume.formatter.output.BucketPath;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.AbstractSink;

import com.google.common.base.Preconditions;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.log.flume.core.EventSerializer;
import com.hbasesoft.framework.log.flume.elasticsearch.client.ElasticSearchClient;
import com.hbasesoft.framework.log.flume.elasticsearch.client.ElasticSearchClientFactory;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 18, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.elasticsearch <br>
 */
public class ElasticSearchSink extends AbstractSink implements Configurable, BatchSizeSupported {

    /** */
    private static final int NUM7 = 7;

    /** */
    private static final int DEFAULT_BATCH_SIZE = 100;

    /** */
    private int batchSize = DEFAULT_BATCH_SIZE;

    /** */
    private Address[] serverAddresses = null;

    /** */
    private String indexName = ElasticSearchSinkConstants.DEFAULT_INDEX_NAME;

    /** */
    private String indexType = ElasticSearchSinkConstants.DEFAULT_INDEX_TYPE;

    /** */
    private String clientType = ElasticSearchSinkConstants.DEFAULT_CLIENT_TYPE;

    /** */
    private String clusterName = ElasticSearchSinkConstants.DEFAULT_CLUSTER_NAME;

    /** */
    private long ttlMs = ElasticSearchSinkConstants.DEFAULT_TTL;

    /** */
    private final Pattern pattern = Pattern.compile(ElasticSearchSinkConstants.TTL_REGEX, Pattern.CASE_INSENSITIVE);

    /** */
    private Matcher matcher = pattern.matcher("");

    /** */
    private Context elasticSearchClientContext = null;

    /** */
    private EventSerializer eventSerializer;

    /** */
    private SinkCounter sinkCounter;

    /** */
    private IndexNameBuilder indexNameBuilder;

    /** */
    private ElasticSearchClient client = null;

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

                String realIndexType = BucketPath.escapeString(indexType, event.getHeaders());
                client.addEvent(event, indexNameBuilder, realIndexType, ttlMs);
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
        if (StringUtils.isNotBlank(context.getString(ElasticSearchSinkConstants.HOSTNAMES))) {
            serverAddresses = ProtocolUtil.parseAddress(context.getString(ElasticSearchSinkConstants.HOSTNAMES));
            Preconditions.checkState(serverAddresses != null && serverAddresses.length > 0,
                "Missing Param:" + ElasticSearchSinkConstants.HOSTNAMES);
        }

        if (StringUtils.isNotBlank(context.getString(ElasticSearchSinkConstants.INDEX_NAME))) {
            this.indexName = context.getString(ElasticSearchSinkConstants.INDEX_NAME);
        }

        if (StringUtils.isNotBlank(context.getString(ElasticSearchSinkConstants.INDEX_TYPE))) {
            this.indexType = context.getString(ElasticSearchSinkConstants.INDEX_TYPE);
        }

        if (StringUtils.isNotBlank(context.getString(ElasticSearchSinkConstants.CLUSTER_NAME))) {
            this.clusterName = context.getString(ElasticSearchSinkConstants.CLUSTER_NAME);
        }

        if (StringUtils.isNotBlank(context.getString(ElasticSearchSinkConstants.BATCH_SIZE))) {
            this.batchSize = Integer.parseInt(context.getString(ElasticSearchSinkConstants.BATCH_SIZE));
        }

        if (StringUtils.isNotBlank(context.getString(ElasticSearchSinkConstants.TTL))) {
            this.ttlMs = parseTTL(context.getString(ElasticSearchSinkConstants.TTL));
            Preconditions.checkState(this.ttlMs > 0,
                ElasticSearchSinkConstants.TTL + " must be greater than 0 or not set.");
        }

        if (StringUtils.isNotBlank(context.getString(ElasticSearchSinkConstants.CLIENT_TYPE))) {
            clientType = context.getString(ElasticSearchSinkConstants.CLIENT_TYPE);
        }

        elasticSearchClientContext = new Context();
        elasticSearchClientContext.putAll(context.getSubProperties(ElasticSearchSinkConstants.CLIENT_PREFIX));

        String serializerClazz = ElasticSearchSinkConstants.DEFAULT_SERIALIZER_CLASS;
        if (StringUtils.isNotBlank(context.getString(ElasticSearchSinkConstants.SERIALIZER))) {
            serializerClazz = context.getString(ElasticSearchSinkConstants.SERIALIZER);
        }

        Context serializerContext = new Context();
        serializerContext.putAll(context.getSubProperties(ElasticSearchSinkConstants.SERIALIZER_PREFIX));

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

        String indexNameBuilderClass = ElasticSearchSinkConstants.DEFAULT_INDEX_NAME_BUILDER_CLASS;
        if (StringUtils.isNotBlank(context.getString(ElasticSearchSinkConstants.INDEX_NAME_BUILDER))) {
            indexNameBuilderClass = context.getString(ElasticSearchSinkConstants.INDEX_NAME_BUILDER);
        }

        Context indexnameBuilderContext = new Context();
        serializerContext.putAll(context.getSubProperties(ElasticSearchSinkConstants.INDEX_NAME_BUILDER_PREFIX));

        try {
            @SuppressWarnings("unchecked")
            Class<? extends IndexNameBuilder> clazz = (Class<? extends IndexNameBuilder>) Class
                .forName(indexNameBuilderClass);
            indexNameBuilder = clazz.newInstance();
            indexnameBuilderContext.put(ElasticSearchSinkConstants.INDEX_NAME, indexName);
            indexNameBuilder.configure(indexnameBuilderContext);
        }
        catch (Exception e) {
            LoggerUtil.error("Could not instantiate index name builder.", e);
            throw new InitializationException(e);
        }

        Preconditions.checkState(StringUtils.isNotBlank(indexName),
            "Missing Param:" + ElasticSearchSinkConstants.INDEX_NAME);
        Preconditions.checkState(StringUtils.isNotBlank(indexType),
            "Missing Param:" + ElasticSearchSinkConstants.INDEX_TYPE);
        Preconditions.checkState(StringUtils.isNotBlank(clusterName),
            "Missing Param:" + ElasticSearchSinkConstants.CLUSTER_NAME);
        Preconditions.checkState(batchSize >= 1, ElasticSearchSinkConstants.BATCH_SIZE + " must be greater than 0");
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
        ElasticSearchClientFactory clientFactory = new ElasticSearchClientFactory();

        LoggerUtil.info("ElasticSearch sink {} started");
        sinkCounter.start();

        try {
            client = clientFactory.getClient(clientType, serverAddresses, clusterName, eventSerializer);
            client.configure(elasticSearchClientContext);
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

    private long parseTTL(final String ttl) {
        matcher = matcher.reset(ttl);
        while (matcher.find()) {
            if (matcher.group(2).equals("ms")) {
                return Long.parseLong(matcher.group(1));
            }
            else if (matcher.group(2).equals("s")) {
                return TimeUnit.SECONDS.toMillis(Integer.parseInt(matcher.group(1)));
            }
            else if (matcher.group(2).equals("m")) {
                return TimeUnit.MINUTES.toMillis(Integer.parseInt(matcher.group(1)));
            }
            else if (matcher.group(2).equals("h")) {
                return TimeUnit.HOURS.toMillis(Integer.parseInt(matcher.group(1)));
            }
            else if (matcher.group(2).equals("d")) {
                return TimeUnit.DAYS.toMillis(Integer.parseInt(matcher.group(1)));
            }
            else if (matcher.group(2).equals("w")) {
                return TimeUnit.DAYS.toMillis(NUM7 * Integer.parseInt(matcher.group(1)));
            }
            else if (matcher.group(2).equals("")) {
                LoggerUtil.info("TTL qualifier is empty. Defaulting to day qualifier.");
                return TimeUnit.DAYS.toMillis(Integer.parseInt(matcher.group(1)));
            }
            else {
                LoggerUtil.debug("Unknown TTL qualifier provided. Setting TTL to 0.");
                return 0;
            }
        }
        LoggerUtil.info("TTL not provided. Skipping the TTL config by returning 0.");
        return 0;
    }
}
