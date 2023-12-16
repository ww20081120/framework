/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.langchain4j.demo;

import java.util.HashMap;
import java.util.Map;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

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
public class PromptTemplateExamples {

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
    static class PromptTemplateWithOneVariableExample {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param args <br>
         */
        public static void main(final String[] args) {

            PromptTemplate promptTemplate = PromptTemplate.from("Say 'hi' in {{it}}.");

            Prompt prompt = promptTemplate.apply("German");

            System.out.println(prompt.text()); // Say 'hi' in German.
        }
    }

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
    static class PromptTemplateWithMultipleVariablesExample {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param args <br>
         */
        public static void main(final String[] args) {

            PromptTemplate promptTemplate = PromptTemplate.from("Say '{{text}}' in {{language}}.");

            Map<String, Object> variables = new HashMap<>();
            variables.put("text", "hi");
            variables.put("language", "German");

            Prompt prompt = promptTemplate.apply(variables);

            System.out.println(prompt.text()); // Say 'hi' in German.
        }
    }
}
