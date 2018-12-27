/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.demo.lock.impl;

import java.util.Arrays;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.cache.core.ICache;
import com.hbasesoft.framework.cache.core.annotation.CacheLock;
import com.hbasesoft.framework.cache.core.annotation.Key;
import com.hbasesoft.framework.cache.demo.lock.LuckDrawService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年1月5日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.lock.impl <br>
 */
@Service
public class LuckDrawServiceImpl implements LuckDrawService {

    private Random random = new Random();

    private static int level = 2000;

    private static int[] gifs = new int[] {
        10, 30, 100
    };

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param activityCode
     * @param userCode
     * @return <br>
     */
    @Override
    @CacheLock(value = "ShakeActivity", timeOut = 60000, expireTime = 70, key = "${activityCode}")
    public int luckDraw(@Key("activityCode") String activityCode, String userCode) {
        // TODO: 获取用户还有几次抽奖机会

        // TODO: 判断能否抽奖
        int count = 0;
        for (int c : gifs) {
            count += c;
        }
        if (count <= 0) {
            return 0;
        }

        int winner = getWinner(activityCode, count, level);
        // TODO：发放奖品
        if (winner > 0) {
            int index = random.nextInt(count);
            int s = 0;
            for (int i = 0; i < gifs.length; i++) {
                s += gifs[i];
                if (s > index) {
                    gifs[i]--;
                    System.out.println("恭喜获得" + (i + 1) + "等奖，奖品还剩余" + Arrays.toString(gifs));
                    return (i + 1);
                }
            }
        }
        return 0;
    }

    private int getWinner(String activityCode, int count, int level) {
        ICache cache = CacheHelper.getCache();
        Integer oldLevel = cache.get("LUCK_DRAW_LEVEL", activityCode);
        int key = 0;

        int[] winners;
        if (oldLevel == null || oldLevel - level != 0) {
            winners = new int[count];
            int index = 0;
            while (index < count) {
                int win = random.nextInt(level) + 1;

                boolean flag = false;
                for (int k = 0; k < index; k++) {
                    if (winners[k] == win) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    continue;
                }
                winners[index++] = win;
            }
            Arrays.sort(winners);
            cache.put("LUCK_DRAW_LEVEL", activityCode, level);
            cache.put("LUCK_DRAW", activityCode, winners);
        }
        else {
            winners = cache.get("LUCK_DRAW", activityCode);
            key = CacheHelper.getCache().get("LUCK_DRAW_CURRENT", activityCode);
        }
        CacheHelper.getCache().put("LUCK_DRAW_CURRENT", activityCode, ++key);
        System.out.println("你是第" + key + "位访问者");
        return Arrays.binarySearch(winners, key);
    }

}
