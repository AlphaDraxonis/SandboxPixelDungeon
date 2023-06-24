package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.mobs;

import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.Quest;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.FloatFunction;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
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
            super(true, quest.type() + 2, (Object[]) createData(quest));
            this.quest = quest;
        }

        @Override
        protected Image getIcon(Object value) {
            int index = getCurrentIndex();
            quest.setType(index-2);
            Image icon = quest.getIcon();
            if (icon == null) {
                if (index == 0) return null;
                if (index == 1) return new ItemSprite(ItemSpriteSheet.SOMETHING);
            }
            return icon;
        }

        @Override
        protected String getAsString(Object value) {
            return Messages.get(QuestSpinner.class, (String) value);
        }

        private static String[] createData(Quest quest) {
            String[] ret = new String[quest.getNumQuests() + 2];
            ret[0] = "none";
            ret[1] = "random";
            for (int i = 2; i < ret.length; i++) {
                ret[i] = quest.getMessageString(i - 2);
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