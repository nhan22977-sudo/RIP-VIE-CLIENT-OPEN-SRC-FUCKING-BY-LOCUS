package de.javagl.jgltf.model.io;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class JacksonUtils {
   private static final Logger logger = Logger.getLogger(JacksonUtils.class.getName());
   private static final Consumer<JsonError> LOG_JSON_ERROR_CONSUMER = new Consumer<JsonError>() {
      public void accept(JsonError jsonError) {
         JacksonUtils.logger.warning("Error: " + jsonError.getMessage() + ", JSON path " + jsonError.getJsonPathString());
      }
   };

   private static DeserializationProblemHandler createDeserializationProblemHandler(final Consumer<? super JsonError> jsonErrorConsumer) {
      return new DeserializationProblemHandler() {
         public boolean handleUnknownProperty(
            DeserializationContext ctxt, JsonParser jp, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName
         ) throws IOException, JsonProcessingException {
            if (jsonErrorConsumer != null) {
               jsonErrorConsumer.accept(new JsonError("Unknown property: " + propertyName, jp.getParsingContext(), null));
            }

            return super.handleUnknownProperty(ctxt, jp, deserializer, beanOrClass, propertyName);
         }
      };
   }

   private static BeanDeserializerModifier createErrorHandlingBeanDeserializerModifier(final Consumer<? super JsonError> jsonErrorConsumer) {
      return new BeanDeserializerModifier() {
         public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
            Iterator<SettableBeanProperty> propertiesIterator = builder.getProperties();

            while (propertiesIterator.hasNext()) {
               SettableBeanProperty property = propertiesIterator.next();
               SettableBeanProperty wrappedProperty = new ErrorReportingSettableBeanProperty(property, jsonErrorConsumer);
               builder.addOrReplaceProperty(wrappedProperty, true);
            }

            return builder;
         }
      };
   }

   public static Consumer<JsonError> loggingJsonErrorConsumer() {
      return LOG_JSON_ERROR_CONSUMER;
   }

   public static ObjectMapper createObjectMapper() {
      ObjectMapper objectMapper = new ObjectMapper();
      configure(objectMapper, loggingJsonErrorConsumer());
      return objectMapper;
   }

   public static ObjectMapper createObjectMapper(Consumer<? super JsonError> jsonErrorConsumer) {
      ObjectMapper objectMapper = new ObjectMapper();
      configure(objectMapper, jsonErrorConsumer);
      return objectMapper;
   }

   public static void configure(ObjectMapper objectMapper, final Consumer<? super JsonError> jsonErrorConsumer) {
      objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.addHandler(createDeserializationProblemHandler(jsonErrorConsumer));
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      objectMapper.setPropertyNamingStrategy(new KeywordPropertyNamingStrategy());
      objectMapper.registerModule(new SimpleModule() {
         private static final long serialVersionUID = 1L;

         public void setupModule(SetupContext context) {
            super.setupModule(context);
            context.addBeanDeserializerModifier(JacksonUtils.createErrorHandlingBeanDeserializerModifier(jsonErrorConsumer));
         }
      });
   }

   public static JsonNode readJson(ByteBuffer jsonData) throws IOException {
      ObjectMapper objectMapper = createObjectMapper();

      try (InputStream jsonInputStream = Buffers.createByteBufferInputStream(jsonData)) {
         return objectMapper.readTree(jsonInputStream);
      }
   }

   private JacksonUtils() {
   }
}
