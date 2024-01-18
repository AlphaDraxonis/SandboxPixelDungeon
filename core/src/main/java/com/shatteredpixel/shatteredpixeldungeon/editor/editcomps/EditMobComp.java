package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HeroMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpawnerMob;
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
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.ChangeMobNameDesc;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.FistSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.HeroClassSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.ItemSelectables;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.LotusLevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.MobStateSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.QuestSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.DestCellSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.WndEditStats;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Buffs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.PermaGas;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.HeroSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelectorList;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerFloatModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
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
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PylonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpawnerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EditMobComp extends DefaultEditComp<Mob> {


    //TODO demon spawner should set their cooldown here!
    private final MobStateSpinner mobStateSpinner;
    private final StyledSpinner playerAlignment;
    private final StyledButton addBuffs, editStats;

    private final ItemContainer<Item> mimicItems;
    private final StyledItemSelector mobWeapon, mobArmor, thiefItem;
    private final StyledItemSelector mobRing, mobArti, mobMisc;
    private final LotusLevelSpinner lotusLevelSpinner;
    private final StyledSpinner sheepLifespan;
    private final QuestSpinner questSpinner;
    private final StyledItemSelector questItem1, questItem2;
    private final StyledCheckBox spawnQuestRoom;
    private final ItemSelectorList<Item> blacksmithQuestRewards;

    private final StyledSpinner sentryRange, sentryDelay;
    private final StyledSpinner abilityCooldown;
    private final ItemContainer<MobItem> summonMobs;
    private final StyledSpinner tenguPhase, tenguRange, dm300pylonsNeeded, yogSpawnersAlive;
    private final ItemSelectorList<MobItem> yogNormalFists, yogChallengeFists;
    private final StyledCheckBox dm300destroyWalls, pylonAlwaysActive, showBossBar;

    private final StyledSpinner heroMobLvl, heroMobStr;
    private final HeroClassSpinner heroClassSpinner;
    private final HeroClassSpinner.SubclassSpinner heroSubclassSpinner;


    private final Component[] rectComps, linearComps;

    public EditMobComp(Mob mob) {
        super(mob);

        if (mob instanceof Mimic) {
            if (((Mimic) mob).items == null) ((Mimic) mob).items = new ArrayList<>();
            ArrayList<Item> mimicItemList = ((Mimic) mob).items;
            mimicItems = new ItemContainerWithLabel<Item>(mimicItemList, this, true, Messages.get(WndJournal.class, "items") + ":") {
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

        if (mob instanceof ItemSelectables.WeaponSelectable) {
            mobWeapon = new StyledItemSelector(Messages.get(EditMobComp.class, "weapon"),
                    Weapon.class, ((ItemSelectables.WeaponSelectable) mob).weapon(), ((ItemSelectables.WeaponSelectable) mob).useNullWeapon()) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    ((ItemSelectables.WeaponSelectable) mob).weapon((Weapon) selectedItem);
                    EditMobComp.this.updateObj();
                }
            };
            mobWeapon.setShowWhenNull(ItemSpriteSheet.WEAPON_HOLDER);
            add(mobWeapon);
        } else mobWeapon = null;
        if (mob instanceof ItemSelectables.ArmorSelectable) {
            mobArmor = new StyledItemSelector(Messages.get(EditMobComp.class, "armor"),
                    Armor.class, ((ItemSelectables.ArmorSelectable) mob).armor(), ((ItemSelectables.ArmorSelectable) mob).useNullArmor()) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    ((ItemSelectables.ArmorSelectable) mob).armor((Armor) selectedItem);
                    EditMobComp.this.updateObj();
                }
            };
            mobArmor.setShowWhenNull(ItemSpriteSheet.ARMOR_HOLDER);
            add(mobArmor);
        } else mobArmor = null;
        if (mob instanceof HeroMob) {
            Hero hero = ((HeroMob) mob).hero();

            mobRing = new StyledItemSelector(Messages.get(HeroSettings.class, "ring"),
                    Ring.class, hero.belongings.ring, ItemSelector.NullTypeSelector.NONE) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    hero.belongings.ring = (Ring) selectedItem;
                    EditMobComp.this.updateObj();
                }
            };
            mobRing.setShowWhenNull(ItemSpriteSheet.RING_HOLDER);
            add(mobRing);

            mobArti = new StyledItemSelector(Messages.get(HeroSettings.class, "artifact"), Artifact.class, hero.belongings.artifact, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    hero.belongings.artifact = (Artifact) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            mobArti.setShowWhenNull(ItemSpriteSheet.ARTIFACT_HOLDER);
            add(mobArti);
            mobMisc = new StyledItemSelector(Messages.get(HeroSettings.class, "misc"), KindofMisc.class, hero.belongings.misc, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    hero.belongings.misc = (KindofMisc) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            mobMisc.setShowWhenNull(ItemSpriteSheet.SOMETHING);
            add(mobMisc);

            heroMobLvl = new StyledSpinner(new SpinnerIntegerModel(1, 30, hero.lvl, 1, false, null) {
                {
                    setAbsoluteMinimum(1);
                }
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 15;
                }
            }, Messages.titleCase(Messages.get(HeroSettings.class, "lvl")), 10, IconTitleWithSubIcon.createSubIcon(ItemSpriteSheet.Icons.POTION_EXP));
            heroMobLvl.addChangeListener(() -> ((HeroMob) mob).setHeroLvl((int) heroMobLvl.getValue()));
            add(heroMobLvl);

            heroMobStr = new StyledSpinner(new SpinnerIntegerModel(1, 100, hero.STR, 1, false, null) {
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 15;
                }
            }, Messages.titleCase(Messages.get(WndGameInProgress.class, "str")), 10, IconTitleWithSubIcon.createSubIcon(ItemSpriteSheet.Icons.POTION_STRENGTH));
            heroMobStr.addChangeListener(() -> hero.STR = (int) heroMobStr.getValue());
            add(heroMobStr);

            heroClassSpinner = new HeroClassSpinner(hero);
            heroClassSpinner.addChangeListener(this::updateObj);
            add(heroClassSpinner);
            heroSubclassSpinner = new HeroClassSpinner.SubclassSpinner(hero);
            heroSubclassSpinner.addChangeListener(this::updateObj);
            add(heroSubclassSpinner);

        } else {
            mobRing = null;
            mobArti = null;
            mobMisc = null;
            heroMobLvl = null;
            heroMobStr = null;
            heroClassSpinner = null;
            heroSubclassSpinner = null;
        }

        if (mob instanceof Thief) {
            thiefItem = new StyledItemSelector(Messages.get(EditMobComp.class, "item"),
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
            sheepLifespan = new StyledSpinner(new SpinnerIntegerModel(0, 600, (int) ((Sheep) mob).lifespan, 1, false, null) {
                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 120;
                }
            }, Messages.get(EditMobComp.class, "sheep_lifespan"), 8, IconTitleWithSubIcon.createSubIcon(ItemSpriteSheet.Icons.POTION_HEALING));
            sheepLifespan.icon().scale.set(9f / sheepLifespan.icon().height());
            sheepLifespan.addChangeListener(() -> ((Sheep) mob).lifespan = (int) sheepLifespan.getValue());
            add(sheepLifespan);
        } else sheepLifespan = null;

        if (mob instanceof QuestNPC<?>) {
            questSpinner = new QuestSpinner(((QuestNPC<?>) mob).quest, h -> mobStateSpinner.getCurrentInputFieldWith());
            add(questSpinner);
            if (mob instanceof Wandmaker) {
                if (mob.pos < 0) {
                    spawnQuestRoom = new StyledCheckBox(Messages.get(EditMobComp.class, "spawn_quest_room")) {
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
                questItem1 = new StyledItemSelector(Messages.get(EditMobComp.class, "wand_1"),
                        Wand.class, ((Wandmaker) mob).quest.wand1, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Wandmaker) mob).quest.wand1 = (Wand) selectedItem;
                        EditMobComp.this.updateObj();
                    }
                };
                add(questItem1);
                questItem2 = new StyledItemSelector(Messages.get(EditMobComp.class, "wand_2"),
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
                questItem1 = new StyledItemSelector(Messages.get(EditMobComp.class, "weapon"),
                        Weapon.class, ((Ghost) mob).quest.weapon, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Ghost) mob).quest.weapon = (Weapon) selectedItem;
                        EditMobComp.this.updateObj();
                    }
                };
                add(questItem1);
                questItem2 = new StyledItemSelector(Messages.get(EditMobComp.class, "armor"),
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
                questItem1 = new StyledItemSelector(Messages.get(EditMobComp.class, "ring"), Ring.class,
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
                    blacksmithQuestRewards = new ItemSelectorList<Item>(quest.smithRewards, Messages.get(EditMobComp.class, "blacksmith_items")) {
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
            sentryRange = new StyledSpinner(new SpinnerIntegerModel(1, 100, ((SentryRoom.Sentry) mob).range, 1, false, null),
                    Messages.get(EditMobComp.class, "range"));
            sentryRange.addChangeListener(() -> ((SentryRoom.Sentry) mob).range = (int) sentryRange.getValue());
            add(sentryRange);
            sentryDelay = new StyledSpinner(new SpinnerFloatModel(0f, 100f, ((SentryRoom.Sentry) mob).getInitialChargeDelay() - 1, false),
                    Messages.get(EditMobComp.class, "delay"));
            sentryDelay.addChangeListener(() -> ((SentryRoom.Sentry) mob).setInitialChargeDelay(((SpinnerFloatModel) sentryDelay.getModel()).getAsFloat() + 1));
            add(sentryDelay);
        } else {
            sentryRange = null;
            sentryDelay = null;
        }

        if (mob instanceof Guard) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((Guard) mob).maxChainCooldown, 1, false, null),
                    Messages.get(EditMobComp.class, "chains_cd"), 8, new ItemSprite(ItemSpriteSheet.ARTIFACT_CHAINS));
            abilityCooldown.addChangeListener(() -> ((Guard) mob).maxChainCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);
        } else if (mob instanceof DM200) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((DM200) mob).maxVentCooldown, 1, false, null),
                    Messages.get(EditMobComp.class, "vent_cd"), 8, BlobItem.createIcon(mob instanceof DM201 ? PermaGas.PCorrosiveGas.class : PermaGas.PToxicGas.class));
            abilityCooldown.addChangeListener(() -> ((DM200) mob).maxVentCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);
        } else if (mob instanceof Golem) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((Golem) mob).maxTeleCooldown, 1, false, null),
                    Messages.get(EditMobComp.class, "tele_cd"), 8);
            abilityCooldown.addChangeListener(() -> ((Golem) mob).maxTeleCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);
        } else if (mob instanceof com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner) {
            com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner m = (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner) mob;
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, m.maxWebCoolDown, 1, false, null),
                    Messages.get(EditMobComp.class, "web_cd"), 8);
            abilityCooldown.addChangeListener(() -> m.maxWebCoolDown = (int) abilityCooldown.getValue());
            add(abilityCooldown);
        } else if (mob instanceof DemonSpawner) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(0, Integer.MAX_VALUE, (int) ((DemonSpawner) mob).maxSpawnCooldown, 1, false, null) {
                @Override
                public String getDisplayString() {
                    if ((int) getValue() == 0) return Messages.get(EditMobComp.class, "by_depth");
                    return super.getDisplayString();
                }

                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, Messages.get(EditMobComp.class, "spawn_cd"), 8);
            abilityCooldown.setButtonWidth(9f);
            abilityCooldown.addChangeListener(() -> ((DemonSpawner) mob).maxSpawnCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);
        } else abilityCooldown = null;

        if (mob instanceof SpawnerMob) {
            List<MobItem> asMobItems = new ArrayList<>();
            if (((SpawnerMob) mob).summonTemplate != null) {
                for (Mob m : ((SpawnerMob) mob).summonTemplate) {
                    asMobItems.add(new MobItem(m));
                }
            }
            summonMobs = new ItemContainerWithLabel<MobItem>(asMobItems, null, true, Messages.get(EditMobComp.class, "summon_mob")) {
                @Override
                public boolean itemSelectable(Item item) {
                    return item instanceof MobItem;
                }

                @Override
                protected void doAddItem(MobItem item) {
                    item = (MobItem) item.getCopy();
                    super.doAddItem(item);
                    ((SpawnerMob) mob).summonTemplate.add(item.mob());
                }

                @Override
                protected boolean removeSlot(ItemContainer<MobItem>.Slot slot) {
                    if (super.removeSlot(slot)) {
                        ((SpawnerMob) mob).summonTemplate.remove(((MobItem) slot.item()).mob());
                        return true;
                    }
                    return false;
                }

                @Override
                public Class<? extends Bag> preferredBag() {
                    return Mobs.bag.getClass();
                }

                @Override
                protected void onSlotNumChange() {
                    super.onSlotNumChange();
                    EditMobComp.this.layout();
                }
            };
            add(summonMobs);
        } else summonMobs = null;

        if (mob instanceof Tengu) {
            tenguPhase = new StyledSpinner(new SpinnerIntegerModel(1, 2, ((Tengu) mob).phase, 1, true, null) {
                @Override
                public void displayInputAnyNumberDialog() {
                    //disabled
                }
            }, Messages.get(EditMobComp.class, "phase"));
            tenguPhase.addChangeListener(() -> ((Tengu) mob).phase = (int) tenguPhase.getValue());
            add(tenguPhase);
            tenguRange = new StyledSpinner(new SpinnerIntegerModel(1, 100, ((Tengu) mob).arenaRadius, 1, false, null),
                    Messages.get(EditMobComp.class, "range"));
            tenguRange.addChangeListener(() -> ((Tengu) mob).arenaRadius = (int) tenguRange.getValue());
            add(tenguRange);
        } else {
            tenguPhase = null;
            tenguRange = null;
        }

        if (mob instanceof DM300) {
            dm300destroyWalls = new StyledCheckBox(Messages.get(EditMobComp.class, "dm300_destroy_walls")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    ((DM300) mob).destroyWalls = value;
                }
            };
            dm300destroyWalls.icon(new ItemSprite(Assets.Environment.TILES_SEWERS, new TileItem(-1, DungeonTileSheet.FLAT_WALL, -1)));
            dm300destroyWalls.checked(((DM300) mob).destroyWalls);
            add(dm300destroyWalls);

            dm300pylonsNeeded = new StyledSpinner(new SpinnerIntegerModel(0, 4, ((DM300) mob).pylonsNeeded, 1, false, null),
                    Messages.get(EditMobComp.class, "dm300_pylons_needed"));
            dm300pylonsNeeded.addChangeListener(() -> ((DM300) mob).pylonsNeeded = (int) dm300pylonsNeeded.getValue());
            add(dm300pylonsNeeded);

        } else {
            dm300destroyWalls = null;
            dm300pylonsNeeded = null;
        }

        if (mob instanceof YogDzewa) {
            int spAlive = ((YogDzewa) mob).spawnersAlive;
            yogSpawnersAlive = new StyledSpinner(new SpinnerIntegerModel(0, 4, spAlive == -1 ? null : spAlive, 1, true,
                    Messages.get(DestCellSpinner.class, "default")) {
                @Override
                public void displayInputAnyNumberDialog() {
                }

                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, Messages.get(EditMobComp.class, "spawners_alive"), 6 + (PixelScene.landscape() ? 2 : 0), new SpawnerSprite());
            yogSpawnersAlive.addChangeListener(() -> {
                Integer val = (Integer) yogSpawnersAlive.getValue();
                if (val == null) val = -1;
                ((YogDzewa) mob).spawnersAlive = val;
            });
            yogSpawnersAlive.setButtonWidth(9f);
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
            pylonAlwaysActive = new StyledCheckBox(Messages.get(EditMobComp.class, "pylon_always_active")) {
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

        if (mob instanceof Goo || mob instanceof Tengu || mob instanceof DM300
                || mob instanceof DwarfKing || mob instanceof YogDzewa || mob instanceof CrystalSpire) {
            showBossBar = new StyledCheckBox(Messages.get(EditMobComp.class, "show_boss_bar")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    mob.showBossBar = value;
                    updateObj();
                }
            };
            showBossBar.checked(mob.showBossBar);
            add(showBossBar);
        } else showBossBar = null;

        if (!(mob instanceof QuestNPC || mob instanceof RatKing || mob instanceof Sheep ||
                mob instanceof WandOfRegrowth.Lotus || mob instanceof Shopkeeper || mob instanceof SentryRoom.Sentry)) {

            if (!(mob instanceof YogFist || mob instanceof Mimic || mob instanceof CrystalSpire)) {
                playerAlignment = new StyledSpinner(new SpinnerIntegerModel(Mob.NORMAL_ALIGNMENT, Mob.FRIENDLY_ALIGNMENT, mob.playerAlignment, 1, true, null) {
                    @Override
                    public void displayInputAnyNumberDialog() {
                    }

                    @Override
                    public Component createInputField(int fontSize) {
                        return super.createInputField(fontSize - 1);
                    }

                    @Override
                    public float getInputFieldWidth(float height) {
                        return Spinner.FILL;
                    }

                    @Override
                    public String getDisplayString() {
                        switch ((int) getValue()) {
                            case Mob.NORMAL_ALIGNMENT:
                                return Messages.get(EditMobComp.class, "player_alignment_normal");
                            case Mob.NEUTRAL_ALIGNMENT:
                                return Messages.get(EditMobComp.class, "player_alignment_neutral");
                            case Mob.FRIENDLY_ALIGNMENT:
                                return Messages.get(EditMobComp.class, "player_alignment_friendly");
                        }
                        return super.getDisplayString();
                    }
                }, Messages.get(EditMobComp.class, "player_alignment"));
                playerAlignment.setButtonWidth(9f);
                playerAlignment.addChangeListener(() -> {
                    mob.setPlayerAlignment((int) playerAlignment.getValue());
                    updateObj();
                });
                add(playerAlignment);
            } else playerAlignment = null;

            if (!(mob instanceof Pylon || mob instanceof CrystalSpire)) {
                addBuffs = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(EditMobComp.class, "add_buff"), PixelScene.landscape() ? 9 : 8) {
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
            editStats = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(EditMobComp.class, "edit_stats"), PixelScene.landscape() ? 9 : 8) {
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

        rectComps = new Component[]{

                mobStateSpinner, playerAlignment,

                yogSpawnersAlive,
                pylonAlwaysActive,
                tenguRange,
                abilityCooldown,
                dm300pylonsNeeded,

                questSpinner == null && playerAlignment != null && mobArmor == null
                        ? EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE : null,

                addBuffs, editStats,

                mob instanceof SentryRoom.Sentry ? EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE : null,

                showBossBar,

                mobWeapon, mobArmor, mobRing, mobArti, mobMisc, thiefItem,
                lotusLevelSpinner, sheepLifespan, sentryRange, sentryDelay,
                dm300destroyWalls,

                tenguPhase,

                heroClassSpinner, heroSubclassSpinner,
                heroMobLvl, heroMobStr,

                questSpinner, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE, questItem1, questItem2, spawnQuestRoom,

        };
        linearComps = new Component[]{
                mimicItems,
                summonMobs,
                yogNormalFists, yogChallengeFists,
                blacksmithQuestRewards
        };
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(rectComps);
        layoutCompsLinear(linearComps);
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
                Armor armor = ((ArmoredStatue) obj).armor();
                ((StatueSprite) ((MobTitleEditor) title).image).setArmor(armor == null ? 0 : armor.tier);
            } else if (obj instanceof HeroMob) {
                ((HeroSprite.HeroMobSprite) ((MobTitleEditor) title).image).updateHeroClass(((HeroMob) obj).hero());
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
            ((MobTitleEditor) title).setText(((MobTitleEditor) title).createTitle(obj));
        }
        desc.text(createDescription());
        if (mobWeapon != null) mobWeapon.updateItem();
        if (mobArmor != null && obj instanceof ArmoredStatue) {
            Armor armor = ((ArmoredStatue) obj).armor();
            if (obj.sprite != null)
                ((StatueSprite) obj.sprite).setArmor(armor == null ? 0 : armor.tier);
            mobArmor.updateItem();
        }
        if (mobArmor != null && obj instanceof HeroMob) {
            if (obj.sprite != null) {
                ((HeroSprite.HeroMobSprite) obj.sprite).updateHeroClass(((HeroMob) obj).hero());
            }
            mobArmor.updateItem();
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
        if (!Objects.equals(a.customName, b.customName)) return false;
        if (!Objects.equals(a.customDesc, b.customDesc)) return false;
        if (!Objects.equals(a.dialog, b.dialog)) return false;
        Set<Class<? extends Buff>> aBuffs = new HashSet<>(4);
        Set<Class<? extends Buff>> bBuffs = new HashSet<>(4);
        for (Buff buff : a.buffs()) aBuffs.add(buff.getClass());
        for (Buff buff : b.buffs()) bBuffs.add(buff.getClass());
        if (!bBuffs.equals(aBuffs)) return false;//only very simple, does not compare any values, just the types!!
        if (a.loot instanceof ItemsWithChanceDistrComp.RandomItemData) {
            if (!a.loot.equals(b.loot)) return false;
        } else if (b.loot instanceof ItemsWithChanceDistrComp.RandomItemData) return false;
        if (!EditItemComp.areEqual(a.glyphArmor, b.glyphArmor)) return false;

        if (a instanceof ItemSelectables.WeaponSelectable) {
            if (!EditItemComp.areEqual(((ItemSelectables.WeaponSelectable) a).weapon(), ((ItemSelectables.WeaponSelectable) b).weapon())) return false;
        }
        if (a instanceof ItemSelectables.ArmorSelectable) {
            if (!EditItemComp.areEqual(((ItemSelectables.ArmorSelectable) a).armor(), ((ItemSelectables.ArmorSelectable) b).armor())) return false;
        }

        if (a instanceof Thief) {
            return EditItemComp.areEqual(((Thief) a).item, ((Thief) b).item);
        } else if (a instanceof Mimic) {
            return DefaultEditComp.isItemListEqual(((Mimic) a).items, ((Mimic) b).items);
        } else if (a instanceof YogDzewa) {
            if (((YogDzewa) a).spawnersAlive != ((YogDzewa) b).spawnersAlive) return false;
            if (!isMobListEqual(((YogDzewa) a).fistSummons, ((YogDzewa) b).fistSummons)) return false;
            return isMobListEqual(((YogDzewa) a).challengeSummons, ((YogDzewa) b).challengeSummons);
        } else if (a instanceof SpawnerMob) {
            if (a instanceof DemonSpawner) {
                if (((DemonSpawner) a).maxSpawnCooldown != ((DemonSpawner) a).maxSpawnCooldown) return false;
            }
            return EditMobComp.isMobListEqual(((SpawnerMob) a).summonTemplate, ((SpawnerMob) b).summonTemplate);
        } else if (a instanceof Tengu) {
            return ((Tengu) a).arenaRadius == ((Tengu) b).arenaRadius && ((Tengu) a).phase == ((Tengu) b).phase;
        } else if (a instanceof DM300) {
            return ((DM300) a).destroyWalls == ((DM300) b).destroyWalls
                    && ((DM300) a).pylonsNeeded == ((DM300) b).pylonsNeeded;
        } else if (a instanceof Pylon) {
            return ((Pylon) a).alwaysActive == ((Pylon) b).alwaysActive;
        } else if (a instanceof HeroMob) {
            Hero h1 = ((HeroMob) a).hero();
            Hero h2 = ((HeroMob) b).hero();
            if (h1.heroClass != h2.heroClass) return false;
            if (h1.subClass != h2.subClass) return false;
            if (!EditItemComp.areEqual(h1.belongings.ring, h2.belongings.ring)) return false;
            if (!EditItemComp.areEqual(h1.belongings.artifact, h2.belongings.artifact)) return false;
            if (!EditItemComp.areEqual(h1.belongings.misc, h2.belongings.misc)) return false;
            if (h1.lvl != h2.lvl) return false;
            if (h1.STR != h2.STR) return false;
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

        protected IconButton rename;

        public MobTitleEditor(Mob mob) {
            super(mob, false);
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);
            rename = new IconButton(Icons.RENAME_ON.get()) {
                @Override
                protected void onClick() {
                    Window parent = EditorUtilies.getParentWindow(this);
                    SimpleWindow w = new SimpleWindow(parent.camera().width - 10, parent.camera().height - 10) {
                        @Override
                        public void hide() {
                            super.hide();
                            updateObj();
                        }
                    };
                    w.initComponents(ChangeMobNameDesc.createTitle(), new ChangeMobNameDesc(obj), null, 0f, 0.5f);
                    EditorScene.show(w);
                }
            };
            add(rename);
        }

        @Override
        protected BuffIndicator createBuffIndicator(Mob mob, boolean large) {
            return new BuffIndicatorEditor(mob, large, EditMobComp.this);
        }

        protected String createTitle(Mob mob) {
            return super.createTitle(mob) + EditorUtilies.appendCellToString(mob.pos);
        }

        @Override
        protected void layout() {
            width -= rename.icon().width + 3;
            super.layout();
            rename.setRect(x + width + 2, y + (height - rename.icon().height) * 0.5f, rename.icon().width, rename.icon().height);
            width += rename.icon().width + 3;
        }
    }
}