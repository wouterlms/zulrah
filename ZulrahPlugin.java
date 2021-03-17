package net.runelite.client.plugins.zulrah;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.zulrah.enums.Equipment;
import net.runelite.client.plugins.zulrah.enums.ZulrahPosition;
import net.runelite.client.plugins.zulrah.helpers.Inventory;
import net.runelite.client.plugins.zulrah.helpers.LocalPlayer;
import net.runelite.client.plugins.zulrah.helpers.Mouse;
import net.runelite.client.plugins.zulrah.helpers.Prayer;
import net.runelite.client.plugins.zulrah.rotations.*;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.Random;

@PluginDescriptor(
        name = "Zulrah",
        description = "YEET"
)
@Slf4j
public class ZulrahPlugin extends Plugin implements KeyListener {

  private final static int ZULRAH_SHOOT_ANIMATION = 5069;
  private final static int ZULRAH_DIVE_ANIMATION = 5072;
  private final static int ZULRAH_APPEAR_ANIMATION = 5073;
  private final static int ZULRAH_DEATH_ANIMATION = 5804;
  private final static int ZULRAH_RANGE = 2042;
  private final static int ZULRAH_MELEE = 2043;
  private final static int ZULRAH_MAGE = 2044;
  private final static String[] MAGE_EQUIPMENT = {"Trident of the swamp", "Mystic robe top", "Mystic robe bottom", "Unholy book", "Occult necklace"};
  private final static String[] RANGE_EQUIPMENT = {"Black d'hide chaps", "Black d'hide body", "Necklace of anguish", "Toxic blowpipe", "Ava's accumulator"};
  private Mouse mouse;
  private Inventory inventory;
  private Prayer prayer;
  private LocalPlayer player;
  @Inject
  private Client client;

  @Inject
  private KeyManager keyManager;

  @Inject
  private OverlayManager overlayManager;

  @Inject
  private ZulrahOverlay zulrahOverlay;

  @Inject
  private ItemManager itemManager;

  // If at zulrah
  private boolean hasStarted = false;

  // Current zulrah rotation, or null if unknown
  private Rotation rotation = null;

  // Zulrah
  private NPC zulrah;

  private boolean abort = false;

  private boolean isSwitchingGear = false;
  private boolean isSwitchingPrayers = false;
  private boolean isEating = false;
  private boolean isDrinkingAntiVenom = false;
  private boolean isUsingSpecialAttack = false;

  /**
   * zulrah id: 2042
   * melee zulrah id: 2043
   * mage zulrah id: 2044
   * <p>
   * mage projectile 1046
   * range projectile: 1044
   * toxic cloud projectile: 1045
   * spawn snakeling projectile: 1047
   * <p>
   * shoot animation: 5069
   * go down animation: 5072
   * go up animation: 5073
   * melee target animation: 5806
   * melee attack animation: 5807
   * <p>
   * <p>
   * https://i.redd.it/2g6oirse2wny.jpg
   */

  public ZulrahPlugin() {
    mouse = new Mouse(this);
    inventory = new Inventory(this);
    prayer = new Prayer(this);
    player = new LocalPlayer(this);
  }

  @Override
  protected void startUp() throws Exception {
    overlayManager.add(zulrahOverlay);
    keyManager.registerKeyListener(this);
  }

  @Subscribe
  protected void onGameTick(GameTick gameTick) {
    // checken wanneer zulrah is "ready to attack
    // camera rotaten
    // System.out.println("distance to pos: " + player.distanceTo(rotation.getStandPosition().getPosition()));
    if (!hasStarted || abort) {
      return;
    }

        /*
        if (zulrah != null && zulrah.getHealthRatio() == 0) {
            System.out.println("finished!");
            System.out.println("zulrah anim: " + zulrah.getAnimation());
            hasStarted = false;
        }
         */

    rotation.increaseTicks();

    boolean isUnknownRotation = rotation instanceof UnknownRotation;

    // detect which rotation
    if (isUnknownRotation && rotation.isZulrahReady()) {
      if (rotation.getCurrentPhase() == 2) {
        switch (zulrah.getId()) {
          case ZULRAH_RANGE:
            rotation = new RotationCharlie();
            rotation.setCurrentPhase(2);
            break;
          case ZULRAH_MAGE:
            rotation = new RotationDelta();
            rotation.setCurrentPhase(2);
            break;
        }
      } else if (rotation.getCurrentPhase() == 4) {
        if (isEqualLocalPoint(zulrah.getLocalLocation(), ZulrahPosition.NORTH.getPosition())) {
          rotation = new RotationAlpha();
        } else {
          rotation = new RotationBravo();
        }
        rotation.setCurrentPhase(4);
      }
    }

    // We don't know which rotation it will be, so we'll wait
    if ((rotation.getCurrentPhase() == 2 && !rotation.isZulrahReady())) {
      return;
    }

    String[] equipment = rotation.getEquipment() == Equipment.MAGE ? MAGE_EQUIPMENT : RANGE_EQUIPMENT;

    boolean hasCorrectEquipment = !inventory.containsAnyOf(equipment);
    boolean shouldAttack =
            !rotation.getDontAttack() &&
                    !player.hasTarget() &&
                    player.distanceTo(rotation.getStandPosition().getPosition()) < 4 &&
                    rotation.isZulrahReady();

    boolean shouldPrayPot = inventory.containsAnyOf(new String[]{"Prayer potion(1)", "Prayer potion(2)", "Prayer potion(3)", "Prayer potion(4)"}) && client.getBoostedSkillLevel(Skill.PRAYER) < 10 && !isEating;
    boolean isPoisened = client.getVar(VarPlayer.IS_POISONED) > 0;

    // misschien kijken of spec aan staat of niet
    boolean canSpec = player.getSpecialAttack() >= 50 &&
            player.getHealth() < 80 &&
            rotation.getEquipment() == Equipment.RANGE &&
            !player.isSpecialAttackEnabled();


    // walk to location
    if (!player.isMoving() && rotation != null && !isEqualLocalPoint(client.getLocalPlayer().getLocalLocation(), rotation.getStandPosition().getPosition())) {
      mouse.clickLocalPoint(rotation.getStandPosition().getPosition(), true);
    }
    // use correct prayer
    else if (
            !isSwitchingPrayers &&
                    rotation.getPrayers().size() != 0 &&
                    (!prayer.areAllEnabled(rotation.getPrayers()) || prayer.areAnyEnabledExcept(rotation.getPrayers()))
    ) {
      prayer.enable(rotation.getPrayers(), true);
    }
    // eat
    else if (player.getHealth() < 60 && inventory.contains("Shark")) {
      inventory.clickItem("Shark");
    }
    // equip correct gear
    else if (!hasCorrectEquipment && !isSwitchingGear) {
      inventory.clickItems(equipment);
    }
    // pray pot
    else if (shouldPrayPot) {
      inventory.clickFirstWhichContains("Prayer potion");
    }
    // attack
    else if (shouldAttack) {
      mouse.clickNpc(zulrah);
    }
    // venomed
    else if (isPoisened) {
      drinkAntiVenom();
    }
    // spec
    else if (canSpec) {
      useSpecialAttack();
    }
  }

