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

  private final ModerationModel moderationModel;

  public ModeratingInputMessageGuardrail(ModerationModel moderationModel) {
    this.moderationModel = moderationModel;
  }

  @Override
  public InputGuardrailResult validate(UserMessage userMessage) {
    Response<Moderation> response = moderationModel.moderate(userMessage);

    if (response.content().flagged()) {
      LOG.error("Moderation: User message has been flagged " + response);
      return fatal("User message has been flagged", new ModerationException("User message has been flagged", response.content()));
    } else {
      LOG.info("Moderation: User message is ok");
      return success();
    }
  }
}
