/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.server.BugReportUploadDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.server.ServerDungeonList;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.Fireball;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.AvailableUpdateData;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndVictoryCongrats;
import com.watabou.NotAllowedInLua;
import com.watabou.glwrap.Blending;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.ColorMath;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NotAllowedInLua
public class TitleScene extends PixelScene {

	private Image title;
	private Fireball leftFB;
	private Fireball rightFB;
	private Image signs;

	private StyledButton btnPlay;
	private StyledButton btnEditor;
	private StyledButton btnDiscover;
	private StyledButton btnRankings;
	private StyledButton btnHelp;
	private StyledButton btnChanges;
	private StyledButton btnSettings;
	private StyledButton btnAbout;
	
	private DiscordButton btnDiscord;
	private ReportBugButton btnBug;
	private IconButton btnJournal;

	private BitmapText version;
	private IconButton btnFade;
	private ExitButton btnExit;

	@Override
	public void create() {

		Dungeon.customDungeon = null;

		super.create();

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
				new float[]{1, 1},
				false);

		uiCamera.visible = false;
		
		int w = Camera.main.width;
		int h = Camera.main.height;

		RectF insets = getCommonInsets();

		TitleBackground BG = new TitleBackground( w, h );
		add( BG );

		w -= insets.left + insets.right;
		h -= insets.top + insets.bottom;

