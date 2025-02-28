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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeInfo;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.WndChanges;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.WndChangesTabbed;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_1_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_2_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_3_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_4_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_5_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_6_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_7_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_8_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_9_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v1_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v2_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v3_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

@NotAllowedInLua
public class ChangesScene extends PixelScene {
	
	public static int changesSelected = 0;
	public static boolean viewShatteredChanges = true;
	private static int otherChangesSelected = 0;

	private NinePatch rightPanel;
	private ScrollPane rightScroll;
	private IconTitle changeTitle;
	private RenderedTextBlock changeBody;

	private StyledButton[] shatteredButtons;
	private StyledButton[] sandboxButtons;

	@Override
	public void create() {
		super.create();

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
				new float[]{1, 1},
				false);

		int w = Camera.main.width;
		int h = Camera.main.height;

		IconTitle title = new IconTitle(Icons.CHANGES.get(), Messages.get(this, "title"));
		title.setSize(200, 0);
		title.setPos(
				(w - title.reqWidth()) / 2f,
				(20 - title.height()) / 2f
		);
		align(title);
		add(title);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

//		StyledButton switcher = new RedButton("Switch tzz") {
//			@Override
//			protected void onClick() {
//				super.onClick();
//				int temp = changesSelected;
//				changesSelected = otherChangesSelected;
//				otherChangesSelected = temp;
//				viewShatteredChanges = !viewShatteredChanges;
//				SandboxPixelDungeon.seamlessResetScene();
//			}
//		};
//		switcher.setRect(0,0, 40, 16);//tzz
//		add(switcher);

		NinePatch panel = Chrome.get(Chrome.Type.TOAST);

		int pw = 135 + panel.marginLeft() + panel.marginRight() - 2;
		int ph = h - 36;

		if (h >= PixelScene.MIN_HEIGHT_FULL && w >= 300) {
			panel.size( pw, ph );
			panel.x = (w - pw) / 2f - pw/2 - 1;
			panel.y = 20;

			rightPanel = Chrome.get(Chrome.Type.TOAST);
			rightPanel.size( pw, ph );
			rightPanel.x = (w - pw) / 2f + pw/2 + 1;
			rightPanel.y = 20;
			add(rightPanel);

			rightScroll = new ScrollPane(new Component());
			add(rightScroll);
			rightScroll.setRect(
					rightPanel.x + rightPanel.marginLeft(),
					rightPanel.y + rightPanel.marginTop()-1,
					rightPanel.innerWidth() + 2,
					rightPanel.innerHeight() + 2);
			rightScroll.scrollTo(0, 0);

			changeTitle = new IconTitle(Icons.get(Icons.CHANGES), Messages.get(this, "right_title"));
			changeTitle.setPos(0, 1);
			changeTitle.setSize(pw, 20);
			rightScroll.content().add(changeTitle);

			String body = Messages.get(this, "right_body");

			changeBody = PixelScene.renderTextBlock(body, 6);
			changeBody.maxWidth(pw - panel.marginHor());
			changeBody.setPos(0, changeTitle.bottom()+2);
			rightScroll.content().add(changeBody);

		} else {
			panel.size( pw, ph );
			panel.x = (w - pw) / 2f;
			panel.y = 20;
		}
		align( panel );
		add( panel );
		
		final ArrayList<ChangeInfo> changeInfos = new ArrayList<>();

		if (Messages.lang() != Languages.ENGLISH){
			ChangeInfo langWarn = new ChangeInfo("", true, Messages.get(this, "lang_warn"));
			langWarn.hardlight(CharSprite.WARNING);
			changeInfos.add(langWarn);
		}

		if (viewShatteredChanges) {
			switch (changesSelected) {
				case 0:
				default:
					v3_X_Changes.addAllChanges(changeInfos);
					break;
				case 1:
					v2_X_Changes.addAllChanges(changeInfos);
				break;
			case 2:v1_X_Changes.addAllChanges(changeInfos);
					break;
				case 3:
					v0_9_X_Changes.addAllChanges(changeInfos);
					break;
				case 4:
					v0_8_X_Changes.addAllChanges(changeInfos);
					break;
				case 5:
					v0_7_X_Changes.addAllChanges(changeInfos);
					break;
				case 6:
					v0_6_X_Changes.addAllChanges(changeInfos);
					break;
				case 7:
					v0_5_X_Changes.addAllChanges(changeInfos);
					v0_4_X_Changes.addAllChanges(changeInfos);
					v0_3_X_Changes.addAllChanges(changeInfos);
					v0_2_X_Changes.addAllChanges(changeInfos);
					v0_1_X_Changes.addAllChanges(changeInfos);
					break;
			}
		} else {
			switch (changesSelected) {//TODO
//				case 0:
//				default:
//					vSa1_1_X_Changes.addAllChanges(changeInfos);
//					break;
//				case 1:
//					vSa1_0_X_Changes.addAllChanges(changeInfos);
//					break;
//				case 2:
//					vSa0_9_X_Changes.addAllChanges(changeInfos);
//					break;
//				case 3:
//					vSa0_8_X_Changes.addAllChanges(changeInfos);
//					break;
//				case 4:
//					vSa0_7_X_Changes.addAllChanges(changeInfos);
//					break;
//				case 5:
//					vSa0_6_X_Changes.addAllChanges(changeInfos);
//					vSa0_5_X_Changes.addAllChanges(changeInfos);
//					vSa0_4_X_Changes.addAllChanges(changeInfos);
//					vSa0_3_X_Changes.addAllChanges(changeInfos);
//					vSa0_2_X_Changes.addAllChanges(changeInfos);
//					vSa0_1_X_Changes.addAllChanges(changeInfos);
//					break;
			}
		}

