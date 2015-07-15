package com.game.bizModule.human.bizServ;

import com.game.bizModule.human.Human;
import com.game.bizModule.human.HumanLog;
import com.game.bizModule.human.HumanStateTable;
import com.game.bizModule.human.io.IoOper_CreateHuman;
import com.game.bizModule.login.LoginStateTable;
import com.game.gameServer.framework.Player;

import java.text.MessageFormat;

/**
 * 创建玩家角色
 *
 * @author hjj2017
 * @since 2015/7/12
 *
 */
interface IServ_CreateHuman {
    /**
     * 异步方式创建角色
     *
     * @param p
     * @param serverName
     * @param humanName
     * @param tmplId
     *
     */
    default void asyncCreateHuman(Player p, String serverName, String humanName, int tmplId) {
        if (p == null ||
            serverName == null ||
            serverName.isEmpty() ||
            humanName == null ||
            humanName.isEmpty()) {
            // 如果参数对象为空,
            // 则直接退出!
            return;
        }

        //
        // 要创建角色, 可以! 但是需要满足几个条件:
        // 1. 登陆验证是 Ok 的;
        // 2. 尚未创建过角色;
        // 3. 角色创建尚未进入 IO 过程;
        // 以上几个条件同时满足才进入建角逻辑!
        //
        // 获取登陆状态表
        LoginStateTable loginStateTbl = p.getPropValOrCreate(LoginStateTable.class);

        if (loginStateTbl._platformUIdOk == false ||
            loginStateTbl._authSuccess == false) {
            // 平台验证都没过,
            // 直接退出!
            HumanLog.LOG.error(MessageFormat.format(
                "玩家 {0} 还没有通过登陆验证",
                p._platformUId
            ));
            return;
        }

        // 获取角色状态表
        HumanStateTable hStateTable = p.getPropValOrCreate(HumanStateTable.class);

        if (hStateTable._serverName.equals(serverName) == false) {
            // 如果服务器名称不对,
            // 则直接退出!
            HumanLog.LOG.error(MessageFormat.format(
                "玩家 {0} 所选择的服务器名称不对! 查询角色时用的是 ''{1}'', 但在创建角色时用的是 ''{2}''",
                p._platformUId,
                hStateTable._serverName,
                serverName
            ));
            return;
        }

        if (hStateTable._execQueryHumanEntryList ||
            hStateTable._hasHuman ||
            hStateTable._execCreateHuman) {
            // 如果正在查询角色入口列表,
            // 或者已经有角色,
            // 直接退出!
            HumanLog.LOG.error(MessageFormat.format(
                "玩家 {0} 已有角色或正在操作中",
                p._platformUId
            ));
            return;
        }

        // 执行建角过程
        hStateTable._execCreateHuman = true;

        // 新建一个角色对象
        Human h = Human.create(p, serverName);
        p.putPropVal(Human.class, h);

        // 创建异步操作
        IoOper_CreateHuman op = new IoOper_CreateHuman();
        op._h = h;
        // 执行异步操作
        HumanServ.OBJ.execute(op);
    }
}