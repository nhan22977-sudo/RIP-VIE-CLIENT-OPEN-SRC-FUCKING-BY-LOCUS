package de.javagl.jgltf.model.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

class ErrorReportingSettableBeanProperty extends SettableBeanProperty {
   private static final long serialVersionUID = 7398743799397469737L;
   private final SettableBeanProperty delegate;
   private final Consumer<? super JsonError> jsonErrorConsumer;

   ErrorReportingSettableBeanProperty(SettableBeanProperty delegate, Consumer<? super JsonError> jsonErrorConsumer) {
      super(delegate);
      this.delegate = delegate;
      this.jsonErrorConsumer = jsonErrorConsumer;
   }

   public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
      return new ErrorReportingSettableBeanProperty(this.delegate.withValueDeserializer(deser), this.jsonErrorConsumer);
   }

   public SettableBeanProperty withName(PropertyName newName) {
      return new ErrorReportingSettableBeanProperty(this.delegate.withName(newName), this.jsonErrorConsumer);
   }

   public Object setAndReturn(Object instance, Object value) throws IOException {
      return this.delegate.setAndReturn(instance, value);
   }

   public void set(Object instance, Object value) throws IOException {
      this.delegate.set(instance, value);
   }

   public AnnotatedMember getMember() {
      return this.delegate.getMember();
   }

   public <A extends Annotation> A getAnnotation(Class<A> acls) {
      return (A)this.delegate.getAnnotation(acls);
   }

   public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
      return this.delegate.deserializeSetAndReturn(p, ctxt, instance);
   }

   public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
      try {
         this.delegate.deserializeAndSet(p, ctxt, instance);
      } catch (Exception e) {
         if (this.jsonErrorConsumer != null) {
            this.jsonErrorConsumer.accept(new JsonError(e.getMessage(), p.getParsingContext(), e));
         }
      }
   }

   public SettableBeanProperty withNullProvider(NullValueProvider nva) {
      return new ErrorReportingSettableBeanProperty(this.delegate.withNullProvider(nva), this.jsonErrorConsumer);
   }
}
