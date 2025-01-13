package morepets;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import morepets.interfaces.IPetList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class MorePets implements ModInitializer {
	public static final String MOD_ID = "more-pets";
	public static final String NBT_OWNER_KEY = MOD_ID + "-owner-uuid";
	public static final String NBT_PETS_LIST_KEY = MOD_ID + "-pets-list";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing MorePets");
		this.registerCommands();
	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("pets").executes((context) -> {
				PlayerEntity player = context.getSource().getPlayer();
				if (player == null)
					return 1;
				if (!(player instanceof IPetList))
					return 1;
				List<MobEntity> pets = player.getPets();
				if (pets.size() == 0) {
					context.getSource().sendFeedback(() -> Text.literal("You have no pets"), false);
					return 0;
				}
				context.getSource().sendFeedback(() -> Text.literal("You have the following pets:"), false);
				for (MobEntity pet : pets) {
					context.getSource().sendFeedback(
							() -> Text.literal(String.format("  - %s (%s)", pet.getName().getString(),
									pet.getType().getName().getString())),
							false);
				}
				return 0;
			}));
		});
	}
}