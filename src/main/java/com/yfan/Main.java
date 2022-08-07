package com.yfan;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import com.yfan.config.Config;
import com.yfan.console.Console;
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
                Console.info("请选择需要执行的工具(1：项目构建工具，2：待开发)");
                type = sc.nextLine();
            } else {
                type = args[0];
            }
            Setting config = Config.getConfig();
            if (config == null) {
                Console.info("未获取到配置文件！");
            }
            Starter starter = StarterFactory.getStarter(type);
            if (starter == null) {
                Console.info(StrUtil.format("未找到名为[{}]的工具，输入错误！", StarterType.getType(type).getDes()));
            } else {
                TimeInterval timer = DateUtil.timer();
                timer.start();
                starter.start();
                Console.info(StrUtil.format("执行完毕，耗时[{}]毫秒。", timer.interval()));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            Console.info("输入任意键以关闭");
            sc.nextLine();
            sc.close();
            System.exit(0);
        }
    }
}
