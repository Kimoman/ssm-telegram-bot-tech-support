package com.study.botssm.botssmstudy.ssmconfig;

import com.study.botssm.botssmstudy.bot.TechBot;
import com.study.botssm.botssmstudy.database.model.TemporaryMessageStatus;
import com.study.botssm.botssmstudy.database.repository.SMRepository;
import com.study.botssm.botssmstudy.database.service.AppealService;
import com.study.botssm.botssmstudy.database.service.TemporaryMessageService;
import com.study.botssm.botssmstudy.sender.Keyboards;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.mongodb.MongoDbPersistingStateMachineInterceptor;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;
import org.springframework.util.NumberUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.function.Predicate;

@Configuration
@EnableStateMachineFactory
class Config extends StateMachineConfigurerAdapter<States, String> {

    private static final Logger logger
            = LoggerFactory.getLogger(StateMachineConfigurerAdapter.class);

    private final Predicate<StateContext<States, String>> predicateWriteAppeal =
            context -> context.getSource().getId().equals(States.WRITE_APPEAL) &&
                    context.getStage().equals(StateContext.Stage.EVENT_NOT_ACCEPTED);

    private final Predicate<StateContext<States, String>> predicateWriteAnswer =
            context -> context.getSource().getId().equals(States.WRITE_ANSWER) &&
                    context.getStage().equals(StateContext.Stage.EVENT_NOT_ACCEPTED);

    private final Predicate<StateContext<States, String>> predicateFindAppeal =
            context -> context.getSource().getId().equals(States.FIND_APPEAL) &&
                    context.getStage().equals(StateContext.Stage.EVENT_NOT_ACCEPTED);


    @Autowired
    private SMRepository mongoDbStateMachineRepository;

    @Autowired
    private TechBot bot;

    @Autowired
    private TemporaryMessageService temporaryMessageService;

