package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;


import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.ItemSelectables;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.HeroSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundle;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class HeroMob extends Mob implements ItemSelectables.WeaponSelectable, ItemSelectables.ArmorSelectable, MobBasedOnDepth {

    private DirectableAlly directableAlly;

    {
        spriteClass = null;

        maxLvl = 29;
    }

    private InternalHero internalHero;

    public boolean bindEquipment;//if true, the player can't change the equipment of this hero when it is allied

    public HeroMob() {
        setInternalHero(new InternalHero());
    }

    @Override
    public void setLevel(int depth) {

        if (alignment == Alignment.ALLY) directableAlly = new DriedRose.GhostHero.GhostHeroDirectableAlly(this);

        internalHero.baseSpeed = baseSpeed;
        if (internalHero.belongings.weapon != null)
            internalHero.belongings.weapon.activate(internalHero);
        if (internalHero.belongings.armor != null)
            internalHero.belongings.armor.activate(internalHero);
        if (internalHero.belongings.ring != null)
            internalHero.belongings.ring.activate(internalHero);
        if (internalHero.belongings.artifact != null)
            internalHero.belongings.artifact.activate(internalHero);
        if (internalHero.belongings.misc != null)
            internalHero.belongings.misc.activate(internalHero);

        if (!hpSet) {
            HP = HT = internalHero.HP = internalHero.HT = (int) (internalHero.HT * statsScale);
            hpSet = !CustomDungeon.isEditing();
        }

        if (buff(Regeneration.class) == null) {
            Buff.affect(this, Regeneration.class);
            Buff.affect(this, Hunger.class);
        }
        updateEXP();
    }

    @Override
    public void initRandoms() {
        super.initRandoms();
        Belongings belongings = hero().belongings;
        belongings.weapon = RandomItem.initRandomStatsForItemSubclasses(belongings.weapon);
        belongings.armor = RandomItem.initRandomStatsForItemSubclasses(belongings.armor);
        belongings.ring = RandomItem.initRandomStatsForItemSubclasses(belongings.ring);
        belongings.artifact = RandomItem.initRandomStatsForItemSubclasses(belongings.artifact);
        belongings.misc = RandomItem.initRandomStatsForItemSubclasses(belongings.misc);
    }

    @Override
    public boolean interact(Char c) {
        if (directableAlly != null && state == SLEEPING) {
            sprite.turnTo(pos, c.pos);
            if (c == Dungeon.hero) {
                if (state == SLEEPING) {
                    notice();
                    yell(Messages.get(RatKing.class, "not_sleeping"));
                    state = WANDERING;
                }
            }
            return true;
        }
        return super.interact(c);
    }

    @Override
    public CharSprite sprite() {
        HeroSprite.HeroMobSprite sprite = new HeroSprite.HeroMobSprite(internalHero) {
            @Override
            public void link(Char ch) {
                super.link(ch);
                internalHero.sprite = this;
            }
        };
        sprite.updateArmor(internalHero);
        return sprite;
    }

    @Override
    public String name() {
        return customName == null ? internalHero.name() : super.name();
    }

    @Override
    public String description() {
        return customDesc == null ? internalHero.heroClass.shortDesc() : super.description();
    }

    @Override
    public void hitSound(float pitch) {
        updateInternalStats();
        internalHero.hitSound(pitch);
        updateStats();
    }

    @Override
    public boolean blockSound(float pitch) {
        updateInternalStats();
        boolean ret = internalHero.blockSound(pitch);
        updateStats();
        return ret;
    }

    @Override
    public String defenseVerb() {
        updateInternalStats();
        String ret = internalHero.defenseVerb();
        updateStats();
        return ret;
    }

    @Override
    public int defenseSkill(Char enemy) {
        int defenseSkill = super.defenseSkill(enemy);
        if (defenseSkill == 0 || defenseSkill == INFINITE_EVASION) return defenseSkill;//normal defense skill is always at least 5 (Hero.STARTING_DEF_SKILL)
        updateInternalStats();
        int ret = (int) (internalHero.defenseSkill(enemy) * statsScale);
        updateStats();
        return ret;
    }

    @Override
    public int drRoll() {
        updateInternalStats();
        int ret = (int) (internalHero.drRoll() * statsScale);
        updateStats();
        return ret;
    }

    @Override
    public float speed() {
        updateInternalStats();
        float ret = internalHero.speed();
        updateStats();
        return ret;
    }

    @Override
    public boolean canSurpriseAttack() {
        updateInternalStats();
        boolean ret = internalHero.canSurpriseAttack();
        updateStats();
        return ret;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return super.canAttack(enemy)
                || internalHero.belongings.weapon() instanceof SpiritBow
                    && new Ballistica( pos, enemy.pos, Ballistica.REAL_PROJECTILE, null).collisionPos == enemy.pos;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        if (Dungeon.level.adjacent( pos, enemy.pos ) || !(internalHero.belongings.weapon() instanceof SpiritBow)
                || new Ballistica( pos, enemy.pos, Ballistica.REAL_PROJECTILE, null).collisionPos != enemy.pos) {

            return super.doAttack( enemy );

        } else {

            shootMissile();

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                return false;
            } else {
                return true;
            }
        }
    }

    protected void shootMissile() {

        if (!(internalHero.belongings.weapon() instanceof SpiritBow)) {
            return;
        }

        Invisibility.dispel(this);

        SpiritBow bow = (SpiritBow) internalHero.belongings.weapon();

        bow.knockArrow().cast(internalHero, target);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        updateInternalStats();
        int ret = internalHero.attackProc(enemy, damage);
        updateStats();
        return ret;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (surprisedBy(enemy)) Surprise.hit(this);
        updateInternalStats();
        int ret = internalHero.defenseProc(enemy, damage);
        updateStats();
        return ret;
    }

    @Override
    public void damage(int dmg, Object src) {

        //from super.damage()
        boolean bleedingCheck;
        if (isBossMob && !BossHealthBar.isAssigned(this)){
            BossHealthBar.addBoss( this );
            Dungeon.level.seal();
            bleedingCheck = (HP*2 <= HT);
        } else bleedingCheck = false;

        if (!isInvulnerable(src.getClass())) {
            if (state == SLEEPING) {
                state = WANDERING;
            }
            if (state != HUNTING && !(src instanceof Corruption)) {
                alerted = true;
            }
        }

        updateInternalStats();
        internalHero.damage(dmg, src);
        updateStats();

        if (isBossMob) {
            if ((HP * 2 <= HT) && !bleedingCheck) {
                bleeding = true;
                sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
//                ((GooSprite) sprite).spray(true);
            }
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) {
                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) lock.addTime(dmg);
                else lock.addTime(dmg * 1.5f);
            }
        }
    }

    public void earnExp( int EXP, Class source ) {
        updateInternalStats();
        int exp = internalHero.lvl <= maxLvl ? EXP : 0;
        if (exp > 0 && sprite != null) {
            sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(exp), FloatingText.EXPERIENCE);
        }
        internalHero.earnExp(exp, source);
        if (internalHero.subClass == HeroSubClass.MONK){
            Buff.affect(internalHero, MonkEnergy.class).gainEnergy(this);
        }
        updateEXP();
        updateStats();
    }

    private void updateEXP() {
        EXP = (Hero.totalExp(internalHero.lvl) + internalHero.exp) / 3;
    }

    @Override
    public float stealth() {
        updateInternalStats();
        float ret = internalHero.stealth();
        updateStats();
        return ret;
    }

    @Override
    public boolean isActive() {
        updateInternalStats();
        boolean ret = internalHero.isActive();
        updateStats();
        return ret;
    }

    @Override
    public void move(int step, boolean travelling) {
        updateInternalStats();
        internalHero.moveNoSound(step, travelling);
        updateStats();
    }

    @Override
    public void onAttackComplete() {
        updateInternalStats();
        internalHero.onAttackComplete();
        updateStats();
    }

    @Override
    public boolean isImmune(Class effect) {
        if (internalHero == null) return false;
        updateInternalStats();
        boolean ret = internalHero.isImmune(effect);
        updateStats();
        return ret;
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        if (internalHero == null) return false;
        updateInternalStats();
        boolean ret = internalHero.isInvulnerable(effect);
        updateStats();
        return ret;
    }

    public boolean isStarving() {
        return internalHero.isStarving();
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        if (directableAlly != null && !bindEquipment) {
            if (weapon() != null) Dungeon.level.drop(weapon(), pos);
            if (armor() != null) Dungeon.level.drop(armor(), pos);
            if (internalHero.belongings.ring != null) Dungeon.level.drop(internalHero.belongings.ring, pos);
            if (internalHero.belongings.artifact != null) Dungeon.level.drop(internalHero.belongings.artifact, pos);
            if (internalHero.belongings.misc != null) Dungeon.level.drop(internalHero.belongings.misc, pos);
        }
    }

    @Override
    protected void spend(float time) {
        updateInternalStats();
        super.spend(time);
        internalHero.superSpend(time);
        updateStats();
    }

    public boolean killedMob(Object cause) {
        return cause == this || cause == internalHero || cause instanceof Item && internalHero.belongings.contains((Item) cause);
    }

    private void updateInternalStats() {
        updateStats(this, internalHero);
        internalHero.setEnemy(enemy);
        internalHero.resting = state == SLEEPING;
    }

    private void updateStats() {
        updateStats(internalHero, this);
        enemy = internalHero.enemy();
        if (internalHero.resting) state = SLEEPING;
    }

    private void updateStats(Char src, Char dest) {
        dest.HP = src.HP;
        dest.HT = src.HT;
        dest.paralysed = src.paralysed;
        dest.rooted = src.rooted;
        dest.setFlying(src.isFlying());
        dest.invisible = src.invisible;
        dest.viewDistance = src.viewDistance;
        dest.baseSpeed = src.baseSpeed;
        dest.pos = src.pos;
    }

    public void setInternalHero(InternalHero hero) {
        internalHero = hero;
        internalHero.alignment = alignment;
        internalHero.setOwner(this);
        updateStats();
        updateEXP();
    }

    public InternalHero hero() {
        return internalHero;
    }

    @Override
    public Weapon weapon() {
        return (Weapon) internalHero.belongings.weapon;
    }

    @Override
    public void weapon(Weapon weapon) {
        internalHero.belongings.weapon = weapon;
    }

    @Override
    public Armor armor() {
        return internalHero.belongings.armor;
    }

    @Override
    public void armor(Armor armor) {
        internalHero.belongings.armor = armor;
    }

    public void setHeroLvl(int lvl) {
        internalHero.setLvl(lvl);
        updateEXP();
    }

    private static final String INTERNAL_HERO = "internal_hero";
    private static final String BIND_EQUIPMENT = "bind_equipment";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(INTERNAL_HERO, internalHero);
        bundle.put(BIND_EQUIPMENT, bindEquipment);
        if (directableAlly != null) directableAlly.store(bundle);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        if (alignment == Alignment.ALLY) {
            directableAlly = new DirectableAlly(this);
            directableAlly.restore(bundle);
        }
        internalHero = null;
        super.restoreFromBundle(bundle);
        bindEquipment = bundle.getBoolean(BIND_EQUIPMENT);
        internalHero = (InternalHero) bundle.get(INTERNAL_HERO);
        internalHero.owner = this;
    }

    @Override
    public void aggro(Char ch) {
        if (directableAlly != null) directableAlly.aggroOverride(ch);
        else super.aggro(ch);
    }

    @Override
    public void beckon(int cell) {
        if (directableAlly != null) directableAlly.beckonOverride(cell);
        else super.beckon(cell);
    }

    @Override
    public DirectableAlly getDirectableAlly() {
        return directableAlly;
    }

    public static class InternalHero extends Hero {

        private HeroMob owner;

        public void setOwner(HeroMob owner) {
            this.owner = owner;
            for (Buff b : super.buffs()) {
                moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob(b, owner);
            }
        }

        @Override
        public int attackSkill(Char target) {
            return (int) (super.attackSkill(target) * owner.statsScale);
        }

        @Override
        public int damageRoll() {
            return (int) (super.damageRoll() * owner.statsScale);
        }

        @Override
        public void die(Object cause) {
            owner.die(cause);
        }

        public void superDie(Object cause) {
            super.die(cause);
        }

        @Override
        public void spend(float time) {
            owner.spend(time);
        }

        public void superSpend(float time) {
            super.spend(time);
        }

        @Override
        public void next() {
            owner.next();
        }

        protected void setEnemy(Char enemy) {
            this.enemy = enemy;
        }

        @Override
        public synchronized <T extends Buff> T buff(Class<T> c) {
            if (owner == null) return super.buff(c);
            return owner.buff(c);
        }

        @Override
        public synchronized <T extends Buff> HashSet<T> buffs(Class<T> c) {
            if (owner == null) return super.buffs(c);
            return owner.buffs(c);
        }

        @Override
        public synchronized LinkedHashSet<Buff> buffs() {
            if (owner == null) return super.buffs();
            return owner.buffs();
        }

        @Override
        public boolean add(Buff buff) {
            if (super.add(buff)) {
                if (owner != null) moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob(buff, owner);
                return true;
            }
            return false;
        }
    }

    public Window mobInfoWindow() {
        if (directableAlly == null) return null;
        return new EditCompWindow(new EquipHeroMobComp(this));
    }

    private static class EquipHeroMobComp extends DefaultEditComp<HeroMob> {

        private StyledItemSelector mobWeapon, mobArmor;
        private StyledItemSelector mobRing, mobArti, mobMisc;
        private RedButton direct;

        private final Component[] rectComps, linearComps;

        public EquipHeroMobComp(HeroMob hero) {
            super(hero);

            if (!hero.bindEquipment) {

                mobWeapon = new HeroEqSelector(Messages.get(EditMobComp.class, "weapon"),
                        MeleeWeapon.class, hero.weapon(), new HeroEqSelector.Selector() {
                    @Override
                    public void actuallyOnSelectAfterConditions(Item item) {
                        mobWeapon.setSelectedItem(item);
                    }

                    @Override
                    public String textPrompt() {
                        return Messages.get(DriedRose.WndGhostHero.class, "weapon_prompt");
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof MeleeWeapon;
                    }

                    @Override
                    public boolean passMoreConditions(Item item) {
                        if (((MeleeWeapon) item).STRReq() > hero.internalHero.STR) {
                            GLog.w(Messages.get(HeroMob.class, "cant_strength"));
                            return false;
                        }
                        return true;
                    }
                }) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {

                        maybeDetachItem(selectedItem, hero.weapon());

                        super.setSelectedItem(selectedItem);
                        hero.weapon((Weapon) selectedItem);
                        EquipHeroMobComp.this.updateObj();
                    }
                };
                mobWeapon.setShowWhenNull(ItemSpriteSheet.WEAPON_HOLDER);
                add(mobWeapon);

                mobArmor = new HeroEqSelector(Messages.get(EditMobComp.class, "armor"),
                        Armor.class, hero.armor(), new HeroEqSelector.Selector() {
                    @Override
                    public void actuallyOnSelectAfterConditions(Item item) {
                        mobArmor.setSelectedItem(item);
                    }

                    @Override
                    public String textPrompt() {
                        return Messages.get(DriedRose.WndGhostHero.class, "armor_prompt");
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof Armor;
                    }

                    @Override
                    public boolean passMoreConditions(Item item) {
                        if (((Armor) item).checkSeal() != null) {
                            GLog.w(Messages.get(HeroMob.class, "cant_unique"));
                            return false;
                        }
                        if (((Armor) item).STRReq() > hero.internalHero.STR) {
                            GLog.w(Messages.get(HeroMob.class, "cant_strength"));
                            return false;
                        }
                        return true;
                    }
                }) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {

                        maybeDetachItem(selectedItem, hero.armor());

                        super.setSelectedItem(selectedItem);
                        hero.armor((Armor) selectedItem);
                        EquipHeroMobComp.this.updateObj();
                    }
                };
                mobArmor.setShowWhenNull(ItemSpriteSheet.ARMOR_HOLDER);
                add(mobArmor);

                Hero h = hero.internalHero;
                mobRing = new HeroEqSelector(Messages.get(HeroSettings.class, "ring"),
                        Ring.class, h.belongings.ring, new HeroEqSelector.Selector() {
                    @Override
                    public void actuallyOnSelectAfterConditions(Item item) {
                        mobRing.setSelectedItem(item);
                    }

                    @Override
                    public String textPrompt() {
                        return Messages.get(HeroMob.class, "ring_prompt");
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof Ring;
                    }
                }) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {

                        maybeDetachItem(selectedItem, h.belongings.ring);

                        super.setSelectedItem(selectedItem);
                        h.belongings.ring = (Ring) selectedItem;
                        EquipHeroMobComp.this.updateObj();
                    }
                };
                mobRing.setShowWhenNull(ItemSpriteSheet.RING_HOLDER);
                add(mobRing);

                mobArti = new HeroEqSelector(Messages.get(HeroSettings.class, "artifact"), Artifact.class, h.belongings.artifact, new HeroEqSelector.Selector() {
                    @Override
                    public void actuallyOnSelectAfterConditions(Item item) {
                        mobArti.setSelectedItem(item);
                    }

                    @Override
                    public String textPrompt() {
                        return Messages.get(HeroMob.class, "artifact_prompt");
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof Artifact;
                    }
                }) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {

                        maybeDetachItem(selectedItem, h.belongings.artifact);

                        super.setSelectedItem(selectedItem);
                        h.belongings.artifact = (Artifact) selectedItem;
                        EquipHeroMobComp.this.updateObj();
                    }
                };
                mobArti.setShowWhenNull(ItemSpriteSheet.ARTIFACT_HOLDER);
                add(mobArti);

                mobMisc = new HeroEqSelector(Messages.get(HeroSettings.class, "misc"), KindofMisc.class, h.belongings.misc, new HeroEqSelector.Selector() {
                    @Override
                    public void actuallyOnSelectAfterConditions(Item item) {
                        mobMisc.setSelectedItem(item);
                    }

                    @Override
                    public String textPrompt() {
                        return Messages.get(HeroMob.class, "misc_prompt");
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof KindofMisc;
                    }
                }) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {

                        maybeDetachItem(selectedItem, h.belongings.misc);

                        super.setSelectedItem(selectedItem);
                        h.belongings.misc = (KindofMisc) selectedItem;
                        EquipHeroMobComp.this.updateObj();
                    }
                };
                mobMisc.setShowWhenNull(ItemSpriteSheet.SOMETHING);
                add(mobMisc);

            }

            direct = new RedButton(Messages.get(DriedRose.class, "ac_direct")) {
                @Override
                protected void onClick() {
                    EditorUtilies.getParentWindow(this).hide();
                    GameScene.selectCell(new CellSelector.Listener(){
                        @Override
                        public void onSelect(Integer cell) {
                            if (cell == null) return;
                            hero.directableAlly.directTocell(cell);
                        }

                        @Override
                        public String prompt() {
                            return  "\"" + Messages.get(DriedRose.GhostHero.class, "direct_prompt") + "\"";
                        }
                    });
                }
            };
            add(direct);

            rectComps = new Component[] {
                  mobWeapon, mobArmor, mobRing, mobArti, mobMisc
            };

            linearComps = new Component[] {
                    direct
            };
        }

        @Override
        protected void layout() {
            super.layout();
            layoutCompsInRectangles(rectComps);
            layoutCompsLinear(linearComps);
        }

        @Override
        public void updateObj() {
            if (mainTitleComp instanceof WndInfoMob.MobTitle) {

                ((HeroSprite.HeroMobSprite) ((WndInfoMob.MobTitle) mainTitleComp).image).updateHeroClass(obj.hero());

                ((WndInfoMob.MobTitle) mainTitleComp).setText(((WndInfoMob.MobTitle) mainTitleComp).createTitle(obj));
                mainTitleComp.setPos(mainTitleComp.left(), mainTitleComp.top());
            }

            if (mobWeapon != null) {
                mobWeapon.updateItem();
            }

            if (mobArmor != null) {
                mobArmor.updateItem();
            }

            ((HeroSprite.HeroMobSprite) obj.sprite).updateHeroClass(obj.hero());

            super.updateObj();
        }

        private void maybeDetachItem(Item newItem, Item item) {
            if (newItem != item && item != null) {
                if (!item.doPickUp(Dungeon.hero)) {
                    Dungeon.level.drop(item, Dungeon.hero.pos);
                }
            }
        }

        @Override
        protected Component createTitle() {
            return new WndInfoMob.MobTitle(obj, Mimic.isLikeMob(obj));
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

        private static class HeroEqSelector extends StyledItemSelector {

            private final WndBag.ItemSelector selector;

            public HeroEqSelector(String text, Class<? extends Item> itemClasses, Item startItem, WndBag.ItemSelector selector) {
                super(text, itemClasses, startItem, null);
                this.selector = selector;
            }

            private static abstract class Selector extends WndBag.ItemSelector {

                public abstract void actuallyOnSelectAfterConditions(Item item);

                @Override
                public Class<?extends Bag> preferredBag(){
                    return Belongings.Backpack.class;
                }

                public boolean passMoreConditions(Item item) {
                    return true;
                }

                @Override
                public void onSelect(Item item) {
                    if (!itemSelectable(item)) {
                        //do nothing, should only happen when window is cancelled
                        return;
                    }

                    if (item.unique) {
                        GLog.w( Messages.get(HeroMob.class, "cant_unique"));
                        return;
                    }

                    if (!item.isIdentified()) {
                        GLog.w( Messages.get(DriedRose.WndGhostHero.class, "cant_unidentified"));
                        return;
                    }

                    if (item.cursed) {
                        GLog.w( Messages.get(HeroMob.class, "cant_cursed"));
                        return;
                    }

                    if (!passMoreConditions(item)) {
                        return;
                    }

                    if (item instanceof EquipableItem && item.isEquipped(Dungeon.hero)) {
                        ((EquipableItem) item).doUnequip(Dungeon.hero, false, false);
                    } else {
                        item.detach(Dungeon.hero.belongings.backpack);
                    }
                    actuallyOnSelectAfterConditions(item);

                }
            }

            @Override
            public void change() {
                GameScene.selectItem(selector);
            }

        }
    }

}