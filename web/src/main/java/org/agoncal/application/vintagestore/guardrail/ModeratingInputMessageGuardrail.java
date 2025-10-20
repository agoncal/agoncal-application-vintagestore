package org.agoncal.application.vintagestore.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.ModerationException;
import org.jboss.logging.Logger;

public class ModeratingInputMessageGuardrail implements InputGuardrail {

  private static final Logger LOG = Logger.getLogger(ModeratingInputMessageGuardrail.class);
  private static final String MODERATION_PROMPT = "I don't know why you are frustrated, but I will redirect you to a human assistant who can help you better. Please wait a moment...";

  private final ModerationModel moderationModel;

  public ModeratingInputMessageGuardrail(ModerationModel moderationModel) {
    this.moderationModel = moderationModel;
  }

  @Override
  public InputGuardrailResult validate(UserMessage userMessage) {
    LOG.info("Moderating message " + userMessage);

    Response<Moderation> response = moderationModel.moderate(userMessage);

    if (response.content().flagged()) {
      LOG.error("Moderation response " + response);
      throw new ModerationException(MODERATION_PROMPT, response.content());
//      return failure(MODERATION_PROMPT, new ModerationException(MODERATION_PROMPT, response.content()));
    } else {
      return success();
    }
  }
}
