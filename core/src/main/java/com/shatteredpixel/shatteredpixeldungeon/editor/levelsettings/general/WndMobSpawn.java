package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.general;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.general.GeneralTab.BUTTON_HEIGHT;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class WndMobSpawn extends WndTitledMessage {

    public WndMobSpawn() {
        super(Messages.get(WndMobSpawn.class, "title"), Body::new);
    }

    private static class Body extends WndTitledMessage.Body {

        private final Spinner moblimit, respawnTime;
        private final RedButton openMobCycle;
        private final CheckBox enableMutations, disableSpawning;

        public Body() {
            CustomLevel f = EditorScene.customLevel();

            enableMutations = new CheckBox(Messages.get(WndMobSpawn.class, "mutation")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    f.setSwapForMutations(value);
                }
            };
            add(enableMutations);

            moblimit = new Spinner(new SpinnerIntegerModel(0, 100, f.mobLimit(), 1, false, null) {
                @Override
                public float getInputFieldWith(float height) {
                    return height * 1.1f;
                }
            }, " " + Messages.get(WndMobSpawn.class, "limit") + ":", 9);
            moblimit.addChangeListener(() -> f.setMobLimit((int) moblimit.getValue()));
            add(moblimit);
            respawnTime = new Spinner(new SpinnerIntegerModel(1, 100, (int) f.respawnCooldown(), 1, false, null) {
                @Override
                public float getInputFieldWith(float height) {
                    return height * 1.1f;
                }
            }, " " + Messages.get(WndMobSpawn.class, "respawn_time") + ":", 9);
            respawnTime.addChangeListener(() -> f.setRespawnCooldown((int) respawnTime.getValue()));
            add(respawnTime);

            openMobCycle = new RedButton(Messages.get(WndMobSpawn.class, "edit_cycle")) {
                @Override
                protected void onClick() {
                    EditorScene.show(new WndChangeMobRotation());
                }
            };
            add(openMobCycle);

            disableSpawning = new CheckBox(Messages.get(WndMobSpawn.class, "enable_respawn")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    enableMutations.enable(value);
                    openMobCycle.enable(value);
                    moblimit.enable(value);
                    respawnTime.enable(value);
                    f.enableRespawning(value);
                }
            };
            disableSpawning.checked(f.isRespawEnabled());
            add(disableSpawning);

        }

        @Override
        protected void layout() {
            float posY = y;

            disableSpawning.setRect(x, posY, width, BUTTON_HEIGHT);
            posY = disableSpawning.bottom() + GeneralTab.GAP;

            respawnTime.setRect(x, posY, width, BUTTON_HEIGHT);
            posY = respawnTime.bottom() + GeneralTab.GAP;

            moblimit.setRect(x, posY, width, BUTTON_HEIGHT);
            posY = moblimit.bottom() + GeneralTab.GAP;

            enableMutations.setRect(x, posY, width, BUTTON_HEIGHT);
            posY = enableMutations.bottom() + GeneralTab.GAP;

            openMobCycle.setRect(x, posY, width, BUTTON_HEIGHT);
            posY = openMobCycle.bottom() + GeneralTab.GAP;

            height = posY - GeneralTab.GAP;
        }
    }

}


// ≙ ≈ =
class WndChangeMobRotation extends Window {

    private final ScrollPane sp;
    private final Component wrapper;
    private final RedButton addMobBtn;
    private final RenderedTextBlock title;
    private final IconButton infoBtn;

    private final Map<Class<? extends Mob>, MobRotItem> mobRotItems = new HashMap<>();

    {
        for (Class<?> cl : Mobs.NPC.classes()) {
            mobRotItems.put((Class<? extends Mob>) cl, null);
        }
    }

    private int sum;
    private boolean isInInit;

