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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaClassGenerator;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.server.ServerDungeonList;
import com.shatteredpixel.shatteredpixeldungeon.editor.server.UploadDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.Fireball;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.AvailableUpdateData;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.ColorMath;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;
import com.watabou.utils.PointF;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TitleScene extends PixelScene {
	
	@Override
	public void create() {
		
		super.create();

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
				new float[]{1, 1},
				false);

		uiCamera.visible = false;
		
		int w = Camera.main.width;
		int h = Camera.main.height;
		
		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );
		
		Image title = BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON );
		add( title );

		float topRegion = Math.max(title.height - 6, h*0.45f);

		title.x = (w - title.width()) / 2f;
		title.y = 2 + (topRegion - title.height()) / 2f;

		align(title);

        placeTorch(title.x + 9, title.y + 46);
        placeTorch(title.x + title.width - 10, title.y + 46);

		Image signs = new Image( BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON_SIGNS ) ) {
			private float time = 0;
			@Override
			public void update() {
				super.update();
				am = Math.max(0f, (float)Math.sin( time += Game.elapsed ));
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
		add( signs );

        final Chrome.Type GREY_TR = Chrome.Type.GREY_BUTTON_TR;

        DiscordButton btnDiscord = new DiscordButton();
        btnDiscord.setPos(5, 5);
        btnDiscord.updateSize();
        add(btnDiscord);

        ReportBugButton btnBug = new ReportBugButton();
        btnBug.setPos(5, h - 5 - 16);
        add(btnBug);

        StyledButton btnPlay = new StyledButton(GREY_TR, Messages.get(this, "enter")) {
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

        StyledButton btnSupport = new SupportButton(GREY_TR, "Dungeon Editor");
        add(btnSupport);

		StyledButton btnRankings = new StyledButton(GREY_TR,Messages.get(this, "rankings")){
			@Override
			protected void onClick() {
				SandboxPixelDungeon.switchNoFade( RankingsScene.class );
			}
		};
		btnRankings.icon(Icons.get(Icons.RANKINGS));
		add(btnRankings);
		Dungeon.daily = Dungeon.dailyReplay = false;

//		StyledButton btnBadges = new StyledButton(GREY_TR, Messages.get(this, "badges")){
//			@Override
//			protected void onClick() {
//				SandboxPixelDungeon.switchNoFade( BadgesScene.class );
//			}
//		};
//		btnBadges.icon(Icons.get(Icons.BADGES));
//		add(btnBadges);

		StyledButton btnDiscover = new StyledButton(GREY_TR, Messages.get(this, "discover")){
			@Override
			protected void onClick() {
				Game.scene().addToFront(new ServerDungeonList.WndServerDungeonList());
			}
		};
		btnDiscover.icon(Icons.get(Icons.DOWNLOAD));
		add(btnDiscover);

		StyledButton btnNews = new NewsButton(GREY_TR, Messages.get(this, "help"));
		btnNews.icon(Icons.get(Icons.NEWS));
		add(btnNews);

		StyledButton btnChanges = new ChangesButton(GREY_TR, Messages.get(this, "changes"));
		btnChanges.icon(Icons.get(Icons.CHANGES));
		add(btnChanges);

		StyledButton btnSettings = new SettingsButton(GREY_TR, Messages.get(this, "settings"));
		add(btnSettings);

		StyledButton btnAbout = new StyledButton(GREY_TR, Messages.get(this, "about")){
			@Override
			protected void onClick() {
				SandboxPixelDungeon.switchScene( AboutScene.class );
			}
		};
		btnAbout.icon(Icons.get(Icons.SUPPORT_EMERALD));
		add(btnAbout);
		
		final int BTN_HEIGHT = 20;
		int GAP = (int)(h - topRegion - (landscape() ? 3 : 4)*BTN_HEIGHT)/3;
		GAP /= landscape() ? 3 : 5;
		GAP = Math.max(GAP, 2);

		if (landscape()) {
			btnPlay.setRect(title.x-50, topRegion+GAP, ((title.width()+100)/2)-1, BTN_HEIGHT);
			align(btnPlay);
			btnSupport.setRect(btnPlay.right()+2, btnPlay.top(), btnPlay.width(), BTN_HEIGHT);
			btnDiscover.setRect(btnPlay.left(), btnPlay.bottom()+ GAP, (btnPlay.width()*.67f)-1, BTN_HEIGHT);
			btnRankings.setRect(btnDiscover.left(), btnDiscover.bottom()+GAP, btnDiscover.width(), BTN_HEIGHT);
			btnNews.setRect(btnDiscover.right()+2, btnDiscover.top(), btnDiscover.width(), BTN_HEIGHT);
			btnChanges.setRect(btnNews.left(), btnNews.bottom() + GAP, btnDiscover.width(), BTN_HEIGHT);
			btnSettings.setRect(btnNews.right()+2, btnNews.top(), btnDiscover.width(), BTN_HEIGHT);
			btnAbout.setRect(btnSettings.left(), btnSettings.bottom() + GAP, btnDiscover.width(), BTN_HEIGHT);
		} else {
			btnPlay.setRect(title.x, topRegion+GAP, title.width(), BTN_HEIGHT);
			align(btnPlay);
			btnSupport.setRect(btnPlay.left(), btnPlay.bottom()+ GAP, btnPlay.width(), BTN_HEIGHT);
			btnDiscover.setRect(btnPlay.left(), btnSupport.bottom()+ GAP, (btnPlay.width()/2)-1, BTN_HEIGHT);
			btnRankings.setRect(btnDiscover.right()+2, btnDiscover.top(), btnDiscover.width(), BTN_HEIGHT);
			btnNews.setRect(btnDiscover.left(), btnDiscover.bottom()+ GAP, btnDiscover.width(), BTN_HEIGHT);
			btnChanges.setRect(btnNews.right()+2, btnNews.top(), btnNews.width(), BTN_HEIGHT);
			btnSettings.setRect(btnNews.left(), btnNews.bottom()+GAP, btnDiscover.width(), BTN_HEIGHT);
			btnAbout.setRect(btnSettings.right()+2, btnSettings.top(), btnSettings.width(), BTN_HEIGHT);
		}

		BitmapText version = new BitmapText( "v" + Game.version, pixelFont);
		version.measure();
		version.hardlight( 0x888888 );
		version.x = w - version.width() - 4;
		version.y = h - version.height() - 2;
		add( version );

		if (DeviceCompat.isDesktop()) {
			ExitButton btnExit = new ExitButton();
			btnExit.setPos( w - btnExit.width(), 0 );
			add( btnExit );
		}

		fadeIn();
	}
	
	private void placeTorch( float x, float y ) {
		Fireball fb = new Fireball();
		fb.setColor(Window.GOLD);
		fb.setPos( x, y );
		add( fb );
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
			LuaClassGenerator.initStatic();
//            SandboxPixelDungeon.platform.openURI("https://docs.google.com/document/d/1LEx8uZYdv04ndrITJeUttdphXp_hkJ5WDdysWNQTXyY");
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

				SandboxPixelDungeon.scene().addToFront( new WndOptions(
						Icons.get(Icons.CHANGES),
						update.versionName == null ? Messages.get(this,"title") : Messages.get(this,"versioned_title", update.versionName),
						update.desc == null ? Messages.get(this,"desc") : update.desc,
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
			if (Messages.lang().status() == Languages.Status.UNFINISHED){
				icon(Icons.get(Icons.LANGS));
				icon.hardlight(1.5f, 0, 0);
			} else {
				icon(Icons.get(Icons.PREFS));
			}
		}

		@Override
		public void update() {
			super.update();

			if (Messages.lang().status() == Languages.Status.UNFINISHED){
				textColor(ColorMath.interpolate( 0xFFFFFF, CharSprite.NEGATIVE, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
			}
		}

		@Override
		protected void onClick() {
			if (Messages.lang().status() == Languages.Status.UNFINISHED){
				WndSettings.last_index = 4;
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
				} else Game.scene().addToFront(new WndSelectDungeon(allInfos, true));
			}
        }
    }

	public static class ReportBugButton extends Button {
		private Image image;
		private BitmapText text;
		@Override
		protected void createChildren(Object... params) {
			super.createChildren(params);

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
			UploadDungeon.showUploadWindow(ServerCommunication.UploadType.REPORT_BUG, null);
		}
	}

    public static class DiscordButton extends Button {

        private Image image;
        private BitmapText text;

        private PointF scale;
        private boolean increasing;

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

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
    }
}