  @Subscribe
  private void onNpcSpawned(NpcSpawned npcSpawned) {
    NPC npc = npcSpawned.getNpc();

    if (npc.getName() != null && npc.getName().equalsIgnoreCase("zulrah")) {
      System.out.println("@set Zulrah");
      zulrah = npc;
    }
  }

  @Subscribe
  private void onNpcDespawned(NpcDespawned npcDespawned) {
    NPC npc = npcDespawned.getNpc();

    if (npc.getName() != null && npc.getName().equalsIgnoreCase("zulrah")) {
      System.out.println("@bye Zulrah");
      hasStarted = false;
      rotation = null;
    }
  }

  @Subscribe
  private void onAnimationChanged(AnimationChanged animationChanged) {
    Actor actor = animationChanged.getActor();

    if (actor != null && actor.getName() != null && actor instanceof NPC && actor.equals(zulrah)) {
      if (actor.getAnimation() == ZULRAH_SHOOT_ANIMATION) {
        rotation.increaseAnimationCount();
      }

      if (actor.getAnimation() == ZULRAH_DIVE_ANIMATION) {
        rotation.setIsZulrahReady(false);
        rotation.nextPhase();
      }

      if (actor.getAnimation() == ZULRAH_APPEAR_ANIMATION) {
        rotation.setIsZulrahReady(true);
      }

      if (actor.getAnimation() == ZULRAH_DEATH_ANIMATION) {
        System.out.println("@Zulrah dead");
        hasStarted = false;
        rotation = null;
      }
    }
  }

  public boolean isEqualLocalPoint(LocalPoint p1, LocalPoint p2) {
    if (p1 == null || p2 == null) {
      return false;
    }

    return p1.getX() == p2.getX() && p1.getY() == p2.getY();
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {
    //System.out.println(e.getKeyCode());

    if (e.getKeyCode() == 61 && hasStarted) {
      abort = true;
    }

    if (e.getKeyCode() == 61 && !hasStarted) {
      hasStarted = true;
      rotation = new UnknownRotation();
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {

  }

  private void drinkAntiVenom() {
    Widget healthOrb = client.getWidget(WidgetInfo.MINIMAP_HEALTH_ORB);

    int x = healthOrb.getCanvasLocation().getX();
    int y = healthOrb.getCanvasLocation().getY();

    int width = healthOrb.getWidth();
    int height = healthOrb.getHeight();

    final int xx = randomNumber(x + 10, x + width - 10);
    final int yy = randomNumber(y + 10, y + height - 10);

    (new Thread(() -> {
      isEating = true;
      mouse.clickAt(xx, yy);
      sleep(600);
      isEating = false;
    })).start();
  }

  private void useSpecialAttack() {
    Widget healthOrb = client.getWidget(WidgetInfo.MINIMAP_SPEC_ORB);

    int x = healthOrb.getCanvasLocation().getX();
    int y = healthOrb.getCanvasLocation().getY();

    int width = healthOrb.getWidth();
    int height = healthOrb.getHeight();

    final int xx = randomNumber(x + 10, x + width - 10);
    final int yy = randomNumber(y + 10, y + height - 10);

    (new Thread(() -> {
      isUsingSpecialAttack = true;
      mouse.clickAt(xx, yy);
      sleep(600);
      isUsingSpecialAttack = false;
    })).start();
  }

  public int randomNumber(int min, int max) {
    return new Random().nextInt(max - min + 1) + min;
  }

  public void sleep(final int ms) {
    final int random = randomNumber(ms - (ms / 10), ms + (ms / 10));

    try {
      Thread.sleep(random);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public int getScreenOffsetX() {
    return client.getCanvas().getLocationOnScreen().x;
  }

  public int getScreenOffsetY() {
    return client.getCanvas().getLocationOnScreen().y;
  }

  public Client getClient() {
    return client;
  }

  public Mouse getMouse() {
    return mouse;
  }

  public ItemManager getItemManager() {
    return itemManager;
  }

  public void setIsSwitchingGear(boolean switchingGear) {
    isSwitchingGear = switchingGear;
  }

  public void setIsSwitchingPrayers(boolean switchingPrayers) {
    isSwitchingPrayers = switchingPrayers;
  }

  public void setIsEating(boolean eating) {
    isEating = eating;
  }
}
