package deadlyhunter.bloodarsenalreawakened.common.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import deadlyhunter.bloodarsenalreawakened.common.item.sigil.FlightSigilItem;
import net.minecraft.item.ItemStack;

@Mod.EventBusSubscriber
public class FlightSigilEvents
{
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event)
    {
        PlayerEntity player = event.player;

        // Prüfen, ob Spieler fliegen darf nur wenn Sigil aktiviert und im Inventar ist
        boolean canFly = false;

        for (ItemStack stack : player.inventory.mainInventory)
        {
            if (stack.getItem() instanceof FlightSigilItem)
            {
                FlightSigilItem sigil = (FlightSigilItem) stack.getItem();

                if (sigil.getActivated(stack))
                {
                    canFly = true;
                    break;
                }
            }
        }

        if (!canFly)
        {
            if (!player.isCreative() && !player.isSpectator())
            {
                player.abilities.allowFlying = false;
                player.abilities.isFlying = false;
                player.stopFallFlying();
                player.sendPlayerAbilities();
            }
        }
    }
}
