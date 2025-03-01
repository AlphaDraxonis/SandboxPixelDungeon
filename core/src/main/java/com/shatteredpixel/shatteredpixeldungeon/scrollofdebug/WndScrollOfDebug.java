package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.StaticReference;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.StaticValueReference;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.ui.Component;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WndScrollOfDebug extends Window {
    
    static final List<Reference> references;
    
    static {
        references = new LinkedList<>();
        
        references.add(new StaticReference(Dungeon.class, "Dungeon"));
        
        references.add(new StaticValueReference<CustomDungeon>(CustomDungeon.class, "customDungeon") {
            @Override
            public CustomDungeon getValue() {
                return Dungeon.customDungeon;
            }
        });
        references.add(new StaticValueReference<Level>(Level.class, "level") {
            @Override
            public Level getValue() {
                return Dungeon.level;
            }
        });
        
        references.add(new StaticValueReference<Hero>(Hero.class, "hero") {
            @Override
            public Hero getValue() {
                return Dungeon.hero;
            }
        });
        
        references.add(new StaticReference(Statistics.class, "Statistics"));
    }
    
    public static void addReference(Reference reference) {
        references.add(reference);
        if (instance != null) instance.getReferenceTable().addReferenceToUI(reference);
    }
    
    private final ReferenceTable referenceTable;
    private final StyledButton spawnMob, spawnItem;
    private static WndScrollOfDebug instance;
    
    public WndScrollOfDebug() {
        
        CustomDungeon.knowsEverything = true;
        
        resize(WindowSize.WIDTH_LARGE.get(), WindowSize.HEIGHT_SMALL.get());
        
        if (instance != null) {
            instance.hide();
        }
        instance = this;
        
        Undo.startAction();
        
        offset(0, EditorUtilities.getMaxWindowOffsetYForVisibleToolbar());
        
        Component outsideSp = new Component() {
            @Override
            protected void layout() {
                height = 0;
                height = EditorUtilities.layoutStyledCompsInRectangles(2, width, 2, this, spawnMob, spawnItem);
            }
        };
        
        spawnMob = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(WndScrollOfDebug.class, "spawn_mob"), 9) {
            {
                text.align(RenderedTextBlock.CENTER_ALIGN);
            }
            
            @Override
            protected void onClick() {
                EditorScene.selectItem(mobSelector);
            }
        };
        spawnMob.multiline = true;
        spawnMob.icon(new GnollSprite());
        outsideSp.add(spawnMob);
        
        spawnItem = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(WndScrollOfDebug.class, "spawn_item"), 9) {
            {
                text.align(RenderedTextBlock.CENTER_ALIGN);
            }
            
            @Override
            protected void onClick() {
                EditorScene.selectItem(itemSelector);
            }
        };
        spawnItem.multiline = true;
        spawnItem.icon(new ItemSprite(ItemSpriteSheet.BACKPACK));
        outsideSp.add(spawnItem);
        
        add(outsideSp);
        
        referenceTable = new ReferenceTable() {
            @Override
            protected void destroyCurrentSubMenu() {
                super.destroyCurrentSubMenu();
                if (getSubMenuComp() == null) {
                    outsideSp.setVisible(true);
                    referenceTable.setSize(width, WndScrollOfDebug.this.height - outsideSp.height() - 2);
                }
            }
            
            @Override
            public void changeContent(SubMenuComp subMenuComp) {
                outsideSp.setVisible(false);
                super.changeContent(subMenuComp);
                
                referenceTable.setSize(width, WndScrollOfDebug.this.height);
            }
        };
        add(referenceTable);
        
        outsideSp.setSize(width, 0);
        
        referenceTable.setSize(width, height - outsideSp.height() - 2);
        outsideSp.setPos(referenceTable.left(), referenceTable.bottom() + 1);
    }
    
    public ReferenceTable getReferenceTable() {
        return referenceTable;
    }
    
    @Override
    public void hide() {
        CustomDungeon.knowsEverything = false;
        super.hide();
        instance = null;
    }
    
    public static WndScrollOfDebug getInstance() {
        return instance;
    }
    
    
    public static final int ACCESS_LEVEL_PRIVATE = 0, ACCESS_LEVEL_PCKGE_PRIVATE = 1, ACCESS_LEVEL_PROTECTED = 2, ACCESS_LEVEL_PUBLIC = 3;
    
    public static boolean canAccess(int modifiers, int minAccessLevel) {
        int accessLevel;
        if (Modifier.isPublic(modifiers)) accessLevel = ACCESS_LEVEL_PUBLIC;
        else if (Modifier.isPrivate(modifiers)) accessLevel = ACCESS_LEVEL_PRIVATE;
        else if (Modifier.isProtected(modifiers)) accessLevel = ACCESS_LEVEL_PROTECTED;
        else accessLevel = ACCESS_LEVEL_PCKGE_PRIVATE;
        
        return minAccessLevel <= accessLevel;
    }
    
    public static String modifiersToString(int mods) {
        if (mods == 0) return "";
        if ((mods & (Modifier.PUBLIC + Modifier.PROTECTED + Modifier.PRIVATE)) == 0) //package-private
            return "private " + Modifier.toString(mods) + " ";
        else return Modifier.toString(mods).replace("public ", "") + " ";
    }
    
    private final WndBag.ItemSelector mobSelector = new WndBag.ItemSelector() {
        @Override
        public String textPrompt() {
            return "";
        }
        
        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof MobItem;
        }
        
        @Override
        public boolean acceptsNull() {
            return false;
        }
        
        @Override
        public Class<? extends Bag> preferredBag() {
            return Mobs.bag().getClass();
        }
        
        @Override
        public List<Bag> getBags() {
            return Collections.singletonList(Mobs.bag());
        }
        
        @Override
        public void onSelect(Item item) {
            if (item == null) {
                return;
            }
            hide();
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer cell) {
                    if (cell != null) {
                        Mob ch = (Mob) ((MobItem) item).getObject().getCopy();
//                        if (Barrier.canEnterCell(cell, ch, ch.isFlying(), false)) {
                        if (/*Dungeon.level.solid[cell] || */!Dungeon.level.insideMap(cell)) {
                            GLog.w(Messages.get(WndScrollOfDebug.class, "invalid_position_mob"));
                            return;
                        }
                        
                        ch.pos = cell;
                        Level.placeMob(ch);
                        Dungeon.level.occupyCell(ch);
                        
                        Dungeon.hero.checkVisibleMobs();
                        AttackIndicator.updateState();
                    }
                }
                
                @Override
                public String prompt() {
                    return Messages.get(WndScrollOfDebug.class, "prompt_choose_spawn_position");
                }
            });
        }
    };
    
    private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
        @Override
        public String textPrompt() {
            return "";
        }
        
        @Override
        public boolean itemSelectable(Item item) {
            return true;
        }
        
        @Override
        public boolean acceptsNull() {
            return false;
        }
        
        @Override
        public Class<? extends Bag> preferredBag() {
            return Items.bag().getClass();
        }
        
        @Override
        public List<Bag> getBags() {
            return Collections.singletonList(Items.bag());
        }
        
        @Override
        public void onSelect(Item item) {
            if (item == null) {
                return;
            }
            hide();
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer cell) {
                    if (cell != null) {
                        Item i = ((ItemItem) item).getObject().getCopy();
//                        if (Barrier.canEnterCell(cell, ch, ch.isFlying(), false)) {
                        if (/*Dungeon.level.solid[cell] || */!Dungeon.level.insideMap(cell)) {
                            GLog.w(Messages.get(WndScrollOfDebug.class, "invalid_position_item"));
                            return;
                        }
                        
                        Dungeon.level.drop(i, cell).sprite.drop();
                    }
                }
                
                @Override
                public String prompt() {
                    return Messages.get(WndScrollOfDebug.class, "prompt_choose_spawn_position");
                }
            });
        }
    };
}