    public WndChangeMobRotation() {
        super();

        int width = WndEditorSettings.calclulateWidth();
        int height = WndEditorSettings.calclulateHeight();

        resize(width, height);

        addMobBtn = new RedButton(Messages.get(WndMobSpawn.class, "add_mob")) {
            @Override
            protected void onClick() {
                EditorScene.show(new WndChooseMob("", mobRotItems.keySet()) {
                    @Override
                    protected void onSelect(Mob mob) {
                        Class<? extends Mob> cl = mob.getClass();
                        EditorScene.customLevel().getMobRotationVar().add(cl);
                        addRotItem(cl);
                        sum++;

                        updateList();
                    }
                });
            }
        };
        add(addMobBtn);

        sp = new ScrollPane(wrapper = new Component());

        infoBtn = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                showInfo();
            }
        };
        add(infoBtn);

        title = PixelScene.renderTextBlock(Messages.get(WndMobSpawn.class, "mob_rot"), 13);
        title.hardlight(Window.TITLE_COLOR);
        add(title);

        float titleW = title.width() + BUTTON_HEIGHT + GeneralTab.GAP;

        float titleH = Math.max(title.height(), BUTTON_HEIGHT);
        title.setPos((width - titleW) / 2, GeneralTab.GAP);//TODO
        infoBtn.setRect(title.right() + GeneralTab.GAP, -1, titleH, titleH);
        PixelScene.align(title);
        PixelScene.align(infoBtn);

        add(sp);

        sp.setRect(0, title.bottom() + GeneralTab.GAP * 2.5f, width, height - title.bottom() - GeneralTab.GAP * 4 - BUTTON_HEIGHT);
        addMobBtn.setRect(0, sp.bottom() + GeneralTab.GAP, width, BUTTON_HEIGHT);
        PixelScene.align(addMobBtn);

        initComps();
        isInInit = false;
    }

    private void showInfo() {
        EditorScene.show(new WndTitledMessage(Icons.get(Icons.INFO),
                Messages.titleCase(Messages.get(WndMobSpawn.class, "mob_rot")),
                Messages.get(WndMobSpawn.class, "mob_rot_info")));
    }

    private void initComps() {
        List<Class<? extends Mob>> curRot = EditorScene.customLevel().getMobRotationVar();
        Map<Class<? extends Mob>, Integer> mobCounterMap = new HashMap<>();
        for (Class<? extends Mob> cl : curRot) {
            Integer prev = mobCounterMap.get(cl);
            if (prev == null) {
                prev = 0;
                addRotItem(cl);
            }
            mobCounterMap.put(cl, prev + 1);
        }
        for (Class<? extends Mob> cl : mobCounterMap.keySet()) {
            Integer count = mobCounterMap.get(cl);
            mobRotItems.get(cl).setCount(count);
            sum += count;
        }
        updateList();
    }

    public void updateList() {
        float posY = wrapper.top();

        isInInit = true;
        Class<?>[][] allMobs = Mobs.getAllMobs(null);
        for (Class<?>[] allMob : allMobs) {
            for (Class<?> cl : allMob) {
                if (NPC.class.isAssignableFrom(cl)) continue;//Skip NPCS
                MobRotItem r = mobRotItems.get(cl);
                if (r != null) {
                    r.setPos(wrapper.left(), posY);
                    r.setCount((int) r.countSpinner.getValue());
                    PixelScene.align(r);
                    posY = r.bottom();
                }
            }
        }
        wrapper.setSize(width, posY);
        isInInit = false;
        sp.givePointerPriority();
    }

    private void addRotItem(Class<? extends Mob> mobClass) {
        MobRotItem r = new MobRotItem(Reflection.newInstance(mobClass), 1);
        mobRotItems.put(mobClass, r);
        wrapper.add(r);
        r.setSize(width, BUTTON_HEIGHT);
    }

    @Override
    public void hide() {
        WndChooseMob.resetLastView();
        super.hide();
    }

    private String calculatePercentage(float count) {
        float calc = count * 100 / sum;
        int asInt = Math.round(calc);
        char string = (asInt == calc) ? '=' : '≈';
        return " " + string + asInt + "%";
    }


    private void updateSpinners(MobRotItem exclude) {
        for (MobRotItem r : mobRotItems.values()) {
            if (r != null && r != exclude) r.setCount((int) r.countSpinner.getValue());
        }
    }

    private class MobRotItem extends ScrollingListPane.ListItem {

        private final Spinner countSpinner;
        private final IconButton removeBtn;
        private final Mob mob;

        public MobRotItem(Mob mob, int count) {
            super(mob.sprite(), Messages.titleCase(mob.name()));
            this.mob = mob;

            String oldText = label.text();
            remove(label);
            label = PixelScene.renderTextBlock(6);
            add(label);
            label.text(oldText);

            countSpinner = new Spinner(new SModel(count) {
                @Override
                public void changeValue(Object oldValue, Object newValue) {
                    super.changeValue(oldValue, newValue);
                    int diff = (int) newValue - (int) oldValue;
                    if (isInInit) return;
                    sum += diff;
                    if (diff > 0) {
                        diff = Math.abs(diff);
                        for (int i = 0; i < diff; i++) {
                            EditorScene.customLevel().getMobRotationVar().add(mob.getClass());
                        }
                    } else {
                        diff = Math.abs(diff);
                        for (int i = 0; i < diff; i++) {
                            EditorScene.customLevel().getMobRotationVar().remove(mob.getClass());
                        }
                    }
                }
            }, "", 6);
            countSpinner.setButtonWidth(10);
            countSpinner.addChangeListener(() -> updateSpinners(this));
            add(countSpinner);

            removeBtn = new IconButton(Icons.get(Icons.CLOSE)) {
                @Override
                protected void onClick() {
                    removeMob();
                }
            };
            add(removeBtn);
        }

        public void removeMob() {
            int count = (int) countSpinner.getValue();
            for (int i = 0; i < count; i++) {
                EditorScene.customLevel().getMobRotationVar().remove(mob.getClass());
            }
            sum -= count;
            wrapper.remove(this);
            mobRotItems.remove(mobClass());
            destroy();
            updateList();
        }

        public void setCount(int count) {
            countSpinner.setValue(count);
        }

        @Override
        public void onClick() {
            EditorScene.show(new WndTitledMessage(new WndInfoMob.MobTitle(mob, false), mob.info()));
        }

        @Override
        protected void layout() {
            super.layout();

            float h = height() - 3;
            float ypsilon = y + 2f;
            float spinnW = countSpinner.width();
            float gap = -1.1f;
            countSpinner.setRect(width - spinnW + x - h - gap, ypsilon, spinnW, h);
            removeBtn.setRect(countSpinner.right() + gap, ypsilon, h, h);
            PixelScene.align(countSpinner);
            PixelScene.align(removeBtn);

            hotArea.width = countSpinner.left() - x - 2;
        }

        public Class<? extends Mob> mobClass() {
            return mob.getClass();
        }

    }

    private class SModel extends SpinnerIntegerModel {

        public SModel(int count) {
            super(1, 100, count, 1, false, null);
        }

        @Override
        public int getClicksPerSecondWhileHolding() {
            return 40;
        }

        @Override
        public float getInputFieldWith(float height) {
            return height * 2f;
        }

        @Override
        public String getDisplayString() {
            return super.getDisplayString() + " " + calculatePercentage((int) getValue());
        }
    }

}