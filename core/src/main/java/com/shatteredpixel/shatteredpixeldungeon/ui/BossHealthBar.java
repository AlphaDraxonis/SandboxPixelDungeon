/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundle;
import com.watabou.utils.Consumer;

import java.util.ArrayList;
import java.util.List;

public class BossHealthBar extends Component {

    private static List<Mob> bosses = new ArrayList<>();

    private static BossHealthBar instance;

    private static List<BossHealthBarComp> bars;

    public BossHealthBar() {
        super();

        width = 64;
        height = 16;

        if (loadedBossIDs != null) {
            bosses.clear();
            for (int i = 0; i < loadedBossIDs.length; i++)
                bosses.add((Mob) Actor.findById(loadedBossIDs[i]));
            loadedBossIDs = null;
        }

        visible = active = !bosses.isEmpty();
        instance = this;

        bars = new ArrayList<>();
        for (Mob m : bosses) {
            if (m != null) {
                BossHealthBarComp bar = new BossHealthBarComp(m);
                add(bar);
                bars.add(bar);
            }
        }

        updateLayout();
    }

    public static void refresh() {
        for (BossHealthBarComp bar : bars)
            BuffIndicator.refreshBoss(bar.buffs);
    }

    public static void removeBoss(Mob boss) {
        removeBoss(boss, findBoss(boss));
    }

    public static void addBoss(Mob boss) {
        if (!bosses.contains(boss)) {
            bosses.add(boss);
            if (instance != null && boss.showBossBar) {
                addBarCompToUI(boss);
                instance.visible = instance.active = !bosses.isEmpty();
            }
        }
    }

    private static void addBarCompToUI(Mob boss) {
        BossHealthBarComp bar = new BossHealthBarComp(boss);
        instance.add(bar);
        bars.add(bar);
        updateLayout();
    }

    public static void removeBoss(Mob boss, BossHealthBarComp bossBar) {
        bosses.remove(boss);
        if (bossBar != null) {
            instance.remove(bossBar);
            bars.remove(bossBar);
            bossBar.killAndErase();
            updateLayout();
        }
        instance.visible = instance.active = !bosses.isEmpty();
    }

    public static void updateLayout() {
        instance.layout();
    }

    @Override
    protected void layout() {
        int numBars = Math.min(4, bars.size());
        if (numBars > 0) {
            int numLayouted = 0;
            float posY = y;
            if (numBars > 1) posY -= 8;
            if (numBars > 2) posY -= 4;
            int gap = 4 / numBars;
            for (BossHealthBarComp bar : bars) {
                if (numLayouted++ <= 4) {
                    bar.visible = bar.active = true;
                    bar.setPos(x, posY);
                    posY += bar.height() + gap;
                } else bar.visible = bar.active = false;
            }
            height = posY - y - gap;
        }
    }

    private static class BossHealthBarComp extends Component {

        private static String asset = Assets.Interfaces.BOSSHP;

        private Image bar;

        private Image rawShielding;
        private Image shieldedHP;
        private Image hp;
        private BitmapText hpText;

        private Button bossInfo;
        private BuffIndicator buffs;

        private Image skull;
        private Emitter blood;


        private final Mob boss;

        public BossHealthBarComp(Mob boss) {
            super(boss);
            this.boss = boss;
            layout();
        }

        @Override
        protected void createChildren(Object... params) {

            Mob boss = (Mob) params[0];

            bar = new Image(asset, 0, 0, 64, 16);
            add(bar);

            width = bar.width;
            height = bar.height;

            rawShielding = new Image(asset, 15, 25, 47, 4);
            rawShielding.alpha(0.5f);
            add(rawShielding);

            shieldedHP = new Image(asset, 15, 25, 47, 4);
            add(shieldedHP);

            hp = new Image(asset, 15, 19, 47, 4);
            add(hp);

            hpText = new BitmapText(PixelScene.pixelFont);
            hpText.alpha(0.6f);
            add(hpText);

            bossInfo = new Button() {
                @Override
                protected void onClick() {
                    super.onClick();
                    if (boss != null) {
                        GameScene.show(new WndInfoMob(boss));
                    }
                }

                @Override
                protected String hoverText() {
                    if (boss != null) {
                        return boss.name();
                    }
                    return super.hoverText();
                }
            };
            add(bossInfo);

            buffs = new BuffIndicator(boss, false);
            add(buffs);

            skull = new Image(asset, 5, 18, 6, 6);
            add(skull);

            blood = new Emitter();
            blood.pos(skull);
            blood.pour(BloodParticle.FACTORY, 0.3f);
            blood.autoKill = false;
            blood.on = false;
            add(blood);
        }

        @Override
        protected void layout() {
            bar.x = x;
            bar.y = y;

            hp.x = shieldedHP.x = rawShielding.x = bar.x + 15;
            hp.y = shieldedHP.y = rawShielding.y = bar.y + 3;

            hpText.scale.set(PixelScene.align(0.5f));
            hpText.x = hp.x + 1;
            hpText.y = hp.y + (hp.height - (hpText.baseLine() + hpText.scale.y)) / 2f;
            hpText.y -= 0.001f; //prefer to be slightly higher
            PixelScene.align(hpText);

            bossInfo.setRect(x, y, bar.width, bar.height);

            if (buffs != null) {
                buffs.setRect(hp.x, hp.y + 5, 47, 8);
            }

            skull.x = bar.x + 5;
            skull.y = bar.y + 5;

            width = bar.width;
            height = bar.height;
        }

        @Override
        public void update() {
            super.update();
            if (boss != null) {
                if (!boss.isAlive() || !Dungeon.level.mobs.contains(boss)) {
                    removeBoss(boss, this);
                    return;
                }

                int health = boss.HP;
                int shield = boss.shielding();
                int max = boss.HT;

                hp.scale.x = Math.max(0, (health - shield) / (float) max);
                shieldedHP.scale.x = health / (float) max;
                rawShielding.scale.x = shield / (float) max;

                if (boss.bleeding != blood.on) {
                    if (boss.bleeding) skull.tint(0xcc0000, 0.6f);
                    else skull.resetColor();
                    blood.on = boss.bleeding;
                }

                if (shield <= 0) {
                    hpText.text(health + "/" + max);
                } else {
                    hpText.text(health + "+" + shield + "/" + max);
                }
            }
        }
    }

    public static void reset() {
        bosses.clear();
    }

    private static final String BOSS_IDS = "boss_ids";
    private static int[] loadedBossIDs;

    public static void storeInBundle(Bundle bundle) {
        int[] intArray = new int[bosses.size()];
        for (int i = 0; i < intArray.length; i++)
            intArray[i] = bosses.get(i).id();
        bundle.put(BOSS_IDS, intArray);
    }

    public static void restoreFromBundle(Bundle bundle) {
        loadedBossIDs = bundle.getIntArray(BOSS_IDS);
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (instance == this) instance = null;
    }


    public static boolean bossBarActive() {
        for (Mob boss : bosses) {
            if (boss.showBossBar && boss.isAlive()) return true;
        }
        return false;
    }

    public static void doForEachBoss(Consumer<Mob> whatToDo) {
        for (Mob boss : bosses)
            whatToDo.accept(boss);
    }

    public static boolean bleedingActive() {
        for (Mob boss : bosses) {
            if (boss.bleeding) return true;
        }
        return false;
    }

    public static boolean isAssigned(Mob boss) {
        return bosses.contains(boss);
    }

    private static BossHealthBarComp findBoss(Mob boss) {
        for (BossHealthBarComp bar : bars) {
            if (bar.boss == boss) return bar;
        }
        return null;
    }

}