/**
 * Copyright 2018 The original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dekorate.thorntail;

import io.dekorate.Generator;
import io.dekorate.Session;
import io.dekorate.doc.Description;
import io.dekorate.processor.AbstractAnnotationProcessor;
import io.dekorate.thorntail.configurator.ThorntailPrometheusAgentConfigurator;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@Description("Detects JAX-RS and servlet annotations and registers the http port.")
@SupportedAnnotationTypes({"javax.ws.rs.ApplicationPath", "javax.ws.rs.Path", "javax.servlet.annotation.WebServlet"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ThorntailProcessor extends AbstractAnnotationProcessor implements ThorntailWebAnnotationGenerator {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Session session = getSession();
    session.configurators().add(new ThorntailPrometheusAgentConfigurator());

    if (roundEnv.processingOver()) {
      session.close();
      return true;
    }

    session.addPropertyConfiguration(readApplicationConfig("project-defaults.yml"));
    for (TypeElement typeElement : annotations) {
      for (Element mainClass : roundEnv.getElementsAnnotatedWith(typeElement)) {
        add(mainClass);
      }
    }
    return false;
  }
}
