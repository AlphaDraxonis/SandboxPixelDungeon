package com.shatteredpixel.shatteredpixeldungeon.editor.server;

import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.DungeonPreview;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.HashSet;
import java.util.Set;

@NotAllowedInLua
public class ServerDungeonList extends MultiWindowTabComp {

	private static ServerDungeonList instance;

	public static DungeonPreview[][] dungeons;

	private Spinner outsideSp;
	protected RedButton upload;
	protected RedButton refresh;
	protected RenderedTextBlock msgWaitingForData;

	private int page = 0;
	private static int numPages, lastPage = 0;
	public static final int PREVIEWS_PER_PAGE = 4;

	private static final Set<Integer> pagesLoading = new HashSet<>(5);

	public ServerDungeonList() {

		instance = this;

		mainWindowComps = new Component[PREVIEWS_PER_PAGE];

		upload = new RedButton("") {
			@Override
			protected void onClick() {
				UploadDungeon.showUploadWindow(ServerCommunication.UploadType.UPLOAD, null);
			}

			@Override
			protected String hoverText() {
				return Messages.get(WndSelectDungeon.class, "upload_label");
			}
		};
		upload.icon(Icons.UPLOAD.get());
		add(upload);

		refresh = new RedButton("") {
			@Override
			protected void onClick() {
				dungeons = null;
				hidePage(page);
				requestPage(lastPage, true);
				for (int i = Math.max(0, lastPage-1); i < 3; i++) {
					loadPageFromServer(i, false);
				}
			}

			@Override
			protected String hoverText() {
				return Messages.get(WndSelectDungeon.class, "refresh_label");
			}
		};
		refresh.icon(Icons.REFRESH.get());
		refresh.icon().scale.set(0.7f);
		add(refresh);

		title = createTitle();
		add(title);

		msgWaitingForData = PixelScene.renderTextBlock(Messages.get(ServerCommunication.class, "wait_body"), 6);
		content.add(msgWaitingForData);

		outsideSp = new Spinner(new SpinnerIntegerModel(1, numPages, lastPage+1, true) {
			{
				setAbsoluteMinAndMax(getMinimum(), getMaximum());
			}

			@Override
			protected String displayString(Object value) {
				return Messages.get(ServerDungeonList.class, "page", outsideSp == null ? lastPage+1 : value, numPages);
			}

			@Override
			public float getInputFieldWidth(float height) {
				return Spinner.FILL;
			}

			@Override
			public int getClicksPerSecondWhileHolding() {
				return 0;
			}
		}, "", 10) {
			@Override
			protected void layout() {
				height = 20;
				super.layout();
			}
		};
		outsideSp.visible = outsideSp.active = numPages > 1;

		outsideSp.addChangeListener(() -> {
			hidePage(page);
			requestPage((int) outsideSp.getValue()-1, true);
			layout();
			sp.scrollTo(0, 0);
		});

		if (dungeons == null) {
			for (int i = Math.max(0, lastPage-1); i < 3; i++) {
				loadPageFromServer(i, false);
			}
		}
		requestPage(lastPage, true);
	}

	@Override
	public void layout() {
		super.layout();
		upload.givePointerPriority();
	}

	public static void setNumPreviews(int numPreviews) {
		numPages = (int) Math.ceil((float) numPreviews / PREVIEWS_PER_PAGE);
		if (dungeons == null) {
			dungeons = new DungeonPreview[numPages][];
		}
		if (instance != null) {
			instance.outsideSp.visible = instance.outsideSp.active = numPages > 1;
			((SpinnerIntegerModel) instance.outsideSp.getModel()).setMaximum(numPages);
			((SpinnerIntegerModel) instance.outsideSp.getModel()).setAbsoluteMaximum(numPages);
			Game.runOnRenderThread(() -> instance.outsideSp.setValue(instance.outsideSp.getValue()));
			((WndServerDungeonList) EditorUtilities.getParentWindow(instance)).updateLayout();
		}
	}

	private void killMainWindowComp(int i) {
		if (mainWindowComps[i] != null) {
			mainWindowComps[i].remove();
			mainWindowComps[i].killAndErase();
			mainWindowComps[i].destroy();
			mainWindowComps[i] = null;
		}
	}

	private void hidePage(int page) {
		for (int i = 0; i < mainWindowComps.length; i++) {
			killMainWindowComp(i);
		}
	}

