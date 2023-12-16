/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.dashscope;

/**
 * <Description> 通义千问支持的模型名称 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年10月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.langchain4j.dashscope <br>
 */
public interface QwenModelName {

    /** Qwen base model, 4k context. */
    String QWEN_TURBO = "qwen-turbo";

    /** Qwen plus model, 8k context. */
    String QWEN_PLUS = "qwen-plus";

    /** Qwen sft for ai character scene, 4k context. */
    String QWEN_SPARK_V1 = "qwen-spark-v1";

    /** Qwen sft for ai character scene, 8k context. */
    String QWEN_SPARK_V2 = "qwen-spark-v2";

    /** Qwen open sourced 7-billion-parameters version */
    String QWEN_7B_CHAT = "qwen-7b-chat";

    /** Qwen open sourced 14-billion-parameters version */
    String QWEN_14B_CHAT = "qwen-14b-chat";

    /** Use with QwenEmbeddingModel */
    String TEXT_EMBEDDING_V1 = "text-embedding-v1";
}
