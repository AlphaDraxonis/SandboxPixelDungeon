package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

public abstract class AbstractWndChooseSubclass extends Window {

	private static final int WIDTH		= 130;
	private static final float GAP		= 2;

	public AbstractWndChooseSubclass(Component titlebar, String msg, String cancelLabel, final HeroClass heroClass, final TengusMask tome ) {

			super();

			if (titlebar instanceof RenderedTextBlock) {
				((RenderedTextBlock) titlebar).maxWidth(WIDTH);
				titlebar.setPos((WIDTH - titlebar.width()) * 0.5f, 0);
			} else titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			float pos = titlebar.bottom() + GAP;

			if (msg != null) {
				RenderedTextBlock message = PixelScene.renderTextBlock(6);
				message.text(Messages.get(this, "message"), WIDTH);
				message.setPos(titlebar.left(), pos);
				add(message);
				pos = message.bottom() + 3 * GAP;
			} else pos += GAP * 2;

			for (HeroSubClass subCls : heroClass.subClasses()){

				StyledButton btnCls = createHeroSubClassButton(tome, subCls);
				if (btnCls == null) continue;

				btnCls.leftJustify = true;
				btnCls.multiline = true;
				btnCls.setSize(WIDTH-20, btnCls.reqHeight()+2);
				btnCls.setRect( 0, pos, WIDTH-20, btnCls.reqHeight()+2);
				add( btnCls );

				IconButton clsInfo = new IconButton(Icons.get(Icons.INFO)){
					@Override
					protected void onClick() {
						if (Game.scene() instanceof GameScene) GameScene.show(new WndInfoSubclass(heroClass, subCls));
						else EditorScene.show(new WndInfoSubclass(heroClass, subCls));
					}
				};
				clsInfo.setRect(WIDTH-20, btnCls.top() + (btnCls.height()-20)/2, 20, 20);
				add(clsInfo);

				pos = btnCls.bottom() + GAP;
			}

			if (cancelLabel != null) {
				RedButton btnCancel = new RedButton(cancelLabel) {
					@Override
					protected void onClick() {
						hide();
					}
				};
				btnCancel.setRect(0, pos, WIDTH, 18);
				add(btnCancel);
				pos = btnCancel.bottom();
			}
			
			resize(WIDTH, (int) pos);
		}

		protected abstract StyledButton createHeroSubClassButton(final TengusMask tome, HeroSubClass subCls);

	}