package com.fccfc.framework.web.manager;

import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.web.WebConstant;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/11/4 <br>
 * @see com.fccfc.framework.web.manager.constant <br>
 * @since V1.0<br>
 */
public interface ManagerConstant extends WebConstant, GlobalConstants {

    String SESSION_ACCOUNT = "SESSION_ACCOUNT__";

    String SESSION_ADMIN = "SESSION_ADMIN__";

    String SESSION_PERMISSIONS_DATA = "SESSION_PERMISSIONS_DATA__";

    /**
     * 状态：可用
     */
    String STATE_AVAILABLE = "A";

    /**
     * 状诚：不可用 或 删除
     */
    String STATE_UNAVAILABLE = "X";

    String DIRECTORY_URL = "DIR_URL";

    String DIRECTORY_ITEM = "DIR_ITEM";

    String CONFIG_ITEM_RESOURCE_PATH = "RESOURCE.PATH";
}
