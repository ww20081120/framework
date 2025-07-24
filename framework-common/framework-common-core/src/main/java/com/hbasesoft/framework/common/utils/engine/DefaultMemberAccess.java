/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.engine;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import ognl.MemberAccess;
import ognl.OgnlContext;

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
     * Sets up access for a member, saving the previous state.
     * 
     * @param context The current execution context.
     * @param target The object being accessed.
     * @param member The member being accessed.
     * @param propertyName The property name being accessed.
     * @return The previous access state that was set (to be restored later).
     */
    @Override
    public Object setup(final OgnlContext context, final Object target, final Member member, 
            final String propertyName) {
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
     * Restores access for a member based on the previous state.
     * 
     * @param context The current execution context.
     * @param target The object being accessed.
     * @param member The member being accessed.
     * @param propertyName The property name being accessed.
     * @param state The previous access state that was returned from setup().
     */
    @Override
    public void restore(final OgnlContext context, final Object target, final Member member, 
            final String propertyName, final Object state) {
        if (state != null) {
            ((AccessibleObject) member).setAccessible(((Boolean) state).booleanValue());
        }
    }

    /**
     * Returns true if the given member is accessible or can be made accessible by this object.
     * 
     * @param context The current execution context.
     * @param target The object being accessed.
     * @param member The member being accessed.
     * @param propertyName The property name being accessed.
     * @return True if the member is accessible, false otherwise.
     */
    @Override
    public boolean isAccessible(final OgnlContext context, final Object target, final Member member,
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
