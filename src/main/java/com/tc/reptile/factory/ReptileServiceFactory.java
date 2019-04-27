package com.tc.reptile.factory;

import com.tc.reptile.service.CowlevelReptileService;
import com.tc.reptile.service.GameResReptileService;
import com.tc.reptile.service.ReptileService;
import com.tc.reptile.service.YystvReptileService;
import org.springframework.stereotype.Component;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 14:37 2019/4/26
 */
@Component
public class ReptileServiceFactory {
    private final YystvReptileService yysService;
    private final CowlevelReptileService cowService;
    private final GameResReptileService gameResReptileService;

    public ReptileServiceFactory(YystvReptileService yysService, CowlevelReptileService cowService, GameResReptileService gameResReptileService) {
        this.yysService = yysService;
        this.cowService = cowService;
        this.gameResReptileService = gameResReptileService;
    }

    public ReptileService getService(int webId) {
        switch (webId) {
            case 1:
                return yysService;
            case 2:
                return cowService;
            case 3:
                return gameResReptileService;
            default:
                return null;
        }
    }
}
