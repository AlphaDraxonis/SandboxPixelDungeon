package com.alphadraxonis.sandboxpixeldungeon.editor.overview.dungeon;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.FloorOverviewScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.DungeonToJsonConverter;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.ExoticPotion;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndGameInProgress;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndOptions;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.badlogic.gdx.Files;
import com.watabou.noosa.Game;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WndSelectDungeon extends Window {


    protected ScrollingListPane listPane;
    protected RedButton createNewDungeonBtn;

    private List<CustomDungeonSaves.Info> allInfos;
    private Set<String> dungeonNames;

    public WndSelectDungeon(List<CustomDungeonSaves.Info> allInfos, boolean showAddButton) {
        this.allInfos = allInfos;

        resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        listPane = new ScrollingListPane();
        add(listPane);

        dungeonNames = new HashSet<>();
        for (CustomDungeonSaves.Info info : allInfos) dungeonNames.add(info.name);

        if (showAddButton) {
            createNewDungeonBtn = new RedButton(Messages.get(WndSelectDungeon.class, "new")) {
                @Override
                protected void onClick() {
                    Game.scene().addToFront(new WndNewDungeon(dungeonNames));
                }
            };
            add(createNewDungeonBtn);
            createNewDungeonBtn.setRect(0, height - 18, width, 18);
        }

        listPane.setSize(width, createNewDungeonBtn == null ? height : createNewDungeonBtn.top());

        updateList();
    }

    private void updateList() {
        listPane.clear();
        for (CustomDungeonSaves.Info info : allInfos) {
            listPane.addItem(new ListItem(info));
        }
    }

    protected void select(String customDungeonName) {
        openDungeon(customDungeonName);
    }


    private class ListItem extends ScrollingListPane.ListItem {

        private final CustomDungeonSaves.Info info;

        public ListItem(CustomDungeonSaves.Info info) {
            super(Icons.get(Icons.STAIRS), info.name);
            this.info = info;
        }

        @Override
        protected boolean onLongClick() {
            Window w = new WndInfoDungeon(info);
            if (Game.scene() instanceof EditorScene) EditorScene.show(w);
            else Game.scene().addToFront(w);
            return true;
        }

        @Override
        protected void onClick() {
            select(info.name);
        }

        private class WndInfoDungeon extends Window {

            private static final int GAP = 6;

            public WndInfoDungeon(CustomDungeonSaves.Info info) {

                resize(PixelScene.landscape() ? 215 : Math.min(160, (int) (PixelScene.uiCamera.width * 0.9)), 100);

                RenderedTextBlock title = PixelScene.renderTextBlock(info.name, 10);
                title.hardlight(Window.TITLE_COLOR);
                title.maxWidth(width);
                add(title);

                RedButton cont = new RedButton(Messages.get(WndGameInProgress.class, "continue")) {
                    @Override
                    protected void onClick() {
                        hide();
                        select(info.name);
                    }
                };
                add(cont);

                RedButton erase = new RedButton(Messages.get(WndGameInProgress.class, "erase")) {
                    @Override
                    protected void onClick() {
                        super.onClick();

                        SandboxPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING),
                                Messages.get(WndSelectDungeon.class, "erase_title"),
                                Messages.get(WndSelectDungeon.class, "erase_body"),
                                Messages.get(ExoticPotion.class, "yes"), Messages.get(ExoticPotion.class, "no")) {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    CustomDungeon.deleteDungeon(info.name);
                                    allInfos.remove(info);
                                    dungeonNames.remove(info.name);
                                    updateList();
                                    WndInfoDungeon.this.hide();
                                }
                            }
                        });
                    }
                };

                RedButton export = new RedButton(Messages.get(WndSelectDungeon.class, "export_label")) {
                    @Override
                    protected void onClick() {
                        String fileName = "exports/" + info.name + ".json";
                        String destLocation = CustomDungeonSaves.getAbsolutePath(fileName);
                        Window w = new WndOptions(
                                Messages.get(WndSelectDungeon.class, "export_title", info.name),
                                Messages.get(WndSelectDungeon.class, "export_body", destLocation),
                                Messages.get(WndSelectDungeon.class, "export_yes"), Messages.get(WndSelectDungeon.class, "export_no")) {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    try {
                                        CustomDungeonSaves.setFileType(Files.FileType.External);
                                        CustomDungeonSaves.writeClearText(fileName,
                                                DungeonToJsonConverter.getAsJson(CustomDungeonSaves.loadDungeon(info.name)));

                                        Window win = new WndOptions(
                                                Messages.get(WndSelectDungeon.class, "export_confirm_title", info.name),
                                                Messages.get(WndSelectDungeon.class, "export_confirm_body", info.name) +
                                                        (info.name.equals("dungeon") ? "" : Messages.get(WndSelectDungeon.class, "export_confirm_rename_hint")),
                                                Messages.get(WndSelectDungeon.class, "export_confirm_close"));
                                        if (Game.scene() instanceof EditorScene)
                                            EditorScene.show(win);
                                        else Game.scene().addToFront(win);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        SandboxPixelDungeon.reportException(e);
                                    }
                                }
                            }
                        };
                        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                        else Game.scene().addToFront(w);
                    }
                };
                export.enable(info.numLevels > 0);


                float pos = 0;
                title.setPos((width - title.width()) * 0.5f, pos);
                pos = title.bottom() + GAP;

                pos = statSlot(Messages.get(WndSelectDungeon.class, "num_floors"), Integer.toString(info.numLevels), pos) + GAP * 2;

                cont.icon(Icons.get(Icons.ENTER));
                cont.setRect(0, pos, width / 2 - 1, 20);
                add(cont);

                erase.icon(Icons.get(Icons.CLOSE));
                erase.setRect(width / 2 + 1, pos, width / 2 - 1, 20);
                add(erase);

                pos = erase.bottom() + 3;

                export.setRect(0, pos, width, 20);
                add(export);

                resize(width, (int) export.bottom() + 1);
            }

            private float statSlot(String label, String value, float pos) {

                RenderedTextBlock txt = PixelScene.renderTextBlock(label, 8);
                txt.setPos(0, pos);
                add(txt);

                int size = 8;
                if (value.length() >= 14) size -= 2;
                if (value.length() >= 18) size -= 1;
                txt = PixelScene.renderTextBlock(value, size);
                txt.setPos(width * 0.55f, pos + (6 - txt.height()) / 2);
                PixelScene.align(txt);
                add(txt);

                return GAP + txt.height();
            }

        }

    }

    private static void openDungeon(String name) {
        try {
            Dungeon.customDungeon = CustomDungeonSaves.loadDungeon(name);
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
        }
        String lastEditedFloor = Dungeon.customDungeon.getLastEditedFloor();
        if (Dungeon.customDungeon.getNumFloors() == 0 || lastEditedFloor == null)
            SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
        else {
            LevelScheme l = Dungeon.customDungeon.getFloor(lastEditedFloor);
            if (l.getType() != CustomLevel.class)
                SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
            else {
                l.loadLevel();
                EditorScene.open((CustomLevel) l.getLevel());
            }
        }
    }
}