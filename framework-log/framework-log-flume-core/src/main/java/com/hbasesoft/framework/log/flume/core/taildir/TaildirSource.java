/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.core.taildir;

import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.BATCH_SIZE;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.BYTE_OFFSET_HEADER;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.CACHE_PATTERN_MATCHING;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.DEFAULT_BATCH_SIZE;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.DEFAULT_BYTE_OFFSET_HEADER;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.DEFAULT_CACHE_PATTERN_MATCHING;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.DEFAULT_FILENAME_HEADER_KEY;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.DEFAULT_FILE_HEADER;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.DEFAULT_IDLE_TIMEOUT;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.DEFAULT_MAX_BATCH_COUNT;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.DEFAULT_POSITION_FILE;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.DEFAULT_SKIP_TO_END;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.DEFAULT_WRITE_POS_INTERVAL;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.FILENAME_HEADER;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.FILENAME_HEADER_KEY;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.FILE_GROUPS;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.FILE_GROUPS_PREFIX;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.HEADERS_PREFIX;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.IDLE_TIMEOUT;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.MAX_BATCH_COUNT;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.POSITION_FILE;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.SKIP_TO_END;
import static com.hbasesoft.framework.log.flume.core.taildir.TaildirSourceConfigurationConstants.WRITE_POS_INTERVAL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.BatchSizeSupported;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.apache.flume.source.PollableSourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;

