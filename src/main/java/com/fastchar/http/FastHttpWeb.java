package com.fastchar.http;

import com.fastchar.core.FastEngine;
import com.fastchar.interfaces.IFastWeb;

/**
 * @author 沈建（Janesen）
 * @date 2021/8/13 11:21
 */
public class FastHttpWeb implements IFastWeb {
    @Override
    public void onInit(FastEngine engine) throws Exception {


    }

    @Override
    public void onDestroy(FastEngine engine) throws Exception {

    }

    @Override
    public void onRun(FastEngine engine) throws Exception {
        FastHttp.initConfig();

    }
}
