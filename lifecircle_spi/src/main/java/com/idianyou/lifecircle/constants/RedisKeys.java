package com.idianyou.lifecircle.constants;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/28 10:03
 */
public interface RedisKeys {

    /**
     * 生活圈根目录
     */
    String ROOT_PATH = "lifecircle:";

    /**
     * 自增ID目录
     */
    String AUTOID_PATH = ROOT_PATH.concat("autoId:");

    /**
     * 分布式锁
     */
    String LOCK_PATH = ROOT_PATH.concat("lock:");

    /**
     * 配置项
     */
    String CONF_PATH = ROOT_PATH.concat("conf:");

    /**
     * 服务业务数据ID关联动态ID
     */
    String BIZ_PARAM_DATAID_RELATED_CONTENTIDS = ROOT_PATH.concat("bizParamDataidRelatedContentIds:");

    /**
     * 服务业务数据ID关联服务数据
     */
    String BIZ_PARAM_DATAID_RELATED_SERVICE_DATA = ROOT_PATH.concat("bizParamDataIdRelatedServiceData:");

    /**
     * 动态ID关联了哪些查询批次号(哪些请求了这个动态)
     */
    String CONTENTID_RELATED_REQUESTNOS = ROOT_PATH.concat("contentIdRelatedRequestNos:");

    /**
     * 当前用户对应的查询批次号
     */
    String USERID_RELATED_REQUESTNO = ROOT_PATH.concat("userIdRelatedRequestNo:");

    /**
     * 查询批次号关联动态ID
     */
    String REQUESTNO_RELATED_CONTENTIDS_SET = ROOT_PATH.concat("requestnoRelatedContentIdsSet:");

    /**
     * 查询批次号关联已查询过的主题
     */
    String REQUESTNO_RELATED_CATEGORY_SET = ROOT_PATH.concat("requestnoRelatedCategorySet:");

    /**
     * 查询批次号最后活跃时间
     */
    String REQUESTNO_RELATED_LAST_ACTIVE_TIME = ROOT_PATH.concat("requestnoRelatedLastActiveTime:");

    /**
     * 记录数据分类
     */
    String DATA_CATEGORY_ZSET = ROOT_PATH.concat("dataCategoryZSet:");

    /**
     * 用户在本地的情况
     */
    String USER_NATIVE_TYPE_MAP = ROOT_PATH.concat("userNativeTypeMap");


    //======================== 问答红包相关=========================
    /**
     * 问答红包开关
     */
    String QUESTION_REDPACKET_SWITCH = CONF_PATH.concat("questionRedpacketSwitch");

    /**
     * 问答红包间隔时间(秒)
     */
    String QUESTION_REDPACKET_INTERVAL = CONF_PATH.concat("questionRedpacketInterval");

    /**
     * 问答红包相关
     */
    String QUESTION_REDPACKET_PATH = ROOT_PATH.concat("questionRedpacket:");

    /**
     * 用户上一个问答红包的问题ID
     */
    String QUESTION_REDPACKET_PREVIOUS_QUESTIONID = QUESTION_REDPACKET_PATH.concat("previousQuestionId:");

    /**
     * 用户上一个问答红包的创建时间
     */
    String QUESTION_REDPACKET_PREVIOUS_CREATE_TIME = QUESTION_REDPACKET_PATH.concat("previousCreateTime:");


    //=======================配置项========================
    String CURRENT_CATEGORY_SORT_NO = CONF_PATH.concat("currentCategorySortNo");


    /**
     * 个人动态推荐
     */
    String PERSONAL_DATA_RECOMMENDED_LIST = ROOT_PATH.concat("personalDataRecommendedList");
}
