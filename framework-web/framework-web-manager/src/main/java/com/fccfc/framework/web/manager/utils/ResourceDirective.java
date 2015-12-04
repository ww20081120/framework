/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.utils;

import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.web.WebConstant;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.DirectiveConstants;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年9月19日 <br>
 * @since V1.0<br>
 * @see com.fccfc.migu.kf.portal.util <br>
 */
public class ResourceDirective extends Directive {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getName() {
        return "resource";
    }

    /**
     * Description: 块<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public int getType() {
        return DirectiveConstants.BLOCK;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context
     * @param writer
     * @param node
     * @return
     * @throws java.io.IOException
     * @throws org.apache.velocity.exception.ResourceNotFoundException
     * @throws org.apache.velocity.exception.ParseErrorException
     * @throws org.apache.velocity.exception.MethodInvocationException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node)
        throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        Set<String> permissionSet = (Set<String>) WebUtil.getAttribute(WebConstant.SESSION_PERMISSIONS);
        if (CommonUtil.isNotEmpty(permissionSet)) {
            SimpleNode resourceNode = (SimpleNode) node.jjtGetChild(0);
            Object resourceId = resourceNode.value(context);
            if (permissionSet.contains(resourceId.toString())) {
                node.jjtGetChild(1).render(context, writer);
                return true;
            }
        }
        return false;
    }

}
