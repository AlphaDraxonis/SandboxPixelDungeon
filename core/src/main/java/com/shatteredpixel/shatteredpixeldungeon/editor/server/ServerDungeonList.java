package com.shatteredpixel.shatteredpixeldungeon.editor.server;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.DungeonPreview;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class ServerDungeonList extends MultiWindowTabComp {

    private static ServerDungeonList instance;

    private final DungeonPreview[] dungeons;

    private Spinner outsideSp;

    private int page = 0;
    private static final int PREVIEWS_PER_PAGE = 20;

    public ServerDungeonList(DungeonPreview[] dungeons) {

        instance = this;

        this.dungeons = dungeons;

        title = createTitle();
        add(title);

        mainWindowComps = new Component[PREVIEWS_PER_PAGE];

        if (dungeons.length > PREVIEWS_PER_PAGE) {
            int numPages = dungeons.length / PREVIEWS_PER_PAGE + 1;
            outsideSp = new Spinner(new SpinnerIntegerModel(1, numPages, 1, 1, true, null) {
                {
                    setAbsoluteMinAndMax(getMinimum(), getMaximum());
                }

                @Override
                public String getDisplayString() {
                    return Messages.get(ServerDungeonList.class, "page", outsideSp == null ? 1 : outsideSp.getValue(), numPages);
                }

                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, "", 10) {
                @Override
                protected void layout() {
                    height = 20;
                    super.layout();
                }
            };
            outsideSp.addChangeListener(() -> {
                hidePage(page);
                initPage((int) outsideSp.getValue() - 1);
                layout();
                sp.scrollTo(0, 0);
            });
        }

        initPage(page);
    }

    private void hidePage(int page) {
        int indexLast = Math.min(dungeons.length, (page + 1) * PREVIEWS_PER_PAGE) - page * PREVIEWS_PER_PAGE;
        for (int i = 0; i < indexLast; i++) {
            mainWindowComps[i].remove();
            mainWindowComps[i].killAndErase();
            mainWindowComps[i].destroy();
            mainWindowComps[i] = null;
        }
    }

    private void initPage(int page) {
        this.page = page;
        int indexLast = Math.min(dungeons.length, (page + 1) * PREVIEWS_PER_PAGE);
        int pageMultiply = page * PREVIEWS_PER_PAGE;
        for (int i = pageMultiply; i < indexLast; i++) {
            mainWindowComps[i - pageMultiply] = createListItem(dungeons[i]);
            content.add(mainWindowComps[i - pageMultiply]);
        }
    }

    public static void updatePage() {
        if (instance != null) {
            instance.hidePage(instance.page);
            instance.initPage(instance.page);
        }
    }

    @Override
    protected void layoutOwnContent() {
        content.setSize(width, -1);
        content.setSize(width, EditorUtilies.layoutCompsLinear(GAP, content, mainWindowComps));
    }

    public Component createTitle() {
//        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(ServerDungeonList.class, "title")), 12);
//        title.hardlight(Window.TITLE_COLOR);
        IconTitle title = new IconTitle(null, Messages.titleCase(Messages.get(ServerDungeonList.class, "title")));
        return title;
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    @Override
    public Image createIcon() {
        return null;
    }

    @Override
    public String hoverText() {
        return null;
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (instance == this) instance = null;
    }

    public DungeonPreviewItem createListItem(DungeonPreview preview) {
        return preview.dungeonFileID.startsWith("ERROR")
                ? new DungeonPreviewItem(preview.dungeonFileID)
                : new DungeonPreviewItem(preview);
    }

    private class DungeonPreviewItem extends ScrollingListPane.ListItem {

        private final DungeonPreview preview;

        protected RenderedTextBlock desc;

        private DungeonPreviewItem(String error) {
            super(new Image(), Messages.get(ServerCommunication.class, "error") + ": " + error);
            preview = null;
        }

        private DungeonPreviewItem(DungeonPreview preview) {
            super(new Image(), null, Messages.get(ServerDungeonList.class, "title_entry", preview.title, preview.uploader));
            this.preview = preview;

            desc = PixelScene.renderTextBlock(preview.description, 6);
            desc.maxNumLines = 3;
            add(desc);
        }

        @Override
        protected void layout() {
            if (desc == null) super.layout();
            else {
                desc.maxWidth(getLabelMaxWidth() - 3);
                label.maxWidth(getLabelMaxWidth());
                height = label.height() + desc.height() + 9;
                super.layout();
                label.setPos(label.left(), y + 3);
                desc.setPos(label.left() + 3, label.bottom() + 4);
            }
        }

        @Override
        protected void onClick() {
            if (preview != null) {
                WndPreview prev = new WndPreview(preview, ServerDungeonList.this);
                changeContent(prev.createTitle(), prev, prev.getOutsideSp());
            }
        }
    }


    public static void show() {
        if (SPDSettings.WiFi() && !Game.platform.connectedToUnmeteredNetwork()) {
            Game.scene().addToFront(new WndOptions(
                    Messages.get(ServerCommunication.class, "paid_wifi_title"),
                    Messages.get(ServerCommunication.class, "paid_wifi_body"),
                    Messages.get(ServerCommunication.class, "paid_wifi_yes"),
                    Messages.get(ServerCommunication.class, "paid_wifi_no")
            ) {
                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        ServerCommunication.dungeonList(new ServerCommunication.OnPreviewReceive() {
                            @Override
                            protected void onSuccessful(DungeonPreview[] previews) {
                                Game.scene().addToFront(new WndServerDungeonList(previews));
                            }
                        });
                    }
                }
            });
            return;
        }

        ServerCommunication.dungeonList(new ServerCommunication.OnPreviewReceive() {
            @Override
            protected void onSuccessful(DungeonPreview[] previews) {
                Game.scene().addToFront(new WndServerDungeonList(previews));
            }
        });
    }

    public static void updateLayout() {
        if (instance != null) instance.layout();
    }

    private static class WndServerDungeonList extends Window {
        private ServerDungeonList serverDungeonList;
        private Component outsideSp;

        public WndServerDungeonList(DungeonPreview[] previews) {
            super(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

            add(serverDungeonList = new ServerDungeonList(previews) {
                @Override
                public void changeContent(Component titleBar, Component body, Component outsideSp, float alignment, float titleAlignmentX) {
                    super.changeContent(titleBar, body, outsideSp, alignment, titleAlignmentX);
                    if (WndServerDungeonList.this.outsideSp != null) {
                        WndServerDungeonList.this.outsideSp.visible
                                = WndServerDungeonList.this.outsideSp.active = false;
                        serverDungeonList.setSize(WndServerDungeonList.this.width, WndServerDungeonList.this.height - 2);
                    }
                }

                @Override
                public void closeCurrentSubMenu() {
                    super.closeCurrentSubMenu();
                    if (outsideSp != null) {
                        outsideSp.active = outsideSp.visible = true;
                        serverDungeonList.setSize(WndServerDungeonList.this.width, WndServerDungeonList.this.height - outsideSp.height() - 4);
                    }
                }
            });
            outsideSp = serverDungeonList.getOutsideSp();
            if (outsideSp != null) {
                add(outsideSp);
                outsideSp.setSize(width, -1);
                outsideSp.setPos(0, height - outsideSp.height());
                serverDungeonList.setRect(0, 2, width, height - outsideSp.height() - 4);
            } else {
                serverDungeonList.setRect(0, 2, width, height - 2);
            }
        }
    }

}