package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab.GAP;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ui.Component;

public class ZoneMobSettings extends Component {

    private final Component outsideSp;
    private Component title;
    private RenderedTextBlock titleText;
    private IconButton buttonInTitle;

    protected final StyledSpinner respawnTime;
    protected final StyledCheckBox spawningEnabled;
    protected final MobSettings.ChangeMobRotation changeMobRotation;

    public ZoneMobSettings(Zone z) {

        changeMobRotation = new MobSettings.ChangeMobRotation(z.getMobRotationVar()) {
            @Override
            protected void updateParent() {
                Window w = EditorUtilies.getParentWindow(this);
                if (w instanceof SimpleWindow) ((SimpleWindow) w).layout();
            }
        };
        add(changeMobRotation);

        outsideSp = changeMobRotation.getOutsideSp();

        respawnTime = new StyledSpinner(new SpinnerIntegerModel(1, 100, (int) z.respawnCooldown, 1, false, null) {
            @Override
            public void displayInputAnyNumberDialog() {
                displayInputAnyNumberDialog(1, Integer.MAX_VALUE);
            }
        }, Messages.get(MobSettings.class, "respawn_time"), 9);
        ((SpinnerIntegerModel) respawnTime.getModel()).setAbsoluteMinimum(1f);
        respawnTime.addChangeListener(() -> z.respawnCooldown = (int) respawnTime.getValue());
        add(respawnTime);

        spawningEnabled = new StyledCheckBox(Messages.get(MobSettings.class, "enable_respawn")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                respawnTime.enable(value);
                z.ownMobRotationEnabled = value;
            }
        };
        spawningEnabled.checked(z.ownMobRotationEnabled);
        add(spawningEnabled);
    }

    public Component createTitle() {

        return title = new Component() {
            @Override
            protected void createChildren(Object... params) {
                super.createChildren(params);
                titleText = PixelScene.renderTextBlock(Messages.get(ZoneMobSettings.class, "title"), 11);
                titleText.hardlight(Window.TITLE_COLOR);
                add(titleText);

                buttonInTitle = new IconButton(Icons.get(Icons.INFO)) {
                    {
                        height = icon.height();
                        width = icon.width();
                    }

                    @Override
                    protected void onClick() {
                        EditorScene.show(new WndTitledMessage(Icons.get(Icons.INFO),
                                Messages.titleCase(Messages.get(MobSettings.class, "mob_rot")),
                                Messages.get(ZoneMobSettings.class, "mob_rot_info")));
                    }
                };
                add(buttonInTitle);
            }

            @Override
            public float width() {
                return titleText.width() + (buttonInTitle == null ? 0 : GAP + buttonInTitle.width());
            }

            @Override
            public float height() {
                return titleText.height();
            }

            @Override
            public float bottom() {
                return super.bottom() - GAP * 3;
            }

            @Override
            protected void layout() {
                titleText.maxWidth((int) (width - GAP - (buttonInTitle == null ? 0 : buttonInTitle.height())));
                titleText.setPos(x, y);
                if (buttonInTitle != null)
                    buttonInTitle.setPos(title.right() - GAP - buttonInTitle.width() - x,titleText.top() + (height - buttonInTitle.height()) * 0.5f);
            }
        };
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    @Override
    protected void layout() {
        height = 6;
        height = EditorUtilies.layoutStyledCompsInRectangles(GAP, width, this, spawningEnabled, respawnTime) + GAP * 3;
        changeMobRotation.setRect(x, y + height, width, -1);
        height = changeMobRotation.bottom() - y;
    }

}