package deadlyhunter.bloodarsenalreawakened.common.item.sigil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import wayoftime.bloodmagic.common.item.sigil.ItemSigilToggleable;
import deadlyhunter.bloodarsenalreawakened.common.ConfigHandler;
import deadlyhunter.bloodarsenalreawakened.common.util.helper.PlayerHelper;

public class FlightSigilItem extends ItemSigilToggleable
{
    private static final String ACTIVATED_TAG = "enabled";

    public FlightSigilItem(Properties properties)
    {
        super(properties, ConfigHandler.COMMON.flightSigilCost.get());
    }

    // Aktivierungsstatus speichern / lesen
    public boolean getActivated(ItemStack stack)
    {
        return stack.getOrCreateTag().getBoolean(ACTIVATED_TAG);
    }

    public void setActivated(ItemStack stack, boolean activated)
    {
        stack.getOrCreateTag().putBoolean(ACTIVATED_TAG, activated);
    }

    // Rechtsklick: Sigil aktivieren/deaktivieren
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote)
        {
            boolean active = !getActivated(stack);
            setActivated(stack, active);

            player.sendStatusMessage(new StringTextComponent("Flight Sigil " + (active ? "activated" : "deactivated")), true);
        }

        return ActionResult.resultSuccess(stack);
    }

    // Wird jeden Tick aufgerufen, wenn das Sigil im Inventar oder Hand ist
    @Override
    public void onSigilUpdate(ItemStack stack, World world, PlayerEntity player, int itemSlot, boolean isSelected)
    {
        if (world.isRemote || PlayerHelper.isFakePlayer(player))
            return;

        boolean active = getActivated(stack);

        if (active)
        {
            // Erlaubt Fliegen dauerhaft, auch ohne das Item in der Hand zu halten
            player.abilities.allowFlying = true;

            // Flug starten, falls noch nicht fliegend
            if (!player.abilities.isFlying)
            {
                player.abilities.isFlying = true;
            }

            player.sendPlayerAbilities();
        }
        else
        {
            if (!player.isCreative() && !player.isSpectator())
            {
                // Flug verbieten
                player.abilities.allowFlying = false;

                // Flug beenden, falls noch fliegend
                if (player.abilities.isFlying)
                {
                    player.abilities.isFlying = false;
                }

                // Elytra-Flug stoppen
                player.stopFallFlying();

                player.sendPlayerAbilities();
            }
        }
    }
}
