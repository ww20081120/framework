/**
 * 
 */
package com.fccfc.framework.web;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web <br>
 */
public interface WebConstant {

    /** 菜单 */
    String MENU_VARIABLE = "MENU_VARIABLE";

    /** 树形菜单 */
    String MENU_VARIABLE_TREE = "menuList";

    /** 面包屑集合 */
    String BREAD_LINE_LIST = "breadLineList";

    /** session保存的operator信息 */
    String SESSION_OPERATOR = "__SESSION_OPERATOR";
    
    /** session保存的验证码信息 */
    String SESSION_VERIFY_CODE = "__SESSION_VERIFY_CODE";
    
    /** 扩展参数列表*/
    String SESSION_EXTEND_PARAMS = "__SESSION_EXTEND_PARAMS";
    
    /** session中保存的权限*/
    String SESSION_PERMISSIONS = "SESSION_PERMISSIONS__";
    
    /** url*/
    String APPLICATION_URL = "APPLICATION_URL__";
    
    /** menu*/
    String APPLICATION_MENU = "APPLICATION_MENU__";

    /** CONTEXT_PATH */
    String CONTEXT_PATH = "CONTEXT_PATH";

    /** 错误页面 不跳转 */
    String PAGE_NOT_REDIRECT = "PAGE_NOT_REDIRECT";
}
