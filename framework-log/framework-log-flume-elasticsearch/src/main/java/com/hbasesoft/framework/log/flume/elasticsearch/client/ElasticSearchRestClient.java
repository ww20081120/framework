/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.elasticsearch.client;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.http.HttpStatus;

import com.google.gson.Gson;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.io.HttpUtil;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.log.flume.elasticsearch.ElasticSearchEventSerializer;
import com.hbasesoft.framework.log.flume.elasticsearch.IndexNameBuilder;

import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 18, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.elasticsearch.client <br>
 */
public class ElasticSearchRestClient implements ElasticSearchClient {

    private static final String INDEX_OPERATION_NAME = "index";

    private static final String INDEX_PARAM = "_index";

    private static final String TYPE_PARAM = "_type";

    private static final String TTL_PARAM = "_ttl";

    private static final String BULK_ENDPOINT = "_bulk";

    private final ElasticSearchEventSerializer serializer;

    private final RoundRobinList<Address> serversList;

    private StringBuilder bulkBuilder;

    public ElasticSearchRestClient(Address[] address, ElasticSearchEventSerializer serializer) {

        this.serializer = serializer;

        serversList = new RoundRobinList<Address>(Arrays.asList(address));
        bulkBuilder = new StringBuilder();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @param indexNameBuilder
     * @param indexType
     * @param ttlMs
     * @throws Exception <br>
     */
    @Override
    public void addEvent(Event event, IndexNameBuilder indexNameBuilder, String indexType, long ttlMs)
        throws Exception {
        String content = serializer.getContentBuilder(event);
        Map<String, Map<String, String>> parameters = new HashMap<String, Map<String, String>>();
        Map<String, String> indexParameters = new HashMap<String, String>();
        indexParameters.put(INDEX_PARAM, indexNameBuilder.getIndexName(event));
        indexParameters.put(TYPE_PARAM, indexType);
        if (ttlMs > 0) {
            indexParameters.put(TTL_PARAM, Long.toString(ttlMs));
        }
        parameters.put(INDEX_OPERATION_NAME, indexParameters);

        Gson gson = new Gson();
        synchronized (bulkBuilder) {
            bulkBuilder.append(gson.toJson(parameters));
            bulkBuilder.append("\n");
            bulkBuilder.append(content);
            bulkBuilder.append("\n");
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @Override
    public void execute() throws Exception {
        int statusCode = 0, triesCount = 0;
        String entity;
        synchronized (bulkBuilder) {
            entity = bulkBuilder.toString();
            bulkBuilder.setLength(0);
        }
        while (statusCode != HttpStatus.SC_OK && triesCount < serversList.size()) {
            triesCount++;
            Address addr = serversList.get();
            String url = new StringBuilder().append(addr.getProtocol()).append("://").append(addr.getHost()).append(':')
                .append(addr.getPort()).append('/').append(BULK_ENDPOINT).toString();
            MediaType mediaType = MediaType.parse("application/x-ndjson");
            RequestBody requestBody = RequestBody.create(entity.getBytes(), mediaType);
            Builder builder = new Request.Builder().url(url).post(requestBody);
            String authorization = null;
            if (StringUtils.isNotEmpty(addr.getUsername()) && StringUtils.isNotEmpty(addr.getPassword())) {
                authorization = Credentials.basic(addr.getUsername(), addr.getPassword());
                builder.addHeader("Authorization", authorization);
            }
            Request request = builder.build();

            Call call = HttpUtil.getOkHttpClient().newCall(request);
            long beginTime = System.currentTimeMillis();

            Response response = call.execute();
            statusCode = response.code();
            if (statusCode != HttpStatus.SC_OK) {
                String transId = CommonUtil.getTransactionID();
                LoggerUtil.info("{0} elasticsearch request {1}|{2}|{3}", transId, authorization, url, entity);
                String respBody = IOUtil
                    .readString(new InputStreamReader(response.body().byteStream(), GlobalConstants.DEFAULT_CHARSET));
                LoggerUtil.info("{0} elasticsearch response {1}|{2}", transId, (System.currentTimeMillis() - beginTime),
                    respBody);
            }
        }

        if (statusCode != HttpStatus.SC_OK) {
            throw new EventDeliveryException("Elasticsearch status code was: " + statusCode);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void configure(Context context) {

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void close() {

    }

}