public class TaildirSource extends AbstractSource implements PollableSource, Configurable, BatchSizeSupported {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaildirSource.class);

    /** */
    private Map<String, String> filePaths;

    /** */
    private Table<String, String, String> headerTable;

    /** */
    private int batchSize;

    /** */
    private String positionFilePath;

    /** */
    private boolean skipToEnd;

    /** */
    private boolean byteOffsetHeader;

    /** */
    private SourceCounter sourceCounter;

    /** */
    private ReliableTaildirEventReader reader;

    /** */
    private ScheduledExecutorService idleFileChecker;

    /** */
    private ScheduledExecutorService positionWriter;

    /** */
    private static final int NUM_1000 = 1000;

    /** */
    private int retryInterval = NUM_1000;

    /** */
    private static final int NUM = 1000;

    /** */
    private final int maxRetryInterval = 5000;

    /** */
    private int idleTimeout;

    /** */
    private final int checkIdleInterval = 5000;

    /** */
    private final int writePosInitDelay = 5000;

    /** */
    private int writePosInterval;

    /** */
    private boolean cachePatternMatching;

    /** */
    private List<Long> existingInodes = new CopyOnWriteArrayList<Long>();

    /** */
    private List<Long> idleInodes = new CopyOnWriteArrayList<Long>();

    /** */
    private Long backoffSleepIncrement;

    /** */
    private Long maxBackOffSleepInterval;

    /** */
    private boolean fileHeader;

    /** */
    private String fileHeaderKey;

    /** */
    private Long maxBatchCount;

    @Override
    public synchronized void start() {
        LOGGER.info("{} TaildirSource source starting with directory: {}", getName(), filePaths);
        try {
            reader = new ReliableTaildirEventReader.Builder().filePaths(filePaths).headerTable(headerTable)
                .positionFilePath(positionFilePath).skipToEnd(skipToEnd).addByteOffset(byteOffsetHeader)
                .cachePatternMatching(cachePatternMatching).annotateFileName(fileHeader).fileNameHeader(fileHeaderKey)
                .build();
        }
        catch (IOException e) {
            throw new FlumeException("Error instantiating ReliableTaildirEventReader", e);
        }
        idleFileChecker = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("idleFileChecker").build());
        idleFileChecker.scheduleWithFixedDelay(new IdleFileCheckerRunnable(), idleTimeout, checkIdleInterval,
            TimeUnit.MILLISECONDS);

        positionWriter = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("positionWriter").build());
        positionWriter.scheduleWithFixedDelay(new PositionWriterRunnable(), writePosInitDelay, writePosInterval,
            TimeUnit.MILLISECONDS);

        super.start();
        LOGGER.debug("TaildirSource started");
        sourceCounter.start();
    }

    @Override
    public synchronized void stop() {
        try {
            super.stop();
            ExecutorService[] services = {
                idleFileChecker, positionWriter
            };
            for (ExecutorService service : services) {
                service.shutdown();
                if (!service.awaitTermination(1, TimeUnit.SECONDS)) {
                    service.shutdownNow();
                }
            }
            // write the last position
            writePosition();
            reader.close();
        }
        catch (InterruptedException e) {
            LOGGER.info("Interrupted while awaiting termination", e);
        }
        catch (IOException e) {
            LOGGER.info("Failed: " + e.getMessage(), e);
        }
        sourceCounter.stop();
        LOGGER.info("Taildir source {} stopped. Metrics: {}", getName(), sourceCounter);
    }

    @Override
    public String toString() {
        return String.format(
            "Taildir source: { positionFile: %s, skipToEnd: %s, "
                + "byteOffsetHeader: %s, idleTimeout: %s, writePosInterval: %s }",
            positionFilePath, skipToEnd, byteOffsetHeader, idleTimeout, writePosInterval);
    }

    @Override
    public synchronized void configure(final Context context) {
        String fileGroups = context.getString(FILE_GROUPS);
        Preconditions.checkState(fileGroups != null, "Missing param: " + FILE_GROUPS);

        filePaths = selectByKeys(context.getSubProperties(FILE_GROUPS_PREFIX), fileGroups.split("\\s+"));
        Preconditions.checkState(!filePaths.isEmpty(),
            "Mapping for tailing files is empty or invalid: '" + FILE_GROUPS_PREFIX + "'");

        String homePath = System.getProperty("user.home").replace('\\', '/');
        positionFilePath = context.getString(POSITION_FILE, homePath + DEFAULT_POSITION_FILE);
        Path positionFile = Paths.get(positionFilePath);
        try {
            Files.createDirectories(positionFile.getParent());
        }
        catch (IOException e) {
            throw new FlumeException("Error creating positionFile parent directories", e);
        }
        headerTable = getTable(context, HEADERS_PREFIX);
        batchSize = context.getInteger(BATCH_SIZE, DEFAULT_BATCH_SIZE);
        skipToEnd = context.getBoolean(SKIP_TO_END, DEFAULT_SKIP_TO_END);
        byteOffsetHeader = context.getBoolean(BYTE_OFFSET_HEADER, DEFAULT_BYTE_OFFSET_HEADER);
        idleTimeout = context.getInteger(IDLE_TIMEOUT, DEFAULT_IDLE_TIMEOUT);
        writePosInterval = context.getInteger(WRITE_POS_INTERVAL, DEFAULT_WRITE_POS_INTERVAL);
        cachePatternMatching = context.getBoolean(CACHE_PATTERN_MATCHING, DEFAULT_CACHE_PATTERN_MATCHING);

        backoffSleepIncrement = context.getLong(PollableSourceConstants.BACKOFF_SLEEP_INCREMENT,
            PollableSourceConstants.DEFAULT_BACKOFF_SLEEP_INCREMENT);
        maxBackOffSleepInterval = context.getLong(PollableSourceConstants.MAX_BACKOFF_SLEEP,
            PollableSourceConstants.DEFAULT_MAX_BACKOFF_SLEEP);
        fileHeader = context.getBoolean(FILENAME_HEADER, DEFAULT_FILE_HEADER);
        fileHeaderKey = context.getString(FILENAME_HEADER_KEY, DEFAULT_FILENAME_HEADER_KEY);
        maxBatchCount = context.getLong(MAX_BATCH_COUNT, DEFAULT_MAX_BATCH_COUNT);
        if (maxBatchCount <= 0) {
            maxBatchCount = DEFAULT_MAX_BATCH_COUNT;
            LOGGER.warn("Invalid maxBatchCount specified, initializing source " + "default maxBatchCount of {}",
                maxBatchCount);
        }

        if (sourceCounter == null) {
            sourceCounter = new SourceCounter(getName());
        }
    }

    @Override
    public long getBatchSize() {
        return batchSize;
    }

    private Map<String, String> selectByKeys(final Map<String, String> map, final String[] keys) {
        Map<String, String> result = Maps.newHashMap();
        for (String key : keys) {
            if (map.containsKey(key)) {
                result.put(key, map.get(key));
            }
        }
        return result;
    }

    private Table<String, String, String> getTable(final Context context, final String prefix) {
        Table<String, String, String> table = HashBasedTable.create();
        for (Entry<String, String> e : context.getSubProperties(prefix).entrySet()) {
            String[] parts = e.getKey().split("\\.", 2);
            table.put(parts[0], parts[1], e.getValue());
        }
        return table;
    }

    @VisibleForTesting
    protected SourceCounter getSourceCounter() {
        return sourceCounter;
    }

    @Override
    public Status process() {
        Status status = Status.BACKOFF;
        try {
            existingInodes.clear();
            existingInodes.addAll(reader.updateTailFiles());
            for (long inode : existingInodes) {
                TailFile tf = reader.getTailFiles().get(inode);
                if (tf.needTail()) {
                    boolean hasMoreLines = tailFileProcess(tf, true);
                    if (hasMoreLines) {
                        status = Status.READY;
                    }
                }
            }
            closeTailFiles();
        }
        catch (Throwable t) {
            LOGGER.error("Unable to tail files", t);
            sourceCounter.incrementEventReadFail();
            status = Status.BACKOFF;
        }
        return status;
    }

    @Override
    public long getBackOffSleepIncrement() {
        return backoffSleepIncrement;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return maxBackOffSleepInterval;
    }

    private boolean tailFileProcess(final TailFile tf, final boolean backoffWithoutNL)
        throws IOException, InterruptedException {
        long batchCount = 0;
        while (true) {
            reader.setCurrentFile(tf);
            List<Event> events = reader.readEvents(batchSize, backoffWithoutNL);
            if (events.isEmpty()) {
                return false;
            }
            sourceCounter.addToEventReceivedCount(events.size());
            sourceCounter.incrementAppendBatchReceivedCount();
            try {
                getChannelProcessor().processEventBatch(events);
                reader.commit();
            }
            catch (ChannelException ex) {
                LOGGER.warn("The channel is full or unexpected failure. " + "The source will try again after "
                    + retryInterval + " ms");
                sourceCounter.incrementChannelWriteFail();
                TimeUnit.MILLISECONDS.sleep(retryInterval);
                retryInterval = retryInterval << 1;
                retryInterval = Math.min(retryInterval, maxRetryInterval);
                continue;
            }
            retryInterval = NUM;
            sourceCounter.addToEventAcceptedCount(events.size());
            sourceCounter.incrementAppendBatchAcceptedCount();
            if (events.size() < batchSize) {
                LOGGER.debug("The events taken from " + tf.getPath() + " is less than " + batchSize);
                return false;
            }
            if (++batchCount >= maxBatchCount) {
                LOGGER.debug("The batches read from the same file is larger than " + maxBatchCount);
                return true;
            }
        }
    }

    private void closeTailFiles() throws IOException, InterruptedException {
        for (long inode : idleInodes) {
            TailFile tf = reader.getTailFiles().get(inode);
            if (tf.getRaf() != null) { // when file has not closed yet
                tailFileProcess(tf, false);
                tf.close();
                LOGGER.info("Closed file: " + tf.getPath() + ", inode: " + inode + ", pos: " + tf.getPos());
            }
        }
        idleInodes.clear();
    }

    /**
     * Runnable class that checks whether there are files which should be closed.
     */
    private final class IdleFileCheckerRunnable implements Runnable {
        @Override
        public void run() {
            try {
                long now = System.currentTimeMillis();
                for (TailFile tf : reader.getTailFiles().values()) {
                    if (tf.getLastUpdated() + idleTimeout < now && tf.getRaf() != null) {
                        idleInodes.add(tf.getInode());
                    }
                }
            }
            catch (Throwable t) {
                LOGGER.error("Uncaught exception in IdleFileChecker thread", t);
                sourceCounter.incrementGenericProcessingFail();
            }
        }
    }

    /**
     * Runnable class that writes a position file which has the last read position of each file.
     */
    private final class PositionWriterRunnable implements Runnable {
        @Override
        public void run() {
            writePosition();
        }
    }

    private void writePosition() {
        File file = new File(positionFilePath);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            if (!existingInodes.isEmpty()) {
                String json = toPosInfoJson();
                writer.write(json);
            }
        }
        catch (Throwable t) {
            LOGGER.error("Failed writing positionFile", t);
            sourceCounter.incrementGenericProcessingFail();
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (IOException e) {
                LOGGER.error("Error: " + e.getMessage(), e);
                sourceCounter.incrementGenericProcessingFail();
            }
        }
    }

    private String toPosInfoJson() {
        @SuppressWarnings("rawtypes")
        List<Map> posInfos = Lists.newArrayList();
        for (Long inode : existingInodes) {
            TailFile tf = reader.getTailFiles().get(inode);
            posInfos.add(ImmutableMap.of("inode", inode, "pos", tf.getPos(), "file", tf.getPath()));
        }
        return new Gson().toJson(posInfos);
    }
}
