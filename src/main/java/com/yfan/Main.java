package com.yfan;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import com.yfan.config.Config;
import com.yfan.enums.StarterType;
import com.yfan.starter.Starter;
import com.yfan.starter.StarterFactory;

import java.util.Scanner;

/**
 * 主函数类
 *
 * @Author: YFAN
 * @CreateTime: 2022-07-27 20:41
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            String type;
            if (args.length == 0) {
                Console.log("请选择需要执行的工具(1：项目构建工具，2：代码生成器)");
                type = sc.nextLine();
            } else {
                type = args[0];
            }
            Setting config = Config.getConfig();
            if (config == null) {
                Console.log("未获取到配置文件！");
            }
            Starter starter = StarterFactory.getStarter(type);
            if (starter == null) {
                Console.log(StrUtil.format("未找到名为[{}]的工具，输入错误！", StarterType.getType(type).getDes()));
            } else {
                TimeInterval timer = DateUtil.timer();
                timer.start();
                starter.start();
                Console.log(StrUtil.format("执行完毕，耗时[{}]毫秒。", timer.interval()));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            Console.log("输入任意键以关闭");
            sc.nextLine();
            sc.close();
            System.exit(0);
        }
    }
}
