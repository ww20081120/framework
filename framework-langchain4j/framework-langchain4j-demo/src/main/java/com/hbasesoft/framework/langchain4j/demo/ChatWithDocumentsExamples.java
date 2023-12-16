/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.demo;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocument;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.langchain4j.dashscope.QwenChatModel;
import com.hbasesoft.framework.langchain4j.dashscope.QwenEmbeddingModel;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年10月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.langchain4j.demo <br>
 */
public class ChatWithDocumentsExamples {

    /**
     * <Description> <br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2023年10月26日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.langchain4j.demo <br>
     */
    public static class IfYouNeedSimplicity {

        /** NUM_500 */
        private static final int NUM_500 = 500;

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param args
         * @throws Exception <br>
         */
        public static void main(final String[] args) throws Exception {

            EmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey(PropertyHolder.getProperty("qwen.apikey")).build();

            EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(NUM_500, 0)).embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore).build();

            Document document = loadDocument(toPath("example-files/前端项目配置启动.docx"));
            ingestor.ingest(document);

            ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
                .chatLanguageModel(QwenChatModel.builder().apiKey(PropertyHolder.getProperty("qwen.apikey")).build())
                .retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel))
                // .chatMemory() // you can override default chat memory
                // .promptTemplate() // you can override default prompt template
                .build();

            String answer = chain.execute("npm 私服的地址是什么?");
            System.out.println(answer); // Charlie is a cheerful carrot living in VeggieVille...
        }
    }

    private static Path toPath(final String fileName) {
        try {
            URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
