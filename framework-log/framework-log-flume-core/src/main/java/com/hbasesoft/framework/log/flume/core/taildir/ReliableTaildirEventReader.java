/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.core.taildir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;
import org.apache.flume.client.avro.ReliableEventReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.gson.stream.JsonReader;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Sep 2, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.core.taildir <br>
 */
@InterfaceAudience.Private
@InterfaceStability.Evolving
public final class ReliableTaildirEventReader implements ReliableEventReader {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReliableTaildirEventReader.class);

    /** */
    private final List<TaildirMatcher> taildirCache;

    /** */
    private final Table<String, String, String> headerTable;

    /** */
    private TailFile currentFile = null;

    /** */
    private Map<Long, TailFile> tailFiles = Maps.newHashMap();

    /** */
    private long updateTime;

    /** */
    private boolean addByteOffset;

    /** */
    private boolean cachePatternMatching;

    /** */
    private boolean committed = true;

    /** */
    private final boolean annotateFileName;

    /** */
    private final String fileNameHeader;

    /** */
    private final int params3 = 3;

    /** */
    private final int params4 = 4;

    /** */
    private final int params5 = 5;

    /**
     * @Method ReliableTaildirEventReader
     * @param filePaths
     * @param headerTable
     * @param params
     * @return
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/10 11:50
    */
    private ReliableTaildirEventReader(final Map<String, String> filePaths,
        final Table<String, String, String> headerTable, final Object... params) throws IOException {
        // Sanity checks
        Preconditions.checkNotNull(filePaths);
        Preconditions.checkNotNull(params[0]);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initializing {} with directory={}", new Object[] {
                ReliableTaildirEventReader.class.getSimpleName(), filePaths
            });
        }

        List<TaildirMatcher> taildirMatchers = Lists.newArrayList();
        for (Entry<String, String> e : filePaths.entrySet()) {
            taildirMatchers.add(new TaildirMatcher(e.getKey(), e.getValue(), (boolean) params[params3]));
        }
        LOGGER.info("taildirCache: " + taildirMatchers.toString());
        LOGGER.info("headerTable: " + headerTable.toString());

        this.taildirCache = taildirMatchers;
        this.headerTable = headerTable;
        this.addByteOffset = (boolean) params[2];
        this.cachePatternMatching = (boolean) params[params3];
        this.annotateFileName = (boolean) params[params4];
        this.fileNameHeader = (String) params[params5];
        updateTailFiles((boolean) params[1]);

        LOGGER.info("Updating position from position file: " + params[0]);
        loadPositionFile((String) params[0]);
    }

    /**
     * Load a position file which has the last read position of each file. If the position file exists, update tailFiles
     * mapping.
     * @param filePath
     */
    public void loadPositionFile(final String filePath) {
        Long inode;
        Long pos;
        String path;
        FileReader fr = null;
        JsonReader jr = null;
        try {
            fr = new FileReader(filePath);
            jr = new JsonReader(fr);
            jr.beginArray();
            while (jr.hasNext()) {
                inode = null;
                pos = null;
                path = null;
                jr.beginObject();
                while (jr.hasNext()) {
                    switch (jr.nextName()) {
                        case "inode":
                            inode = jr.nextLong();
                            break;
                        case "pos":
                            pos = jr.nextLong();
                            break;
                        case "file":
                            path = jr.nextString();
                            break;
                        default:
                            break;
                    }
                }
                jr.endObject();

                for (Object v : Arrays.asList(inode, pos, path)) {
                    Preconditions.checkNotNull(v, "Detected missing value in position file. " + "inode: " + inode
                        + ", pos: " + pos + ", path: " + path);
                }
                TailFile tf = tailFiles.get(inode);
                if (tf != null && tf.updatePos(path, inode, pos)) {
                    tailFiles.put(inode, tf);
                }
                else {
                    LOGGER.info("Missing file: " + path + ", inode: " + inode + ", pos: " + pos);
                }
            }
            jr.endArray();
        }
        catch (FileNotFoundException e) {
            LOGGER.info("File not found: " + filePath + ", not updating position");
        }
        catch (IOException e) {
            LOGGER.error("Failed loading positionFile: " + filePath, e);
        }
        finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (jr != null) {
                    jr.close();
                }

            }
            catch (IOException e) {
                LOGGER.error("Error: " + e.getMessage(), e);
            }
        }
    }

    public Map<Long, TailFile> getTailFiles() {
        return tailFiles;
    }

    public void setCurrentFile(final TailFile currentFile) {
        this.currentFile = currentFile;
    }

    @Override
    public Event readEvent() throws IOException {
        List<Event> events = readEvents(1);
        if (events.isEmpty()) {
            return null;
        }
        return events.get(0);
    }

    @Override
    public List<Event> readEvents(final int numEvents) throws IOException {
        return readEvents(numEvents, false);
    }

    /**
     * @Method readEvents
     * @param tf
     * @param numEvents
     * @return java.util.List<org.apache.flume.Event>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 13:44
    */
    @VisibleForTesting
    public List<Event> readEvents(final TailFile tf, final int numEvents) throws IOException {
        setCurrentFile(tf);
        return readEvents(numEvents, true);
    }

    /**
     * @Method readEvents
     * @param numEvents
     * @param backoffWithoutNL
     * @return java.util.List<org.apache.flume.Event>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 13:44
    */
    public List<Event> readEvents(final int numEvents, final boolean backoffWithoutNL) throws IOException {
        if (!committed) {
            if (currentFile == null) {
                throw new IllegalStateException("current file does not exist. " + currentFile.getPath());
            }
            LOGGER.info("Last read was never committed - resetting position");
            long lastPos = currentFile.getPos();
            currentFile.updateFilePos(lastPos);
        }
        List<Event> events = currentFile.readEvents(numEvents, backoffWithoutNL, addByteOffset);
        if (events.isEmpty()) {
            return events;
        }

        Map<String, String> headers = currentFile.getHeaders();
        if (annotateFileName || (headers != null && !headers.isEmpty())) {
            for (Event event : events) {
                if (headers != null && !headers.isEmpty()) {
                    event.getHeaders().putAll(headers);
                }
                if (annotateFileName) {
                    event.getHeaders().put(fileNameHeader, currentFile.getPath());
                }
            }
        }
        committed = false;
        return events;
    }

    @Override
    public void close() throws IOException {
        for (TailFile tf : tailFiles.values()) {
            if (tf.getRaf() != null) {
                tf.getRaf().close();
            }
        }
    }

    /** Commit the last lines which were read. */
    @Override
    public void commit() throws IOException {
        if (!committed && currentFile != null) {
            long pos = currentFile.getLineReadPos();
            currentFile.setPos(pos);
            currentFile.setLastUpdated(updateTime);
            committed = true;
        }
    }

    /**
     * Update tailFiles mapping if a new file is created or appends are detected to the existing file.
     * @param skipToEnd
     * @return java.util.List<java.lang.Long>
     */
    public List<Long> updateTailFiles(final boolean skipToEnd) throws IOException {
        updateTime = System.currentTimeMillis();
        List<Long> updatedInodes = Lists.newArrayList();

        for (TaildirMatcher taildir : taildirCache) {
            Map<String, String> headers = headerTable.row(taildir.getFileGroup());

            for (File f : taildir.getMatchingFiles()) {
                long inode;
                try {
                    inode = getInode(f);
                }
                catch (NoSuchFileException e) {
                    LOGGER.info("File has been deleted in the meantime: " + e.getMessage());
                    continue;
                }
                TailFile tf = tailFiles.get(inode);
                if (tf == null || !tf.getPath().equals(f.getAbsolutePath())) {
                    long startPos = skipToEnd ? f.length() : 0;
                    tf = openFile(f, headers, inode, startPos);
                }
                else {
                    boolean updated = tf.getLastUpdated() < f.lastModified() || tf.getPos() != f.length();
                    if (updated) {
                        if (tf.getRaf() == null) {
                            tf = openFile(f, headers, inode, tf.getPos());
                        }
                        if (f.length() < tf.getPos()) {
                            LOGGER.info("Pos " + tf.getPos() + " is larger than file size! "
                                + "Restarting from pos 0, file: " + tf.getPath() + ", inode: " + inode);
                            tf.updatePos(tf.getPath(), inode, 0);
                        }
                    }
                    tf.setNeedTail(updated);
                }
                tailFiles.put(inode, tf);
                updatedInodes.add(inode);
            }
        }
        return updatedInodes;
    }

    /**
     * @Method updateTailFiles
     * @param
     * @return java.util.List<java.lang.Long>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 13:46
    */
    public List<Long> updateTailFiles() throws IOException {
        return updateTailFiles(false);
    }

    private long getInode(final File file) throws IOException {
        long inode = (long) Files.getAttribute(file.toPath(), "unix:ino");
        return inode;
    }

    private TailFile openFile(final File file, final Map<String, String> headers, final long inode, final long pos) {
        try {
            LOGGER.info("Opening file: " + file + ", inode: " + inode + ", pos: " + pos);
            return new TailFile(file, headers, inode, pos);
        }
        catch (IOException e) {
            throw new FlumeException("Failed opening file: " + file, e);
        }
    }

    /**
     * Special builder class for ReliableTaildirEventReader
     */
    public static class Builder {
        /** */
        private Map<String, String> filePaths;

        /** */
        private Table<String, String, String> headerTable;

        /** */
        private String positionFilePath;

        /** */
        private boolean skipToEnd;

        /** */
        private boolean addByteOffset;

        /** */
        private boolean cachePatternMatching;

        /** */
        private Boolean annotateFileName = TaildirSourceConfigurationConstants.DEFAULT_FILE_HEADER;

        /** */
        private String fileNameHeader = TaildirSourceConfigurationConstants.DEFAULT_FILENAME_HEADER_KEY;

        /**
         * @Method filePaths
         * @param filepaths
         * @return com.hbasesoft.framework.log.flume.core.taildir.ReliableTaildirEventReader.Builder
         * @Author 李煜龙
         * @Description TODD
         * @Date 2023/1/29 13:48
        */
        public Builder filePaths(final Map<String, String> filepaths) {
            this.filePaths = filepaths;
            return this;
        }

        /**
         * @Method headerTable
         * @param headertable
         * @return com.hbasesoft.framework.log.flume.core.taildir.ReliableTaildirEventReader.Builder
         * @Author 李煜龙
         * @Description TODD
         * @Date 2023/1/29 13:52
        */
        public Builder headerTable(final Table<String, String, String> headertable) {
            this.headerTable = headertable;
            return this;
        }

        /**
         * @Method positionFilePath
         * @param positionfilepath
         * @return com.hbasesoft.framework.log.flume.core.taildir.ReliableTaildirEventReader.Builder
         * @Author 李煜龙
         * @Description TODD
         * @Date 2023/1/29 13:52
        */
        public Builder positionFilePath(final String positionfilepath) {
            this.positionFilePath = positionfilepath;
            return this;
        }

        /**
         * @Method skipToEnd
         * @param skiptoend
         * @return com.hbasesoft.framework.log.flume.core.taildir.ReliableTaildirEventReader.Builder
         * @Author 李煜龙
         * @Description TODD
         * @Date 2023/1/29 13:53
        */
        public Builder skipToEnd(final boolean skiptoend) {
            this.skipToEnd = skiptoend;
            return this;
        }

        /**
         * @Method addByteOffset
         * @param addbyteoffset
         * @return com.hbasesoft.framework.log.flume.core.taildir.ReliableTaildirEventReader.Builder
         * @Author 李煜龙
         * @Description TODD
         * @Date 2023/1/29 13:53
        */
        public Builder addByteOffset(final boolean addbyteoffset) {
            this.addByteOffset = addbyteoffset;
            return this;
        }

        /**
         * @Method cachePatternMatching
         * @param cachepatternmatching
         * @return com.hbasesoft.framework.log.flume.core.taildir.ReliableTaildirEventReader.Builder
         * @Author 李煜龙
         * @Description TODD
         * @Date 2023/1/29 13:54
        */
        public Builder cachePatternMatching(final boolean cachepatternmatching) {
            this.cachePatternMatching = cachepatternmatching;
            return this;
        }

        /**
         * @Method annotateFileName
         * @param annotatefilename
         * @return com.hbasesoft.framework.log.flume.core.taildir.ReliableTaildirEventReader.Builder
         * @Author 李煜龙
         * @Description TODD
         * @Date 2023/1/29 13:54
        */
        public Builder annotateFileName(final boolean annotatefilename) {
            this.annotateFileName = annotatefilename;
            return this;
        }

        /**
         * @Method fileNameHeader
         * @param filenameheader
         * @return com.hbasesoft.framework.log.flume.core.taildir.ReliableTaildirEventReader.Builder
         * @Author 李煜龙
         * @Description TODD
         * @Date 2023/1/29 13:55
        */
        public Builder fileNameHeader(final String filenameheader) {
            this.fileNameHeader = filenameheader;
            return this;
        }

        /**
         * @Method build
         * @param
         * @return com.hbasesoft.framework.log.flume.core.taildir.ReliableTaildirEventReader
         * @Author 李煜龙
         * @Description TODD
         * @Date 2023/1/29 13:55
        */
        public ReliableTaildirEventReader build() throws IOException {
            return new ReliableTaildirEventReader(filePaths, headerTable, positionFilePath, skipToEnd, addByteOffset,
                cachePatternMatching, annotateFileName, fileNameHeader);
        }
    }

}
