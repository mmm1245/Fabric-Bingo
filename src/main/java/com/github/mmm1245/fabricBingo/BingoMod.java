package com.github.mmm1245.fabricBingo;

import com.github.mmm1245.fabricBingo.bingo.BingoConfig;
import com.github.mmm1245.fabricBingo.bingo.BingoLobby;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.plasmid.game.GameType;

public class BingoMod implements ModInitializer {
	public static final String ID = "bingo";
	public static final Logger LOGGER = LogManager.getLogger(ID);
	//public static final PlayerKitStorage KIT_STORAGE = ServerStorage.createStorage(identifier("kits"), new PlayerKitStorage());

	public static final GameType<BingoConfig> TYPE = GameType.register(
			identifier("bingo"),
			BingoLobby::open,
			BingoConfig.CODEC
	);

	@Override
	public void onInitialize() {

	}

	public static Identifier identifier(String value) {
		return new Identifier(ID, value);
	}
}
