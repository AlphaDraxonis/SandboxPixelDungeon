package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.editor.quests.Quest;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.FloatFunction;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class QuestSpinner extends StyledSpinner {


    public QuestSpinner(Quest quest, FloatFunction<Float> inputFieldWith) {
        super(new QuestSpinnerModel(quest) {
            @Override
            public float getInputFieldWidth(float height) {
                return inputFieldWith.get(height);
            }
        }, Messages.get(QuestSpinner.class, "label"));
        setButtonWidth(9f);
    }

    private static class QuestSpinnerModel extends SpinnerTextIconModel {

        private final Quest quest;

        public QuestSpinnerModel(Quest quest) {
            super(true, quest.type() + 3, (Object[]) createData(quest));
            this.quest = quest;
        }

        @Override
        protected Image displayIcon(Object value) {
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
        protected String displayString(Object value) {
            return Messages.get(QuestSpinner.class, (String) value);
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
            return super.createInputField(Math.max(6, fontSize - 2));
        }
    }

}