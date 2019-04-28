package com.tc.reptile.factory;

import com.tc.reptile.service.*;
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
    private final GameLookReptileService gameLookReptileService;
    private final ChuAppReptileService chuAppReptileService;

    public ReptileServiceFactory(YystvReptileService yysService, CowlevelReptileService cowService, GameResReptileService gameResReptileService, GameLookReptileService gameLookReptileService, ChuAppReptileService chuAppReptileService) {
        this.yysService = yysService;
        this.cowService = cowService;
        this.gameResReptileService = gameResReptileService;
        this.gameLookReptileService = gameLookReptileService;
        this.chuAppReptileService = chuAppReptileService;
    }

    public ReptileService getService(int webId) {
        switch (webId) {
            case 1:
                return yysService;
            case 2:
                return cowService;
            case 3:
                return gameResReptileService;
            case 4:
                return gameLookReptileService;
            case 5:
                return chuAppReptileService;
            default:
                return null;
        }
    }
}
