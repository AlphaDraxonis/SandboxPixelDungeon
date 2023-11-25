package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpawnerMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.BuffIndicatorEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.FistSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.LotusLevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.MobStateSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.QuestSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.DestCellSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.LootTableComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.WndEditStats;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Buffs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelectorList;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerFloatModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PylonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditMobComp extends DefaultEditComp<Mob> {


    //TODO demon spawner should set their cooldown here!
    private final ItemSelector statueWeapon, statueArmor;
    private final ItemSelector thiefItem;
    private final MobStateSpinner mobStateSpinner;
    private final Spinner playerAlignment;
    private final RedButton addBuffs, editStats;

    private final ItemContainer<Item> mimicItems;
    private final LotusLevelSpinner lotusLevelSpinner;
    private final Spinner sheepLifespan;
    private final QuestSpinner questSpinner;
    private final ItemSelector questItem1, questItem2;
    private final CheckBox spawnQuestRoom;

    private final Spinner sentryRange, sentryDelay;
    private final Spinner abilityCooldown;
    private final ItemSelector summonMob;
    private final Spinner tenguPhase, tenguRange;
    private final CheckBox dm300destroyWalls;
    private final Spinner dm300pylonsNeeded;
    private final Spinner yogSpawnersAlive;
    private final ItemSelectorList<MobItem> yogNormalFists, yogChallengeFists;
    private final CheckBox pylonAlwaysActive;

    private final ItemSelectorList<Item> blacksmithQuestRewards;

    private final Component[] comps;

    public EditMobComp(Mob mob) {
        super(mob);

        if (mob instanceof Statue) {
            statueWeapon = new ItemSelector(" " + Messages.get(EditMobComp.class, "weapon") + ":",
                    Weapon.class, ((Statue) mob).weapon, ItemSelector.NullTypeSelector.NONE) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    ((Statue) mob).weapon = (Weapon) selectedItem;
                    EditMobComp.this.updateObj();
                }
            };
            add(statueWeapon);
            if (mob instanceof ArmoredStatue) {
                statueArmor = new ItemSelector(" " + Messages.get(EditMobComp.class, "armor") + ":",
                        Armor.class, ((ArmoredStatue) mob).armor, ItemSelector.NullTypeSelector.NONE) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((ArmoredStatue) mob).armor = (Armor) selectedItem;
                        EditMobComp.this.updateObj();
                    }
                };
                add(statueArmor);
            } else statueArmor = null;
        } else {
            statueWeapon = null;
            statueArmor = null;
        }

        if (mob instanceof Thief) {
            thiefItem = new ItemSelector(" " + Messages.get(EditMobComp.class, "item") + ":",
                    Item.class, ((Thief) mob).item, ItemSelector.NullTypeSelector.NONE) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    ((Thief) mob).item = selectedItem;
                    EditMobComp.this.updateObj();
                }

                @Override
                public void change() {
                    EditorScene.selectItem(selector);
                }
            };
            add(thiefItem);
        } else thiefItem = null;

        if (mob instanceof Mimic) {
            if (((Mimic) mob).items == null) ((Mimic) mob).items = new ArrayList<>();
            ArrayList<Item> mimicItemList = ((Mimic) mob).items;
            mimicItems = new ItemContainer<Item>(mimicItemList, this, true) {
                @Override
                protected void doAddItem(Item item) {
                    //From Heap#drop()
                    if (item.stackable) {
                        for (Item i : mimicItemList) {
                            if (i.isSimilar(item)) {
                                item = i.merge(item);
                                break;
                            }
                        }
                        mimicItemList.remove(item);
                    }
                    mimicItemList.add(item);
                }
            };
            add(mimicItems);
        } else mimicItems = null;

        if (!(mob instanceof Pylon)) {//mob (Pylon) should not be instanceof QuestNPC!!!
            mobStateSpinner = new MobStateSpinner(mob);
            add(mobStateSpinner);
            if (mob instanceof Mimic) mobStateSpinner.addChangeListener(this::updateObj);
        } else mobStateSpinner = null;//mob instanceof QuestNPC MUST be false in this line

        if (mob instanceof WandOfRegrowth.Lotus) {
            WandOfRegrowth.Lotus lotus = (WandOfRegrowth.Lotus) mob;
            lotusLevelSpinner = new LotusLevelSpinner(lotus) {
                private int lastValueUpdated;

                @Override
                protected void updateDesc(boolean forceUpdate) {
                    if (lastValueUpdated != lotus.getLvl() && (forceUpdate || lotus.getLvl() % 10 == 0)) {
                        updateObj();
                        lastValueUpdated = lotus.getLvl();
                    }
                }
            };
            add(lotusLevelSpinner);
        } else {
            lotusLevelSpinner = null;
        }

        if (mob instanceof Sheep) {
            sheepLifespan = new Spinner(
                    new SpinnerIntegerModel(0, 600, (int) ((Sheep) mob).lifespan, 1, false, null) {
                        @Override
                        public float getInputFieldWith(float height) {
                            return height * 2.5f;
                        }

                        @Override
                        public int getClicksPerSecondWhileHolding() {
                            return 120;
                        }
                    },
                    " " + Messages.get(EditMobComp.class, "sheep_lifespan") + ":", 9);
            sheepLifespan.addChangeListener(() -> ((Sheep) mob).lifespan = (int) sheepLifespan.getValue());
            sheepLifespan.setAlignmentSpinnerX(1f);
            add(sheepLifespan);
        } else sheepLifespan = null;

        if (mob instanceof QuestNPC<?>) {
            questSpinner = new QuestSpinner(((QuestNPC<?>) mob).quest, h -> mobStateSpinner.getCurrentInputFieldWith());
            add(questSpinner);
            if (mob instanceof Wandmaker) {
                if (mob.pos < 0) {
                    spawnQuestRoom = new CheckBox(Messages.get(EditMobComp.class, "spawn_quest_room")) {
                        @Override
                        public void checked(boolean value) {
                            super.checked(value);
                            ((Wandmaker) mob).quest.spawnQuestRoom = value;
                        }
                    };
                    spawnQuestRoom.checked(((Wandmaker) mob).quest.spawnQuestRoom);
                    add(spawnQuestRoom);
                    questSpinner.addChangeListener(() -> {
                        if ("none".equals(questSpinner.getValue())) {
                            spawnQuestRoom.enable(false);
                            spawnQuestRoom.checked(false);
                        } else spawnQuestRoom.enable(true);
                    });
                } else spawnQuestRoom = null;
                questItem1 = new ItemSelector(" " + Messages.get(EditMobComp.class, "wand_1") + ":",
                        Wand.class, ((Wandmaker) mob).quest.wand1, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Wandmaker) mob).quest.wand1 = (Wand) selectedItem;
                        EditMobComp.this.updateObj();
                    }
                };
                add(questItem1);
                questItem2 = new ItemSelector(" " + Messages.get(EditMobComp.class, "wand_2") + ":",
                        Wand.class, ((Wandmaker) mob).quest.wand2, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Wandmaker) mob).quest.wand2 = (Wand) selectedItem;
                        EditMobComp.this.updateObj();
                    }
                };
                add(questItem2);
                questItem1.setShowWhenNull(ItemSpriteSheet.SOMETHING);
                questItem2.setShowWhenNull(ItemSpriteSheet.SOMETHING);
                blacksmithQuestRewards = null;

            } else if (mob instanceof Ghost) {
                questItem1 = new ItemSelector(" " + Messages.get(EditMobComp.class, "weapon") + ":",
                        Weapon.class, ((Ghost) mob).quest.weapon, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Ghost) mob).quest.weapon = (Weapon) selectedItem;
                        EditMobComp.this.updateObj();
                    }
                };
                add(questItem1);
                questItem2 = new ItemSelector(" " + Messages.get(EditMobComp.class, "armor") + ":",
                        Armor.class, ((Ghost) mob).quest.armor, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Ghost) mob).quest.armor = (Armor) selectedItem;
                        EditMobComp.this.updateObj();
                    }
                };
                add(questItem2);
                questItem1.setShowWhenNull(ItemSpriteSheet.SOMETHING);
                questItem2.setShowWhenNull(ItemSpriteSheet.SOMETHING);
                spawnQuestRoom = null;
                blacksmithQuestRewards = null;
            } else if (mob instanceof Imp) {
                questItem1 = new ItemSelector(" " + Messages.get(EditMobComp.class, "ring") + ":", Ring.class,
                        ((Imp) mob).quest.reward, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Imp) mob).quest.reward = (Ring) selectedItem;
                        EditMobComp.this.updateObj();
                    }
                };
                add(questItem1);
                questItem1.setShowWhenNull(ItemSpriteSheet.SOMETHING);
                questItem2 = null;
                spawnQuestRoom = null;
                blacksmithQuestRewards = null;
            } else {
                spawnQuestRoom = null;
                questItem1 = null;
                questItem2 = null;
                if (mob instanceof Blacksmith) {
                    BlacksmithQuest quest = ((Blacksmith) mob).quest;
                    if (quest.smithRewards == null) quest.smithRewards = new ArrayList<>(3);
                    while (quest.smithRewards.size() < 3) quest.smithRewards.add(new Item());
                    blacksmithQuestRewards = new ItemSelectorList<Item>(quest.smithRewards, " " + Messages.get(EditMobComp.class, "blacksmith_items") + ":", 9) {
                        @Override
                        public void change(int index) {
                            ItemSelector.showSelectWindow(new WndBag.ItemSelector() {
                                @Override
                                public String textPrompt() {
                                    return null;
                                }

                                @Override
                                public boolean itemSelectable(Item item) {
                                    Item i = item instanceof ItemItem ? ((ItemItem) item).item() : item;
                                    return i instanceof Weapon && !(i instanceof MissileWeapon || i instanceof SpiritBow)
                                            || i instanceof Armor;
                                }

                                @Override
                                public void onSelect(Item item) {
                                    if (item == EditorItem.RANDOM_ITEM) item = new Item();
                                    else if (item instanceof ItemItem) {
                                        item = ((ItemItem) item).item().getCopy();
                                    } else return;
                                    list.set(index, item);
                                    updateItem(index);
                                }

                                @Override
                                public boolean addOtherTabs() {
                                    return false;
                                }

                                @Override
                                public Class<? extends Bag> preferredBag() {
                                    return Items.bag.getClass();
                                }
                            }, ItemSelector.NullTypeSelector.RANDOM, Item.class, Items.bag, new HashSet<>());
                        }

                        @Override
                        public synchronized void destroy() {
                            super.destroy();
                            Set<Item> toRemove = new HashSet<>(4);
                            for (Item i : list) {
                                if (i.getClass() == Item.class) toRemove.add(i);
                            }
                            list.removeAll(toRemove);
                        }
                    };
                    add(blacksmithQuestRewards);
                } else blacksmithQuestRewards = null;
            }
        } else {
            questSpinner = null;
            spawnQuestRoom = null;
            questItem1 = null;
            questItem2 = null;
            blacksmithQuestRewards = null;
        }

        if (mob instanceof SentryRoom.Sentry) {
            sentryRange = new Spinner(new SpinnerIntegerModel(1, 100, ((SentryRoom.Sentry) mob).range, 1, false, null) {
                @Override
                public float getInputFieldWith(float height) {
                    return height * 2.5f;
                }
            },
                    " " + Messages.get(EditMobComp.class, "range") + ":", 9);
            sentryRange.addChangeListener(() -> ((SentryRoom.Sentry) mob).range = (int) sentryRange.getValue());
            add(sentryRange);
            sentryDelay = new Spinner(new SpinnerFloatModel(0f, 100f, ((SentryRoom.Sentry) mob).getInitialChargeDelay() - 1, false),
                    " " + Messages.get(EditMobComp.class, "delay") + ":", 9);
            sentryDelay.addChangeListener(() -> ((SentryRoom.Sentry) mob).setInitialChargeDelay(((SpinnerFloatModel) sentryDelay.getModel()).getAsFloat() + 1));
            add(sentryDelay);
        } else {
            sentryRange = null;
            sentryDelay = null;
        }

        if (mob instanceof Guard) {
            abilityCooldown = new Spinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((Guard) mob).maxChainCooldown, 1, false, null) {
                {
                    setAbsoluteMinimum(1f);
                }
                @Override
                public float getInputFieldWith(float height) {
                    return height * 2.5f;
                }
            },
                    " " + Messages.get(EditMobComp.class, "chains_cd") + ":", 9);
            abilityCooldown.addChangeListener(() -> ((Guard) mob).maxChainCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);
        } else if (mob instanceof DM200) {
            abilityCooldown = new Spinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((DM200) mob).maxVentCooldown, 1, false, null) {
                {
                    setAbsoluteMinimum(1f);
                }
                @Override
                public float getInputFieldWith(float height) {
                    return height * 2.5f;
                }
            },
                    " " + Messages.get(EditMobComp.class, "vent_cd") + ":", 9);
            abilityCooldown.addChangeListener(() -> ((DM200) mob).maxVentCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);
        } else if (mob instanceof Golem) {
            abilityCooldown = new Spinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((Golem) mob).maxTeleCooldown, 1, false, null) {
                {
                    setAbsoluteMinimum(1f);
                }
                @Override
                public float getInputFieldWith(float height) {
                    return height * 2.5f;
                }
            },
                    " " + Messages.get(EditMobComp.class, "tele_cd") + ":", 9);
            abilityCooldown.addChangeListener(() -> ((Golem) mob).maxTeleCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);
        } else if (mob instanceof com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner) {
            com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner m = (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner) mob;
            abilityCooldown = new Spinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, m.maxWebCoolDown, 1, false, null) {
                {
                    setAbsoluteMinimum(1f);
                }
                @Override
                public float getInputFieldWith(float height) {
                    return height * 2.5f;
                }
            },
                    " " + Messages.get(EditMobComp.class, "web_cd") + ":", 9);
            abilityCooldown.addChangeListener(() -> m.maxWebCoolDown = (int) abilityCooldown.getValue());
            add(abilityCooldown);
        }
        else abilityCooldown = null;

        if (mob instanceof SpawnerMob) {
            Mob necroMob = ((SpawnerMob) mob).summonTemplate;
            summonMob = new ItemSelector(Messages.get(EditMobComp.class, "summon_mob"), MobItem.class, necroMob == null ? null : new MobItem(necroMob), ItemSelector.NullTypeSelector.NOTHING) {
                {
                    selector.preferredBag = Mobs.bag.getClass();
                }

                @Override
                public void change() {
                    EditorScene.selectItem(selector);
                }

                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    ((SpawnerMob) mob).summonTemplate = selectedItem == EditorItem.NULL_ITEM ? null
                            : selectedItem instanceof MobItem ? ((MobItem) selectedItem).mob() : null;
                    updateObj();
                }
            };
            add(summonMob);
        } else summonMob = null;

        if (mob instanceof Tengu) {
            tenguPhase = new Spinner(new SpinnerIntegerModel(1, 2, ((Tengu) mob).phase, 1, true, null) {
                @Override
                public float getInputFieldWith(float height) {
                    return height * 2.5f;
                }

                @Override
                public void displayInputAnyNumberDialog() {
                    //disabled
                }
            },
                    " " + Messages.get(EditMobComp.class, "phase") + ":", 9);
            tenguPhase.addChangeListener(() -> ((Tengu) mob).phase = (int) tenguPhase.getValue());
            add(tenguPhase);
            tenguRange = new Spinner(new SpinnerIntegerModel(1, 100, ((Tengu) mob).arenaRadius, 1, false, null) {
                @Override
                public float getInputFieldWith(float height) {
                    return height * 2.5f;
                }
            },
                    " " + Messages.get(EditMobComp.class, "range") + ":", 9);
            tenguRange.addChangeListener(() -> ((Tengu) mob).arenaRadius = (int) tenguRange.getValue());
            add(tenguRange);
        } else {
            tenguPhase = null;
            tenguRange = null;
        }

        if (mob instanceof DM300) {
            dm300destroyWalls = new CheckBox(Messages.get(EditMobComp.class, "dm300_destroy_walls")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    ((DM300) mob).destroyWalls = value;
                }
            };
            dm300destroyWalls.checked(((DM300) mob).destroyWalls);
            add(dm300destroyWalls);

            dm300pylonsNeeded = new Spinner(new SpinnerIntegerModel(0, 4, ((DM300) mob).pylonsNeeded, 1, false, null) {
                @Override
                public float getInputFieldWith(float height) {
                    return height * 2.5f;
                }
            },
                    " " + Messages.get(EditMobComp.class, "dm300_pylons_needed") + ":", 9);
            dm300pylonsNeeded.addChangeListener(() -> ((DM300) mob).pylonsNeeded = (int) dm300pylonsNeeded.getValue());
            add(dm300pylonsNeeded);

        } else {
            dm300destroyWalls = null;
            dm300pylonsNeeded = null;
        }

        if (mob instanceof YogDzewa) {
            int spAlive = ((YogDzewa) mob).spawnersAlive;
            yogSpawnersAlive = new Spinner(new SpinnerIntegerModel(0, 4, spAlive == -1 ? null : spAlive, 1, true,
                    Messages.get(DestCellSpinner.class, "default")) {
                @Override
                public float getInputFieldWith(float height) {
                    return height * 2.5f;
                }

                @Override
                public void displayInputAnyNumberDialog() {
                }
            },
                    " " + Messages.get(EditMobComp.class, "spawners_alive") + ":", 7 + (PixelScene.landscape() ? 2 : 0));
            yogSpawnersAlive.addChangeListener(() -> {
                Integer val = (Integer) yogSpawnersAlive.getValue();
                if (val == null) val = -1;
                ((YogDzewa) mob).spawnersAlive = val;
            });
            yogSpawnersAlive.setButtonWidth(10f);
            add(yogSpawnersAlive);

            yogNormalFists = new FistSelector(((YogDzewa) mob).fistSummons, " " + Messages.get(EditMobComp.class, "normal_fists") + ":", 7 + (PixelScene.landscape() ? 2 : 0));
            add(yogNormalFists);
            yogChallengeFists = new FistSelector(((YogDzewa) mob).challengeSummons, " " + Messages.get(EditMobComp.class, "challenge_fists") + ":", 7 + (PixelScene.landscape() ? 2 : 0));
            add(yogChallengeFists);
        } else {
            yogSpawnersAlive = null;
            yogNormalFists = null;
            yogChallengeFists = null;
        }

        if (mob instanceof Pylon) {
            pylonAlwaysActive = new CheckBox(Messages.get(EditMobComp.class, "pylon_always_active")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    ((Pylon) mob).alwaysActive = value;
                    updateObj();
                }
            };
            pylonAlwaysActive.checked(((Pylon) mob).alwaysActive);
            add(pylonAlwaysActive);
        } else {
            pylonAlwaysActive = null;
        }

        if (!(mob instanceof QuestNPC || mob instanceof RatKing || mob instanceof Sheep ||
                mob instanceof WandOfRegrowth.Lotus || mob instanceof Shopkeeper || mob instanceof SentryRoom.Sentry)) {

            if (!(mob instanceof YogFist || mob instanceof Mimic)) {
                playerAlignment = new Spinner(new SpinnerIntegerModel(Mob.NORMAL_ALIGNMENT, Mob.FRIENDLY_ALIGNMENT, mob.playerAlignment, 1, true, null) {
                    @Override
                    public void displayInputAnyNumberDialog() {
                    }

                    @Override
                    public String getDisplayString() {
                        switch ((int) getValue()) {
                            case Mob.NORMAL_ALIGNMENT: return Messages.get(EditMobComp.class, "player_alignment_normal");
                            case Mob.NEUTRAL_ALIGNMENT: return Messages.get(EditMobComp.class, "player_alignment_neutral");
                            case Mob.FRIENDLY_ALIGNMENT: return Messages.get(EditMobComp.class, "player_alignment_friendly");
                        }
                        return super.getDisplayString();
                    }

                    @Override
                    public float getInputFieldWith(float height) {
                        return height * 2.5f;
                    }
                },
                        " " + Messages.get(EditMobComp.class, "player_alignment") + ":", 10);
                playerAlignment.addChangeListener(()-> {
                    mob.setPlayerAlignment((int) playerAlignment.getValue());
                    updateObj();
                });
                add(playerAlignment);
            } else playerAlignment = null;

            if (!(mob instanceof Tengu || mob instanceof Pylon)) {
                addBuffs = new RedButton(Messages.get(EditMobComp.class, "add_buff")) {
                    @Override
                    protected void onClick() {
                        Set<Class<? extends Buff>> buffsToIgnore = new HashSet<>();
                        for (Buff b : mob.buffs()) buffsToIgnore.add(b.getClass());
                        for (Class<?> c : mob.immunities) {
                            if (Buff.class.isAssignableFrom(c)) {
                                buffsToIgnore.add((Class<? extends Buff>) c);
                            }
                        }

                        Window w = new WndChooseOneInCategories(
                                Messages.get(EditMobComp.class, "add_buff_title"), "",
                                Buffs.getAllBuffs(buffsToIgnore), Buffs.getCatNames()) {
                            @Override
                            protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] category) {
                                ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[category.length];
                                for (int i = 0; i < ret.length; i++) {
                                    Buff b = Reflection.newInstance((Class<? extends Buff>) category[i]);
                                    ret[i] = new ChooseOneInCategoriesBody.BtnRow(b.name(), b.desc(), new BuffIcon(b, true)) {
                                        @Override
                                        protected void onClick() {
                                            finish();
                                            Buff.affect(mob, b.getClass()).permanent = true;
                                            updateObj();
                                        }
                                    };
                                    ret[i].setLeftJustify(true);
                                }
                                return ret;
                            }
                        };
                        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                        else Game.scene().addToFront(w);
                    }
                };
                add(addBuffs);
            } else addBuffs = null;
        } else {
            playerAlignment = null;
            addBuffs = null;
        }

        Mob defaultStats = DefaultStatsCache.getDefaultObject(mob.getClass());
        if (defaultStats != null) {
            editStats = new RedButton(Messages.get(EditMobComp.class, "edit_stats")) {
                @Override
                protected void onClick() {
                    Window w = WndEditStats.createWindow((int) Math.ceil(EditMobComp.this.width),
                            EditorUtilies.getParentWindow(EditMobComp.this).getOffset().y, defaultStats, mob, () -> updateObj());
                    if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                    else Game.scene().addToFront(w);
                }
            };
            add(editStats);
        } else editStats = null;

        comps = new Component[]{statueWeapon, statueArmor, thiefItem, mimicItems, summonMob,
                lotusLevelSpinner, sheepLifespan,
                yogSpawnersAlive, yogNormalFists, yogChallengeFists,
                mobStateSpinner, playerAlignment, sentryRange, sentryDelay, abilityCooldown,
                tenguPhase, tenguRange,
                questSpinner, questItem1, questItem2, spawnQuestRoom, blacksmithQuestRewards,
                dm300pylonsNeeded, dm300destroyWalls, pylonAlwaysActive, addBuffs, editStats};
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(comps);
    }

    @Override
    protected Component createTitle() {
        return new MobTitleEditor(obj);
    }

    @Override
    protected String createDescription() {
        return obj.info();
    }

    @Override
    public Image getIcon() {
        return obj.sprite();
    }

    @Override
    public void updateObj() {
        if (title instanceof MobTitleEditor) {
            if (obj instanceof ArmoredStatue) {
                Armor armor = ((ArmoredStatue) obj).armor;
                ((StatueSprite) ((MobTitleEditor) title).image).setArmor(armor == null ? 0 : armor.tier);
            } else if (obj instanceof Mimic) {
                MimicSprite sprite = (MimicSprite) ((MobTitleEditor) title).image;
                if (obj.state != obj.PASSIVE) sprite.idle();
                else sprite.hideMimic();
            } else if (obj instanceof Pylon) {
                Pylon pylon = (Pylon) obj;
                if (pylon.alwaysActive || pylon.alignment != Char.Alignment.NEUTRAL && obj.playerAlignment == Mob.NORMAL_ALIGNMENT)
                    ((PylonSprite) pylon.sprite).activate();
                else ((PylonSprite) pylon.sprite).deactivate();
            }
        }
        desc.text(createDescription());
        if (statueWeapon != null) statueWeapon.updateItem();
        if (statueArmor != null) {
            Armor armor = ((ArmoredStatue) obj).armor;
            if (obj.sprite != null)
                ((StatueSprite) obj.sprite).setArmor(armor == null ? 0 : armor.tier);
            statueArmor.updateItem();
        }
        if (obj instanceof Mimic) {
            if (obj.state != obj.PASSIVE) obj.sprite.idle();
            else ((MimicSprite) obj.sprite).hideMimic();
        }
        if (pylonAlwaysActive != null) {
            boolean active = false;
            Pylon pylon = (Pylon) obj;
            if (obj.playerAlignment == Mob.NORMAL_ALIGNMENT) {
                pylonAlwaysActive.enable(true);
                active = pylon.alignment != Char.Alignment.NEUTRAL;
            } else {
                if (!pylonAlwaysActive.active) {
                    pylonAlwaysActive.enable(false);
                    pylonAlwaysActive.checked(true);
                }
            }
            if (active || pylon.alwaysActive) ((PylonSprite) pylon.sprite).activate();
            else ((PylonSprite) pylon.sprite).deactivate();
        }

        super.updateObj();
    }

    public static boolean areEqual(Mob a, Mob b) {
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.state.getClass() != b.state.getClass()) return false;
        if (!DefaultStatsCache.areStatsEqual(a, b)) return false;
        if (a.alignment != b.alignment) return false;
        if (a.playerAlignment != b.playerAlignment) return false;
        Set<Class<? extends Buff>> aBuffs = new HashSet<>(4);
        Set<Class<? extends Buff>> bBuffs = new HashSet<>(4);
        for (Buff buff : a.buffs()) aBuffs.add(buff.getClass());
        for (Buff buff : b.buffs()) bBuffs.add(buff.getClass());
        if (!bBuffs.equals(aBuffs)) return false;//only very simple, does not compare any values, just the types!!
        if (a.loot instanceof LootTableComp.CustomLootInfo) {
            if (!a.loot.equals(b.loot)) return false;
        } else if (b.loot instanceof LootTableComp.CustomLootInfo) return false;

        if (a instanceof Statue) {
            if (!EditItemComp.areEqual(((Statue) a).weapon, ((Statue) b).weapon)) return false;
            return !(a instanceof ArmoredStatue)
                    || EditItemComp.areEqual(((ArmoredStatue) a).armor, ((ArmoredStatue) b).armor);
        } else if (a instanceof Thief) {
            return EditItemComp.areEqual(((Thief) a).item, ((Thief) b).item);
        } else if (a instanceof Mimic) {
            return DefaultEditComp.isItemListEqual(((Mimic) a).items, ((Mimic) b).items);
        } else if (a instanceof YogDzewa) {
            if (((YogDzewa) a).spawnersAlive != ((YogDzewa) b).spawnersAlive) return false;
            if (!isMobListEqual(((YogDzewa) a).fistSummons, ((YogDzewa) b).fistSummons)) return false;
            return isMobListEqual(((YogDzewa) a).challengeSummons, ((YogDzewa) b).challengeSummons);
        } else if (a instanceof Necromancer) {
            return EditMobComp.areEqual(((Necromancer) a).summonTemplate, ((Necromancer) b).summonTemplate);
        } else if (a instanceof Tengu) {
            return ((Tengu) a).arenaRadius == ((Tengu) b).arenaRadius && ((Tengu) a).phase == ((Tengu) b).phase;
        } else if (a instanceof DM300) {
            return ((DM300) a).destroyWalls == ((DM300) b).destroyWalls
                    && ((DM300) a).pylonsNeeded == ((DM300) b).pylonsNeeded;
        } else if (a instanceof Pylon) {
            return ((Pylon) a).alwaysActive == ((Pylon) b).alwaysActive;
        }
        return true;
    }

    public static boolean isMobListEqual(List<? extends Mob> a, List<? extends Mob> b) {
        if (a == null) return b == null || b.size() == 0;
        if (b == null) return a.size() == 0;
        if (a.size() != b.size()) return false;
        int index = 0;
        for (Mob m : a) {
            if (!EditMobComp.areEqual(m, b.get(index))) return false;
            index++;
        }
        return true;
    }

    private class MobTitleEditor extends WndInfoMob.MobTitle {

        public MobTitleEditor(Mob mob) {
            super(mob, false);
        }

        @Override
        protected BuffIndicator createBuffIndicator(Mob mob, boolean large) {
            return new BuffIndicatorEditor(mob, large, EditMobComp.this);
        }

        protected String createTitle(Mob mob) {
            return super.createTitle(mob) + EditorUtilies.appendCellToString(mob.pos);
        }
    }
}