		ScrollPane list = new ScrollPane( new Component() ){

			@Override
			public void onClick(float x, float y) {
				for (ChangeInfo info : changeInfos){
					if (info.onClick( x, y )){
						return;
					}
				}
			}

		};
		add( list );

		Component content = list.content();
		content.clear();

		float posY = 0;
		float nextPosY = 0;
		boolean second = false;
		for (ChangeInfo info : changeInfos){
			if (info.major) {
				posY = nextPosY;
				second = false;
				info.setRect(0, posY, panel.innerWidth(), 0);
				content.add(info);
				posY = nextPosY = info.bottom();
			} else {
				if (!second){
					second = true;
					info.setRect(0, posY, panel.innerWidth()/2f, 0);
					content.add(info);
					nextPosY = info.bottom();
				} else {
					second = false;
					info.setRect(panel.innerWidth()/2f, posY, panel.innerWidth()/2f, 0);
					content.add(info);
					nextPosY = Math.max(info.bottom(), nextPosY);
					posY = nextPosY;
				}
			}
		}

		content.setSize( panel.innerWidth(), (int)Math.ceil(posY) );

		list.setRect(
				panel.x + panel.marginLeft(),
				panel.y + panel.marginTop() - 1,
				panel.innerWidth() + 2,
				panel.innerHeight() + 2);
		list.scrollTo(0, 0);

		//************************
		//****SHATTERED BUTTONS***
		//*********START**********