	private void initPage(int page) {
		msgWaitingForData.setVisible(false);
		this.page = page;
		for (int i = 0; i < dungeons[page].length; i++) {
			killMainWindowComp(i);
			mainWindowComps[i] = createListItem(dungeons[page][i]);
			content.add(mainWindowComps[i]);
		}
	}

	public static void updatePage() {
		if (instance != null) {
			instance.hidePage(instance.page);
			instance.initPage(instance.page);
		}
	}

	public void requestPage(int page, boolean startMoreProcesses) {
		lastPage = page;
		if (dungeons == null || dungeons.length > 0 && dungeons[page] == null) {
			msgWaitingForData.setVisible(true);
			loadPageFromServer(page, startMoreProcesses);
		} else {
			if (dungeons.length == 0) {
				msgWaitingForData.setVisible(false);
				if (sp.camera() != null) {
					layout();
					sp.scrollTo(0, 0);
				}
			}
			else initPage(page);
		}
	}

	private synchronized void loadPageFromServer(final int page, boolean startMoreProcesses) {
		if (pagesLoading.contains(page)) return;
		pagesLoading.add(page);
		ServerCommunication.dungeonList(new ServerCommunication.OnPreviewReceive() {
			@Override
			protected void onSuccessful(DungeonPreview[] previews) {
				pagesLoading.remove(page);
				if (dungeons.length <= page) {
					if (dungeons.length == 0) {
						msgWaitingForData.setVisible(false);
						instance.layout();
						instance.sp.scrollTo(0, 0);
					}
					return;
				}
				dungeons[page] = previews;
				if (lastPage == page && instance != null) {
					instance.initPage(page);
					instance.layout();
					instance.sp.scrollTo(0, 0);
				}
				if (startMoreProcesses && instance != null) {
					if (page + 1 < dungeons.length && dungeons[page + 1] == null && !pagesLoading.contains(page+1)) loadPageFromServer(page+1, true);
					else if (page > 0 && dungeons[page - 1] != null  && !pagesLoading.contains(page-1)) loadPageFromServer(page-1, true);
				}
			}

			@Override
			public void failed(Throwable t) {
				super.failed(t);
				pagesLoading.remove(page);
			}
		}, page);
	}

	@Override
	protected void layoutOwnContent() {
		if (mainWindowComps[0] == null) {
			msgWaitingForData.maxWidth((int) width);
			msgWaitingForData.setPos((width - msgWaitingForData.width()) * 0.5f, 0);
			content.setSize(msgWaitingForData.width(), msgWaitingForData.height());
		}
		else {
			content.setSize(width, 0);
			content.setSize(width, EditorUtilities.layoutCompsLinear(GAP, content, mainWindowComps));
		}
		upload.setRect(x + width - 17, y + height - 15, 16, 16);
	}

	@Override
	public void changeContent(Component titleBar, Component body, Component outsideSp, float contentAlignmentV, float titleAlignmentH) {
		upload.setVisible(false);
		refresh.setVisible(false);
		super.changeContent(titleBar, body, outsideSp, contentAlignmentV, titleAlignmentH);
	}

	@Override
	public void closeCurrentSubMenu() {
		upload.setVisible(true);
		refresh.setVisible(true);
		super.closeCurrentSubMenu();
	}

	public Component createTitle() {
		return new Component() {
			private IconTitle main;
			{
				main = new IconTitle(null, Messages.titleCase(Messages.get(ServerDungeonList.class, "title")));
				add(main);
			}

			@Override
			protected void layout() {
				main.setRect(x, y, width - 20, -1);
				height = Math.max(18, main.height());
				refresh.setRect(width - 17, (height - 16) * 0.5f + 1, 16, 16);
			}
		};
	};

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

	private DungeonPreviewItem createListItem(DungeonPreview preview) {
		return preview.dungeonFileID.startsWith("ERROR")
				? new DungeonPreviewItem(preview.dungeonFileID)
				: new DungeonPreviewItem(preview);
	}

	private class DungeonPreviewItem extends Button {

		private final DungeonPreview preview;

		protected RenderedTextBlock title, by, creator, desc;

		protected ColorBlock line;

		private DungeonPreviewItem(String error) {
			super();
			preview = null;

			title.text( Messages.get(ServerCommunication.class, "error") + ": " + error );
			desc = by = creator = null;
		}

		private DungeonPreviewItem(DungeonPreview preview) {
			super();
			this.preview = preview;

			title.text( preview.title );
			by.text(Messages.get(ServerDungeonList.class, "title_entry"));
			creator.text("_" + preview.uploader + "_");

			desc.text(preview.description);
		}

