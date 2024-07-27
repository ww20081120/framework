/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.engine;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;

import ognl.MemberAccess;

/**
 * This class provides methods for setting up and restoring access in a Field. Java 2 provides access utilities for
 * setting and getting fields that are non-public. This object provides coarse-grained access controls to allow access
 * to private, protected and package protected members. This will apply to all classes and members.
 *
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 * @version 15 October 1999
 */
@SuppressWarnings("rawtypes")
public class DefaultMemberAccess implements MemberAccess {

    /** allowPrivateAccess */
    private boolean allowPrivateAccess = false;

    /** allowProtectedAccess */
    private boolean allowProtectedAccess = false;

    /** allowPackageProtectedAccess */
    private boolean allowPackageProtectedAccess = false;

    /**
     * DefaultMemberAccess
     * 
     * @param allowAllAccess
     */
    public DefaultMemberAccess(final boolean allowAllAccess) {
        this(allowAllAccess, allowAllAccess, allowAllAccess);
    }

    /**
     * DefaultMemberAccess
     * 
     * @param ap
     * @param access
     * @param allowPackage
     */
    public DefaultMemberAccess(final boolean ap, final boolean access, final boolean allowPackage) {
        super();
        this.allowPrivateAccess = ap;
        this.allowProtectedAccess = access;
        this.allowPackageProtectedAccess = allowPackage;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public boolean getAllowPrivateAccess() {
        return allowPrivateAccess;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param value <br>
     */
    public void setAllowPrivateAccess(final boolean value) {
        allowPrivateAccess = value;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public boolean getAllowProtectedAccess() {
        return allowProtectedAccess;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param value <br>
     */
    public void setAllowProtectedAccess(final boolean value) {
        allowProtectedAccess = value;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public boolean getAllowPackageProtectedAccess() {
        return allowPackageProtectedAccess;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param value <br>
     */
    public void setAllowPackageProtectedAccess(final boolean value) {
        allowPackageProtectedAccess = value;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context
     * @param target
     * @param member
     * @param propertyName
     * @return <br>
     */
    public Object setup(final Map context, final Object target, final Member member, final String propertyName) {
        Object result = null;

        if (isAccessible(context, target, member, propertyName)) {
            AccessibleObject accessible = (AccessibleObject) member;

            if (!accessible.canAccess(target)) {
                result = Boolean.TRUE;
                accessible.setAccessible(true);
            }
        }
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context
     * @param target
     * @param member
     * @param propertyName
     * @param state <br>
     */
    public void restore(final Map context, final Object target, final Member member, final String propertyName,
        final Object state) {
        if (state != null) {
            ((AccessibleObject) member).setAccessible(((Boolean) state).booleanValue());
        }
    }

    /**
     * Description: Returns true if the given member is accessible or can be made accessible by this object.<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context
     * @param target
     * @param member
     * @param propertyName
     * @return <br>
     */
    public boolean isAccessible(final Map context, final Object target, final Member member,
        final String propertyName) {
        int modifiers = member.getModifiers();
        boolean result = Modifier.isPublic(modifiers);

        if (!result) {
            if (Modifier.isPrivate(modifiers)) {
                result = getAllowPrivateAccess();
            }
            else {
                if (Modifier.isProtected(modifiers)) {
                    result = getAllowProtectedAccess();
                }
                else {
                    result = getAllowPackageProtectedAccess();
                }
            }
        }
        return result;
    }
}
