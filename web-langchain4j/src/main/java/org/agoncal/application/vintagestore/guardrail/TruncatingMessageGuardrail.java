package org.agoncal.application.vintagestore.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import org.jboss.logging.Logger;

public class TruncatingMessageGuardrail implements InputGuardrail {

  private static final Logger LOG = Logger.getLogger(TruncatingMessageGuardrail.class);
  private static final int MAX_LENGTH = 20;

  @Override
  public InputGuardrailResult validate(UserMessage userMessage) {
    String returnedMessage = userMessage.singleText().substring(0, MAX_LENGTH);
    LOG.info("Returned Message: " + returnedMessage);
    return successWith(returnedMessage);
  }
}