		StyledButton btn3_X = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "3.X", 8){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 0) {
					changesSelected = 0;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 0) btn3_X.textColor( 0xBBBBBB );
		btn3_X.setRect(list.left()-4f, list.bottom(), 19, changesSelected == 0 ? 19 : 15);
		addToBack(btn3_X);

		StyledButton btn2_X = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "2.X", 8){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 1) {
					changesSelected = 1;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 1) btn2_X.textColor( 0xBBBBBB );
		btn2_X.setRect(btn3_X.right()-2, list.bottom(), 19, changesSelected == 1 ? 19 : 15);
		addToBack(btn2_X);

		StyledButton btn1_X = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "1.X", 8){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 2) {
					changesSelected = 2;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 2) btn1_X.textColor( 0xBBBBBB );
		btn1_X.setRect(btn2_X.right()-2, list.bottom(), 19, changesSelected == 2 ? 19 : 15);
		addToBack(btn1_X);

		StyledButton btn0_9 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.9", 8){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 3) {
					changesSelected = 3;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 3) btn0_9.textColor( 0xBBBBBB );
		btn0_9.setRect(btn1_X.right()-2, list.bottom(), 19, changesSelected == 3 ? 19 : 15);
		addToBack(btn0_9);

		StyledButton btn0_8 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.8", 8){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 4) {
					changesSelected = 4;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 4) btn0_8.textColor( 0xBBBBBB );
		btn0_8.setRect(btn0_9.right()-2, list.bottom(), 19, changesSelected == 4 ? 19 : 15);
		addToBack(btn0_8);
		
		StyledButton btn0_7 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.7", 8){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 5) {
					changesSelected = 5;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 5) btn0_7.textColor( 0xBBBBBB );
		btn0_7.setRect(btn0_8.right()-2, btn0_8.top(), 19, changesSelected == 5 ? 19 : 15);
		addToBack(btn0_7);
		
		StyledButton btn0_6 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.6", 8){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 6) {
					changesSelected = 6;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 6) btn0_6.textColor( 0xBBBBBB );
		btn0_6.setRect(btn0_7.right()-2, btn0_8.top(), 19, changesSelected == 6 ? 19 : 15);
		addToBack(btn0_6);
		
		StyledButton btnOld = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.5-", 8){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 7) {
					changesSelected = 7;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 7) btnOld.textColor( 0xBBBBBB );
		btnOld.setRect(btn0_6.right()-2, btn0_8.top(), 22, changesSelected == 7 ? 19 : 15);
		addToBack(btnOld);

		shatteredButtons = new StyledButton[] {
				btn2_X, btn1_X, btn0_9, btn0_8, btn0_7, btn0_6, btnOld
		};

		//**********END***********
		//****SHATTERED BUTTONS***
		//************************

		//************************
		//*****SANDBOX BUTTONS****
		//*********START**********

		StyledButton btnSa1_1 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "1.1"){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 0) {
					changesSelected = 0;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 0) btnSa1_1.textColor( 0xBBBBBB );
		btnSa1_1.setRect(list.left()-4f, list.bottom(), 22, changesSelected == 0 ? 19 : 15);
		addToBack(btnSa1_1);

		StyledButton btnSa1_0 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "1.0"){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 1) {
					changesSelected = 1;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 1) btnSa1_0.textColor( 0xBBBBBB );
		btnSa1_0.setRect(btnSa1_1.right()+1, list.bottom(), 22, changesSelected == 1 ? 19 : 15);
		addToBack(btnSa1_0);

		StyledButton btnSa0_9 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.9"){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 2) {
					changesSelected = 2;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 2) btnSa0_9.textColor( 0xBBBBBB );
		btnSa0_9.setRect(btnSa1_0.right() + 1, list.bottom(), 22, changesSelected == 2 ? 19 : 15);
		addToBack(btnSa0_9);

		StyledButton btnSa0_8 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.8"){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 3) {
					changesSelected = 3;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 3) btnSa0_8.textColor( 0xBBBBBB );
		btnSa0_8.setRect(btnSa0_9.right() + 1, btnSa0_9.top(), 22, changesSelected == 3 ? 19 : 15);
		addToBack(btnSa0_8);

		StyledButton btnSa0_7 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.7"){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 4) {
					changesSelected = 4;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 4) btnSa0_7.textColor( 0xBBBBBB );
		btnSa0_7.setRect(btnSa0_8.right() + 1, btnSa0_9.top(), 22, changesSelected == 4 ? 19 : 15);
		addToBack(btnSa0_7);

		StyledButton btnSaOld = new StyledButton(Chrome.Type.GREY_BUTTON_TR,"0.6-1"){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != 5) {
					changesSelected = 5;
					SandboxPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != 5) btnSaOld.textColor( 0xBBBBBB );
		btnSaOld.setRect(btnSa0_7.right() + 1, btnSa0_9.top(), 26, changesSelected == 5 ? 19 : 15);
		addToBack(btnSaOld);

		sandboxButtons = new StyledButton[] {
				btnSa1_1, btnSa1_0, btnSa0_9, btnSa0_8, btnSa0_7, btnSaOld
		};

		//**********END***********
		//****SANDBOX BUTTONS*****
		//************************

		updateButtons();

		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );

		fadeIn();
	}

	private void updateButtons() {
		for (StyledButton btn : shatteredButtons) {
			btn.active = btn.visible = viewShatteredChanges;
		}
		for (StyledButton btn : sandboxButtons) {
			btn.active = btn.visible = !viewShatteredChanges;
		}
	}

	private void updateChangesText(Image icon, String title, String... messages){
		if (changeTitle != null){
			changeTitle.icon(icon);
			changeTitle.label(title);
			changeTitle.setPos(changeTitle.left(), changeTitle.top());

			String message = "";
			for (int i = 0; i < messages.length; i++){
				message += messages[i];
				if (i != messages.length-1){
					message += "\n\n";
				}
			}
			changeBody.text(message);
			rightScroll.content().setSize(rightScroll.width(), changeBody.bottom()+2);
			rightScroll.setSize(rightScroll.width(), rightScroll.height());
			rightScroll.scrollTo(0, 0);

		} else {
			if (messages.length == 1) {
				addToFront(new WndChanges(icon, title, messages[0]));
			} else {
				addToFront(new WndChangesTabbed(icon, title, messages));
			}
		}
	}

	public static void showChangeInfo(Image icon, String title, String... messages){
		Scene s = SandboxPixelDungeon.scene();
		if (s instanceof ChangesScene){
			((ChangesScene) s).updateChangesText(icon, title, messages);
			return;
		}
		if (messages.length == 1) {
			s.addToFront(new WndChanges(icon, title, messages[0]));
		} else {
			s.addToFront(new WndChangesTabbed(icon, title, messages));
		}
	}
	
	@Override
	protected void onBackPressed() {
		SandboxPixelDungeon.switchNoFade(TitleScene.class);
	}

}
