package com.yfan.starter;

import cn.hutool.core.lang.Console;
import com.yfan.enums.StarterType;

import java.util.HashMap;

/**
 * 启动器工厂
 * @Author: YFAN
 * @CreateTime: 2022-08-07 11:18
 */
public class StarterFactory {

    private static HashMap<Integer, Starter> map = new HashMap<>();

    static {
        map.put(StarterType.PROJECT.getCode(), new ProjectStarter());
        map.put(StarterType.GENERATOR.getCode(), new GeneratorStarter());
    }

    /**
     * 获取启动器
     * @param code
     * @return
     */
    public static Starter getStarter(Integer code) {
        return map.get(code);
    }

    /**
     * 获取启动器
     * @param code
     * @return
     */
    public static Starter getStarter(String code) {
        Integer num = null;
        try {
            num = Integer.parseInt(code);
        } catch (Exception e) {
            Console.log(e);
        }
        return map.get(num);
    }
}
