package com.hbasesoft.framework.ai.demo.jmanus.planning.coordinator;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import org.springframework.stereotype.Component;

@Component
public class PlanIdDispatcher {

    /**
     * planId 前缀
     */
    private static final String PLAN_ID_PREFIX = "plan-";

    /**
     * planTemplateId 前缀
     */
    private static final String PLAN_TEMPLATE_ID_PREFIX = "planTemplate-";

    /**
     * 判断是否是计划ID
     *
     * @param id 待检查的ID
     * @return 如果是planId则返回true 否则返回false
     */
    public boolean isPlanId(String id) {
        return id != null && id.startsWith(PLAN_ID_PREFIX);
    }

    /**
     * 判断是否是计划模板Id
     *
     * @param id 计划Id
     * @return 如果是planTemplateId则返回true 否则返回false
     */
    public boolean isPlanTemplateId(String id) {
        return id != null && id.startsWith(PLAN_TEMPLATE_ID_PREFIX);
    }

    /**
     * 将planTemplateId转换为planId 由于一个planTemplate 可以创建多个计划，所以每次生成唯一的planId
     *
     * @param planTemplateId 计划模版Id
     * @return 转换后的唯一planId
     */
    public String toPlanId(String planTemplateId) {

        if (planTemplateId == null) {
            return null;
        }

        if (isPlanId(planTemplateId)) {
            return planTemplateId; // 如果是planId则直接返回
        }

        // 无论是否为PlanTemplateId，都会生成新的唯一planId
        // 使用时间戳和随机数确保唯一性
        String uniqueId = PLAN_ID_PREFIX + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);

        if (isPlanTemplateId(planTemplateId)) {
            LoggerUtil.debug("从planTemplateId[{0}]生成新的唯一planId[{1}]", planTemplateId, uniqueId);
        } else {
            LoggerUtil.info("未知ID格式[{0}]生成新的唯一planId[{1}]", planTemplateId, uniqueId);
        }
        return uniqueId;
    }

    /**
     * 将planId转换为planTemplateId 如果输入已经是planTemplateId则返回原值
     *
     * @param planId 计划id
     * @return 转换后的planTemplateId
     */
    public String toPlanTemplateId(String planId) {
        if (planId == null) {
            return null;
        }

        if (isPlanTemplateId(planId)) {
            return planId;
        }

        if (isPlanId(planId)) {
            // 提取ID的数字部分并创建新的planTemplateId
            String numericPart = planId.substring(PLAN_ID_PREFIX.length());
            String planTemplateId = PLAN_TEMPLATE_ID_PREFIX + numericPart;
            LoggerUtil.debug("转换planId[{0}]到planTemplateId[{1}]", planId, planTemplateId);
            return planTemplateId;
        }

        // 对于不符合任何已知格式的ID，添加planTemplateId的前缀
        LoggerUtil.info("未知ID格式[{0}]，添加planTemplateId前缀", planId);
        return PLAN_TEMPLATE_ID_PREFIX + planId;
    }

    /**
     * 生成planTemplateId
     *
     * @return planTemplateId
     */
    public String generatePlanTemplateId() {
        String planTemplateId = PLAN_TEMPLATE_ID_PREFIX + System.currentTimeMillis();
        LoggerUtil.debug("生成planTemplateId[{0}]", planTemplateId);
        return planTemplateId;
    }

    /**
     * 生成planId
     *
     * @return planId
     */
    public String generatePlanId() {
        String planId = PLAN_ID_PREFIX + System.currentTimeMillis();
        LoggerUtil.debug("生成planId[{0}]", planId);
        return planId;
    }

    /**
     * 根据现有ID生成对应的另一种类型ID 如果是PlanId，则生成PlanTemplateId， 如果是PlanTemplateId，则生成PlanId
     *
     * @param id 现有ID
     * @return 新的ID
     */
    public String convertId(String id) {

        if (id == null) {
            return null;
        }

        if (isPlanId(id)) {
            return toPlanTemplateId(id);
        } else if (isPlanTemplateId(id)) {
            return toPlanId(id);
        }
        // 无法确定ID类型，则返回原ID
        LoggerUtil.info("无法确定ID类型[{0}], 返回原ID", id);
        return id;
    }

}
