package com.yfan.starter;

import com.yfan.generator.GeneratorExecuter;

/**
 * 代码生成器
 *
 * @Author: YFAN
 * @CreateTime: 2022-08-07 16:14
 */
public class GeneratorStarter implements Starter {
    @Override
    public void start() throws Exception {
        GeneratorExecuter.execute();
    }
}
