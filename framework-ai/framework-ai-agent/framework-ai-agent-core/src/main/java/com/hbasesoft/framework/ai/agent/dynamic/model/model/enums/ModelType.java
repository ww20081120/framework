/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.model.model.enums;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.model.model.enums <br>
 */
public enum ModelType {

	/**
	 * General model: Has multiple capabilities and can handle various tasks (e.g.,
	 * text generation, reasoning, code)
	 */
	GENERAL,

	/**
	 * Reasoning model: Used for logical reasoning and decision-making scenarios
	 */
	REASONING,

	/**
	 * Planner model: Used for task decomposition and plan formulation
	 */
	PLANNER,

	/**
	 * Vision model: Used for visual tasks such as image recognition, OCR, and
	 * object detection
	 */
	VISION,

	/**
	 * Code model: Used for code generation, understanding, and repair
	 */
	CODE,

	/**
	 * Text generation model: Used for natural language text generation (e.g.,
	 * dialogue, article generation)
	 */
	TEXT_GENERATION,

	/**
	 * Embedding model: Used for text vectorization and semantic encoding
	 */
	EMBEDDING,

	/**
	 * Classification model: Used for text classification and sentiment analysis
	 */
	CLASSIFICATION,

	/**
	 * Summarization model: Used for generating summaries of long texts
	 */
	SUMMARIZATION,

	/**
	 * Multimodal model: Processes multiple modalities such as text and images
	 * together
	 */
	MULTIMODAL,

	/**
	 * Speech model: Used for speech recognition and synthesis
	 */
	SPEECH,

	/**
	 * Translation model: Used for cross-language translation
	 */
	TRANSLATION;
}
