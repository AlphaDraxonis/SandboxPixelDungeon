package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FungalCore;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
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
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.BuffIndicatorEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.BuffListContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.ChangeMobNameDesc;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.FistSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.HeroClassSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.ItemSelectables;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.LotusLevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.MobStateSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.QuestSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.DestCellSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.WndEditStats;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BuffItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.PermaGas;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.HeroSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelectorList;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledItemSelector;
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
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PylonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpawnerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EditMobComp extends DefaultEditComp<Mob> {


    //TODO demon spawner should set their cooldown here!
    private MobStateSpinner mobStateSpinner;
    private StyledSpinner playerAlignment;
    private StyledButton editStats;
    private StyledButton turnTo;
    BuffListContainer buffs;

    private ItemContainer<Item> mimicItems;
    private StyledItemSelector mobWeapon, mobArmor, thiefItem;
    private StyledItemSelector mobRing, mobArti, mobMisc;
    private LotusLevelSpinner lotusLevelSpinner;
    private StyledSpinner sheepLifespan;
    private QuestSpinner questSpinner;
    private StyledItemSelector questItem1, questItem2;
    private StyledCheckBox spawnQuestRoom;
    private ItemSelectorList<Item> blacksmithQuestRewards;

    private StyledCheckBox mimicSuperHidden;
    private StyledSpinner sentryRange, sentryDelay;
    private StyledSpinner abilityCooldown;
    private ItemContainer<MobItem> summonMobs;
    private StyledSpinner tenguPhase, tenguRange, dm300pylonsNeeded, yogSpawnersAlive;
    private ItemSelectorList<MobItem> yogNormalFists, yogChallengeFists;
    private StyledCheckBox dm300destroyWalls, pylonAlwaysActive, showBossBar;

    private StyledSpinner heroMobLvl, heroMobStr;
    private HeroClassSpinner heroClassSpinner;
    private HeroClassSpinner.SubclassSpinner heroSubclassSpinner;


    private Window windowInstance;

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

            mimicSuperHidden = new StyledCheckBox(label("mimic_super_hidden"));
            mimicSuperHidden.checked(((Mimic) mob).superHidden);
            mimicSuperHidden.addChangeListener(v -> {
                ((Mimic) mob).superHidden = v;
                updateObj();
            });
            add(mimicSuperHidden);
        }

        if (mob instanceof ItemSelectables.WeaponSelectable) {
            mobWeapon = new StyledItemSelector(label("weapon"),
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
        }
        if (mob instanceof ItemSelectables.ArmorSelectable) {
            mobArmor = new StyledItemSelector(label("armor"),
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
        }
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
            }, Messages.titleCase(Messages.get(HeroSettings.class, "lvl")), 10, EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.POTION_EXP));
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
            }, Messages.titleCase(Messages.get(WndGameInProgress.class, "str")), 10, EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.POTION_STRENGTH));
            heroMobStr.addChangeListener(() -> hero.STR = (int) heroMobStr.getValue());
            add(heroMobStr);

            heroClassSpinner = new HeroClassSpinner(hero);
            heroClassSpinner.addChangeListener(this::updateObj);
            add(heroClassSpinner);
            heroSubclassSpinner = new HeroClassSpinner.SubclassSpinner(hero);
            heroSubclassSpinner.addChangeListener(this::updateObj);
            add(heroSubclassSpinner);
        }

        if (mob instanceof Thief) {
            thiefItem = new StyledItemSelector(label("item"),
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
        }

        if (!(mob instanceof Pylon) || mob instanceof QuestNPC<?>) {//mob (Pylon) should not be instanceof QuestNPC!!!
            mobStateSpinner = new MobStateSpinner(mob);
            add(mobStateSpinner);
            if (mob instanceof Mimic) mobStateSpinner.addChangeListener(this::updateObj);
        }

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
        }

        if (mob instanceof Sheep) {
            sheepLifespan = new StyledSpinner(new SpinnerIntegerModel(0, 600, (int) ((Sheep) mob).lifespan, 1, false, null) {
                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 120;
                }
            }, label("sheep_lifespan"), 8, EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.POTION_HEALING));
            sheepLifespan.icon().scale.set(9f / sheepLifespan.icon().height());
            sheepLifespan.addChangeListener(() -> ((Sheep) mob).lifespan = (int) sheepLifespan.getValue());
            add(sheepLifespan);
        }

        if (mob instanceof QuestNPC<?>) {
            questSpinner = new QuestSpinner(((QuestNPC<?>) mob).quest, h -> mobStateSpinner.getCurrentInputFieldWith());
            add(questSpinner);
            if (mob instanceof Wandmaker) {
                if (mob.pos < 0) {
                    spawnQuestRoom = new StyledCheckBox(label("spawn_quest_room")) {
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
                questItem1 = new StyledItemSelector(label("wand_1"),
                        Wand.class, ((Wandmaker) mob).quest.wand1, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Wandmaker) mob).quest.wand1 = (Wand) selectedItem;
                        updateObj();
                    }
                };
                add(questItem1);
                questItem2 = new StyledItemSelector(label("wand_2"),
                        Wand.class, ((Wandmaker) mob).quest.wand2, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Wandmaker) mob).quest.wand2 = (Wand) selectedItem;
                        updateObj();
                    }
                };
                add(questItem2);
                questItem1.setShowWhenNull(ItemSpriteSheet.SOMETHING);
                questItem2.setShowWhenNull(ItemSpriteSheet.SOMETHING);
                blacksmithQuestRewards = null;

            } else if (mob instanceof Ghost) {
                questItem1 = new StyledItemSelector(label("weapon"),
                        Weapon.class, ((Ghost) mob).quest.weapon, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Ghost) mob).quest.weapon = (Weapon) selectedItem;
                        updateObj();
                    }
                };
                add(questItem1);
                questItem2 = new StyledItemSelector(label("armor"),
                        Armor.class, ((Ghost) mob).quest.armor, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Ghost) mob).quest.armor = (Armor) selectedItem;
                        updateObj();
                    }
                };
                add(questItem2);
                questItem1.setShowWhenNull(ItemSpriteSheet.SOMETHING);
                questItem2.setShowWhenNull(ItemSpriteSheet.SOMETHING);

            } else if (mob instanceof Imp) {
                questItem1 = new StyledItemSelector(label("ring"), Ring.class,
                        ((Imp) mob).quest.reward, ItemSelector.NullTypeSelector.RANDOM) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((Imp) mob).quest.reward = (Ring) selectedItem;
                        updateObj();
                    }
                };
                add(questItem1);
                questItem1.setShowWhenNull(ItemSpriteSheet.SOMETHING);

            } else {
                if (mob instanceof Blacksmith) {
                    BlacksmithQuest quest = ((Blacksmith) mob).quest;
                    if (quest.smithRewards == null) quest.smithRewards = new ArrayList<>(3);
                    while (quest.smithRewards.size() < 3) quest.smithRewards.add(new Item());
                    blacksmithQuestRewards = new ItemSelectorList<Item>(quest.smithRewards, label("blacksmith_items")) {
                        @Override
                        public void change(int index) {
                            ItemSelector.showSelectWindow(new WndBag.ItemSelector() {
                                @Override
                                public String textPrompt() {
                                    return null;
                                }

                                @Override
                                public boolean itemSelectable(Item item) {
                                    Item i = item instanceof ItemItem ? ((ItemItem) item).getObject() : item;
                                    return i instanceof Weapon && !(i instanceof MissileWeapon || i instanceof SpiritBow)
                                            || i instanceof Armor;
                                }

                                @Override
                                public void onSelect(Item item) {
                                    if (item == EditorItem.RANDOM_ITEM) item = new Item();
                                    else if (item instanceof ItemItem) {
                                        item = ((ItemItem) item).getObject().getCopy();
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
                }
            }
        }

        if (mob instanceof SentryRoom.Sentry) {
            sentryRange = new StyledSpinner(new SpinnerIntegerModel(1, 100, ((SentryRoom.Sentry) mob).range, 1, false, null),
                    label("range"));
            sentryRange.addChangeListener(() -> ((SentryRoom.Sentry) mob).range = (int) sentryRange.getValue());
            add(sentryRange);
            sentryDelay = new StyledSpinner(new SpinnerFloatModel(0f, 100f, ((SentryRoom.Sentry) mob).getInitialChargeDelay() - 1, false),
                    label("delay"));
            sentryDelay.addChangeListener(() -> ((SentryRoom.Sentry) mob).setInitialChargeDelay(((SpinnerFloatModel) sentryDelay.getModel()).getAsFloat() + 1));
            add(sentryDelay);
        }

        if (mob instanceof Guard) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((Guard) mob).maxChainCooldown, 1, false, null),
                    label("chains_cd"), 8, new ItemSprite(ItemSpriteSheet.ARTIFACT_CHAINS));
            abilityCooldown.addChangeListener(() -> ((Guard) mob).maxChainCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);

        } else if (mob instanceof DM200) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((DM200) mob).maxVentCooldown, 1, false, null),
                    label("vent_cd"), 8, BlobItem.createIcon(mob instanceof DM201 ? PermaGas.PCorrosiveGas.class : PermaGas.PToxicGas.class));
            abilityCooldown.addChangeListener(() -> ((DM200) mob).maxVentCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);

        } else if (mob instanceof Golem) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((Golem) mob).maxTeleCooldown, 1, false, null),
                    label("tele_cd"), 8);
            abilityCooldown.addChangeListener(() -> ((Golem) mob).maxTeleCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);

        } else if (mob instanceof com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner) {
            com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner m = (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner) mob;
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, m.maxWebCoolDown, 1, false, null),
                    label("web_cd"), 8);
            abilityCooldown.addChangeListener(() -> m.maxWebCoolDown = (int) abilityCooldown.getValue());
            add(abilityCooldown);

        } else if (mob instanceof DemonSpawner) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(0, Integer.MAX_VALUE, (int) ((DemonSpawner) mob).maxSpawnCooldown, 1, false, null) {
                @Override
                public String getDisplayString() {
                    if ((int) getValue() == 0) return label("by_depth");
                    return super.getDisplayString();
                }

                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, label("spawn_cd"), 8);
            abilityCooldown.setButtonWidth(9f);
            abilityCooldown.addChangeListener(() -> ((DemonSpawner) mob).maxSpawnCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);
        }
        ;

        if (mob instanceof SpawnerMob) {
            List<MobItem> asMobItems = new ArrayList<>();
            if (((SpawnerMob) mob).summonTemplate != null) {
                for (Mob m : ((SpawnerMob) mob).summonTemplate) {
                    asMobItems.add(new MobItem(m));
                }
            }
            summonMobs = new ItemContainerWithLabel<MobItem>(asMobItems, null, true, label("summon_mob")) {
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
        }

        if (mob instanceof Tengu) {
            tenguPhase = new StyledSpinner(new SpinnerIntegerModel(1, 2, ((Tengu) mob).phase, 1, true, null) {
                @Override
                public void displayInputAnyNumberDialog() {
                    //disabled
                }
            }, label("phase"));
            tenguPhase.addChangeListener(() -> ((Tengu) mob).phase = (int) tenguPhase.getValue());
            add(tenguPhase);
            tenguRange = new StyledSpinner(new SpinnerIntegerModel(1, 100, ((Tengu) mob).arenaRadius, 1, false, null),
                    label("range"));
            tenguRange.addChangeListener(() -> ((Tengu) mob).arenaRadius = (int) tenguRange.getValue());
            add(tenguRange);
        }

        if (mob instanceof DM300) {
            dm300destroyWalls = new StyledCheckBox(label("dm300_destroy_walls"));
            dm300destroyWalls.icon(TileSprite.createTilespriteWithImage(Assets.Environment.TILES_SEWERS, DungeonTileSheet.FLAT_WALL));
            dm300destroyWalls.checked(((DM300) mob).destroyWalls);
            dm300destroyWalls.addChangeListener(v -> ((DM300) mob).destroyWalls = v);
            add(dm300destroyWalls);

            dm300pylonsNeeded = new StyledSpinner(new SpinnerIntegerModel(0, 4, ((DM300) mob).pylonsNeeded, 1, false, null),
                    label("dm300_pylons_needed"));
            dm300pylonsNeeded.addChangeListener(() -> ((DM300) mob).pylonsNeeded = (int) dm300pylonsNeeded.getValue());
            add(dm300pylonsNeeded);

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
            }, label("spawners_alive"), 6 + (PixelScene.landscape() ? 2 : 0), new SpawnerSprite());
            yogSpawnersAlive.addChangeListener(() -> {
                Integer val = (Integer) yogSpawnersAlive.getValue();
                if (val == null) val = -1;
                ((YogDzewa) mob).spawnersAlive = val;
            });
            yogSpawnersAlive.setButtonWidth(9f);
            add(yogSpawnersAlive);

            yogNormalFists = new FistSelector(((YogDzewa) mob).fistSummons, " " + label("normal_fists") + ":", 7 + (PixelScene.landscape() ? 2 : 0));
            add(yogNormalFists);
            yogChallengeFists = new FistSelector(((YogDzewa) mob).challengeSummons, " " + label("challenge_fists") + ":", 7 + (PixelScene.landscape() ? 2 : 0));
            add(yogChallengeFists);
        }

        if (mob instanceof Pylon) {
            pylonAlwaysActive = new StyledCheckBox(label("pylon_always_active"));
            pylonAlwaysActive.checked(((Pylon) mob).alwaysActive);
            pylonAlwaysActive.addChangeListener(v -> {
                ((Pylon) mob).alwaysActive = v;
                updateObj();
            });
            add(pylonAlwaysActive);
        }

        if (mob instanceof Goo || mob instanceof Tengu || mob instanceof DM300|| mob instanceof DwarfKing || mob instanceof YogDzewa
                || mob instanceof CrystalSpire || mob instanceof GnollGeomancer || mob instanceof FungalCore) {
            showBossBar = new StyledCheckBox(label("show_boss_bar")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    mob.showBossBar = value;
                    updateObj();
                }
            };
            showBossBar.checked(mob.showBossBar);
            add(showBossBar);
        }

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
                                return label("player_alignment_normal");
                            case Mob.NEUTRAL_ALIGNMENT:
                                return label("player_alignment_neutral");
                            case Mob.FRIENDLY_ALIGNMENT:
                                return label("player_alignment_friendly");
                        }
                        return super.getDisplayString();
                    }
                }, label("player_alignment"));
                playerAlignment.setButtonWidth(9f);
                playerAlignment.addChangeListener(() -> {
                    mob.setPlayerAlignment((int) playerAlignment.getValue());
                    updateObj();
                });
                add(playerAlignment);
            } else playerAlignment = null;

            if (!(mob instanceof Pylon || mob instanceof CrystalSpire)) {

                BuffItem.editMobComp = this;

                List<BuffItem> asBuffItems = new ArrayList<>();
                for (Buff b : mob.buffs()) {
                    asBuffItems.add(new BuffItem(b));
                }
                buffs = new BuffListContainer(asBuffItems, EditMobComp.this, label("buffs")) {
                    @Override
                    protected Set<Class<? extends Buff>> getBuffsToIgnore() {
                        Set<Class<? extends Buff>> buffsToIgnore = super.getBuffsToIgnore();
                        for (Class<?> c : mob.immunities) {
                            if (Buff.class.isAssignableFrom(c)) {
                                buffsToIgnore.add((Class<? extends Buff>) c);
                            }
                        }
                        buffsToIgnore.add(MagicalSight.class);
                        buffsToIgnore.add(Foresight.class);
                        buffsToIgnore.add(Light.class);
                        buffsToIgnore.add(Blindness.class);
                        return buffsToIgnore;
                    }

                    @Override
                    protected Buff doAddBuff(Class<? extends Buff> buff) {
                        Buff b = Buff.affect(mob, buff);
                        b.permanent = !(b instanceof ChampionEnemy);
                        updateObj();
                        return b;
                    }

                    @Override
                    protected void doRemoveBuff(Buff buff) {
                        buff.detach();
                        updateObj();
                    }

                    @Override
                    public synchronized void destroy() {
                        super.destroy();
                        BuffItem.editMobComp = null;
                    }
                };
                add(buffs);
            }
        }

        if (mob.pos != -1) {
            turnTo = new StyledButton(Chrome.Type.GREY_BUTTON_TR,
                    Messages.get(this, "turn_to", mob.turnToCell == -1 ? label("turn_to_random") : EditorUtilies.cellToString(mob.turnToCell))) {
                @Override
                protected void onClick() {
                    EditorScene.selectCell(turnToListener);
                    windowInstance = EditorUtilies.getParentWindow(turnTo);
                    windowInstance.active = false;
                    if (windowInstance instanceof WndTabbed)
                        ((WndTabbed) windowInstance).setBlockLevelForTabs(PointerArea.NEVER_BLOCK);
                    Game.scene().remove(windowInstance);
                }
            };
            turnTo.leftJustify = false;
            add(turnTo);
        }

        Mob defaultStats = DefaultStatsCache.getDefaultObject(mob.getClass());
        if (defaultStats != null) {
            editStats = new StyledButton(Chrome.Type.GREY_BUTTON_TR, label("edit_stats")) {
                @Override
                protected void onClick() {
                    EditorScene.show(WndEditStats.createWindow((int) Math.ceil(EditMobComp.this.width),
                            EditorUtilies.getParentWindow(EditMobComp.this).getOffset().y, defaultStats, mob, () -> updateObj()));
                }
            };
            add(editStats);
        }

        rectComps = new Component[]{

                mobStateSpinner, playerAlignment, mob instanceof Ghost ? questSpinner : null, editStats, turnTo,

                yogSpawnersAlive,
                pylonAlwaysActive,
                tenguRange,
                abilityCooldown,
                dm300pylonsNeeded,

                questSpinner == null && playerAlignment != null && mobArmor == null
                        ? EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE : null,

                mob instanceof SentryRoom.Sentry ? EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE : null,

                showBossBar,

                mobWeapon, mobArmor, mobRing, mobArti, mobMisc, thiefItem,
                lotusLevelSpinner, sheepLifespan, sentryRange, sentryDelay,
                mimicSuperHidden, dm300destroyWalls,

                tenguPhase,

                heroClassSpinner, heroSubclassSpinner,
                heroMobLvl, heroMobStr,

                mob instanceof Ghost ? null : questSpinner, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE, questItem1, questItem2, spawnQuestRoom,

        };
        linearComps = new Component[]{
                mimicItems,
                summonMobs,
                yogNormalFists, yogChallengeFists,
                blacksmithQuestRewards,
                buffs
        };
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(rectComps);
        layoutCompsLinear(linearComps);
    }

    private static String label(String key) {
        return Messages.get(EditMobComp.class, key);
    }

    @Override
    protected Component createTitle() {
        return new MobTitleEditor(obj);
    }

    @Override
    protected String createTitleText() {
        return null;
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
            }

            if (obj instanceof HeroMob) {
                ((HeroSprite.HeroMobSprite) ((MobTitleEditor) title).image).updateHeroClass(((HeroMob) obj).hero());
            }

            if (obj instanceof Mimic) {
                MimicSprite sprite = (MimicSprite) ((MobTitleEditor) title).image;
                sprite.superHidden = ((Mimic) obj).superHidden;
                if (obj.state != obj.PASSIVE) sprite.idle();
                else sprite.hideMimic();
            }

            if (obj instanceof Pylon) {
                Pylon pylon = (Pylon) obj;
                if (pylon.alwaysActive || pylon.alignment != Char.Alignment.NEUTRAL && obj.playerAlignment == Mob.NORMAL_ALIGNMENT)
                    ((PylonSprite) pylon.sprite).activate();
                else ((PylonSprite) pylon.sprite).deactivate();
            }

            ((MobTitleEditor) title).setText(((MobTitleEditor) title).createTitle(obj));
            ((MobTitleEditor) title).layout();
        }

        if (mobWeapon != null) {
            mobWeapon.updateItem();
        }

        if (mobArmor != null) {

            if (obj instanceof ArmoredStatue) {
                Armor armor = ((ArmoredStatue) obj).armor();
                if (obj.sprite != null)
                    ((StatueSprite) obj.sprite).setArmor(armor == null ? 0 : armor.tier);
            }

            if (obj instanceof HeroMob) {
                if (obj.sprite != null) {
                    ((HeroSprite.HeroMobSprite) obj.sprite).updateHeroClass(((HeroMob) obj).hero());
                }
            }

            mobArmor.updateItem();
        }

        if (obj instanceof Mimic) {
            ((MimicSprite) obj.sprite).superHidden = ((Mimic) obj).superHidden;
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

    public void updateTitleIcon() {
        if (title instanceof MobTitleEditor) {
            ((MobTitleEditor) title).image.destroy();
            ((MobTitleEditor) title).image.killAndErase();
            title.add(((MobTitleEditor) title).image = obj.sprite());
        }
    }

    public static boolean areEqual(Mob a, Mob b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.state.getClass() != b.state.getClass()) return false;

        if (!DefaultStatsCache.areStatsEqual(a, b)) return false;
        if (a.alignment != b.alignment) return false;
        if (a.playerAlignment != b.playerAlignment) return false;

        if (a.turnToCell != b.turnToCell) return false;

        if (!Objects.equals(a.customName, b.customName)) return false;
        if (!Objects.equals(a.customDesc, b.customDesc)) return false;
        if (!Objects.equals(a.dialog, b.dialog)) return false;

        if (a.spriteClass != b.spriteClass) return false;

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
            if (!EditItemComp.areEqual(((ItemSelectables.WeaponSelectable) a).weapon(), ((ItemSelectables.WeaponSelectable) b).weapon()))
                return false;
        }
        if (a instanceof ItemSelectables.ArmorSelectable) {
            if (!EditItemComp.areEqual(((ItemSelectables.ArmorSelectable) a).armor(), ((ItemSelectables.ArmorSelectable) b).armor())) return false;
        }

        if (a instanceof Thief) {
            if (!EditItemComp.areEqual(((Thief) a).item, ((Thief) b).item)) return false;
        }
        if (a instanceof Mimic) {
            if (((Mimic) a).superHidden != ((Mimic) b).superHidden) return false;
            if (!EditItemComp.isItemListEqual(((Mimic) a).items, ((Mimic) b).items)) return false;
        }
        if (a instanceof YogDzewa) {
            if (((YogDzewa) a).spawnersAlive != ((YogDzewa) b).spawnersAlive) return false;
            if (!isMobListEqual(((YogDzewa) a).fistSummons, ((YogDzewa) b).fistSummons)) return false;
            if (!isMobListEqual(((YogDzewa) a).challengeSummons, ((YogDzewa) b).challengeSummons)) return false;
        }
        if (a instanceof DemonSpawner) {
            if (((DemonSpawner) a).maxSpawnCooldown != ((DemonSpawner) a).maxSpawnCooldown) return false;
        }
        if (a instanceof SpawnerMob) {
            if (!EditMobComp.isMobListEqual(((SpawnerMob) a).summonTemplate, ((SpawnerMob) b).summonTemplate)) return false;
        }
        if (a instanceof Tengu) {
            if (((Tengu) a).arenaRadius != ((Tengu) b).arenaRadius) return false;
            if (((Tengu) a).phase != ((Tengu) b).phase) return false;
        }
        if (a instanceof DM300) {
            if (((DM300) a).destroyWalls != ((DM300) b).destroyWalls) return false;
            if (((DM300) a).pylonsNeeded != ((DM300) b).pylonsNeeded) return false;
        }
        if (a instanceof Pylon) {
            if (((Pylon) a).alwaysActive != ((Pylon) b).alwaysActive) return false;
        }
        if (a instanceof HeroMob) {
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
                    w.initComponents(ChangeMobNameDesc.createTitle(), new ChangeMobNameDesc(EditMobComp.this), null, 0f, 0.5f);
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

    private final CellSelector.Listener turnToListener = new CellSelector.Listener() {
        private int ignoreNextSelections = 0;
        @Override
        public void onSelect(Integer cell) {
            if (cell == null) {
                if (ignoreNextSelections > 0) {
                    ignoreNextSelections--;
                    return;
                }
                cell = -1;
            }

            obj.turnToCell = cell;
            turnTo.text(Messages.get(EditMobComp.class, "turn_to", obj.turnToCell == -1 ? label("turn_to_random") : EditorUtilies.cellToString(obj.turnToCell)));

            obj.sprite.turnTo(obj.pos, obj.turnToCell == -1 ? Random.Int( Dungeon.level.length() ) : obj.turnToCell);

            ignoreNextSelections = 2;//will be canceled
            windowInstance.active = true;
            if (windowInstance instanceof WndTabbed)
                ((WndTabbed) windowInstance).setBlockLevelForTabs(PointerArea.ALWAYS_BLOCK);
            EditorScene.show(windowInstance);
        }

        @Override
        public String prompt() {
            return Messages.get(EditMobComp.class, "turn_to_prompt");
        }
    };
}