		@Override
		protected void createChildren() {
			super.createChildren();

			title = PixelScene.renderTextBlock(7);
			title.setHighlighting(false);
			title.hardlight(Window.TITLE_COLOR);
			add(title);

			by = PixelScene.renderTextBlock(7);
			by.setHighlighting(false);
			add(by);

			creator = PixelScene.renderTextBlock(7);
			add(creator);

			desc = PixelScene.renderTextBlock(6);
			desc.maxNumLines = 3;
			add(desc);

			line = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR);
			add(line);
		}

		@Override
		protected void layout() {

			title.maxWidth((int) width);

			if (desc != null) {
				by.maxWidth((int) width);
				creator.maxWidth((int) width);
				desc.maxWidth((int) width - 3);
			}

			title.setPos(x + 1, y + 3);

			line.size(width, 1);
			line.x = x;
			line.y = y;

			if (desc != null) {
				by.setPos(title.right(), title.top());
				creator.setPos(by.right(), by.top());

				if (creator.right() > width - 3) {
					if (creator.width() + by.width() < title.maxWidth()) {
						by.setPos(title.left(), title.bottom() + 4);
						creator.setPos(by.right(), by.top());
					} else if (by.right() <= width - 3) {
						creator.setPos(title.left(), title.bottom() + 4);
					} else {
						by.setPos(title.left(), title.bottom() + 4);
						creator.setPos(by.left(), by.bottom() + 4);
					}
				}
				desc.setPos(title.left() + 3, creator.bottom() + 4);
				height = desc.bottom() - y + 1;
			} else {
				height = title.bottom() + 1;
			}

			super.layout();
		}

		@Override
		protected void onClick() {
			if (preview != null) {
				WndPreview prev = new WndPreview(preview, ServerDungeonList.this);
				changeContent(prev.createTitle(), prev, prev.getOutsideSp());

				if (preview.intVersion > Game.versionCode) {
					Game.scene().addToFront(new WndOptions(
							Icons.WARNING.get(),
							Messages.titleCase(Messages.get(WndPreview.class, "update_req_title")),
							Messages.get(WndPreview.class, "update_req_body"),
							Messages.get(WndPreview.class, "update_now"),
							Messages.get(WndPreview.class, "update_later")
					) {
						@Override
						protected void onSelect(int index) {
							if (index == 0) {
								if (Updates.updateAvailable()) Updates.launchUpdate(Updates.updateData());
								else Game.platform.openURI("https://github.com/AlphaDraxonis/SandboxPixelDungeon/releases/latest");
							}
						}
					});
				}
			}
		}
	}

	public static void updateLayout() {
		if (instance != null) instance.layout();
	}

	public static class WndServerDungeonList extends Window {
		private ServerDungeonList serverDungeonList;
		private Component outsideSp;

		public WndServerDungeonList() {
			super(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

			add(serverDungeonList = new ServerDungeonList() {
				@Override
				public void changeContent(Component titleBar, Component body, Component outsideSp, float contentAlignmentV, float titleAlignmentH) {
					super.changeContent(titleBar, body, outsideSp, contentAlignmentV, titleAlignmentH);
					if (WndServerDungeonList.this.outsideSp != null) {
						WndServerDungeonList.this.outsideSp.setVisible(false);
						serverDungeonList.setSize(WndServerDungeonList.this.width, WndServerDungeonList.this.height - 2);
					}
					sp.givePointerPriority();
					upload.givePointerPriority();
				}

				@Override
				public void closeCurrentSubMenu() {
					super.closeCurrentSubMenu();
					if (outsideSp != null) {
						outsideSp.setVisible(numPages > 1);
						if (outsideSp.visible)
							serverDungeonList.setSize(WndServerDungeonList.this.width, WndServerDungeonList.this.height - outsideSp.height() - 4);
					}
				}
			});
			outsideSp = serverDungeonList.getOutsideSp();
			add(outsideSp);

			updateLayout();
		}

		private void updateLayout() {
			if (outsideSp != null && outsideSp.visible) {
				outsideSp.setSize(width, -1);
				outsideSp.setPos(0, height - outsideSp.height());
				serverDungeonList.setRect(0, 2, width, height - outsideSp.height() - 4);
			} else {
				serverDungeonList.setRect(0, 2, width, height - 2);
			}
			serverDungeonList.sp.givePointerPriority();
			serverDungeonList.upload.givePointerPriority();
		}
	}

}