    @Autowired
    private AppealService appealService;

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, String> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(false)
                .listener(listener())
                .and()
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister());
    }

    private StateMachineListener<States, String> listener() {

        return new StateMachineListenerAdapter<States, String>() {
            @Override
            public void stateContext(StateContext<States, String> stateContext) {
                Optional.ofNullable(stateContext.getSource()).map(State::getId).ifPresent(
                        state -> {
                            if (predicateWriteAppeal.test(stateContext)) {
                                logger.info(stateContext.getMessage().getPayload());
                                temporaryMessageService.saveCommonMessage(
                                        Long.parseLong(stateContext.getStateMachine().getId()),
                                        (String) stateContext
                                                .getMessageHeaders()
                                                .getOrDefault("text", "")
                                );
                            }

                            if (predicateWriteAnswer.test(stateContext)) {
                                logger.info(stateContext.getMessage().getPayload());
                                temporaryMessageService.saveAnswerMessage(
                                        Long.parseLong(stateContext.getStateMachine().getId()),
                                        (String) stateContext
                                                .getMessageHeaders()
                                                .getOrDefault("text", "")
                                );
                            }

                            if (predicateFindAppeal.test(stateContext)) {
                                logger.info(stateContext.getMessage().getPayload());
                                stateContext.getExtendedState()
                                        .getVariables()
                                        .put("numAppeal", stateContext.getMessage().getPayload());
                            }
                        }
                );
            }
        };
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, String> states) throws Exception {
        states
                .withStates()
                .initial(States.START_OFF, sendKeyboard(Keyboards.MAIN_MENU))
                .state(States.SELECTED_SUBSCRIBER, sendKeyboard(Keyboards.SUBSCRIBER))
                .state(States.WRITE_APPEAL, sendKeyboard(Keyboards.END_OF_APPEAL), actionFormAppeal())
                .state(States.END_OF_APPEAL, actionMyAppeals())
                .state(States.SELECTED_SUPPORT, actionNewAppeals())
                .state(States.FIND_APPEAL, sendKeyboard(Keyboards.FIND_APPEAL))
                .state(States.WRITE_ANSWER, sendKeyboard(Keyboards.END_OF_ANSWER), actionFormAnswer());
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, String> transitions) throws Exception {
        transitions
                .withExternal()
                .source(States.START_OFF)
                .target(States.SELECTED_SUBSCRIBER)
                .event("/subscriber")
                .and()
                .withExternal()
                .source(States.START_OFF)
                .target(States.SELECTED_SUPPORT)
                .event("/support")
                .and()
                .withExternal()
                .source(States.SELECTED_SUBSCRIBER)
                .target(States.WRITE_APPEAL)
                .event("/wmsg")
                .and()
                .withExternal()
                .source(States.WRITE_APPEAL)
                .target(States.END_OF_APPEAL)
                .event("/endmsg")
                .and()
                .withExternal()
                .source(States.END_OF_APPEAL)
                .target(States.END_OF_APPEAL)
                .event("/endmsg")
                .and()
                .withExternal()
                .source(States.END_OF_APPEAL)
                .target(States.START_OFF)
                .event("/start")
                .action(sendKeyboard(Keyboards.MAIN_MENU))
                .and()
                .withExternal()
                .source(States.SELECTED_SUPPORT)
                .target(States.SELECTED_SUPPORT)
                .event("/support")
                .and()
                .withExternal()
                .source(States.SELECTED_SUPPORT)
                .target(States.FIND_APPEAL)
                .event("/wanswer")
                .and()
                .withExternal()
                .source(States.FIND_APPEAL)
                .target(States.WRITE_ANSWER)
                .event("/fapp")
                .guard(guardToGoToWriteAnswer())
                .and()
                .withExternal()
                .source(States.WRITE_ANSWER)
                .target(States.START_OFF)
                .event("/endanswer")
                .action(sendKeyboard(Keyboards.MAIN_MENU));
    }

    @Bean
    public StateMachineRuntimePersister<States, String, String> stateMachineRuntimePersister() {
        return new MongoDbPersistingStateMachineInterceptor<>(mongoDbStateMachineRepository);
    }

    @Bean
    public StateMachineService<States, String> stateMachineService(
            StateMachineFactory<States, String> stateMachineFactory,
            StateMachineRuntimePersister<States, String, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<States, String>(stateMachineFactory, stateMachineRuntimePersister);
    }

    @Bean
    public Action<States, String> actionMyAppeals() {

        return new Action<States, String>() {
            @Override
            public void execute(StateContext<States, String> context) {

                SendMessage message
                        = Keyboards.TRACKING_APPEAL
                        .getKeyboard(Long.parseLong(context.getStateMachine().getId()))
                        .setText(appealService
                                .getMyAppeals(Long.parseLong(context.getStateMachine().getId())));

                try {
                    bot.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Bean
    public Action<States, String> actionNewAppeals() {

        return new Action<States, String>() {
            @Override
            public void execute(StateContext<States, String> context) {

                SendMessage message
                        = Keyboards.SUPPORT
                        .getKeyboard(Long.parseLong(context.getStateMachine().getId()))
                        .setText(appealService.getNewAppeals());

                try {
                    bot.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Bean
    public Action<States, String> actionFormAppeal() {

        return new Action<States, String>() {
            @Override
            public void execute(StateContext<States, String> context) {
                temporaryMessageService.textOfMessages(
                        Long.parseLong(context.getStateMachine().getId()),
                        TemporaryMessageStatus.APPEAL)
                        .ifPresent(str -> {
                            appealService.createAppeal(Long.parseLong(context.getStateMachine().getId()), str);
                            temporaryMessageService.deleteMessage(
                                    Long.parseLong(context.getStateMachine().getId()),
                                    TemporaryMessageStatus.APPEAL);
                        });
            }
        };
    }

    @Bean
    public Action<States, String> actionFormAnswer() {

        return new Action<States, String>() {
            @Override
            public void execute(StateContext<States, String> context) {
                temporaryMessageService.textOfMessages(
                        Long.parseLong(context.getStateMachine().getId()),
                        TemporaryMessageStatus.ANSWER)
                        .ifPresent(str -> {
                            appealService.updateAppeal(Long.parseLong((String) context.getExtendedState().getVariables().get("numAppeal")), str);
                            temporaryMessageService.deleteMessage(
                                    Long.parseLong(context.getStateMachine().getId()),
                                    TemporaryMessageStatus.ANSWER);
                        });
            }
        };
    }


    public Action<States, String> sendKeyboard(Keyboards keyboard) {

        return context -> {
            try {
                bot.execute(keyboard.getKeyboard(Long.parseLong(context.getStateMachine().getId())));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    public Guard<States, String> guardToGoToWriteAnswer() {

        return new Guard<States, String>() {
            @Override
            public boolean evaluate(StateContext<States, String> context) {
                Object numAppeal = context.getExtendedState().getVariables().get("numAppeal");
                Boolean isPresent
                        = Optional.ofNullable(numAppeal)
                        .flatMap(o -> appealService.getAppeal(NumberUtils.parseNumber((String) o, Long.class)))
                        .isPresent();
                return isPresent;
            }
        };
    }
}
