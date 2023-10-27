package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.Quest;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.FloatFunction;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class QuestSpinner extends Spinner {


    public QuestSpinner(Quest quest, FloatFunction<Float> inputFiledWith) {
        super(new QuestSpinnerModel(quest) {
            @Override
            public float getInputFieldWith(float height) {
                return inputFiledWith.get(height);
            }
        }, " " + Messages.get(QuestSpinner.class, "label"), 10);
        setButtonWidth(12);
    }

    private static class QuestSpinnerModel extends SpinnerTextIconModel {

        private final Quest quest;

        public QuestSpinnerModel(Quest quest) {
            super(true, quest.type() + 3, (Object[]) createData(quest));
            this.quest = quest;
        }

        @Override
        protected Image getIcon(Object value) {
            int index = getCurrentIndex();
            quest.setType(index-3);
            Image icon = quest.getIcon();
            if (icon == null) {
                if (index == 0) return null;
                if (index == 1) return null;
                if (index == 2) return new ItemSprite(ItemSpriteSheet.SOMETHING);
            }
            return icon;
        }

        @Override
        protected String getAsString(Object value) {
            return " "+Messages.get(QuestSpinner.class, (String) value);
        }

        private static String[] createData(Quest quest) {
            String[] ret = new String[quest.getNumQuests() + 3];
            ret[0] = "based_on_depth";
            ret[1] = "none";
            ret[2] = "random";
            for (int i = 3; i < ret.length; i++) {
                ret[i] = quest.getMessageString(i - 3);
            }
            return ret;
        }

        @Override
        public Component createInputField(int fontSize) {
            inputField = new ShowField(Chrome.get(getChromeType()), 8);
            return inputField;
        }
    }

}