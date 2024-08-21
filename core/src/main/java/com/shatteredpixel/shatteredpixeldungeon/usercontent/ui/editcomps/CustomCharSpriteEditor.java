/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.editcomps;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.blueprints.CustomCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.CustomObjSelector;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.TabResourceFiles;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.WndSelectResourceFile;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomCharSpriteEditor extends CustomObjectEditor<CustomCharSprite> {

	private AnimationView[] animViews;

	protected CustomObjSelector<String> spritePath;

	public CustomCharSpriteEditor(Runnable onUpdateObj, CustomCharSprite obj) {
		super(onUpdateObj, obj);
	}

	@Override
	protected void createChildren(CustomCharSprite obj) {
		super.createChildren(obj);

		spritePath = new CustomObjSelector<>("Sprite Path, ", new CustomObjSelector.Selector<String>() {

			@Override
			public String getCurrentValue() {
				return obj.getResourcePath();
			}

			@Override
			public void onSelect(String path) {
				obj.setResourcePath(path);
				updateObj();
				obj.reloadSprite();
			}

			@Override
			public void onItemSlotClick() {
				if (getCurrentValue() != null) {
					TabResourceFiles.viewResource(getCurrentValue(), CustomObject.getResourceFile(getCurrentValue(), true));
				}
			}

			@Override
			public void onChangeClick() {
				DungeonScene.show(new WndSelectResourceFile() {
					@Override
					protected boolean acceptExtension(String extension) {
						return ResourcePath.isImage(extension);
					}

					@Override
					protected void onSelect(Map.Entry<String, FileHandle> path) {
						spritePath.setValue(path.getKey());
					}
				});
			}
		});
		spritePath.enableChanging(true);
		spritePath.enableDetaching(true);
		add(spritePath);

		rectComps = new Component[] {
				luaScriptPath, spritePath
		};

		CharSprite sprite = obj.getCharSprite(this::updateObj);
		LinkedHashMap<String, MovieClip.Animation> anims = sprite.getAnimations();

		animViews = new AnimationView[anims.size()];

		for (int i = 0; i < animViews.length; i++) {
			animViews[i] = new AnimationView();
			add(animViews[i]);
		}

	}

	@Override
	protected void layout() {
		super.layout();
		layoutCompsInRectangles(6, animViews);
	}

	@Override
	public void updateObj() {
		CharSprite sprite = obj.getCharSprite(this::updateObj);

		int i = 0;
		for (Map.Entry<String, MovieClip.Animation> anim : sprite.getAnimations().entrySet()) {
			animViews[i++].set(anim.getKey(), anim.getValue(), sprite.texture);
		}

		super.updateObj();
	}



	private static class AnimationView extends Component {
		//Animation with background
		//name of the animaton
		//pause, +-1 frame

		private static final float ZOOM = 3f;

		private MovieClip.Animation anim;

		private ColorBlock bg;
		private AnimationPlayer player;
		private RenderedTextBlock title;

		private Button pauseBtn;

		public AnimationView() {
			bg = new ColorBlock(1, 1, InventorySlot.NORMAL);
			add(bg);

			title = PixelScene.renderTextBlock(8);
			add(title);

			player = new AnimationPlayer();
			player.scale.set(ZOOM);
			add(player);

			pauseBtn = new Button() {

				@Override
				protected void onClick() {
					player.paused = !player.paused;
				}

				@Override
				protected void onRightClick() {
					player.goToNextFrame();
				}

				@Override
				protected void onMiddleClick() {
					player.goToPrevFrame();
				}

				@Override
				protected void onPointerDown() {
					bg.brightness( 1.5f );
					Sample.INSTANCE.play( Assets.Sounds.CLICK, 0.7f, 0.7f, 1.2f );
				}

				@Override
				protected void onPointerUp() {
					bg.brightness( 1.0f );
				}
			};
			add(pauseBtn);
		}

		@Override
		protected void layout() {
			title.maxWidth((int) width);

			if (anim != null) {
				//assumes all frames have the same size
				player.width = (player.texture.width * anim.frames[0].width());
				player.height = (player.texture.height * anim.frames[0].height());
			}

			bg.size(player.width(), player.height());

			title.setPos(x + (width - title.width()) * 0.5f, y + 1);
			player.x = bg.x = x + (width - player.width()) * 0.5f;

			player.y = bg.y = title.bottom() + 3;
			height = player.y + player.height() - y;

			pauseBtn.setRect(bg.x, bg.y, bg.width(), bg.height());
		}

		private float requiredWidth() {
			title.maxWidth(Integer.MAX_VALUE);
			return title.width() + 4;
		}

		public void set(String title, MovieClip.Animation animation, Object texture) {
			this.title.text(title);
			if (animation != null) {
				anim = animation.clone();
				player.texture(texture);
				player.paused = false;
				player.originalFrameLength = anim.frames.length;
				setVisible(true);
				if (!anim.looped) {
					//add 2 seconds pause before restarting the animation
					int addFrames = (int) (2 / this.anim.delay);
					RectF[] frames = new RectF[anim.frames.length + addFrames];
					int i = 0;
					for (; i < anim.frames.length; i++) {
						frames[i] = anim.frames[i];
					}
					for (; i < frames.length; i++) {
						frames[i] = anim.frames[anim.frames.length - 1];
					}
					anim.frames = frames;
					anim.looped = true;
				}
				player.play(anim);
			} else {
				anim = null;
				player.paused = true;
				setVisible(false);
			}
		}

		private static class AnimationPlayer extends MovieClip {

			public int originalFrameLength;

			public void goToNextFrame() {
				curFrame++;
				curFrame = curFrame % originalFrameLength;
				frame(curAnim.frames[curFrame]);
			}

			public void goToPrevFrame() {
				curFrame--;
				if (curFrame < 0) curFrame = originalFrameLength - 1;
				else if (curFrame >= originalFrameLength) curFrame = originalFrameLength - 2;
				frame(curAnim.frames[curFrame]);
			}
		}

	}

}