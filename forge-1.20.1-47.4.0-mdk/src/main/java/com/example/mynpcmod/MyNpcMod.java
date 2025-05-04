package com.example.mynpcmod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(MyNpcMod.MODID)
public class MyNpcMod {
    public static final String MODID = "mynpcmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MyNpcMod() {
        LOGGER.info("Hello from MyNpcMod!");
        // 后面我们会在这里注册NPC
    }
}
