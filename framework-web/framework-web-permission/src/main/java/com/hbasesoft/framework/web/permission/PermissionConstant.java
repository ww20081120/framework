package com.hbasesoft.framework.web.permission;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.web.core.WebConstant;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/11/4 <br>
 * @see com.hbasesoft.framework.web.manager.constant <br>
 * @since V1.0<br>
 */
public interface PermissionConstant extends WebConstant, GlobalConstants {

    String SESSION_ADMIN = "SESSION_ADMIN__";

    String SESSION_PERMISSIONS_DATA = "SESSION_PERMISSIONS_DATA__";

    String SESSION_ROLE_DATA = "ROLE_DATA__";

    String DIRECTORY_URL = "DIR_URL";

    String DIRECTORY_ITEM = "DIR_ITEM";

    String CONFIG_ITEM_RESOURCE_PATH = "RESOURCE.PATH";

    /** 菜单缓存key */
    String CACHE_MENU = "/SYSTEM_MENU";

    /** URL_RESOURCE缓存key */
    String CACHE_URL_RESOURCE = "/URL_RESOURCE";
}
