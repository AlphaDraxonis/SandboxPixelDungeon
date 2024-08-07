package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables.ChangeMobCustomizable;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.DestCellSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.WndEditStats;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.PermaGas;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.HeroSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaCodeHolder;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaMob;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.IDEWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerFloatModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.*;

public class EditMobComp extends DefaultEditComp<Mob> {

    private MobStateSpinner mobStateSpinner;
    private StyledSpinner playerAlignment;
    private StyledButton editStats;
    private StyledButton turnTo;
    BuffListContainer buffs;

    private ItemContainer<Item> mimicItems;
    private StyledItemSelector mobWeapon, mobArmor, thiefItem, tormentedSpiritPrize;
    private StyledItemSelector mobRing, mobArti, mobMisc;
    private StyledCheckBox heroBindEquipment;
    private ItemContainer<Item> heroWands, heroUtilityItems;
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
    private StyledButton bossMusic;

    private StyledSpinner heroMobLvl, heroMobStr;
    private HeroClassSpinner heroClassSpinner;
    private HeroClassSpinner.SubclassSpinner heroSubclassSpinner;
    private StyledButton test;
    private CheckBox inheritsStats;

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
                if (mob.sprite != null) {
                    for (Buff b : mob.buffs()) {
                        b.fx(true);
                    }
                }
            });
            add(mimicSuperHidden);
        }

        if (mob instanceof ItemSelectables.WeaponSelectable) {
            mobWeapon = new StyledItemSelector(label("weapon"),
                    MeleeWeapon.class, ((ItemSelectables.WeaponSelectable) mob).weapon(), ((ItemSelectables.WeaponSelectable) mob).useNullWeapon()) {
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
            HeroMob.InternalHero hero = ((HeroMob) mob).hero();

            mobRing = new StyledItemSelector(Messages.get(HeroSettings.class, "ring"),
                    Ring.class, hero.belongings.ring, ItemSelector.NullTypeSelector.NOTHING) {
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
                    EditMobComp.this.updateObj();
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
                    EditMobComp.this.updateObj();
                }
            };
            mobMisc.setShowWhenNull(ItemSpriteSheet.SOMETHING);
            add(mobMisc);

            heroMobLvl = new StyledSpinner(new SpinnerIntegerModel(1, 30, hero.lvl) {
                {
                    setAbsoluteMinimum(1);
                }
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, Messages.titleCase(Messages.get(HeroSettings.class, "lvl")), 10, EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.POTION_EXP));
            heroMobLvl.addChangeListener(() -> ((HeroMob) mob).setHeroLvl((int) heroMobLvl.getValue()));
            add(heroMobLvl);

            heroMobStr = new StyledSpinner(new SpinnerIntegerModel(1, 50, hero.STR) {
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
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

            heroBindEquipment = new StyledCheckBox(label("hero_bind_equipment"));
            heroBindEquipment.checked(((HeroMob) mob).bindEquipment);
            heroBindEquipment.addChangeListener(v -> ((HeroMob) mob).bindEquipment = v);
            heroBindEquipment.visible = heroBindEquipment.active = obj.playerAlignment == Mob.FRIENDLY_ALIGNMENT;
            add(heroBindEquipment);

            heroWands = new ItemContainerWithLabel<Item>(hero.wands(), this, Messages.get(HeroMob.class, "wands"), false, 0, 3) {

                @Override
                public boolean itemSelectable(Item item) {
                    if (item instanceof ItemItem) item = ((ItemItem) item).item();
                    return item instanceof Wand;
                }

                @Override
                protected void doAddItem(Item item) {
                    super.doAddItem(item);
                    if (Dungeon.isLevelTesting()) {
                        ((Wand) item).charge(hero);
                    }
                }

                @Override
                protected boolean removeSlot(ItemContainer<Item>.Slot slot) {
                    if (super.removeSlot(slot)) {
                        if (Dungeon.isLevelTesting()) {
                            Wand wand = ((Wand) slot.item());
                            wand.stopCharging();
                        }
                        return true;
                    }
                    return false;
                }

                @Override
                protected void showSelectWindow() {
                    ItemSelector.showSelectWindow(this, ItemSelector.NullTypeSelector.DISABLED, Wand.class, Items.bag, new HashSet<>(0));
                }
            };
            add(heroWands);

            List<Item> utilItemList = new ArrayList<>(hero.potions());
            utilItemList.addAll(hero.utilItems());
            heroUtilityItems = new ItemContainerWithLabel<Item>(utilItemList, this, Messages.get(HeroMob.class, "utils")) {
                @Override
                protected void doAddItem(Item item) {
                    if (item.stackable) {
                        for (Item i : itemList) {
                            if (item.isSimilar( i )) {
                                i.merge( item );
                                return;
                            }
                        }
                    }
                    if (item instanceof MissileWeapon) ((MissileWeapon) item).resetParent();
                    super.doAddItem(item);
                }

                @Override
                protected void onSlotNumChange() {
                    if (heroUtilityItems != null) {
                        updateObj();
                    }
                }

                @Override
                public synchronized void destroy() {
                    super.destroy();
                    hero.potions().clear();
                    hero.utilItems().clear();
                    for (Item i : utilItemList) {
                        if (i instanceof Potion) hero.potions().add(((Potion) i));
                        else hero.utilItems().add(i);
                    }
                }

                @Override
                public boolean itemSelectable(Item item) {
                    return item instanceof Potion && !(item instanceof PotionOfMastery) && !(item instanceof PotionOfDivineInspiration)
                            || item instanceof MissileWeapon || item instanceof Bomb;
                }
            };
            add(heroUtilityItems);
        }

        if (mob instanceof Thief) {
            thiefItem = new StyledItemSelector(label("item"),
                    Item.class, ((Thief) mob).item, ItemSelector.NullTypeSelector.NOTHING) {
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

        if (mob instanceof TormentedSpirit) {
            tormentedSpiritPrize = new StyledItemSelector(label("prize"),
                    Item.class, ((TormentedSpirit) mob).prize, ItemSelector.NullTypeSelector.RANDOM) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    ((TormentedSpirit) mob).prize = selectedItem;
                    EditMobComp.this.updateObj();
                }

                @Override
                public void change() {
                    EditorScene.selectItem(selector);
                }
            };
            tormentedSpiritPrize.setShowWhenNull(ItemSpriteSheet.SOMETHING);
            add(tormentedSpiritPrize);
        }

        if (!(mob instanceof Pylon)) {//mob (Pylon) should not be instanceof QuestNPC!!!
            mobStateSpinner = new MobStateSpinner(mob);
            mobStateSpinner.addChangeListener(this::updateObj);
            add(mobStateSpinner);
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
            sheepLifespan = new StyledSpinner(new SpinnerIntegerModel(0, 600, (int) ((Sheep) mob).lifespan) {
                @Override
                public int getClicksPerSecondWhileHolding() {
                    return super.getClicksPerSecondWhileHolding() / 3;
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
                    while (quest.smithRewards.size() < 3) quest.smithRewards.add(ItemSelectorList.NULL_ITEM);
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
                                public Class<? extends Bag> preferredBag() {
                                    return Items.bag.getClass();
                                }

                                @Override
                                public List<Bag> getBags() {
                                    return Collections.singletonList(Items.bag);
                                }
                            }, ItemSelector.NullTypeSelector.RANDOM, Item.class, Items.bag, new HashSet<>());
                        }

                        @Override
                        public synchronized void destroy() {
                            super.destroy();
                            Set<Item> toRemove = new HashSet<>(4);
                            for (Item i : list) {
                                if (i == ItemSelectorList.NULL_ITEM) toRemove.add(i);
                            }
                            list.removeAll(toRemove);
                        }
                    };
                    add(blacksmithQuestRewards);
                }
            }
        }

        if (mob instanceof SentryRoom.Sentry) {
            sentryRange = new StyledSpinner(new SpinnerIntegerModel(1, 100, ((SentryRoom.Sentry) mob).range),
                    label("range"));
            sentryRange.addChangeListener(() -> ((SentryRoom.Sentry) mob).range = (int) sentryRange.getValue());
            add(sentryRange);
            sentryDelay = new StyledSpinner(new SpinnerFloatModel(0f, 100f, ((SentryRoom.Sentry) mob).getInitialChargeDelay() - 1),
                    label("delay"));
            sentryDelay.addChangeListener(() -> ((SentryRoom.Sentry) mob).setInitialChargeDelay(((SpinnerFloatModel) sentryDelay.getModel()).getAsFloat() + 1));
            add(sentryDelay);
        }

        if (mob instanceof Guard) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((Guard) mob).maxChainCooldown),
                    label("chains_cd"), 8, new ItemSprite(ItemSpriteSheet.ARTIFACT_CHAINS));
            abilityCooldown.addChangeListener(() -> ((Guard) mob).maxChainCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);

        } else if (mob instanceof DM200) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((DM200) mob).maxVentCooldown),
                    label("vent_cd"), 8, BlobItem.createIcon(mob instanceof DM201 ? PermaGas.PCorrosiveGas.class : PermaGas.PToxicGas.class));
            abilityCooldown.addChangeListener(() -> ((DM200) mob).maxVentCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);

        } else if (mob instanceof Golem) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, ((Golem) mob).maxTeleCooldown),
                    label("tele_cd"), 8);
            abilityCooldown.addChangeListener(() -> ((Golem) mob).maxTeleCooldown = (int) abilityCooldown.getValue());
            add(abilityCooldown);

        } else if (mob instanceof com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner) {
            com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner m = (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner) mob;
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(1, Integer.MAX_VALUE, m.maxWebCoolDown),
                    label("web_cd"), 8);
            abilityCooldown.addChangeListener(() -> m.maxWebCoolDown = (int) abilityCooldown.getValue());
            add(abilityCooldown);

        } else if (mob instanceof DemonSpawner) {
            abilityCooldown = new StyledSpinner(new SpinnerIntegerModel(0, Integer.MAX_VALUE, (int) ((DemonSpawner) mob).maxSpawnCooldown) {
                @Override
                protected String displayString(Object value) {
                    if ((int) value == 0) return label("by_depth");
                    return super.displayString(value);
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

        if (mob instanceof SpawnerMob) {
            List<MobItem> asMobItems = new ArrayList<>();
            if (((SpawnerMob) mob).summonTemplate != null) {
                for (Mob m : ((SpawnerMob) mob).summonTemplate) {
                    asMobItems.add(new MobItem(m));
                }
            }
            summonMobs = new ItemContainerWithLabel<MobItem>(asMobItems, this, label("summon_mob")) {
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
            };
            add(summonMobs);
        }

        if (mob instanceof Tengu) {
            tenguPhase = new StyledSpinner(new SpinnerIntegerModel(1, 2, ((Tengu) mob).phase, true) {
                @Override
                public void displayInputAnyNumberDialog() {
                    //disabled
                }
            }, label("phase"));
            tenguPhase.addChangeListener(() -> ((Tengu) mob).phase = (int) tenguPhase.getValue());
            add(tenguPhase);
            tenguRange = new StyledSpinner(new SpinnerIntegerModel(1, 100, ((Tengu) mob).arenaRadius),
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

            dm300pylonsNeeded = new StyledSpinner(new SpinnerIntegerModel(0, 4, ((DM300) mob).pylonsNeeded),
                    label("dm300_pylons_needed"));
            dm300pylonsNeeded.addChangeListener(() -> ((DM300) mob).pylonsNeeded = (int) dm300pylonsNeeded.getValue());
            add(dm300pylonsNeeded);

        }

        if (mob instanceof YogDzewa) {
            int spAlive = ((YogDzewa) mob).spawnersAlive;
            yogSpawnersAlive = new StyledSpinner(new SpinnerIntegerModel(-1, 4, spAlive, true) {
                @Override
                public void displayInputAnyNumberDialog() {
                }

                @Override
                protected String displayString(Object value) {
                    if (((int) value) == -1) return Messages.get(DestCellSpinner.class, "default");
                    return super.displayString(value);
                }

                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, label("spawners_alive"), 6 + (PixelScene.landscape() ? 2 : 0), new SpawnerSprite());
            yogSpawnersAlive.addChangeListener(() -> ((YogDzewa) mob).spawnersAlive = (int) yogSpawnersAlive.getValue());
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

            if (!(mob instanceof CrystalSpire || mob instanceof GnollGeomancer || mob instanceof FungalCore)) {
                bossMusic = new BtnSelectBossMusic(mob.bossMusic) {
                    @Override
                    protected void setBossMusic(String music) {
                        super.setBossMusic(music);
                        mob.bossMusic = music;
                        updateObj();
                    }
                };
                add(bossMusic);
            }
        }

        if (!(mob instanceof QuestNPC || mob instanceof RatKing || mob instanceof Sheep ||
                mob instanceof WandOfRegrowth.Lotus || mob instanceof Shopkeeper || mob instanceof SentryRoom.Sentry)) {

            if (!(mob instanceof YogFist || mob instanceof Mimic || mob instanceof CrystalSpire)) {
                playerAlignment = new StyledSpinner(new SpinnerIntegerModel(Mob.NORMAL_ALIGNMENT, Mob.FRIENDLY_ALIGNMENT, mob.playerAlignment, true) {
                    @Override
                    public void displayInputAnyNumberDialog() {
                    }

                    @Override
                    public int getClicksPerSecondWhileHolding() {
                        return 0;
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
                    protected String displayString(Object value) {
                        switch ((int) value) {
                            case Mob.NORMAL_ALIGNMENT:
                                return label("player_alignment_normal");
                            case Mob.NEUTRAL_ALIGNMENT:
                                return label("player_alignment_neutral");
                            case Mob.FRIENDLY_ALIGNMENT:
                                return label("player_alignment_friendly");
                        }
                        return super.displayString(value);
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
                    if (b.icon() != BuffIndicator.NONE) asBuffItems.add(new BuffItem(b));
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
                        if (!(mob instanceof HeroMob)) buffsToIgnore.add(Recharging.class);
                        return buffsToIgnore;
                    }

                    @Override
                    protected Buff doAddBuff(Buff buff) {
                        buff.attachTo(mob);
                        buff.permanent = !(buff instanceof ChampionEnemy);
                        updateObj();
                        return buff;
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
            turnTo = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR,
                    Messages.get(this, "turn_to", mob.turnToCell == -1 ? label("turn_to_random") : EditorUtilies.cellToString(mob.turnToCell))) {
                {
                    text.align(RenderedTextBlock.CENTER_ALIGN);
                }
                @Override
                protected void onClick() {
                    EditorScene.hideWindowsTemporarily();
                    EditorScene.selectCell(turnToListener);
                }
            };
            add(turnTo);
        }

        Mob defaultStats = DefaultStatsCache.getDefaultObject(mob.getClass());
        if (defaultStats != null) {
            editStats = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, label("edit_stats")) {
                {
                    text.align(RenderedTextBlock.CENTER_ALIGN);
                }
                @Override
                protected void onClick() {
                    EditorScene.show(WndEditStats.createWindow((int) Math.ceil(EditMobComp.this.width),
                            EditorUtilies.getParentWindow(EditMobComp.this).getOffset().y, defaultStats, mob, () -> updateObj()));
                }
            };
            editStats.icon(Icons.EDIT.get());
            add(editStats);
        }



        if (mob instanceof LuaMob) {
            test = new RedButton(label(CustomDungeon.isEditing() ? "edit_code" : "view_code")) {
                @Override
                protected void onClick() {
                    LuaCodeHolder luaCodeHolder = CustomObject.customObjects.get(((LuaMob) mob).getIdentifier());
                    if (CustomDungeon.isEditing()) {
                        IDEWindow.showWindow(luaCodeHolder);
                    } else {
                        GameScene.show(new WndTitledMessage(Icons.INFO.get(), luaCodeHolder.pathToScript, CustomDungeonSaves.readLuaFile(luaCodeHolder.pathToScript).code){{
                            setHighlightingEnabled(false);
                        }});
                    }
                }
            };
            add(test);
        }

		rectComps = new Component[]{

                mobStateSpinner, playerAlignment, mob instanceof Ghost ? questSpinner : null, editStats, turnTo,

                yogSpawnersAlive,
                pylonAlwaysActive,
                tenguRange,
                abilityCooldown,
                dm300pylonsNeeded,

//                questSpinner == null && playerAlignment != null && mobArmor == null
//                        ? EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE : null,

//                mob instanceof SentryRoom.Sentry ? EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE : null,

                showBossBar, bossMusic,

                mobWeapon, mobArmor, mobRing, mobArti, mobMisc, thiefItem, tormentedSpiritPrize,
                lotusLevelSpinner, sheepLifespan, sentryRange, sentryDelay,
                mimicSuperHidden, dm300destroyWalls,

                tenguPhase,

                heroClassSpinner, heroSubclassSpinner,
                heroMobLvl, heroMobStr, heroBindEquipment,

                mob instanceof Ghost ? null : questSpinner, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE, questItem1, questItem2, spawnQuestRoom,

        };
        linearComps = new Component[]{
                mimicItems,
                summonMobs,
                yogNormalFists, yogChallengeFists,
                blacksmithQuestRewards,
                heroWands, heroUtilityItems,
                buffs, test
        };


        if (CustomDungeon.isEditing() && mob instanceof LuaMob && !((LuaMob) mob).isOriginal()) {
            inheritsStats = new CheckBox(label("inherit_stats")) {
                boolean initializing;
                {
                    initializing = true;
                    checked(((LuaMob) mob).getInheritsStats());
                    initializing = false;
                }
                @Override
                public void checked(boolean value) {
                    if (value != checked()) {
                        if (value && !initializing) {
                            setToMakeEqual(mob, (Mob) CustomObject.getLuaClass(((LuaMob) mob).getIdentifier()));
                        }

                        for (Component c : rectComps) {
                            if (c != null) c.visible = c.active = !value;
                        }
                        for (Component c : linearComps) {
                            if (c != null) c.visible = c.active = !value;
                        }

                        IconButton rename = mainTitleComp instanceof MobTitleEditor ? ((MobTitleEditor) mainTitleComp).rename : null;
                        if (rename != null) rename.visible = rename.active = !value;

                        ((LuaMob) mob).setInheritsStats(value);
                        if (test != null) test.visible = test.active = true;
                        updateObj();
                    }
                    super.checked(value);
                }
            };
            add(inheritsStats);
        }
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(inheritsStats);
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
        if (mainTitleComp instanceof MobTitleEditor) {

            if (obj instanceof ArmoredStatue) {
                Armor armor = ((ArmoredStatue) obj).armor();
                ((StatueSprite) ((MobTitleEditor) mainTitleComp).image).setArmor(armor == null ? 0 : armor.tier);
            }

            if (obj instanceof HeroMob) {
                ((HeroSprite.HeroMobSprite) ((MobTitleEditor) mainTitleComp).image).updateHeroClass(((HeroMob) obj).hero());
            }

            if (obj instanceof Mimic) {
                MimicSprite sprite = (MimicSprite) ((MobTitleEditor) mainTitleComp).image;
                sprite.superHidden = ((Mimic) obj).superHidden;
                if (obj.state != obj.PASSIVE) sprite.idle();
                else sprite.hideMimic();
            }

            ((MobTitleEditor) mainTitleComp).setText(((MobTitleEditor) mainTitleComp).createTitle(obj));
            ((MobTitleEditor) mainTitleComp).layout();
        }

        if (mobWeapon != null) {
            mobWeapon.updateItem();
        }

        if (mobArmor != null) {
            mobArmor.updateItem();
        }

        if (heroBindEquipment != null) {
            heroBindEquipment.visible = heroBindEquipment.active = obj.playerAlignment == Mob.FRIENDLY_ALIGNMENT;
        }

        if (pylonAlwaysActive != null) {
            if (obj.playerAlignment == Mob.NORMAL_ALIGNMENT) {
                pylonAlwaysActive.enable(true);
            } else {
                if (!pylonAlwaysActive.active) {
                    pylonAlwaysActive.enable(false);
                    pylonAlwaysActive.checked(true);
                }
            }
        }

        updateMobTexture(obj);

        super.updateObj();
    }

    public static void updateMobTexture(Mob mob) {

        if (mob.sprite == null) return;

        if (mob instanceof Pylon) {
            Pylon pylon = (Pylon) mob;
            if (pylon.alwaysActive || mob.playerAlignment == Mob.NORMAL_ALIGNMENT && mob.alignment != Char.Alignment.NEUTRAL)
                ((PylonSprite) pylon.sprite).activate();
            else ((PylonSprite) pylon.sprite).deactivate();
        }

        if (mob instanceof ArmoredStatue) {
            Armor armor = ((ArmoredStatue) mob).armor();
            if (mob.sprite != null)
                ((StatueSprite) mob.sprite).setArmor(armor == null ? 0 : armor.tier);
        }

        if (mob instanceof HeroMob) {
			((HeroSprite.HeroMobSprite) mob.sprite).updateHeroClass(((HeroMob) mob).hero());
		}

        if (mob instanceof Mimic) {
            ((MimicSprite) mob.sprite).superHidden = ((Mimic) mob).superHidden;
            if (mob.state != mob.PASSIVE) mob.sprite.idle();
            else ((MimicSprite) mob.sprite).hideMimic();
        }
    }

    public void updateTitleIcon() {
        if (mainTitleComp instanceof MobTitleEditor) {
            ((MobTitleEditor) mainTitleComp).image.destroy();
            ((MobTitleEditor) mainTitleComp).image.killAndErase();
            mainTitleComp.add(((MobTitleEditor) mainTitleComp).image = obj.sprite());
        }
    }

    public static boolean areEqual(Mob a, Mob b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.state.getClass() != b.state.getClass()) return false;

        if (!DefaultStatsCache.areStatsEqual(a, b)) return false;
        if (a.alignment != b.alignment) return false;
        if (a.playerAlignment != b.playerAlignment) return false;

        if (a.turnToCell != b.turnToCell) return false;

        if (!Objects.equals(a.getCustomName(), b.getCustomName())) return false;
        if (!Objects.equals(a.getCustomDesc(), b.getCustomDesc())) return false;
        if (!Objects.equals(a.bossMusic, b.bossMusic)) return false;

        if (!(a.dialogs == null ? new HashSet<>() : new HashSet<>(a.dialogs)).equals(b.dialogs == null ? new HashSet<>() : new HashSet<>(b.dialogs))) return false;

        if (a.spriteClass != b.spriteClass) return false;

        if (!EditBuffComp.isBuffListEqual(a.buffs(), b.buffs())) return false;

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
        if (a instanceof TormentedSpirit) {
            if (!EditItemComp.areEqual(((TormentedSpirit) a).prize, ((TormentedSpirit) b).prize)) return false;
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
            HeroMob.InternalHero h1 = ((HeroMob) a).hero();
            HeroMob.InternalHero h2 = ((HeroMob) b).hero();
            if (h1.heroClass != h2.heroClass) return false;
            if (h1.subClass != h2.subClass) return false;
            if (h1.lvl != h2.lvl) return false;
            if (h1.STR != h2.STR) return false;
            if (!EditItemComp.areEqual(h1.belongings.weapon, h2.belongings.weapon)) return false;
            if (!EditItemComp.areEqual(h1.belongings.armor, h2.belongings.armor)) return false;
            if (!EditItemComp.areEqual(h1.belongings.ring, h2.belongings.ring)) return false;
            if (!EditItemComp.areEqual(h1.belongings.artifact, h2.belongings.artifact)) return false;
            if (!EditItemComp.areEqual(h1.belongings.misc, h2.belongings.misc)) return false;

            if (!EditItemComp.isItemListEqual(h1.wands(), h2.wands())) return false;
            if (!EditItemComp.isItemListEqual(h1.potions(), h2.potions())) return false;
            if (!EditItemComp.isItemListEqual(h1.utilItems(), h2.utilItems())) return false;
        }

        if (a instanceof LuaMob) {
            if (((LuaMob) a).getInheritsStats() != ((LuaMob) b).getInheritsStats()) return false;
        }

        return true;
    }

    public static boolean isMobListEqual(List<? extends Mob> a, List<? extends Mob> b) {
        int sizeA = a == null ? 0 : a.size();
        int sizeB = b == null ? 0 : b.size();
        if (sizeA != sizeB) return false;
        if (a == null || b == null) return true;
        int index = 0;
        for (Mob m : a) {
            if (!EditMobComp.areEqual(m, b.get(index))) return false;
            index++;
        }
        return true;
    }

    public static void setToMakeEqual(Mob change, Mob template) {
        if (change == null || template == null) return;
        if (change.getClass() != template.getClass()) return;
        Bundle bundle = new Bundle();
        bundle.put("MOB", template);

        int pos = change.pos;
        boolean replaceSprite = change.spriteClass != template.spriteClass;
        change.restoreFromBundle(bundle.getBundle("MOB"));
        change.pos = pos;

        if (replaceSprite && change.sprite != null) {
            EditorScene.replaceMobSprite(change, template.spriteClass);
        }
    }

    private class MobTitleEditor extends WndInfoMob.MobTitle {

        protected IconButton rename;

        public MobTitleEditor(Mob mob) {
            super(mob, false);
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            rename = new IconButton(Icons.RENAME_ON.get()) {
                @Override
                protected void onClick() {
                    Window parent = EditorUtilies.getParentWindow(this);
                    SimpleWindow w = new SimpleWindow(parent.camera().width - 10, (int) Math.ceil(PixelScene.uiCamera.height * 0.8f - 10)) {
                        @Override
                        public void hide() {
                            super.hide();
                            updateObj();
                        }
                    };
                    w.offset(0, EditorUtilies.getMaxWindowOffsetYForVisibleToolbar());
                    ChangeMobCustomizable cc = new ChangeMobCustomizable(EditMobComp.this);
                    w.initComponents(cc.createTitle(), cc, null, 0f, 0.5f);
                    EditorScene.show(w);
                }
            };
            add(rename);
        }

        @Override
        protected BuffIndicator createBuffIndicator(Mob mob, boolean large) {
            return new BuffIndicatorEditor(mob, large, EditMobComp.this);
        }

        @Override
        public String createTitle(Mob mob) {
            if (MobSpriteItem.canChangeSprite(mob)) {
                Mob defaultMob = DefaultStatsCache.getDefaultObject(mob.getClass());
                if (defaultMob == null && MobSpriteItem.canChangeSprite(mob)) defaultMob = Reflection.newInstance(mob.getClass());
                if (MobSpriteItem.isSpriteChanged(mob)) {
                    return super.createTitle(mob) + " (" + super.createTitle(defaultMob) + ")" + EditorUtilies.appendCellToString(mob.pos);
                }
            }
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

            EditorScene.reshowWindows();
        }

        @Override
        public String prompt() {
            return Messages.get(EditMobComp.class, "turn_to_prompt");
        }
    };
}