		title = BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON);
		add( title );

		float topRegion = Math.max(title.height - 6, h*0.45f);

		title.x = insets.left + (w - title.width()) / 2f;
		title.y = insets.top + 2 + (topRegion - title.height()) / 2f;

		align(title);
		
		leftFB = placeTorch(title.x + 9, title.y + 46);
		rightFB = placeTorch(title.x + title.width - 10, title.y + 46);

		signs = new Image(BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON_SIGNS)){
			private float time = 0;
			@Override
			public void update() {
				super.update();
				am = Math.max(0f, (float)Math.sin( time += Game.elapsed ));
				am = Math.min(am, title.am);
				if (time >= 1.5f*Math.PI) time = 0;
			}
			@Override
			public void draw() {
				Blending.setLightMode();
				super.draw();
				Blending.setNormalMode();
			}
		};
		signs.x = title.x + (title.width() - signs.width())/2f;
		signs.y = title.y;
		signs.color(Window.SILVER);
		add(signs);
		
		version = new BitmapText( "v" + Game.version, pixelFont);
		version.measure();
		version.hardlight( 0x888888 );
		version.x = insets.left + w - version.width() - (DeviceCompat.isDesktop() ? 4 : 8);
		version.y = insets.top + h - version.height() - (DeviceCompat.isDesktop() ? 2 : 4);
		add( version );

		final Chrome.Type GREY_TR = Chrome.Type.GREY_BUTTON_TR;

		btnDiscord = new DiscordButton();
		btnDiscord.setPos(5, 5);
		btnDiscord.updateSize();
		add(btnDiscord);

		btnBug = new ReportBugButton();
		btnBug.setPos(5, h - 5 - 16);
		add(btnBug);

		btnJournal = new IconButton(Icons.JOURNAL.get()) {
			@Override
			protected void onClick() {
				SandboxPixelDungeon.switchNoFade( JournalScene.class );
			}
			
			@Override
			protected String hoverText() {
				return Messages.get(TitleScene.class, "journal");
			}
		};
		btnJournal.setRect(w - 5 - btnJournal.icon().width(), version.y - 4 - btnJournal.icon().height(), btnJournal.icon().width(), btnJournal.icon().height());
		add(btnJournal);

		btnPlay = new StyledButton(GREY_TR, Messages.get(this, "enter")) {
			@Override
			protected void onClick() {
				FileUtils.resetDefaultFileType();
				if (GamesInProgress.checkAll().size() == 0) {
					StartScene.showWndSelectDungeon(1);
				} else {
					SandboxPixelDungeon.switchNoFade(StartScene.class);
				}
			}
			
			@Override
			protected boolean onLongClick() {
				//making it easier to start runs quickly while debugging
				if (DeviceCompat.isDebug()) {
					StartScene.showWndSelectDungeon(1);
					return true;
				}
				return super.onLongClick();
			}
		};
		btnPlay.icon(Icons.get(Icons.ENTER));
		add(btnPlay);

		btnEditor = new SupportButton(GREY_TR, Messages.get(this, "editor"));
		add(btnEditor);

		btnRankings = new StyledButton(GREY_TR,Messages.get(this, "rankings")){
			@Override
			protected void onClick() {
				SandboxPixelDungeon.switchNoFade(RankingsScene.class);
			}
		};
		btnRankings.icon(Icons.get(Icons.RANKINGS));
		add(btnRankings);
		Dungeon.daily = Dungeon.dailyReplay = false;

		btnDiscover = new StyledButton(GREY_TR, Messages.get(this, "discover")) {
			{
				if (SPDSettings.updates()) Updates.checkForNewCommunityDungeons();
			}
			@Override
			protected void onClick() {
				Game.scene().addToFront(new ServerDungeonList.WndServerDungeonList());
			}

			private boolean isInterpolatingColor = false;

			@Override
			public void update() {
				super.update();

				if (Updates.newDungeonsAvailable()) {
					isInterpolatingColor = true;
					textColor(ColorMath.interpolate( 0xFFFFFF, 0xF68334, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
				} else if (isInterpolatingColor) {
					isInterpolatingColor = false;
					textColor(0xFFFFFF);
				}
			}
		};
		btnDiscover.icon(Icons.get(Icons.DOWNLOAD));
		add(btnDiscover);

		btnHelp = new NewsButton(GREY_TR, Messages.get(this, "help"));
		btnHelp.icon(Icons.get(Icons.NEWS));
		add(btnHelp);

		btnChanges = new ChangesButton(GREY_TR, Messages.get(this, "changes"));
		btnChanges.icon(Icons.get(Icons.CHANGES));
		add(btnChanges);

		btnSettings = new SettingsButton(GREY_TR, Messages.get(this, "settings"));
		add(btnSettings);

		btnAbout = new StyledButton(GREY_TR, Messages.get(this, "about")){
			@Override
			protected void onClick() {
				SandboxPixelDungeon.switchScene(AboutScene.class);
			}
		};
		btnAbout.icon(Icons.get(Icons.SUPPORT_EMERALD));
		add(btnAbout);
		
		final int BTN_HEIGHT = 20;
		int GAP = (int)(h - topRegion - (landscape() ? 3 : 4)*BTN_HEIGHT)/3;
		GAP /= landscape() ? 3 : 5;
		GAP = Math.max(GAP, 2);

		float buttonAreaWidth = landscape() ? PixelScene.MIN_WIDTH_L-6 : PixelScene.MIN_WIDTH_P-2;
		float btnAreaLeft = insets.left + (w - buttonAreaWidth) / 2f;
		if (landscape()) {
			btnPlay.setRect(btnAreaLeft, insets.top + topRegion+GAP, (buttonAreaWidth/2)-1, BTN_HEIGHT);
			align(btnPlay);
			btnEditor.setRect(btnPlay.right() + 2, btnPlay.top(), btnPlay.width(), BTN_HEIGHT);
			btnDiscover.setRect(btnPlay.left(), btnPlay.bottom() + GAP, (float) (Math.floor(buttonAreaWidth/3f) - 1), BTN_HEIGHT);
			btnRankings.setRect(btnDiscover.left(), btnDiscover.bottom() + GAP, btnDiscover.width(), BTN_HEIGHT);
			btnHelp.setRect(btnDiscover.right() + 2, btnDiscover.top(), btnDiscover.width(), BTN_HEIGHT);
			btnChanges.setRect(btnHelp.left(), btnHelp.bottom() + GAP, btnDiscover.width(), BTN_HEIGHT);
			btnSettings.setRect(btnHelp.right() + 2, btnHelp.top(), btnDiscover.width(), BTN_HEIGHT);
			btnAbout.setRect(btnSettings.left(), btnSettings.bottom() + GAP, btnDiscover.width(), BTN_HEIGHT);
		} else {
			btnPlay.setRect(btnAreaLeft, insets.top + topRegion+GAP, buttonAreaWidth, BTN_HEIGHT);
			align(btnPlay);
			btnEditor.setRect(btnPlay.left(), btnPlay.bottom() + GAP, btnPlay.width(), BTN_HEIGHT);
			btnDiscover.setRect(btnPlay.left(), btnEditor.bottom() + GAP, (btnPlay.width() / 2) - 1, BTN_HEIGHT);
			btnRankings.setRect(btnDiscover.right() + 2, btnDiscover.top(), btnDiscover.width(), BTN_HEIGHT);
			btnHelp.setRect(btnDiscover.left(), btnDiscover.bottom() + GAP, btnDiscover.width(), BTN_HEIGHT);
			btnChanges.setRect(btnHelp.right() + 2, btnHelp.top(), btnHelp.width(), BTN_HEIGHT);
			btnSettings.setRect(btnHelp.left(), btnHelp.bottom() + GAP, btnDiscover.width(), BTN_HEIGHT);
			btnAbout.setRect(btnSettings.right() + 2, btnSettings.top(), btnSettings.width(), BTN_HEIGHT);
		}

		btnFade = new IconButton(Icons.CHEVRON.get()){
			@Override
			protected void onClick() {
				enable(false);
				parent.add(new Tweener(parent, 0.5f) {
					@Override
					protected void updateValues(float progress) {
						if (!btnFade.active) {
							uiAlpha = 1 - progress;
							updateFade();
						}
					}
				});
			}
		};
		btnFade.icon().originToCenter();
		btnFade.icon().angle = 180f;
		btnFade.setRect(btnAreaLeft + (buttonAreaWidth-16)/2, camera.main.height - 16, 16, 16);
		add(btnFade);

		PointerArea fadeResetter = new PointerArea(0, 0, Camera.main.width, Camera.main.height){
			@Override
			public boolean onSignal(PointerEvent event) {
				if (event != null && event.type == PointerEvent.Type.UP && !btnPlay.active){
					parent.add(new Tweener(parent, 0.5f) {
						@Override
						protected void updateValues(float progress) {
							uiAlpha = progress;
							updateFade();
							if (progress >= 1){
								btnFade.enable(true);
							}
						}
					});
				}
				return false;
			}
		};
		add(fadeResetter);

		if (DeviceCompat.isDesktop()) {
			btnExit = new ExitButton();
			btnExit.setPos( w - btnExit.width(), 0 );
			add( btnExit );
		}

		Badges.loadGlobal();
		if (Badges.isUnlocked(Badges.Badge.VICTORY) && !SPDSettings.victoryNagged()) {
			SPDSettings.victoryNagged(true);
			add(new WndVictoryCongrats());
		}

		fadeIn();
	}

	private float uiAlpha;

	public void updateFade() {
		float alpha = GameMath.gate(0f, uiAlpha, 1f);

		title.am = alpha;
		leftFB.alpha(alpha);
		rightFB.alpha(alpha);
		//signs.am = alpha; handles this itself

		btnPlay.enable(alpha != 0);
		btnEditor.enable(alpha != 0);
		btnRankings.enable(alpha != 0);
		btnDiscover.enable(alpha != 0);
		btnHelp.enable(alpha != 0);
		btnChanges.enable(alpha != 0);
		btnSettings.enable(alpha != 0);
		btnAbout.enable(alpha != 0);
		
		btnJournal.enable(alpha != 0);
		btnDiscord.active = alpha != 0;
		btnBug.active = alpha != 0;

		btnPlay.alpha(alpha);
		btnEditor.alpha(alpha);
		btnRankings.alpha(alpha);
		btnDiscover.alpha(alpha);
		btnHelp.alpha(alpha);
		btnChanges.alpha(alpha);
		btnSettings.alpha(alpha);
		btnAbout.alpha(alpha);
		
		btnJournal.alpha(alpha);
		btnDiscord.alpha(alpha);
		btnBug.alpha(alpha);

		version.alpha(alpha);
		btnFade.icon().alpha(alpha);
		if (btnExit != null){
			btnExit.enable(alpha != 0);
			btnExit.icon().alpha(alpha);
		}
	}

	private Fireball placeTorch(float x, float y ) {
		Fireball fb = new Fireball();
		fb.setColor(Window.GOLD);
		fb.setPos( x, y );
		add( fb );
		return fb;
	}

	private static class NewsButton extends StyledButton {

        public NewsButton(Chrome.Type type, String label) {
            super(type, label);
			ServerCommunication.loadURL(false);
//            if (SPDSettings.news()) News.checkForNews();
        }

//        int unreadCount = -1;

		@Override
		public void update() {
			super.update();

//            if (unreadCount == -1 && News.articlesAvailable()) {
//                long lastRead = SPDSettings.newsLastRead();
//                if (lastRead == 0) {
//                    if (News.articles().get(0) != null) {
//                        SPDSettings.newsLastRead(News.articles().get(0).date.getTime());
//                    }
//                } else {
//                    unreadCount = News.unreadArticles(new Date(SPDSettings.newsLastRead()));
//                    if (unreadCount > 0) {
//                        unreadCount = Math.min(unreadCount, 9);
//                        text(text() + "(" + unreadCount + ")");
//                    }
//                }
//            }
//
//            if (unreadCount > 0) {
//                textColor(ColorMath.interpolate(0xFFFFFF, Window.SHPX_COLOR, 0.5f + (float) Math.sin(Game.timeTotal * 5) / 2f));
//            }
        }

        @Override
        protected void onClick() {
            super.onClick();
            SandboxPixelDungeon.platform.openURI("https://docs.google.com/document/d/1LEx8uZYdv04ndrITJeUttdphXp_hkJ5WDdysWNQTXyY");
//            Sandbox.switchNoFade(NewsScene.class);
        }
    }

	private static class ChangesButton extends StyledButton {

		public ChangesButton( Chrome.Type type, String label ){
			super(type, label);
			if (SPDSettings.updates()) Updates.checkForUpdate();
		}

		boolean updateShown = false;

		@Override
		public void update() {
			super.update();

			if (!updateShown && Updates.updateAvailable()){
				updateShown = true;
				text(Messages.get(TitleScene.class, "update"));
			}

			if (updateShown){
				textColor(ColorMath.interpolate( 0xFFFFFF, Window.SHPX_COLOR, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
			}
		}

		@Override
		protected void onClick() {
			if (Updates.updateAvailable()){
				AvailableUpdateData update = Updates.updateData();
				
				String desc;
				if (update.desc == null) {
					desc = Messages.get(this,"desc");
					desc = desc.replace("Shattered Pixel Dungeon", "Sandbox Pixel Dungeon");
					desc = desc.replace("Shattered PD", "Sandbox PD");
					desc = desc.replace("ShatteredPD", "SandboxPD");
				} else {
					desc = update.desc;
				}
				

				SandboxPixelDungeon.scene().addToFront( new WndOptions(
						Icons.get(Icons.CHANGES),
						update.versionName == null ? Messages.get(this,"title") : Messages.get(this,"versioned_title", update.versionName),
						desc,
						Messages.get(this,"update"),
						Messages.get(this,"changes")
				) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							Updates.launchUpdate(Updates.updateData());
						} else if (index == 1){
							ChangesScene.changesSelected = 0;
							SandboxPixelDungeon.switchNoFade( ChangesScene.class );
						}
					}
				});

			} else {
				ChangesScene.changesSelected = 0;
				SandboxPixelDungeon.switchNoFade( ChangesScene.class );
			}
		}

	}

	private static class SettingsButton extends StyledButton {

		public SettingsButton( Chrome.Type type, String label ){
			super(type, label);
			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				icon(Icons.get(Icons.LANGS));
				icon.hardlight(1.5f, 0, 0);
			} else {
				icon(Icons.get(Icons.PREFS));
			}
		}

		@Override
		public void update() {
			super.update();

			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				textColor(ColorMath.interpolate( 0xFFFFFF, CharSprite.NEGATIVE, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
			}
		}

		@Override
		protected void onClick() {
			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				WndSettings.last_index = 5;
			}
			SandboxPixelDungeon.scene().add(new WndSettings());
		}
	}

	private static class SupportButton extends StyledButton{

		public SupportButton( Chrome.Type type, String label ){
			super(type, label);
			icon(Icons.get(Icons.EDITOR));
			textColor(Window.TITLE_COLOR);
		}

        private static final Set<String> EMPTY_HASHSET = new HashSet<>(0);

        @Override
        protected void onClick() {
            EditorScene.start();

			List<CustomDungeonSaves.Info> allInfos = CustomDungeonSaves.getAllInfos();
			if (allInfos != null) {
				if (allInfos.isEmpty()) {
					Game.scene().addToFront(new WndNewDungeon(EMPTY_HASHSET));
				} else {
					Game.scene().addToFront(new WndSelectDungeon(allInfos, true, true));
				}
			}
        }
    }

	public static class ReportBugButton extends Button {
		private Image image;
		private BitmapText text;
		@Override
		protected void createChildren() {
			super.createChildren();

			image = Icons.BUG.get();
			add(image);

			text = new BitmapText(Messages.get(this, "name"), PixelScene.pixelFont);
			text.measure();
			add(text);
		}

		@Override
		protected void layout() {

			height = image.height;
			width = image.width + 2 + text.width;
			super.layout();

			image.x = x;
			image.y = y;
			text.x = x + image.width - 2;
			text.y = y + (height - text.height()) * 0.5f + 1;
		}

		@Override
		protected void onPointerDown() {
			image.brightness(1.2f);
			text.brightness(1.2f);
			Sample.INSTANCE.play(Assets.Sounds.CLICK);
		}

		@Override
		protected void onPointerUp() {
			image.resetColor();
			text.resetColor();
		}

		@Override
		protected void onClick() {
			BugReportUploadDungeon.showUploadWindow();
		}
		
		public void alpha(float alpha) {
			image.alpha(alpha);
			text.alpha(alpha);
		}
	}

    public static class DiscordButton extends Button {

        private Image image;
        private BitmapText text;

        private PointF scale;
        private boolean increasing;

        @Override
        protected void createChildren() {
            super.createChildren();

            scale = new PointF(1, 1);
            increasing = true;

            image = new Image(Assets.Interfaces.ICON_DISCORD);
            image.scale = scale;
            image.originToCenter();
            add(image);

            text = new BitmapText(Messages.get(this, "name"), PixelScene.pixelFont);
//            text.scale = scale;
            add(text);
        }

        @Override
        protected void layout() {
            super.layout();

            image.x = x;
            image.y = y;
            text.x = x + image.width + 2;
            text.y = y + 2;

            text.measure();
            //if you don't call originToCenter
//            float w = image.width ;
//            float h = image.height;
//            image.x = (int) (x - (w * (scale.x - 1) / 2));
//            image.y = (int) (y - (h * (scale.y - 1) / 2));
//            text.x = (int) (x + image.width * scale.x + 2 - (w * (scale.x - 1) / 2));
//            text.y = (int) (y + 2 - (h * (scale.y - 1) / 2));
        }

        @Override
        protected void onPointerDown() {
            image.brightness(1.2f);
            text.brightness(1.2f);
            Sample.INSTANCE.play(Assets.Sounds.CLICK);
        }

        @Override
        protected void onPointerUp() {
            image.resetColor();
            text.resetColor();
        }

        @Override
        protected void onClick() {
			SPDSettings.discordClicked(true);
            SandboxPixelDungeon.platform.openURI("https://discord.gg/AQAyPqwXvS");
//          SandboxPixelDungeon.platform.openURI("https://discord.gg@download.zip");
        }

        public void updateSize() {
            setSize(image.width + 2 + text.width(), image.height);
        }

        @Override
        public void update() {
            super.update();
			if (!SPDSettings.discordClicked()) {
				if (scale.x >= 1.8f || scale.x <= .8f) increasing = !increasing;
				float diff = .05f;
				if (increasing) diff = -diff;
				scale.x += diff;
				scale.y += diff;
				layout();
			}
        }
		
		public void alpha(float alpha) {
			image.alpha(alpha);
			text.alpha(alpha);
		}
	}
}
