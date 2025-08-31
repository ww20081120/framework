/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

/**
 * Agents 框架的组件注册器<br>
 * 该类负责动态扫描和注册 Agents 框架的组件。
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.config <br>
 */
public class AgentsRegistrar implements ImportBeanDefinitionRegistrar {

	private static final String AGENT_PACKAGE = "com.hbasesoft.framework.ai.agent";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		// 创建扫描器
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

		// 添加过滤器，只扫描带有特定注解的类
		scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));

		// 扫描指定包下的组件
		Set<BeanDefinition> components = scanner.findCandidateComponents(AGENT_PACKAGE);

		// 用于跟踪已注册的 bean 名称，防止重复
		Map<String, Integer> registeredBeanNames = new ConcurrentHashMap<>();

		// 注册组件
		for (BeanDefinition component : components) {
			String beanClassName = component.getBeanClassName();
			if (beanClassName != null) {
				// 生成唯一的 bean 名称
				String beanName = generateUniqueBeanName(beanClassName, registry, registeredBeanNames);

				// 注册 bean 定义
				registry.registerBeanDefinition(beanName, component);
			}
		}
	}

	/**
	 * 生成唯一的 bean 名称，避免名称冲突
	 * 
	 * @param beanClassName       完整的类名
	 * @param registry            Bean 定义注册器
	 * @param registeredBeanNames 已注册的 bean 名称映射
	 * @return 唯一的 bean 名称
	 */
	private String generateUniqueBeanName(String beanClassName, BeanDefinitionRegistry registry,
			Map<String, Integer> registeredBeanNames) {
		// 首先尝试使用简单类名
		String simpleBeanName = ClassUtils.getShortName(beanClassName);
		simpleBeanName = Character.toLowerCase(simpleBeanName.charAt(0)) + simpleBeanName.substring(1);

		// 检查是否已存在同名的 bean
		if (registry.containsBeanDefinition(simpleBeanName) || registeredBeanNames.containsKey(simpleBeanName)) {
			// 如果存在冲突，使用包名前缀来区分
			String packageName = ClassUtils.getPackageName(beanClassName);
			// 提取包名的最后一部分作为前缀
			String[] packageParts = packageName.split("\\.");
			String packageSuffix = packageParts.length > 0 ? packageParts[packageParts.length - 1] : "";

			// 构造带包名前缀的 bean 名称
			String prefixedBeanName = packageSuffix + simpleBeanName.substring(0, 1).toUpperCase()
					+ simpleBeanName.substring(1);

			// 检查带前缀的名称是否也存在冲突
			if (registry.containsBeanDefinition(prefixedBeanName) || registeredBeanNames.containsKey(prefixedBeanName)) {
				// 如果仍然存在冲突，添加序号后缀
				Integer count = registeredBeanNames.getOrDefault(prefixedBeanName, 1);
				String numberedBeanName = prefixedBeanName + count;
				while (registry.containsBeanDefinition(numberedBeanName)
						|| registeredBeanNames.containsKey(numberedBeanName)) {
					count++;
					numberedBeanName = prefixedBeanName + count;
				}
				registeredBeanNames.put(prefixedBeanName, count + 1);
				return numberedBeanName;
			} else {
				registeredBeanNames.put(prefixedBeanName, 1);
				return prefixedBeanName;
			}
		} else {
			// 没有冲突，直接使用简单类名
			registeredBeanNames.put(simpleBeanName, 1);
			return simpleBeanName;
		}
	}
}
