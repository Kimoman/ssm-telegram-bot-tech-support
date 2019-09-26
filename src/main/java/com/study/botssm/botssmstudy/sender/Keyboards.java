package com.study.botssm.botssmstudy.sender;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public enum Keyboards {
    MAIN_MENU {
        @Override
        public SendMessage getKeyboard(Long chatId) {
            return InlineKeyboardBuilder.create(chatId)
                    .setText("Main menu :")
                    .row()
                    .button("Support", "/support")
                    .button("Subscriber", "/subscriber")
                    .endRow()
                    .build();
        }
    },
    SUBSCRIBER {
        @Override
        public SendMessage getKeyboard(Long chatId) {
            return InlineKeyboardBuilder.create(chatId)
                    .setText("Subscriber :")
                    .row()
                    .button("Write messages", "/wmsg")
                    .endRow()
                    .build();
        }
    },
    SUPPORT {
        @Override
        public SendMessage getKeyboard(Long chatId) {
            return InlineKeyboardBuilder.create(chatId)
                    .setText("Support :")
                    .row()
                    .button("New appeals", "/support")
                    .button("To write an answer", "/wanswer")
                    .endRow()
                    .build();
        }
    },
    END_OF_APPEAL {
        @Override
        public SendMessage getKeyboard(Long chatId) {
            return InlineKeyboardBuilder.create(chatId)
                    .setText("Subscriber :")
                    .row()
                    .button("Finish writing the appeal", "/endmsg")
                    .endRow()
                    .build();
        }
    },
    END_OF_ANSWER {
        @Override
        public SendMessage getKeyboard(Long chatId) {
            return InlineKeyboardBuilder.create(chatId)
                    .setText("Support :")
                    .row()
                    .button("Finish writing the answer", "/endanswer")
                    .endRow()
                    .build();
        }
    },
    TRACKING_APPEAL {
        @Override
        public SendMessage getKeyboard(Long chatId) {
            return InlineKeyboardBuilder.create(chatId)
                    .row()
                    .button("Main menu", "/start")
                    .button("My appeals", "/endmsg")
                    .endRow()
                    .build();
        }
    },
    FIND_APPEAL {
        @Override
        public SendMessage getKeyboard(Long chatId) {
            return InlineKeyboardBuilder.create(chatId)
                    .setText("Find appeal")
                    .row()
                    .button("Find", "/fapp")
                    .endRow()
                    .build();
        }
    };

    public abstract SendMessage getKeyboard(Long chatId);
}
