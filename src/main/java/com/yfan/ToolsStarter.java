package com.yfan;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.dialect.console.ConsoleLog;
import cn.hutool.log.level.Level;
import cn.hutool.setting.Setting;
import com.yfan.config.Config;
import com.yfan.enums.ToolType;
import com.yfan.tools.ITool;

import java.util.Scanner;

/**
 * @BelongsProject: tools-master
 * @BelongsPackage: com.yfan
 * @Description: 工具启动类
 * @Author: YFAN
 * @CreateTime: 2022-10-15 14:35
 * @Version: 1.0
 */
public class ToolsStarter {

    static {
        ConsoleLog.setLevel(Level.INFO);
    }

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
            ITool iTool = ToolsFactory.create(type, config);
            if (iTool == null) {
                Console.log(StrUtil.format("未找到名为[{}]的工具，输入错误！", ToolType.getType(type).getDes()));
            } else {
                TimeInterval timer = DateUtil.timer();
                timer.start();
                iTool.excute();
                Console.log(StrUtil.format("执行完毕，耗时[{}]毫秒。", timer.interval()));
            }
        } catch (Throwable e) {
            Console.log(e);
        } finally {
            Console.log("输入任意键以关闭");
            sc.nextLine();
            sc.close();
            System.exit(0);
        }
    }

}
