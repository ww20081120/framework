/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.elasticsearch.client;

import java.util.Collection;
import java.util.Iterator;

/**
 * <Description> <br>
 * @param <T>
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 18, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.elasticsearch.client <br>
 */
public class RoundRobinList<T> {

    /** */
    private Iterator<T> iterator;

    /** */
    private final Collection<T> elements;

    /** 
     * @Method RoundRobinList
     * @param elements 
     * @return 
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 14:06
    */
    public RoundRobinList(final Collection<T> elements) {
        this.elements = elements;
        iterator = this.elements.iterator();
    }

    /** 
     * @Method get
     * @param  
     * @return T
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 14:07
    */
    public synchronized T get() {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        else {
            iterator = elements.iterator();
            return iterator.next();
        }
    }

    /** 
     * @Method size
     * @param  
     * @return int
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 14:10
    */
    public int size() {
        return elements.size();
    }
}
